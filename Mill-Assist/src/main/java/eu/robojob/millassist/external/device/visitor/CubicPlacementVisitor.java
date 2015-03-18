package eu.robojob.millassist.external.device.visitor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.robojob.millassist.external.device.ClampingManner;
import eu.robojob.millassist.external.device.SimpleWorkArea;
import eu.robojob.millassist.external.device.ClampingManner.Type;
import eu.robojob.millassist.external.device.processing.cnc.AbstractCNCMachine;
import eu.robojob.millassist.external.device.processing.prage.PrageDevice;
import eu.robojob.millassist.external.device.processing.reversal.ReversalUnit;
import eu.robojob.millassist.external.device.stacking.StackingPosition;
import eu.robojob.millassist.external.device.stacking.conveyor.normal.Conveyor;
import eu.robojob.millassist.external.robot.AbstractRobotActionSettings.ApproachType;
import eu.robojob.millassist.positioning.Coordinates;
import eu.robojob.millassist.workpiece.RectangularDimensions;
import eu.robojob.millassist.workpiece.WorkPiece.Dimensions;

public final class CubicPlacementVisitor extends AbstractPiecePlacementVisitor<RectangularDimensions> {
	
	private static Logger logger = LogManager.getLogger(CubicPlacementVisitor.class.getName());

	@Override
	public Coordinates getPutLocation(PrageDevice device,
			SimpleWorkArea workArea, RectangularDimensions dimensions,
			ClampingManner clampType, ApproachType approachType) {
		Coordinates c = new Coordinates(workArea.getDefaultClamping().getRelativePosition());
		if (clampType.getType() == Type.LENGTH) {
			if (clampType.isChanged()) {
				c.setR(c.getR() + device.getClampingWidthDeltaR());
			} else {
				c.setR(c.getR());
			}
			switch (workArea.getDefaultClamping().getType()) {
				case CENTRUM:
					// no action needed
					break;
				case FIXED_XM:
					c.setX(c.getX() - dimensions.getWidth()/2);
					break;
				case FIXED_YM:
					c.setY(c.getY() - dimensions.getWidth()/2);
					break;
				case FIXED_XP:
					c.setX(c.getX() + dimensions.getWidth()/2);
					break;
				case FIXED_YP:
					c.setY(c.getY() + dimensions.getWidth()/2);
					break;
				case NONE:
					throw new IllegalArgumentException("Machine clamping type can't be NONE.");
				default:
					throw new IllegalArgumentException("Unknown clamping type: " + workArea.getDefaultClamping().getType());
			}
		} else {
			if (clampType.isChanged()) {
				c.setR(c.getR());
			} else {
				c.setR(c.getR() + device.getClampingWidthDeltaR());
			}
			switch (workArea.getDefaultClamping().getType()) {
			case CENTRUM:
				// no action needed
				break;
			case FIXED_XM:
				c.setX(c.getX() - dimensions.getLength()/2);
				break;
			case FIXED_YM:
				c.setY(c.getY() - dimensions.getLength()/2);
				break;
			case FIXED_XP:
				c.setX(c.getX() + dimensions.getLength()/2);
				break;
			case FIXED_YP:
				c.setY(c.getY() + dimensions.getLength()/2);
				break;
			case NONE:
				throw new IllegalArgumentException("Machine clamping type can't be NONE.");
			default:
				throw new IllegalArgumentException("Unknown clamping type: " + workArea.getDefaultClamping().getType());
			}
		}
		return c;
	}

	@Override
	public Coordinates getPutLocation(ReversalUnit reversal, SimpleWorkArea workArea, RectangularDimensions dimensions,
			ClampingManner clampType, ApproachType approachType) {
		Coordinates c = new Coordinates(workArea.getDefaultClamping().getRelativePosition());
		if (reversal.isReversalWidth()) {
			dimensions = new RectangularDimensions(dimensions.getWidth(), dimensions.getLength(), dimensions.getHeight());
		}
		c.setX(c.getX() + getXCoord(dimensions, approachType) + reversal.getAddedXOrigin());
		c.setY(c.getY() + getYCoord(dimensions, approachType));
		c.setZ(c.getZ() + getZCoord(dimensions, approachType));
		return c;
	}
	
