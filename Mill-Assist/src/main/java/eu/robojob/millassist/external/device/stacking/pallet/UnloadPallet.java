package eu.robojob.millassist.external.device.stacking.pallet;


import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.robojob.millassist.external.communication.AbstractCommunicationException;
import eu.robojob.millassist.external.device.Clamping;
import eu.robojob.millassist.external.device.ClampingManner;
import eu.robojob.millassist.external.device.DeviceActionException;
import eu.robojob.millassist.external.device.DevicePickSettings;
import eu.robojob.millassist.external.device.DevicePutSettings;
import eu.robojob.millassist.external.device.DeviceSettings;
import eu.robojob.millassist.external.device.EDeviceGroup;
import eu.robojob.millassist.external.device.SimpleWorkArea;
import eu.robojob.millassist.external.device.Zone;
import eu.robojob.millassist.external.device.stacking.IncorrectWorkPieceDataException;
import eu.robojob.millassist.external.device.visitor.AbstractPiecePlacementVisitor;
import eu.robojob.millassist.external.robot.AbstractRobotActionSettings.ApproachType;
import eu.robojob.millassist.positioning.Coordinates;
import eu.robojob.millassist.workpiece.IWorkPieceDimensions;
import eu.robojob.millassist.workpiece.RectangularDimensions;
import eu.robojob.millassist.workpiece.WorkPiece;
import eu.robojob.millassist.workpiece.WorkPiece.Material;
import eu.robojob.millassist.workpiece.WorkPiece.Type;

public class UnloadPallet extends AbstractPallet {

    private static Logger logger = LogManager.getLogger(UnloadPallet.class.getName());
    private PalletStackingPosition currentPutLocation;
    
    public UnloadPallet(String name, final Set<Zone> zones) {
        super(name, zones);
    }

    public UnloadPallet(String name) {
        this(name, new HashSet<Zone>());
    }

    /**
     * Recalculate the positions on this UnloadPallet.
     */
    public void recalculateLayout() {
        getPalletLayout().calculateLayoutForWorkPiece(getFinishedWorkPiece());
        getPalletLayout().initFinishedWorkPieces(getFinishedWorkPiece());
    }

    /**
     * Add an amount of work pieces to this UnloadPallet.
     * 
     * @param amount
     *            The amount of work pieces to add
     * @throws IncorrectWorkPieceDataException
     *             If a wrong work piece is added
     */
    public synchronized void addWorkPieces(final int amount) throws IncorrectWorkPieceDataException {
        placeFinishedWorkPieces(getFinishedWorkPiece(), amount, true);
        notifyLayoutChanged();
    }

    /**
     * Remove an amount of work pieces from this UnloadPallet.
     * 
     * @param amount
     *            The amount of work pieces to remove
     * @throws IncorrectWorkPieceDataException
     *             If a wrong work piece is removed
     */
    public synchronized void removeWorkPieces(final int amount) throws IncorrectWorkPieceDataException {
        removeFinishedWorkPieces(amount);
        notifyLayoutChanged();
    }
    
    /**
     * Manually add finished work pieces to the pallet.
     * 
     * @param finishedWorkPiece
     *            The finished work piece that will be added
     * @param amount
     *            The amount of finished work pieces that will be added
     * @param isAddOperation
     *            Boolean indicating whether this is an add operation
     */
    public void placeFinishedWorkPieces(final WorkPiece finishedWorkPiece, final int amount, boolean isAddOperation) {
        logger.debug("Adding finished workpieces: [" + amount + "].");
        int placedAmount = 0;
        int stackingPos = 0;
        // For any number of layers, we only put 1 workPiece on the first position. This ensures that the robot
        // places finished products always at the same position
        PalletStackingPosition stPos = getPalletLayout().getStackingPositions().get(0);
        while (placedAmount < amount) {
            if (stackingPos == getPalletLayout().getStackingPositions().size()) {
                stackingPos = 0;
            }
            stPos = getPalletLayout().getStackingPositions().get(stackingPos);
            while (placedAmount < amount && addOneWorkPiece(finishedWorkPiece, stPos)) {
                placedAmount++;
            }
            stackingPos++;
        }
        if (isAddOperation) {
            transferFirstToLast(stackingPos - 1, finishedWorkPiece);
        }
    }

