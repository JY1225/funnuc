package eu.robojob.irscw.external.device;

import java.util.List;


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
		public AbstractCNCMachinePutSettings(WorkArea workArea, Clamping clamping) {
			super(workArea, clamping);
		}
	}
	public abstract static class AbstractCNCMachinePickSettings extends AbstractProcessingDevicePickSettings{
		public AbstractCNCMachinePickSettings(WorkArea workArea, Clamping clamping) {
			super(workArea, clamping);
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
