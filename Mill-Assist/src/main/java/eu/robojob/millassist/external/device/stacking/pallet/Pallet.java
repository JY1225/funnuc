package eu.robojob.millassist.external.device.stacking.pallet;

import java.util.Map.Entry;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.robojob.millassist.external.communication.AbstractCommunicationException;
import eu.robojob.millassist.external.device.Clamping;
import eu.robojob.millassist.external.device.ClampingManner;
import eu.robojob.millassist.external.device.DeviceActionException;
import eu.robojob.millassist.external.device.DevicePutSettings;
import eu.robojob.millassist.external.device.DeviceSettings;
import eu.robojob.millassist.external.device.EDeviceGroup;
import eu.robojob.millassist.external.device.SimpleWorkArea;
import eu.robojob.millassist.external.device.Zone;
import eu.robojob.millassist.external.device.stacking.IncorrectWorkPieceDataException;
import eu.robojob.millassist.external.device.stacking.stackplate.gridplate.GridPlate;
import eu.robojob.millassist.external.device.stacking.stackplate.gridplate.GridPlateLayout;
import eu.robojob.millassist.external.device.visitor.AbstractPiecePlacementVisitor;
import eu.robojob.millassist.external.robot.AbstractRobotActionSettings.ApproachType;
import eu.robojob.millassist.positioning.Coordinates;
import eu.robojob.millassist.ui.configure.device.stacking.pallet.PalletDeviceSettings;
import eu.robojob.millassist.workpiece.IWorkPieceDimensions;
import eu.robojob.millassist.workpiece.WorkPiece.Type;

public class Pallet extends AbstractPallet {

    private static Logger logger = LogManager.getLogger(Pallet.class.getName());
    private GridPlate gridPlate;
    private GridPlate defaultGridPlate;

    private float horizontalR;
    private float tiltedR;
    private int layers;

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
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean canPut(DevicePutSettings putSettings) throws AbstractCommunicationException, DeviceActionException,
            InterruptedException {
        // TODO Auto-generated method stub
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
        if (getGridPlate() == null) {
            return new PalletDeviceSettings(getRawWorkPiece(), getFinishedWorkPiece(), getDefaultGridPlate(), 0,
                    getLayers());
        }
        return new PalletDeviceSettings(getRawWorkPiece(), getFinishedWorkPiece(), getGridPlate(), 0, getLayers());
    }

    @Override
    public Coordinates getLocationOrientation(SimpleWorkArea workArea, ClampingManner clampType) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T extends IWorkPieceDimensions> Coordinates getPutLocation(AbstractPiecePlacementVisitor<T> visitor,
            SimpleWorkArea workArea, T dimensions, ClampingManner clampType, ApproachType approachType) {
        // return visitor.getPut
        return null;
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
        } else {
            logger.debug("Default grid plate added [" + getDefaultGridPlate().getName() + "] to pallet");
            deviceSettings.setGridId(getDefaultGridPlate().getId());
            deviceSettings.setStudHeight(getDefaultGridPlate().getDepth());
            setLayout(new GridPlateLayout(getDefaultGridPlate()));
            this.gridPlate = getDefaultGridPlate();
        }
    }

    @Override
    public void setDefaultLayout(PalletLayout layout) {
        // NOOP
    }

    @Override
    public void setDefaultGrid(GridPlate gridPlate) {
        this.defaultGridPlate = gridPlate;

    }

    public GridPlate getDefaultGridPlate() {
        return this.defaultGridPlate;
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

}
