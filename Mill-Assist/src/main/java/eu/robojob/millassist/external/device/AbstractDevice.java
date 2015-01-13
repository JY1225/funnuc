package eu.robojob.millassist.external.device;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import eu.robojob.millassist.external.AbstractServiceProvider;
import eu.robojob.millassist.external.communication.AbstractCommunicationException;
import eu.robojob.millassist.external.robot.AbstractRobotActionSettings.ApproachType;
import eu.robojob.millassist.positioning.Coordinates;
import eu.robojob.millassist.process.ProcessFlow;
import eu.robojob.millassist.workpiece.WorkPieceDimensions;

public abstract class AbstractDevice extends AbstractServiceProvider {
	
	public enum DeviceType {
		DEVICE_TYPE_CNCMILLING(1), DEVICE_TYPE_STACKPLATE(2), DEVICE_TYPE_PRAGE(3), DEVICE_TYPE_CONVEYOR(4), DEVICE_TYPE_BIN(5), DEVICE_TYPE_CONVEYOR_EATON(6), DEVICE_TYPE_REVERSAL_UNIT(7);
	
		private int id;
		
		private DeviceType(int id) {
			this.id = id;
		}
		
		public int getId() {
			return this.id;
		}
		
		public static DeviceType getTypeById(int id) {
			for (DeviceType type: values()) {
				if (type.getId() == id) {
					return type;
				}
			}
			throw new IllegalArgumentException("Unknown device type " + id);
		}
		
	}
	
	private Set<Zone> zones;
	
	public abstract void prepareForProcess(ProcessFlow process) throws AbstractCommunicationException, InterruptedException;
		
	public abstract boolean canPick(DevicePickSettings pickSettings) throws AbstractCommunicationException, DeviceActionException;
	public abstract boolean canPut(DevicePutSettings putSettings) throws AbstractCommunicationException, DeviceActionException, InterruptedException;
	public abstract boolean canIntervention(DeviceInterventionSettings interventionSettings) throws AbstractCommunicationException, DeviceActionException;
	
	public abstract void prepareForPick(DevicePickSettings pickSettings, int processId) throws AbstractCommunicationException, DeviceActionException, InterruptedException;
	public abstract void prepareForPut(DevicePutSettings putSettings, int processId) throws AbstractCommunicationException, DeviceActionException, InterruptedException;
	public abstract void prepareForIntervention(DeviceInterventionSettings interventionSettings) throws AbstractCommunicationException, DeviceActionException, InterruptedException;
	
	public abstract void pickFinished(DevicePickSettings pickSettings, int processId) throws AbstractCommunicationException, DeviceActionException, InterruptedException;
	public abstract void putFinished(DevicePutSettings putSettings) throws AbstractCommunicationException, DeviceActionException, InterruptedException;
	public abstract void interventionFinished(DeviceInterventionSettings interventionSettings) throws AbstractCommunicationException, DeviceActionException, InterruptedException;
	
	public abstract void releasePiece(DevicePickSettings pickSettings) throws AbstractCommunicationException, DeviceActionException, InterruptedException;
	public abstract void grabPiece(DevicePutSettings putSettings) throws AbstractCommunicationException, DeviceActionException, InterruptedException;
	
	public abstract void reset() throws AbstractCommunicationException, DeviceActionException, InterruptedException;
	
	/**
	 * Load specific process information into the device
	 * 
	 * @param deviceSettings
	 */
	public abstract void loadDeviceSettings(DeviceSettings deviceSettings);
	public abstract DeviceSettings getDeviceSettings();
	
	public boolean validatePickSettings(final DevicePickSettings pickSettings) {
		if ((pickSettings != null) && (pickSettings.getWorkArea() != null) && (getWorkAreaNames().contains(pickSettings.getWorkArea().getName())) 
				&& (pickSettings.getWorkArea().getDefaultClamping() != null)) {
			return true;
		}
		return false;
	}

	public boolean validatePutSettings(final DevicePutSettings putSettings) {
		if ((putSettings != null) && (putSettings.getWorkArea() != null) && (getWorkAreaNames().contains(putSettings.getWorkArea().getName())) 
				&& (putSettings.getWorkArea().getDefaultClamping() != null)) {
			return true;
		} 
		return false;
	}

	public boolean validateInterventionSettings(final DeviceInterventionSettings interventionSettings) {
		if ((interventionSettings != null) && (interventionSettings.getWorkArea() != null) && (getWorkAreaNames().contains(interventionSettings.getWorkArea().getName())) 
				&& (interventionSettings.getWorkArea().getDefaultClamping() != null)) {
			return true;
		}
		return false;
	}
	
