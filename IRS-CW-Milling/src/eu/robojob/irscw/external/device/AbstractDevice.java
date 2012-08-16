package eu.robojob.irscw.external.device;

import java.util.ArrayList;
import java.util.List;

import eu.robojob.irscw.external.AbstractServiceProvider;

public abstract class AbstractDevice extends AbstractServiceProvider {
	
	protected List<Zone> zones;
	
	public abstract void prepareForPick(AbstractDevicePickSettings pickSettings);
	public abstract void prepareForPut(AbstractDevicePutSettings putSettings);
	public abstract void prepareForIntervention(AbstractDeviceInterventionSettings interventionSettings);
	
	public abstract void pickFinished(AbstractDevicePickSettings pickSettings);
	public abstract void putFinished(AbstractDevicePutSettings putSettings);
	public abstract void interventionFinished(AbstractDeviceInterventionSettings interventionSettings);
	
	public abstract void releasePiece(AbstractDeviceClampingSettings clampingSettings);
	public abstract void grabPiece(AbstractDeviceClampingSettings clampingSettings);
	
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
	
	public abstract class AbstractDevicePickSettings {
		protected WorkArea workArea;
	}
	public abstract class AbstractDevicePutSettings {
		protected WorkArea workArea;
	}
	public abstract class AbstractDeviceInterventionSettings {
		protected WorkArea workArea;
	}
	public abstract class AbstractDeviceClampingSettings {
		protected WorkArea workArea;
	}
	
	
}
