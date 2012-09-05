package eu.robojob.irscw.ui.main.configure.process;

import eu.robojob.irscw.ui.main.configure.ConfigurePresenter;

public class ProcessMenuPresenter {

	private ProcessMenuView view;
	private ConfigurePresenter parent;
	
	private ProcessConfigurePresenter configurePresenter;
		
	public ProcessMenuPresenter(ProcessMenuView view, ProcessConfigurePresenter configurePresenter) {
		this.view = view;
		view.setPresenter(this);
		this.configurePresenter = configurePresenter;
	}
	
	//TODO review this design decision, assigning parent of menu-presenter to other presenters
	public void setParent(ConfigurePresenter parent) {
		this.parent = parent;
		configurePresenter.setParent(parent);
	}

	public ProcessMenuView getView() {
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
