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
	private float horizontalR;
	private float tiltedR;
	private WorkPieceOrientation orientation;

	private StudPosition[][] studPositions;
	private List<StackingPosition> stackingPositions;
	
	private static final float MIN_OVERLAP_DISTANCE = 5;
	private static final double MAX_OVERLAP_PERCENTAGE = 0.35;
	private static final double MAX_OVERFLOW = 50;
	
	private static Logger logger = LogManager.getLogger(BasicStackPlateLayout.class.getName());
		
	public BasicStackPlateLayout(final int horizontalHoleAmount, final int verticalHoleAmount, final float holeDiameter, final float studDiameter, final float horizontalPadding,
			final float verticalPaddingTop, final float verticalPaddingBottom, final float horizontalHoleDistance, final float interferenceDistance, final float overflowPercentage,
				final float horizontalR, final float tiltedR) {
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
		this.horizontalR = horizontalR;
		this.tiltedR = tiltedR;
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
	
	public float getHorizontalR() {
		return horizontalR;
	}

	public void setHorizontalR(final float horizontalR) {
		this.horizontalR = horizontalR;
	}

	public float getTiltedR() {
		return tiltedR;
	}

	public void setTiltedR(final float tiltedR) {
		this.tiltedR = tiltedR;
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
		if (rawWorkPiece != null) {
			WorkPieceDimensions dimensions = rawWorkPiece.getDimensions();
			if (!((dimensions != null) && (dimensions.getWidth() > 0) && (dimensions.getLength() > 0) && (dimensions.getHeight() > 0))) {
				throw new IncorrectWorkPieceDataException(IncorrectWorkPieceDataException.INCORRECT_DATA);
			}
			switch(orientation) {
				case HORIZONTAL:
					configureHorizontalStackingPositions(dimensions);
					break;
				case TILTED:
					//configureTiltedStackingPositionsAlt(dimensions);
					configureTiltedStackingPositions(dimensions);
					break;
				default:
					throw new IllegalArgumentException("Unknown work piece orientation");
			}
			this.orientation = orientation;
		}
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
				StackingPosition stackingPosition = new StackingPosition(horizontalPos, verticalPos, horizontalR, null, WorkPieceOrientation.HORIZONTAL);
				
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

	private void configureTiltedStackingPositions(final WorkPieceDimensions dimensions) throws IncorrectWorkPieceDataException {
		if (dimensions.getLength() < dimensions.getWidth()) {
			throw new IncorrectWorkPieceDataException(IncorrectWorkPieceDataException.LENGTH_SMALLER_WIDTH);
		}
		
		double a = horizontalHoleDistance/(Math.sqrt(2)) - studDiameter/2;
		double b = horizontalHoleDistance/(Math.sqrt(2));
		double c = studDiameter/2;
		double width = dimensions.getWidth();
		double length = dimensions.getLength();
		double surface = width * length;
		boolean ok = false;

		
		//------------------------
		// **FIRST WORK PIECE**
		
		int n = 0;
		while(!ok) {
			double overflowHorL = (width - a - n*b - c) / Math.sqrt(2) - horizontalPadding;	// check the horizontal distance overflowing the stacker to the left
			if ((overflowHorL < 0) || (Math.pow(overflowHorL, 2)/surface < overFlowPercentage)) {	// if this distance is negative, or small enough, everything is ok
				ok = true;
			} else {
				n++;	// if not, we increase the amount of studs to the left
				if (n >= horizontalHoleAmount/2) {
					throw new IncorrectWorkPieceDataException(IncorrectWorkPieceDataException.TOO_LARGE);
				}
			}
		}
		int amountOfStudsLeftFirst = n + 1;
		
		// check if corners are needed
		boolean cornerLength = false;
		if ( (length - a - MIN_OVERLAP_DISTANCE) < Math.sqrt(2) * (2 * horizontalHoleDistance)) {
			cornerLength = true;
		}
		boolean cornerWidth = false;
		if ( (width - a - c - MIN_OVERLAP_DISTANCE) < 0) {
			cornerWidth = true;
		}
		
		// check overflow to the right
		int remainingStudsRight = horizontalHoleAmount - amountOfStudsLeftFirst;
		if (!isOverFlowRightOk(a, b, c, length, width, remainingStudsRight)) {
			throw new IncorrectWorkPieceDataException(IncorrectWorkPieceDataException.TOO_LARGE);
		}

		// check overflow to the top
		int remainingStudsTop = verticalHoleAmount - 1;
		if (!isOverFlowTopOk(a, c, length, width, remainingStudsTop)) {
			throw new IncorrectWorkPieceDataException(IncorrectWorkPieceDataException.TOO_LARGE);
		}
		
		//------------------------
		// **NEXT WORK PIECES FIRST ROW**
		
		double extraSpace = 0;
		if (cornerLength) {
			extraSpace = studDiameter;
		}
		// no corner piece used, or corner piece because of width
		int extraStudsLeft = 0;
		while (width - a - extraStudsLeft*b - extraSpace > interferenceDistance) {
			extraStudsLeft++;
		}
		int amountOfStudsLeftOther = extraStudsLeft + 2;	// also added the aligning stud of this one and the wp to the left

		// calculate amount of pieces horizontally by checking overflow to the right
		int maxHorizontalIndex = 0;
		ok = false;
		while (!ok) {
			remainingStudsRight = horizontalHoleAmount - amountOfStudsLeftFirst - (maxHorizontalIndex) * amountOfStudsLeftOther;
			if (isOverFlowRightOk(a, b, c, length, width, remainingStudsRight)) {
				maxHorizontalIndex++;
			} else {
				maxHorizontalIndex = maxHorizontalIndex - 1;
				ok = true;
			}
		}
		
		
		//------------------------
		// **NEXT WORK PIECE ROWS**
		
		ok = false;
		int verticalRowIndex = 1;
		while (!ok) {
			double boundingWidth = (verticalRowIndex * horizontalHoleDistance * 2 - studDiameter) / Math.sqrt(2);
			double boundingLength = -1;
			double amountOfHoles = amountOfStudsLeftOther - 2;
			
			if (amountOfHoles%2 != 0) {
				boundingLength = (verticalRowIndex + (amountOfHoles - 1)/2 + 1) * Math.sqrt(2) * horizontalHoleDistance + horizontalHoleDistance / Math.sqrt(2);
			} else {
				boundingLength = (verticalRowIndex + amountOfHoles/2 + 1) * Math.sqrt(2) * horizontalHoleDistance - studDiameter;
			}
			if ((width + interferenceDistance < boundingWidth) && (length + interferenceDistance < boundingLength)) {
				ok = true;
			} else {
				verticalRowIndex++;
			}
		}

		int maxVerticalIndex = 0;
		ok = false;
		while (!ok) {
			remainingStudsTop = verticalHoleAmount - maxVerticalIndex  * verticalRowIndex - 1;
			if (isOverFlowTopOk(a, c, length, width, remainingStudsTop)) {
				maxVerticalIndex++;
			} else {
				maxVerticalIndex = maxVerticalIndex - 1;
				ok = true;
			}
		}
		
		logger.info("-amount of studs left first: " + amountOfStudsLeftFirst);
		logger.info("-amount of studs left other: " + amountOfStudsLeftOther);
		logger.info("-amount of horizontal pieces: " + (maxHorizontalIndex + 1));
		logger.info("--verticalRowIndex: " + verticalRowIndex);
		logger.info("-amount of vertical pieces: " + (maxVerticalIndex + 1));
		logger.info("corner piece length: " + cornerLength);
		logger.info("corner piece width: " + cornerWidth);
		
		initializeRawWorkPiecePositionsTilted2(dimensions, amountOfStudsLeftFirst, amountOfStudsLeftOther, (maxHorizontalIndex + 1), verticalRowIndex, (maxVerticalIndex + 1), cornerLength, cornerWidth);
	}
	
	private void initializeRawWorkPiecePositionsTilted2(final WorkPieceDimensions dimensions, final int amountOfStudsLeftFirst,
			final int amountOfStudsLeftOther, final int amountHorizontal, final int amountOfStudsVertical, 
				final int amountVertical, final boolean cornerLength, final boolean cornerWidth) {
		double a = horizontalHoleDistance/(Math.sqrt(2)) - studDiameter/2;
		stackingPositions.clear();
		for (int i = 0; i < amountVertical; i++) {
			for (int j = 0; j < amountHorizontal; j++) {
				int amountOfStudsLeft = amountOfStudsLeftFirst + j * amountOfStudsLeftOther;
				int amountOfStudsBottom = 1 + i * amountOfStudsVertical;
				double adjustment = (horizontalHoleDistance/2 - studDiameter/Math.sqrt(2));
				double xBottom = horizontalPadding + (amountOfStudsLeft - 1)*horizontalHoleDistance + horizontalHoleDistance/2;
				double yBottom = verticalPaddingBottom - adjustment + (amountOfStudsBottom - 1)*verticalHoleDistance;
				double extraX = (dimensions.getLength()/Math.sqrt(2) - dimensions.getWidth()/Math.sqrt(2))/2;
				double extraY = (dimensions.getLength()/Math.sqrt(2) + dimensions.getWidth()/Math.sqrt(2))/2;
				float x = (float) (xBottom + extraX);
				float y = (float) (yBottom + extraY);
				StackingPosition stPos = new StackingPosition(x, y, tiltedR, null, WorkPieceOrientation.TILTED);
				stackingPositions.add(stPos);
				int firstStudPosX = amountOfStudsLeftFirst + j * amountOfStudsLeftOther - 1;
				int firstStudPosY = amountOfStudsVertical * i;
				StudPosition studPos = null;
				if (cornerLength || cornerWidth) {
					studPos = new StudPosition(firstStudPosX, firstStudPosY, studPositions[firstStudPosY][firstStudPosX].getCenterPosition(), StudType.TILTED_CORNER);
					stPos.addstud(studPos);
					// if the corner is not needed because of the length, we will add an extra stud for stability
				} else {
					studPos = new StudPosition(firstStudPosX, firstStudPosY, studPositions[firstStudPosY][firstStudPosX].getCenterPosition(), StudType.NORMAL);
					StudPosition studPos2 = new StudPosition(firstStudPosX + 1, firstStudPosY, studPositions[firstStudPosY][firstStudPosX + 1].getCenterPosition(), StudType.NORMAL);
					stPos.addstud(studPos);
					stPos.addstud(studPos2);
				}
				if (!cornerLength) {
					boolean ok = false;
					int maxTimes = (int) Math.floor((dimensions.getLength() - a - MIN_OVERLAP_DISTANCE) / ((horizontalHoleDistance*2) * Math.sqrt(2)));
					while (!ok) {
						if (maxTimes <= 0) {
							ok = true;
							if (!cornerWidth) {
								stackingPositions.remove(stPos);	// remove the last work piece as it can not be supported correctly
							}
						} else if ((studPositions[0].length > (maxTimes * 2 + firstStudPosX + 1)) && (studPositions.length > (maxTimes + firstStudPosY))) {
							int positionX = maxTimes * 2 + firstStudPosX + 1;
							int positionY = maxTimes + firstStudPosY;
							StudPosition studPos2 = new StudPosition(positionX, positionY, studPositions[positionY][positionX].getCenterPosition(), StudType.NORMAL);
							stPos.addstud(studPos2);
							ok = true;
						} else {
							maxTimes--;
						}		
					}
				}
			}
		}
	}
		
	private boolean isOverFlowRightOk(final double a, final double b, final double c, final double length, final double width, final int remainingStudsRight) {
		double surface = length * width;
		if (remainingStudsRight < 1) {
			return false;
		}
		double overflowHorR = (length - a - remainingStudsRight*b - c) / Math.sqrt(2) - horizontalPadding;
		if ((overflowHorR < 0) || (Math.pow(overflowHorR, 2)/surface < overFlowPercentage)) {
			// check if max overflow isn't reached
			if (overflowHorR > MAX_OVERFLOW) {
				return false;
			}
			return true;
		}
		return false;
	}
	
	private boolean isOverFlowTopOk(final double a, final double c, final double length, final double width, final int remainingStudsTop) {
		double surface = length * width;
		double adjustment = (horizontalHoleDistance/2 - studDiameter/Math.sqrt(2));
		double overflowTop = (width + length)/Math.sqrt(2) - adjustment - remainingStudsTop*horizontalHoleDistance*2 - verticalPaddingTop;
		if (remainingStudsTop < 1) {
			return false;
		}
		if ((overflowTop < 0) || (Math.pow(overflowTop, 2)/surface < overFlowPercentage)) {
			// check if max overflow isn't reached
			if (overflowTop > MAX_OVERFLOW) {
				return false;
			}
			return true;
		} 
		return false;
	}
	
	public void placeRawWorkPieces(final WorkPiece rawWorkPiece, final int amount) throws IncorrectWorkPieceDataException {
		logger.debug("Placing raw workpieces: [" + amount + "].");
		if (amount <= getMaxRawWorkPiecesAmount()) {
			for (int i = 0; i < amount; i++) {
				StackingPosition stackingPos = stackingPositions.get(i);
				stackingPos.setWorkPiece(new WorkPiece(rawWorkPiece));
				for (StudPosition studPos : stackingPos.getStuds()) {
					studPositions[studPos.getRowIndex()][studPos.getColumnIndex()] = studPos;
				}
			}
		} else {
			logger.debug("Trying to place [" + amount + "] but maximum is [" + getMaxRawWorkPiecesAmount() + "].");
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
	
	public void setVerticalPaddingBottom(final float verticalPaddingBottom) {
		this.verticalPaddingBottom = verticalPaddingBottom;
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
	
	public void setInterferenceDistance(final float interferenceDistance) {
		this.interferenceDistance = interferenceDistance;
	}
	
	public float getOverflowPercentage() {
		return overFlowPercentage;
	}

	public void setOverflowPercentage(final float overFlowPercentage) {
		this.overFlowPercentage = overFlowPercentage;
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