	private static float getXCoord(final RectangularDimensions workPieceDimensions, final ApproachType approachType) {
		switch (approachType) {
		case BOTTOM:
		case TOP:
		case LEFT:
			return workPieceDimensions.getWidth()/2;
		case FRONT:
			return workPieceDimensions.getHeight();
		default: 
			return 0;
		}
	}

	private static float getYCoord(final RectangularDimensions workPieceDimensions, final ApproachType approachType) {
		switch (approachType) {
		case BOTTOM:
		case TOP:
		case FRONT:
			return workPieceDimensions.getLength()/2;
		case LEFT:
			return 0;
		default:
			return 0;
		}
	}
	
	private static float getZCoord(final RectangularDimensions workPieceDimensions, final ApproachType approachType) {
		switch (approachType) {
		case BOTTOM:
		case TOP:	
			return 0;
		case FRONT:
			return workPieceDimensions.getWidth()/2;
		case LEFT:
			return workPieceDimensions.getLength()/2;
		default:
			return 0;
		}
	}
	
	@Override
	public Coordinates getPickLocation(ReversalUnit reversal, SimpleWorkArea workArea, RectangularDimensions dimensions,
			ClampingManner clampType, ApproachType approachType) {
		return getPutLocation(reversal, workArea, dimensions, clampType, approachType);
	}

	@Override
	public Coordinates getPutLocation(AbstractCNCMachine cncMachine,
			SimpleWorkArea workArea, RectangularDimensions dimensions,
			ClampingManner clampType, ApproachType approachType) {
		Coordinates c = new Coordinates(workArea.getWorkAreaManager().getActiveClamping(false, workArea.getSequenceNb()).getRelativePosition());
		if (clampType.getType() == Type.LENGTH) {
			if (clampType.isChanged()) {
				c.setR(c.getR() + cncMachine.getClampingWidthR());
			} else {
				c.setR(c.getR());
			}
			switch (workArea.getWorkAreaManager().getActiveClamping(false, workArea.getSequenceNb()).getType()) {
				case CENTRUM:
					// no action needed
					break;
				case FIXED_XM:
					c.setX(c.getX() - dimensions.getWidth()/2);
					break;
				case FIXED_YM:
					c.setY(c.getY() - dimensions.getWidth()/2);
					break;
				case FIXED_XP:
					c.setX(c.getX() + dimensions.getWidth()/2);
					break;
				case FIXED_YP:
					c.setY(c.getY() + dimensions.getWidth()/2);
					break;
				case NONE:
					throw new IllegalArgumentException("Machine clamping type can't be NONE.");
				default:
					throw new IllegalArgumentException("Unknown clamping type: " + workArea.getWorkAreaManager().getActiveClamping(false, workArea.getSequenceNb()).getType());
			}
		} else {
			if (clampType.isChanged()) {
				c.setR(c.getR());
			} else {
				c.setR(c.getR() + cncMachine.getClampingWidthR());
			}
			switch (workArea.getWorkAreaManager().getActiveClamping(false, workArea.getSequenceNb()).getType()) {
			case CENTRUM:
				// no action needed
				break;
			case FIXED_XM:
				c.setX(c.getX() - dimensions.getLength()/2);
				break;
			case FIXED_YM:
				c.setY(c.getY() - dimensions.getLength()/2);
				break;
			case FIXED_XP:
				c.setX(c.getX() + dimensions.getLength()/2);
				break;
			case FIXED_YP:
				c.setY(c.getY() + dimensions.getLength()/2);
				break;
			case NONE:
				throw new IllegalArgumentException("Machine clamping type can't be NONE.");
			default:
				throw new IllegalArgumentException("Unknown clamping type: " + workArea.getWorkAreaManager().getActiveClamping(false,workArea.getSequenceNb()).getType());
			}
		}
		return c;
	}

