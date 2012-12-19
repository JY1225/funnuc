package eu.robojob.irscw.ui.controls;

import eu.robojob.irscw.ui.controls.keyboard.KeyboardParentPresenter;

public interface TextFieldListener extends KeyboardParentPresenter {

	void textFieldFocussed(AbstractTextField<?> textField);
	void textFieldLostFocus(AbstractTextField<?> textField);
		
}
