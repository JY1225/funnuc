package eu.robojob.millassist.external.device.stacking.stackplate.gridplate;

import eu.robojob.millassist.external.device.stacking.IncorrectWorkPieceDataException;
import eu.robojob.millassist.external.device.stacking.stackplate.AbstractStackPlateLayout;
import eu.robojob.millassist.external.device.stacking.stackplate.StackPlateStackingPosition;
import eu.robojob.millassist.external.device.stacking.stackplate.AbstractStackPlate.WorkPieceOrientation;
import eu.robojob.millassist.positioning.Coordinates;
import eu.robojob.millassist.workpiece.WorkPieceDimensions;

public class GridPlateLayout extends AbstractStackPlateLayout {

	private float height;
	//Dimensions of the gridplate
	private float length;
	private float width;
	//Dimensions of a hole in the gridplate
	private float holeLength;
	private float holeWidth;
	//Offset positions going from one hole to the next one
	private float horizontalOffsetNxtPiece;
	private float verticalOffsetNxtPiece;
	//Positions first hole
	private float firstX;
	private float firstY;
	//Name
	private String name;
	//Holeorientation
	private HoleOrientation holeOrientation;
	//Smooth points
	private Coordinates smoothTo;
	private Coordinates smoothFrom;
	//ID
	private int ID;
	
	public enum HoleOrientation {
		
		HORIZONTAL {
			@Override
			public int getId() {
				return 0;
			}

			@Override
			public int getDegrees() {
				return 0;
			}
		}, TILTED {
			@Override
			public int getId() {
				return 1;
			}

			@Override
			public int getDegrees() {
				return 45;
			}
		};
		
		public abstract int getId();
		
		public abstract int getDegrees();
	}
	
	public GridPlateLayout(String name, float length, float width, float height, float firstX, float firstY, float holeLength,
			float holeWidth, float horizontalOffsetNxtPiece, float verticalOffsetNxtPiece, 
			int horizontalAmount, int verticalAmount, float horizontalPadding, float verticalPaddingTop, 
			float verticalPaddingBottom,float tiltedR, float horizontalR, int holeOrientation) {
		super(horizontalPadding, verticalPaddingTop, verticalPaddingBottom, tiltedR, horizontalR);
		this.name = name;
		this.width = width;
		this.length = length;
		this.height = height;
		this.holeLength = holeLength;
		this.holeWidth = holeWidth;
		this.firstX = firstX;
		this.firstY = firstY;
		this.horizontalOffsetNxtPiece = horizontalOffsetNxtPiece;
		this.verticalOffsetNxtPiece = verticalOffsetNxtPiece;
		this.holeOrientation = HoleOrientation.values()[holeOrientation];
		this.smoothFrom = new Coordinates();
		this.smoothTo = new Coordinates();
		setHorizontalAmount(horizontalAmount);
		setVerticalAmount(verticalAmount);
		calcPlateWidth();
		calcPlateLength();
	}	
	
	@Override
	protected void calcPlateWidth() {
		setPlateWidth(getVerticalPaddingTop() + getVerticalPaddingBottom() + width);
	}
	
	@Override
	protected void calcPlateLength() {
		setPlateLength(getHorizontalPadding() * 2 + length);
	}

