package eu.robojob.irscw.ui.process.flow;

import eu.robojob.irscw.ui.process.configure.ConfigurePresenter;

public class ProcessFlowPresenter {

	private ProcessFlowView view;
	private ConfigurePresenter parent;
	
	public ProcessFlowPresenter(ProcessFlowView view) {
		this.view = view;
		view.setPresenter(this);
	}
	
	public void setParent(ConfigurePresenter parent) {
		this.parent = parent;
	}
	
	public ProcessFlowView getView() {
		return view;
	}
	
	public void deviceClicked(String id) {
		
	}
	
	public void transportClicked(String id) {
		
	}
}
