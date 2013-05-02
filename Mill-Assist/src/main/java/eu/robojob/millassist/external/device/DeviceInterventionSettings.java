package eu.robojob.millassist.external.device;

import eu.robojob.millassist.process.InterventionStep;

public class DeviceInterventionSettings extends AbstractDeviceActionSettings<InterventionStep> {
	
	public DeviceInterventionSettings(final AbstractDevice device, final WorkArea workArea) {
		super(device, workArea);
	}
}