package eu.robojob.irscw.ui.general.flow;

import javafx.application.Platform;
import eu.robojob.irscw.process.AbstractProcessStep;
import eu.robojob.irscw.process.PickStep;
import eu.robojob.irscw.process.ProcessFlow;
import eu.robojob.irscw.process.ProcessingStep;
import eu.robojob.irscw.process.PutStep;
import eu.robojob.irscw.process.event.ExceptionOccuredEvent;
import eu.robojob.irscw.process.event.FinishedAmountChangedEvent;
import eu.robojob.irscw.process.event.ModeChangedEvent;
import eu.robojob.irscw.process.event.ProcessFlowEvent;
import eu.robojob.irscw.process.event.ProcessFlowListener;
import eu.robojob.irscw.process.event.StatusChangedEvent;
import eu.robojob.irscw.ui.general.flow.ProcessFlowView.ProgressBarPieceMode;
import eu.robojob.irscw.ui.general.model.ProcessFlowAdapter;

public class FixedProcessFlowPresenter extends AbstractProcessFlowPresenter implements ProcessFlowListener {
	
	private ProcessFlowAdapter processFlowAdapter;
	
	public FixedProcessFlowPresenter(final ProcessFlowView view) {
		super(view);
		view.setPresenter(this);
	}
	
	@Override public void deviceClicked(final int deviceIndex) {
	}
	@Override public void transportClicked(final int transportIndex) {
	}
	@Override public void backgroundClicked() {
	}
	
	public void loadProcessFlow(final ProcessFlow processFlow) {
		processFlowAdapter = new ProcessFlowAdapter(processFlow);
		getView().loadProcessFlow(processFlow);
		getView().disableClickable();
	}
	
	public void startListening() {
		processFlowAdapter.getProcessFlow().addListener(this);
	}
	
	public void stopListening() {
		processFlowAdapter.getProcessFlow().removeListener(this);
	}
	
	public void setNoneActive() {
		getView().setAllProgressBarPiecesModeNone();
		getView().stopAllAnimations();
	}
	
	@Override
	public void refresh() {
		super.refresh();
		getView().disableClickable();
		setNoneActive();
	}
	
	// we assume a process always starts with a PICK - this way for a new WP the progress bar is also initialized correctly
	public void setPickStepActive(final int activeWorkPieceIndex, final int transportIndex) {
		getView().setAllProgressBarPiecesModeNone(activeWorkPieceIndex);
		for (int i = 0; i < processFlowAdapter.getDeviceStepCount(); i++) {
			if (i < transportIndex) {
				getView().setTransportLeftProgressBarPieceMode(i, activeWorkPieceIndex, ProgressBarPieceMode.GREEN);
				getView().setTransportRightProgressBarPieceMode(i, activeWorkPieceIndex, ProgressBarPieceMode.GREEN);
				getView().setDeviceProgressBarPieceMode(i, activeWorkPieceIndex, ProgressBarPieceMode.GREEN);
			} else if (i == transportIndex) {
				getView().setDeviceProgressBarPieceMode(i, activeWorkPieceIndex, ProgressBarPieceMode.GREEN);
				getView().setTransportLeftProgressBarPieceMode(i, activeWorkPieceIndex, ProgressBarPieceMode.YELLOW);
				getView().setTransportRightProgressBarPieceMode(i, activeWorkPieceIndex, ProgressBarPieceMode.NONE);
			} else {
				getView().setDeviceProgressBarPieceMode(i, activeWorkPieceIndex, ProgressBarPieceMode.NONE);
				if (i < processFlowAdapter.getTransportStepCount()) {
					getView().setTransportLeftProgressBarPieceMode(i, activeWorkPieceIndex, ProgressBarPieceMode.NONE);
					getView().setTransportRightProgressBarPieceMode(i, activeWorkPieceIndex, ProgressBarPieceMode.NONE);
				}
			}
		}
	}
	
	public void setPickStepFinished(final int activeWorkPieceIndex, final int transportIndex) {
		getView().setTransportLeftProgressBarPieceMode(transportIndex, activeWorkPieceIndex, ProgressBarPieceMode.GREEN);
	}
	
	public void setPutStepActive(final int activeWorkPieceIndex, final int transportIndex) {
		getView().setTransportRightProgressBarPieceMode(transportIndex, activeWorkPieceIndex, ProgressBarPieceMode.YELLOW);
	}
	
	public void setPutStepFinished(final int activeWorkPieceIndex, final int transportIndex) {
		getView().setTransportRightProgressBarPieceMode(transportIndex, activeWorkPieceIndex, ProgressBarPieceMode.GREEN);
		if (transportIndex == (processFlowAdapter.getTransportStepCount() - 1)) {
			getView().setDeviceProgressBarPieceMode(transportIndex + 1, activeWorkPieceIndex, ProgressBarPieceMode.GREEN);
		}
	}
	
	public void setProcessingStepActive(final int activeWorkPieceIndex, final int deviceIndex) {
		getView().animateDevice(deviceIndex, true);
		getView().setDeviceProgressBarPieceMode(deviceIndex, activeWorkPieceIndex, ProgressBarPieceMode.YELLOW);
	}
	
	public void setProcessingStepFinished(final int activeWorkPieceIndex, final int deviceIndex) {
		getView().animateDevice(deviceIndex, false);
		getView().setDeviceProgressBarPieceMode(deviceIndex, activeWorkPieceIndex, ProgressBarPieceMode.GREEN);
	}

	private void showActiveStepChange(final StatusChangedEvent e) {
		AbstractProcessStep step = e.getActiveStep();
		int activeWorkPieceIndex = e.getWorkPieceId();
		if (step != null) {
			if (step instanceof PickStep) {
				if (e.getStatusId() == StatusChangedEvent.ENDED) {
					setPickStepFinished(activeWorkPieceIndex, processFlowAdapter.getTransportIndex((PickStep) step));
				} else if (e.getStatusId() == StatusChangedEvent.STARTED) {
					setPickStepActive(activeWorkPieceIndex, processFlowAdapter.getTransportIndex((PickStep) step));
				}
			} else if (step instanceof PutStep) {
				if (e.getStatusId() == StatusChangedEvent.ENDED) {
					setPutStepFinished(activeWorkPieceIndex, processFlowAdapter.getTransportIndex((PutStep) step));
				} else if (e.getStatusId() == StatusChangedEvent.STARTED) {
					setPutStepActive(activeWorkPieceIndex, processFlowAdapter.getTransportIndex((PutStep) step));
				}
			} else if (step instanceof ProcessingStep) {
				if (e.getStatusId() == StatusChangedEvent.ENDED) {
					setProcessingStepFinished(activeWorkPieceIndex, processFlowAdapter.getDeviceIndex((ProcessingStep) step));
				} else if (e.getStatusId() == StatusChangedEvent.STARTED) {
					setProcessingStepActive(activeWorkPieceIndex, processFlowAdapter.getDeviceIndex((ProcessingStep) step));
				}
			} else {
				throw new IllegalStateException("Unknown step type [" + step + "].");
			}
		} else {
			setNoneActive();
		}
	}

	@Override
	public void statusChanged(final StatusChangedEvent e) {
		Platform.runLater(new Runnable() {
			@Override public void run() {
				FixedProcessFlowPresenter.this.showActiveStepChange(e);
			}
		}); 
	}

	@Override public void modeChanged(final ModeChangedEvent e) { }
	@Override public void dataChanged(final ProcessFlowEvent e) { }
	@Override public void finishedAmountChanged(final FinishedAmountChangedEvent e) { }
	@Override public void exceptionOccured(final ExceptionOccuredEvent e) { }

}
