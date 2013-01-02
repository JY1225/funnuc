package eu.robojob.irscw.ui.general.flow;

import javafx.application.Platform;
import eu.robojob.irscw.process.AbstractProcessStep;
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
	
	public FixedProcessFlowPresenter(final ProcessFlowView view) {
		super(view);
		view.setPresenter(this);
		this.showQuestionMarks = false;
	}
	
	public void setShowQuestionMarks(final boolean showQuestionMarks) {
		this.showQuestionMarks = showQuestionMarks;
	}

	@Override public void deviceClicked(final int deviceIndex) {
	}
	@Override public void transportClicked(final int transportIndex) {
	}
	@Override public void backgroundClicked() {
	}
	
	public void loadProcessFlow(final ProcessFlow processFlow) {
		processFlowAdapter = new ProcessFlowAdapter(processFlow);
		processFlow.addListener(this);
		getView().loadProcessFlow(processFlow);
		getView().showQuestionMarks(showQuestionMarks);
		getView().disableClickable();
	}
	
	public void setNoneActive() {
		getView().setAllProgressBarPiecesModeNone();
	}
	
	@Override
	public void refresh() {
		super.refresh();
		getView().showQuestionMarks(showQuestionMarks);
		getView().disableClickable();
		setNoneActive();
	}
	
	public void setPickStepActive(final int activeWorkPieceIndex, final int transportIndex) {
		getView().setAllProgressBarPiecesModeNone(activeWorkPieceIndex);
		
	}
	
	public void setPickStepFinished(final int activeWorkPieceIndex, final int transportIndex) {
		getView().setAllProgressBarPiecesModeNone(activeWorkPieceIndex);
		
	}
	
	public void setPutStepActive(final int activeWorkPieceIndex, final int transportIndex) {
		getView().setAllProgressBarPiecesModeNone(activeWorkPieceIndex);
		
	}
	
	public void setPutStepFinished(final int activeWorkPieceIndex, final int transportIndex) {
		getView().setAllProgressBarPiecesModeNone(activeWorkPieceIndex);
		
	}
	
	public void setProcessingStepActive(final int activeWorkPieceIndex, final int deviceIndex) {
		getView().setAllProgressBarPiecesModeNone(activeWorkPieceIndex);
		//TODO animation
		
	}
	
	public void setProcessingStepFinished(final int activeWorkPieceIndex, final int deviceIndex) {
		getView().setAllProgressBarPiecesModeNone(activeWorkPieceIndex);
		//TODO stop animation
		
	}

	private void showActiveStepChange(final StatusChangedEvent e) {
		AbstractProcessStep step = e.getActiveStep();
		int activeWorkPieceIndex = e.getWorkPieceId();
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
				setProcessingStepActive(activeWorkPieceIndex, processFlowAdapter.getDeviceIndex((ProcessingStep) step));
			} else if (e.getStatusId() == StatusChangedEvent.STARTED) {
				setProcessingStepActive(activeWorkPieceIndex, processFlowAdapter.getDeviceIndex((ProcessingStep) step));
			}
		} else {
			throw new IllegalStateException("Unknown step type [" + step + "].");
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

	@Override public void modeChanged(final ModeChangedEvent e) {
	}
	@Override public void dataChanged(final ProcessFlowEvent e) {
	}
	@Override public void finishedAmountChanged(final FinishedAmountChangedEvent e) {
	}

}
