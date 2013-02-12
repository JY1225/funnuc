package eu.robojob.irscw.ui.controls.keyboard;

import javafx.scene.input.KeyCode;
import eu.robojob.irscw.ui.controls.FullTextField;
import eu.robojob.irscw.ui.controls.TextArea;

public class FullKeyboardPresenter extends AbstractKeyboardPresenter {
	
	public FullKeyboardPresenter(final FullKeyboardView view) {
		super(view);
	}
	
	public char getChar(final KeyCode keyCode) {
		char returnChar = keyCode.toString().charAt(keyCode.toString().length() - 1);	// get last character of key-code string (e.g. KEYCODE_A)
		switch (keyCode) {
			case UNDERSCORE:
				return '_';
			case DECIMAL:
				return '.';
			case MINUS:
				return '-';
			case SPACE:
				return ' ';
			case COLORED_KEY_0:
				return '�';
			case COLORED_KEY_1:
				return '�';
			case COLORED_KEY_2:
				return '�';
			default:
				return returnChar;
		}
	}
	
	public FullKeyboardView getView() {
		return (FullKeyboardView) super.getView();
	}
	
	public void setTarget(final FullTextField target) {
		super.setTarget(target);
	}
	
	public void setTarget(final TextArea target) {
		super.setTarget(target);
	}
}