	@Override
	protected void checkSpecialStackingConditions(WorkPieceDimensions dimensions, WorkPieceOrientation orientation) throws IncorrectWorkPieceDataException {
		if(orientation == WorkPieceOrientation.TILTED && holeOrientation != HoleOrientation.TILTED) {
			throw new IncorrectWorkPieceDataException(IncorrectWorkPieceDataException.GRID_WRONG_ORIENTATION);
		}
		if(orientation != WorkPieceOrientation.TILTED && holeOrientation == HoleOrientation.TILTED) {
			throw new IncorrectWorkPieceDataException(IncorrectWorkPieceDataException.GRID_WRONG_ORIENTATION);
		}
		if(orientation == WorkPieceOrientation.DEG90) {
			if(dimensions.getWidth() > holeLength) 
				throw new IncorrectWorkPieceDataException(IncorrectWorkPieceDataException.GRID_WRONG_DIMENSIONS);
			if(dimensions.getLength() > holeWidth) 
				throw new IncorrectWorkPieceDataException(IncorrectWorkPieceDataException.GRID_WRONG_DIMENSIONS);			
		} else {
			if(dimensions.getLength() > holeLength)
				throw new IncorrectWorkPieceDataException(IncorrectWorkPieceDataException.GRID_WRONG_DIMENSIONS);	
			if(dimensions.getWidth() > holeWidth)
				throw new IncorrectWorkPieceDataException(IncorrectWorkPieceDataException.GRID_WRONG_DIMENSIONS);	
		}
	}
	
	@Override
	protected int getMaxHorizontalAmount(WorkPieceDimensions dimensions, WorkPieceOrientation orientation) {
		return getHorizontalAmount();
	}

	@Override
	protected int getMaxVerticalAmount(WorkPieceDimensions dimensions, WorkPieceOrientation orientation) {
		return getVerticalAmount();
	}
	
	public float getHeight() {
		return this.height;
	}
	
	public void setHeight(float height) {
		this.height = height;
	}
	
	public float getLength() {
		return this.length;
	}
	
	public void setLength(float length) {
		this.length = length;
	}
	
	public float getWidth() {
		return this.width;
	}
	
	public void setWidth(float width) {
		this.width = width;
	}
	
	public float getHoleLength() {
		return this.holeLength;
	}
	
	public void setHoleLength(float holeLength) {
		this.holeLength = holeLength;
	}
	
	public float getHoleWidth() {
		return this.holeWidth;
	}
	
	public void setHoleWidth(float holeWidth) {
		this.holeWidth = holeWidth;
	}
	
	public float getHorizontalOffsetNxtPiece() {
		return this.horizontalOffsetNxtPiece;
	}
	
	public void setHorizontalOffsetNxtPiece(float horizontalOffsetNxtPiece) {
		this.horizontalOffsetNxtPiece = horizontalOffsetNxtPiece;
	}

	public float getVerticalOffsetNxtPiece() {
		return this.verticalOffsetNxtPiece;
	}
	
	public void setVerticalOffsetNxtPiece(float verticalOffsetNxtPiece) {
		this.verticalOffsetNxtPiece = verticalOffsetNxtPiece;
	}
	
	public float getFirstHolePosX() {
		return this.firstX;
	}
	
	public void setFirstHolePosX(float xPos) {
		this.firstX = xPos;
	}
	
	public float getFirstHolePosY() {
		return this.firstY;
	}
	
	public void setFirstHolePosY(float yPos) {
		this.firstY = yPos;
	}
	
	public HoleOrientation getHoleOrientation() {
		return this.holeOrientation;
	}
	
	public void setHoleOrientation(int orientation) {
		this.holeOrientation = HoleOrientation.values()[orientation];
	}
	
	public void setSmoothTo(Coordinates smoothTo) {
		this.smoothTo = smoothTo;
	}
	
	public void setSmoothFrom(Coordinates smoothFrom) {
		this.smoothFrom = smoothFrom;
	}
	
	public Coordinates getSmoothTo() {
		return this.smoothTo;
	}
	
	public Coordinates getSmoothFrom() {
		return smoothFrom;
	}
	
	public void setId(int Id) {
		this.ID = Id;
	}
	
	public int getId() {
		return this.ID;
	}
	
