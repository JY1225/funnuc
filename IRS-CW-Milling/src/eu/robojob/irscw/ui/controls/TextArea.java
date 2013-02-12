package eu.robojob.irscw.ui.controls;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

public class TextArea extends javafx.scene.control.TextArea {

	private int maxLength;
	private String originalText;
	private TextInputControlListener listener;
	private ChangeListener<String> changeListener;
	
	public TextArea(final int maxLength) {
		this.focusedProperty().addListener(new TextAreaFocusListener(this));
		this.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
			@Override
			public void handle(final KeyEvent event) {
				if (event.getCode().equals(KeyCode.ENTER)) {
					listener.closeKeyboard();
				} else {
					if (event.getCode().equals(KeyCode.ESCAPE)) {
						if (originalText.equals(null)) {
							throw new IllegalStateException("No original text value was set.");
						} 
						setText(originalText);
						listener.closeKeyboard();
					}
				}
			}
		});
		this.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(final MouseEvent event) {
				if ((TextArea.this.getCaretPosition() < TextArea.this.getText().length())
						&& (TextArea.this.getSelection().getLength() == 0)) {
					TextArea.this.selectAll();
				}
			}
		});
		this.maxLength = maxLength;
	}
	
	public void setFocusListener(final TextInputControlListener listener) {
		this.listener = listener;
	}
	
	public void setOnChange(final ChangeListener<String> changeListener) {
		this.changeListener = changeListener;
	}
	
	private class TextAreaFocusListener implements ChangeListener<Boolean> {

		private TextArea textArea;
		
		public TextAreaFocusListener(final TextArea textArea) {
			this.textArea = textArea;
		}
		
		@Override
		public void changed(final ObservableValue<? extends Boolean> observable, final Boolean oldValue, final Boolean newValue) {
			if (newValue) {
				originalText = textArea.getText();
				listener.textFieldFocussed(textArea);
			} else {
				cleanText();
				listener.textFieldLostFocus(textArea);
				if (changeListener != null) {
					changeListener.changed(null, originalText, textArea.getText());
				}
			}
		}
	}
	
	@Override
	public void replaceText(final int start, final int end, final String text) {
		String currentText = getText();
		String newString = currentText.substring(0, start) + text + currentText.substring(end);
		
		if (newString.matches(getMatchingExpression()) && calculateLength(newString) <= maxLength) {
			super.replaceText(start, end, text);
		}
	}
	
	@Override
	public void replaceSelection(final String text) {
		String currentText = getText();
		String newString = currentText.substring(0, getSelection().getStart()) + text + currentText.substring(getSelection().getEnd());
		
		if (newString.matches(getMatchingExpression()) && calculateLength(newString) <= maxLength) {
			super.replaceSelection(text);
		}
	}
	
	public String getMatchingExpression() {
		return "[A-Z0-9ÖÜÄ_ \\.\\r-]*$";
	}
	
	public int calculateLength(final String string) {
		return string.length();
	}
	
	public void cleanText() {
		// not necessary
	}
}
