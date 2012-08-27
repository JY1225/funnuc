package eu.robojob.irscw.ui.process;

public class ProcessConfigurationPresenter {

	private ProcessConfigurationView view;
	
	public ProcessConfigurationPresenter(ProcessConfigurationView view) {
		this.view = view;
		view.setPresenter(this);
	}

	public ProcessConfigurationView getView() {
		return view;
	}
}