    /**
     * Transfer the work piece in the first position to the last position if possible.
     * 
     * @param lastPosition
     *            Index of the last position to which the first work piece will be transfered
     */
    private void transferFirstToLast(int lastPosition, WorkPiece workPiece) {
        // If the final stack of pieces (in case of multiple layers) does not hold the maximum, try to move pieces from
        // the
        // first raw stack to the last raw stack (min of first stack is always 1)
        if (getLayers() > 1) {
            PalletStackingPosition lastStackingPosition = getPalletLayout().getStackingPositions().get(lastPosition);
            PalletStackingPosition firstStackingPosition = null;
            if (lastStackingPosition.getAmount() < getLayers()) {
                int amountToTransfer1 = getLayers() - lastStackingPosition.getAmount();
                int amountToTransfer2 = 0;
                for (PalletStackingPosition stPlatePosition : getPalletLayout().getStackingPositions()) {
                    // Find the first stacking position that has raw workpieces
                    if (stPlatePosition.getWorkPiece() != null
                            && stPlatePosition.getWorkPiece().getType().equals(Type.RAW)) {
                        firstStackingPosition = stPlatePosition;
                        amountToTransfer2 = stPlatePosition.getAmount() - 1;
                        break;
                    }
                }
                int amountToTransfer = Math.min(amountToTransfer1, amountToTransfer2);
                if (firstStackingPosition != null
                        && !firstStackingPosition.equals(getPalletLayout().getStackingPositions().get(0))) {
                    lastStackingPosition.setAmount(lastStackingPosition.getAmount() + amountToTransfer);
                    firstStackingPosition.setAmount(lastStackingPosition.getAmount() - amountToTransfer);
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UnloadPalletDeviceSettings getDeviceSettings() {
        return new UnloadPalletDeviceSettings(getFinishedWorkPiece(), getPalletLayout().getLayoutType(), getPalletLayout()
                .getLayersBeforeCardBoard(), getPalletLayout(), getPalletLayout().getCardBoardThickness());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Coordinates getLocationOrientation(SimpleWorkArea workArea, ClampingManner clampType) {
        Coordinates c = new Coordinates(getPalletLayout().getStackingPositions().get(0).getPosition());
        c.offset(workArea.getDefaultClamping().getRelativePosition());
        c.setX(0);
        c.setY(0);
        c.setZ(0);
        return c;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EDeviceGroup getType() {
        return EDeviceGroup.UNLOAD_PALLET;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void clearDeviceSettings() {
        setRawWorkPiece(new WorkPiece(WorkPiece.Type.RAW, new RectangularDimensions(), Material.OTHER, 0.0f));
        setFinishedWorkPiece(new WorkPiece(WorkPiece.Type.FINISHED, new RectangularDimensions(), Material.OTHER, 0.0f));
        getWorkAreas().get(0).getDefaultClamping().resetHeightToDefault();
        getPalletLayout().calculateLayoutForWorkPiece(getFinishedWorkPiece());
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends IWorkPieceDimensions> Coordinates getPutLocation(AbstractPiecePlacementVisitor<T> visitor,
            SimpleWorkArea workArea, T dimensions, ClampingManner clampType, ApproachType approachType) {
        return visitor.getPutLocation(this, workArea, dimensions, clampType, approachType);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends IWorkPieceDimensions> Coordinates getLocation(AbstractPiecePlacementVisitor<T> visitor,
            SimpleWorkArea workArea, Type type, ClampingManner clampType) {
        return visitor.getLocation(this, workArea, type, clampType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadDeviceSettings(DeviceSettings deviceSettings) {
        for (Entry<SimpleWorkArea, Clamping> entry : deviceSettings.getClampings().entrySet()) {
            entry.getKey().setDefaultClamping(entry.getValue());
        }
        if (deviceSettings instanceof UnloadPalletDeviceSettings) {
            UnloadPalletDeviceSettings settings = (UnloadPalletDeviceSettings) deviceSettings;
            setFinishedWorkPiece(settings.getFinishedWorkPiece());
            setPalletLayout(settings.getLayout());
            if (getPalletLayout() != null) {
                getPalletLayout().setLayoutType(settings.getLayoutType());
                getPalletLayout().setLayersBeforeCardBoard(settings.getLayersBeforeCardBoard());
                getPalletLayout().calculateLayoutForWorkPiece(getFinishedWorkPiece());
                getPalletLayout().initFinishedWorkPieces(getFinishedWorkPiece());
                getPalletLayout().setCardBoardThickness(settings.getCardBoardThickness());
                notifyLayoutChanged();
            }  
        } else {
            throw new IllegalArgumentException("Unknown device settings");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canPut(DevicePutSettings putSettings) throws AbstractCommunicationException, DeviceActionException,
            InterruptedException {
        for (PalletStackingPosition stackingPos : getPalletLayout().getStackingPositions()) {
            if (stackingPos.getAmount() < getLayers()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Add one work piece to the pallet.
     * 
     * @param workPiece
     *            The work piece that will be added
     * @param position
     *            The position on which the work piece will be added
     * @param maxNbOfPieces
     *            The maximum number of work pieces
     * @return Boolean indicating that the work piece is added to the PalletStackingPosition
     */
    private boolean addOneWorkPiece(final WorkPiece workPiece, PalletStackingPosition position) {
        if (position.hasWorkPiece()) {
            if (position.getWorkPiece().getType().equals(workPiece.getType())) {
                if (getWorkPieceAmount(WorkPiece.Type.FINISHED) >= position.getAmount() * getMaxPiecesPerLayerAmount()) {
                    position.incrementAmount();
                    return true;
                }
                return false;
            }
            return false;
        } else {
            position.setWorkPiece(workPiece);
            position.setAmount(1);
            return true;
        }
    }

    public int getLayers() {
        int maxLayers = (int) Math.floor((maxHeight - getPalletLayout().getPalletHeight())
                / getFinishedWorkPiece().getDimensions().getZSafe());
        if (getPalletLayout().getLayersBeforeCardBoard() == 0) {
            return maxLayers;
        }
        float cardboardHeight = (float) (Math.floor(maxLayers / getPalletLayout().getLayersBeforeCardBoard()) * getPalletLayout()
                .getCardBoardThickness());
        while (getFinishedWorkPiece().getDimensions().getZSafe() * maxLayers + cardboardHeight
                + getPalletLayout().getPalletHeight() > maxHeight) {
            maxLayers--;
            cardboardHeight = (float) (Math.floor(maxLayers / getPalletLayout().getLayersBeforeCardBoard()) * getPalletLayout()
                    .getCardBoardThickness());
        }
        return maxLayers;
    }

    public int getMaxPiecesPossibleAmount() {
        return getLayers() * getPalletLayout().getStackingPositions().size();
    }

    public int getMaxPiecesPerLayerAmount() {
        return getPalletLayout().getStackingPositions().size();
    }

    /**
     * Get the number of work pieces for the given type
     * 
     * @param type
     *            The type of work piece from which the amount is requested
     * @return The number of work pieces of the given type on the pallet
     */
    public int getWorkPieceAmount(WorkPiece.Type type) {
        if (type == WorkPiece.Type.FINISHED) {
            int result = 0;
            for (PalletStackingPosition position : getPalletLayout().getStackingPositions()) {
                result += position.getAmount();
            }
            return result;
        }
        return 0;
    }

    /**
     * Removes the given number of finished work pieces from the pallet.
     * 
     * @param amount
     *            The amount of finished work pieces that will be removed from the pallet
     */
    public void removeFinishedWorkPieces(final int amount) {
        logger.debug("Removing finished workpieces: [" + amount + "].");
        int removedAmount = 0;
        int stackingPos = getPalletLayout().getStackingPositions().size() -1;

        PalletStackingPosition stPos = getPalletLayout().getStackingPositions().get(stackingPos);
        while (removedAmount < amount && stackingPos > -1) {
            stPos = getPalletLayout().getStackingPositions().get(stackingPos);
            while (removedAmount < amount && removeOneWorkPiece(stPos)) {
                removedAmount++;
            }
            stackingPos--;
        }
    }

    /**
     * Removes a single work piece from the pallet.
     * 
     * @param position
     *            The position from which a work piece will be removed
     * @return A boolean indicating if the work piece is removed from that position on the pallet
     */
    private boolean removeOneWorkPiece(PalletStackingPosition position) {
        if (position.hasWorkPiece()) {
            if (position.getAmount() > 0) {
                position.decrementAmount();
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
    
    public PalletStackingPosition getCurrentPutLocation() {
        return currentPutLocation;
    }

    public void setCurrentPutLocation(PalletStackingPosition currentPutLocation) {
        this.currentPutLocation = currentPutLocation;
    }
    

    /**
     * {@inheritDoc}
     */
    @Override
    public void pickFinished(DevicePickSettings pickSettings, int processId) throws AbstractCommunicationException,
            DeviceActionException, InterruptedException {
        // Cannot pick
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void putFinished(DevicePutSettings putSettings) throws AbstractCommunicationException,
            DeviceActionException, InterruptedException {
        currentPutLocation.setWorkPiece(getFinishedWorkPiece());
        currentPutLocation.incrementAmount();
        currentPutLocation = null;
        notifyLayoutChanged();
        logger.info("put finished!");
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends IWorkPieceDimensions> Coordinates getPickLocation(AbstractPiecePlacementVisitor<T> visitor,
            SimpleWorkArea workArea, T dimensions, ClampingManner clampType, ApproachType approachType) {
        // TODO Auto-generated method stub
        return null;
    }
    

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canPick(DevicePickSettings pickSettings) throws AbstractCommunicationException,
            DeviceActionException {
        return false;
    }
}