	@Override
	public Coordinates getPickLocation(AbstractCNCMachine cncMachine, SimpleWorkArea workArea, RectangularDimensions dimensions,
			ClampingManner clampType, ApproachType approachType) {
		Coordinates c = new Coordinates(workArea.getWorkAreaManager().getActiveClamping(true, workArea.getSequenceNb()).getRelativePosition());
		if (clampType.getType() == Type.LENGTH) {
			if (clampType.isChanged()) {
				c.setR(c.getR() + cncMachine.getClampingWidthR());
			} else {
				c.setR(c.getR());
			}
			switch (workArea.getWorkAreaManager().getActiveClamping(true,workArea.getSequenceNb()).getType()) {
				case CENTRUM:
					// no action needed
					break;
				case FIXED_XM:
					c.setX(c.getX() - dimensions.getWidth()/2);
					break;
				case FIXED_YM:
					c.setY(c.getY() - dimensions.getWidth()/2);
					break;
				case FIXED_XP:
					c.setX(c.getX() + dimensions.getWidth()/2);
					break;
				case FIXED_YP:
					c.setY(c.getY() + dimensions.getWidth()/2);
					break;
				case NONE:
					throw new IllegalArgumentException("Machine clamping type can't be NONE.");
				default:
					throw new IllegalArgumentException("Unknown clamping type: " + workArea.getWorkAreaManager().getActiveClamping(true, workArea.getSequenceNb()).getType());
			}
		} else {
			if (clampType.isChanged()) {
				c.setR(c.getR());
			} else {
				c.setR(c.getR() + cncMachine.getClampingWidthR());
			}
			switch (workArea.getWorkAreaManager().getActiveClamping(true, workArea.getSequenceNb()).getType()) {
			case CENTRUM:
				// no action needed
				break;
			case FIXED_XM:
				c.setX(c.getX() - dimensions.getLength()/2);
				break;
			case FIXED_YM:
				c.setY(c.getY() - dimensions.getLength()/2);
				break;
			case FIXED_XP:
				c.setX(c.getX() + dimensions.getLength()/2);
				break;
			case FIXED_YP:
				c.setY(c.getY() + dimensions.getLength()/2);
				break;
			case NONE:
				throw new IllegalArgumentException("Machine clamping type can't be NONE.");
			default:
				throw new IllegalArgumentException("Unknown clamping type: " + workArea.getWorkAreaManager().getActiveClamping(true, workArea.getSequenceNb()).getType());
			}
		}
		return c;
	}

	@Override
	public Coordinates getPutLocation(Conveyor conveyor, SimpleWorkArea workArea, RectangularDimensions dimensions,
			ClampingManner clampType, ApproachType approachType) {
		return conveyor.getLayout().getStackingPositionsFinishedWorkPieces().get(conveyor.getLastFinishedWorkPieceIndex()).getPosition();
	}

	@Override
	public Coordinates getPutLocation(
			eu.robojob.millassist.external.device.stacking.conveyor.eaton.ConveyorEaton conveyor, SimpleWorkArea workArea, RectangularDimensions dimensions,
			ClampingManner clampType, ApproachType approachType) {
		if (workArea.getWorkAreaManager().equals(conveyor.getWorkAreaB())) {
			if (!conveyor.isTrackBModeLoad()) {
				StackingPosition stPos = conveyor.getLayout().getStackingPositionTrackB();
				return stPos.getPosition();
			} else {
				throw new IllegalArgumentException("Track B mode is load...");
			}
		} else {
			throw new IllegalArgumentException("Illegal workarea: " + workArea);
		}
	}

