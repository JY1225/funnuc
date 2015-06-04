package eu.robojob.millassist.external.device.stacking.stackplate.gridplate;

import eu.robojob.millassist.external.device.stacking.IncorrectWorkPieceDataException;
import eu.robojob.millassist.external.device.stacking.stackplate.AbstractStackPlateLayout;
import eu.robojob.millassist.external.device.stacking.stackplate.StackPlateStackingPosition;
import eu.robojob.millassist.external.device.stacking.stackplate.strategy.gridPlate.RoundGridPlateStrategy;
import eu.robojob.millassist.workpiece.RectangularDimensions;
import eu.robojob.millassist.workpiece.RoundDimensions;
import eu.robojob.millassist.workpiece.WorkPiece;
import eu.robojob.millassist.workpiece.WorkPiece.WorkPieceShape;

public class GridPlateLayout extends AbstractStackPlateLayout {

	private GridPlate gridPlate;
	private RoundGridPlateStrategy roundGridStrategy;
	
	public GridPlateLayout(GridPlate gridPlate) {
		super();
		this.gridPlate = gridPlate;
		calcPlateWidth();
		calcPlateLength();
		this.roundGridStrategy = new RoundGridPlateStrategy(this);
	}	
	
	@Override
	protected void calcPlateWidth() {
		setPlateWidth((float) gridPlate.getHeight());
	}
	
	@Override
	protected void calcPlateLength() {
		setPlateLength((float) gridPlate.getWidth());
	}
	
	@Override
	protected int getMaxHorizontalAmount(RectangularDimensions dimensions, float orientation) {
		return getHorizontalAmount();
	}

	@Override
	protected int getMaxVerticalAmount(RectangularDimensions dimensions, float orientation) {
		return getVerticalAmount();
	}
	
	@Override
	protected void initStackingPositions(int nbHorizontal, int nbVertical, RectangularDimensions dimensions, float orientation) {
		for (GridHole hole: gridPlate.getGridHoles()) {
			if (hole.getAngle() < 90) {
				double tangens = (double) dimensions.getWidth() / (double) dimensions.getLength();
				double alpha = Math.atan(tangens);
				double side = dimensions.getWidth()/ (2 * Math.sin(alpha));
				double theta = hole.getAngle() * Math.PI / 180;
				float extraX = (float) (side * Math.cos(alpha + theta));
				float extraY = (float) (side * Math.sin(alpha + theta));
				StackPlateStackingPosition stPos = new StackPlateStackingPosition(hole.getX() + extraX + gridPlate.getOffsetX(), hole.getY() + extraY + gridPlate.getOffsetY(), getR(hole.getAngle()), null, 0, hole.getAngle());
				getRawStackingPositions().add(stPos);
			} else {
				double tangens = (double) dimensions.getLength()/2 / ((double) gridPlate.getHoleWidth() - dimensions.getWidth()/2);
				double alpha = Math.atan(tangens);
				double side = ((double) gridPlate.getHoleWidth() - dimensions.getWidth()/2)/ (Math.cos(alpha));
				double theta = hole.getAngle() * Math.PI / 180;
				theta = Math.PI/2 - (Math.PI/2 - (theta - Math.PI/2) + alpha);
				float extraX = Math.abs((float) (side * Math.cos(theta)));
				float extraY = (float) (side * Math.sin(theta));
				StackPlateStackingPosition stPos = new StackPlateStackingPosition(hole.getX() - extraX + gridPlate.getOffsetX(), hole.getY() - extraY + gridPlate.getOffsetY(), getR(hole.getAngle()), null, 0, hole.getAngle());
				getRawStackingPositions().add(stPos);
			}
		}	
	}

	@Override
	protected void checkSpecialStackingConditions(RectangularDimensions dimensions, float orientation)
			throws IncorrectWorkPieceDataException {
	}
	
	/**
	 * Configures the list of Stacking-positions and updates the 2D-array of studPositions 
	 * @throws IncorrectWorkPieceDataException 
	 */
	@Override
	public void configureStackingPositions(final WorkPiece rawWorkPiece, final WorkPiece finishedWorkPiece, final float orientation, final int layers) throws IncorrectWorkPieceDataException {
		getRawStackingPositions().clear();
		getFinishedStackingPositions().clear();
		getStackingPositions().clear();
		if (rawWorkPiece == null)
			return;
		if (rawWorkPiece.getShape().equals(WorkPieceShape.CYLINDRICAL)) {
			setLayers(layers);
			if (finishedWorkPiece == null) {
				getRoundStrategy().configureOnlyRawStackingPos((RoundDimensions) rawWorkPiece.getDimensions());
			} else if (finishedWorkPiece.getDimensions().hasSameDimensions(rawWorkPiece.getDimensions())) {
				getRoundStrategy().configureSameDimensionPositions((RoundDimensions) rawWorkPiece.getDimensions());
			} else {
				getRoundStrategy().configureStackingPositions((RoundDimensions) rawWorkPiece.getDimensions(), (RoundDimensions) finishedWorkPiece.getDimensions());
			}
		} else {
			super.configureStackingPositions(rawWorkPiece, finishedWorkPiece, orientation, layers);
		}
	}
	
	private RoundGridPlateStrategy getRoundStrategy() {
		return roundGridStrategy;
	}
	
	public GridPlate getGridPlate() {
		return this.gridPlate;
	}
	
	public void setGridPlate(GridPlate gridPlate) {
		this.gridPlate = gridPlate;
	}
	
	private float getR(float orientation) {
	    if(getStackPlate() != null) {
	        return getStackPlate().getR(orientation);
	    } else if(getPallet() != null) {
	        return getPallet().getR(orientation);
	    }
	    return 0;
	}
	
    public float getRRound() {
	    if(getStackPlate() != null) {
	        return getStackPlate().getRRound();
	    }else if(getPallet() != null) {
            return getPallet().getHorizontalR();
        }
	    return 0;
    }
}
