package eu.robojob.millassist.process;

import eu.robojob.millassist.external.device.AbstractDevice;
import eu.robojob.millassist.external.device.AbstractDeviceActionSettings;

public interface DeviceStep {

	AbstractDeviceActionSettings<?> getDeviceSettings();
	AbstractDevice getDevice();
	
}
