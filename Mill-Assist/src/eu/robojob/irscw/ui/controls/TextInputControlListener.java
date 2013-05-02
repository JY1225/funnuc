package eu.robojob.irscw.ui.controls;

import javafx.scene.control.TextInputControl;
import eu.robojob.irscw.ui.controls.keyboard.KeyboardParentPresenter;

public interface TextInputControlListener extends KeyboardParentPresenter {

	void textFieldFocussed(TextInputControl textInputControl);
	void textFieldLostFocus(TextInputControl textInputControl);
		
}
