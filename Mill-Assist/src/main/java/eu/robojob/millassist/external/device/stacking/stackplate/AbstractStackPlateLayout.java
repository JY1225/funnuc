package eu.robojob.millassist.external.device.stacking.stackplate;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.robojob.millassist.external.device.stacking.IncorrectWorkPieceDataException;
import eu.robojob.millassist.external.device.stacking.pallet.Pallet;
import eu.robojob.millassist.process.ProcessFlowManager;
import eu.robojob.millassist.ui.RoboSoftAppFactory;
import eu.robojob.millassist.util.PropertyManager;
import eu.robojob.millassist.util.PropertyManager.Setting;
import eu.robojob.millassist.workpiece.IWorkPieceDimensions;
import eu.robojob.millassist.workpiece.RectangularDimensions;
import eu.robojob.millassist.workpiece.WorkPiece;
import eu.robojob.millassist.workpiece.WorkPiece.Dimensions;
import eu.robojob.millassist.workpiece.WorkPiece.Type;

/**
 * This class is the main class to represent a stackplate. A stackplate is defined as a plate which holds positions
 * where workpieces can be put upon.
 */
public abstract class AbstractStackPlateLayout {

    // General settings
    private float orientation;
    private int horizontalAmount;
    private int verticalAmount;
    private int layers;
    private float plateWidth;
    private float plateLength;
    private boolean alignRight;
    private AbstractStackPlate stackPlate;
    private Pallet pallet;

    private List<StackPlateStackingPosition> rawStackingPositions, finishedStackingPositions;
    // Actual stackingPositions used for workPiece placement
    private List<StackPlateStackingPosition> stackingPositions;

    private static Logger logger = LogManager.getLogger(AbstractStackPlateLayout.class.getName());

    protected AbstractStackPlateLayout() {
        setAlignRight();
        // Default values
        this.layers = 1;
        this.orientation = 0;
        this.rawStackingPositions = new ArrayList<StackPlateStackingPosition>();
        this.finishedStackingPositions = new ArrayList<StackPlateStackingPosition>();
        this.stackingPositions = new ArrayList<StackPlateStackingPosition>();
    }

    public void configureStackingPositions(final WorkPiece rawWorkPiece, final WorkPiece finishedWorkPiece,
            final float orientation, final int layers) throws IncorrectWorkPieceDataException {
        rawStackingPositions.clear();
        finishedStackingPositions.clear();
        stackingPositions.clear();
        setLayers(layers);
        if (rawWorkPiece != null) {
            IWorkPieceDimensions dimensions = rawWorkPiece.getDimensions();
            if ((dimensions != null) && dimensions.isValidDimension()) {
                configureStackingPositions(dimensions, orientation);
            } else {
                throw new IncorrectWorkPieceDataException(IncorrectWorkPieceDataException.INCORRECT_DATA);
            }
        }
    }

    private void configureStackingPositions(final IWorkPieceDimensions dimensions, final float orientation)
            throws IncorrectWorkPieceDataException {
        if (dimensions.getDimension(Dimensions.LENGTH) < dimensions.getDimension(Dimensions.WIDTH)) {
            throw new IncorrectWorkPieceDataException(IncorrectWorkPieceDataException.LENGTH_SMALLER_WIDTH);
        }
        checkSpecialStackingConditions((RectangularDimensions) dimensions, orientation);
        setOrientation(orientation);

        horizontalAmount = getMaxHorizontalAmount((RectangularDimensions) dimensions, orientation);
        verticalAmount = getMaxVerticalAmount((RectangularDimensions) dimensions, orientation);

        initStackingPositions(horizontalAmount, verticalAmount, (RectangularDimensions) dimensions, orientation);
        finishedStackingPositions.addAll(rawStackingPositions);
    }

    protected abstract void checkSpecialStackingConditions(final RectangularDimensions dimensions,
            final float orientation) throws IncorrectWorkPieceDataException;

    protected abstract int getMaxHorizontalAmount(final RectangularDimensions dimensions, final float orientation);

    protected abstract int getMaxVerticalAmount(final RectangularDimensions dimensions, final float orientation);

    protected abstract void initStackingPositions(int nbHorizontal, int nbVertical, RectangularDimensions dimensions,
            float orientation);

