package eu.robojob.irscw.process.event;

import eu.robojob.irscw.process.AbstractProcessStep;
import eu.robojob.irscw.process.ProcessFlow;

public class StatusChangedEvent extends ProcessFlowEvent {

	private AbstractProcessStep activeStep;
	
	public static final int NONE_ACTIVE = 0;
	
	public static final int STARTED = 1;
	public static final int PREPARE_DEVICE = 2;
	public static final int EXECUTE_TEACHED = 3;
	public static final int EXECUTE_NORMAL = 4;
	public static final int INTERVENTION_READY = 5;
	public static final int PROCESSING_STARTED = 6;
	public static final int ENDED = 10;
	
	public static final int TEACHING_NEEDED = 21;
	public static final int TEACHING_FINISHED = 22;
	
	private int statusId;
	
	private int workPieceId;
	
	public StatusChangedEvent(final ProcessFlow source, final AbstractProcessStep activeStep, final int statusId, final int workPieceId) {
		super(source, ProcessFlowEvent.ACTIVE_STEP_CHANGED);
		this.activeStep = activeStep;
		this.statusId = statusId;
		this.workPieceId = workPieceId;
	}

	public AbstractProcessStep getActiveStep() {
		return activeStep;
	}
	
	public int getStatusId() {
		return statusId;
	}
	
	public int getWorkPieceId() {
		return workPieceId;
	}
	
	public String toString() {
		return "ActiveStepChangedEvent: " + statusId + " - " + activeStep;
	}
}
