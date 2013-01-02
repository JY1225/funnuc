package eu.robojob.irscw.ui.general.flow;

import eu.robojob.irscw.process.ProcessFlow;

public abstract class AbstractProcessFlowPresenter {

	private ProcessFlowView view;
	
	public AbstractProcessFlowPresenter(final ProcessFlowView view) {
		this.view = view;
	}
	
	public abstract void deviceClicked(int deviceIndex);
	public abstract void transportClicked(int transportIndex);
	public abstract void backgroundClicked();
	
	public void refresh() {
		view.refresh();
	}
	
	public void loadProcessFlow(final ProcessFlow processFlow) {
		view.loadProcessFlow(processFlow);
	}
	
	public ProcessFlowView getView() {
		return view;
	}
	
	public void setView(final ProcessFlowView view) {
		this.view = view;
	}
	
}
