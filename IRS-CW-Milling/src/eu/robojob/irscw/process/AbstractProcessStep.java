package eu.robojob.irscw.process;

import java.io.IOException;
import java.util.Set;

import eu.robojob.irscw.external.AbstractServiceProvider;
import eu.robojob.irscw.external.communication.CommunicationException;
import eu.robojob.irscw.external.device.AbstractDevice;
import eu.robojob.irscw.external.device.DeviceActionException;
import eu.robojob.irscw.external.robot.RobotActionException;

public abstract class AbstractProcessStep {
	
	protected ProcessFlow processFlow;
	private boolean inProcess;
	protected AbstractDevice device; 
	
	public AbstractProcessStep(ProcessFlow processFlow, AbstractDevice device) {
		this.processFlow = processFlow;
		inProcess = false;
		this.device = device;
	}
	
	public AbstractProcessStep(AbstractDevice device) {
		this(null, device);
	}
	
	public abstract void executeStep() throws CommunicationException, RobotActionException, DeviceActionException;
		
	public abstract String toString();
	
	public boolean isInProcess() {
		return inProcess;
	}

	public void setInProcess(boolean inProcess) {
		this.inProcess = inProcess;
	}
	
	public ProcessFlow getProcessFlow() {
		return processFlow;
	}

	public void setProcessFlow(ProcessFlow processFlow) {
		this.processFlow = processFlow;
	}
	
	public abstract Set<AbstractServiceProvider> getServiceProviders();
	
	public AbstractDevice getDevice() {
		return device;
	}
	
	public abstract ProcessStepType getType();

	public void setDevice(AbstractDevice device) {
		this.device = device;
	}
	
}
