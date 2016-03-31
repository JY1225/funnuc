package eu.robojob.millassist.ui.controls.keyboard;

import javafx.scene.input.KeyCode;
import eu.robojob.millassist.ui.controls.FullTextField;

public class FullKeyboardPresenter extends AbstractKeyboardPresenter {

    public FullKeyboardPresenter(final FullKeyboardView view) {
        super(view);
    }

    @Override
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
        case AT:
            return '@';
        default:
            return returnChar;
        }
    }

    @Override
    public FullKeyboardView getView() {
        return (FullKeyboardView) super.getView();
    }

    public void setTarget(final FullTextField target) {
        super.setTarget(target);
    }
}
