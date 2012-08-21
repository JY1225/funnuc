package eu.robojob.irscw.process;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import eu.robojob.irscw.external.AbstractServiceProvider;
import eu.robojob.irscw.external.device.AbstractProcessingDevice;

public class ProcessingStep extends AbstractProcessStep {

	private AbstractProcessingDevice.AbstractProcessingDeviceStartCyclusSettings startCyclusSettings;
	
	public ProcessingStep(Process parentProcess, AbstractProcessingDevice processingDevice,
			AbstractProcessingDevice.AbstractProcessingDeviceStartCyclusSettings startCyclusSettings) {
		super(parentProcess, processingDevice);
		this.startCyclusSettings = startCyclusSettings;
	}
	
	public ProcessingStep clone(Process parentProcess) {
		return new ProcessingStep(parentProcess, (AbstractProcessingDevice) device, startCyclusSettings);
	}

	@Override
	public void executeStep() throws IOException {
		// check if the parent process has locked the device to be used
		if (!device.lock(parentProcess)) {
			throw new IllegalStateException("Device " + device + " was already locked by: " + device.getLockingProcess());
		} else {
			((AbstractProcessingDevice) device).prepareForStartCyclus(startCyclusSettings);
			((AbstractProcessingDevice) device).startCyclus(startCyclusSettings);
		}
	}

	public AbstractProcessingDevice.AbstractProcessingDeviceStartCyclusSettings getStartCyclusSettings() {
		return startCyclusSettings;
	}

	public void setStartCyclusSettings(
			AbstractProcessingDevice.AbstractProcessingDeviceStartCyclusSettings startCyclusSettings) {
		this.startCyclusSettings = startCyclusSettings;
	}

	@Override
	public String toString() {
		return "Processing step, " + "device: " + device; 
	}
	
	@Override
	public Set<AbstractServiceProvider> getServiceProviders() {
		Set<AbstractServiceProvider> providers = new HashSet<AbstractServiceProvider>();
		providers.add(device);
		return providers;
	}
	
}
