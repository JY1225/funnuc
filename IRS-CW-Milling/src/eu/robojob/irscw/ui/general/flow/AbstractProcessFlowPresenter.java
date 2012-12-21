package eu.robojob.irscw.ui.general.flow;

import eu.robojob.irscw.process.ProcessFlow;

public abstract class AbstractProcessFlowPresenter {

	private OldProcessFlowView view;
	
	public AbstractProcessFlowPresenter(final OldProcessFlowView view) {
		this.view = view;
	}
	
	public abstract void deviceClicked(int deviceIndex);
	public abstract void transportClicked(int transportIndex);
	public abstract void backgroundClicked();
	
	public void refresh() {
		view.buildView();
	}
	
	public void loadProcessFlow(final ProcessFlow processFlow) {
		view.setProcessFlow(processFlow);
	}
	
	public void setDeviceProgressGreen(final int deviceIndex) {
		view.setDeviceProgressGreen(deviceIndex);
	}
	
	public void setDeviceProgressNone(final int deviceIndex) {
		view.setDeviceProgressNone(deviceIndex);
	}
	
	public void setTransportProgressGreen(final int deviceIndex) {
		view.setTransportProgressGreen(deviceIndex);
	}
	
	public void setTransportProgressNone(final int deviceIndex) {
		view.setTransportProgressNone(deviceIndex);
	}
	
	public OldProcessFlowView getView() {
		return view;
	}
	
	public void setView(final OldProcessFlowView view) {
		this.view = view;
	}
	
}
