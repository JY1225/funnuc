package eu.robojob.millassist.external.device.stacking.stackplate.basicstackplate;

import eu.robojob.millassist.external.device.stacking.IncorrectWorkPieceDataException;
import eu.robojob.millassist.external.device.stacking.stackplate.AbstractStackPlateLayout;
import eu.robojob.millassist.external.device.stacking.stackplate.AbstractStackPlate.WorkPieceOrientation;
import eu.robojob.millassist.external.device.stacking.stackplate.StackPlateStackingPosition;
import eu.robojob.millassist.external.device.stacking.stackplate.basicstackplate.StudPosition.StudType;
import eu.robojob.millassist.workpiece.WorkPiece;
import eu.robojob.millassist.workpiece.WorkPieceDimensions;

public class BasicStackPlateLayout extends AbstractStackPlateLayout {
	
	// general settings

	private float horizontalPadding;
	private float verticalPaddingTop;
	private float verticalPaddingBottom;
	
	private int horizontalHoleAmount;
	private int verticalHoleAmount;
	private float holeDiameter;
	private float studDiameter;

	private float horizontalHoleDistance;
	private float verticalHoleDistance;
	private float interferenceDistance;
	private float overFlowPercentage;

	private float tiltedR;
	private float horizontalR;

	private StudPosition[][] studPositions;

	private double minOverlap;
	private double maxOverflow;
	
	private boolean isCornerLength;
	private boolean isCornerWidth;
	
//	private static Logger logger = LogManager.getLogger(BasicStackPlateLayout.class.getName());
		
	public BasicStackPlateLayout(final int horizontalHoleAmount, final int verticalHoleAmount, final float holeDiameter, final float studDiameter, final float horizontalPadding,
			final float verticalPaddingTop, final float verticalPaddingBottom, final float horizontalHoleDistance, final float interferenceDistance, final float overflowPercentage,
				final float horizontalR, final float tiltedR, final double maxOverflow, final double minOverlap) {
		super();
		this.horizontalPadding = horizontalPadding;
		this.verticalPaddingBottom = verticalPaddingBottom;
		this.verticalPaddingTop = verticalPaddingTop;
		this.horizontalHoleAmount = horizontalHoleAmount;
		this.verticalHoleAmount = verticalHoleAmount;
		this.holeDiameter = holeDiameter;
		this.studDiameter = studDiameter;
		this.horizontalHoleDistance = horizontalHoleDistance;
		this.verticalHoleDistance = 2 * horizontalHoleDistance;		// this is always the case with this Basic Stack Plate (so tilted layout results in a 45° angle)
		this.interferenceDistance = interferenceDistance;
		this.overFlowPercentage = overflowPercentage;
		this.maxOverflow = maxOverflow;
		this.minOverlap = minOverlap;
		this.tiltedR = tiltedR;
		this.horizontalR = horizontalR;
		initStudPositions();
		calcPlateWidth();
		calcPlateLength();
	}
	
	/////////////////////////
	//                     //
	//     INITIALIZE      // 
	//                     //
	/////////////////////////
	
	
	/**
	 * Initialize all stud positions by setting the possible stud positions back to StudType.NONE
	 */
	private void initStudPositions() {
		this.studPositions = new StudPosition[verticalHoleAmount][horizontalHoleAmount];
		for (int i = 0; i < verticalHoleAmount; i++) {
			for (int j = 0; j < horizontalHoleAmount; j++) {
				//Calculate coordinates using the origin from the stacker
				float x = j * horizontalHoleDistance + getHorizontalPadding();
				float y = i * verticalHoleDistance + getVerticalPaddingBottom();
				studPositions[i][j] = new StudPosition(j, i, x, y, StudType.NONE);
			}
		}
	}
	
	/**
	 * Calculate the width of the stackPlate starting from the origin point of the stacker
	 */
	@Override
	protected void calcPlateWidth() {
		setPlateWidth(getVerticalPaddingBottom() + (verticalHoleAmount - 1) * verticalHoleDistance + getVerticalPaddingTop());
	}
	
	/**
	 * Calculate the length of the stackPlate starting from the origin point of the stacker
	 */
	@Override
	protected void calcPlateLength() {
		setPlateLength(getHorizontalPadding() + (horizontalHoleAmount - 1) * horizontalHoleDistance + getHorizontalPadding());
	}

