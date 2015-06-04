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
    private PalletLayoutPresenter palletLayoutPresenter;
    private AbstractFormPresenter<?, PalletMenuPresenter> workPieceConfigurePresenter;

    public PalletMenuPresenter(final StackingDeviceMenuView view, final DeviceInformation deviceInfo,
            final StackingDeviceConfigurePresenter deviceConfigurePresenter,
            final PalletLayoutPresenter palletLayoutPresenter,
            AbstractFormPresenter<?, PalletMenuPresenter> workPieceConfigurePresenter,
            final ConfigureSmoothPresenter<PalletMenuPresenter> configurePickPresenter,
            final ConfigureSmoothPresenter<PalletMenuPresenter> configurePutPresenter) {
        super(view, deviceInfo);
        this.deviceConfigurePresenter = deviceConfigurePresenter;
        deviceConfigurePresenter.setMenuPresenter(this);
        if (palletLayoutPresenter != null) {
            this.palletLayoutPresenter = palletLayoutPresenter;
            this.palletLayoutPresenter.setMenuPresenter(this);
        }

        if (configurePutPresenter != null) {
            this.configurePutPresenter = configurePutPresenter;
            this.configurePutPresenter.setMenuPresenter(this);
        }

        if (configurePickPresenter != null) {
            this.configurePickPresenter = configurePickPresenter;
            this.configurePickPresenter.setMenuPresenter(this);
        }

        if (workPieceConfigurePresenter != null) {
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
        getView().setViewLayoutActive();
        getParent().setBottomRightView(palletLayoutPresenter.getView());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isConfigured() {
        if (workPieceConfigurePresenter != null) {
            return deviceConfigurePresenter.isConfigured() && workPieceConfigurePresenter.isConfigured();
        } else {
            return deviceConfigurePresenter.isConfigured();
        }
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
        palletLayoutPresenter.setTextFieldListener(listener);
        if (workPieceConfigurePresenter != null) {
            workPieceConfigurePresenter.setTextFieldListener(listener);
        }
        if (configurePutPresenter != null) {
            configurePutPresenter.setTextFieldListener(listener);
        }
        if (configurePickPresenter != null) {
            configurePickPresenter.setTextFieldListener(listener);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unregisterListeners() {
        palletLayoutPresenter.unregister();
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
