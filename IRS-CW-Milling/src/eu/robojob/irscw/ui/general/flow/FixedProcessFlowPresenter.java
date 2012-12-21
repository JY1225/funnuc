package eu.robojob.irscw.ui.general.flow;

import javafx.application.Platform;
import eu.robojob.irscw.process.AbstractProcessStep;
import eu.robojob.irscw.process.InterventionStep;
import eu.robojob.irscw.process.PickStep;
import eu.robojob.irscw.process.ProcessFlow;
import eu.robojob.irscw.process.ProcessingStep;
import eu.robojob.irscw.process.PutStep;
import eu.robojob.irscw.process.event.FinishedAmountChangedEvent;
import eu.robojob.irscw.process.event.ModeChangedEvent;
import eu.robojob.irscw.process.event.ProcessFlowEvent;
import eu.robojob.irscw.process.event.ProcessFlowListener;
import eu.robojob.irscw.process.event.StatusChangedEvent;
import eu.robojob.irscw.ui.general.model.ProcessFlowAdapter;

public class FixedProcessFlowPresenter extends AbstractProcessFlowPresenter implements ProcessFlowListener {

	private boolean showQuestionMarks;
	private ProcessFlowAdapter processFlowAdapter;
			
	public FixedProcessFlowPresenter(ProcessFlowView view, boolean showQuestionMarks) {
		super(view);
		view.setPresenter(this);
		this.showQuestionMarks = showQuestionMarks;
		this.processFlowAdapter = null;
	}

	@Override
	public void deviceClicked(int deviceIndex) {
	}

	@Override
	public void transportClicked(int transportIndex) {
	}

	@Override
	public void backgroundClicked() {
	}
	
	public void loadProcessFlow(ProcessFlow processFlow) {
		this.processFlowAdapter = new ProcessFlowAdapter(processFlow);
		processFlow.addListener(this);
		getView().setProcessFlow(processFlow);
		getView().showQuestionMarks(showQuestionMarks);
		getView().disableClickable();
	}
	
	public void setNoneActive() {
		getView().setAllProgressNone();
	}
	
	@Override
	public void refresh() {
		super.refresh();
		getView().showQuestionMarks(showQuestionMarks);
		getView().disableClickable();
		setNoneActive();
	}
	
	public void setPickStepActive(int transportIndex) {
		getView().setAllProgressNone();
		for (int i = 0; i < transportIndex; i++) {
			getView().setTransportProgressGreen(i);
			getView().setDeviceProgressGreen(i);
		}
		getView().setDeviceProgressGreen(transportIndex);
		getView().setTransportProgressFirstYellow(transportIndex);
	}
	
	public void setPickStepFinished(int transportIndex) {
		getView().setAllProgressNone();
		for (int i = 0; i < transportIndex; i++) {
			getView().setTransportProgressGreen(i);
			getView().setDeviceProgressGreen(i);
		}
		getView().setDeviceProgressGreen(transportIndex);
		getView().setTransportProgressFirstGreen(transportIndex);
	}
	
	public void setPutStepActive(int transportIndex) {
		getView().setAllProgressNone();
		for (int i = 0; i < transportIndex; i++) {
			getView().setTransportProgressGreen(i);
			getView().setDeviceProgressGreen(i);
		}
		getView().setDeviceProgressGreen(transportIndex);
		getView().setTransportProgressSecondYellow(transportIndex);
	}
	
	public void setPutStepFinished(int transportIndex) {
		getView().setAllProgressNone();
		for (int i = 0; i < transportIndex + 1; i++) {
			getView().setTransportProgressGreen(i);
			getView().setDeviceProgressGreen(i);
		}
	}
	
	public void setProcessingStepActive(int deviceIndex) {
		getView().setAllProgressNone();
		getView().startDeviceAnimation(deviceIndex);
		for (int i = 0; i < deviceIndex; i++) {
			getView().setDeviceProgressGreen(i);
			getView().setTransportProgressGreen(i);
		}
		getView().setDeviceProgressYellow(deviceIndex);
	}
	
	public void setProcessingStepFinished(int deviceIndex) {
		getView().setAllProgressNone();
		getView().stopDeviceAnimation(deviceIndex);
		for (int i = 0; i < deviceIndex; i++) {
			getView().setDeviceProgressGreen(i);
			getView().setTransportProgressGreen(i);
		}
		getView().setDeviceProgressGreen(deviceIndex);
	}

	@Override
	public void modeChanged(final ModeChangedEvent e) {
	}
	
	private void showActiveStepChange(StatusChangedEvent e) {
		AbstractProcessStep step = e.getActiveStep();
		//FIXME implement
	}

	@Override
	public void statusChanged(final StatusChangedEvent e) {
		Platform.runLater(new Runnable() {
			@Override public void run() {
				FixedProcessFlowPresenter.this.showActiveStepChange(e);
			}
		}); 
		
	}

	@Override
	public void dataChanged(ProcessFlowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void finishedAmountChanged(FinishedAmountChangedEvent e) {
		// TODO Auto-generated method stub
		
	}

}
