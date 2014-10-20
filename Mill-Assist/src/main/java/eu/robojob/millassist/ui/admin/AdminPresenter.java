package eu.robojob.millassist.ui.admin;

import javafx.scene.Node;
import javafx.scene.control.TextInputControl;
import eu.robojob.millassist.ui.MainPresenter;
import eu.robojob.millassist.ui.controls.FullTextField;
import eu.robojob.millassist.ui.controls.IntegerTextField;
import eu.robojob.millassist.ui.controls.NumericTextField;
import eu.robojob.millassist.ui.controls.TextInputControlListener;
import eu.robojob.millassist.ui.controls.keyboard.FullKeyboardPresenter;
import eu.robojob.millassist.ui.controls.keyboard.NumericKeyboardPresenter;
import eu.robojob.millassist.ui.general.MainContentPresenter;

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
		mainMenuPresenter.setParent(this);
		mainMenuPresenter.setTextFieldListener(this);
		this.fullKeyboardPresenter = fullKeyboardPresenter;
		fullKeyboardPresenter.setParent(this);
		this.numericKeyboardPresenter = numericKeyboardPresenter;
		numericKeyboardPresenter.setParent(this);
	}
	
	@Override
	public void closeKeyboard() {
		getView().closeKeyboard();
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
		if (textInputControl instanceof FullTextField) {
			this.textFieldFocussed((FullTextField) textInputControl);
		} else if (textInputControl instanceof NumericTextField) {
			this.textFieldFocussed((NumericTextField) textInputControl);
		} else if (textInputControl instanceof IntegerTextField) {
			this.textFieldFocussed((IntegerTextField) textInputControl);
		} else {
			throw new IllegalArgumentException("Unknown keyboard-type [" + textInputControl + "].");
		}
	}
	
	private void textFieldFocussed(final FullTextField textField) {
		fullKeyboardPresenter.setTarget(textField);
		view.showKeyboardPane(fullKeyboardPresenter.getView(), (textField.localToScene(textField.getLayoutBounds().getMinY(), textField.getLayoutBounds().getMaxY()).getY() > 337));
	}
	
	private void textFieldFocussed(final NumericTextField textField) {
		numericKeyboardPresenter.setTarget(textField);
		view.showKeyboardPane(numericKeyboardPresenter.getView(), false);
	}
	
	private void textFieldFocussed(final IntegerTextField textField) {
		numericKeyboardPresenter.setTarget(textField);
		view.showKeyboardPane(numericKeyboardPresenter.getView(), false);
	}

	@Override
	public void textFieldLostFocus(final TextInputControl textInputControl) {
		view.closeKeyboard();
	}
	
	public void setContentNode(final Node node) {
		getView().setContent(node);
	}

}
