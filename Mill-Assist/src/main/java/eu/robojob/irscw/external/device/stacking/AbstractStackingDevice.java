package eu.robojob.irscw.external.device.stacking;

import java.util.Set;

import eu.robojob.irscw.external.device.AbstractDevice;
import eu.robojob.irscw.external.device.ClampingManner;
import eu.robojob.irscw.external.device.DeviceType;
import eu.robojob.irscw.external.device.WorkArea;
import eu.robojob.irscw.external.device.Zone;
import eu.robojob.irscw.positioning.Coordinates;
import eu.robojob.irscw.workpiece.WorkPiece.Type;

public abstract class AbstractStackingDevice extends AbstractDevice {

	public AbstractStackingDevice(final String name, final Set<Zone> zones) {
		super(name, zones);
	}
	
	public AbstractStackingDevice(final String name) {
		super(name);
	}
	
	public abstract Coordinates getLocation(WorkArea workArea, Type type, ClampingManner clampType);
	
	@Override
	public DeviceType getType() {
		return DeviceType.STACKING;
	}
}
