package eu.robojob.irscw.ui.main.configure.process;

import eu.robojob.irscw.ui.keyboard.KeyboardParentPresenter;
import eu.robojob.irscw.ui.main.configure.ConfigurePresenter;

public class ProcessConfigurationMenuPresenter {

	private ProcessConfigurationMenuView view;
	private ConfigurePresenter parent;
	
	private ProcessConfigurationPresenter configurePresenter;
		
	public ProcessConfigurationMenuPresenter(ProcessConfigurationMenuView view, ProcessConfigurationPresenter configurePresenter) {
		this.view = view;
		view.setPresenter(this);
		this.configurePresenter = configurePresenter;
	}
	
	//TODO review this design decision, assigning parent of menu-presenter to other presenters
	public void setParent(ConfigurePresenter parent) {
		this.parent = parent;
		configurePresenter.setParent(parent);
	}

	public ProcessConfigurationMenuView getView() {
		return view;
	}
	
	public void saveData() {
		
	}
	
	public void configureProcess() {
		parent.setBottomRightView(configurePresenter.getView());
	}
	
	public void openProcess() {
		
	}
	
	public void newProcess() {
		
	}

}
