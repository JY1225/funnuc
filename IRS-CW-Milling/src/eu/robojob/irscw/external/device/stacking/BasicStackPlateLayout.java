package eu.robojob.irscw.external.device.stacking;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.robojob.irscw.external.device.stacking.BasicStackPlate.WorkPieceOrientation;
import eu.robojob.irscw.external.device.stacking.StudPosition.StudType;
import eu.robojob.irscw.workpiece.WorkPiece;
import eu.robojob.irscw.workpiece.WorkPiece.Type;
import eu.robojob.irscw.workpiece.WorkPieceDimensions;

public class BasicStackPlateLayout {
	
	// general settings
	private int horizontalHoleAmount;
	private int verticalHoleAmount;
	private float holeDiameter;
	private float studDiameter;
	private float horizontalPadding;
	private float verticalPaddingTop;
	private float verticalPaddingBottom; 
	private float horizontalHoleDistance;
	private float verticalHoleDistance;
	private float interferenceDistance;
	private float overFlowPercentage;
	private WorkPieceOrientation orientation;

	private StudPosition[][] studPositions;
	private List<StackingPosition> stackingPositions;
	
	private static final int INTERFERENCE_DISTANCE = 5;
	private static final float MIN_OVERLAP_DISTANCE = 9;
	private static final double MAX_OVERLAP_PERCENTAGE = 0.35;
	
	private static Logger logger = LogManager.getLogger(BasicStackPlateLayout.class.getName());
		
	public BasicStackPlateLayout(final int horizontalHoleAmount, final int verticalHoleAmount, final float holeDiameter, final float studDiameter, final float horizontalPadding,
			final float verticalPaddingTop, final float verticalPaddingBottom, final float horizontalHoleDistance, final float interferenceDistance, final float overflowPercentage) {
		this.horizontalHoleAmount = horizontalHoleAmount;
		this.verticalHoleAmount = verticalHoleAmount;
		this.holeDiameter = holeDiameter;
		this.studDiameter = studDiameter;
		this.horizontalPadding = horizontalPadding;
		this.verticalPaddingTop = verticalPaddingTop;
		this.verticalPaddingBottom = verticalPaddingBottom;
		this.horizontalHoleDistance = horizontalHoleDistance;
		this.verticalHoleDistance = 2 * horizontalHoleDistance;		// this is always the case with this Basic Stack Plate (so tilted layout results in a 45° angle)
		this.interferenceDistance = interferenceDistance;
		this.overFlowPercentage = overflowPercentage;
		// initialize stud positions
		this.studPositions = new StudPosition[verticalHoleAmount][horizontalHoleAmount];
		for (int i = 0; i < verticalHoleAmount; i++) {
			for (int j = 0; j < horizontalHoleAmount; j++) {
				float x = j * horizontalHoleDistance + horizontalPadding;
				float y = i * verticalHoleDistance + verticalPaddingBottom;
				studPositions[i][j] = new StudPosition(j, i, x, y, StudType.NONE);
			}
		}
		this.stackingPositions = new ArrayList<StackingPosition>();
		this.orientation = WorkPieceOrientation.HORIZONTAL;
	}
	
	public float getWidth() {
		return verticalPaddingTop + verticalPaddingBottom + (verticalHoleAmount - 1) * verticalHoleDistance;
	}
	
	public float getLength() {
		return horizontalPadding * 2 + (horizontalHoleAmount - 1) * horizontalHoleDistance;
	}
	
	private void clearStuds() {
		for (StudPosition[] vertPos : studPositions) {
			for (StudPosition pos : vertPos) {
				pos.setStudType(StudType.NONE);
			}
		}
	}
	
