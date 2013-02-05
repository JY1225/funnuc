package eu.robojob.irscw.external.device.processing;

import java.util.Set;

import eu.robojob.irscw.external.communication.AbstractCommunicationException;
import eu.robojob.irscw.external.device.AbstractDevice;
import eu.robojob.irscw.external.device.DeviceActionException;
import eu.robojob.irscw.external.device.Zone;

public abstract class AbstractProcessingDevice extends AbstractDevice {
	
	private boolean isInvasive;
	
	public AbstractProcessingDevice(final String name, final Set<Zone> zones, final boolean isInvasive) {
		super(name, zones);
		this.isInvasive = isInvasive;
	}
	
	public AbstractProcessingDevice(final String name, final boolean isInvasive) {
		super(name);
		this.isInvasive = isInvasive;
	}
		
	public boolean isInvasive() {
		return isInvasive;
	}
	
	public abstract void startCyclus(ProcessingDeviceStartCyclusSettings startCylusSettings) throws AbstractCommunicationException, DeviceActionException, InterruptedException;	
	public abstract void prepareForStartCyclus(ProcessingDeviceStartCyclusSettings startCylusSettings) throws AbstractCommunicationException, DeviceActionException;
		
	public String toString() {
		return "ProcessingDevice: " + getName();
	}
	
	public boolean validateStartCyclusSettings(final ProcessingDeviceStartCyclusSettings startCyclusSettings) {
		if ((startCyclusSettings != null) && (startCyclusSettings.getWorkArea() != null) && (getWorkAreaNames().contains(startCyclusSettings.getWorkArea().getName())) 
				&& (startCyclusSettings.getWorkArea().getActiveClamping() != null)) {
			return true;
		}
		return false;
	}
	
}
