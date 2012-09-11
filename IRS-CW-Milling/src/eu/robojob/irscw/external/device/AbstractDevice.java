package eu.robojob.irscw.external.device;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import eu.robojob.irscw.external.AbstractServiceProvider;

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
	
	public static abstract class AbstractDeviceActionSettings {
		protected WorkArea workArea;
		
		public AbstractDeviceActionSettings(WorkArea workArea) {
			setWorkArea(workArea);
		}

		public WorkArea getWorkArea() {
			return workArea;
		}
		
		public void setWorkArea(WorkArea workArea) {
			this.workArea = workArea;
		}
	}
	
	public static abstract class AbstractDevicePickSettings extends AbstractDeviceActionSettings {
		
		protected Clamping clamping; 
		
		public AbstractDevicePickSettings(WorkArea workArea, Clamping clamping) {
			super(workArea);
			this.clamping = clamping;
		}

		public Clamping getClamping() {
			return clamping;
		}

		public void setClamping(Clamping clamping) {
			this.clamping = clamping;
		}

	}
	
	public static abstract class AbstractDevicePutSettings extends AbstractDeviceActionSettings {
		
		protected Clamping clamping; 
		
		public AbstractDevicePutSettings(WorkArea workArea, Clamping clamping) {
			super(workArea);
			this.clamping = clamping;
		}

		public Clamping getClamping() {
			return clamping;
		}

		public void setClamping(Clamping clamping) {
			this.clamping = clamping;
		}
		
	}
	
	public static abstract class AbstractDeviceInterventionSettings extends AbstractDeviceActionSettings {
				
		public AbstractDeviceInterventionSettings(WorkArea workArea) {
			super(workArea);
		}
	}
	
	public abstract DeviceType getType();
	
}