	@Override
	protected void initStackingPositions(int nbHorizontal, int nbVertical, WorkPieceDimensions dimensions, WorkPieceOrientation orientation) {
		switch(orientation) {
		case HORIZONTAL:
			initStackingPositionsHorizontal(nbHorizontal, nbVertical, dimensions, orientation);
			break;
		case DEG90:
			if(isRightAligned()) {
				initStackingPositionsDeg90Right(nbHorizontal, nbVertical, dimensions, orientation);
			} else {
				initStackingPositionsDeg90(nbHorizontal, nbVertical, dimensions, orientation);
			}
			break;
		case TILTED:
			initStackingPositionsTilted(nbHorizontal, nbVertical, dimensions, orientation);
			break;
		}
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	private void initStackingPositionsHorizontal(int nbHorizontal, int nbVertical, WorkPieceDimensions dimensions, WorkPieceOrientation orientation) {
		for(int i = 0; i < nbVertical; i++) {
			for(int j = 0; j < nbHorizontal; j++) {
				double xBottomLeft = getHorizontalPadding() + j * horizontalOffsetNxtPiece + firstX;
				double yBottomLeft = getVerticalPaddingBottom() + i * verticalOffsetNxtPiece + firstY;
				float x = (float) xBottomLeft + dimensions.getLength()/2;
				float y = (float) yBottomLeft + dimensions.getWidth()/2;
				StackPlateStackingPosition stPos = new StackPlateStackingPosition(x, y, getR(orientation), null, 0, orientation);
				getStackingPositions().add(stPos);
			}
		}
	}
	
	private void initStackingPositionsDeg90(int nbHorizontal, int nbVertical, WorkPieceDimensions dimensions, WorkPieceOrientation orientation) {
		for(int i = 0; i < nbVertical; i++) {
			for(int j = 0; j < nbHorizontal; j++) {
				double xBottomLeft = getHorizontalPadding() + j * horizontalOffsetNxtPiece + firstX;
				double yBottomLeft = getVerticalPaddingBottom() + i * verticalOffsetNxtPiece + firstY;
				float x = (float) xBottomLeft + dimensions.getWidth()/2;
				float y = (float) yBottomLeft + dimensions.getLength()/2;
				StackPlateStackingPosition stPos = new StackPlateStackingPosition(x, y, getR(orientation), null, 0, orientation);
				getStackingPositions().add(stPos);
			}
		}
	}
	
	private void initStackingPositionsDeg90Right(int nbHorizontal, int nbVertical, WorkPieceDimensions dimensions, WorkPieceOrientation orientation) {
		for(int i = 0; i < nbVertical; i++) {
			for(int j = 0; j < nbHorizontal; j++) {
				double xBottomLeft = getHorizontalPadding() + j * horizontalOffsetNxtPiece + holeLength + firstX;
				double yBottomLeft = getVerticalPaddingBottom() + i * verticalOffsetNxtPiece + firstY;
				float x = (float) xBottomLeft - dimensions.getWidth()/2;
				float y = (float) yBottomLeft + dimensions.getLength()/2;
				StackPlateStackingPosition stPos = new StackPlateStackingPosition(x, y, getR(orientation), null, 0, orientation);
				getStackingPositions().add(stPos);
			}
		}
	}
	
	private void initStackingPositionsTilted(int nbHorizontal, int nbVertical, WorkPieceDimensions dimensions, WorkPieceOrientation orientation) {
		for (int i = 0; i < nbVertical; i++) {
			for (int j = 0; j < nbHorizontal; j++) {
				double xBottom = getHorizontalPadding() + j * horizontalOffsetNxtPiece + firstX;
				double yBottom = getVerticalPaddingBottom()  + i * verticalOffsetNxtPiece + firstY;
				double extraX = (dimensions.getLength()/Math.sqrt(2) - dimensions.getWidth()/Math.sqrt(2))/2;
				double extraY = (dimensions.getLength()/Math.sqrt(2) + dimensions.getWidth()/Math.sqrt(2))/2;
				float x = (float) (xBottom + extraX);
				float y = (float) (yBottom + extraY);
				StackPlateStackingPosition stPos = new StackPlateStackingPosition(x, y, getR(getOrientation()), null, 0, WorkPieceOrientation.TILTED);
				getStackingPositions().add(stPos);
			}
		}
	}

}
