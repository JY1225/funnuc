package eu.robojob.irscw.external.device;

import java.util.List;

public abstract class AbstractStackingDevice extends AbstractDevice {

	public AbstractStackingDevice(String id, List<Zone> zones) {
		super(id, zones);
	}
	
	public AbstractStackingDevice(String id) {
		super(id);
	}
	
	public static abstract class AbstractStackingDevicePickSettings extends AbstractDevicePickSettings {
		public AbstractStackingDevicePickSettings(WorkArea workArea) {
			super(workArea);
		}
	}
	
	public static abstract class AbstractStackingDevicePutSettings extends AbstractDevicePutSettings {
		public AbstractStackingDevicePutSettings(WorkArea workArea) {
			super(workArea);
		}
	}
	
	public static abstract class AbstractStackingDeviceInterventionSettings extends AbstractDeviceInterventionSettings {
		public AbstractStackingDeviceInterventionSettings(WorkArea workArea) {
			super(workArea);
		}
	}
	
	public abstract class AbstractStackingDeviceSettings extends AbstractDeviceSettings {
	}
	
	@Override
	public DeviceType getType() {
		return DeviceType.STACKING;
	}
}
