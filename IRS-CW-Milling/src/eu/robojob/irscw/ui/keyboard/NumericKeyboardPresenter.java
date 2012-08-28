package eu.robojob.irscw.ui.keyboard;

import eu.robojob.irscw.ui.controls.NumericTextField;
import javafx.scene.input.KeyCode;

public class NumericKeyboardPresenter extends AbstractKeyboardPresenter {

	public NumericKeyboardPresenter(NumericKeyboardView view) {
		super(view);
	}

	@Override
	public char getChar(KeyCode keyCode) {
		char returnChar = keyCode.toString().charAt(keyCode.toString().length()-1);
		if (keyCode.equals(KeyCode.DECIMAL)) {
			returnChar = '.';
		}
		return returnChar;
	}
	
	public void setTarget(NumericTextField target) {
		super.setTarget(target);
	}
}
