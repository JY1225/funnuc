package eu.robojob.millassist.external.device.stacking.pallet;

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
import eu.robojob.millassist.external.device.stacking.stackplate.StackPlateStackingPosition;
import eu.robojob.millassist.external.device.stacking.stackplate.gridplate.GridPlate;
import eu.robojob.millassist.external.device.stacking.stackplate.gridplate.GridPlateLayout;
import eu.robojob.millassist.external.device.visitor.AbstractPiecePlacementVisitor;
import eu.robojob.millassist.external.robot.AbstractRobotActionSettings.ApproachType;
import eu.robojob.millassist.positioning.Coordinates;
import eu.robojob.millassist.ui.configure.device.stacking.pallet.PalletDeviceSettings;
import eu.robojob.millassist.workpiece.IWorkPieceDimensions;
import eu.robojob.millassist.workpiece.WorkPiece;
import eu.robojob.millassist.workpiece.WorkPiece.Type;

public class Pallet extends AbstractPallet {

    private static Logger logger = LogManager.getLogger(Pallet.class.getName());
    private GridPlate gridPlate;

    private float horizontalR;
    private float tiltedR;
    private int layers;
    private PalletLayout palletLayout;
    
    private StackPlateStackingPosition currentPickLocation;
    private StackPlateStackingPosition currentPutLocation;

    private GridPlateLayout layout;

    public Pallet(String name, Set<Zone> zones) {
        super(name, zones);
    }

    public Pallet(String name) {
        super(name);
    }

