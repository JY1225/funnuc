package eu.robojob.millassist.external.device.stacking;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.robojob.millassist.external.device.stacking.BasicStackPlate.WorkPieceOrientation;
import eu.robojob.millassist.external.device.stacking.StudPosition.StudType;
import eu.robojob.millassist.workpiece.WorkPiece;
import eu.robojob.millassist.workpiece.WorkPiece.Type;
import eu.robojob.millassist.workpiece.WorkPieceDimensions;

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
	private static final double MAX_OVERFLOW = 25;
	
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
		
		double width = dimensions.getWidth();
		double length = dimensions.getLength();
		boolean ok = false;
		
		//------------------------
		// **FIRST WORK PIECE**
		
		// the first piece is aligned against the first column of studs
		
		// check if corners are needed
		boolean cornerLength = false;
		if (length - MIN_OVERLAP_DISTANCE < horizontalHoleDistance) {
			cornerLength = true;
		}
		boolean cornerWidth = false;
		if (width - MIN_OVERLAP_DISTANCE < verticalHoleDistance) {
			cornerWidth = true;
		}
		
		// check overflow to the right
		int remainingStudsRight = horizontalHoleAmount - 1;
		if (!isOverFlowRightHorizontalOk((cornerLength | cornerWidth), length, width, remainingStudsRight)) {
			throw new IncorrectWorkPieceDataException(IncorrectWorkPieceDataException.TOO_LARGE);
		}

		// check overflow to the top
		int remainingStudsTop = verticalHoleAmount - 1;
		if (!isOverFlowTopHorizontalOk((cornerLength | cornerWidth), length, width, remainingStudsTop)) {
			throw new IncorrectWorkPieceDataException(IncorrectWorkPieceDataException.TOO_LARGE);
		}
		
		//------------------------
		// **NEXT WORK PIECES FIRST ROW**
		// we calculate the amount of studs to the left of the next workpiece

		int amountOfStudsWorkPiece = 2;
		double remainingLength = length;
		
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
			amountOfStudsWorkPiece++;
		}
		
		// the remaining distance is the space between the next stud and the end of the piece
		double remainingDistance = horizontalHoleDistance - remainingLength;
		if (remainingDistance - studDiameter / 2 < interferenceDistance) {
			remainingLength = 0;
			amountOfStudsWorkPiece++;
		}
		
		// calculate amount of pieces horizontally by checking overflow to the right
		int maxHorizontalIndex = 0;
		ok = false;
		while (!ok) {
			remainingStudsRight = horizontalHoleAmount - (maxHorizontalIndex) * amountOfStudsWorkPiece;
			if (isOverFlowRightHorizontalOk((cornerLength | cornerWidth), length, width, remainingStudsRight)) {
				maxHorizontalIndex++;
			} else {
				maxHorizontalIndex = maxHorizontalIndex - 1;
				ok = true;
			}
		}
		
		//------------------------
		// **NEXT WORK PIECE ROWS**
		
		int amountOfStudsWorkPieceVertical = 1;
		double remainingWidth = width + studDiameter/2;
		// for each remainingWidth the horizontal hole distance fits in the remaining length the amount of horizontal studs is incremented
		while (remainingWidth > verticalHoleDistance) {
			remainingWidth -= verticalHoleDistance;
			amountOfStudsWorkPieceVertical++;
		}
		
		// the remaining distance is the space between the next stud and the end of the piece
		remainingDistance = verticalHoleDistance - remainingWidth;
		if (remainingDistance - studDiameter / 2 < interferenceDistance) {
			remainingWidth = 0;
			amountOfStudsWorkPieceVertical++;
		}
		
		// calculate amount of pieces horizontally by checking overflow to the right
		int maxVerticalIndex = 0;
		ok = false;
		while (!ok) {
			remainingStudsTop = verticalHoleAmount - maxVerticalIndex * amountOfStudsWorkPieceVertical - 1;
			if (isOverFlowTopHorizontalOk((cornerLength | cornerWidth), length, width, remainingStudsTop)) {
				maxVerticalIndex++;
			} else {
				maxVerticalIndex = maxVerticalIndex - 1;
				ok = true;
			}
		}
		
		logger.info("-amount of studs hor: " + amountOfStudsWorkPiece);
		logger.info("-amount of studs vert: " + amountOfStudsWorkPieceVertical);
		logger.info("-amount of wp hor: " + (maxHorizontalIndex + 1));
		logger.info("-amount of wp vert: " + (maxVerticalIndex + 1));
		logger.info("-corner length: " + cornerLength);
		logger.info("-corner width: " + cornerWidth);

		initializeRawWorkPiecePositionsHorizontal(dimensions, amountOfStudsWorkPiece, amountOfStudsWorkPieceVertical, 
				(maxHorizontalIndex + 1), (maxVerticalIndex + 1), cornerLength, cornerWidth);
	}
	
	private void initializeRawWorkPiecePositionsHorizontal(final WorkPieceDimensions dimensions, final int amountOfStudsWorkPiece,
			final int amountOfStudsWorkPieceVertical, final int amountHorizontal, final int amountVertical, 
				final boolean cornerLength, final boolean cornerWidth) {
		for (int i = 0; i < amountVertical; i++) {
			for (int j = 0; j < amountHorizontal; j++) {
				int amountOfStudsLeft = j * amountOfStudsWorkPiece;
				int amountOfStudsBottom = 1 + i * amountOfStudsWorkPieceVertical;
				double xBottomLeft = horizontalPadding + (amountOfStudsLeft) * horizontalHoleDistance + studDiameter/2;
				double yBottomLeft = verticalPaddingBottom + (amountOfStudsBottom - 1)*verticalHoleDistance + studDiameter/2;
				float x = (float) xBottomLeft + dimensions.getLength()/2;
				float y = (float) yBottomLeft + dimensions.getWidth()/2;
				StackingPosition stPos = new StackingPosition(x, y, horizontalR, null, WorkPieceOrientation.HORIZONTAL);
				stackingPositions.add(stPos);
				int firstStudPosX = j * amountOfStudsWorkPiece;
				int firstStudPosY = i * amountOfStudsWorkPieceVertical;
				StudPosition studPos = null;
				if (cornerLength || cornerWidth) {
					studPos = new StudPosition(firstStudPosX, firstStudPosY, studPositions[firstStudPosY][firstStudPosX].getCenterPosition(), StudType.HORIZONTAL_CORNER);
					stPos.addstud(studPos);
					// if the corner is not needed because of the length, we will add an extra stud for stability
				} else {
					studPos = new StudPosition(firstStudPosX + 1, firstStudPosY, studPositions[firstStudPosY][firstStudPosX + 1].getCenterPosition(), StudType.NORMAL);
					StudPosition studPos2 = new StudPosition(firstStudPosX, firstStudPosY + 1, studPositions[firstStudPosY + 1][firstStudPosX].getCenterPosition(), StudType.NORMAL);
					stPos.addstud(studPos);
					stPos.addstud(studPos2);
				}
				if (!cornerLength) {
					boolean ok = false;
					int maxTimes = (int) Math.floor((dimensions.getLength() + studDiameter/2 - MIN_OVERLAP_DISTANCE)/horizontalHoleDistance);
					while (!ok) {
						if (maxTimes <= 1) {
							ok = true;
						} else if (studPositions[0].length > maxTimes + firstStudPosX) {
							int positionX = maxTimes + firstStudPosX;
							int positionY = firstStudPosY;
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
	
	private boolean isOverFlowRightHorizontalOk(final boolean corner, final double length, final double width, final int remainingStuds) {
		double surface = length * width;
		if (((corner) && (remainingStuds <= 1)) || ((!corner) && (remainingStuds <= 2))) {
			return false;
		}
		double overflowHorR = length - remainingStuds * horizontalHoleDistance + studDiameter/2 - horizontalPadding;
		if ((overflowHorR < 0) || ((Math.pow(overflowHorR, 2)/surface < overFlowPercentage) && (overflowHorR < MAX_OVERFLOW))) {
			return true;
		}
		return false;
	}
	
	private boolean isOverFlowTopHorizontalOk(final boolean corner, final double length, final double width, final int remainingStudsTop) {
		double surface = length * width;
		double overflowTop = width - verticalHoleDistance * remainingStudsTop - studDiameter/2 - verticalPaddingTop;
		if ((!corner) && (remainingStudsTop <= 1)) {
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
			if ((overflowHorL < 0) || ((Math.pow(overflowHorL, 2)/surface < overFlowPercentage) && (overflowHorL < MAX_OVERFLOW))) {	// if this distance is negative, or small enough, everything is ok
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
		if (!isOverFlowRightTiltedOk(a, b, c, length, width, remainingStudsRight)) {
			throw new IncorrectWorkPieceDataException(IncorrectWorkPieceDataException.TOO_LARGE);
		}

		// check overflow to the top
		int remainingStudsTop = verticalHoleAmount - 1;
		if (!isOverFlowTopTiltedOk((cornerLength | cornerWidth), a, c, length, width, remainingStudsTop)) {
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
			if (isOverFlowRightTiltedOk(a, b, c, length, width, remainingStudsRight)) {
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
			double boundingWidth = (verticalRowIndex * horizontalHoleDistance * 2 - studDiameter*Math.sqrt(2)) / Math.sqrt(2);
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
			if (isOverFlowTopTiltedOk((cornerLength | cornerWidth), a, c, length, width, remainingStudsTop)) {
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
		
		initializeRawWorkPiecePositionsTilted(dimensions, amountOfStudsLeftFirst, amountOfStudsLeftOther, (maxHorizontalIndex + 1), verticalRowIndex, (maxVerticalIndex + 1), cornerLength, cornerWidth);
	}
	
	private void initializeRawWorkPiecePositionsTilted(final WorkPieceDimensions dimensions, final int amountOfStudsLeftFirst,
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
		
	private boolean isOverFlowRightTiltedOk(final double a, final double b, final double c, final double length, final double width, final int remainingStudsRight) {
		double surface = length * width;
		if (remainingStudsRight < 1) {
			return false;
		}
		double overflowHorR = (length - a - (remainingStudsRight-1)*horizontalHoleDistance*Math.sqrt(2) - c) / Math.sqrt(2) - horizontalPadding;
		if ((overflowHorR < 0) || (Math.pow(overflowHorR, 2)/surface < overFlowPercentage)) {
			// check if max overflow isn't reached
			if (overflowHorR > MAX_OVERFLOW) {
				return false;
			}
			return true;
		}
		return false;
	}
	
	private boolean isOverFlowTopTiltedOk(final boolean corner, final double a, final double c, final double length, final double width, final int remainingStudsTop) {
		double surface = length * width;
		double adjustment = (horizontalHoleDistance/2 - studDiameter/Math.sqrt(2));
		double overflowTop = (width + length)/Math.sqrt(2) - adjustment - remainingStudsTop*horizontalHoleDistance*2 - verticalPaddingTop;
		if ((!corner) && (remainingStudsTop < 1)) {
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