    /**
     * Remove all the workpieces from the stacking positions, making the plate empty again.
     */
    protected void resetStackingPositions() {
        getStackingPositions().addAll(rawStackingPositions);
        for (StackPlateStackingPosition stackingPos : getStackingPositions()) {
            stackingPos.setWorkPiece(null);
            stackingPos.setAmount(0);
        }
    }

    /**
     * Get the maximum amount of pieces possible to place on the stackPlate. In case of multiple layers, a place must be
     * foreseen to put the finished pieces (not on top of the raw ones). This means, that one of the many stacking
     * positions can have only 1 piece. In case of only one layer, this condition is trivial.
     *
     * @return - amount of layers * (amount of stacking positions - 1) + 1 extra piece
     */
    public int getMaxPiecesPossibleAmount() {
        // First position can only have 1 piece, because the robot must have place to put the finished products
        // TODO - kunnen we enkel finishedPositions hebben?
        if(usedForFinished()) {
            return (getLayers() * (rawStackingPositions.size() - 1)) + 1;
        } else {
            return getLayers() * rawStackingPositions.size();
        }
    }

    /**
     * Calculate the amount of workpieces of a certain type that is currently situated on the stackPlate.
     *
     * @param workPieceType
     *            (RAW or FINISHED)
     * @return - amount of pieces of the given type that is present on the stackPlate
     */
    public int getWorkPieceAmount(final Type workPieceType) {
        int amount = 0;
        for (StackPlateStackingPosition position : getStackingPositions()) {
            if ((position.hasWorkPiece()) && (position.getWorkPiece().getType().equals(workPieceType))) {
                amount += position.getAmount();
            }
        }
        return amount;
    }

    /**************************************
     * * WORKPIECE PLACEMENT * *
     **************************************/

    /**
     * Clear the stackPlate and add a given amount of raw workPieces.
     *
     * @param rawWorkPiece
     * @param amount
     * @throws IncorrectWorkPieceDataException
     */
    public void initRawWorkPieces(final WorkPiece rawWorkPiece, final int amount)
            throws IncorrectWorkPieceDataException {
        logger.debug("Placing raw workpieces: [" + amount + "].");
        resetStackingPositions();
        if (amount > 0) {
            if (amount <= getMaxPiecesPossibleAmount()) {
                placeRawWorkPieces(rawWorkPiece, amount, false, false);
            } else {
                logger.debug("Trying to place [" + amount + "] but maximum is [" + getMaxPiecesPossibleAmount() + "].");
                throw new IncorrectWorkPieceDataException(IncorrectWorkPieceDataException.INCORRECT_AMOUNT);
            }
        }
    }

    /**
     * Add a certain amount of raw workPieces to the stackPlate. The first place that is free, will be populated by a
     * workPiece.
     *
     * @param rawWorkPiece
     * @param amount
     * @param resetFirst
     *            - flag to indicate whether or not the first position can be changed by new pieces
     * @param isAddOperation
     *            - flag to indicate whether the function is called from the ADD/REPLACE function
     */
    public void placeRawWorkPieces(final WorkPiece rawWorkPiece, final int amount, final boolean resetFirst,
            boolean isAddOperation) {
        logger.debug("Adding raw workpieces: [" + amount + "].");
        int placedAmount = 0;
        int stackingPos = 0;
        if (getNumberOfStackingPositionsWithRawWorkPiece() > 0) {
            stackingPos = getStackingPositions().size() - 1;
            // For any number of layers, we only put 1 workPiece on the first position. This ensures that the robot
            // places finished products always at the same position

            if (isAddOperation) {
                placedAmount = fillAfterLastRaw(rawWorkPiece, amount);
                if (placedAmount != 0) {
                    isAddOperation = false;
                }
            }

            StackPlateStackingPosition stPos = getStackingPositions().get(stackingPos);
            if(usedForFinished()) {
                while ((placedAmount < amount) && (stackingPos != 0)) {
                    stPos = getStackingPositions().get(stackingPos);
                    while ((placedAmount < amount) && addOneWorkPiece(rawWorkPiece, stPos, getLayers(), false)) {
                        placedAmount++;
                    }
                    stackingPos--;
                }
                if((stackingPos == 0) && (placedAmount < amount) && placeFirstPiece(rawWorkPiece, resetFirst)) {
                    placedAmount++;
                }
            } else {
                while (placedAmount < amount) {
                    stPos = getStackingPositions().get(stackingPos);
                    while ((placedAmount < amount) && addOneWorkPiece(rawWorkPiece, stPos, getLayers(), false)) {
                        placedAmount++;
                    }
                    stackingPos--;
                }
            }
        } else {
            stackingPos = 0;
            if ((amount > 0) && usedForFinished() && placeFirstPiece(rawWorkPiece, resetFirst)) {
                placedAmount++;
            }
            if(usedForFinished()) {
                stackingPos++;
            }
            StackPlateStackingPosition stPos = getStackingPositions().get(0);
            while ((placedAmount < amount) && (stackingPos < getStackingPositions().size())) {
                stPos = getStackingPositions().get(stackingPos);
                while ((placedAmount < amount) && addOneWorkPiece(rawWorkPiece, stPos, getLayers(), false)) {
                    placedAmount++;
                }
                stackingPos++;
            }
        }

        // if (isAddOperation) {
        // transferFirstToLast(stackingPos-1);
        // }
    }

