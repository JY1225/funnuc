package eu.robojob.irscw.ui.controls.keyboard;

import javafx.scene.input.KeyCode;
import eu.robojob.irscw.ui.controls.NumericTextField;

public class NumericKeyboardPresenter extends AbstractKeyboardPresenter {

	public NumericKeyboardPresenter(final NumericKeyboardView view) {
		super(view);
	}

	@Override
	public char getChar(final KeyCode keyCode) {
		char returnChar = keyCode.toString().charAt(keyCode.toString().length() - 1);
		if (keyCode.equals(KeyCode.DECIMAL)) {
			returnChar = '.';
		}
		if (keyCode.equals(KeyCode.MINUS)) {
			returnChar = '-';
		}
		return returnChar;
	}
	
	public void setTarget(final NumericTextField target) {
		super.setTarget(target);
	}
}