	/**
	 * Reset all studs to StudType.NONE
	 */
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
	public void configureStackingPositions(final WorkPiece rawWorkPiece, final WorkPieceOrientation orientation, final int layers) throws IncorrectWorkPieceDataException {
		clearStuds();
		super.configureStackingPositions(rawWorkPiece, orientation, layers);
	}
	
	/////////////////////////
	//                     //
	//      OVERFLOW       // 
	//                     //
	/////////////////////////
	
	@Override
	protected void checkSpecialStackingConditions(final WorkPieceDimensions dimensions, final WorkPieceOrientation orientation)  
			throws IncorrectWorkPieceDataException {
		checkCorners(dimensions, orientation);
		boolean overFlow = checkInitialOverflow(dimensions, orientation);
		if(!overFlow) {
			throw new IncorrectWorkPieceDataException(IncorrectWorkPieceDataException.TOO_LARGE);
		}
	}
	
	/**
	 * Check whether one workpiece fits the stacker
	 * 
	 * @param dimensions
	 * 		- Dimensions of the workpiece (length, width)
	 * @param orientation
	 * 		- Orientation of the workpiece on the stacker
	 * @return
	 * 		- true, in case there is no overflow
	 * 		- false, in case the piece is too big for the stacker and thus overflow occurs
	 * @throws IncorrectWorkPieceDataException 
	 */
	private boolean checkInitialOverflow(WorkPieceDimensions dimensions, WorkPieceOrientation orientation) throws IncorrectWorkPieceDataException  {
		boolean overflowR, overflowTop;
		int remainingStudsRight = horizontalHoleAmount - 1;
		int remainingStudsTop = verticalHoleAmount - 1;
		switch(orientation) {
		case HORIZONTAL:
			overflowR = isOverFlowRightHorizontal(dimensions.getLength(), dimensions.getWidth(), remainingStudsRight);
			overflowTop = isOverFlowTopHorizontal(dimensions.getLength(), dimensions.getWidth(), remainingStudsTop);
			break;
		case DEG90:
			overflowR = isOverFlowRightHorizontal(dimensions.getWidth(), dimensions.getLength(), remainingStudsRight);
			overflowTop = isOverFlowTopHorizontal(dimensions.getWidth(), dimensions.getLength(), remainingStudsTop);
			break;
		case TILTED:
			int n = getAmountOfStudsLeft(dimensions.getLength(), dimensions.getWidth());
			if (n >= horizontalHoleAmount/2) {
				throw new IncorrectWorkPieceDataException(IncorrectWorkPieceDataException.TOO_LARGE);
			}
			remainingStudsRight = horizontalHoleAmount - n;
			overflowR = isOverFlowRightTilted(dimensions.getLength(), dimensions.getWidth(), remainingStudsRight);
			overflowTop = isOverFlowTiltedTop(dimensions.getLength(), dimensions.getWidth(), remainingStudsTop);
			break;
		default:
			throw new IllegalArgumentException("Unknown work piece orientation");
		}
		return (overflowR && overflowTop);
	}
	
	private boolean isOverFlowRightHorizontal(float length, float width, int remainingStuds) {
		double overflowR = length + studDiameter/2 - remainingStuds * horizontalHoleDistance - getHorizontalPadding();
		return isOverFlow(overflowR, length*width);
	}
	
	private boolean isOverFlowTopHorizontal(float length, float width, int remainingStuds) {
		double overflowTop = width - remainingStuds * verticalHoleDistance - getVerticalPaddingTop() + studDiameter / 2;
		return isOverFlow(overflowTop, length*width);
	}
	
	private boolean isOverFlowRightTilted(float length, float width, int remainingStuds) {
		double overflowR = (length - (horizontalHoleDistance/(Math.sqrt(2)) - studDiameter/2) - (remainingStuds-1)*horizontalHoleDistance*Math.sqrt(2) - studDiameter/2) / Math.sqrt(2) - getHorizontalPadding();
		return isOverFlow(overflowR, length*width);
	}
	
