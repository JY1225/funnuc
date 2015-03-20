package eu.robojob.millassist.external.device.stacking.stackplate.strategy.gridPlate;

import eu.robojob.millassist.external.device.stacking.IncorrectWorkPieceDataException;
import eu.robojob.millassist.external.device.stacking.stackplate.StackPlateStackingPosition;
import eu.robojob.millassist.external.device.stacking.stackplate.gridplate.GridHole;
import eu.robojob.millassist.external.device.stacking.stackplate.gridplate.GridPlateLayout;
import eu.robojob.millassist.workpiece.RoundDimensions;

public class RoundGridPlateStrategy extends AGridPlateStrategy<RoundDimensions> {

	public RoundGridPlateStrategy(GridPlateLayout context) {
		super(context);
	}

	@Override
	public void configureStackingPositions(RoundDimensions rawPiece, RoundDimensions finishedPiece)
			throws IncorrectWorkPieceDataException {
		isValidWorkPiece(rawPiece);
		isValidWorkPiece(finishedPiece);
		configureSinglePiece(rawPiece, true);
		configureSinglePiece(finishedPiece, false);
	}

	@Override
	public void configureOnlyRawStackingPos(RoundDimensions rawPiece)
			throws IncorrectWorkPieceDataException {	
		isValidWorkPiece(rawPiece);
		configureSinglePiece(rawPiece, true);
	}

	@Override
	public void configureSameDimensionPositions(RoundDimensions rawPiece)
			throws IncorrectWorkPieceDataException {
		isValidWorkPiece(rawPiece);
		configureSinglePiece(rawPiece, true);
		getContext().getFinishedStackingPositions().addAll(getContext().getRawStackingPositions());
	}

	private void configureSinglePiece(RoundDimensions dimensions, boolean isRaw) {
		for (GridHole hole: getContext().getGridPlate().getGridHoles()) {
			StackPlateStackingPosition stPos;
			if (hole.getAngle() < 90) {
				float y = getYComp(dimensions, hole.getAngle());
				float x = getXComp(dimensions.getDiameter() / 2, y);
				if (hole.getAngle() > 45) {
					x *= -1;
				}
				stPos = new StackPlateStackingPosition(hole.getX() + x, hole.getY() + y, 
						getContext().getStackPlate().getRRound(), null, 0, hole.getAngle());
			} else if (hole.getAngle() > 90) {			
				float angle = hole.getAngle();
				float y = getYComp(dimensions, angle);
				float x = getXComp(dimensions.getDiameter() / 2, y);
				float xLeftUnder = (float) (getContext().getGridPlate().getHoleWidth() * Math.sin(angle * Math.PI/180));
				float yLeftUnder = (float) (getContext().getGridPlate().getHoleWidth() * Math.cos(angle * Math.PI/180));
				stPos = new StackPlateStackingPosition(hole.getX() + y - xLeftUnder, hole.getY() + x + yLeftUnder, 
						getContext().getStackPlate().getRRound(), null, 0, hole.getAngle());
			} else {
				float y = dimensions.getDiameter() / 2;
				float x = dimensions.getDiameter() / 2;
				stPos = new StackPlateStackingPosition(hole.getX() + x - getContext().getGridPlate().getHoleWidth(), hole.getY() + y, 
						getContext().getStackPlate().getRRound(), null, 0, hole.getAngle());
			}
			if (isRaw) {
				getContext().getRawStackingPositions().add(stPos);
			} else {
				getContext().getFinishedStackingPositions().add(stPos);
			}
		}
	}
	
	private static float getYComp(RoundDimensions dimensions, float angle) {
		double alpha = angle * Math.PI / 180;
		double B = (dimensions.getDiameter() / 2) * Math.tan(alpha) + dimensions.getDiameter() / 2;
		return (float) (B * Math.sin(Math.PI / 2 - alpha));
	}
	
	private static float getXComp(float radius, float yComp) {
		float C = (float) Math.sqrt(2) * radius;
		return (float) Math.sqrt(C * C - (yComp * yComp));
	}

	@Override
	protected void isValidWorkPiece(RoundDimensions dimensions) throws IncorrectWorkPieceDataException {
		if (dimensions == null || !dimensions.isValidDimension()) {
			throw new IncorrectWorkPieceDataException(IncorrectWorkPieceDataException.INCORRECT_DATA);
		}
	}

}
