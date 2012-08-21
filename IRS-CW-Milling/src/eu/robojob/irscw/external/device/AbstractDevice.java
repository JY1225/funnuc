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
	
	public AbstractDevice (String id, List<Zone> zones) {
		super(id);
		this.zones = zones;
	}
	
	public AbstractDevice(String id) {
		this(id, new ArrayList<Zone>());
	}

	public void addZone(Zone zone) {
		this.zones.add(zone);
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
	
	public String toString() {
		return "Device: " + id;
	}
	
	public abstract class AbstractDeviceActionSettings {
		final private WorkArea workArea;
		
		public AbstractDeviceActionSettings(WorkArea workArea) {
			this.workArea = workArea;
		}

		public WorkArea getWorkArea() {
			return workArea;
		}
	}
	
	public abstract class AbstractDevicePickSettings extends AbstractDeviceActionSettings {
		public AbstractDevicePickSettings(WorkArea workArea) {
			super(workArea);
		}
	}
	
	public abstract class AbstractDevicePutSettings extends AbstractDeviceActionSettings {
		public AbstractDevicePutSettings(WorkArea workArea) {
			super(workArea);
		}
	}
	
	public abstract class AbstractDeviceInterventionSettings extends AbstractDeviceActionSettings {
		public AbstractDeviceInterventionSettings(WorkArea workArea) {
			super(workArea);
		}
	}
	
}
