package eu.robojob.millassist.process.event;

import eu.robojob.millassist.process.AbstractProcessStep;
import eu.robojob.millassist.process.ProcessFlow;

public class StatusChangedEvent extends ProcessFlowEvent {

	private AbstractProcessStep activeStep;
	
	public static final int INACTIVE = 0;
	
	public static final int STARTED = 1;
	public static final int PREPARE_DEVICE = 2;
	public static final int EXECUTE_TEACHED = 3;
	public static final int EXECUTE_NORMAL = 4;
	public static final int INTERVENTION_READY = 5;
	public static final int PROCESSING_STARTED = 6;
	public static final int ENDED = 10;
	
	public static final int TEACHING_NEEDED = 21;
	public static final int TEACHING_FINISHED = 22;
	
	public static final int PREPARE = 30;
	public static final int FINISHED = 31;
	
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
