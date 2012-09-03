package eu.robojob.irscw.ui.process.configure;

public class ProcessConfigurationMenuPresenter {

	private ProcessConfigurationMenuView view;
	private ConfigurePresenter parent;
	
	public ProcessConfigurationMenuPresenter(ProcessConfigurationMenuView view) {
		this.view = view;
		view.setPresenter(this);
	}
	
	public void setParent(ConfigurePresenter parent) {
		this.parent = parent;
	}

	public ProcessConfigurationMenuView getView() {
		return view;
	}
	
	public void saveData() {
		
	}
	
	public void configureProcess() {
		
	}
	
	public void openProcess() {
		
	}
	
	public void newProcess() {
		
	}
}