	private boolean isOverFlowTiltedTop(float length, float width, int remainingStuds) {
		double overflowTop = (width + length)/Math.sqrt(2) - (horizontalHoleDistance/2 - studDiameter/Math.sqrt(2)) - remainingStuds*horizontalHoleDistance*2 - getVerticalPaddingTop();
		return isOverFlow(overflowTop, length*width);
	}
	
	private boolean isOverFlow(double remainingDistance, double surface) {
		if((remainingDistance < 0) || (Math.pow(remainingDistance, 2)/surface < overFlowPercentage)) {
			if(remainingDistance > maxOverflow) {
				return false;
			}
			return true;
		}
		return false;
	}
	
	private int getAmountOfStudsLeft(float length, float width) {
		double a = horizontalHoleDistance/(Math.sqrt(2)) - studDiameter/2;
		double b = horizontalHoleDistance/(Math.sqrt(2));
		double c = studDiameter/2;
		boolean ok = false;
		int n = 0;
		while(!ok) {
			double overflowHorL = (width - a - n*b - c) / Math.sqrt(2) - getHorizontalPadding();	// check the horizontal distance overflowing the stacker to the left
			if (isOverFlow(overflowHorL, length*width)) {	// if this distance is negative, or small enough, everything is ok
				ok = true;	
			} else {
				n++; // if not, we increase the amount of studs to the left
			}
		}
		return n + 1;
	}
	
	////////////////////////
	//                    //
	//      CORNERS       // 
	//                    //
	////////////////////////
	private boolean checkCorners(final WorkPieceDimensions dimensions, final WorkPieceOrientation orientation) 
			throws IllegalArgumentException {
		boolean result = false;
		switch(orientation) {
			case HORIZONTAL:
				result = isCornerNeededNormal(dimensions.getLength(), dimensions.getWidth());
				break;
			case DEG90:
				result = isCornerNeededNormal(dimensions.getWidth(), dimensions.getLength());
				break;
			case TILTED:
				result = isCornerNeededTilted(dimensions.getLength(), dimensions.getWidth());
				break;
			default: 
				throw new IllegalArgumentException("Unknown work piece orientation");
		}
		return result;
	}
	
	private boolean isCornerNeededNormal(float length, float width) {
		isCornerLength = false;
		isCornerWidth  = false;		
		if (length - minOverlap < 2 * horizontalHoleDistance) {
			isCornerLength = true;
		}
		if (width - minOverlap < verticalHoleDistance) {
			isCornerWidth = true;
		}
		return (isCornerLength || isCornerWidth);
	}
	
	private boolean isCornerNeededTilted(float length, float width) {
		isCornerLength = false;
		isCornerWidth  = false;	
		double a = horizontalHoleDistance/(Math.sqrt(2)) - studDiameter/2;
		double c = studDiameter/2;
		if ( (length - a - minOverlap) < Math.sqrt(2) * (2 * horizontalHoleDistance)) {
			isCornerLength = true;
		}
		if ( (width - a - c - minOverlap) < 0) {
			isCornerWidth = true;
		}
		return (isCornerLength || isCornerWidth);
	}
	
	/////////////////////////
	//                     //
	//       AMOUNTS       // 
	//                     //
	/////////////////////////
	@Override
	protected int getMaxHorizontalAmount(final WorkPieceDimensions dimensions, final WorkPieceOrientation orientation)  {
		switch(orientation) {
		case HORIZONTAL:
			return getMaxHorizontalAmountNormal(dimensions.getLength(), dimensions.getWidth(), orientation);			
		case DEG90:
			return getMaxHorizontalAmountNormal(dimensions.getWidth(), dimensions.getLength(), orientation);
		case TILTED:
			return getMaxHorizontalAmountTilted(dimensions.getLength(), dimensions.getWidth(), orientation);
		default:
			throw new IllegalArgumentException("Unknown work piece orientation");
		}
	}
	
	private int getMaxHorizontalAmountNormal(float length, float width, WorkPieceOrientation orientation) {
		int amountOfHorizontalStudsWorkPiece = getNumberOfStudsPerWorkPiece(length, true);
		int remainingStudsRight = horizontalHoleAmount - 1;
		int maxHorizontalIndex = (int) Math.floor(remainingStudsRight/amountOfHorizontalStudsWorkPiece);
		remainingStudsRight -= amountOfHorizontalStudsWorkPiece*maxHorizontalIndex;
		if(isSufficientStudsLeftHorizontal(remainingStudsRight, orientation, (isCornerLength || isCornerWidth))) {
			if(isOverFlowRightHorizontal(length, width, remainingStudsRight)) {
				maxHorizontalIndex++;
			}
		}
		return maxHorizontalIndex;
	}
	
