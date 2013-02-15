package eu.robojob.irscw.ui.admin;

import javafx.scene.control.TextInputControl;
import eu.robojob.irscw.ui.MainContentPresenter;
import eu.robojob.irscw.ui.MainPresenter;
import eu.robojob.irscw.ui.controls.TextInputControlListener;
import eu.robojob.irscw.ui.controls.keyboard.FullKeyboardPresenter;
import eu.robojob.irscw.ui.controls.keyboard.NumericKeyboardPresenter;

public class AdminPresenter implements TextInputControlListener, MainContentPresenter {

	private MainPresenter parent;
	private AdminView view;
	private MainMenuPresenter mainMenuPresenter;
	private FullKeyboardPresenter fullKeyboardPresenter;
	private NumericKeyboardPresenter numericKeyboardPresenter;
	
	public AdminPresenter(final AdminView view, final MainMenuPresenter mainMenuPresenter, final FullKeyboardPresenter fullKeyboardPresenter, 
			final NumericKeyboardPresenter numericKeyboardPresenter) {
		this.view = view;
		view.setPresenter(this);
		view.setMainMenu(mainMenuPresenter.getView());
		this.mainMenuPresenter = mainMenuPresenter;
		this.fullKeyboardPresenter = fullKeyboardPresenter;
		this.numericKeyboardPresenter = numericKeyboardPresenter;
	}
	
	@Override
	public void closeKeyboard() {
	}

	@Override
	public void setActive(final boolean active) {
		mainMenuPresenter.openFirst();
	}

	@Override
	public void setParent(final MainPresenter mainPresenter) {
		this.parent = mainPresenter;
	}
	
	public MainPresenter getParent() {
		return parent;
	}

	@Override
	public AdminView getView() {
		return view;
	}

	@Override
	public void textFieldFocussed(final TextInputControl textInputControl) {
	}

	@Override
	public void textFieldLostFocus(final TextInputControl textInputControl) {
	}

}
