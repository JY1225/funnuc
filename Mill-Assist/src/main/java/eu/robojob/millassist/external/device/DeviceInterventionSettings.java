package eu.robojob.millassist.external.device;

import eu.robojob.millassist.process.InterventionStep;
import eu.robojob.millassist.workpiece.WorkPiece;

public class DeviceInterventionSettings extends AbstractDeviceActionSettings<InterventionStep> {
	
	public DeviceInterventionSettings(final AbstractDevice device, final WorkArea workArea, final WorkPiece.Type workPieceType) {
		super(device, workArea, workPieceType);
	}
}