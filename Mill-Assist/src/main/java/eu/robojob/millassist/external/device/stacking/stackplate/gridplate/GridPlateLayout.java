package eu.robojob.millassist.external.device.stacking.stackplate.gridplate;

import eu.robojob.millassist.external.device.stacking.IncorrectWorkPieceDataException;
import eu.robojob.millassist.external.device.stacking.stackplate.AbstractStackPlateLayout;
import eu.robojob.millassist.external.device.stacking.stackplate.StackPlateStackingPosition;
import eu.robojob.millassist.workpiece.WorkPieceDimensions;

public class GridPlateLayout extends AbstractStackPlateLayout {

	private GridPlate gridPlate;
	
	public GridPlateLayout(GridPlate gridPlate) {
		super();
		this.gridPlate = gridPlate;
		calcPlateWidth();
		calcPlateLength();
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
	protected int getMaxHorizontalAmount(WorkPieceDimensions dimensions, float orientation) {
		return getHorizontalAmount();
	}

	@Override
	protected int getMaxVerticalAmount(WorkPieceDimensions dimensions, float orientation) {
		return getVerticalAmount();
	}
	
	@Override
	protected void initStackingPositions(int nbHorizontal, int nbVertical, WorkPieceDimensions dimensions, float orientation) {
		for (GridHole hole: gridPlate.getGridHoles()) {
			if (hole.getAngle() < 90) {
				double tangens = (double) dimensions.getWidth() / (double) dimensions.getLength();
				double alpha = Math.atan(tangens);
				double side = dimensions.getWidth()/ (2 * Math.sin(alpha));
				double theta = hole.getAngle() * Math.PI / 180;
				float extraX = (float) (side * Math.cos(alpha + theta));
				float extraY = (float) (side * Math.sin(alpha + theta));
				StackPlateStackingPosition stPos = new StackPlateStackingPosition(hole.getX() + extraX, hole.getY() + extraY, hole.getAngle(), null, 0, hole.getAngle());
				getStackingPositions().add(stPos);
			} else {
				double tangens = (double) dimensions.getLength()/2 / ((double) gridPlate.getHoleWidth() - dimensions.getWidth()/2);
				double alpha = Math.atan(tangens);
				double side = ((double) gridPlate.getHoleWidth() - dimensions.getWidth()/2)/ (Math.cos(alpha));
				double theta = hole.getAngle() * Math.PI / 180;
				theta = Math.PI/2 - (Math.PI/2 - (theta - Math.PI/2) + alpha);
				float extraX = Math.abs((float) (side * Math.cos(theta)));
				float extraY = Math.abs((float) (side * Math.sin(theta)));
				StackPlateStackingPosition stPos = new StackPlateStackingPosition(hole.getX() - extraX, hole.getY() + extraY, hole.getAngle() - 180, null, 0, hole.getAngle());
				getStackingPositions().add(stPos);
			}
		}	
	}

	@Override
	protected void checkSpecialStackingConditions(
			WorkPieceDimensions dimensions, float orientation)
			throws IncorrectWorkPieceDataException {
		// TODO Auto-generated method stub
		
	}
	
	public GridPlate getGridPlate() {
		return this.gridPlate;
	}
	
	public void setGridPlate(GridPlate gridPlate) {
		this.gridPlate = gridPlate;
	}
}
