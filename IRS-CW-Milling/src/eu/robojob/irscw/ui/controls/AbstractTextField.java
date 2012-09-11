package eu.robojob.irscw.ui.controls;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.input.InputMethodEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public abstract class AbstractTextField<T> extends javafx.scene.control.TextField {

	protected TextFieldListener listener;
	protected ChangeListener<T> changeListener;
	private String originalText;
	
	private int maxLength;
	
	public AbstractTextField(int maxLength) {
		this.focusedProperty().addListener(new TextFieldFocusListener(this));
		this.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent event) {
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
		
		this.maxLength = maxLength;
	}
	
	public void setFocusListener(TextFieldListener listener) {
		this.listener = listener;
	}
	
	public void setOnChange(ChangeListener<T> changeListener) {
		this.changeListener = changeListener;
	}
	
	@Override
	public void replaceText(int start, int end, String text) {
		String currentText = getText();
		String newString = currentText.substring(0, start) + text + currentText.substring(end);
		
		if (newString.matches(getMatchingExpression()) && calculateLength(newString) <= maxLength) {
			super.replaceText(start, end, text);
		}
	}

	@Override
	public void replaceSelection(String text) {
		String currentText = getText();
		String newString = currentText.substring(0, getSelection().getStart()) + text + currentText.substring(getSelection().getEnd());
		
		if (newString.matches(getMatchingExpression()) && calculateLength(newString) <= maxLength) {
			super.replaceSelection(text);
		}
	}
	
	public abstract String getMatchingExpression();
	public abstract int calculateLength(String string);
	

	private class TextFieldFocusListener implements ChangeListener<Boolean> {

		private AbstractTextField<?> textField;
		
		public TextFieldFocusListener(AbstractTextField<?> textField) {
			this.textField = textField;
		}
		
		@Override
		public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
			if (newValue) {
				originalText = textField.getText();
				listener.textFieldFocussed(textField);
			} else {
				cleanText();
				listener.textFieldLostFocus(textField);
				if (changeListener!= null) {
					changeListener.changed(null, convertString(originalText), convertString(textField.getText()));
				}
			}
		}
	}
	
	public abstract void cleanText();
	
	public abstract T convertString(String text);
	
}
