package eu.robojob.millassist.external.device.processing;

import eu.robojob.millassist.external.device.AbstractDeviceActionSettings;
import eu.robojob.millassist.external.device.SimpleWorkArea;
import eu.robojob.millassist.process.ProcessingStep;

public class ProcessingDeviceStartCyclusSettings extends AbstractDeviceActionSettings<ProcessingStep> {

	public ProcessingDeviceStartCyclusSettings(final AbstractProcessingDevice device, final SimpleWorkArea workArea) {
		super(device, workArea);
	}
	
	public AbstractProcessingDevice getDevice() {
		return (AbstractProcessingDevice) super.getDevice();
	}

}