    @Override
    public void clearDeviceSettings() {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EDeviceGroup getType() {
        return EDeviceGroup.PALLET;
    }

    @Override
    public <T extends IWorkPieceDimensions> Coordinates getLocation(AbstractPiecePlacementVisitor<T> visitor,
            SimpleWorkArea workArea, Type type, ClampingManner clampType) {
        return visitor.getLocation(this, workArea, type, clampType);
    }

    @Override
    public boolean canPut(DevicePutSettings putSettings) throws AbstractCommunicationException, DeviceActionException,
            InterruptedException {
        for (StackPlateStackingPosition stackingPos : getLayout().getStackingPositions()) {
            if (stackingPos.getWorkPiece() == null) {
                return true;
            }
        }
        return false;
    }
    

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canPick(DevicePickSettings pickSettings) throws AbstractCommunicationException,
            DeviceActionException {
        for (StackPlateStackingPosition stackingPos : getLayout().getStackingPositions()) {
            if ((stackingPos.getWorkPiece() != null) && (stackingPos.getWorkPiece().getType() == Type.RAW)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void loadDeviceSettings(DeviceSettings deviceSettings) {
        for (Entry<SimpleWorkArea, Clamping> entry : deviceSettings.getClampings().entrySet()) {
            entry.getKey().setDefaultClamping(entry.getValue());
        }
        if (deviceSettings instanceof PalletDeviceSettings) {
            PalletDeviceSettings settings = (PalletDeviceSettings) deviceSettings;
            setFinishedWorkPiece(settings.getFinishedWorkPiece());
            setRawWorkPiece(settings.getRawWorkPiece());
            setLayers(settings.getLayers());
            setGridPlate(settings.getGridPlate());
            setPalletLayout(settings.getPalletLayout());
            try {
                getLayout().configureStackingPositions(settings.getRawWorkPiece(), settings.getFinishedWorkPiece(),
                        settings.getOrientation(), settings.getLayers());
                getLayout().initRawWorkPieces(getRawWorkPiece(), settings.getAmount());
            } catch (IncorrectWorkPieceDataException exception) {
                logger.error(exception);
            }

        } else {
            throw new IllegalArgumentException("Unknown device settings");
        }
    }

    @Override
    public PalletDeviceSettings getDeviceSettings() {
        return new PalletDeviceSettings(getRawWorkPiece(), getFinishedWorkPiece(), getGridPlate(), 0, getLayers(), getPalletLayout());
    }

    @Override
    public Coordinates getLocationOrientation(SimpleWorkArea workArea, ClampingManner clampType) {
        Coordinates c = new Coordinates(getLayout().getStackingPositions().get(0).getPosition());
        c.offset(workArea.getDefaultClamping().getRelativePosition());
        c.setX(0);
        c.setY(0);
        c.setZ(0);
        return c;
    }

    @Override
    public <T extends IWorkPieceDimensions> Coordinates getPutLocation(AbstractPiecePlacementVisitor<T> visitor,
            SimpleWorkArea workArea, T dimensions, ClampingManner clampType, ApproachType approachType) {
        return visitor.getPutLocation(this, workArea, dimensions, clampType, approachType);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends IWorkPieceDimensions> Coordinates getPickLocation(AbstractPiecePlacementVisitor<T> visitor,
            SimpleWorkArea workArea, T dimensions, ClampingManner clampType, ApproachType approachType) {
        return visitor.getPickLocation(this, workArea, dimensions, clampType, approachType);
    }

    public GridPlate getGridPlate() {
        return this.gridPlate;
    }

    public void setGridPlate(GridPlate gridPlate) {
        PalletDeviceSettings deviceSettings = getDeviceSettings();
        if (gridPlate != null) {
            logger.debug("Adding gridplate [" + gridPlate.getName() + "] to pallet.");
            deviceSettings.setGridId(gridPlate.getId());
            deviceSettings.setStudHeight(gridPlate.getDepth());
            setLayout(new GridPlateLayout(gridPlate));
            this.gridPlate = gridPlate;
        } 
    }

    public GridPlateLayout getLayout() {
        return this.layout;
    }

    public void setLayout(GridPlateLayout layout) {
        this.layout = layout;
        layout.setPallet(this);
    }

    public float getR(float orientation) {
        float deltaR = getTiltedR() - getHorizontalR();
        if (orientation >= 90) {
            orientation = orientation - 90;
            if (deltaR > 0) {
                return getHorizontalR() + (float) orientation;
            } else {
                return getHorizontalR() - (float) orientation;
            }
        } else {
            if (deltaR > 0) {
                return getHorizontalR() + (float) orientation;
            } else {
                return getHorizontalR() - (float) orientation;
            }
        }
    }

    public float getHorizontalR() {
        return this.horizontalR;
    }

    public void setHorizontalR(float horizontalR) {
        this.horizontalR = horizontalR;
    }

    public float getTiltedR() {
        return this.tiltedR;
    }

    public void setTiltedR(float tiltedR) {
        this.tiltedR = tiltedR;
    }

    public int getLayers() {
        return this.layers;
    }

    public void setLayers(int layers) {
        this.layers = layers;
    }

    public PalletLayout getPalletLayout() {
        return this.palletLayout;
    }

    public void setPalletLayout(PalletLayout palletLayout) {
        this.palletLayout = palletLayout;
    }

    public StackPlateStackingPosition getCurrentPickLocation() {
        return this.currentPickLocation;
    }

    public void setCurrentPickLocation(StackPlateStackingPosition currentPickLocation) {
        this.currentPickLocation = currentPickLocation;
    }

    public StackPlateStackingPosition getCurrentPutLocation() {
        return this.currentPutLocation;
    }

    public void setCurrentPutLocation(StackPlateStackingPosition currentPutLocation) {
        this.currentPutLocation = currentPutLocation;
    }

    @Override
    public void pickFinished(DevicePickSettings pickSettings, int processId) throws AbstractCommunicationException,
            DeviceActionException, InterruptedException {
        currentPickLocation.setAmount(currentPickLocation.getAmount() - 1);
        if (currentPickLocation.getAmount() == 0) {
            currentPickLocation.setWorkPiece(null);
        }
        currentPickLocation = null;
        notifyLayoutChanged();
        logger.info("pick finished!!");
    }

    @Override
    public void putFinished(DevicePutSettings putSettings) throws AbstractCommunicationException,
            DeviceActionException, InterruptedException {
        currentPutLocation.setWorkPiece(getFinishedWorkPiece());
        currentPutLocation.incrementAmount();
        currentPutLocation = null;
        notifyLayoutChanged();
        logger.info("put finished!");
    }
    
    public void addWorkPieces(int amount, boolean reset) {
        getLayout().placeRawWorkPieces(getRawWorkPiece(), amount, reset, true);
        notifyLayoutChanged();
    }
    
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
                while(stPos.getAmount() < nbLayers && placedAmount < finishedAmount) {
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
                while(stPos.getAmount() < nbLayers && placedAmount < finishedAmount) {
                    stPos.incrementAmount();
                    placedAmount++;
                } 
            } else if (stPos.getWorkPiece().getType().equals(WorkPiece.Type.FINISHED)) {
                while(stPos.getAmount() < nbLayers && placedAmount < finishedAmount) {
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

}
