package eu.robojob.millassist.external.device.visitor;

import eu.robojob.millassist.external.device.ClampingManner;
import eu.robojob.millassist.external.device.SimpleWorkArea;
import eu.robojob.millassist.external.device.processing.cnc.AbstractCNCMachine;
import eu.robojob.millassist.external.device.processing.prage.PrageDevice;
import eu.robojob.millassist.external.device.processing.reversal.ReversalUnit;
import eu.robojob.millassist.external.device.stacking.conveyor.eaton.ConveyorEaton;
import eu.robojob.millassist.external.device.stacking.conveyor.normal.Conveyor;
import eu.robojob.millassist.external.robot.AbstractRobotActionSettings.ApproachType;
import eu.robojob.millassist.positioning.Coordinates;
import eu.robojob.millassist.workpiece.RoundDimensions;
import eu.robojob.millassist.workpiece.WorkPiece.Type;

public final class CylindricPlacementVisitor extends AbstractPiecePlacementVisitor<RoundDimensions> {

	@Override
	public Coordinates getPutLocation(PrageDevice device,
			SimpleWorkArea workArea, RoundDimensions dimensions,
			ClampingManner clampType, ApproachType approachType) {
		return new Coordinates(workArea.getDefaultClamping().getRelativePosition());
	}

	@Override
	public Coordinates getPutLocation(ReversalUnit reversal,
			SimpleWorkArea workArea, RoundDimensions dimensions,
			ClampingManner clampType, ApproachType approachType) {
		Coordinates c = new Coordinates(workArea.getDefaultClamping().getRelativePosition());
		c.setX(c.getX() + getXCoord(dimensions, approachType) + reversal.getAddedXOrigin());
		c.setY(c.getY() + getYCoord(dimensions, approachType));
		c.setZ(c.getZ() + getZCoord(dimensions, approachType));
		return c;
	}
	
	private static float getXCoord(final RoundDimensions workPieceDimensions, final ApproachType approachType) {
		switch (approachType) {
		case BOTTOM:
		case TOP:
		case LEFT:
			return workPieceDimensions.getDiameter()/2;
		case FRONT:
			return workPieceDimensions.getHeight();
		default: 
			return 0;
		}
	}

	private static float getYCoord(final RoundDimensions workPieceDimensions, final ApproachType approachType) {
		switch (approachType) {
		case BOTTOM:
		case TOP:
		case FRONT:
			return workPieceDimensions.getDiameter()/2;
		case LEFT:
			return 0;
		default:
			return 0;
		}
	}
	
	private static float getZCoord(final RoundDimensions workPieceDimensions, final ApproachType approachType) {
		switch (approachType) {
		case BOTTOM:
		case TOP:	
			return 0;
		case FRONT:
			return workPieceDimensions.getDiameter()/2;
		case LEFT:
			return workPieceDimensions.getDiameter()/2;
		default:
			return 0;
		}
	}
	

	@Override
	public Coordinates getPickLocation(ReversalUnit reversal,
			SimpleWorkArea workArea, RoundDimensions dimensions,
			ClampingManner clampType, ApproachType approachType) {
		return getPutLocation(reversal, workArea, dimensions, clampType, approachType);
	}

	@Override
	public Coordinates getPutLocation(AbstractCNCMachine cncMachine,
			SimpleWorkArea workArea, RoundDimensions dimensions,
			ClampingManner clampType, ApproachType approachType) {
		Coordinates c = new Coordinates(workArea.getWorkAreaManager().getActiveClamping(false, workArea.getSequenceNb()).getRelativePosition());
		c.setR(cncMachine.getRRoundPieces());
		return c;
	}
	
	@Override
	public Coordinates getPickLocation(AbstractCNCMachine cncMachine,
			SimpleWorkArea workArea, RoundDimensions dimensions,
			ClampingManner clampType, ApproachType approachType) {
		Coordinates c = new Coordinates(workArea.getWorkAreaManager().getActiveClamping(true, workArea.getSequenceNb()).getRelativePosition());
		c.setR(cncMachine.getRRoundPieces());
		return c;
	}
	
	@Override
	public Coordinates getPutLocation(Conveyor conveyor,
			SimpleWorkArea workArea, RoundDimensions dimensions,
			ClampingManner clampType, ApproachType approachType) {
		throw new IllegalArgumentException("Round pieces on the conveyor are not supported by the current software version");
	}

	@Override
	public Coordinates getPutLocation(ConveyorEaton conveyor,
			SimpleWorkArea workArea, RoundDimensions dimensions,
			ClampingManner clampType, ApproachType approachType) {
		throw new IllegalArgumentException("Round pieces on the conveyor are not supported by the current software version");
	}

	@Override
	public Coordinates getPickLocation(Conveyor conveyor,
			SimpleWorkArea workArea, RoundDimensions dimensions,
			ClampingManner clampType, ApproachType approachType) {
		throw new IllegalArgumentException("Round pieces on the conveyor are not supported by the current software version");
	}

	@Override
	public Coordinates getPickLocation(ConveyorEaton conveyorEaton,
			SimpleWorkArea workArea, RoundDimensions dimensions,
			ClampingManner clampType, ApproachType approachType) {
		throw new IllegalArgumentException("Round pieces on the conveyor are not supported by the current software version");
	}

	@Override
	public Coordinates getLocation(Conveyor conveyor, SimpleWorkArea workArea,
			Type type, ClampingManner clampType) {
		throw new IllegalArgumentException("Round pieces on the conveyor are not supported by the current software version");
	}

	@Override
	public Coordinates getLocation(ConveyorEaton conveyorEaton,
			SimpleWorkArea workArea, Type type, ClampingManner clampType) {
		throw new IllegalArgumentException("Round pieces on the conveyor are not supported by the current software version");
	}

}
