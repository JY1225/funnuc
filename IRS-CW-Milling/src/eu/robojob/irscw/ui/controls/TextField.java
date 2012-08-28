package eu.robojob.irscw.ui.controls;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public abstract class TextField extends javafx.scene.control.TextField {

	protected TextFieldListener listener;
	private String originalText;
	
	public TextField() {
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
	}
	
	public void setFocusListener(TextFieldListener listener) {
		this.listener = listener;
	}
	
	@Override
	public void replaceText(int start, int end, String text) {
		String currentText = getText();
		String newString = currentText.substring(0, start) + text + currentText.substring(end);
		
		if (newString.matches(getMatchingExpression())) {
			super.replaceText(start, end, text);
		}
	}

	@Override
	public void replaceSelection(String text) {
		String currentText = getText();
		String newString = currentText.substring(0, getSelection().getStart()) + text + currentText.substring(getSelection().getEnd());
		
		if (newString.matches(getMatchingExpression())) {
			super.replaceSelection(text);
		}
	}
	
	public abstract String getMatchingExpression();
	

	private class TextFieldFocusListener implements ChangeListener<Boolean> {

		private TextField textField;
		
		public TextFieldFocusListener(TextField textField) {
			this.textField = textField;
		}
		
		@Override
		public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
			if (newValue) {
				originalText = textField.getText();
				listener.textFieldFocussed(textField);
			} else {
				listener.textFieldLostFocus(textField);
			}
		}
	}

}
