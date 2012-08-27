package eu.robojob.irscw.ui.controls;

import eu.robojob.irscw.ui.keyboard.KeyboardParentPresenter;


public interface TextFieldListener extends KeyboardParentPresenter {

	public void textFieldFocussed(TextField textField);
	public void textFieldLostFocus(TextField textField);
		
}
