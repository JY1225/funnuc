package eu.robojob.irscw.ui.main.flow;

import org.apache.log4j.Logger;

import eu.robojob.irscw.process.ProcessFlow;

public class FixedProcessFlowPresenter extends AbstractProcessFlowPresenter {

	private static Logger logger = Logger.getLogger(FixedProcessFlowPresenter.class);
	
	public FixedProcessFlowPresenter(ProcessFlowView view) {
		super(view);
		view.setPresenter(this);
	}

	@Override
	public void deviceClicked(int deviceIndex) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void transportClicked(int transportIndex) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void backgroundClicked() {
		// TODO Auto-generated method stub
		
	}
	
	public void loadProcessFlow(ProcessFlow processFlow) {
		view.setProcessFlow(processFlow);
		view.showQuestionMarks();
		view.disableClickable();
	}
	
	public void setPickStepActive(int transportIndex) {
		view.setAllProgressNone();
		for (int i = 0; i < transportIndex; i++) {
			view.setTransportProgressGreen(i);
			view.setDeviceProgressGreen(i);
		}
		view.setDeviceProgressGreen(transportIndex);
		view.setTransportProgressFirstYellow(transportIndex);
	}
	
	public void setPickStepFinished(int transportIndex) {
		view.setAllProgressNone();
		for (int i = 0; i < transportIndex; i++) {
			view.setTransportProgressGreen(i);
			view.setDeviceProgressGreen(i);
		}
		view.setDeviceProgressGreen(transportIndex);
		view.setTransportProgressFirstGreen(transportIndex);
	}
	
	public void setPutStepActive(int transportIndex) {
		view.setAllProgressNone();
		for (int i = 0; i < transportIndex; i++) {
			view.setTransportProgressGreen(i);
			view.setDeviceProgressGreen(i);
		}
		view.setDeviceProgressGreen(transportIndex);
		view.setTransportProgressSecondYellow(transportIndex);
	}
	
	public void setPutStepFinished(int transportIndex) {
		view.setAllProgressNone();
		for (int i = 0; i < transportIndex + 1; i++) {
			view.setTransportProgressGreen(i);
			view.setDeviceProgressGreen(i);
		}
	}
	
	public void setProcessingStepActive(int deviceIndex) {
		view.setAllProgressNone();
		for (int i = 0; i < deviceIndex; i++) {
			view.setDeviceProgressGreen(i);
			view.setTransportProgressGreen(i);
		}
		view.setDeviceProgressYellow(deviceIndex);
	}
	
	public void setProcessingStepFinished(int deviceIndex) {
		view.setAllProgressNone();
		for (int i = 0; i < deviceIndex; i++) {
			view.setDeviceProgressGreen(i);
			view.setTransportProgressGreen(i);
		}
		view.setDeviceProgressGreen(deviceIndex);
	}

}
