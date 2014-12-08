package eu.robojob.millassist.external.device;

import eu.robojob.millassist.external.device.processing.cnc.AbstractCNCMachine;
import eu.robojob.millassist.process.PutStep;
import eu.robojob.millassist.workpiece.WorkPiece;

public class DevicePutSettings extends AbstractDeviceActionSettings<PutStep> {
	
	//value from CNC configure
	private boolean isMachineAirblow = false;
	
	public DevicePutSettings(final AbstractDevice device, final WorkArea workArea, final WorkPiece.Type workPieceType) {
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