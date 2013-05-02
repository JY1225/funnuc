package eu.robojob.irscw.external.device.processing;

import eu.robojob.irscw.external.device.AbstractDeviceActionSettings;
import eu.robojob.irscw.external.device.WorkArea;
import eu.robojob.irscw.process.ProcessingStep;

public class ProcessingDeviceStartCyclusSettings extends AbstractDeviceActionSettings<ProcessingStep> {

	public ProcessingDeviceStartCyclusSettings(final AbstractProcessingDevice device, final WorkArea workArea) {
		super(device, workArea);
	}
	
	public AbstractProcessingDevice getDevice() {
		return (AbstractProcessingDevice) super.getDevice();
	}

}
