package eu.robojob.millassist.external.device;

import eu.robojob.millassist.external.device.processing.cnc.AbstractCNCMachine;
import eu.robojob.millassist.process.PutStep;

public class DevicePutSettings extends AbstractDeviceActionSettings<PutStep> {
	
	//value from CNC configure
	private boolean isMachineAirblow = false;
	
	public DevicePutSettings(final AbstractDevice device, final SimpleWorkArea workArea) {
		super(device, workArea);
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