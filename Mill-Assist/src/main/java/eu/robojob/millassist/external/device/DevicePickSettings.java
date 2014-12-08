package eu.robojob.millassist.external.device;

import eu.robojob.millassist.external.device.processing.cnc.AbstractCNCMachine;
import eu.robojob.millassist.process.PickStep;
import eu.robojob.millassist.workpiece.WorkPiece;

public class DevicePickSettings extends AbstractDeviceActionSettings<PickStep> {
	
	private boolean isMachineAirblow = false;
	
	public DevicePickSettings(final AbstractDevice device, final WorkArea workArea, final WorkPiece.Type workPieceType) {
		super(device, workArea, workPieceType);
	}
	
	public boolean getMachineAirblow() {
		if (getDevice() instanceof AbstractCNCMachine) {
			return (((AbstractCNCMachine) getDevice()).getMachineAirblow() && this.isMachineAirblow);
		}
		return false;
	}
	
	public void setIsMachineAirblow(final boolean isMachineAirblow) {
		this.isMachineAirblow = isMachineAirblow;
	}
	
}
