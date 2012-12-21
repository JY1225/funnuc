package eu.robojob.irscw.ui.teach;

import eu.robojob.irscw.positioning.Coordinates;
import eu.robojob.irscw.ui.controls.AbstractTextField;
import eu.robojob.irscw.ui.controls.TextFieldListener;
import eu.robojob.irscw.ui.controls.keyboard.KeyboardParentPresenter;
import eu.robojob.irscw.ui.controls.keyboard.NumericKeyboardPresenter;

public class OffsetPresenter implements TextFieldListener, KeyboardParentPresenter {

	private OffsetView view;
	private NumericKeyboardPresenter keyboardPresenter;
	private TeachPresenter parent;
	private double offsetLength;
	private double offsetWidth;
	private boolean keyboardActive;
	
	public OffsetPresenter(final NumericKeyboardPresenter keyboardPresenter) {
		this.view = new OffsetView();
		view.setPresenter(this);
		view.setTextFieldListener(this);
		this.keyboardPresenter = keyboardPresenter;
		keyboardPresenter.setParent(this);
		this.offsetLength = 0;
		this.offsetWidth = 0;
		this.keyboardActive = false;
	}

	public void setParent(final TeachPresenter parent) {
		this.parent = parent;
	}
	
	@Override
	public synchronized void closeKeyboard() {
		if (keyboardActive) {
			keyboardActive = false;
			view.closeKeyboardView(keyboardPresenter.getView());
			view.requestFocus();
		}
	}

	@Override
	public void textFieldFocussed(final AbstractTextField<?> textField) {
		keyboardPresenter.setTarget(textField);
		if (!keyboardActive) {
			view.setKeyboardView(keyboardPresenter.getView());
			keyboardActive = true;
		}
	}

	@Override
	public void textFieldLostFocus(final AbstractTextField<?> textField) {
	}
	
	public OffsetView getView() {
		return view;
	}
	
	public void clickedOk() {
		Coordinates extraOffsetFinished = new Coordinates((float) offsetWidth, (float) -offsetLength, 0, 0, 0, 0);
		parent.startFlow(extraOffsetFinished);
	}
	
	public void setOffsetLength(final float offsetLength) {
		this.offsetLength = offsetLength;
	}
	
	public void setOffsetWidth(final float offsetWidth) {
		this.offsetWidth = offsetWidth;
	}
}
