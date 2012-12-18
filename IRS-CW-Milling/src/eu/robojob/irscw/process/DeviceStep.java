package eu.robojob.irscw.process;

import eu.robojob.irscw.external.device.AbstractDevice;
import eu.robojob.irscw.external.device.AbstractDeviceActionSettings;

public interface DeviceStep {

	AbstractDeviceActionSettings<?> getDeviceSettings();
	AbstractDevice getDevice();
	
}
