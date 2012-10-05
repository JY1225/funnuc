package eu.robojob.irscw.external.device;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import eu.robojob.irscw.external.AbstractServiceProvider;
import eu.robojob.irscw.process.AbstractProcessStep;
import eu.robojob.irscw.process.InterventionStep;
import eu.robojob.irscw.process.PickStep;
import eu.robojob.irscw.process.PutStep;

public abstract class AbstractDevice extends AbstractServiceProvider {
	
	protected List<Zone> zones;
	
	public abstract void prepareForPick(AbstractDevicePickSettings pickSettings) throws IOException;
	public abstract void prepareForPut(AbstractDevicePutSettings putSettings) throws IOException;
	public abstract void prepareForIntervention(AbstractDeviceInterventionSettings interventionSettings) throws IOException;
	
	public abstract void pickFinished(AbstractDevicePickSettings pickSettings) throws IOException;
	public abstract void putFinished(AbstractDevicePutSettings putSettings) throws IOException;
	public abstract void interventionFinished(AbstractDeviceInterventionSettings interventionSettings) throws IOException;
	
	public abstract void releasePiece(AbstractDevicePickSettings pickSettings) throws IOException;
	public abstract void grabPiece(AbstractDevicePutSettings putSettings) throws IOException;
	
	public abstract void loadDeviceSettings(AbstractDeviceSettings deviceSettings);
	public abstract AbstractDeviceSettings getDeviceSettings();
	
	public abstract boolean validatePickSettings(AbstractDevicePickSettings pickSettings);
	public abstract boolean validatePutSettings(AbstractDevicePutSettings putSettings);
	public abstract boolean validateInterventionSettings(AbstractDeviceInterventionSettings interventionSettings);
	
	public abstract AbstractDeviceInterventionSettings getInterventionSettings(AbstractDevicePickSettings pickSettings);
	public abstract AbstractDeviceInterventionSettings getInterventionSettings(AbstractDevicePutSettings putSettings);
	
	public AbstractDevice(String id) {
		super(id);
		zones = new ArrayList<Zone>();
	}
	
	public AbstractDevice (String id, List<Zone> zones) {
		this(id);
		for (Zone zone : zones) {
			addZone(zone);
		}
	}

	public void addZone(Zone zone) {
		this.zones.add(zone);
		zone.setDevice(this);
	}
	
	public Zone getZoneById(String id) {
		for (Zone zone : zones) {
			if (zone.getId().equals(id)) {
				return zone;
			}
		}
		return null;
	}
	
	public void removeZone(Zone zone) {
		this.zones.remove(zone);
	}
	
	public WorkArea getWorkAreaById(String id) {
		for (Zone zone : zones) {
			for (WorkArea workArea : zone.getWorkAreas()) {
				if (workArea.getId().equals(id)) {
					return workArea;
				}
			}
		}
		return null;
	}
	
	public List<WorkArea> getWorkAreas() {
		List<WorkArea> workAreas = new ArrayList<WorkArea>();
		for (Zone zone : zones) {
			workAreas.addAll(zone.getWorkAreas());
		}
		return workAreas;
	}
	
	public List<String> getWorkAreaIds() {
		List<String> workAreaIds = new ArrayList<String>();
		for (Zone zone : zones) {
			workAreaIds.addAll(zone.getWorkAreaIds());
		}
		return workAreaIds;
	}
	
	public String toString() {
		return "Device: " + id;
	}
	
	public static abstract class AbstractDeviceActionSettings<T extends AbstractProcessStep> {
		protected WorkArea workArea;
		protected T step;
		
		public AbstractDeviceActionSettings(WorkArea workArea) {
			setWorkArea(workArea);
		}
		
		public void setStep(T step) {
			this.step = step;
		}

		public WorkArea getWorkArea() {
			return workArea;
		}
		
		public void setWorkArea(WorkArea workArea) {
			this.workArea = workArea;
		}
	}
	
	public static abstract class AbstractDevicePickSettings extends AbstractDeviceActionSettings<PickStep> {
				
		public AbstractDevicePickSettings(WorkArea workArea) {
			super(workArea);
		}
		
		public abstract boolean isTeachingNeeded();

	}
	
	public static abstract class AbstractDevicePutSettings extends AbstractDeviceActionSettings<PutStep> {
				
		public AbstractDevicePutSettings(WorkArea workArea) {
			super(workArea);
		}

		public abstract boolean isTeachingNeeded();
		
	}
	
	public static abstract class AbstractDeviceInterventionSettings extends AbstractDeviceActionSettings<InterventionStep> {
				
		public AbstractDeviceInterventionSettings(WorkArea workArea) {
			super(workArea);
		}
	}
	
	public static abstract class AbstractDeviceSettings {
	}
	
	public abstract DeviceType getType();
	
}