	@Override
	public Coordinates getPickLocation(Conveyor conveyor,
			SimpleWorkArea workArea, RectangularDimensions dimensions,
			ClampingManner clampType, ApproachType approachType) {
		if (!workArea.getWorkAreaManager().equals(conveyor.getRawWorkArea())) {
			throw new IllegalStateException("Can only pick from raw conveyor");
		}
		//FIXME: review if this is still ok
		/*// wait until workpiece in position
		logger.info("Waiting for raw work piece in position status.");
		waitForStatus((ConveyorConstants.RAW_WP_IN_POSITION));
		logger.info("Work piece in position.");*/
		// get lowest non-zero sensor value
		int sensorValue = Integer.MAX_VALUE;
		int sensorIndex = -1;
		int validIndex = 0;
		for (int i = 0; i < conveyor.getSensorValues().size(); i++) {
			if (conveyor.getLayout().getRequestedSupportStatus()[i]) {
				logger.info("OK: " + i);
				if ((conveyor.getSensorValues().get(i) < sensorValue) && (conveyor.getSensorValues().get(i) > 0)) {
					sensorIndex = validIndex;
					sensorValue = conveyor.getSensorValues().get(i);
				}
				// also check next sensors if their supports are down
				int j = 1;
				while ((i+j) < conveyor.getLayout().getRequestedSupportStatus().length && !conveyor.getLayout().getRequestedSupportStatus()[i+j]) {
					if ((conveyor.getSensorValues().get(j+i) < sensorValue) && (conveyor.getSensorValues().get(j+i) > 0)) {
						sensorIndex = validIndex;
						sensorValue = conveyor.getSensorValues().get(i+j);
					}
					j++;
				}
				validIndex++;
			}
		}
		if (sensorIndex == -1) {
			throw new IllegalStateException("Couldn't find a stacking position.");
		}
		StackingPosition stPos = conveyor.getLayout().getStackingPositionsRawWorkPieces().get(sensorIndex);
		Coordinates c = new Coordinates(stPos.getPosition());
		if (conveyor.isLeftSetup()) {
			c.setX((((float) sensorValue)/100) + stPos.getWorkPiece().getDimensions().getDimension(Dimensions.LENGTH)/2);
		} else {
			c.setY((((float) sensorValue)/100) + stPos.getWorkPiece().getDimensions().getDimension(Dimensions.LENGTH)/2);
		}
		logger.info("Pick location at sensor: " + sensorIndex + " - coordinates: " + c);
		return c;
	}

	@Override
	public Coordinates getPickLocation(
			eu.robojob.millassist.external.device.stacking.conveyor.eaton.ConveyorEaton conveyor, SimpleWorkArea workArea, RectangularDimensions dimensions,
			ClampingManner clampType, ApproachType approachType) {
		if (workArea.getWorkAreaManager().equals(conveyor.getWorkAreaA())) {
			StackingPosition stPos = conveyor.getLayout().getStackingPositionTrackA();
			return stPos.getPosition();
		} else if (workArea.getWorkAreaManager().equals(conveyor.getWorkAreaB())) {
			if (conveyor.isTrackBModeLoad()) {
				StackingPosition stPos = conveyor.getLayout().getStackingPositionTrackB();
				return stPos.getPosition();
			} else {
				throw new IllegalArgumentException("Track B mode is unload...");
			}
		} else {
			throw new IllegalArgumentException("Illegal workarea: " + workArea);
		}
	}

	@Override
	public Coordinates getLocation(Conveyor conveyor, SimpleWorkArea workArea,
			eu.robojob.millassist.workpiece.WorkPiece.Type type,
			ClampingManner clampType) {
		if (type.equals(eu.robojob.millassist.workpiece.WorkPiece.Type.FINISHED)) {
			return conveyor.getLayout().getStackingPositionsFinishedWorkPieces().get(conveyor.getLastFinishedWorkPieceIndex()).getPosition();
		} else if (type.equals(eu.robojob.millassist.workpiece.WorkPiece.Type.RAW)) {
			return getPickLocation(conveyor, workArea, (RectangularDimensions) conveyor.getRawWorkPiece().getDimensions(), clampType, ApproachType.TOP);
		}
		return null;
	}

	@Override
	public Coordinates getLocation(
			eu.robojob.millassist.external.device.stacking.conveyor.eaton.ConveyorEaton conveyor,
			SimpleWorkArea workArea,
			eu.robojob.millassist.workpiece.WorkPiece.Type type,
			ClampingManner clampType) {
		if (type.equals(eu.robojob.millassist.workpiece.WorkPiece.Type.FINISHED)) {
			return getPutLocation(conveyor, workArea, (RectangularDimensions) conveyor.getFinishedWorkPiece().getDimensions(), clampType, ApproachType.TOP);
		} else if (type.equals(eu.robojob.millassist.workpiece.WorkPiece.Type.RAW)) {
			return getPickLocation(conveyor, workArea, (RectangularDimensions) conveyor.getRawWorkPiece().getDimensions(), clampType, ApproachType.TOP);
		}
		return null;
	}
	
}
