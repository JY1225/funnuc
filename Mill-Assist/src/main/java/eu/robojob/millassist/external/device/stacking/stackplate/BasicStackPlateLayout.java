package eu.robojob.millassist.external.device.stacking.stackplate;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.robojob.millassist.external.device.stacking.IncorrectWorkPieceDataException;
import eu.robojob.millassist.external.device.stacking.stackplate.BasicStackPlate.WorkPieceOrientation;
import eu.robojob.millassist.external.device.stacking.stackplate.StudPosition.StudType;
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
	private int layers;
	private WorkPieceOrientation orientation;

	private StudPosition[][] studPositions;
	private List<StackPlateStackingPosition> stackingPositions;
	
	private double minOverlap;
	private double maxOverflow;
	
	private static Logger logger = LogManager.getLogger(BasicStackPlateLayout.class.getName());
		
	public BasicStackPlateLayout(final int horizontalHoleAmount, final int verticalHoleAmount, final float holeDiameter, final float studDiameter, final float horizontalPadding,
			final float verticalPaddingTop, final float verticalPaddingBottom, final float horizontalHoleDistance, final float interferenceDistance, final float overflowPercentage,
				final float horizontalR, final float tiltedR, final double maxOverflow, final double minOverlap) {
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
		this.maxOverflow = maxOverflow;
		this.minOverlap = minOverlap;
		this.layers = 1;
		// initialize stud positions - studType = NONE
		this.studPositions = new StudPosition[verticalHoleAmount][horizontalHoleAmount];
		for (int i = 0; i < verticalHoleAmount; i++) {
			for (int j = 0; j < horizontalHoleAmount; j++) {
				float x = j * horizontalHoleDistance + horizontalPadding;
				float y = i * verticalHoleDistance + verticalPaddingBottom;
				studPositions[i][j] = new StudPosition(j, i, x, y, StudType.NONE);
			}
		}
		this.stackingPositions = new ArrayList<StackPlateStackingPosition>();
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
	
	public double getMinOverlap() {
		return minOverlap;
	}

	public void setMinOverlap(final double minOverlap) {
		this.minOverlap = minOverlap;
	}

	public double getMaxOverflow() {
		return maxOverflow;
	}

	public void setMaxOverflow(final double maxOverflow) {
		this.maxOverflow = maxOverflow;
	}

	/**
	 * Configures the list of Stacking-positions and updates the 2D-array of studPositions 
	 * @throws IncorrectWorkPieceDataException 
	 */
	public void configureStackingPositions(final WorkPiece rawWorkPiece, final WorkPieceOrientation orientation, final int layers) throws IncorrectWorkPieceDataException {
		stackingPositions.clear();
		this.layers = layers;
		clearStuds();
		if (rawWorkPiece != null) {
			WorkPieceDimensions dimensions = rawWorkPiece.getDimensions();
			//Check the dimensions. If length, width or height has a illogical value (0 or lower), throw an exception
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
				case DEG90:
					final Properties properties = new Properties();
					boolean alignRight = false;
					try {
						//Based on the properties file, you can choose to have your workpieces aligned to the right of the corners  
						properties.load(new FileInputStream(new File("settings.properties")));
						if (properties.containsKey("align-right") && properties.get("align-right").equals("true")) {
							alignRight = true;
						}
					} catch (IOException e) {
						logger.error(e);
						e.printStackTrace();
					}
					configureDeg90StackingPositions(dimensions, alignRight);
					break;
				default:
					throw new IllegalArgumentException("Unknown work piece orientation");
			}
			this.orientation = orientation;
		}
	}
	
	private void configureDeg90StackingPositions(final WorkPieceDimensions dimensions, final boolean alignRight) throws IncorrectWorkPieceDataException {
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
		if (length - minOverlap < verticalHoleDistance) {
			cornerLength = true;
		}
		boolean cornerWidth = false;
		if (width - (horizontalHoleDistance - studDiameter/2) - minOverlap < horizontalHoleDistance) {
			cornerWidth = true;
		}
		
		// check overflow to the right
		int remainingStudsRight = horizontalHoleAmount - 1;
		if (!isOverFlowRightHorizontalOk((cornerLength | cornerWidth), width, length,remainingStudsRight)) {
			throw new IncorrectWorkPieceDataException(IncorrectWorkPieceDataException.TOO_LARGE);
		}

		// check overflow to the top
		int remainingStudsTop = verticalHoleAmount - 1;
		if (!isOverFlowTopHorizontalOk((cornerLength | cornerWidth), width, length,remainingStudsTop)) {
			throw new IncorrectWorkPieceDataException(IncorrectWorkPieceDataException.TOO_LARGE);
		}
		
		//------------------------
		// **NEXT WORK PIECES FIRST ROW**
		// we calculate the amount of studs to the left of the next workpiece

		int amountOfHorizontalStudsWorkPiece = getNumberOfStudsPerWorkPiece(width, true);
		
		// calculate amount of pieces horizontally by checking overflow to the right
		int maxHorizontalIndex = 0;
		ok = false;
		while (!ok) {
//			remainingStudsRight = horizontalHoleAmount - (maxHorizontalIndex) * amountOfHorizontalStudsWorkPiece;
			if (isOverFlowRightHorizontalOk((cornerLength | cornerWidth), width, length, remainingStudsRight)) {
				maxHorizontalIndex++;
				remainingStudsRight -= amountOfHorizontalStudsWorkPiece;
			} else {
				maxHorizontalIndex = maxHorizontalIndex - 1;
				ok = true;
			}
		}
		
		//------------------------
		// **NEXT WORK PIECE ROWS**
		
		int amountOfStudsWorkPieceVertical = getNumberOfStudsPerWorkPiece(length, false);
		
		// calculate amount of pieces horizontally by checking overflow to the right
		int maxVerticalIndex = 0;
		ok = false;
		while (!ok) {
			if (isOverFlowTopHorizontalOk((cornerLength | cornerWidth), width, length, remainingStudsTop)) {
				maxVerticalIndex++;
				remainingStudsTop -= amountOfStudsWorkPieceVertical;
			} else {
				maxVerticalIndex = maxVerticalIndex - 1;
				ok = true;
			}
		}

		if (alignRight) {
			initializeRawWorkPiecePositionsDeg90Right(dimensions, amountOfHorizontalStudsWorkPiece, amountOfStudsWorkPieceVertical, 
				(maxHorizontalIndex + 1), (maxVerticalIndex + 1), cornerLength, cornerWidth);
		} else {
			initializeRawWorkPiecePositionsDeg90(dimensions, amountOfHorizontalStudsWorkPiece, amountOfStudsWorkPieceVertical, 
					(maxHorizontalIndex + 1), (maxVerticalIndex + 1), cornerLength, cornerWidth);
		}
	}
	
	private void initializeRawWorkPiecePositionsDeg90(final WorkPieceDimensions dimensions, final int amountOfStudsWorkPiece,
			final int amountOfStudsWorkPieceVertical, final int amountHorizontal, final int amountVertical, 
				final boolean cornerLength, final boolean cornerWidth) {
		for (int i = 0; i < amountVertical; i++) {
			for (int j = 0; j < amountHorizontal; j++) {
				int amountOfStudsLeft = j * amountOfStudsWorkPiece;
				int amountOfStudsBottom = 1 + i * amountOfStudsWorkPieceVertical;
				double xBottomLeft = horizontalPadding + (amountOfStudsLeft) * horizontalHoleDistance + studDiameter/2;
				double yBottomLeft = verticalPaddingBottom + (amountOfStudsBottom - 1)*verticalHoleDistance + studDiameter/2;
				float x = (float) xBottomLeft + dimensions.getWidth()/2;
				float y = (float) yBottomLeft + dimensions.getLength()/2;
				StackPlateStackingPosition stPos = new StackPlateStackingPosition(x, y, horizontalR, null, 0, WorkPieceOrientation.DEG90);
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
				if (!cornerWidth) {
					boolean ok = false;
					int maxTimes = (int) Math.floor((dimensions.getWidth() + studDiameter/2 - minOverlap)/horizontalHoleDistance);
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
	
	private void initializeRawWorkPiecePositionsDeg90Right(final WorkPieceDimensions dimensions, final int amountOfStudsWorkPiece,
			final int amountOfStudsWorkPieceVertical, final int amountHorizontal, final int amountVertical, 
				final boolean cornerLength, final boolean cornerWidth) {
		for (int i = 0; i < amountVertical; i++) {
			for (int j = 0; j < amountHorizontal; j++) {
				int amountOfStudsLeft = j * amountOfStudsWorkPiece;
				int amountOfStudsBottom = 1 + i * amountOfStudsWorkPieceVertical;
				double xBottomRight = horizontalPadding + (amountOfStudsLeft + amountOfStudsWorkPiece - 1) * horizontalHoleDistance - studDiameter/2;
				if (amountHorizontal * amountOfStudsWorkPiece > horizontalHoleAmount) {
					// normally: overflow right, here: left
					xBottomRight = horizontalPadding + (amountOfStudsLeft + amountOfStudsWorkPiece - 1 - (amountHorizontal * amountOfStudsWorkPiece - horizontalHoleAmount)) * horizontalHoleDistance - studDiameter/2;
				}
				double yBottomLeft = verticalPaddingBottom + (amountOfStudsBottom - 1)*verticalHoleDistance + studDiameter/2;
				float x = (float) xBottomRight - dimensions.getWidth()/2;
				float y = (float) yBottomLeft + dimensions.getLength()/2;
				StackPlateStackingPosition stPos = new StackPlateStackingPosition(x, y, horizontalR, null, 0, WorkPieceOrientation.DEG90);
				stackingPositions.add(stPos);
				int firstStudPosX = (j + 1) * amountOfStudsWorkPiece - 1;
				if (amountHorizontal * amountOfStudsWorkPiece > horizontalHoleAmount) {
					// normally: overflow right, here: left
					firstStudPosX = (j + 1) * amountOfStudsWorkPiece - 1 - (amountHorizontal * amountOfStudsWorkPiece - horizontalHoleAmount);
				}
				int firstStudPosY = i * amountOfStudsWorkPieceVertical;
				StudPosition studPos = null;
				if (cornerLength || cornerWidth) {
					studPos = new StudPosition(firstStudPosX, firstStudPosY, studPositions[firstStudPosY][firstStudPosX].getCenterPosition(), StudType.HORIZONTAL_CORNER_LEFT);
					stPos.addstud(studPos);
					// if the corner is not needed because of the length, we will add an extra stud for stability
				} else {
					studPos = new StudPosition(firstStudPosX - 1, firstStudPosY, studPositions[firstStudPosY][firstStudPosX - 1].getCenterPosition(), StudType.NORMAL);
					StudPosition studPos2 = new StudPosition(firstStudPosX, firstStudPosY + 1, studPositions[firstStudPosY + 1][firstStudPosX].getCenterPosition(), StudType.NORMAL);
					stPos.addstud(studPos);
					stPos.addstud(studPos2);
				}
				if (!cornerWidth) {
					boolean ok = false;
					int maxTimes = (int) Math.floor((dimensions.getWidth() + studDiameter/2 - minOverlap)/horizontalHoleDistance);
					while (!ok) {
						if (maxTimes <= 1) {
							ok = true;
						} else if (firstStudPosX - maxTimes >= 0) {
							int positionX = firstStudPosX - maxTimes;
							int positionY = firstStudPosY;
							StudPosition studPos2 = new StudPosition(positionX, positionY, studPositions[positionY][positionX].getCenterPosition(), StudType.NORMAL);
							stPos.addstud(studPos2);
							ok = true;
						} else {
							maxTimes--;
						}		
					}
				}
				if (!cornerLength) {
					boolean ok = false;
					int maxTimes = (int) Math.floor((dimensions.getLength() + studDiameter/2 - minOverlap)/verticalHoleDistance);
					while (!ok) {
						if (maxTimes <= 1) {
							ok = true;
						} else if (studPositions.length > firstStudPosY + maxTimes) {
							int positionX = firstStudPosX;
							int positionY = firstStudPosY + maxTimes;
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
		if (length - minOverlap < horizontalHoleDistance) {
			cornerLength = true;
		}
		boolean cornerWidth = false;
		if (width - minOverlap < verticalHoleDistance) {
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
		int amountOfHorizontalStudsWorkPiece = getNumberOfStudsPerWorkPiece(length, true);
		
		// calculate amount of pieces horizontally by checking overflow to the right
		int maxHorizontalIndex = 0;
		ok = false;
		while (!ok) {
//			remainingStudsRight = horizontalHoleAmount - (maxHorizontalIndex) * amountOfHorizontalStudsWorkPiece;
			if (isOverFlowRightHorizontalOk((cornerLength | cornerWidth), length, width, remainingStudsRight)) {
				maxHorizontalIndex++;
				remainingStudsRight -= amountOfHorizontalStudsWorkPiece;
			} else {
				maxHorizontalIndex = maxHorizontalIndex - 1;
				ok = true;
			}
		}
		
		//------------------------
		// **NEXT WORK PIECE ROWS**	
		int amountOfStudsWorkPieceVertical = getNumberOfStudsPerWorkPiece(width, false);
		
		// calculate amount of pieces vertically by checking overflow at the top
		int maxVerticalIndex = 0;
		ok = false;
		while (!ok) {
			if (isOverFlowTopHorizontalOk((cornerLength | cornerWidth), length, width, remainingStudsTop)) {
				maxVerticalIndex++;
				remainingStudsTop -= amountOfStudsWorkPieceVertical;
			} else {
				maxVerticalIndex = maxVerticalIndex - 1;
				ok = true;
			}
		}

		initializeRawWorkPiecePositionsHorizontal(dimensions, amountOfHorizontalStudsWorkPiece, amountOfStudsWorkPieceVertical, 
				(maxHorizontalIndex + 1), (maxVerticalIndex + 1), cornerLength, cornerWidth);
	}
	
	/**
	 * Calculate the number of stud positions covered by a workpiece.
	 * 
	 * @param dimension - length/width of the workpiece
	 * @param isHorizontal - boolean indicating the direction of studs to calculate. If true, check horizontally; if false, check vertically.
	 * @return number of stud positions covered by a workpiece
	 */
	private int getNumberOfStudsPerWorkPiece(final double dimension, final boolean isHorizontal) {
		float holeDistance;
		int amountOfStudsWorkPiece;
		
		// The workpiece is aligned to its left-most (or lower) stud, so it is in fact shifted with studDiameter/2 to the right (or up). 
		double remainingWorkPieceDimension = dimension + (studDiameter /2);
		
		if(isHorizontal) {
			//Initial value - 2 studs needed
			amountOfStudsWorkPiece = 2;
			holeDistance = horizontalHoleDistance;
			// The first 2 studs are default. This means we can subtract the length of the horizontalHoleDistance already from the initial
			// length of the workpiece since this distance is already covered by studs.
			remainingWorkPieceDimension -= holeDistance;
		} else {
			//Initial value - 1 stud needed
			amountOfStudsWorkPiece = 1;
			holeDistance = verticalHoleDistance;
		}		
		// for real small work-pieces - smaller than the distance between 2 studs
		if (remainingWorkPieceDimension < 0) {
			remainingWorkPieceDimension = 0;
		}		
		// for each time the horizontal hole distance fits in the remaining length, the amount of horizontal studs is incremented
		while (remainingWorkPieceDimension > holeDistance) {
			remainingWorkPieceDimension -= holeDistance;
			amountOfStudsWorkPiece++;
		}		
		// When the remainingLength is less than the distance between 2 studs, we still have a small piece of the workpiece 
		// that we did not take into account. The remaining distance is the space between the next stud and the end of the piece
		// (the distance to the nextWorkpiece - including the first left-most stud of the next workpiece)
		double distanceToNextWorkPiece = holeDistance - (studDiameter / 2) - remainingWorkPieceDimension;
		// If the distance between 2 successive workpieces becomes too small (safe distance - interferenceDistance), we include 1 more stud
		if (distanceToNextWorkPiece < interferenceDistance) {
			remainingWorkPieceDimension = 0;
			amountOfStudsWorkPiece++;
		}
		return amountOfStudsWorkPiece;
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
				StackPlateStackingPosition stPos = new StackPlateStackingPosition(x, y, horizontalR, null, 0, WorkPieceOrientation.HORIZONTAL);
				stackingPositions.add(stPos);
				int firstStudPosX = j * amountOfStudsWorkPiece;
				int firstStudPosY = i * amountOfStudsWorkPieceVertical;
				StudPosition studPos = null;
				if (cornerLength || cornerWidth) {
					studPos = new StudPosition(firstStudPosX, firstStudPosY, studPositions[firstStudPosY][firstStudPosX].getCenterPosition(), StudType.HORIZONTAL_CORNER);
					stPos.addstud(studPos);
					// if the corner is not needed because of the length, we will add an extra stud for stability
				} else {
					//first stud for the workpiece
					studPos = new StudPosition(firstStudPosX + 1, firstStudPosY, studPositions[firstStudPosY][firstStudPosX + 1].getCenterPosition(), StudType.NORMAL);
					StudPosition studPos2 = new StudPosition(firstStudPosX, firstStudPosY + 1, studPositions[firstStudPosY + 1][firstStudPosX].getCenterPosition(), StudType.NORMAL);
					stPos.addstud(studPos);
					stPos.addstud(studPos2);
				}
				if (!cornerLength) {
					boolean ok = false;
					//max number of studs for 1 workpiece
					int maxTimes = (int) Math.floor((dimensions.getLength() + studDiameter/2 - minOverlap)/horizontalHoleDistance);
					while (!ok) {
						if (maxTimes <= 1) {
							ok = true;
						} 
						//add stud at the last studpositions possible
						else if (studPositions[0].length > maxTimes + firstStudPosX) {
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
		if (remainingStuds < 0) {
			return false;
		}
		double surface = length * width;
		//In case a corner is needed, we need at least 2 stud positions for the attachment of the cornerpieces (1 in the corner of the cornerpiece and one next to it)
		//If we do not have corners, we need at least 3 stud positions for stability (2 to support the workpiece and 1 left of the workpiece)
		if (((corner) && (remainingStuds <= 1)) || ((!corner) && (remainingStuds <= 2))) {
			return false;
		}
		double overflowHorR = length + studDiameter/2 - remainingStuds * horizontalHoleDistance - horizontalPadding;
		//If the piece sticking out after the last stud is more than 50% of the width, then return false.
		if ((overflowHorR + horizontalPadding - studDiameter/2)/width >= 0.5) {
			return false;
		}
		if ((overflowHorR < 0) || ((Math.pow(overflowHorR, 2)/surface < overFlowPercentage) && (overflowHorR < maxOverflow))) {
			return true;
		}
		return false;
	}
	
	private boolean isOverFlowTopHorizontalOk(final boolean corner, final double length, final double width, final int remainingStudsTop) {
		if (remainingStudsTop < 0) {
			return false;
		}
		double surface = length * width;
		if ((!corner) && (remainingStudsTop <= 1)) {
			return false;
		}
		double overflowTop = width - verticalHoleDistance * remainingStudsTop - studDiameter/2 - verticalPaddingTop;
		if ((overflowTop < 0) || (Math.pow(overflowTop, 2)/surface < overFlowPercentage)) {
			// check if max overflow isn't reached
			if (overflowTop > maxOverflow) {
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
			if ((overflowHorL < 0) || ((Math.pow(overflowHorL, 2)/surface < overFlowPercentage) && (overflowHorL < maxOverflow))) {	// if this distance is negative, or small enough, everything is ok
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
		if ( (length - a - minOverlap) < Math.sqrt(2) * (2 * horizontalHoleDistance)) {
			cornerLength = true;
		}
		boolean cornerWidth = false;
		if ( (width - a - c - minOverlap) < 0) {
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
		while (a + extraStudsLeft*b + extraSpace - width < interferenceDistance) {
			extraStudsLeft++;
		}
		int amountOfStudsLeftOther = extraStudsLeft + 2;	// also added the aligning stud of this one and the wp to the left

		// calculate amount of pieces horizontally by checking overflow to the right
		int maxHorizontalIndex = 0;
		ok = false;
		while (!ok) {
//			remainingStudsRight = horizontalHoleAmount - amountOfStudsLeftFirst - (maxHorizontalIndex) * amountOfStudsLeftOther;
			if (isOverFlowRightTiltedOk(a, b, c, length, width, remainingStudsRight)) {
				maxHorizontalIndex++;
				remainingStudsRight -= amountOfStudsLeftOther;
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
			if (isOverFlowTopTiltedOk((cornerLength | cornerWidth), a, c, length, width, remainingStudsTop)) {
				maxVerticalIndex++;
				remainingStudsTop -= verticalRowIndex;
			} else {
				maxVerticalIndex = maxVerticalIndex - 1;
				ok = true;
			}
		}
		
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
				StackPlateStackingPosition stPos = new StackPlateStackingPosition(x, y, tiltedR, null, 0, WorkPieceOrientation.TILTED);
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
					int maxTimes = (int) Math.floor((dimensions.getLength() - a - minOverlap) / ((horizontalHoleDistance*2) * Math.sqrt(2)));
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
			if (overflowHorR > maxOverflow) {
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
			if (overflowTop > maxOverflow) {
				return false;
			}
			return true;
		} 
		return false;
	}
	
	private void clearStackingPositions() {
		for (StackPlateStackingPosition stackingPos : stackingPositions) {
			stackingPos.setWorkPiece(null);
			stackingPos.setAmount(0);
		}
	}
	
	/**
	 * Clear the stackPlate and add a given amount of raw workPieces.
	 * 
	 * @param rawWorkPiece
	 * @param amount
	 * @throws IncorrectWorkPieceDataException
	 */
	public void initRawWorkPieces(final WorkPiece rawWorkPiece, final int amount) throws IncorrectWorkPieceDataException {
		logger.debug("Placing raw workpieces: [" + amount + "].");
		clearStackingPositions();
		if(amount <= getMaxRawWorkPiecesAmount()) {
			placeRawWorkPieces(rawWorkPiece, amount, false);
		} else {
			logger.debug("Trying to place [" + amount + "] but maximum is [" + getMaxRawWorkPiecesAmount() + "].");
			throw new IncorrectWorkPieceDataException(IncorrectWorkPieceDataException.INCORRECT_AMOUNT);
		}
	}
	
	/**
	 * Add a certain amount of raw workPieces to the stackPlate. The first place that is free, will be populated by
	 * a workPiece.
	 * 
	 * @param rawWorkPiece
	 * @param amount
	 */
	public void placeRawWorkPieces(final WorkPiece rawWorkPiece, final int amount, boolean resetFirst) {
		logger.debug("Adding raw workpieces: [" + amount + "].");
		int placedAmount = 0;
		int stackingPos = 0;
		StackPlateStackingPosition stPos = stackingPositions.get(0);
		//For any number of layers, we only put 1 workPiece on the first position. This ensures that the robot
		//places finished products always at the same position
		if(placeFirstPiece(rawWorkPiece, resetFirst)) {
			placedAmount++;
		}
		stackingPos++;
		while (placedAmount < amount && stackingPos < stackingPositions.size()) {
			stPos = stackingPositions.get(stackingPos);
			while(placedAmount < amount && addOneWorkPiece(rawWorkPiece, stPos,getLayers(), false) ) {
				placedAmount++;
			}
			stackingPos++;
		}
	}
	
	private boolean placeFirstPiece(final WorkPiece workPiece, boolean isEmptyPiece) {
		if(isEmptyPiece) {
			resetWorkPieceAt(0);
			return false;
		} else {
			return addOneWorkPiece(workPiece, stackingPositions.get(0),1, false);
		}
	}
	
//	public void replaceFinishedPieces(final WorkPiece rawWorkPiece, final int amount) {	
//		removeFinishedFromTable();
//		int positionIndex = getFirstRawPosition();
//		StackPlateStackingPosition stPos = stackingPositions.get(positionIndex);
//		int placedAmount = 0;
//		while (placedAmount < amount && positionIndex > 0) {
//			stPos = stackingPositions.get(positionIndex);
//			while(placedAmount < amount && addOneWorkPiece(rawWorkPiece, stPos,getLayers(), true) ) {
//				placedAmount++;
//			}
//			positionIndex--;
//		}
//	}
	
	public void removeFinishedFromTable() {
		for (StackPlateStackingPosition position : stackingPositions) {
			if(position.hasWorkPiece() && position.getWorkPiece().getType().equals(WorkPiece.Type.FINISHED)) {
				position.setWorkPiece(null);
				position.setAmount(0);
			}
		}
	}
	
	public int getFirstRawPosition() {
		int lastPiece = -1;
		for (StackPlateStackingPosition position : stackingPositions) {
			if ((position.hasWorkPiece()) && (position.getWorkPiece().getType().equals(WorkPiece.Type.RAW))) {
				return stackingPositions.indexOf(position);
			} else if (position.hasWorkPiece()) {
				lastPiece = stackingPositions.indexOf(position);
			}
		}
		return lastPiece;
	}	
	
	public void resetWorkPieceAt(int positionIndex) {
		stackingPositions.get(positionIndex).setWorkPiece(null);
		stackingPositions.get(positionIndex).setAmount(0);	
	}
	
	/**
	 * Try to place one given WorkPiece on a given position. In case a WorkPiece is already present on that
	 * position, the count will be incremented. Otherwise, if a finished workPiece is there, nothing will be done
	 * 
	 * @param rawWorkPiece
	 * @param position
	 * @param maxNbOfPieces
	 * 			- the maximum number of pieces stacked on each other
	 * @return
	 */
	private boolean addOneWorkPiece(final WorkPiece workPiece, StackPlateStackingPosition position, int maxNbOfPieces, boolean overwrite) {
		if(position.hasWorkPiece()) {
			if(position.getWorkPiece().getType().equals(workPiece.getType())) {
				if(position.getAmount() < maxNbOfPieces) {
					position.incrementAmount();
					return true;
				}
				return false;
			}
			if(overwrite) {
				position.setWorkPiece(workPiece);
				position.setAmount(1);
				return true;
			}
			return false;
		} 
		else {
			for (StudPosition studPos : position.getStuds()) {
				studPositions[studPos.getRowIndex()][studPos.getColumnIndex()] = studPos;
			}
			position.setWorkPiece(workPiece);
			position.setAmount(1);
			return true;
		}
	}
	
	public int getMaxRawWorkPiecesAmount() {
		if (layers == 1) {
			return stackingPositions.size();
		} else {
			//Only 1 piece at the first position
			return layers * (stackingPositions.size() - 1) + 1;
		}
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

	public List<StackPlateStackingPosition> getStackingPositions() {
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
	
	public void setStackingPositions(final List<StackPlateStackingPosition> stackingPositions) {
		this.stackingPositions = stackingPositions;
	}
	
	public WorkPieceOrientation getOrientation() {
		return orientation;
	}
	
	public int getWorkPieceAmount(Type workPieceType) {
		int amount = 0;
		for (StackPlateStackingPosition position : stackingPositions) {
			if ((position.hasWorkPiece()) && (position.getWorkPiece().getType().equals(workPieceType))) {
				amount += position.getAmount();
			}
		}
		return amount;
	}

	public int getLayers() {
		return layers;
	}

	public void setLayers(final int layers) {
		this.layers = layers;
	}
	
//	private boolean isOverFlowRightDeg90Ok(final boolean corner, final double length, final double width, final int remainingStuds) {
//	if (remainingStuds < 0) {
//		return false;
//	}
//	double surface = length * width;
//	if (((corner) && (remainingStuds <= 1)) || ((!corner) && (remainingStuds <= 2))) {
//		return false;
//	}
//	double overflowHorR = width + studDiameter/2 - remainingStuds * horizontalHoleDistance - horizontalPadding;
//	if ((overflowHorR + horizontalPadding - studDiameter/2)/width >= 0.5) {
//		return false;
//	}
//	if ((overflowHorR < 0) || ((Math.pow(overflowHorR, 2)/surface < overFlowPercentage) && (overflowHorR < maxOverflow))) {
//		return true;
//	}
//	return false;
//}
	
//	private boolean isOverFlowTopDeg90Ok(final boolean corner, final double length, final double width, final int remainingStudsTop) {
//	if (remainingStudsTop < 0) {
//		return false;
//	}
//	double surface = length * width;
//	if ((!corner) && (remainingStudsTop <= 1)) {
//		return false;
//	}
//	double overflowTop = length - verticalHoleDistance * remainingStudsTop - studDiameter/2 - verticalPaddingTop;
//	if ((overflowTop < 0) || (Math.pow(overflowTop, 2)/surface < overFlowPercentage)) {
//		// check if max overflow isn't reached
//		if (overflowTop > maxOverflow) {
//			return false;
//		}
//		return true;
//	} 
//	return false;
//}
}
