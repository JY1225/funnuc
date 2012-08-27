package eu.robojob.irscw.ui.process;

import javafx.scene.layout.GridPane;
import eu.robojob.irscw.ui.controls.FullTextField;

public class ProcessConfigurationView extends GridPane {

	private ProcessConfigurationPresenter presenter;
	
	private FullTextField name;
	private FullTextField test;
	
	public ProcessConfigurationView() {
		buildView();
	}
	
	private void buildView() {
		name = new FullTextField();
		add(name, 0, 0);
		test = new FullTextField();
		add(test, 0, 1);
	}
	
	public void setPresenter(ProcessConfigurationPresenter presenter) {
		this.presenter = presenter;
		name.setFocusListener(presenter);
		test.setFocusListener(presenter);
	}
	
	public ProcessConfigurationPresenter getPresenter() {
		return presenter;
	}

}
