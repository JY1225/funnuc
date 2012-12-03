package eu.robojob.irscw.ui.teach;

import org.apache.log4j.Logger;

import eu.robojob.irscw.positioning.Coordinates;
import eu.robojob.irscw.ui.controls.AbstractTextField;
import eu.robojob.irscw.ui.controls.TextFieldListener;
import eu.robojob.irscw.ui.keyboard.KeyboardParentPresenter;
import eu.robojob.irscw.ui.keyboard.NumericKeyboardPresenter;

public class OffsetPresenter implements TextFieldListener, KeyboardParentPresenter {

	private OffsetView view;
	private NumericKeyboardPresenter keyboardPresenter;
	private TeachPresenter parent;
	
	private double offsetLength;
	private double offsetWidth;
	
	private boolean keyboardActive;
	
	private static final Logger logger = Logger.getLogger(OffsetPresenter.class);

	public OffsetPresenter(NumericKeyboardPresenter keyboardPresenter) {
		this.view = new OffsetView();
		view.setPresenter(this);
		view.setTextFieldListener(this);
		this.keyboardPresenter = keyboardPresenter;
		keyboardPresenter.setParent(this);
		this.offsetLength = 0;
		this.offsetWidth = 0;
		this.keyboardActive = false;
	}

	public void setParent(TeachPresenter parent) {
		this.parent = parent;
	}
	
	@Override
	public synchronized void closeKeyboard() {
		logger.info("closing keyboard");
		if (keyboardActive) {
			keyboardActive = false;
			view.closeKeyboardView(keyboardPresenter.getView());
			view.requestFocus();
		}
	}

	@Override
	public void textFieldFocussed(AbstractTextField<?> textField) {
		logger.info("focussed");
		keyboardPresenter.setTarget(textField);
		if (!keyboardActive) {
			view.setKeyboardView(keyboardPresenter.getView());
			keyboardActive = true;
		}
	}

	@Override
	public void textFieldLostFocus(AbstractTextField<?> textField) {
		logger.info("lost focus");
	}
	
	public OffsetView getView() {
		return view;
	}
	
	public void clickedOk() {
		Coordinates extraOffsetFinished = new Coordinates((float) offsetWidth, (float) -offsetLength, 0, 0, 0, 0);
		logger.info("passing: " + extraOffsetFinished);
		parent.startFlow(extraOffsetFinished);
	}
	
	public void setOffsetLength(float offsetLength) {
		logger.info("set offset length: " + offsetLength);
		this.offsetLength = offsetLength;
	}
	
	public void setOffsetWidth(float offsetWidth) {
		logger.info("set offset width: " + offsetWidth);
		this.offsetWidth = offsetWidth;
	}
}
