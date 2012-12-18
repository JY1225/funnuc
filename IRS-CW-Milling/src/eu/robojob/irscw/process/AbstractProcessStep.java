package eu.robojob.irscw.process;

import eu.robojob.irscw.external.communication.AbstractCommunicationException;
import eu.robojob.irscw.external.device.DeviceActionException;
import eu.robojob.irscw.external.robot.RobotActionException;

public abstract class AbstractProcessStep {
	
	private ProcessFlow processFlow;
	private boolean inProcess;
	
	public AbstractProcessStep(final ProcessFlow processFlow) {
		this.processFlow = processFlow;
		inProcess = false;
	}
	
	public AbstractProcessStep() {
		this(null);
	}
	
	public abstract void executeStep(int workpieceId) throws AbstractCommunicationException, RobotActionException, DeviceActionException, InterruptedException;
	public abstract String toString();
	public abstract ProcessStepType getType();

	public boolean isInProcess() {
		return inProcess;
	}

	public void setInProcess(final boolean inProcess) {
		this.inProcess = inProcess;
	}
	
	public ProcessFlow getProcessFlow() {
		return processFlow;
	}

	public void setProcessFlow(final ProcessFlow processFlow) {
		this.processFlow = processFlow;
	}
	
}
