package eu.robojob.millassist.external.device.stacking.pallet;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
import eu.robojob.millassist.external.device.EDeviceGroup;
import eu.robojob.millassist.external.device.SimpleWorkArea;
import eu.robojob.millassist.external.device.Zone;
import eu.robojob.millassist.external.device.stacking.AbstractStackingDevice;
import eu.robojob.millassist.external.device.stacking.StackingPosition;
import eu.robojob.millassist.external.device.visitor.AbstractPiecePlacementVisitor;
import eu.robojob.millassist.external.robot.AbstractRobotActionSettings.ApproachType;
import eu.robojob.millassist.positioning.Coordinates;
import eu.robojob.millassist.process.ProcessFlow;
import eu.robojob.millassist.workpiece.IWorkPieceDimensions;
import eu.robojob.millassist.workpiece.RectangularDimensions;
import eu.robojob.millassist.workpiece.WorkPiece;
import eu.robojob.millassist.workpiece.WorkPiece.Material;
import eu.robojob.millassist.workpiece.WorkPiece.Type;

public class UnloadPallet extends AbstractStackingDevice{
    
    private static Logger logger = LogManager.getLogger(UnloadPallet.class.getName());
    private PalletLayout layout;
    
    private List<UnloadPalletListener> listeners = new ArrayList<UnloadPalletListener>();
    
    public UnloadPallet(String name, final Set<Zone> zones, final PalletLayout layout) {
        super(name, zones);
        this.layout = layout;
    }
    
    public UnloadPallet(String name, final PalletLayout layout) {
        this(name, new HashSet<Zone>(), layout);
    }

    @Override
    public void clearDeviceSettings() {
        setRawWorkPiece(new WorkPiece(WorkPiece.Type.RAW, new RectangularDimensions(), Material.OTHER, 0.0f));
        setFinishedWorkPiece(new WorkPiece(WorkPiece.Type.FINISHED, new RectangularDimensions(), Material.OTHER, 0.0f));
        getWorkAreas().get(0).getDefaultClamping().resetHeightToDefault();

    }

    @Override
    public <T extends IWorkPieceDimensions> Coordinates getLocation(
            AbstractPiecePlacementVisitor<T> visitor, SimpleWorkArea workArea,
            Type type, ClampingManner clampType) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void prepareForProcess(ProcessFlow process)
            throws AbstractCommunicationException, InterruptedException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public boolean canPick(DevicePickSettings pickSettings)
            throws AbstractCommunicationException, DeviceActionException {
        return false;
    }

    @Override
    public boolean canPut(DevicePutSettings putSettings)
            throws AbstractCommunicationException, DeviceActionException,
            InterruptedException {
        for (StackingPosition stackingPos : getLayout().getStackingPositions()) {
            if (stackingPos.getWorkPiece() == null) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public EDeviceGroup getType() {
        return EDeviceGroup.UNLOAD_PALLET;
    }

    @Override
    public boolean canIntervention(
            DeviceInterventionSettings interventionSettings)
            throws AbstractCommunicationException, DeviceActionException {
        return true;
    }

    @Override
    public void prepareForPick(DevicePickSettings pickSettings, int processId)
            throws AbstractCommunicationException, DeviceActionException,
            InterruptedException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void prepareForPut(DevicePutSettings putSettings, int processId)
            throws AbstractCommunicationException, DeviceActionException,
            InterruptedException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void prepareForIntervention(
            DeviceInterventionSettings interventionSettings)
            throws AbstractCommunicationException, DeviceActionException,
            InterruptedException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void pickFinished(DevicePickSettings pickSettings, int processId)
            throws AbstractCommunicationException, DeviceActionException,
            InterruptedException {
        //Cannot pick
    }

    @Override
    public void putFinished(DevicePutSettings putSettings)
            throws AbstractCommunicationException, DeviceActionException,
            InterruptedException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void interventionFinished(
            DeviceInterventionSettings interventionSettings)
            throws AbstractCommunicationException, DeviceActionException,
            InterruptedException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void releasePiece(DevicePickSettings pickSettings)
            throws AbstractCommunicationException, DeviceActionException,
            InterruptedException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void grabPiece(DevicePutSettings putSettings)
            throws AbstractCommunicationException, DeviceActionException,
            InterruptedException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void reset() throws AbstractCommunicationException,
            DeviceActionException, InterruptedException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void loadDeviceSettings(DeviceSettings deviceSettings) {
        for (Entry<SimpleWorkArea, Clamping> entry : deviceSettings.getClampings().entrySet()) {
            entry.getKey().setDefaultClamping(entry.getValue());
        }
        if (deviceSettings instanceof UnloadPalletDeviceSettings) {
            UnloadPalletDeviceSettings settings = (UnloadPalletDeviceSettings) deviceSettings;
            setFinishedWorkPiece(settings.getFinishedWorkPiece());
//            getLayout().calculateLayoutForWorkPiece(getFinishedWorkPiece());
//            getLayout().initFinishedWorkPieces(getFinishedWorkPiece());
        } else {
            throw new IllegalArgumentException("Unknown device settings");
        }
//        notifyLayoutChanged();
    }

    @Override
    public UnloadPalletDeviceSettings getDeviceSettings() {
        return new UnloadPalletDeviceSettings(getFinishedWorkPiece());
    }

    @Override
    public Coordinates getLocationOrientation(SimpleWorkArea workArea,
            ClampingManner clampType) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void interruptCurrentAction() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public boolean isConnected() {
        return true;
    }

    @Override
    public <T extends IWorkPieceDimensions> Coordinates getPutLocation(
            AbstractPiecePlacementVisitor<T> visitor, SimpleWorkArea workArea,
            T dimensions, ClampingManner clampType, ApproachType approachType) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T extends IWorkPieceDimensions> Coordinates getPickLocation(
            AbstractPiecePlacementVisitor<T> visitor, SimpleWorkArea workArea,
            T dimensions, ClampingManner clampType, ApproachType approachType) {
        // TODO Auto-generated method stub
        return null;
    }

    public PalletLayout getLayout() {
        return layout;
    }
    
    
    
    public void notifyLayoutChanged() {
        for (UnloadPalletListener listener : getListeners()) {
            listener.layoutChanged();
        }
    }

    public List<UnloadPalletListener> getListeners() {
        return listeners;
    }

    public void addListener(final UnloadPalletListener listener) {
        this.listeners.add(listener);
    }
    
    public void removeListener(final UnloadPalletListener listener) {
        this.listeners.remove(listener);
    }
    
    public void clearListeners() {
        this.listeners.clear();
    }

}
