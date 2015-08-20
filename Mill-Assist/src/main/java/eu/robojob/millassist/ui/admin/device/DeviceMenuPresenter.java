package eu.robojob.millassist.ui.admin.device;

import eu.robojob.millassist.external.device.DeviceManager;
import eu.robojob.millassist.external.device.stacking.AbstractStackingDevice;
import eu.robojob.millassist.external.device.stacking.bin.OutputBin;
import eu.robojob.millassist.external.device.stacking.pallet.Pallet;
import eu.robojob.millassist.external.device.stacking.pallet.UnloadPallet;
import eu.robojob.millassist.external.device.stacking.stackplate.basicstackplate.BasicStackPlate;
import eu.robojob.millassist.ui.admin.AbstractSubMenuPresenter;
import eu.robojob.millassist.ui.admin.device.cnc.CNCMachineConfigurePresenter;
import eu.robojob.millassist.ui.controls.TextInputControlListener;

public class DeviceMenuPresenter extends AbstractSubMenuPresenter<DeviceMenuView, DeviceAdminPresenter> {

    private UserFramesConfigurePresenter userFramesConfigurePresenter;
    private BasicStackPlateConfigurePresenter basicStackPlateConfigurePresenter;
    private CNCMachineConfigurePresenter cncMachineConfigurePresenter;
    private CNCMachineClampingsPresenter cncMachineClampingsPresenter;
    private PrageDeviceConfigurePresenter prageDeviceConfigurePresenter;
    private OutputBinConfigurePresenter outputBinConfigurePresenter;
    private GridPlateConfigurePresenter gridPlateConfigurePresenter;
    private ReversalUnitConfigurePresenter reversalUnitConfigurePresenter;
    private UnloadPalletConfigurePresenter unloadPalletConfigurePresenter;
    private PalletLayoutConfigurePresenter palletLayoutConfigurePresenter;
    private PalletConfigurePresenter palletConfigurePresenter;

    public DeviceMenuPresenter(final DeviceMenuView view,
            final UserFramesConfigurePresenter userFramesConfigurePresenter,
            final BasicStackPlateConfigurePresenter basicStackPlateConfigurePresenter,
            final UnloadPalletConfigurePresenter unloadPalletConfigurePresenter,
            final PalletLayoutConfigurePresenter palletLayoutConfigurePresenter,
            final CNCMachineConfigurePresenter cncMachineConfigurePresenter,
            final CNCMachineClampingsPresenter cncMachineClamingsPresenter,
            final PrageDeviceConfigurePresenter prageDeviceConfigurePresenter,
            final OutputBinConfigurePresenter outputBinConfigurePresenter,
            final GridPlateConfigurePresenter gridPlateConfigurePresenter,
            final ReversalUnitConfigurePresenter reversalUnitConfigurePresenter,
            final PalletConfigurePresenter palletConfigurePresenter, final DeviceManager deviceManager) {
        super(view);
        this.userFramesConfigurePresenter = userFramesConfigurePresenter;
        userFramesConfigurePresenter.setMenuPresenter(this);
        this.basicStackPlateConfigurePresenter = basicStackPlateConfigurePresenter;
        basicStackPlateConfigurePresenter.setMenuPresenter(this);
        this.cncMachineConfigurePresenter = cncMachineConfigurePresenter;
        cncMachineConfigurePresenter.setMenuPresenter(this);
        this.cncMachineClampingsPresenter = cncMachineClamingsPresenter;
        cncMachineClampingsPresenter.setMenuPresenter(this);
        this.prageDeviceConfigurePresenter = prageDeviceConfigurePresenter;
        prageDeviceConfigurePresenter.setMenuPresenter(this);
        this.outputBinConfigurePresenter = outputBinConfigurePresenter;
        outputBinConfigurePresenter.setMenuPresenter(this);
        this.gridPlateConfigurePresenter = gridPlateConfigurePresenter;
        gridPlateConfigurePresenter.setMenuPresenter(this);
        this.reversalUnitConfigurePresenter = reversalUnitConfigurePresenter;
        this.unloadPalletConfigurePresenter = unloadPalletConfigurePresenter;
        this.palletLayoutConfigurePresenter = palletLayoutConfigurePresenter;
        this.palletConfigurePresenter = palletConfigurePresenter;

        reversalUnitConfigurePresenter.setMenuPresenter(this);
        if (deviceManager.getPreProcessingDevices().size() == 0) {
            // TODO review if other pre process devices are available!
            getView().disablePrageMenuItem();
        }
        if (deviceManager.getPostProcessingDevices().size() == 0) {
            // TODO review if other post process devices are available!
            getView().disableReversalUnitMenuItem();
        }
        boolean stackPlatePresent = false;
        boolean binPresent = false;
        boolean unloadPalletPresent = false;
        boolean palletPresent = false;

        for (AbstractStackingDevice stackingDevice : deviceManager.getStackingFromDevices()) {
            if (stackingDevice instanceof BasicStackPlate) {
                stackPlatePresent = true;
            }
            if (stackingDevice instanceof Pallet) {
                palletPresent = true;
            }
        }
        for (AbstractStackingDevice stackingDevice : deviceManager.getStackingToDevices()) {
            if (stackingDevice instanceof OutputBin) {
                binPresent = true;
            }
            if (stackingDevice instanceof UnloadPallet) {
                unloadPalletPresent = true;
            }
        }
        if (!stackPlatePresent) {
            getView().disableBasicStackPlateMenuItem();
            getView().disableGridPlateMenuItem();
        }
        if (!binPresent) {
            getView().disableBinMenuItem();
        }
        if (!unloadPalletPresent) {
            getView().disableUnloadPalletMenuItem();
        }
        if (!palletPresent) {
            getView().disablePalletMenuItem();
        }
        if (!unloadPalletPresent && !palletPresent) {
            getView().disablePalletLayoutMenuItem();
        }

    }

