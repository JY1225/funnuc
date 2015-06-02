package eu.robojob.millassist.ui.configure.device.stacking.pallet;

import eu.robojob.millassist.ui.configure.device.stacking.AbstractStackingDeviceMenuPresenter;
import eu.robojob.millassist.ui.configure.device.stacking.ConfigureSmoothPresenter;
import eu.robojob.millassist.ui.configure.device.stacking.StackingDeviceConfigurePresenter;
import eu.robojob.millassist.ui.configure.device.stacking.StackingDeviceMenuView;
import eu.robojob.millassist.ui.controls.TextInputControlListener;
import eu.robojob.millassist.ui.general.AbstractFormPresenter;
import eu.robojob.millassist.ui.general.model.DeviceInformation;

public class PalletMenuPresenter extends AbstractStackingDeviceMenuPresenter {


    private StackingDeviceConfigurePresenter deviceConfigurePresenter;
    private ConfigureSmoothPresenter<PalletMenuPresenter> configurePutPresenter;
    private ConfigureSmoothPresenter<PalletMenuPresenter> configurePickPresenter;
    private PalletLayoutPresenter loadPalletLayoutPresenter;
    private AbstractFormPresenter<?, PalletMenuPresenter> workPieceConfigurePresenter;
    
    public PalletMenuPresenter(final StackingDeviceMenuView view, final DeviceInformation deviceInfo, final StackingDeviceConfigurePresenter deviceConfigurePresenter, final PalletLayoutPresenter loadPalletLayoutPresenter, AbstractFormPresenter<?, PalletMenuPresenter> workPieceConfigurePresenter, final ConfigureSmoothPresenter<PalletMenuPresenter> configurePickPresenter, final ConfigureSmoothPresenter<PalletMenuPresenter> configurePutPresenter) {
        super(view, deviceInfo);
        this.deviceConfigurePresenter = deviceConfigurePresenter;
        deviceConfigurePresenter.setMenuPresenter(this);
        if(loadPalletLayoutPresenter != null) {
            this.loadPalletLayoutPresenter = loadPalletLayoutPresenter;
            this.loadPalletLayoutPresenter.setMenuPresenter(this);
        }
        
        if(configurePutPresenter != null) {
            this.configurePutPresenter = configurePutPresenter;
            this.configurePutPresenter.setMenuPresenter(this);
        }
        
        if(configurePickPresenter != null) {
            this.configurePickPresenter = configurePickPresenter;
            this.configurePickPresenter.setMenuPresenter(this);
        }
        
        if(workPieceConfigurePresenter != null) {
            this.workPieceConfigurePresenter = workPieceConfigurePresenter;
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void configureDevice() {
        getView().setConfigureDeviceActive();
        getParent().setBottomRightView(deviceConfigurePresenter.getView());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void configureWorkPiece() {
        getView().setConfigureWorkPieceActive();
        getParent().setBottomRightView(workPieceConfigurePresenter.getView());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void configurePick() {
        getView().setConfigurePickActive();
        getParent().setBottomRightView(configurePickPresenter.getView());
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
//        getView().setViewLayoutActive();
//        getParent().setBottomRightView(loadPalletLayoutPresenter.getView());
//        loadPalletLayoutPresenter.refresh();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isConfigured() {
        return deviceConfigurePresenter.isConfigured() && workPieceConfigurePresenter.isConfigured();
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
        deviceConfigurePresenter.setTextFieldListener(listener);
        loadPalletLayoutPresenter.setTextFieldListener(listener);
        workPieceConfigurePresenter.setTextFieldListener(listener);
        if (configurePutPresenter != null) {
            configurePutPresenter.setTextFieldListener(listener);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unregisterListeners() {
        loadPalletLayoutPresenter.unregister();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void openFirst() {
        configureDevice();
    }
    
    public void processFlowUpdated() {
        getParent().updateProcessFlow();
    }

}
