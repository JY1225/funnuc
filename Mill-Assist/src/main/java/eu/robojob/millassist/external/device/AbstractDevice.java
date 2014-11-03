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
import eu.robojob.millassist.workpiece.WorkPiece;
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
	
	public abstract void prepareForPick(DevicePickSettings pickSettings) throws AbstractCommunicationException, DeviceActionException, InterruptedException;
	public abstract void prepareForPut(DevicePutSettings putSettings) throws AbstractCommunicationException, DeviceActionException, InterruptedException;
	public abstract void prepareForIntervention(DeviceInterventionSettings interventionSettings) throws AbstractCommunicationException, DeviceActionException, InterruptedException;
	
	public abstract void pickFinished(DevicePickSettings pickSettings, int processId) throws AbstractCommunicationException, DeviceActionException, InterruptedException;
	public abstract void putFinished(DevicePutSettings putSettings) throws AbstractCommunicationException, DeviceActionException, InterruptedException;
	public abstract void interventionFinished(DeviceInterventionSettings interventionSettings) throws AbstractCommunicationException, DeviceActionException, InterruptedException;
	
	public abstract void releasePiece(DevicePickSettings pickSettings) throws AbstractCommunicationException, DeviceActionException, InterruptedException;
	public abstract void grabPiece(DevicePutSettings putSettings) throws AbstractCommunicationException, DeviceActionException, InterruptedException;
	
	public abstract void reset() throws AbstractCommunicationException, DeviceActionException, InterruptedException;
	
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
		if ((putSettings != null) && (putSettings.getWorkArea() != null) && (getWorkAreas().contains(putSettings.getWorkArea())) 
				&& (putSettings.getWorkArea().getDefaultClamping() != null)) {
			return true;
		} 
		return false;
	}

	public boolean validateInterventionSettings(final DeviceInterventionSettings interventionSettings) {
		if ((interventionSettings != null) && (interventionSettings.getWorkArea() != null) && (getWorkAreas().contains(interventionSettings.getWorkArea())) 
				&& (interventionSettings.getWorkArea().getDefaultClamping() != null)) {
			return true;
		}
		return false;
	}
	
	public abstract Coordinates getPickLocation(WorkArea workArea, WorkPieceDimensions workPieceDimensions, ClampingManner clampType);
	public abstract Coordinates getPutLocation(WorkArea workArea, WorkPieceDimensions workPieceDimensions, ClampingManner clampType);
	public abstract Coordinates getLocationOrientation(WorkArea workArea, ClampingManner clampType);
	
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
	
	public WorkArea getWorkAreaByName(final String name) {
		for (Zone zone : zones) {
			for (WorkArea workArea : zone.getWorkAreas()) {
				if (workArea.getName().equals(name)) {
					return workArea;
				}
			}
		}
		return null;
	}
	
	public WorkArea getWorkAreaById(final int id) {
		for (Zone zone : zones) {
			for (WorkArea workArea : zone.getWorkAreas()) {
				if (workArea.getId() == id) {
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
	
	public DevicePickSettings getDefaultPickSettings(final WorkPiece.Type workPieceType) {
		WorkArea workArea = null;
		if (getWorkAreas().size() == 1) {
			workArea = getWorkAreas().iterator().next();
		}
		return new DevicePickSettings(this, workArea, workPieceType);
	}
	
	public DevicePutSettings getDefaultPutSettings(final WorkPiece.Type workPieceType) {
		WorkArea workArea = null;
		if (getWorkAreas().size() == 1) {
			workArea = getWorkAreas().iterator().next();
		}
		return new DevicePutSettings(this, workArea, workPieceType);
	}
	
	public float getZSafePlane(final WorkPieceDimensions dimensions, final WorkArea workArea, final ApproachType approachType) throws IllegalArgumentException {
		if (approachType.equals(ApproachType.BOTTOM)) {
			throw new IllegalArgumentException("Approach from " + ApproachType.BOTTOM + " is not possible for " + workArea.getZone().getDevice().toString());
		} else {
			float zSafePlane = workArea.getDefaultClamping().getRelativePosition().getZ(); 
			zSafePlane += workArea.getDefaultClamping().getHeight(); // position of the clamping 
			zSafePlane += dimensions.getHeight(); // add height of workpiece held by robot 
			return zSafePlane;
		}
	}
	
}
