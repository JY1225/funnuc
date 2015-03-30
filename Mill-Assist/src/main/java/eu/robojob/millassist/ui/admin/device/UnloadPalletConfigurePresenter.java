package eu.robojob.millassist.ui.admin.device;

import java.util.ArrayList;
import java.util.List;

import eu.robojob.millassist.external.device.AbstractDevice;
import eu.robojob.millassist.external.device.DeviceManager;
import eu.robojob.millassist.external.device.stacking.pallet.UnloadPallet;
import eu.robojob.millassist.positioning.UserFrame;
import eu.robojob.millassist.ui.general.AbstractFormPresenter;

public class UnloadPalletConfigurePresenter  extends AbstractFormPresenter<UnloadPalletConfigureView, DeviceMenuPresenter> {

    private DeviceManager deviceManager;
    private UnloadPallet unloadPallet;
    
    public UnloadPalletConfigurePresenter(final UnloadPalletConfigureView view, final DeviceManager deviceManager) {
        super(view);
        this.deviceManager = deviceManager;
        for (AbstractDevice device : deviceManager.getStackingToDevices()) {
            if (device instanceof UnloadPallet) {
                this.unloadPallet = (UnloadPallet) device;
                getView().setUnloadPallet(this.unloadPallet);
                break;
            }
        }
        getView().build();
        getView().refresh();
    }
    
    public void updateUserFrames() {
        List<String> userFrameNames = new ArrayList<String>();
        for (UserFrame uf : deviceManager.getAllUserFrames()) {
            userFrameNames.add(uf.getName());
        }
        getView().setUserFrames(userFrameNames);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void setPresenter() {
        getView().setPresenter(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isConfigured() {
        return false;
    }
    
    /**
     * Save the settings data off the unload pallet and persist to the database. 
     * @param name Name of the unload pallet
     * @param userFrameName User frame of the unload pallet
     * @param width Width of the unload pallet (Y-axis)
     * @param length Length of the unload pallet (X-axis)
     * @param border Minimal distance between the work pieces and the border of the unload pallet
     * @param xOffset Minimal distance between two work pieces on the x-axis
     * @param yOffset Minimal distance between two work pieces on the y-axis
     * @param minInterferenceDistance Minimal distance between two work pieces in all directions
     */
    public void saveData(final String name, final String userFrameName, final float width, final float length, final float border, final float xOffset, final float yOffset, final float minInterferenceDistance) {
        deviceManager.updateUnloadPallet(unloadPallet, name, userFrameName, width, length, border, xOffset, yOffset, minInterferenceDistance);
    }

}