	private int getMaxHorizontalAmountTilted(float length, float width, WorkPieceOrientation orientation) {
		double a = horizontalHoleDistance/(Math.sqrt(2)) - studDiameter/2;
		double b = horizontalHoleDistance/(Math.sqrt(2));
		
		int amountOfStudsLeftOther = getNbStudsLeftOther(length, width, a, b);
		
		// calculate amount of pieces horizontally by checking overflow to the right
		int maxHorizontalIndex = 0;
		int remainingStudsRight = horizontalHoleAmount - getAmountOfStudsLeft(length, width);
		boolean ok = false;
		while (!ok) {
			if(isSufficientStudsLeftHorizontal(remainingStudsRight, orientation, isCornerLength || isCornerWidth)) {
				if (isOverFlowRightTilted(length, width, remainingStudsRight)) {
					maxHorizontalIndex++;
					remainingStudsRight -= amountOfStudsLeftOther;
				} else {
					ok = true;
				}
			} else {
				ok = true;
			}
		}
		return maxHorizontalIndex;
	}
	
	private int getNbStudsLeftOther(float length, float width, double a, double b) {
		//Calculate the stud pieces to the left of the workpiece
		double extraSpace = 0;
		if (isCornerLength) {
			extraSpace = studDiameter;
		}
		// no corner piece used, or corner piece because of width
		int extraStudsLeft = 0;
		while (a + extraStudsLeft*b + extraSpace - width < interferenceDistance) {
			extraStudsLeft++;
		}
		return (extraStudsLeft + 2);	// also added the aligning stud of this one and the wp to the left
	}
	
