package eu.robojob.millassist.external.device.stacking.stackplate;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.robojob.millassist.external.device.stacking.IncorrectWorkPieceDataException;
import eu.robojob.millassist.util.PropertyManager;
import eu.robojob.millassist.util.PropertyManager.Setting;
import eu.robojob.millassist.workpiece.WorkPiece;
import eu.robojob.millassist.workpiece.WorkPiece.Type;
import eu.robojob.millassist.workpiece.WorkPieceDimensions;

/**
 * This class is the main class to represent a stackplate. A stackplate is defined as a plate which holds positions where 
 * workpieces can be put upon. 
 */
public abstract class AbstractStackPlateLayout {

	//General settings
	private float orientation;
	private int horizontalAmount;
	private int verticalAmount;
	private int layers;
	private float plateWidth;
	private float plateLength;
	private boolean alignRightVertical;
	private boolean alignRightHorizontal;
	private AbstractStackPlate stackPlate;
	
	private List<StackPlateStackingPosition> stackingPositions;
	
	private static Logger logger = LogManager.getLogger(AbstractStackPlateLayout.class.getName());
	
	protected AbstractStackPlateLayout() {
		setAlignRight();
		//Default values
		this.layers = 1;
		this.orientation = 0;
		this.stackingPositions = new ArrayList<StackPlateStackingPosition>();
	}
	
	public void configureStackingPositions(final WorkPiece rawWorkPiece, final float orientation, final int layers) throws IncorrectWorkPieceDataException {
		stackingPositions.clear();
		setLayers(layers);
		if(rawWorkPiece != null) {
			WorkPieceDimensions dimensions = rawWorkPiece.getDimensions();
			if(dimensions != null && dimensions.isValidDimension()) {
				configureStackingPositions(dimensions, orientation);
			} else {
				throw new IncorrectWorkPieceDataException(IncorrectWorkPieceDataException.INCORRECT_DATA);
			}
		}
	}
	
	private void configureStackingPositions(final WorkPieceDimensions dimensions, final float orientation) throws IncorrectWorkPieceDataException {
		if (dimensions.getLength() < dimensions.getWidth()) {
			throw new IncorrectWorkPieceDataException(IncorrectWorkPieceDataException.LENGTH_SMALLER_WIDTH);
		}
		checkSpecialStackingConditions(dimensions, orientation);
		setOrientation(orientation);
		
		horizontalAmount = getMaxHorizontalAmount(dimensions, orientation);
		verticalAmount = getMaxVerticalAmount(dimensions, orientation);

		initStackingPositions(horizontalAmount, verticalAmount, dimensions, orientation);
	}
	
	protected abstract void checkSpecialStackingConditions(final WorkPieceDimensions dimensions, final float orientation)  throws IncorrectWorkPieceDataException;
	
	protected abstract int getMaxHorizontalAmount(final WorkPieceDimensions dimensions, final float orientation);
	
	protected abstract int getMaxVerticalAmount(final WorkPieceDimensions dimensions, final float orientation);
	
	protected abstract void initStackingPositions(int nbHorizontal, int nbVertical, WorkPieceDimensions dimensions, float orientation);
	
	/**
	 * Remove all the workpieces from the stacking positions, making the plate empty again.
	 */
	protected void resetStackingPositions() {
		for (StackPlateStackingPosition stackingPos : getStackingPositions()) {
			stackingPos.setWorkPiece(null);
			stackingPos.setAmount(0);
		}
	}
	
	/**
	 * Get the maximum amount of pieces possible to place on the stackPlate. In case of multiple layers, a place must be 
	 * foreseen to put the finished pieces (not on top of the raw ones). This means, that one of the many stacking positions
	 * can have only 1 piece. In case of only one layer, this condition is trivial.
	 * 
	 * @return 
	 * 		- amount of layers * (amount of stacking positions - 1) + 1 extra piece
	 */
	public int getMaxPiecesPossibleAmount() {
		//First position can only have 1 piece, because the robot must have place to put the finished products
		return getLayers() * (stackingPositions.size() - 1) + 1;
	}
	
