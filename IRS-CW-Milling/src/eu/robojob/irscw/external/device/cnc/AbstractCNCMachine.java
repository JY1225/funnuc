package eu.robojob.irscw.external.device.cnc;

import java.util.List;

import eu.robojob.irscw.external.device.AbstractProcessingDevice;
import eu.robojob.irscw.external.device.DeviceType;
import eu.robojob.irscw.external.device.WorkArea;
import eu.robojob.irscw.external.device.Zone;
import eu.robojob.irscw.external.device.AbstractProcessingDevice.AbstractProcessingDeviceInterventionSettings;
import eu.robojob.irscw.external.device.AbstractProcessingDevice.AbstractProcessingDevicePickSettings;
import eu.robojob.irscw.external.device.AbstractProcessingDevice.AbstractProcessingDevicePutSettings;
import eu.robojob.irscw.external.device.AbstractProcessingDevice.AbstractProcessingDeviceStartCyclusSettings;


public abstract class AbstractCNCMachine extends AbstractProcessingDevice {

	public AbstractCNCMachine(String id) {
		super(id, true);
	}
	
	public AbstractCNCMachine(String id, List<Zone> zones) {
		super(id, zones, true);
	}

	@Override
	public DeviceType getType() {
		return DeviceType.CNC_MACHINE;
	}

	public abstract static class AbstractCNCMachinePutSettings extends AbstractProcessingDevicePutSettings{
		public AbstractCNCMachinePutSettings(WorkArea workArea) {
			super(workArea);
		}
	}
	public abstract static class AbstractCNCMachinePickSettings extends AbstractProcessingDevicePickSettings{
		public AbstractCNCMachinePickSettings(WorkArea workArea) {
			super(workArea);
		}
	}
	public abstract static class AbstractCNCMachineInterventionSettings extends AbstractProcessingDeviceInterventionSettings{
		public AbstractCNCMachineInterventionSettings(WorkArea workArea) {
			super(workArea);
		}
	}
	public abstract static class AbstractCNCMachineStartCyclusSettings extends AbstractProcessingDeviceStartCyclusSettings {
		public AbstractCNCMachineStartCyclusSettings(WorkArea workArea) {
			super(workArea);
		}
	}
}
