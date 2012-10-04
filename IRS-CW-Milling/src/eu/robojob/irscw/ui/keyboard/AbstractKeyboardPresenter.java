package eu.robojob.irscw.ui.keyboard;

import javafx.scene.input.KeyCode;

import org.apache.log4j.Logger;

import eu.robojob.irscw.ui.controls.AbstractTextField;

public abstract class AbstractKeyboardPresenter {

	private AbstractKeyboardView view;
	private KeyboardParentPresenter parent;
	private AbstractTextField<?> target;
	
	private String originalText;
	
	private static Logger logger = Logger.getLogger(AbstractKeyboardPresenter.class);
	
	public AbstractKeyboardPresenter(AbstractKeyboardView view) {
		this.view = view;
		view.setPresenter(this);
	}
	
	public void setParent(KeyboardParentPresenter parent) {
		this.parent = parent;
	}
	
	public void setTarget(AbstractTextField<?> target) {
		this.target = target;
		originalText = target.getText();
		target.requestFocus();
	}
	
	public AbstractTextField<?> getTarget() {
		return target;
	}
	
	public AbstractKeyboardView getView() {
		return view;
	}
	
	public void keyPressed(KeyCode keyCode) {
		logger.debug("Pressed key: " + keyCode);
		if (target == null) {
			throw new IllegalStateException("Target was not set.");
		}
		switch(keyCode) {
			case ESCAPE: 
				clickedEscape();
				break;
			case ENTER:
				parent.closeKeyboard();
				break;
			case DELETE:
				target.setText("");
				break;
			case BACK_SPACE:
				clickedBackSpace();
				break;
			default:
				clickedOtherKey(keyCode);
				break;
		}
	}
	
	private void clickedEscape() {
		if (originalText.equals(null)) {
			throw new IllegalStateException("No original text value was set.");
		}
		target.setText(originalText);
		parent.closeKeyboard();
	}
	
	private void clickedBackSpace() {
		if (target.getSelection().getLength() == 0) {
			target.selectBackward();
		}
		target.replaceSelection("");
	}
	
	private void clickedOtherKey(KeyCode keyCode) {
		target.replaceSelection("" + getChar(keyCode));
	}
	
	public abstract char getChar(KeyCode keyCode);
}
