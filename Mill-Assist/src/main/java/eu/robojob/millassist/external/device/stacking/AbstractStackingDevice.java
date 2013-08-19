package eu.robojob.millassist.external.device.stacking;

import java.util.Set;

import eu.robojob.millassist.external.device.AbstractDevice;
import eu.robojob.millassist.external.device.ClampingManner;
import eu.robojob.millassist.external.device.DeviceActionException;
import eu.robojob.millassist.external.device.DeviceType;
import eu.robojob.millassist.external.device.WorkArea;
import eu.robojob.millassist.external.device.Zone;
import eu.robojob.millassist.positioning.Coordinates;
import eu.robojob.millassist.workpiece.WorkPiece.Type;

public abstract class AbstractStackingDevice extends AbstractDevice {

	public AbstractStackingDevice(final String name, final Set<Zone> zones) {
		super(name, zones);
	}
	
	public AbstractStackingDevice(final String name) {
		super(name);
	}
	
	public abstract Coordinates getLocation(WorkArea workArea, Type type, ClampingManner clampType) throws DeviceActionException, InterruptedException;
	
	@Override
	public DeviceType getType() {
		return DeviceType.STACKING;
	}
}