	/**
	 * Configures the list of Stacking-positions and updates the 2D-array of studPositions 
	 * @throws IncorrectWorkPieceDataException 
	 */
	public void configureStackingPositions(final WorkPiece rawWorkPiece, final WorkPieceOrientation orientation) throws IncorrectWorkPieceDataException {
		stackingPositions.clear();
		clearStuds();
		//TODO add upper limits
		WorkPieceDimensions dimensions = rawWorkPiece.getDimensions();
		if (!((dimensions != null) && (dimensions.getWidth() > 0) && (dimensions.getLength() > 0) && (dimensions.getHeight() > 0))) {
			throw new IncorrectWorkPieceDataException(IncorrectWorkPieceDataException.INCORRECT_DATA);
		}
		switch(orientation) {
			case HORIZONTAL:
				configureHorizontalStackingPositions(dimensions);
				break;
			case TILTED:
				configureTiltedStackingPositionsAlt(dimensions);
				break;
			default:
				throw new IllegalArgumentException("Unknown work piece orientation");
		}
		this.orientation = orientation;
	}
	
	private void configureHorizontalStackingPositions(final WorkPieceDimensions dimensions) throws IncorrectWorkPieceDataException {
		if (dimensions.getLength() < dimensions.getWidth()) {
			throw new IncorrectWorkPieceDataException(IncorrectWorkPieceDataException.LENGTH_SMALLER_WIDTH);
		}
		float remainingLength = dimensions.getLength();
		// initially two studs are used (even if corner is needed!)
		int amountOfHorizontalStudsOnePiece = 2;
		
		// the piece is moved studDiameter/2 from the center of its left-most stud (since it's aligned against it)
		// the distance between the left-most side and the center of the second stud is removed from the remaining length
		remainingLength -= (horizontalHoleDistance - studDiameter / 2);
		
		// for real small work-pieces
		if (remainingLength < 0) {
			remainingLength = 0;
		}
				
		// for each time the horizontal hole distance fits in the remaining length the amount of horizontal studs is incremented
		while (remainingLength > horizontalHoleDistance) {
			remainingLength -= horizontalHoleDistance;
			amountOfHorizontalStudsOnePiece++;
		}
		
		// the remaining distance is the space between the next stud and the end of the piece
		float remainingDistance = horizontalHoleDistance - remainingLength;
		if (remainingDistance - studDiameter / 2 < interferenceDistance) {
			remainingLength = 0;
			amountOfHorizontalStudsOnePiece++;
		}
		// note if this distance is long enough, the next stud does not need to be added to the horizontal studs of this piece, as it will be used for the next workpiece
		
		// how many times will this fit
		int amountHorizontal = (int) Math.floor(horizontalHoleAmount / amountOfHorizontalStudsOnePiece);
		
		// special condition for the last piece
		// we calculate the amount of space there is left:
		float remainingDistanceBetweenHoles = (horizontalHoleAmount % amountOfHorizontalStudsOnePiece - 1) * horizontalHoleDistance;
		if (remainingDistanceBetweenHoles < 0)  {
			remainingDistanceBetweenHoles = 0;
		}
		float spaceLeft = remainingDistanceBetweenHoles + horizontalPadding + overFlowPercentage * dimensions.getLength() - studDiameter / 2;
		// if enough space if left (taking into account the overflowPercentage), an extra piece can be placed
		if (spaceLeft >= dimensions.getLength()) {
			// add an extra piece, note minimum two stud-holes should be available:
			if (horizontalHoleAmount % amountOfHorizontalStudsOnePiece >= 2) {
				// also the space over the right-most stud should be smaller than a third of the length
				if ((dimensions.getLength() - remainingDistanceBetweenHoles) / dimensions.getLength() <= MAX_OVERLAP_PERCENTAGE) {
					amountHorizontal++;
				}
			}
		} else if ((spaceLeft < remainingLength) && (remainingDistanceBetweenHoles == 0)) {
			// the last piece would come to much over the edge
			amountHorizontal--;
		}
				
		int amountOfVerticalStudsOnePiece = 1;
		float remainingWidth = dimensions.getWidth();
		while (remainingWidth > verticalHoleDistance) {
			remainingWidth -= verticalHoleDistance;
			amountOfVerticalStudsOnePiece++;
		}
		
		remainingDistance = verticalHoleDistance - remainingWidth;
		// note: whole studDiameter here, because we measure from top of studs (see documentation)
		if (remainingDistance - studDiameter < interferenceDistance) {
			amountOfVerticalStudsOnePiece++;
			remainingWidth = 0;
		}
		
		// how many times will this fit
		int amountVertical = (int) Math.floor(verticalHoleAmount / amountOfVerticalStudsOnePiece);
		// special condition for the last piece
		// we calculate the amount of space there is left: 
		remainingDistanceBetweenHoles = (verticalHoleAmount % amountOfVerticalStudsOnePiece - 1) * verticalHoleDistance;
		if (remainingDistanceBetweenHoles < 0) {
			remainingDistanceBetweenHoles = 0;
		}
		spaceLeft = remainingDistanceBetweenHoles + verticalPaddingTop + overFlowPercentage * dimensions.getWidth() - studDiameter / 2;
		if (spaceLeft >= dimensions.getWidth()) {
			amountVertical++;
		} else if ((spaceLeft < remainingWidth) && (remainingDistanceBetweenHoles == 0)) {
			amountVertical--;
		}
		if ((amountOfVerticalStudsOnePiece == 1) && (amountVertical > verticalHoleAmount)) {
			amountVertical--;
		}
				
		initializeRawWorkPiecePositionsHorizontal(amountOfHorizontalStudsOnePiece, amountOfVerticalStudsOnePiece, amountHorizontal, amountVertical, dimensions, remainingLength, remainingWidth);
	}
	
