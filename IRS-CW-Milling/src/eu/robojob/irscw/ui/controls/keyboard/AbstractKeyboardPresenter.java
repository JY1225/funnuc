package eu.robojob.irscw.ui.controls.keyboard;

import javafx.scene.control.TextInputControl;
import javafx.scene.input.KeyCode;

public abstract class AbstractKeyboardPresenter {

	private AbstractKeyboardView view;
	private KeyboardParentPresenter parent;
	private TextInputControl target;
	
	private String originalText;
		
	public AbstractKeyboardPresenter(final AbstractKeyboardView view) {
		this.view = view;
		view.setPresenter(this);
	}
	
	public void setParent(final KeyboardParentPresenter parent) {
		this.parent = parent;
	}
	
	public void setTarget(final TextInputControl target) {
		this.target = target;
		originalText = target.getText();
		target.requestFocus();
	}
	
	public TextInputControl getTarget() {
		return target;
	}
	
	public AbstractKeyboardView getView() {
		return view;
	}
	
	public void keyPressed(final KeyCode keyCode) {
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
	
	private void clickedOtherKey(final KeyCode keyCode) {
		target.replaceSelection("" + getChar(keyCode));
	}
	
	public abstract char getChar(KeyCode keyCode);
}
