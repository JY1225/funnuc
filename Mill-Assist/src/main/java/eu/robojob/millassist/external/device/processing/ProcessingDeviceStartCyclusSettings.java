package eu.robojob.millassist.external.device.processing;

import eu.robojob.millassist.external.device.AbstractDeviceActionSettings;
import eu.robojob.millassist.external.device.WorkArea;
import eu.robojob.millassist.process.ProcessingStep;

public class ProcessingDeviceStartCyclusSettings extends AbstractDeviceActionSettings<ProcessingStep> {

	public ProcessingDeviceStartCyclusSettings(final AbstractProcessingDevice device, final WorkArea workArea) {
		super(device, workArea);
	}
	
	public AbstractProcessingDevice getDevice() {
		return (AbstractProcessingDevice) super.getDevice();
	}

}