    @Override
    public void setTextFieldListener(final TextInputControlListener listener) {
        userFramesConfigurePresenter.setTextFieldListener(listener);
        basicStackPlateConfigurePresenter.setTextFieldListener(listener);
        cncMachineConfigurePresenter.setTextFieldListener(listener);
        cncMachineClampingsPresenter.setTextFieldListener(listener);
        prageDeviceConfigurePresenter.setTextFieldListener(listener);
        outputBinConfigurePresenter.setTextFieldListener(listener);
        gridPlateConfigurePresenter.setTextFieldListener(listener);
        reversalUnitConfigurePresenter.setTextFieldListener(listener);
        unloadPalletConfigurePresenter.setTextFieldListener(listener);
        palletLayoutConfigurePresenter.setTextFieldListener(listener);
        palletConfigurePresenter.setTextFieldListener(listener);

    }

    @Override
    protected void setPresenter() {
        getView().setPresenter(this);
    }

    @Override
    public void openFirst() {

    }

    @Override
    public void setBlocked(final boolean blocked) {
    }

    public void configureUserFrames() {
        getView().setConfigureUserFramesActive();
        getParent().setContentView(userFramesConfigurePresenter.getView());
    }

    public void configureBasicStackPlate() {
        getView().setConfigureBasicStackPlateActive();
        getParent().setContentView(basicStackPlateConfigurePresenter.getView());
    }

    public void configureCNCMachine() {
        getView().setConfigureCNCMachineActive();
        getParent().setContentView(cncMachineConfigurePresenter.getView());
    }

    public void configureCNCMachineClampings() {
        getView().setConfigureClampingsActive();
        getParent().setContentView(cncMachineClampingsPresenter.getView());
    }

    public void configurePrage() {
        getView().setConfigurePrageActive();
        getParent().setContentView(prageDeviceConfigurePresenter.getView());
    }

    public void configureOutputBin() {
        getView().setConfigureOutputBinActive();
        getParent().setContentView(outputBinConfigurePresenter.getView());
    }

    public void configureGridPlate() {
        getView().setConfigureGridPlateActive();
        getParent().setContentView(gridPlateConfigurePresenter.getView());
    }

    public void configureReversalUnit() {
        getView().setConfigureReversalUnitActive();
        getParent().setContentView(reversalUnitConfigurePresenter.getView());
    }

    public void configureUnloadPallet() {
        getView().setConfigureUnloadPalletActive();
        getParent().setContentView(unloadPalletConfigurePresenter.getView());
    }

    public void configurePalletLayout() {
        getView().setConfigurePalletLayout();
        getParent().setContentView(palletLayoutConfigurePresenter.getView());
    }

    public void configurePallet() {
        getView().setConfigurePallet();
        getParent().setContentView(palletConfigurePresenter.getView());
    }

    @Override
    public boolean isConfigured() {
        return false;
    }

    @Override
    public void unregisterListeners() {
        cncMachineConfigurePresenter.unregister();
    }

}
