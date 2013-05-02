package eu.robojob.irscw.process;

import eu.robojob.irscw.external.communication.AbstractCommunicationException;
import eu.robojob.irscw.external.device.DeviceActionException;
import eu.robojob.irscw.external.robot.RobotActionException;
import eu.robojob.irscw.process.execution.ProcessExecutor;

public abstract class AbstractProcessStep {
	
	private int id;
	private ProcessFlow processFlow;
	private boolean inProcess;
	
	public AbstractProcessStep(final ProcessFlow processFlow) {
		this.processFlow = processFlow;
		inProcess = false;
	}
	
	public AbstractProcessStep() {
		this(null);
	}
	
	public abstract void executeStep(int workpieceId, ProcessExecutor executor) throws AbstractCommunicationException, RobotActionException, DeviceActionException, InterruptedException;
	public abstract String toString();
	public abstract ProcessStepType getType();
	
	public void checkProcessExecutorStatus(final ProcessExecutor executor) throws InterruptedException {
		if (executor.isRunning()) {
			return;
		} else {
			throw new InterruptedException("Executor stopped running.");
		}
	}
	
	public int getId() {
		return id;
	}

	public void setId(final int id) {
		this.id = id;
	}

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
