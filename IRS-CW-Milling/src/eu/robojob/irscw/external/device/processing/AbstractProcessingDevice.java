package eu.robojob.irscw.external.device.processing;

import java.util.List;

import eu.robojob.irscw.external.communication.AbstractCommunicationException;
import eu.robojob.irscw.external.device.AbstractDevice;
import eu.robojob.irscw.external.device.DeviceActionException;
import eu.robojob.irscw.external.device.Zone;

public abstract class AbstractProcessingDevice extends AbstractDevice {
	
	private boolean isInvasive;
	
	public AbstractProcessingDevice(final String id, final List<Zone> zones, final boolean isInvasive) {
		super(id, zones);
		this.isInvasive = isInvasive;
	}
	
	public AbstractProcessingDevice(final String id, final boolean isInvasive) {
		super(id);
		this.isInvasive = isInvasive;
	}
		
	public boolean isInvasive() {
		return isInvasive;
	}
	
	public abstract void startCyclus(ProcessingDeviceStartCyclusSettings startCylusSettings) throws AbstractCommunicationException, DeviceActionException, InterruptedException;	
	public abstract void prepareForStartCyclus(ProcessingDeviceStartCyclusSettings startCylusSettings) throws AbstractCommunicationException, DeviceActionException;
		
	public String toString() {
		return "ProcessingDevice: " + getId();
	}
	
	public boolean validateStartCyclusSettings(final ProcessingDeviceStartCyclusSettings startCyclusSettings) {
		if ((startCyclusSettings != null) && (startCyclusSettings.getWorkArea() != null) && (getWorkAreaIds().contains(startCyclusSettings.getWorkArea().getId())) 
				&& (startCyclusSettings.getWorkArea().getActiveClamping() != null)) {
			return true;
		}
		return false;
	}
	
}
