package eu.robojob.millassist.external.device.stacking;

import java.util.Set;

import eu.robojob.millassist.external.device.AbstractDevice;
import eu.robojob.millassist.external.device.ClampingManner;
import eu.robojob.millassist.external.device.EDeviceGroup;
import eu.robojob.millassist.external.device.SimpleWorkArea;
import eu.robojob.millassist.external.device.Zone;
import eu.robojob.millassist.external.device.visitor.AbstractPiecePlacementVisitor;
import eu.robojob.millassist.external.robot.AbstractRobotActionSettings.ApproachType;
import eu.robojob.millassist.positioning.Coordinates;
import eu.robojob.millassist.workpiece.IWorkPieceDimensions;
import eu.robojob.millassist.workpiece.WorkPiece;
import eu.robojob.millassist.workpiece.WorkPiece.Type;

public abstract class AbstractStackingDevice extends AbstractDevice {

	private WorkPiece rawWorkPiece;
	private WorkPiece finishedWorkPiece; 
	
	public AbstractStackingDevice(final String name, final Set<Zone> zones) {
		super(name, zones);
	}
	
	public AbstractStackingDevice(final String name) {
		super(name);
	}
		
	@Override
	public EDeviceGroup getType() {
		return EDeviceGroup.STACKING;
	}
	
	public WorkPiece getRawWorkPiece() {
		return rawWorkPiece;
	}

	public void setRawWorkPiece(final WorkPiece rawWorkPiece) {
		this.rawWorkPiece = rawWorkPiece;
	}

	public WorkPiece getFinishedWorkPiece() {
		return finishedWorkPiece;
	}

	public void setFinishedWorkPiece(final WorkPiece finishedWorkPiece) {
		this.finishedWorkPiece = finishedWorkPiece;
	}

	public abstract void clearDeviceSettings();
	
	@Override
	public float getZSafePlane(final IWorkPieceDimensions dimensions, final SimpleWorkArea workArea, final ApproachType approachType) throws IllegalArgumentException {
		float zSafePlane = workArea.getDefaultClamping().getRelativePosition().getZ(); 
		float wpHeight = dimensions.getZSafe(); 
		if (wpHeight > workArea.getDefaultClamping().getHeight()) {
			zSafePlane += wpHeight;
		} else {
			zSafePlane += workArea.getDefaultClamping().getHeight();
		}
		return zSafePlane;
	}
	
	public abstract <T extends IWorkPieceDimensions> Coordinates getLocation(AbstractPiecePlacementVisitor<T> visitor, SimpleWorkArea workArea, Type type, ClampingManner clampType);

}
