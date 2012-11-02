package eu.robojob.irscw.ui.main.flow;

import javafx.application.Platform;
import eu.robojob.irscw.process.AbstractProcessStep;
import eu.robojob.irscw.process.InterventionStep;
import eu.robojob.irscw.process.PickStep;
import eu.robojob.irscw.process.ProcessFlow;
import eu.robojob.irscw.process.ProcessFlow.Mode;
import eu.robojob.irscw.process.ProcessingStep;
import eu.robojob.irscw.process.PutStep;
import eu.robojob.irscw.process.event.ActiveStepChangedEvent;
import eu.robojob.irscw.process.event.ExceptionOccuredEvent;
import eu.robojob.irscw.process.event.FinishedAmountChangedEvent;
import eu.robojob.irscw.process.event.ModeChangedEvent;
import eu.robojob.irscw.process.event.ProcessFlowEvent;
import eu.robojob.irscw.process.event.ProcessFlowListener;
import eu.robojob.irscw.ui.main.model.ProcessFlowAdapter;

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
		this.processFlowAdapter = new ProcessFlowAdapter(processFlow);
		processFlow.addListener(this);
		view.setProcessFlow(processFlow);
		if (showQuestionMarks) {
			view.showQuestionMarks(true);
		}
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
		view.startDeviceAnimation(deviceIndex);
		for (int i = 0; i < deviceIndex; i++) {
			view.setDeviceProgressGreen(i);
			view.setTransportProgressGreen(i);
		}
		view.setDeviceProgressYellow(deviceIndex);
	}
	
	public void setProcessingStepFinished(int deviceIndex) {
		view.setAllProgressNone();
		view.stopDeviceAnimation(deviceIndex);
		for (int i = 0; i < deviceIndex; i++) {
			view.setDeviceProgressGreen(i);
			view.setTransportProgressGreen(i);
		}
		view.setDeviceProgressGreen(deviceIndex);
	}

	@Override
	public void modeChanged(final ModeChangedEvent e) {
		Platform.runLater(new Runnable() {
			@Override public void run() {
				if (e.getMode() == Mode.TEACH) {
					view.showQuestionMarks(true);
				} else {
					view.showQuestionMarks(false);
				}
			}
		}); 
	}
	
	private void showActiveStepChange(ActiveStepChangedEvent e) {
		AbstractProcessStep step = e.getActiveStep();
		if (step instanceof PickStep) {
			if (e.getId() != ActiveStepChangedEvent.PICK_FINISHED) {
				setPickStepActive(processFlowAdapter.getTransportIndex((PickStep) step));
			} else {
				setPickStepFinished(processFlowAdapter.getTransportIndex((PickStep) step));
			}
		} else if (step instanceof PutStep) {
			if (e.getId() != ActiveStepChangedEvent.PUT_FINISHED) {
				setPutStepActive(processFlowAdapter.getTransportIndex((PutStep) step));
			} else {
				setPutStepFinished(processFlowAdapter.getTransportIndex((PutStep) step));
			}
		} else if (step instanceof ProcessingStep) {
			if (e.getId() != ActiveStepChangedEvent.PROCESSING_FINISHED) {
				setProcessingStepActive(processFlowAdapter.getDeviceIndex((ProcessingStep) step));
			} else {
				setProcessingStepFinished(processFlowAdapter.getDeviceIndex((ProcessingStep) step));
			}
		} else if (step instanceof InterventionStep) {
			// TODO
		}
	}

	@Override
	public void activeStepChanged(final ActiveStepChangedEvent e) {
		Platform.runLater(new Runnable() {
			@Override public void run() {
				FixedProcessFlowPresenter.this.showActiveStepChange(e);
			}
		}); 
		
	}

	@Override
	public void exceptionOccured(ExceptionOccuredEvent e) {
		// TODO Auto-generated method stub
		
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