	private void initializeRawWorkPiecePositionsHorizontal(final int amountOfHorizontalStudsOnePiece, final int amountOfVerticalStudsOnePiece, 
			final int amountHorizontal, final int amountVertical, final WorkPieceDimensions workPieceDimensions, final float remainingLength, final float remainingWidth) {
		int verticalStudIndex = 0;
		for (int i = 0; i < amountVertical; i++) {
			// calculate vertical position
			// position is calculated using width, because of the orientation of x: right, y: down
			float verticalPos = verticalStudIndex * verticalHoleDistance + studDiameter / 2 + workPieceDimensions.getWidth() / 2 + verticalPaddingBottom;
			int horizontalStudIndex = 0;
			for (int j = 0; j < amountHorizontal; j++) {
				float horizontalPos = horizontalStudIndex * horizontalHoleDistance + studDiameter / 2 + workPieceDimensions.getLength() / 2 + horizontalPadding;
				
				int leftVerticalExtraIndex = (int) Math.ceil(amountOfVerticalStudsOnePiece / 2);
				int rightHorizontalExtraIndex = amountOfHorizontalStudsOnePiece - 1;
				
				if (remainingLength <= MIN_OVERLAP_DISTANCE) {
					rightHorizontalExtraIndex--;
				}
				// not necessary as we divide by 2
				if ((remainingWidth <= MIN_OVERLAP_DISTANCE) && (leftVerticalExtraIndex == amountOfVerticalStudsOnePiece - 1)) {
					leftVerticalExtraIndex--;
				}
				
				boolean corner = false;
				StackingPosition stackingPosition = new StackingPosition(horizontalPos, verticalPos, null, WorkPieceOrientation.HORIZONTAL);
				
				// condition one: only two vertical studs and not enough remaining width (only one leftVerticalExtraIndex)
				// condition two: only two horizontal studs, or: only three horizontal studs and not enough remaining length (only two rightHorizontalExtraIndex)
				if ((rightHorizontalExtraIndex <= 2) || (leftVerticalExtraIndex == 0)) {
					StudPosition studPosition = new StudPosition(horizontalStudIndex, verticalStudIndex, studPositions[verticalStudIndex][horizontalStudIndex].getCenterPosition(), StudType.HORIZONTAL_CORNER);
					stackingPosition.addstud(studPosition);
					corner = true;
					// we can still add the vertical stud!
					if (leftVerticalExtraIndex > 0) {
						StudPosition studPosition2 = new StudPosition(horizontalStudIndex, verticalStudIndex + leftVerticalExtraIndex, studPositions[verticalStudIndex + leftVerticalExtraIndex][horizontalStudIndex].getCenterPosition(), StudType.NORMAL); 
						stackingPosition.addstud(studPosition2);
					}
				} else {
					StudPosition studPosition1 = new StudPosition(horizontalStudIndex, verticalStudIndex + leftVerticalExtraIndex, studPositions[verticalStudIndex + leftVerticalExtraIndex][horizontalStudIndex].getCenterPosition(), StudType.NORMAL);
					StudPosition studPosition2 = new StudPosition(horizontalStudIndex + 1, verticalStudIndex, studPositions[verticalStudIndex][horizontalStudIndex + 1].getCenterPosition(), StudType.NORMAL);
					stackingPosition.addstud(studPosition1);
					stackingPosition.addstud(studPosition2);
				}
				
				int horizontalPos2 = horizontalStudIndex + rightHorizontalExtraIndex;
				while (horizontalPos2 >= studPositions[0].length) {
					horizontalPos2--;
				}
				if (horizontalPos2 > horizontalStudIndex) {
					if ((!corner) || (corner && (horizontalPos2 > horizontalStudIndex + 1))) {
						StudPosition studPos2 = new StudPosition(horizontalPos2, verticalStudIndex, studPositions[verticalStudIndex][horizontalPos2].getCenterPosition(), StudType.NORMAL);
						stackingPosition.addstud(studPos2);
					}
				}
				stackingPositions.add(stackingPosition);
				horizontalStudIndex += amountOfHorizontalStudsOnePiece;
			}
			verticalStudIndex += amountOfVerticalStudsOnePiece;
		}
	}
	
