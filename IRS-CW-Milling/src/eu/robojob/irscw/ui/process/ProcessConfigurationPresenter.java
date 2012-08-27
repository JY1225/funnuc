package eu.robojob.irscw.ui.process;

import javafx.scene.control.TextField;

public class ProcessConfigurationPresenter {

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
	
	public void textFieldFocussed(TextField textField) {
		parent.activateKeyoard(textField);
	}
	
	public void textFieldLostFocus() {
		parent.closeKeyboard();
	}
}
