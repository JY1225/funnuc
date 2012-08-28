package eu.robojob.irscw.ui.controls;

import eu.robojob.irscw.ui.keyboard.KeyboardParentPresenter;


public interface TextFieldListener extends KeyboardParentPresenter {

	public void textFieldFocussed(AbstractTextField textField);
	public void textFieldLostFocus(AbstractTextField textField);
		
}
