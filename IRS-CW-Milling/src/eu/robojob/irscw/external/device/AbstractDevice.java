package eu.robojob.irscw.external.device;

import java.util.ArrayList;
import java.util.List;

import eu.robojob.irscw.external.AbstractServiceProvider;
import eu.robojob.irscw.external.communication.AbstractCommunicationException;
import eu.robojob.irscw.positioning.Coordinates;
import eu.robojob.irscw.process.AbstractProcessStep;
import eu.robojob.irscw.process.InterventionStep;
import eu.robojob.irscw.process.PickStep;
import eu.robojob.irscw.process.ProcessFlow;
import eu.robojob.irscw.process.PutStep;
import eu.robojob.irscw.workpiece.WorkPieceDimensions;

public abstract class AbstractDevice extends AbstractServiceProvider {
	
	protected List<Zone> zones;
	
	public abstract void prepareForProcess(ProcessFlow process) throws AbstractCommunicationException, InterruptedException;
		
	public abstract boolean canPick(AbstractDevicePickSettings pickSettings) throws AbstractCommunicationException, DeviceActionException;
	public abstract boolean canPut(AbstractDevicePutSettings putSettings) throws AbstractCommunicationException, DeviceActionException, InterruptedException;
	
	public abstract void prepareForPick(AbstractDevicePickSettings pickSettings) throws AbstractCommunicationException, DeviceActionException, InterruptedException;
	public abstract void prepareForPut(AbstractDevicePutSettings putSettings) throws AbstractCommunicationException, DeviceActionException, InterruptedException;
	public abstract void prepareForIntervention(AbstractDeviceInterventionSettings interventionSettings) throws AbstractCommunicationException, DeviceActionException;
	
	public abstract void pickFinished(AbstractDevicePickSettings pickSettings) throws AbstractCommunicationException, DeviceActionException;
	public abstract void putFinished(AbstractDevicePutSettings putSettings) throws AbstractCommunicationException, DeviceActionException;
	public abstract void interventionFinished(AbstractDeviceInterventionSettings interventionSettings) throws AbstractCommunicationException, DeviceActionException;
	
	public abstract void releasePiece(AbstractDevicePickSettings pickSettings) throws AbstractCommunicationException, DeviceActionException, InterruptedException;
	public abstract void grabPiece(AbstractDevicePutSettings putSettings) throws AbstractCommunicationException, DeviceActionException, InterruptedException;
	
	public abstract void loadDeviceSettings(AbstractDeviceSettings deviceSettings);
	public abstract AbstractDeviceSettings getDeviceSettings();
	
	public abstract boolean validatePickSettings(AbstractDevicePickSettings pickSettings);
	public abstract boolean validatePutSettings(AbstractDevicePutSettings putSettings);
	public abstract boolean validateInterventionSettings(AbstractDeviceInterventionSettings interventionSettings);
	
	public abstract AbstractDeviceInterventionSettings getInterventionSettings(AbstractDevicePickSettings pickSettings);
	public abstract AbstractDeviceInterventionSettings getInterventionSettings(AbstractDevicePutSettings putSettings);
	
	public abstract Coordinates getPickLocation(WorkArea workArea, ClampingType clampType);
	public abstract Coordinates getPutLocation(WorkArea workArea, WorkPieceDimensions workPieceDimensions, ClampingType clampType);
	
	public abstract void stopCurrentAction();
	
	public abstract boolean isConnected();
	
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
		
	}
	
	public static abstract class AbstractDevicePutSettings extends AbstractDeviceActionSettings<PutStep> {
				
		public AbstractDevicePutSettings(WorkArea workArea) {
			super(workArea);
		}

		public abstract boolean isPutPositionFixed();
		
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
