package eu.robojob.irscw.external.device;

import eu.robojob.irscw.process.InterventionStep;

public class DeviceInterventionSettings extends AbstractDeviceActionSettings<InterventionStep> {
	
	public DeviceInterventionSettings(final AbstractDevice device, final WorkArea workArea) {
		super(device, workArea);
	}
}