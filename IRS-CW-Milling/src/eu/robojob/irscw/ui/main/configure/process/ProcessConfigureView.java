package eu.robojob.irscw.ui.main.configure.process;

import javafx.scene.layout.GridPane;
import eu.robojob.irscw.ui.controls.FullTextField;
import eu.robojob.irscw.ui.controls.NumericTextField;

public class ProcessConfigureView extends GridPane {

	private ProcessConfigurePresenter presenter;
	
	private FullTextField name;
	private NumericTextField test;
	
	public ProcessConfigureView() {
		buildView();
	}
	
	private void buildView() {
		name = new FullTextField();
		add(name, 0, 0);
		test = new NumericTextField();
		add(test, 0, 1);
	}
	
	public void setPresenter(ProcessConfigurePresenter presenter) {
		this.presenter = presenter;
		name.setFocusListener(presenter);
		test.setFocusListener(presenter);
	}
	
	public ProcessConfigurePresenter getPresenter() {
		return presenter;
	}

}
