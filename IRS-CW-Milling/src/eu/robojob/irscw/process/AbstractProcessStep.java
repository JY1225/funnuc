package eu.robojob.irscw.process;

import eu.robojob.irscw.external.communication.AbstractCommunicationException;
import eu.robojob.irscw.external.device.AbstractDevice;
import eu.robojob.irscw.external.device.DeviceActionException;
import eu.robojob.irscw.external.robot.RobotActionException;

public abstract class AbstractProcessStep {
	
	private ProcessFlow processFlow;
	private boolean inProcess;
	private AbstractDevice device; 
	
	public AbstractProcessStep(final ProcessFlow processFlow, final AbstractDevice device) {
		this.processFlow = processFlow;
		inProcess = false;
		this.device = device;
	}
	
	public AbstractProcessStep(final AbstractDevice device) {
		this(null, device);
	}
	
	public abstract void executeStep() throws AbstractCommunicationException, RobotActionException, DeviceActionException, InterruptedException;
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
	
	public AbstractDevice getDevice() {
		return device;
	}
	
	public void setDevice(final AbstractDevice device) {
		this.device = device;
	}
	
}