	/**
	 * Calculate the amount of workpieces of a certain type that is currently situated on the stackPlate.
	 * 
	 * @param workPieceType (RAW or FINISHED)
	 * @return
	 * 		- amount of pieces of the given type that is present on the stackPlate
	 */
	public int getWorkPieceAmount(Type workPieceType) {
		int amount = 0;
		for (StackPlateStackingPosition position : getStackingPositions()) {
			if ((position.hasWorkPiece()) && (position.getWorkPiece().getType().equals(workPieceType))) {
				amount += position.getAmount();
			}
		}
		return amount;
	}
	
	/**************************************
	 *                                    *
	 *       WORKPIECE PLACEMENT          *
	 *                                    *
	 **************************************/
	
	/**
	 * Clear the stackPlate and add a given amount of raw workPieces.
	 *
	 * @param rawWorkPiece
	 * @param amount
	 * @throws IncorrectWorkPieceDataException
	 */
	public void initRawWorkPieces(final WorkPiece rawWorkPiece, final int amount) throws IncorrectWorkPieceDataException {
		logger.debug("Placing raw workpieces: [" + amount + "].");
		resetStackingPositions();
		if(amount <= getMaxPiecesPossibleAmount()) {
			placeRawWorkPieces(rawWorkPiece, amount, false, false);
		} else {
			logger.debug("Trying to place [" + amount + "] but maximum is [" + getMaxPiecesPossibleAmount() + "].");
			throw new IncorrectWorkPieceDataException(IncorrectWorkPieceDataException.INCORRECT_AMOUNT);
		}
	}

	/**
	 * Add a certain amount of raw workPieces to the stackPlate. The first place that is free, will be populated by
	 * a workPiece.
	 *
	 * @param rawWorkPiece
	 * @param amount
	 * @param resetFirst - flag to indicate whether or not the first position can be changed by new pieces
	 * @param isAddOperation - flag to indicate whether the function is called from the ADD/REPLACE function
	 */
	public void placeRawWorkPieces(final WorkPiece rawWorkPiece, final int amount, boolean resetFirst, boolean isAddOperation) {
		logger.debug("Adding raw workpieces: [" + amount + "].");
		int placedAmount = 0;
		int stackingPos = 0;
		StackPlateStackingPosition stPos = getStackingPositions().get(0);
		//For any number of layers, we only put 1 workPiece on the first position. This ensures that the robot
		//places finished products always at the same position
		if(amount > 0 && placeFirstPiece(rawWorkPiece, resetFirst)) {
			placedAmount++;
		}
		stackingPos++;
		while (placedAmount < amount && stackingPos < getStackingPositions().size()) {
			stPos = getStackingPositions().get(stackingPos);
			while(placedAmount < amount && addOneWorkPiece(rawWorkPiece, stPos,getLayers(), false) ) {
				placedAmount++;
			}
			stackingPos++;
		}
		if (isAddOperation) {
			transferLastToFirst(stackingPos-1);
		}
	}
	
	public void transferLastToFirst(int lastPosition) {
		//If the final stack of pieces (in case of multiple layers) does not hold the maximum, try to move pieces from the 
		//first raw stack to the last raw stack (min of first stack is always 1)
		if (getLayers() > 1) {
			StackPlateStackingPosition lastStackingPosition = getStackingPositions().get(lastPosition);
			StackPlateStackingPosition firstStackingPosition = null;
			if (lastStackingPosition.getAmount() < getLayers()) {
				int amountToTransfer1 = getLayers() - lastStackingPosition.getAmount();
				int amountToTransfer2 = 0;
				for (StackPlateStackingPosition stPlatePosition: getStackingPositions()) {
					if (stPlatePosition.getWorkPiece().getType().equals(Type.RAW)) {
						firstStackingPosition = stPlatePosition;
						amountToTransfer2 = stPlatePosition.getAmount() - 1;
						break;
					}
				}
				int amountToTransfer = Math.min(amountToTransfer1, amountToTransfer2);
				if (firstStackingPosition != null && !firstStackingPosition.equals(getStackingPositions().get(0))) {
					lastStackingPosition.setAmount(lastStackingPosition.getAmount() + amountToTransfer);
					firstStackingPosition.setAmount(lastStackingPosition.getAmount() - amountToTransfer);
				}
			}
		} 
	}
	
