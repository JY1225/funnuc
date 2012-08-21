package eu.robojob.irscw.process;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import eu.robojob.irscw.external.AbstractServiceProvider;
import eu.robojob.irscw.external.device.AbstractDevice;

public class InterventionStep extends AbstractProcessStep {

	private int frequency;
	
	private AbstractDevice.AbstractDeviceInterventionSettings interventionSettings;
	
	private Object interventionOver;
	
	private boolean canContinue;
	
	public InterventionStep(ProcessFlow processFlow, AbstractDevice device, AbstractDevice.AbstractDeviceInterventionSettings interventionSettings, int frequency) {
		super(processFlow, device);
		this.frequency = frequency;
		this.interventionSettings = interventionSettings;
		this.canContinue = false;
		interventionOver = new Object();
	}
	
	public InterventionStep(AbstractDevice device, AbstractDevice.AbstractDeviceInterventionSettings interventionSettings, int frequency) {
		this(null, device, interventionSettings, frequency);
	}
	
	public InterventionStep clone(ProcessFlow parentProcess) {
		return new InterventionStep(parentProcess, device, interventionSettings, frequency);
	}
	
	@Override
	public void executeStep() throws IOException {
		// check if the parent process has locked the device to be used
		if (!device.lock(processFlow)) {
			throw new IllegalStateException("Device " + device + " was already locked by: " + device.getLockingProcess());
		} else {
			device.prepareForIntervention(interventionSettings);
			canContinue = false;
			try {
				interventionOver.wait();
			} catch (InterruptedException e) {
				if (canContinue) {
					canContinue = false;
					device.interventionFinished(interventionSettings);
				} else {
					throw new IllegalStateException("Waiting for intervention to be finished interrupted, but intervention is not signalled as being over");
				}
			}
		}
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
		return "InterventionStep, " + "device: " + device;
	}

	@Override
	public Set<AbstractServiceProvider> getServiceProviders() {
		Set<AbstractServiceProvider> providers = new HashSet<AbstractServiceProvider>();
		providers.add(device);
		return providers;
	}

}
