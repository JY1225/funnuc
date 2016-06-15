package eu.robojob.millassist.external.device.stacking.stackplate;

import java.util.Map.Entry;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.robojob.millassist.external.communication.AbstractCommunicationException;
import eu.robojob.millassist.external.device.Clamping;
import eu.robojob.millassist.external.device.ClampingManner;
import eu.robojob.millassist.external.device.DeviceActionException;
import eu.robojob.millassist.external.device.DeviceInterventionSettings;
import eu.robojob.millassist.external.device.DevicePickSettings;
import eu.robojob.millassist.external.device.DevicePutSettings;
import eu.robojob.millassist.external.device.DeviceSettings;
import eu.robojob.millassist.external.device.SimpleWorkArea;
import eu.robojob.millassist.external.device.Zone;
import eu.robojob.millassist.external.device.stacking.AbstractStackingDevice;
import eu.robojob.millassist.external.device.stacking.IncorrectWorkPieceDataException;
import eu.robojob.millassist.external.device.stacking.stackplate.gridplate.GridPlateLayout;
import eu.robojob.millassist.external.device.visitor.AbstractPiecePlacementVisitor;
import eu.robojob.millassist.external.robot.AbstractRobotActionSettings.ApproachType;
import eu.robojob.millassist.positioning.Coordinates;
import eu.robojob.millassist.process.ProcessFlow;
import eu.robojob.millassist.workpiece.IWorkPieceDimensions;
import eu.robojob.millassist.workpiece.WorkPiece.Material;
import eu.robojob.millassist.workpiece.WorkPiece.Type;
import eu.robojob.millassist.workpiece.WorkPiece;
import eu.robojob.millassist.workpiece.RectangularDimensions;

public abstract class AbstractStackPlate extends AbstractStackingDevice {

    public enum UnloadType {
        LAYERWISE, STACKWISE
    }

    private AbstractStackPlateLayout layout;
    private StackPlateStackingPosition currentPickLocation;
    private StackPlateStackingPosition currentPutLocation;

    private UnloadType unloadType;
    private int currentLayer;

    private static Logger logger = LogManager.getLogger(AbstractStackPlate.class.getName());

    public AbstractStackPlate(final String name, final Set<Zone> zones) {
        super(name, zones);
        this.currentPickLocation = null;
        this.currentPutLocation = null;
    }

    public AbstractStackPlateLayout getLayout() {
        return this.layout;
    }

    public void setLayout(final AbstractStackPlateLayout layout) {
        this.layout = layout;
        layout.setStackPlate(this);
    }

    @Override
    public void clearDeviceSettings() {
        setRawWorkPiece(new WorkPiece(WorkPiece.Type.RAW, new RectangularDimensions(), Material.OTHER, 0.0f));
        setFinishedWorkPiece(new WorkPiece(WorkPiece.Type.FINISHED, new RectangularDimensions(), Material.OTHER, 0.0f));
        getWorkAreas().get(0).getDefaultClamping().resetHeightToDefault();
        this.currentPickLocation = null;
        this.currentPutLocation = null;
        try {
            this.getLayout().configureStackingPositions(null, null, getLayout().getOrientation(), 1);
        } catch (IncorrectWorkPieceDataException e) {
            logger.error(e);
        }
        notifyLayoutChanged();
    }

    @Override
    public void prepareForProcess(final ProcessFlow process)
            throws AbstractCommunicationException, InterruptedException {

    }