	private boolean isSufficientStudsLeftHorizontal(int remainingStudsRight, WorkPieceOrientation orientation, boolean corner) {
		switch(orientation) {
		case HORIZONTAL:
		case DEG90:
			if (remainingStudsRight < 0) {
				return false;
			}
			//In case a corner is needed, we need at least 2 stud positions for the attachment of the cornerpieces (1 in the corner of the cornerpiece and one next to it)
			//If we do not have corners, we need at least 3 stud positions for stability (2 to support the workpiece and 1 left of the workpiece)
			if (((corner) && (remainingStudsRight <= 1)) || ((!corner) && (remainingStudsRight < 2))) {
				return false;
			}
		case TILTED:
			if (remainingStudsRight < 1) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Calculate the number of stud positions covered by a workpiece.
	 * 
	 * @param dimension 
	 * 		- length/width of the workpiece
	 * @param isHorizontal 
	 * 		- boolean indicating the direction of studs to calculate. If true, check horizontally; if false, check vertically.
	 * @return 
	 * 		- number of stud positions covered by a workpiece
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
	
	@Override
	protected int getMaxVerticalAmount(final WorkPieceDimensions dimensions, final WorkPieceOrientation orientation) {
		switch(orientation) {
		case HORIZONTAL:
			return getMaxVerticalAmountNormal(dimensions.getLength(), dimensions.getWidth(), orientation);			
		case DEG90:
			return getMaxVerticalAmountNormal(dimensions.getWidth(), dimensions.getLength(), orientation);
		case TILTED:
			return getMaxVerticalAmountTilted(dimensions.getLength(), dimensions.getWidth(), orientation);
		default:
			throw new IllegalArgumentException("Unknown work piece orientation");
		}
	}
	
	private int getMaxVerticalAmountNormal(float length, float width, WorkPieceOrientation orientation) {
		int amountOfStudsWorkPieceVertical = getNumberOfStudsPerWorkPiece(width, false);
		int remainingStudsTop = verticalHoleAmount - 1;
		int maxVerticalIndex = (int) Math.floor(remainingStudsTop/amountOfStudsWorkPieceVertical);
		remainingStudsTop -= (amountOfStudsWorkPieceVertical*maxVerticalIndex);
		if(isSufficientStudsLeftTop(remainingStudsTop, orientation, isCornerLength || isCornerWidth)) {
			if(isOverFlowTopHorizontal(length, width, remainingStudsTop)) {
				maxVerticalIndex++;
			}
		}
		return maxVerticalIndex;
	}
	
	private int getVerticalIndex(float length, float width, WorkPieceOrientation orientation) {
		boolean ok = false;
		double a = horizontalHoleDistance/(Math.sqrt(2)) - studDiameter/2;
		double b = horizontalHoleDistance/(Math.sqrt(2));
		int amountOfStudsLeftOther = getNbStudsLeftOther(length, width, a, b);
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
		return verticalRowIndex;
	}
	
	private int getMaxVerticalAmountTilted(float length, float width, WorkPieceOrientation orientation) {
		boolean ok = false;
		int verticalRowIndex = getVerticalIndex(length, width, orientation);
		int remainingStudsTop = verticalHoleAmount - 1;
		int maxVerticalIndex = 0;
		while (!ok) {
			if (isOverFlowTiltedTop(length, width, remainingStudsTop)) {
				maxVerticalIndex++;
				remainingStudsTop -= verticalRowIndex;
			} else {
				ok = true;
			}
		}
		return maxVerticalIndex;
	}
	
	private boolean isSufficientStudsLeftTop(int remainingStudsTop, WorkPieceOrientation orientation, boolean corner) {
		switch(orientation) {
		case HORIZONTAL:
		case DEG90:
			if (remainingStudsTop < 0) {
				return false;
			}
			if ((!corner) && (remainingStudsTop <= 1)) {
				return false;
			}
			return true;
		case TILTED:
			if ((!corner) && (remainingStudsTop < 1)) {
				return false;
			}
			return true;
		}
		return false;
	}
	
	
	////////////////////////////
	//                        //
	//   STACKING POSITIONS   // 
	//                        //
	////////////////////////////
	@Override
	protected void initStackingPositions(int nbHorizontal, int nbVertical, WorkPieceDimensions dimensions, WorkPieceOrientation orientation) {
		int verticalStuds, horizontalStuds;
		switch(orientation) {
		case HORIZONTAL:
			verticalStuds = getNumberOfStudsPerWorkPiece(dimensions.getWidth(), false); 
			horizontalStuds = getNumberOfStudsPerWorkPiece(dimensions.getLength(), true);
			if (isRightAlignedHorizontal()) {
				initializeRawWorkPiecePositionsHorizontalRight(dimensions, horizontalStuds, verticalStuds, nbHorizontal, nbVertical, isCornerLength, isCornerWidth);
			} else {
				initializeRawWorkPiecePositionsHorizontal(dimensions, horizontalStuds, verticalStuds, nbHorizontal, nbVertical, isCornerLength, isCornerWidth);
			}
			break;
		case DEG90:
			verticalStuds = getNumberOfStudsPerWorkPiece(dimensions.getLength(), false); 
			horizontalStuds = getNumberOfStudsPerWorkPiece(dimensions.getWidth(), true);
			if(isRightAlignedVertical()) {
				initializeRawWorkPiecePositionsDeg90Right(dimensions, horizontalStuds, verticalStuds, nbHorizontal, nbVertical, isCornerLength, isCornerWidth);
			} else {
				initializeRawWorkPiecePositionsDeg90(dimensions, horizontalStuds, verticalStuds, nbHorizontal, nbVertical, isCornerLength, isCornerWidth);
			}
			break;
		case TILTED:
			int n = getAmountOfStudsLeft(dimensions.getLength(), dimensions.getWidth());
			double a = horizontalHoleDistance/(Math.sqrt(2)) - studDiameter/2;
			double b = horizontalHoleDistance/(Math.sqrt(2));
			int leftOther = getNbStudsLeftOther(dimensions.getLength(), dimensions.getWidth(), a, b);
			if(isRightAlignedHorizontal()) {
				initializeRawWorkPiecePositionsTiltedRight(dimensions, leftOther - n, leftOther, nbHorizontal, getVerticalIndex(dimensions.getLength(), dimensions.getWidth(), orientation), nbVertical, isCornerLength, isCornerWidth);
			}
			else {
				initializeRawWorkPiecePositionsTilted(dimensions, n, leftOther, nbHorizontal, getVerticalIndex(dimensions.getLength(), dimensions.getWidth(), orientation), nbVertical, isCornerLength, isCornerWidth);
			}
			break;
		}
	}
	
	private void initializeRawWorkPiecePositionsDeg90(final WorkPieceDimensions dimensions, final int amountOfStudsWorkPiece,
			final int amountOfStudsWorkPieceVertical, final int amountHorizontal, final int amountVertical, 
				final boolean cornerLength, final boolean cornerWidth) {
		for (int i = 0; i < amountVertical; i++) {
			for (int j = 0; j < amountHorizontal; j++) {
				int amountOfStudsLeft = j * amountOfStudsWorkPiece;
				int amountOfStudsBottom = 1 + i * amountOfStudsWorkPieceVertical;
				double xBottomLeft = getHorizontalPadding() + (amountOfStudsLeft) * horizontalHoleDistance + studDiameter/2;
				double yBottomLeft = getVerticalPaddingBottom() + (amountOfStudsBottom - 1)*verticalHoleDistance + studDiameter/2;
				float x = (float) xBottomLeft + dimensions.getWidth()/2;
				float y = (float) yBottomLeft + dimensions.getLength()/2;
				StackPlateStackingPosition stPos = new StackPlateStackingPosition(x, y, getR(getOrientation()), null, 0, getOrientation());
				getStackingPositions().add(stPos);
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
				double xBottomRight = getHorizontalPadding() + (amountOfStudsLeft + amountOfStudsWorkPiece - 1) * horizontalHoleDistance - studDiameter/2;
				if (amountHorizontal * amountOfStudsWorkPiece > horizontalHoleAmount) {
					// normally: overflow right, here: left
					xBottomRight = getHorizontalPadding() + (amountOfStudsLeft + amountOfStudsWorkPiece - 1 - (amountHorizontal * amountOfStudsWorkPiece - horizontalHoleAmount)) * horizontalHoleDistance - studDiameter/2;
				}
				double yBottomLeft = getVerticalPaddingBottom() + (amountOfStudsBottom - 1)*verticalHoleDistance + studDiameter/2;
				float x = (float) xBottomRight - dimensions.getWidth()/2;
				float y = (float) yBottomLeft + dimensions.getLength()/2;
				StackPlateStackingPosition stPos = new StackPlateStackingPosition(x, y, getR(getOrientation()), null, 0, WorkPieceOrientation.DEG90);
				getStackingPositions().add(stPos);
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
	
	private void initializeRawWorkPiecePositionsHorizontal(final WorkPieceDimensions dimensions, final int amountOfStudsWorkPiece,
			final int amountOfStudsWorkPieceVertical, final int amountHorizontal, final int amountVertical, 
				final boolean cornerLength, final boolean cornerWidth) {
		for (int i = 0; i < amountVertical; i++) {
			for (int j = 0; j < amountHorizontal; j++) {
				int amountOfStudsLeft = j * amountOfStudsWorkPiece;
				int amountOfStudsBottom = 1 + i * amountOfStudsWorkPieceVertical;
				double xBottomLeft = getHorizontalPadding() + (amountOfStudsLeft) * horizontalHoleDistance + studDiameter/2;
				double yBottomLeft = getVerticalPaddingBottom() + (amountOfStudsBottom - 1)*verticalHoleDistance + studDiameter/2;
				float x = (float) xBottomLeft + dimensions.getLength()/2;
				float y = (float) yBottomLeft + dimensions.getWidth()/2;
				StackPlateStackingPosition stPos = new StackPlateStackingPosition(x, y, getR(getOrientation()), null, 0, WorkPieceOrientation.HORIZONTAL);
				getStackingPositions().add(stPos);
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
					int maxTimes = (int) Math.floor((dimensions.getLength() + studDiameter/2 - minOverlap)/horizontalHoleDistance);
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
	
	private void initializeRawWorkPiecePositionsHorizontalRight(final WorkPieceDimensions dimensions, final int amountOfStudsWorkPiece,
			final int amountOfStudsWorkPieceVertical, final int amountHorizontal, final int amountVertical, 
				final boolean cornerLength, final boolean cornerWidth) {
		for (int i = 0; i < amountVertical; i++) {
			for (int j = 0; j < amountHorizontal; j++) {
				int amountOfStudsLeft = j * amountOfStudsWorkPiece;
				int amountOfStudsBottom = 1 + i * amountOfStudsWorkPieceVertical;
				double xBottomRight = getHorizontalPadding() + (amountOfStudsLeft + amountOfStudsWorkPiece - 1) * horizontalHoleDistance - studDiameter/2;
				if (amountHorizontal * amountOfStudsWorkPiece > horizontalHoleAmount) {
					// normally: overflow right, here: left
					xBottomRight = getHorizontalPadding() + (amountOfStudsLeft + amountOfStudsWorkPiece - 1 - (amountHorizontal * amountOfStudsWorkPiece - horizontalHoleAmount)) * horizontalHoleDistance - studDiameter/2;
				}
				double yBottomRight = getVerticalPaddingBottom() + (amountOfStudsBottom - 1)*verticalHoleDistance + studDiameter/2;
				float x = (float) xBottomRight - dimensions.getLength()/2;
				float y = (float) yBottomRight + dimensions.getWidth()/2;
				StackPlateStackingPosition stPos = new StackPlateStackingPosition(x, y, getR(getOrientation()), null, 0, WorkPieceOrientation.HORIZONTAL);
				getStackingPositions().add(stPos);
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
				if (!cornerLength) {
					boolean ok = false;
					int maxTimes = (int) Math.floor((dimensions.getLength() + studDiameter/2 - minOverlap)/horizontalHoleDistance);
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
			}
		}
	}

	private void initializeRawWorkPiecePositionsTilted(final WorkPieceDimensions dimensions, final int amountOfStudsLeftFirst,
			final int amountOfStudsLeftOther, final int amountHorizontal, final int amountOfStudsVertical, 
				final int amountVertical, final boolean cornerLength, final boolean cornerWidth) {
		double a = horizontalHoleDistance/(Math.sqrt(2)) - studDiameter/2;
		getStackingPositions().clear();
		for (int i = 0; i < amountVertical; i++) {
			for (int j = 0; j < amountHorizontal; j++) {
				int amountOfStudsLeft = amountOfStudsLeftFirst + j * amountOfStudsLeftOther;
				int amountOfStudsBottom = 1 + i * amountOfStudsVertical;
				double adjustment = (horizontalHoleDistance/2 - studDiameter/Math.sqrt(2));
				double xBottom = getHorizontalPadding() + (amountOfStudsLeft - 1)*horizontalHoleDistance + horizontalHoleDistance/2;
				double yBottom = getVerticalPaddingBottom() - adjustment + (amountOfStudsBottom - 1)*verticalHoleDistance;
				double extraX = (dimensions.getLength()/Math.sqrt(2) - dimensions.getWidth()/Math.sqrt(2))/2;
				double extraY = (dimensions.getLength()/Math.sqrt(2) + dimensions.getWidth()/Math.sqrt(2))/2;
				float x = (float) (xBottom + extraX);
				float y = (float) (yBottom + extraY);
				StackPlateStackingPosition stPos = new StackPlateStackingPosition(x, y, getR(getOrientation()), null, 0, WorkPieceOrientation.TILTED);
				getStackingPositions().add(stPos);
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
								getStackingPositions().remove(stPos);	// remove the last work piece as it can not be supported correctly
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

	private void initializeRawWorkPiecePositionsTiltedRight(final WorkPieceDimensions dimensions, final int amountOfStudsLeftFirst,
			final int amountOfStudsLeftOther, final int amountHorizontal, final int amountOfStudsVertical, 
				final int amountVertical, final boolean cornerLength, final boolean cornerWidth) {
		double a = horizontalHoleDistance/(Math.sqrt(2)) - studDiameter/2;
		getStackingPositions().clear();
		for (int i = 0; i < amountVertical; i++) {
			for (int j = 0; j < amountHorizontal; j++) {
				int amountOfStudsLeft = amountOfStudsLeftFirst + j * amountOfStudsLeftOther;
				int amountOfStudsBottom = 1 + i * amountOfStudsVertical;
				double adjustment = (horizontalHoleDistance/2 - studDiameter/Math.sqrt(2));
				double xBottom = getHorizontalPadding() + (amountOfStudsLeft - 1)*horizontalHoleDistance + horizontalHoleDistance/2;
				double yBottom = getVerticalPaddingBottom() - adjustment + (amountOfStudsBottom - 1)*verticalHoleDistance;
				double extraX = (dimensions.getLength()/Math.sqrt(2) - dimensions.getWidth()/Math.sqrt(2))/2;
				double extraY = (dimensions.getLength()/Math.sqrt(2) + dimensions.getWidth()/Math.sqrt(2))/2;
				float x = (float) (xBottom - extraX);
				float y = (float) (yBottom + extraY);
				StackPlateStackingPosition stPos = new StackPlateStackingPosition(x, y, getR(getOrientation()), null, 0, WorkPieceOrientation.TILTED);
				getStackingPositions().add(stPos);
				int firstStudPosX = amountOfStudsLeftFirst + j * amountOfStudsLeftOther - 1;
				int firstStudPosY = amountOfStudsVertical * i;
				StudPosition studPos = null;
				if (cornerLength || cornerWidth) {
					studPos = new StudPosition(firstStudPosX, firstStudPosY, studPositions[firstStudPosY][firstStudPosX].getCenterPosition(), StudType.TILTED_CORNER);
					stPos.addstud(studPos);
					// if the corner is not needed because of the length, we will add an extra stud for stability
				} else {
					if(firstStudPosX < studPositions[firstStudPosY].length-1) {
						studPos = new StudPosition(firstStudPosX, firstStudPosY, studPositions[firstStudPosY][firstStudPosX].getCenterPosition(), StudType.NORMAL);
						StudPosition studPos2 = new StudPosition(firstStudPosX + 1, firstStudPosY, studPositions[firstStudPosY][firstStudPosX + 1].getCenterPosition(), StudType.NORMAL);
						stPos.addstud(studPos);
						stPos.addstud(studPos2);
					}
					else {
						getStackingPositions().remove(stPos);
						break;
					}
					
				}
				if (!cornerLength) {
					boolean ok = false;
					int maxTimes = (int) Math.floor((dimensions.getLength() - a - minOverlap) / ((horizontalHoleDistance*2) * Math.sqrt(2)));
					while (!ok) {
						if (maxTimes <= 0) {
							ok = true;
							if (!cornerWidth) {
								getStackingPositions().remove(stPos);	// remove the last work piece as it can not be supported correctly
							}
						} else if ((0 <= (firstStudPosX - maxTimes * 2)) && (studPositions.length > (maxTimes + firstStudPosY))) {
							int positionX = firstStudPosX - maxTimes * 2;
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

	/**
	 * Try to place one given WorkPiece on a given position. In case a WorkPiece is already present on that
	 * position, the count will be incremented. Otherwise, if a finished workPiece is there, nothing will be done
	 *
	 * @param rawWorkPiece
	 * @param position
	 * @param maxNbOfPieces
	 * - the maximum number of pieces stacked on each other
	 * @return
	 */
	@Override
	protected boolean addOneWorkPiece(final WorkPiece workPiece, StackPlateStackingPosition position, int maxNbOfPieces, boolean overwrite) {
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
	
	public float getHorizontalPadding() {
		return this.horizontalPadding;
	}

	public void setHorizontalPadding(final float horizontalPadding) {
		this.horizontalPadding = horizontalPadding;
	}

	public float getVerticalPaddingTop() {
		return this.verticalPaddingTop;
	}

	public void setVerticalPaddingTop(final float verticalPaddingTop) {
		this.verticalPaddingTop = verticalPaddingTop;
	}

	public float getVerticalPaddingBottom() {
		return this.verticalPaddingBottom;
	}

	public void setVerticalPaddingBottom(final float verticalPaddingBottom) {
		this.verticalPaddingBottom = verticalPaddingBottom;
	}
	
	public float getTiltedR() {
		return this.tiltedR;
	}
	
	public void setTiltedR(float tiltedR) {
		this.tiltedR = tiltedR;
	}
	
	public float getHorizontalR() {
		return this.horizontalR;
	}
	
	public void setHorizontalR(float horizontalR) {
		this.horizontalR = horizontalR;
	}

	public float getR(WorkPieceOrientation orientation) {
		if(orientation.equals(WorkPieceOrientation.TILTED))
			return getTiltedR();
		else
			return getHorizontalR();
	}
}