	private void configureTiltedStackingPositionsAlt(final WorkPieceDimensions dimensions) throws IncorrectWorkPieceDataException {
		if (dimensions.getLength() < dimensions.getWidth()) {
			logger.error("incorrect data!!!");
			throw new IncorrectWorkPieceDataException(IncorrectWorkPieceDataException.LENGTH_SMALLER_WIDTH);
		}
		
		//TODO take int account strategy
		//TODO take int account edge-overlap
		double interference = INTERFERENCE_DISTANCE;
		double minimumStudOverlap = MIN_OVERLAP_DISTANCE;
		
		double a = Math.sqrt(2) * (horizontalHoleDistance / 2);
		double b = studDiameter / 2 + Math.sqrt(2) * (horizontalHoleDistance / 2 - Math.sqrt(2) * (studDiameter / 2));
		double c = horizontalHoleDistance / 2 - Math.sqrt(2) * studDiameter / 2;
		double d = horizontalHoleDistance / 2;
		
		boolean needsCorners = false;
		if (dimensions.getWidth() < b + minimumStudOverlap) {
			needsCorners = true;
		}
		if (dimensions.getLength() - b < 4 * a + minimumStudOverlap) {
			needsCorners = true;
		}
		
		int studsBetweenRight = 1;
		int studsNeededLeft = 1;
		int studsTotalRight = 1;
		int studsTotalLeft = 1;
		int studsNeededVertical = 1;
		
		double width = dimensions.getWidth();
		width -= b;
		while (width > a - studDiameter / 2) {
			studsNeededLeft++;
			width -= a;
		}
		if (width > a - studDiameter / 2 - interference) {
			studsNeededLeft++;
		}
		double length = dimensions.getLength();
		length -= d;
		double lengthProj = length * Math.cos(Math.PI / 4);
		while (lengthProj > horizontalHoleDistance - studDiameter / 2) {
			studsTotalRight++;
			lengthProj -= horizontalHoleDistance;
		}
		width = dimensions.getWidth();
		width -= d;
		double widthProj = width * Math.cos(Math.PI / 4);
		while (widthProj > horizontalHoleDistance - studDiameter / 2) {
			studsTotalLeft++;
			widthProj -= horizontalHoleDistance;
		}
		double totalHeight = (dimensions.getLength() + dimensions.getWidth()) * Math.sin(Math.PI / 4);
		totalHeight = totalHeight - c;
		studsNeededVertical += Math.floor(totalHeight / verticalHoleDistance);
		double restHeight = totalHeight - (studsNeededVertical - 1) * verticalHoleDistance;
		double extraMargin = 0;
		if (needsCorners) {
			extraMargin = holeDiameter / 2 + Math.sqrt(2) * studDiameter / 2;
		}
		if (restHeight > verticalHoleDistance - studDiameter / 2 - interference - extraMargin) {
			studsNeededVertical++;
		}
		
		initializeRawWorkPiecePositionsTilted(dimensions, studsNeededLeft, studsBetweenRight, studsTotalLeft, studsTotalRight, studsNeededVertical, needsCorners);
		
	}
	
