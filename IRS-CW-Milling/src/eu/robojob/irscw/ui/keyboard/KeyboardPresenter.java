package eu.robojob.irscw.ui.keyboard;

import javafx.scene.control.TextInputControl;
import javafx.scene.input.KeyCode;

import org.apache.log4j.Logger;

public class KeyboardPresenter {

	private KeyboardView view;
	private TextInputControl target;
	private KeyboardParentPresenter parentPresenter;
	
	private String originalText;
	
	private static Logger logger = Logger.getLogger(KeyboardPresenter.class);
	
	public KeyboardPresenter(KeyboardView view) {
		this.view = view;
		view.setPresenter(this);
	}

	public void setParentPresenter(KeyboardParentPresenter parentPresenter) {
		this.parentPresenter = parentPresenter;
	}
	
	public void keyPressed(KeyCode keyCode) {
		
		logger.debug("Pressed key: " + keyCode);
		if (target == null) {
			throw new IllegalStateException("Target was not set.");
		}
		int caretPos = target.getCaretPosition();
		String newString = "";
		switch(keyCode) {
			case ESCAPE: 
				if (originalText.equals(null)) {
					throw new IllegalStateException("No original text value was set.");
				}
				target.setText(originalText);
				target.selectAll();
				target.forward();
				parentPresenter.closeKeyboard();
				break;
			case ENTER:
				parentPresenter.closeKeyboard();
				break;
			case DELETE:
				target.setText("");
				target.selectAll();
				target.forward();
				break;
			case BACK_SPACE:
				String s = target.getText();
				if (caretPos > 0) {
					if (s.length() >= 1) {
						newString = s.substring(0, caretPos -1);
						if (caretPos < (s.length())) {
							newString = newString + s.substring(caretPos, s.length());
						}
						target.setText(newString);
					}
				}
				if (caretPos > 1) {
					target.selectPositionCaret(caretPos - 1);
					target.forward();
				} else {
					target.backward();
				}
				break;
			default:
				String string = target.getText();
				if (string.length() >= 1) {
				 newString = string.substring(0, caretPos);
				}
				newString = newString + keyCode.toString();
				if (target.getCaretPosition() < string.length()) {
					newString = newString + string.substring(caretPos);
				}
				target.setText(newString);
				target.selectPositionCaret(caretPos + 1);
				target.forward();
				break;
		}
	}
	
	public KeyboardView getView() {
		return view;
	}
	
	public void setTargetTextInput(TextInputControl target) {
		this.target = target;
		originalText = target.getText();
	}
}
