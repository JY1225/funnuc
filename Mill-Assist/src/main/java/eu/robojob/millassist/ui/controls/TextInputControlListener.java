package eu.robojob.millassist.ui.controls;

import javafx.scene.control.TextInputControl;
import eu.robojob.millassist.ui.controls.keyboard.KeyboardParentPresenter;

public interface TextInputControlListener extends KeyboardParentPresenter {

	void textFieldFocussed(TextInputControl textInputControl);
	void textFieldLostFocus(TextInputControl textInputControl);
		
}
