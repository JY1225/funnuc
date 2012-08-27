package eu.robojob.irscw.ui;

import javafx.scene.control.FocusModel;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.KeyCode;

import org.apache.log4j.Logger;

import eu.robojob.irscw.ui.controls.TextFieldListener;

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
				if (s.length() >= 1) {
					target.setText(s.substring(0, s.length() - 1));
					target.selectAll();
					target.forward();
				}
				break;
			default:
				target.setText(target.getText() + keyCode.toString());
				target.selectAll();
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
