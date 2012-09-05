package eu.robojob.irscw.ui.main.configure.process;

import eu.robojob.irscw.ui.controls.FullTextField;
import eu.robojob.irscw.ui.controls.NumericTextField;
import eu.robojob.irscw.ui.controls.TextFieldListener;
import eu.robojob.irscw.ui.main.configure.ConfigurePresenter;

public class ProcessConfigurePresenter implements TextFieldListener {

	private ProcessConfigureView view;
	private ConfigurePresenter parent;
	
	public ProcessConfigurePresenter(ProcessConfigureView view) {
		this.view = view;
		view.setPresenter(this);
	}
	
	public void setParent(ConfigurePresenter parent) {
		this.parent = parent;
	}

	public ProcessConfigureView getView() {
		return view;
	}
	
	public ConfigurePresenter getParent() {
		return parent;
	}

	@Override
	public void textFieldFocussed(eu.robojob.irscw.ui.controls.AbstractTextField textField) {
		if (textField instanceof FullTextField)
			parent.textFieldFocussed((FullTextField) textField);
		else if (textField instanceof NumericTextField)
			parent.textFieldFocussed((NumericTextField) textField);
		else 
			throw new IllegalArgumentException("Unknown type of textfield focussed");
	}

	@Override
	public void textFieldLostFocus(eu.robojob.irscw.ui.controls.AbstractTextField textField) {
		parent.closeKeyboard();
	}

	@Override
	public void closeKeyboard() {
		parent.closeKeyboard();
	}
}
