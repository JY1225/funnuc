package eu.robojob.irscw.ui.controls.keyboard;

import javafx.scene.input.KeyCode;
import eu.robojob.irscw.ui.controls.FullTextField;

public class KeyboardPresenter extends AbstractKeyboardPresenter {
	
	public KeyboardPresenter(KeyboardView view) {
		super(view);
	}
	
	public char getChar(KeyCode keyCode) {
		char returnChar = keyCode.toString().charAt(keyCode.toString().length()-1);
		if (keyCode.equals(KeyCode.UNDERSCORE)) {
			returnChar = '_';
		} else if (keyCode.equals(KeyCode.DECIMAL)) {
			returnChar = '.';
		} else if (keyCode.equals(KeyCode.MINUS)) {
			returnChar = '-';
		} else if (keyCode.equals(KeyCode.SPACE)) {
			returnChar = ' ';
		} else if (keyCode.equals(KeyCode.COLORED_KEY_0)) {
			return 'Ü';
		} else if (keyCode.equals(KeyCode.COLORED_KEY_1)) {
			return 'Ö';
		} else if (keyCode.equals(KeyCode.COLORED_KEY_2)) {
			return 'Ä';
		}
		return returnChar;
	}
	
	public KeyboardView getView() {
		return (KeyboardView) super.getView();
	}
	
	public void setTarget(FullTextField target) {
		super.setTarget(target);
	}
}