    @Override
    public boolean canPick(final DevicePickSettings pickSettings) throws AbstractCommunicationException, DeviceActionException {
        for (StackPlateStackingPosition stackingPos : getLayout().getStackingPositions()) {
            if ((stackingPos.getWorkPiece() != null) && (stackingPos.getWorkPiece().getType() == Type.RAW)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean canPut(final DevicePutSettings putSettings) throws AbstractCommunicationException, DeviceActionException, InterruptedException {
        for (StackPlateStackingPosition stackingPos : getLayout().getStackingPositions()) {
            if (stackingPos.getAmount() < getLayout().getLayers()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean canIntervention(final DeviceInterventionSettings interventionSettings) throws AbstractCommunicationException, DeviceActionException {
        return true;
    }

    @Override
    public void prepareForPick(final DevicePickSettings pickSettings, final int processId)
            throws AbstractCommunicationException, DeviceActionException,
            InterruptedException {

    }

    @Override
    public void prepareForPut(final DevicePutSettings putSettings, final int processId)
            throws AbstractCommunicationException, DeviceActionException,
            InterruptedException {

    }

    @Override
    public void prepareForIntervention(
            final DeviceInterventionSettings interventionSettings)
                    throws AbstractCommunicationException, DeviceActionException,
                    InterruptedException {

    }

    @Override
    public synchronized void pickFinished(final DevicePickSettings pickSettings, final int processId) {
        currentPickLocation.setAmount(currentPickLocation.getAmount() - 1);
        if (currentPickLocation.getAmount() == 0) {
            currentPickLocation.setWorkPiece(null);
        }
        currentPickLocation = null;
        notifyLayoutChanged();
        logger.info("pick finished!!");
    }

    @Override
    public synchronized void putFinished(final DevicePutSettings putSettings) {
        currentPutLocation.setWorkPiece(getFinishedWorkPiece());
        currentPutLocation.setAmount(currentPutLocation.getAmount() + 1);
        currentPutLocation = null;
        notifyLayoutChanged();
        logger.info("put finished!");
    }

    @Override
    public void interventionFinished(
            final DeviceInterventionSettings interventionSettings)
                    throws AbstractCommunicationException, DeviceActionException,
                    InterruptedException {

    }

    @Override
    public void releasePiece(final DevicePickSettings pickSettings)
            throws AbstractCommunicationException, DeviceActionException,
            InterruptedException {

    }

    @Override
    public void grabPiece(final DevicePutSettings putSettings)
            throws AbstractCommunicationException, DeviceActionException,
            InterruptedException {

    }

    @Override
    public void reset() throws AbstractCommunicationException,
    DeviceActionException, InterruptedException {

    }

    @Override
    public synchronized void loadDeviceSettings(final DeviceSettings deviceSettings) {
        for (Entry<SimpleWorkArea, Clamping> entry : deviceSettings.getClampings().entrySet()) {
            entry.getKey().setDefaultClamping(entry.getValue());
        }
        if (deviceSettings instanceof AbstractStackPlateDeviceSettings) {
            AbstractStackPlateDeviceSettings settings = (AbstractStackPlateDeviceSettings) deviceSettings;
            resetCurrentPickLocation();
            resetCurrentPutLocation();
            getWorkAreas().get(0).getDefaultClamping().setHeight(settings.getStudHeight());
            try {
                if (settings.getRawWorkPiece() != null) {
                    setRawWorkPiece(settings.getRawWorkPiece());
                    setFinishedWorkPiece(settings.getFinishedWorkPiece());
                    getLayout().configureStackingPositions(settings.getRawWorkPiece(), settings.getFinishedWorkPiece(), settings.getOrientation(), settings.getLayers());
                    getLayout().initRawWorkPieces(getRawWorkPiece(), settings.getAmount());
                } else {
                    logger.info("Raw workpiece was null!");
                    setRawWorkPiece(settings.getRawWorkPiece());
                    setFinishedWorkPiece(settings.getFinishedWorkPiece());
                    // Raw workpiece is null
                    getLayout().configureStackingPositions(settings.getRawWorkPiece(), settings.getFinishedWorkPiece(), settings.getOrientation(), settings.getLayers());
                }
            } catch (IncorrectWorkPieceDataException e) {
                logger.error(e);
            }
        } else {
            throw new IllegalArgumentException("Unknown device settings");
        }
        notifyLayoutChanged();
    }

    @Override
    public AbstractStackPlateDeviceSettings getDeviceSettings() {
        int gridId = 0;
        if(layout instanceof GridPlateLayout) {
            gridId = ((GridPlateLayout) layout).getGridPlate().getId();
        }
        return new AbstractStackPlateDeviceSettings(getRawWorkPiece(), getFinishedWorkPiece(), getLayout().getOrientation(), getLayout().getLayers(),
                getLayout().getWorkPieceAmount(WorkPiece.Type.RAW), getWorkAreas().get(0).getDefaultClamping().getHeight(), gridId);
    }

    public synchronized void setCurrentPickLocation(final StackPlateStackingPosition stackingPos) {
        this.currentPickLocation = stackingPos;
    }

    public synchronized StackPlateStackingPosition getCurrentPickLocation() {
        return currentPickLocation;
    }

    public synchronized void setCurrentPutLocation(final StackPlateStackingPosition stackingPos) {
        this.currentPutLocation = stackingPos;
    }

    public synchronized StackPlateStackingPosition getCurrentPutLocation() {
        return currentPutLocation;
    }

    protected synchronized void resetCurrentPickLocation() {
        this.currentPickLocation = null;
    }

    protected synchronized void resetCurrentPutLocation() {
        this.currentPutLocation = null;
    }

    @Override
    public Coordinates getLocationOrientation(final SimpleWorkArea workArea, final ClampingManner clampType) {
        Coordinates c = new Coordinates(getLayout().getStackingPositions().get(0).getPosition());
        c.offset(workArea.getDefaultClamping().getRelativePosition());
        c.setX(0);
        c.setY(0);
        c.setZ(0);
        return c;
    }

    @Override
    public void interruptCurrentAction() {

    }

    @Override
    public boolean validatePickSettings(final DevicePickSettings pickSettings) {
        // note we assume the corresponding device settings are loaded!
        if ((pickSettings != null) && (pickSettings.getWorkArea() != null) && (getWorkAreaNames().contains(pickSettings.getWorkArea().getName()))
                && (getLayout().getStackingPositions() != null) && (getLayout().getStackingPositions().size() > 0)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean validatePutSettings(final DevicePutSettings putSettings) {
        // note we assume the corresponding device settings are loaded!
        if ((putSettings != null) && (putSettings.getWorkArea() != null) && (getWorkAreaNames().contains(putSettings.getWorkArea().getName()))
                && (getLayout().getStackingPositions() != null) && (getLayout().getStackingPositions().size() > 0)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean validateInterventionSettings(final DeviceInterventionSettings interventionSettings) {
        // note we assume the corresponding device settings are loaded!
        if ((interventionSettings != null) && (interventionSettings.getWorkArea() != null) && (getWorkAreaNames().contains(interventionSettings.getWorkArea().getName()))
                && (getLayout().getStackingPositions() != null) && (getLayout().getStackingPositions().size() > 0)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isConnected() {
        return false;
    }

    public abstract void notifyLayoutChanged();

    public void placeFinishedWorkPieces(final int finishedAmount, final boolean hasBinForFinished) {
        int placedAmount = 0;
        int nbLayers = getLayout().getLayers();
        int position = 0;
        int replacedAmount = 0;
        while(placedAmount < finishedAmount) {
            StackPlateStackingPosition stPos = getLayout().getStackingPositions().get(position);
            if(!stPos.hasWorkPiece()) {
                if (hasBinForFinished) {
                    stPos.setWorkPiece(null);
                } else {
                    getLayout().getStackingPositions().set(position, getLayout().getFinishedStackingPositions().get(position));
                    stPos = getLayout().getStackingPositions().get(position);
                    stPos.setWorkPiece(getFinishedWorkPiece());
                }
                while((stPos.getAmount() < nbLayers) && (placedAmount < finishedAmount)) {
                    stPos.incrementAmount();
                    placedAmount++;
                }
            } else if (stPos.getWorkPiece().getType().equals(WorkPiece.Type.RAW)) {
                replacedAmount += stPos.getAmount();
                if (hasBinForFinished) {
                    stPos.setWorkPiece(null);
                } else {
                    getLayout().getStackingPositions().set(position, getLayout().getFinishedStackingPositions().get(position));
                    stPos = getLayout().getStackingPositions().get(position);
                    stPos.setWorkPiece(getFinishedWorkPiece());
                }
                stPos.setAmount(0);
                while((stPos.getAmount() < nbLayers) && (placedAmount < finishedAmount)) {
                    stPos.incrementAmount();
                    placedAmount++;
                }
            } else if (stPos.getWorkPiece().getType().equals(WorkPiece.Type.FINISHED)) {
                while((stPos.getAmount() < nbLayers) && (placedAmount < finishedAmount)) {
                    stPos.incrementAmount();
                    placedAmount++;
                }
            }
            position++;
        }
        decreaseAmountOfFirstRawPieces(placedAmount - replacedAmount);
        notifyLayoutChanged();
    }

    private void decreaseAmountOfFirstRawPieces(final int amount) {
        int position = getLayout().getFirstRawPosition();
        if(position >= 0) {
            getLayout().getStackingPositions().get(position).decrementAmountBy(amount);
        }
    }

    public abstract float getR(double orientation);

    public abstract float getRRound();

    @Override
    public float getZSafePlane(final IWorkPieceDimensions dimensions, final SimpleWorkArea workArea, final ApproachType approachType) throws IllegalArgumentException {
        float zSafePlane = workArea.getDefaultClamping().getRelativePosition().getZ();
        float wpHeight = layout.getLayers() *  dimensions.getZSafe();
        if (wpHeight > workArea.getDefaultClamping().getHeight()) {
            zSafePlane += wpHeight;
        } else {
            zSafePlane += workArea.getDefaultClamping().getHeight();
        }
        zSafePlane += dimensions.getZSafe();
        return zSafePlane;
    }

    @Override
    public <T extends IWorkPieceDimensions> Coordinates getPutLocation(
            final AbstractPiecePlacementVisitor<T> visitor, final SimpleWorkArea workArea,
            final T dimensions, final ClampingManner clampType, final ApproachType approachType) {
        return visitor.getPutLocation(this, workArea, dimensions, clampType, approachType);
    }

    @Override
    public <T extends IWorkPieceDimensions> Coordinates getPickLocation(
            final AbstractPiecePlacementVisitor<T> visitor, final SimpleWorkArea workArea,
            final T dimensions, final ClampingManner clampType, final ApproachType approachType) {
        return visitor.getPickLocation(this, workArea, dimensions, clampType, approachType, getUnloadType());
    }

    @Override
    public <T extends IWorkPieceDimensions> Coordinates getLocation(
            final AbstractPiecePlacementVisitor<T> visitor, final SimpleWorkArea workArea,
            final Type type, final ClampingManner clampType) {
        return visitor.getLocation(this, workArea, type, clampType, getUnloadType());
    }

    public UnloadType getUnloadType() {
        return unloadType;
    }

    public void setUnloadType(final UnloadType unloadType) {
        this.unloadType = unloadType;
    }

    public int getCurrentLayer() {
        return currentLayer;
    }

    public void setCurrentLayer(final int currentLayer) {
        this.currentLayer = currentLayer;
    }

    public void decrementCurrentLayer() {
        this.currentLayer--;
    }
}