    private int fillAfterLastRaw(final WorkPiece rawWorkPiece, final int amount) {
        int placedAmount = 0;
        StackPlateStackingPosition position = null;
        int lastRawPosition = getStackingPositions().size() - 1;
        for (int i = getStackingPositions().size() - 1; i != 0; i--) {
            position = getStackingPositions().get(i);
            if ((position.getWorkPiece() != null) && position.getWorkPiece().getType().equals(WorkPiece.Type.RAW)) {
                lastRawPosition = i;
                if (position.getAmount() < getLayers()) {
                    lastRawPosition--;
                }
                break;
            }
        }
        if (lastRawPosition != (getStackingPositions().size() - 1)) {
            StackPlateStackingPosition stPos = null;
            while ((placedAmount < amount) && (lastRawPosition < (getStackingPositions().size() - 1))) {
                stPos = getStackingPositions().get(lastRawPosition + 1);
                while ((placedAmount < amount) && addOneWorkPiece(rawWorkPiece, stPos, getLayers(), false)) {
                    placedAmount++;
                }
                lastRawPosition++;
            }
        }
        return placedAmount;
    }

    /**
     * Add a certain amount of raw workPieces to the stackPlate. The first place that is free, will be populated by a
     * workPiece.
     *
     * @param rawWorkPiece
     * @param amount
     * @param resetFirst
     *            - flag to indicate whether or not the first position can be changed by new pieces
     * @param isAddOperation
     *            - flag to indicate whether the function is called from the ADD/REPLACE function
     */
    public void placeFinishedWorkPieces(final WorkPiece finishedWorkPiece, final int amount, final boolean resetFirst,
            final boolean isAddOperation) {
        logger.debug("Adding finished workpieces: [" + amount + "].");
        int placedAmount = 0;
        int stackingPos = 0;
        // For any number of layers, we only put 1 workPiece on the first position. This ensures that the robot
        // places finished products always at the same position
        if ((amount > 0) && placeFirstPiece(finishedWorkPiece, resetFirst)) {
            placedAmount++;
        }
        stackingPos++;
        StackPlateStackingPosition stPos = getStackingPositions().get(0);
        while ((placedAmount < amount) && (stackingPos < getStackingPositions().size())) {
            stPos = getStackingPositions().get(stackingPos);
            while ((placedAmount < amount) && addOneWorkPiece(finishedWorkPiece, stPos, getLayers(), false)) {
                placedAmount++;
            }
            stackingPos++;
        }
        // if (isAddOperation) {
        // transferFirstToLast(stackingPos-1);
        // }
    }

