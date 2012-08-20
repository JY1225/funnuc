package eu.robojob.irscw.external.device;

import java.util.List;

import eu.robojob.irscw.positioning.Coordinates;

public abstract class AbstractStackingDevice extends AbstractDevice {

	public AbstractStackingDevice(String id, List<Zone> zones) {
		super(id, zones);
	}
	
	public AbstractStackingDevice(String id) {
		super(id);
	}
	
	public abstract boolean canPickWorkpiece();
	public abstract boolean canPutWorkpiece();
	public abstract Coordinates getPickLocation(WorkArea workArea);
	public abstract Coordinates getPutLocation(WorkArea workArea);

}