	private void initializeRawWorkPiecePositionsTilted(final WorkPieceDimensions dimensions, final int studsNeededLeft, final int studsNeededRight, final int studsTotalLeft, 
			final int studsTotalRight, final int studsNeededVertical, final boolean needsCorners) {		
		float a = (float) (horizontalHoleDistance / 2 - Math.sqrt(2) * (studDiameter / 2));
		
		float h = (float) (horizontalHoleDistance / 2 + (dimensions.getLength() / (Math.sqrt(2)) - dimensions.getWidth() / (Math.sqrt(2))) / 2);
		float v = (float) ((dimensions.getLength() / (Math.sqrt(2)) + dimensions.getWidth() / (Math.sqrt(2))) / 2 - a);
		
		boolean finished = false;
		int firstHorizontalPositionLeftStudIndex = studsTotalLeft - 1;
		
		int horizontalIndex = firstHorizontalPositionLeftStudIndex;
		int verticalIndex = 0;
		
		while (!finished) {
			
			if (horizontalHoleAmount - horizontalIndex - 1 - studsTotalRight < 0) {
				horizontalIndex = firstHorizontalPositionLeftStudIndex;
				verticalIndex += studsNeededVertical;
			}
			if (verticalHoleAmount - verticalIndex - studsNeededVertical < 0) {
				finished = true;
			} else {
			
				float x = horizontalPadding + (horizontalIndex * horizontalHoleDistance) + h;
				float y = verticalPaddingBottom + (verticalIndex * verticalHoleDistance) + v;
				
				StackingPosition position = new StackingPosition(x, y, null, WorkPieceOrientation.TILTED);
				
				if (needsCorners) {
					StudPosition studPos = new StudPosition(horizontalIndex, verticalIndex, studPositions[verticalIndex][horizontalIndex].getCenterPosition(), StudType.TILTED_CORNER);
					if (studsTotalRight > 2) {
						//int extraHorizontal = (int) Math.floor(studsTotalRight/2);
						//int extraVertical = extraHorizontal/2;
						logger.info("studsTotalRight: " + studsTotalRight);
						int extraHorizontal = studsTotalRight - 1;
						if (extraHorizontal % 2 != 0) {
							extraHorizontal = extraHorizontal - 1;
						}
						int extraVertical = extraHorizontal / 2;
						extraHorizontal++;
						if (extraHorizontal > 4) {
							extraHorizontal = extraHorizontal - 2;
							extraVertical = extraVertical - 1;
						}
						StudPosition studPos2 = new StudPosition(horizontalIndex + extraHorizontal, verticalIndex + extraVertical, studPositions[verticalIndex + extraVertical][horizontalIndex + extraHorizontal].getCenterPosition(), StudType.NORMAL);
						position.addstud(studPos2);
					}
					position.addstud(studPos);
				} else {
					StudPosition studPos = new StudPosition(horizontalIndex, verticalIndex, studPositions[verticalIndex][horizontalIndex].getCenterPosition(), StudType.NORMAL);
					StudPosition studPos2 = new StudPosition(horizontalIndex + 1, verticalIndex, studPositions[verticalIndex][horizontalIndex + 1].getCenterPosition(), StudType.NORMAL);
					int extraHorizontal = (int) Math.floor(studsTotalRight - 1);
					if (extraHorizontal % 2 != 0) {
						extraHorizontal--;
					}
					int extraVertical = extraHorizontal / 2;
					StudPosition studPos3 = new StudPosition(horizontalIndex + 1 + extraHorizontal, verticalIndex + extraVertical, studPositions[verticalIndex + extraVertical][horizontalIndex + 1 + extraHorizontal].getCenterPosition(), StudType.NORMAL);
					position.addstud(studPos);
					position.addstud(studPos2);
					position.addstud(studPos3);
				}
				
				stackingPositions.add(position);
				horizontalIndex = horizontalIndex + studsNeededRight + studsNeededLeft;
			}
			
		}
	}