    /**
     * In case the last stack of pieces does not hold the maximum, we can try to transfer pieces from the first stack to
     * the last one. This ensures that there is always a position free to put finished pieces.
     *
     * @param lastPosition
     */
    private void transferFirstToLast(final int lastPosition) {
        // If the final stack of pieces (in case of multiple layers) does not hold the maximum, try to move pieces from
        // the
        // first raw stack to the last raw stack (min of first stack is always 1)
        if (getLayers() > 1) {
            StackPlateStackingPosition lastStackingPosition = getStackingPositions().get(lastPosition);
            StackPlateStackingPosition firstStackingPosition = null;
            if (lastStackingPosition.getAmount() < getLayers()) {
                int amountToTransfer1 = getLayers() - lastStackingPosition.getAmount();
                int amountToTransfer2 = 0;
                for (StackPlateStackingPosition stPlatePosition : getStackingPositions()) {
                    // Find the first stacking position that has raw workpieces
                    if ((stPlatePosition.getWorkPiece() != null)
                            && stPlatePosition.getWorkPiece().getType().equals(Type.RAW)) {
                        firstStackingPosition = stPlatePosition;
                        amountToTransfer2 = stPlatePosition.getAmount() - 1;
                        break;
                    }
                }
                int amountToTransfer = Math.min(amountToTransfer1, amountToTransfer2);
                if ((firstStackingPosition != null) && !firstStackingPosition.equals(getStackingPositions().get(0))) {
                    lastStackingPosition.setAmount(lastStackingPosition.getAmount() + amountToTransfer);
                    firstStackingPosition.setAmount(lastStackingPosition.getAmount() - amountToTransfer);
                }
            }
        }
    }

    private boolean placeFirstPiece(final WorkPiece workPiece, final boolean isEmptyPiece) {
        if (isEmptyPiece) {
            resetWorkPieceAt(0);
            return false;
        } else {
            return addOneWorkPiece(workPiece, getStackingPositions().get(0), 1, false);
        }
    }

    public void resetWorkPieceAt(final int positionIndex) {
        getStackingPositions().get(positionIndex).setWorkPiece(null);
        getStackingPositions().get(positionIndex).setAmount(0);
    }

