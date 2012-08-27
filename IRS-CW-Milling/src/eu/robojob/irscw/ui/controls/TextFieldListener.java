package eu.robojob.irscw.ui.controls;

import eu.robojob.irscw.ui.KeyboardParentPresenter;


public interface TextFieldListener extends KeyboardParentPresenter {

	public void textFieldFocussed(TextField textField);
	public void textFieldLostFocus(TextField textField);
		
}
