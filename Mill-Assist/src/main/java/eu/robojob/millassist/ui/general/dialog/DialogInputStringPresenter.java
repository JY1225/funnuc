package eu.robojob.millassist.ui.general.dialog;

import javafx.scene.control.TextInputControl;
import eu.robojob.millassist.ui.controls.FullTextField;
import eu.robojob.millassist.ui.controls.TextInputControlListener;
import eu.robojob.millassist.ui.controls.keyboard.FullKeyboardPresenter;

public class DialogInputStringPresenter extends AbstractDialogPresenter<DialogInputStringView, String> implements TextInputControlListener {
	
	private FullKeyboardPresenter fullKeyboardPresenter;
	
	public DialogInputStringPresenter(DialogInputStringView view, final FullKeyboardPresenter fullKeyboardPresenter) {
		super(view);
		this.fullKeyboardPresenter = fullKeyboardPresenter;
		fullKeyboardPresenter.setParent(this);
		view.setTextFieldListener(this);
	}
	
	@Override
	public void setPresenter() {
		getView().setPresenter(this);
	}
	
	@Override
	public void closeKeyboard() {
		getView().closeKeyboard();
	}
	
	@Override
	public void textFieldFocussed(TextInputControl textInputControl) {
		fullKeyboardPresenter.setTarget((FullTextField) textInputControl);
		getView().showKeyboardPane(fullKeyboardPresenter.getView());
	}
	
	@Override
	public void textFieldLostFocus(TextInputControl textInputControl) {
		getView().closeKeyboard();
	}
}