	public void placeRawWorkPieces(final WorkPiece rawWorkPiece, final int amount) throws IncorrectWorkPieceDataException {
		if (amount <= getMaxRawWorkPiecesAmount()) {
			for (int i = 0; i < amount; i++) {
				StackingPosition stackingPos = stackingPositions.get(i);
				stackingPos.setWorkPiece(new WorkPiece(rawWorkPiece));
				for (StudPosition studPos : stackingPos.getStuds()) {
					studPositions[studPos.getRowIndex()][studPos.getColumnIndex()] = studPos;
				}
			}
		} else {
			throw new IncorrectWorkPieceDataException(IncorrectWorkPieceDataException.INCORRECT_AMOUNT);
		}
	}
	
	public int getMaxRawWorkPiecesAmount() {
		return stackingPositions.size();
	}
	
	public int getHorizontalHoleAmount() {
		return horizontalHoleAmount;
	}

	public void setHorizontalHoleAmount(final int horizontalHoleAmount) {
		this.horizontalHoleAmount = horizontalHoleAmount;
	}

	public int getVerticalHoleAmount() {
		return verticalHoleAmount;
	}

	public void setVerticalHoleAmount(final int verticalHoleAmount) {
		this.verticalHoleAmount = verticalHoleAmount;
	}

	public float getHoleDiameter() {
		return holeDiameter;
	}

	public void setHoleDiameter(final float holeDiameter) {
		this.holeDiameter = holeDiameter;
	}

	public float getStudDiameter() {
		return studDiameter;
	}

	public void setStudDiameter(final float studDiameter) {
		this.studDiameter = studDiameter;
	}

	public float getHorizontalPadding() {
		return horizontalPadding;
	}

	public void setHorizontalPadding(final float horizontalPadding) {
		this.horizontalPadding = horizontalPadding;
	}

	public float getVerticalPadding() {
		return verticalPaddingTop;
	}
	
	public float getVerticalPaddingBottom() {
		return verticalPaddingBottom;
	}

	public void setVerticalPadding(final float verticalPadding) {
		this.verticalPaddingTop = verticalPadding;
	}

	public float getHorizontalHoleDistance() {
		return horizontalHoleDistance;
	}

	public void setHorizontalHoleDistance(final float horizontalHoleDistance) {
		this.horizontalHoleDistance = horizontalHoleDistance;
	}

	public float getVerticalHoleDistance() {
		return verticalHoleDistance;
	}

	public void setVerticalHoleDistance(final float verticalHoleDistance) {
		this.verticalHoleDistance = verticalHoleDistance;
	}

	public StudPosition[][] getStudPositions() {
		return studPositions;
	}

	public void setStudPositions(final StudPosition[][] studPositions) {
		this.studPositions = studPositions;
	}

	public List<StackingPosition> getStackingPositions() {
		return stackingPositions;
	}
	
	public float getInterferenceDistance() {
		return interferenceDistance;
	}
	
	public float getOverflowPercentage() {
		return overFlowPercentage;
	}

	public void setStackingPositions(final List<StackingPosition> stackingPositions) {
		this.stackingPositions = stackingPositions;
	}
	
	public WorkPieceOrientation getOrientation() {
		return orientation;
	}
	
	public int getRawWorkPieceAmount() {
		int amount = 0;
		for (StackingPosition position : stackingPositions) {
			if ((position.getWorkPiece() != null) && (position.getWorkPiece().getType() == Type.RAW)) {
				amount++;
			}
		}
		return amount;
	}
}
