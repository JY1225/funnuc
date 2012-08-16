package eu.robojob.irscw.external.device;

import java.util.List;

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
	
	public abstract void startCyclus(AbstractProcessingDeviceStartCyclusSettings startCylusSettings);	
	public abstract void prepareForStartCyclus(AbstractProcessingDeviceStartCyclusSettings startCylusSettings);
	
	public String toString() {
		return "ProcessingDevice: " + id;
	}
	
	public class AbstractProcessingDevicePutSettings extends AbstractDevicePutSettings {}
	public class AbstractProcessingDevicePickSettings extends AbstractDevicePickSettings {}
	public class AbstractProcessingDeviceInterventionSettings extends AbstractDeviceInterventionSettings {}
	public class AbstractProcessingDeviceStartCyclusSettings {
		protected WorkArea workArea;
	}
	
}
