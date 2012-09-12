package eu.robojob.irscw.external.device;

import java.util.List;

import eu.robojob.irscw.positioning.Coordinates;

public abstract class AbstractStackingDevice extends AbstractDevice {

	public AbstractStackingDevice(String id, List<Zone> zones) {
		super(id, zones);
	}
	
	public AbstractStackingDevice(String id) {
		super(id);
	}
	
	public abstract boolean canPickWorkpiece();
	public abstract boolean canPutWorkpiece();
	public abstract Coordinates getPickLocation(WorkArea workArea);
	public abstract Coordinates getPutLocation(WorkArea workArea);

	public static abstract class AbstractStackingDevicePickSettings extends AbstractDevicePickSettings {
		public AbstractStackingDevicePickSettings(WorkArea workArea,
				Clamping clamping) {
			super(workArea, clamping);
		}
	}
	
	public static abstract class AbstractStackingDevicePutSettings extends AbstractDevicePutSettings {
		public AbstractStackingDevicePutSettings(WorkArea workArea,
				Clamping clamping) {
			super(workArea, clamping);
		}
	}
	
	public static abstract class AbstractStackingDeviceInterventionSettings extends AbstractDeviceInterventionSettings {
		public AbstractStackingDeviceInterventionSettings(WorkArea workArea) {
			super(workArea);
		}
	}
	
	@Override
	public DeviceType getType() {
		return DeviceType.STACKING;
	}
}
