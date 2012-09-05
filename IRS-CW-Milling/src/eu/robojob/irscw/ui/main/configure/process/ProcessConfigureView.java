package eu.robojob.irscw.ui.main.configure.process;

import eu.robojob.irscw.ui.controls.FullTextField;
import eu.robojob.irscw.ui.controls.NumericTextField;
import eu.robojob.irscw.ui.controls.TextFieldListener;
import eu.robojob.irscw.ui.main.configure.AbstractFormView;

public class ProcessConfigureView extends AbstractFormView<ProcessConfigurePresenter> {

	private ProcessConfigurePresenter presenter;
	
	private FullTextField name;
	private NumericTextField test;
	
	public ProcessConfigureView() {
		super();	
	}
	
	public ProcessConfigurePresenter getPresenter() {
		return presenter;
	}

	@Override
	protected void build() {
		name = new FullTextField();
		add(name, 0, 0);
		test = new NumericTextField();
		add(test, 0, 1);
	}
	
	@Override
	public void setTextFieldListener(TextFieldListener listener) {
		name.setFocusListener(listener);
		test.setFocusListener(listener);
	}

}
