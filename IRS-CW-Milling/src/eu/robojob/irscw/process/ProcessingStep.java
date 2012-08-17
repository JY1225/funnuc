package eu.robojob.irscw.process;

import eu.robojob.irscw.external.device.AbstractProcessingDevice;

public class ProcessingStep extends AbstractProcessStep {

	private AbstractProcessingDevice processingDevice;
	private AbstractProcessingDevice.AbstractProcessingDeviceStartCyclusSettings startCyclusSettings;
	
	public ProcessingStep(Process parentProcess, AbstractProcessingDevice processingDevice,
			AbstractProcessingDevice.AbstractProcessingDeviceStartCyclusSettings startCyclusSettings) {
		super(parentProcess);
		this.processingDevice = processingDevice;
		this.startCyclusSettings = startCyclusSettings;
	}
	
	@Override
	public void executeStep() {
		// check if the parent process has locked the device to be used
		if (!processingDevice.lock(parentProcess)) {
			throw new IllegalStateException("Device " + processingDevice + " was already locked by: " + processingDevice.getLockingProcess());
		} else {
			processingDevice.prepareForStartCyclus(startCyclusSettings);
			processingDevice.startCyclus(startCyclusSettings);
		}
	}

	public AbstractProcessingDevice getProcessingDevice() {
		return processingDevice;
	}

	public void setProcessingDevice(AbstractProcessingDevice processingDevice) {
		this.processingDevice = processingDevice;
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
		return "Processing step, " + "device: " + processingDevice; 
	}
	
}
