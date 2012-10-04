package eu.robojob.irscw.ui.main.flow;

import eu.robojob.irscw.process.ProcessFlow;

public abstract class AbstractProcessFlowPresenter {

	protected ProcessFlowView view;

	public AbstractProcessFlowPresenter(ProcessFlowView view) {
		this.view = view;
	}
	
	public abstract void deviceClicked(int deviceIndex);
	public abstract void transportClicked(int transportIndex);
	public abstract void backgroundClicked();
	
	public void refresh() {
		view.buildView();
	}
	
	public void loadProcessFlow(ProcessFlow processFlow) {
		view.setProcessFlow(processFlow);
	}
	
	public void setDeviceProgressGreen(int deviceIndex) {
		view.setDeviceProgressGreen(deviceIndex);
	}
	
	public void setDeviceProgressNone(int deviceIndex) {
		view.setDeviceProgressNone(deviceIndex);
	}
	
	public void setTransportProgressGreen(int deviceIndex) {
		view.setTransportProgressGreen(deviceIndex);
	}
	
	public void setTransportProgressNone(int deviceIndex) {
		view.setTransportProgressNone(deviceIndex);
	}
	
	public ProcessFlowView getView() {
		return view;
	}
	
}
