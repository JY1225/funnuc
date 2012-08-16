package eu.robojob.irscw.process;

import eu.robojob.irscw.external.device.AbstractDevice;
import eu.robojob.irscw.external.device.WorkArea;

public class InterventionStep extends AbstractProcessStep {

	private AbstractDevice device;
	private WorkArea workArea;
	private int frequency;
	
	private AbstractDevice.AbstractDeviceInterventionSettings interventionSettings;
	
	private Object interventionOver;
	
	private boolean canContinue;
	
	public InterventionStep(Process parentProcess, AbstractDevice device, WorkArea workArea, AbstractDevice.AbstractDeviceInterventionSettings interventionSettings, int frequency) {
		super(parentProcess);
		this.device = device;
		this.workArea = workArea;
		this.frequency = frequency;
		this.interventionSettings = interventionSettings;
		this.canContinue = false;
		interventionOver = new Object();
	}
	
	@Override
	public void executeStep() {
		// check if the parent process has locked the device to be used
		if (!device.lock(parentProcess)) {
			throw new IllegalStateException("Device " + device + " was already locked by: " + device.getLockingProcess());
		} else {
			device.prepareForIntervention(workArea, interventionSettings);
			canContinue = false;
			try {
				interventionOver.wait();
			} catch (InterruptedException e) {
				if (canContinue) {
					canContinue = false;
					device.interventionFinished(workArea, interventionSettings);
				} else {
					throw new IllegalStateException("Waiting for intervention to be finished interrupted, but intervention is not signalled as being over");
				}
			}
		}
	}

	public AbstractDevice getDevice() {
		return device;
	}

	public void setDevice(AbstractDevice device) {
		this.device = device;
	}

	public AbstractDevice.AbstractDeviceInterventionSettings getInterventionSettings() {
		return interventionSettings;
	}

	public void setInterventionSettings(
			AbstractDevice.AbstractDeviceInterventionSettings interventionSettings) {
		this.interventionSettings = interventionSettings;
	}

	public int getFrequency() {
		return frequency;
	}

	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}
	
	public void continueExecution() {
		canContinue = true;
		interventionOver.notify();
	}

	@Override
	public String toString() {
		return "InterventionStep, " + "device: " + device + "(" + workArea + ")";
	}

}
