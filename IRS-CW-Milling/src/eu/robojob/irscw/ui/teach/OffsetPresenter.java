package eu.robojob.irscw.ui.teach;

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

	public OffsetPresenter(NumericKeyboardPresenter keyboardPresenter) {
		this.view = new OffsetView();
		view.setTextFieldListener(this);
		this.keyboardPresenter = keyboardPresenter;
		keyboardPresenter.setParent(this);
		this.offsetLength = 0;
		this.offsetWidth = 0;
	}

	public void setParent(TeachPresenter parent) {
		this.parent = parent;
	}
	
	@Override
	public void closeKeyboard() {
		view.closeKeyboardView();
		view.requestFocus();
	}

	@Override
	public void textFieldFocussed(AbstractTextField<?> textField) {
		keyboardPresenter.setTarget(textField);
		view.setKeyboardView(keyboardPresenter.getView());
	}

	@Override
	public void textFieldLostFocus(AbstractTextField<?> textField) {
		closeKeyboard();
	}
	
	public OffsetView getView() {
		return view;
	}
	
	public void clickedOk() {
		
	}
	
	public void setOffsetLength(float offsetLength) {
		this.offsetLength = offsetLength;
	}
	
	public void setOffsetWidth(float offsetWidth) {
		this.offsetWidth = offsetWidth;
	}
}