	private boolean placeFirstPiece(final WorkPiece workPiece, boolean isEmptyPiece) {
		if(isEmptyPiece) {
			resetWorkPieceAt(0);
			return false;
		} else {
			return addOneWorkPiece(workPiece, getStackingPositions().get(0),1, false);
		}
	}
	
	public void resetWorkPieceAt(int positionIndex) {
		getStackingPositions().get(positionIndex).setWorkPiece(null);
		getStackingPositions().get(positionIndex).setAmount(0);	
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
			position.setWorkPiece(workPiece);
			position.setAmount(1);
			return true;
		}
	}
	
	public int getFirstRawPosition() {
		int lastPiece = -1;
		for (StackPlateStackingPosition position : getStackingPositions()) {
			if ((position.hasWorkPiece()) && (position.getWorkPiece().getType().equals(WorkPiece.Type.RAW))) {
				return getStackingPositions().indexOf(position);
			} else if (position.hasWorkPiece()) {
				lastPiece = getStackingPositions().indexOf(position);
			}
		}
		return lastPiece;
	}	
	
	public void removeFinishedFromTable() {
		for (StackPlateStackingPosition position : getStackingPositions()) {
			if(position.hasWorkPiece() && position.getWorkPiece().getType().equals(WorkPiece.Type.FINISHED)) {
				position.setWorkPiece(null);
				position.setAmount(0);
			}
		}
	}
	
	/**************************************
	 *                                    *
	 *        GETTERS AND SETTERS         *
	 *                                    *
	 **************************************/
	public float getPlateWidth() {
		return this.plateWidth;
	}
	
	protected abstract void calcPlateWidth();
	
	protected void setPlateWidth(final float width) {
		this.plateWidth = width;
	}
	
	public float getPlateLength() {
		return this.plateLength;
	}
	
	protected abstract void calcPlateLength();
	
	protected void setPlateLength(final float length) {
		this.plateLength = length;
	}

	public float getOrientation() {
		return this.orientation;
	}

	public void setOrientation(float orientation) {
		this.orientation = orientation;
	}

	public int getHorizontalAmount() {
		return this.horizontalAmount;
	}

	public void setHorizontalAmount(final int horizontalAmount) {
		this.horizontalAmount = horizontalAmount;
	}

	public int getVerticalAmount() {
		return this.verticalAmount;
	}

	public void setVerticalAmount(final int verticalAmount) {
		this.verticalAmount = verticalAmount;
	}

	public int getLayers() {
		return this.layers;
	}

	public void setLayers(int layers) {
		this.layers = layers;
	}

	public List<StackPlateStackingPosition> getStackingPositions() {
		return stackingPositions;
	}

	public void setStackingPositions(List<StackPlateStackingPosition> stackingPositions) {
		this.stackingPositions = stackingPositions;
	}
	
	public boolean isRightAlignedVertical() {
		return this.alignRightVertical;
	}
	
	public boolean isRightAlignedHorizontal() {
		return this.alignRightHorizontal;
	}
	
	public void setAlignRight() {
		alignRightVertical = PropertyManager.hasSettingValue(Setting.ALIGN_RIGHT_VERTICAL, "true");
		alignRightHorizontal = PropertyManager.hasSettingValue(Setting.ALIGN_RIGHT_HORIZONTAL, "true");
	}

	public AbstractStackPlate getStackPlate() {
		return stackPlate;
	}

	public void setStackPlate(AbstractStackPlate stackPlate) {
		this.stackPlate = stackPlate;
	}
	
}