	public abstract Coordinates getPickLocation(SimpleWorkArea workArea, WorkPieceDimensions workPieceDimensions, ClampingManner clampType);
	public abstract Coordinates getPutLocation(SimpleWorkArea workArea, WorkPieceDimensions workPieceDimensions, ClampingManner clampType);
	public abstract Coordinates getLocationOrientation(SimpleWorkArea workArea, ClampingManner clampType);
	
	public abstract void interruptCurrentAction();
	
	public abstract boolean isConnected();
	
	public AbstractDevice(final String name) {
		super(name);
		zones = new HashSet<Zone>();
	}
	
	public AbstractDevice(final String name, final Set<Zone> zones) {
		this(name);
		for (Zone zone : zones) {
			addZone(zone);
		}
	}
	
	protected void setZones(final Set<Zone> zones) {
		this.zones = zones;
	}
	
	public Set<Zone> getZones() {
		return this.zones;
	}

	public void addZone(final Zone zone) {
		this.zones.add(zone);
		zone.setDevice(this);
	}
	
	public Zone getZoneByName(final String name) {
		for (Zone zone : zones) {
			if (zone.getName().equals(name)) {
				return zone;
			}
		}
		return null;
	}
	
	public void removeZone(final Zone zone) {
		this.zones.remove(zone);
	}
	
	public WorkAreaManager getWorkAreaByName(final String name) {
		for (Zone zone : zones) {
			for (WorkAreaManager workArea : zone.getWorkAreaManagers()) {
				if (workArea.getName().equals(name)) {
					return workArea;
				}
			}
		}
		return null;
	}
	
	public SimpleWorkArea getWorkAreaById(final int id) {
		for (Zone zone : zones) {
			for (WorkAreaManager workAreaManager : zone.getWorkAreaManagers()) {
				for (SimpleWorkArea workArea: workAreaManager.getWorkAreas().values()) {
					if (workArea.getId() == id) {
						return workArea;
					}
				}
			}
		}
		return null;
	}
	
	public WorkAreaManager getWorkAreaManagerById(final int id) {
		for (Zone zone: zones) {
			for (WorkAreaManager workAreaManager: zone.getWorkAreaManagers()) {
				if (workAreaManager.getId() == id) {
					return workAreaManager;
				}
			}
		}
		return null;
	}
	
	public List<SimpleWorkArea> getWorkAreas() {
		List<SimpleWorkArea> workAreas = new ArrayList<SimpleWorkArea>();
		for (WorkAreaManager workArea: getWorkAreaManagers()) {
			workAreas.addAll(workArea.getWorkAreas().values());
		}
		return workAreas;
	}
	
	public List<WorkAreaManager> getWorkAreaManagers() {
		List<WorkAreaManager> workAreas = new ArrayList<WorkAreaManager>();
		for (Zone zone : zones) {
			workAreas.addAll(zone.getWorkAreaManagers());
		}
		return workAreas;
	}
	
	public List<String> getWorkAreaNames() {
		List<String> workAreaNames = new ArrayList<String>();
		for (Zone zone : zones) {
			workAreaNames.addAll(zone.getWorkAreaNames());
		}
		return workAreaNames;
	}
	
	public String toString() {
		return "Device: " + getName();
	}
	
	public abstract EDeviceGroup getType();
	
	public DevicePickSettings getDefaultPickSettings(int sequenceNb) {
		SimpleWorkArea workArea = null;
		if (getWorkAreas().size() == 1) {
			workArea = getWorkAreaManagers().iterator().next().getWorkAreaWithSequence(sequenceNb);
		}
		return new DevicePickSettings(this, workArea);
	}
	
	public DevicePutSettings getDefaultPutSettings(int sequenceNb) {
		SimpleWorkArea workArea = null;
		if (getWorkAreas().size() == 1) {
			workArea = getWorkAreaManagers().iterator().next().getWorkAreaWithSequence(sequenceNb);
		}
		return new DevicePutSettings(this, workArea);
	}
	
	public float getZSafePlane(final WorkPieceDimensions dimensions, final SimpleWorkArea workArea, final ApproachType approachType) throws IllegalArgumentException {
		if (approachType.equals(ApproachType.BOTTOM)) {
			throw new IllegalArgumentException("Approach from " + ApproachType.BOTTOM + " is not possible for " + workArea.getWorkAreaManager().getZone().getDevice().toString());
		} else {
			float zSafePlane = workArea.getDefaultClamping().getRelativePosition().getZ(); 
			zSafePlane += workArea.getDefaultClamping().getHeight(); // position of the clamping 
			zSafePlane += dimensions.getHeight(); // add height of workpiece held by robot 
			return zSafePlane;
		}
	}
	
}
