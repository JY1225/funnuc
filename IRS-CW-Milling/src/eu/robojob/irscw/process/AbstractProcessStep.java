package eu.robojob.irscw.process;

import java.io.IOException;
import java.util.Set;

import eu.robojob.irscw.external.AbstractServiceProvider;
import eu.robojob.irscw.external.device.AbstractDevice;

public abstract class AbstractProcessStep {
	
	protected Process parentProcess;
	private boolean inProcess;
	protected AbstractDevice device; 
	
	public AbstractProcessStep(Process parentProcess, AbstractDevice device) {
		this.parentProcess = parentProcess;
		inProcess = false;
		this.device = device;
	}
	
	public abstract void executeStep() throws IOException;
	
	public abstract AbstractProcessStep clone(Process parentProcess);
	
	public abstract String toString();
	
	public boolean isInProcess() {
		return inProcess;
	}

	public void setInProcess(boolean inProcess) {
		this.inProcess = inProcess;
	}
	
	public Process getParentProcess() {
		return parentProcess;
	}

	public void setParentProcess(Process parentProcess) {
		this.parentProcess = parentProcess;
	}
	
	public abstract Set<AbstractServiceProvider> getServiceProviders();
	
	public AbstractDevice getDevice() {
		return device;
	}
	
}
