package eu.robojob.irscw.external.device.stacking;

import java.util.List;

import eu.robojob.irscw.external.device.AbstractDevice;
import eu.robojob.irscw.external.device.ClampingManner;
import eu.robojob.irscw.external.device.DeviceType;
import eu.robojob.irscw.external.device.WorkArea;
import eu.robojob.irscw.external.device.Zone;
import eu.robojob.irscw.positioning.Coordinates;
import eu.robojob.irscw.workpiece.WorkPiece.Type;

public abstract class AbstractStackingDevice extends AbstractDevice {

	public AbstractStackingDevice(final String id, final List<Zone> zones) {
		super(id, zones);
	}
	
	public AbstractStackingDevice(final String id) {
		super(id);
	}
	
	public abstract Coordinates getLocation(WorkArea workArea, Type type, ClampingManner clampType);
	
	@Override
	public DeviceType getType() {
		return DeviceType.STACKING;
	}
}
