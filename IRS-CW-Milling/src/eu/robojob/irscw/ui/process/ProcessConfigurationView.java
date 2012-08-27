package eu.robojob.irscw.ui.process;

public class ProcessConfigurationView {

	private ProcessConfigurationPresenter presenter;
	
	public ProcessConfigurationView() {
		
	}
	
	public void setPresenter(ProcessConfigurationPresenter presenter) {
		this.presenter = presenter;
	}
	
	public ProcessConfigurationPresenter getPresenter() {
		return presenter;
	}
}
