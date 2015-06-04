package eu.robojob.millassist.ui.admin.device;

import java.util.ArrayList;
import java.util.List;

import eu.robojob.millassist.external.device.AbstractDevice;
import eu.robojob.millassist.external.device.DeviceManager;
import eu.robojob.millassist.external.device.stacking.pallet.Pallet;
import eu.robojob.millassist.positioning.UserFrame;
import eu.robojob.millassist.ui.general.AbstractFormPresenter;

public class PalletConfigurePresenter extends AbstractFormPresenter<PalletConfigureView, DeviceMenuPresenter>{

    private DeviceManager deviceManager;
    private Pallet pallet;
    public PalletConfigurePresenter(PalletConfigureView view, DeviceManager deviceManager) {
        super(view);
        this.deviceManager = deviceManager;
        for (AbstractDevice device : deviceManager.getStackingToDevices()) {
            if (device instanceof Pallet) {
                this.pallet = (Pallet) device;
                getView().setPallet(this.pallet);
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
    public void saveData(final String name, final String userFrameName) {
        deviceManager.updatePallet(pallet, name, userFrameName);
    }
}
