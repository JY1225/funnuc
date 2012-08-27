package eu.robojob.irscw.ui.process;

import eu.robojob.irscw.ui.controls.TextFieldListener;
import javafx.scene.control.TextField;

public class ProcessConfigurationPresenter implements TextFieldListener {

	private ProcessConfigurationView view;
	private ConfigurePresenter parent;
	
	public ProcessConfigurationPresenter(ProcessConfigurationView view) {
		this.view = view;
		view.setPresenter(this);
	}
	
	public void setParent(ConfigurePresenter parent) {
		this.parent = parent;
	}

	public ProcessConfigurationView getView() {
		return view;
	}
	
	public ConfigurePresenter getParent() {
		return parent;
	}

	@Override
	public void textFieldFocussed(eu.robojob.irscw.ui.controls.TextField textField) {
		parent.textFieldFocussed(textField);
	}

	@Override
	public void textFieldLostFocus(eu.robojob.irscw.ui.controls.TextField textField) {
		parent.textFieldLostFocus(textField);
	}

	@Override
	public void closeKeyboard() {
		parent.closeKeyboard();
	}
}
