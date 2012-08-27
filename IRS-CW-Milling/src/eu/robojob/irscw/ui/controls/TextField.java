package eu.robojob.irscw.ui.controls;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

public abstract class TextField extends javafx.scene.control.TextField {

	protected TextFieldFocussedListener focusListener;
	
	public TextField() {
		this.focusedProperty().addListener(new TextFieldFocusListener(this));
	}
	
	public void setFocusListener(TextFieldFocussedListener focusListener) {
		this.focusListener = focusListener;
	}
	
	@Override
	public void replaceText(int start, int end, String text) {
		// If the replaced text would end up being invalid, then simply
		// ignore this call!
		if (text.matches(getMatchingExpression())) {
			super.replaceText(start, end, text);
		}
	}

	@Override
	public void replaceSelection(String text) {
		if (text.matches(getMatchingExpression())) {
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
				focusListener.textFieldFocussed(textField);
			} else {
				focusListener.textFieldLostFocus(textField);
			}
		}
		
	}

}