    /**
     * Try to place one given WorkPiece on a given position. In case a WorkPiece is already present on that position,
     * the count will be incremented. Otherwise, if a finished workPiece is there, nothing will be done
     *
     * @param rawWorkPiece
     * @param position
     * @param maxNbOfPieces
     *            - the maximum number of pieces stacked on each other
     * @return
     */
    protected boolean addOneWorkPiece(final WorkPiece workPiece, final StackPlateStackingPosition position,
            final int maxNbOfPieces, final boolean overwrite) {
        if (position.hasWorkPiece()) {
            if (position.getWorkPiece().getType().equals(workPiece.getType())) {
                if (position.getAmount() < maxNbOfPieces) {
                    position.incrementAmount();
                    return true;
                }
                return false;
            }
            if (overwrite) {
                position.setWorkPiece(workPiece);
                position.setAmount(1);
                return true;
            }
            return false;
        } else {
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

    public int getFirstFinishedPosition() {
        int lastPiece = -1;
        for (StackPlateStackingPosition position : getStackingPositions()) {
            if ((position.hasWorkPiece()) && (position.getWorkPiece().getType().equals(WorkPiece.Type.FINISHED))) {
                return getStackingPositions().indexOf(position);
            } else if (position.hasWorkPiece()) {
                lastPiece = getStackingPositions().indexOf(position);
            }
        }
        return lastPiece;
    }

    public void removeFinishedFromTable() {
        for (StackPlateStackingPosition position : getStackingPositions()) {
            if (position.hasWorkPiece() && position.getWorkPiece().getType().equals(WorkPiece.Type.FINISHED)) {
                // Make position ready for raw pieces
                position.setWorkPiece(null);
                position.setAmount(0);
                int positionIdx = getStackingPositions().indexOf(position);
                getStackingPositions().set(positionIdx, getRawStackingPositions().get(positionIdx));
                position = getStackingPositions().get(positionIdx);
                position.setWorkPiece(null);
                position.setAmount(0);
            }
        }
    }

    /**
     * Removes the given number of finished work pieces from the pallet.
     *
     * @param amount
     *            The amount of finished work pieces that will be removed from the pallet
     */
    public void removeFinishedWorkPieces(final int amount, final WorkPiece finishedWorkPiece) {
        logger.debug("Removing finished workpieces: [" + amount + "].");
        int removedAmount = 0;
        int stackingPos = getFinishedStackingPositions().size() - 1;

        StackPlateStackingPosition stPos = getFinishedStackingPositions().get(stackingPos);
        while ((removedAmount < amount) && (stackingPos < getFinishedStackingPositions().size())) {
            stPos = getFinishedStackingPositions().get(stackingPos);
            while ((removedAmount < amount) && removeOneWorkPiece(stPos, finishedWorkPiece)) {
                removedAmount++;
            }
            stackingPos--;
        }
    }

    public int getNumberOfStackingPositionsWithRawWorkPiece() {
        int result = 0;
        for (StackPlateStackingPosition position : getStackingPositions()) {
            if ((position.getWorkPiece() != null) && position.getWorkPiece().getType().equals(WorkPiece.Type.RAW)) {
                result++;
            }
        }
        return result;
    }

    public int getNumberOfStackingPositionsWithFinishedWorkPiece() {
        int result = 0;
        for (StackPlateStackingPosition position : getStackingPositions()) {
            if ((position.getWorkPiece() != null) && position.getWorkPiece().getType().equals(WorkPiece.Type.FINISHED)) {
                result++;
            }
        }
        return result;
    }

    /**
     * Removes the given number of finished work pieces from the pallet.
     *
     * @param amount
     *            The amount of finished work pieces that will be removed from the pallet
     */
    public void removeRawWorkPieces(final int amount, final WorkPiece rawWorkPiece) {
        logger.debug("Removing raw workpieces: [" + amount + "].");
        int removedAmount = 0;
        int stackingPos = 0;

        StackPlateStackingPosition stPos = getRawStackingPositions().get(0);
        while ((removedAmount < amount) && (stackingPos < getRawStackingPositions().size())) {
            stPos = getRawStackingPositions().get(stackingPos);
            while ((removedAmount < amount) && removeOneWorkPiece(stPos, rawWorkPiece)) {
                removedAmount++;
            }
            stackingPos++;
        }
    }

    /**
     * Removes a single work piece from the pallet.
     *
     * @param position
     *            The position from which a work piece will be removed
     * @return A boolean indicating if the work piece is removed from that position on the pallet
     */
    private boolean removeOneWorkPiece(final StackPlateStackingPosition position, final WorkPiece workPiece) {
        if (position.hasWorkPiece() && position.getWorkPiece().getType().equals(workPiece.getType())) {
            if (position.getAmount() > 0) {
                position.decrementAmount();
                if (position.getAmount() == 0) {
                    position.setWorkPiece(null);
                }
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**************************************
     * * GETTERS AND SETTERS * *
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

    public void setOrientation(final float orientation) {
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

    public void setLayers(final int layers) {
        this.layers = layers;
    }

    public List<StackPlateStackingPosition> getStackingPositions() {
        return stackingPositions;
    }

    public void getStackingPositions(final List<StackPlateStackingPosition> stackingPositions) {
        this.stackingPositions = stackingPositions;
    }

    public List<StackPlateStackingPosition> getRawStackingPositions() {
        return rawStackingPositions;
    }

    public void setRawStackingPositions(final List<StackPlateStackingPosition> stackingPositions) {
        this.rawStackingPositions = stackingPositions;
    }

    public List<StackPlateStackingPosition> getFinishedStackingPositions() {
        return finishedStackingPositions;
    }

    public void setFinishedStackingPositions(final List<StackPlateStackingPosition> stackingPositions) {
        this.finishedStackingPositions = stackingPositions;
    }

    public boolean isRightAligned() {
        return this.alignRight;
    }

    public void setAlignRight() {
        alignRight = PropertyManager.hasSettingValue(Setting.ALIGN_RIGHT, "true");
    }

    public AbstractStackPlate getStackPlate() {
        return stackPlate;
    }

    public void setStackPlate(final AbstractStackPlate stackPlate) {
        this.stackPlate = stackPlate;
    }

    public Pallet getPallet() {
        return this.pallet;
    }

    public void setPallet(final Pallet pallet) {
        this.pallet = pallet;
    }

    public boolean usedForFinished() {
        boolean usedForFinished = true;
        if(getStackPlate() != null) {
            usedForFinished = RoboSoftAppFactory.getProcessFlow().hasBasicStackPlateForFinishedPieces();
        } else if(getPallet() != null) {
            usedForFinished = RoboSoftAppFactory.getProcessFlow().hasPalletForFinishedPieces();
        }
        return usedForFinished;
    }

}
