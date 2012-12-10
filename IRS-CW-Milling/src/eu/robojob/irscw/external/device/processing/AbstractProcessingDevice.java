package eu.robojob.irscw.external.device.processing;

import java.util.List;

import eu.robojob.irscw.external.communication.AbstractCommunicationException;
import eu.robojob.irscw.external.device.AbstractDevice;
import eu.robojob.irscw.external.device.AbstractDeviceActionSettings;
import eu.robojob.irscw.external.device.DeviceInterventionSettings;
import eu.robojob.irscw.external.device.DevicePickSettings;
import eu.robojob.irscw.external.device.DevicePutSettings;
import eu.robojob.irscw.external.device.DeviceActionException;
import eu.robojob.irscw.external.device.WorkArea;
import eu.robojob.irscw.external.device.Zone;
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
	
	public abstract void startCyclus(ProcessingDeviceStartCyclusSettings startCylusSettings) throws AbstractCommunicationException, DeviceActionException, InterruptedException;	
	public abstract void prepareForStartCyclus(ProcessingDeviceStartCyclusSettings startCylusSettings) throws AbstractCommunicationException, DeviceActionException;
	
	public abstract boolean validateStartCyclusSettings(ProcessingDeviceStartCyclusSettings startCyclusSettings);
	
	public String toString() {
		return "ProcessingDevice: " + id;
	}
	
}
