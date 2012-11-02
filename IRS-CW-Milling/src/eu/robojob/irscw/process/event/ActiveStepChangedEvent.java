package eu.robojob.irscw.process.event;

import eu.robojob.irscw.process.AbstractProcessStep;
import eu.robojob.irscw.process.ProcessFlow;

public class ActiveStepChangedEvent extends ProcessFlowEvent {

	private AbstractProcessStep activeStep;
	
	public static final int PICK_PREPARE_DEVICE = 1;
	public static final int PICK_MOVING_TO_LOCATION = 2;
	public static final int PICK_ROBOT_GRABBING_PIECE = 3;
	public static final int PICK_DEVICE_RELEASING_PIECE = 4;
	public static final int PICK_MOVING_FROM_LOCATION = 5;
	public static final int PICK_WAITING_FOR_TEACH = 6;
	public static final int PICK_FINISHED = 7;
	
	public static final int PUT_PREPARE_DEVICE = 21;
	public static final int PUT_MOVING_TO_LOCATION = 22;
	public static final int PUT_DEVICE_GRABBING_PIECE = 23;
	public static final int PUT_ROBOT_RELEASING_PIECE = 24;
	public static final int PUT_MOVING_FROM_LOCATION = 25;
	public static final int PUT_WAITING_FOR_TEACH = 26;
	public static final int PUT_FINISHED = 27;
	
	public static final int PROCESSING_PREPARE_DEVICE = 41;
	public static final int PROCESSING_IN_PROGRESS = 42;
	public static final int PROCESSING_FINISHED = 43;
	
	public static final int INTERVENTION_ROBOT_TO_HOME = 61;
	public static final int INTERVENTION_PREPARE_DEVICE = 62;
	public static final int INTERVENTION_WAITING_FOR_FINISH = 63;
	public static final int INTERVENTION_FINISHED = 64;
	
	private int statusId;
	
	public ActiveStepChangedEvent(ProcessFlow source, AbstractProcessStep activeStep, int statusId) {
		super(source, ProcessFlowEvent.ACTIVE_STEP_CHANGED);
		this.activeStep = activeStep;
		this.statusId = statusId;
	}

	public AbstractProcessStep getActiveStep() {
		return activeStep;
	}
	
	public int getStatusId() {
		return statusId;
	}
}
