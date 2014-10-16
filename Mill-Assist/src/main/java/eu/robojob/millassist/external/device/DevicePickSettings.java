package eu.robojob.millassist.external.device;

import eu.robojob.millassist.process.PickStep;
import eu.robojob.millassist.workpiece.WorkPiece;

public class DevicePickSettings extends AbstractDeviceActionSettings<PickStep> {
	
	public DevicePickSettings(final AbstractDevice device, final WorkArea workArea, final WorkPiece.Type workPieceType) {
		super(device, workArea, workPieceType);
	}
	
}
