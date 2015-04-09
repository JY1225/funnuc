package eu.robojob.millassist.ui.configure.device.stacking.pallet;

import eu.robojob.millassist.ui.configure.device.stacking.AbstractStackingDeviceMenuPresenter;
import eu.robojob.millassist.ui.configure.device.stacking.ConfigureSmoothPresenter;
import eu.robojob.millassist.ui.configure.device.stacking.StackingDeviceConfigurePresenter;
import eu.robojob.millassist.ui.configure.device.stacking.StackingDeviceMenuView;
import eu.robojob.millassist.ui.controls.TextInputControlListener;
import eu.robojob.millassist.ui.general.model.DeviceInformation;

public class UnloadPalletMenuPresenter extends AbstractStackingDeviceMenuPresenter{

    private StackingDeviceConfigurePresenter basicStackPlateConfigurePresenter;
    private ConfigureSmoothPresenter<UnloadPalletMenuPresenter> configurePutPresenter;
    private UnloadPalletLayoutPresenter unloadPalletLayoutPresenter;
    
    public UnloadPalletMenuPresenter(final StackingDeviceMenuView view, final DeviceInformation deviceInfo, final StackingDeviceConfigurePresenter basicStackPlateConfigurePresenter, final UnloadPalletLayoutPresenter unloadPalletLayoutPresenter, final ConfigureSmoothPresenter<UnloadPalletMenuPresenter> configurePutPresenter) {
        super(view, deviceInfo);
        this.basicStackPlateConfigurePresenter = basicStackPlateConfigurePresenter;
        basicStackPlateConfigurePresenter.setMenuPresenter(this);
        if(unloadPalletLayoutPresenter != null) {
            this.unloadPalletLayoutPresenter = unloadPalletLayoutPresenter;
            this.unloadPalletLayoutPresenter.setMenuPresenter(this);
        }
        
        if(configurePutPresenter != null) {
            this.configurePutPresenter = configurePutPresenter;
            this.configurePutPresenter.setMenuPresenter(this);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void configureDevice() {
        getView().setConfigureDeviceActive();
        getParent().setBottomRightView(basicStackPlateConfigurePresenter.getView());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void configureWorkPiece() {
        //No WP configuration 
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void configurePick() {
        //No pick
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void configurePut() {
        getView().setConfigurePutActive();
        getParent().setBottomRightView(configurePutPresenter.getView());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void showLayout() {
        getView().setViewLayoutActive();
        getParent().setBottomRightView(unloadPalletLayoutPresenter.getView());
        unloadPalletLayoutPresenter.refresh();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isConfigured() {
        return basicStackPlateConfigurePresenter.isConfigured() && unloadPalletLayoutPresenter.isConfigured();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setBlocked(boolean blocked) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setTextFieldListener(TextInputControlListener listener) {
        basicStackPlateConfigurePresenter.setTextFieldListener(listener);
        unloadPalletLayoutPresenter.setTextFieldListener(listener);
        if (configurePutPresenter != null) {
            configurePutPresenter.setTextFieldListener(listener);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unregisterListeners() {
       unloadPalletLayoutPresenter.unregister();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void openFirst() {
        showLayout();
    }
    
    public void processFlowUpdated() {
        getParent().updateProcessFlow();
    }

}
