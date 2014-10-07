package eu.robojob.millassist.external.device;

import eu.robojob.millassist.process.PutStep;
import eu.robojob.millassist.workpiece.WorkPiece;

public class DevicePutSettings extends AbstractDeviceActionSettings<PutStep> {
	
	public DevicePutSettings(final AbstractDevice device, final WorkArea workArea, final WorkPiece workPiece) {
		super(device, workArea, workPiece);
	}
	
}