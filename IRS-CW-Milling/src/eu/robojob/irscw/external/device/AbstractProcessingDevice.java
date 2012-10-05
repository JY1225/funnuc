package eu.robojob.irscw.external.device;

import java.io.IOException;
import java.util.List;

import eu.robojob.irscw.process.ProcessingStep;

public abstract class AbstractProcessingDevice extends AbstractDevice {
	
	private boolean isInvasive;
	
	public AbstractProcessingDevice (String id, List<Zone> zones, boolean isInvasive) {
		super(id, zones);
		this.isInvasive = isInvasive;
	}
	
	public AbstractProcessingDevice (String id, boolean isInvasive) {
		super(id);
		this.isInvasive = isInvasive;
	}
		
	public boolean isInvasive() {
		return isInvasive;
	}
	
	public abstract void startCyclus(AbstractProcessingDeviceStartCyclusSettings startCylusSettings) throws IOException;	
	public abstract void prepareForStartCyclus(AbstractProcessingDeviceStartCyclusSettings startCylusSettings) throws IOException;
	
	public abstract boolean validateStartCyclusSettings(AbstractProcessingDeviceStartCyclusSettings startCyclusSettings);
	
	public String toString() {
		return "ProcessingDevice: " + id;
	}
	
	public abstract static class AbstractProcessingDevicePutSettings extends AbstractDevicePutSettings {
		public AbstractProcessingDevicePutSettings(WorkArea workArea) {
			super(workArea);
		}
	}
	public abstract static class AbstractProcessingDevicePickSettings extends AbstractDevicePickSettings {
		public AbstractProcessingDevicePickSettings(WorkArea workArea) {
			super(workArea);
		}
	}
	public static class AbstractProcessingDeviceInterventionSettings extends AbstractDeviceInterventionSettings {
		public AbstractProcessingDeviceInterventionSettings(WorkArea workArea) {
			super(workArea);
		}
	}
	public static class AbstractProcessingDeviceStartCyclusSettings extends AbstractDeviceActionSettings<ProcessingStep> {
		public AbstractProcessingDeviceStartCyclusSettings(WorkArea workArea) {
			super(workArea);
		}
	}
	
}
