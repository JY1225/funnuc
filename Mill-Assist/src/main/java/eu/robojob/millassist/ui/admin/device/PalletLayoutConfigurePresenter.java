package eu.robojob.millassist.ui.admin.device;

import eu.robojob.millassist.external.device.DeviceManager;
import eu.robojob.millassist.external.device.stacking.pallet.PalletLayout;
import eu.robojob.millassist.ui.general.AbstractFormPresenter;


public class PalletLayoutConfigurePresenter extends AbstractFormPresenter<PalletLayoutConfigureView, DeviceMenuPresenter> {

    private DeviceManager deviceManager;
    private PalletLayout selectedPalletLayout;
    private boolean editMode;
    
    public PalletLayoutConfigurePresenter(final PalletLayoutConfigureView view, DeviceManager deviceManager) {
        super(view);
        this.deviceManager = deviceManager;
        getView().build();
        getView().refresh();
    }
    
    @Override
    public void setPresenter() {
        getView().setPresenter(this);
    }

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
    public void saveData(final String name, final float width, final float length, final float height, final float border, final float xOffset, final float yOffset, final float minInterferenceDistance, final float horizontalR, final float verticalR) {
        deviceManager.savePalletLayout(name, width, length, height, border, xOffset, yOffset, minInterferenceDistance, horizontalR, verticalR);
    }
    
    public void updateData(PalletLayout layout, final String name, final float width, final float length, final float height, final float border, final float xOffset, final float yOffset, final float minInterferenceDistance, final float horizontalR, final float verticalR) {
        deviceManager.updatePalletLayout(layout, name, width, length, height, border, xOffset, yOffset, minInterferenceDistance, horizontalR, verticalR);
    }
    
    public void setSelectedLayout(PalletLayout layout) {
        this.selectedPalletLayout = layout;
    }
    
    public void updatePalletLayouts() {
        getView().setPalletLayouts(deviceManager.getAllPalletLayoutNames());
    }
    
    public void clickedEdit(String layoutName) {
        if (editMode) {
            getView().reset();
            this.selectedPalletLayout = null;
            editMode = false;
        } else {
            this.selectedPalletLayout = deviceManager.getPalletLayoutByName(layoutName);
            getView().palletLayoutSelected(selectedPalletLayout);
            getView().showFormEdit();
            editMode = true;
        }
    }
    
    public void clickedNew() {
        getView().reset();
        this.selectedPalletLayout = null;
        if (!editMode) {
            getView().showFormNew();
            editMode = true;
        } else {
            editMode = false;
        }
        
    }

}
