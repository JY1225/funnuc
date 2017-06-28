package eu.robojob.millassist.external.device.stacking.pallet;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import eu.robojob.millassist.external.communication.AbstractCommunicationException;
import eu.robojob.millassist.external.device.DeviceActionException;
import eu.robojob.millassist.external.device.DeviceInterventionSettings;
import eu.robojob.millassist.external.device.DevicePickSettings;
import eu.robojob.millassist.external.device.DevicePutSettings;
import eu.robojob.millassist.external.device.EDeviceGroup;
import eu.robojob.millassist.external.device.SimpleWorkArea;
import eu.robojob.millassist.external.device.Zone;
import eu.robojob.millassist.external.device.stacking.AbstractStackingDevice;
import eu.robojob.millassist.external.robot.AbstractRobotActionSettings.ApproachType;
import eu.robojob.millassist.process.ProcessFlow;
import eu.robojob.millassist.workpiece.IWorkPieceDimensions;

public abstract class AbstractPallet extends AbstractStackingDevice {

    private PalletLayout palletLayout;
//    private static Logger logger = LogManager.getLogger(AbstractPallet.class.getName());
    /**
     * List of listeners
     */
    private List<UnloadPalletListener> listeners = new ArrayList<UnloadPalletListener>();
    protected float maxHeight;

    public AbstractPallet(String name, final Set<Zone> zones) {
        super(name, zones);
    }

    public AbstractPallet(String name) {
        this(name, new HashSet<Zone>());
    }
    
    public void setPalletLayout(PalletLayout layout) {
        this.palletLayout = layout;
    }
    
    public PalletLayout getPalletLayout() {
        return palletLayout;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void prepareForProcess(ProcessFlow process) throws AbstractCommunicationException, InterruptedException {
        // TODO Auto-generated method stub

    }


    /**
     * {@inheritDoc}
     */
    @Override
    public EDeviceGroup getType() {
        return EDeviceGroup.STACKING;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canIntervention(DeviceInterventionSettings interventionSettings)
            throws AbstractCommunicationException, DeviceActionException {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void prepareForPick(DevicePickSettings pickSettings, int processId) throws AbstractCommunicationException,
            DeviceActionException, InterruptedException {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void prepareForPut(DevicePutSettings putSettings, int processId) throws AbstractCommunicationException,
            DeviceActionException, InterruptedException {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void prepareForIntervention(DeviceInterventionSettings interventionSettings)
            throws AbstractCommunicationException, DeviceActionException, InterruptedException {
        // TODO Auto-generated method stub

    }

    public void notifyLayoutChanged() {
        for (UnloadPalletListener listener : getListeners()) {
            listener.layoutChanged();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void interventionFinished(DeviceInterventionSettings interventionSettings)
            throws AbstractCommunicationException, DeviceActionException, InterruptedException {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void releasePiece(DevicePickSettings pickSettings) throws AbstractCommunicationException,
            DeviceActionException, InterruptedException {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void grabPiece(DevicePutSettings putSettings) throws AbstractCommunicationException, DeviceActionException,
            InterruptedException {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reset() throws AbstractCommunicationException, DeviceActionException, InterruptedException {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void interruptCurrentAction() {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isConnected() {
        return true;
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

    public float getMaxHeight() {
        return maxHeight;
    }

    public void setMaxHeight(float maxHeight) {
        this.maxHeight = maxHeight;
    }

    @Override
    public float getZSafePlane(final IWorkPieceDimensions dimensions, final SimpleWorkArea workArea, final ApproachType approachType) throws IllegalArgumentException {
        float zSafePlane = workArea.getDefaultClamping().getRelativePosition().getZ(); 
        float wpHeight = dimensions.getZSafe() * (palletLayout.getNumberOfLayersPresent() + 1);
        zSafePlane += palletLayout.getPalletHeight();
        if (wpHeight > workArea.getDefaultClamping().getHeight()) {
            zSafePlane += wpHeight;
        } else {
            zSafePlane += workArea.getDefaultClamping().getHeight();
        }
        return zSafePlane;
    }
    
}
