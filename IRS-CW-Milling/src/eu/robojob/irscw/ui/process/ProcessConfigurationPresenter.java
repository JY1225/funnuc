package eu.robojob.irscw.ui.process;

import eu.robojob.irscw.ui.controls.FullTextField;
import eu.robojob.irscw.ui.controls.NumericTextField;
import eu.robojob.irscw.ui.controls.TextFieldListener;

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
		if (textField instanceof FullTextField)
			parent.textFieldFocussed((FullTextField) textField);
		else if (textField instanceof NumericTextField)
			parent.textFieldFocussed((NumericTextField) textField);
		else 
			throw new IllegalArgumentException("Unknown type of textfield focussed");
	}

	@Override
	public void textFieldLostFocus(eu.robojob.irscw.ui.controls.TextField textField) {
		parent.closeKeyboard();
	}

	@Override
	public void closeKeyboard() {
		parent.closeKeyboard();
	}
}
