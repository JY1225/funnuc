package eu.robojob.millassist.external.device.processing;

import eu.robojob.millassist.external.device.AbstractDeviceActionSettings;
import eu.robojob.millassist.external.device.WorkArea;
import eu.robojob.millassist.process.ProcessingStep;
import eu.robojob.millassist.workpiece.WorkPiece;

public class ProcessingDeviceStartCyclusSettings extends AbstractDeviceActionSettings<ProcessingStep> {

	public ProcessingDeviceStartCyclusSettings(final AbstractProcessingDevice device, final WorkArea workArea, final WorkPiece workPiece) {
		super(device, workArea, workPiece);
	}
	
	public AbstractProcessingDevice getDevice() {
		return (AbstractProcessingDevice) super.getDevice();
	}

}
