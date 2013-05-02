package eu.robojob.millassist.ui.controls;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import eu.robojob.millassist.util.UIConstants;

public abstract class AbstractTextField<T> extends javafx.scene.control.TextField {

	private TextInputControlListener listener;
	private ChangeListener<T> changeListener;
	private String originalText;
	
	private int maxLength;
	private boolean notifyEveryChange;
	
	public AbstractTextField(final int maxLength) {
		setPrefHeight(UIConstants.TEXT_FIELD_HEIGHT);
		this.focusedProperty().addListener(new TextFieldFocusListener(this));
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
				if ((AbstractTextField.this.getCaretPosition() < AbstractTextField.this.getText().length())
						&& (AbstractTextField.this.getSelection().getLength() == 0)) {
					AbstractTextField.this.selectAll();
				}
			}
		});
		this.maxLength = maxLength;
		this.notifyEveryChange = false;
	}
	
	public void setNotifyEveryChange(final boolean notify) {
		this.notifyEveryChange = notify;
	}
	
	public void setFocusListener(final TextInputControlListener listener) {
		this.listener = listener;
	}
	
	public void setOnChange(final ChangeListener<T> changeListener) {
		this.changeListener = changeListener;
	}
	
	// These methods are overridden to make sure the entered text is valid
	@Override
	public void replaceText(final int start, final int end, final String text) {
		String currentText = getText();
		String newString = currentText.substring(0, start) + text + currentText.substring(end);
		
		if (newString.matches(getMatchingExpression()) && calculateLength(newString) <= maxLength) {
			super.replaceText(start, end, text);
		}
		
		if (notifyEveryChange) {
			changeListener.changed(null, convertString(currentText), convertString(getText()));
		}
	}
	@Override
	public void replaceSelection(final String text) {
		String currentText = getText();
		String newString = currentText.substring(0, getSelection().getStart()) + text + currentText.substring(getSelection().getEnd());
		
		if (newString.matches(getMatchingExpression()) && calculateLength(newString) <= maxLength) {
			super.replaceSelection(text);
		}
		
		if (notifyEveryChange) {
			changeListener.changed(null, convertString(currentText), convertString(getText()));
		}
	}
	
	public abstract String getMatchingExpression();
	public abstract int calculateLength(String string);
	

	private class TextFieldFocusListener implements ChangeListener<Boolean> {

		private AbstractTextField<?> textField;
		
		public TextFieldFocusListener(final AbstractTextField<?> textField) {
			this.textField = textField;
		}
		
		@Override
		public void changed(final ObservableValue<? extends Boolean> observable, final Boolean oldValue, final Boolean newValue) {
			if (newValue) {
				originalText = textField.getText();
				listener.textFieldFocussed(textField);
			} else {
				cleanText();
				listener.textFieldLostFocus(textField);
				if (changeListener != null) {
					changeListener.changed(null, convertString(originalText), convertString(textField.getText()));
				}
			}
		}
	}
		
	public abstract void cleanText();
	
	public abstract T convertString(String text);
	
}
