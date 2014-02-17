package eu.robojob.millassist.external.device.stacking.conveyor.eaton;

import eu.robojob.millassist.external.device.stacking.IncorrectWorkPieceDataException;
import eu.robojob.millassist.external.device.stacking.StackingPosition;
import eu.robojob.millassist.positioning.Coordinates;

public class ConveyorLayout {

	private Conveyor parent;
	private float minWorkPieceWidth;
	private float maxWorkPieceWidth;
	private float trackWidth;
	private float supportWidth;
	private float xPosSensor1;
	private float xPosSensor2;
	private float sideWidth;
	
	private StackingPosition stackingPositionA;
	private StackingPosition stackingPositionB;
	
	public ConveyorLayout(final float minWorkPieceWidth, final float maxWorkPieceWidth, final float trackWidth,
			final float supportWidth, final float xPosSensor1, final float xPosSensor2, final float sideWidth) {
		this.minWorkPieceWidth = minWorkPieceWidth;
		this.maxWorkPieceWidth = maxWorkPieceWidth;
		this.trackWidth = trackWidth;
		this.supportWidth = supportWidth;
		this.xPosSensor1 = xPosSensor1;
		this.xPosSensor2 = xPosSensor2;
		this.sideWidth = sideWidth;
		this.stackingPositionA = null;
		this.stackingPositionB = null;
	}
	
	public float getGap() {
		return 80;
	}
	
	public float getXSupportStart() {
		return 59.5f;
	}
	
	public float getMinWorkPieceWidth() {
		return minWorkPieceWidth;
	}

	public void setMinWorkPieceWidth(final float minWorkPieceWidth) {
		this.minWorkPieceWidth = minWorkPieceWidth;
	}

	public float getMaxWorkPieceWidth() {
		return maxWorkPieceWidth;
	}

	public void setMaxWorkPieceWidth(final float maxWorkPieceWidth) {
		this.maxWorkPieceWidth = maxWorkPieceWidth;
	}

	public float getTrackWidth() {
		return trackWidth;
	}

	public void setTrackWidth(final float trackWidth) {
		this.trackWidth = trackWidth;
	}

	public float getSupportWidth() {
		return supportWidth;
	}

	public void setSupportWidth(final float supportWidth) {
		this.supportWidth = supportWidth;
	}

	public float getxPosSensor1() {
		return xPosSensor1;
	}

	public void setxPosSensor1(final float xPosSensor1) {
		this.xPosSensor1 = xPosSensor1;
	}

	public float getxPosSensor2() {
		return xPosSensor2;
	}

	public void setxPosSensor2(final float xPosSensor2) {
		this.xPosSensor2 = xPosSensor2;
	}

	public void setStackingPositionTrackA(final StackingPosition stackingPositionA) {
		this.stackingPositionA = stackingPositionA;
	}

	public void setStackingPositionTrackB(final StackingPosition stackingPositionB) {
		this.stackingPositionB = stackingPositionB;
	}

	public void configureRawWorkPieceStackingPositions() throws IncorrectWorkPieceDataException {
		Coordinates coordinates = new Coordinates();
		coordinates.setX(parent.getRawWorkPiece().getDimensions().getLength()/2 + xPosSensor1);
		Coordinates coordinatesA = new Coordinates(coordinates);
		coordinatesA.offset(parent.getWorkAreaA().getActiveClamping().getRelativePosition());
		Coordinates coordinatesB = new Coordinates(coordinates);
		coordinatesB.offset(parent.getWorkAreaB().getActiveClamping().getRelativePosition());
		stackingPositionA = new StackingPosition(coordinatesA,parent.getRawWorkPiece());
		if (parent.isTrackBModeLoad()) {
			stackingPositionB = new StackingPosition(coordinatesB, parent.getRawWorkPiece());
		}
		configureFinishedWorkPieceStackingPositions();
	}
	
	public void configureFinishedWorkPieceStackingPositions() throws IncorrectWorkPieceDataException {
		if (!parent.isTrackBModeLoad()) {
			Coordinates coordinates = new Coordinates();
			coordinates.setX(parent.getFinishedWorkPiece().getDimensions().getLength()/2 + xPosSensor1);
			Coordinates coordinatesB = new Coordinates(coordinates);
			coordinatesB.offset(parent.getWorkAreaB().getActiveClamping().getRelativePosition());
			stackingPositionB = new StackingPosition(coordinatesB, parent.getFinishedWorkPiece());
		}
	}
	
	public void clearSettings() {
		stackingPositionA = null;
		stackingPositionB = null;
	}
	
	public void shiftFinishedWorkPieces() {
		
		parent.notifyFinishedShifted();
	}
	
	public StackingPosition getStackingPositionTrackA() {
		return stackingPositionA;
	}
	
	public StackingPosition getStackingPositionTrackB() {
		return stackingPositionB;
	}

	public float getSideWidth() {
		return sideWidth;
	}

	public void setSideWidth(final float sideWidth) {
		this.sideWidth = sideWidth;
	}

	public Conveyor getParent() {
		return parent;
	}

	public void setParent(final Conveyor parent) {
		this.parent = parent;
	}

}
