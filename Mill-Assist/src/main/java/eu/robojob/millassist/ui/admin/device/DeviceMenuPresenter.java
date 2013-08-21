package eu.robojob.millassist.ui.admin.device;

import eu.robojob.millassist.external.device.DeviceManager;
import eu.robojob.millassist.external.device.stacking.AbstractStackingDevice;
import eu.robojob.millassist.external.device.stacking.stackplate.BasicStackPlate;
import eu.robojob.millassist.ui.admin.AbstractSubMenuPresenter;
import eu.robojob.millassist.ui.admin.device.cnc.CNCMachineConfigurePresenter;
import eu.robojob.millassist.ui.controls.TextInputControlListener;

public class DeviceMenuPresenter extends AbstractSubMenuPresenter<DeviceMenuView, DeviceAdminPresenter> {

	private UserFramesConfigurePresenter userFramesConfigurePresenter;
	private BasicStackPlateConfigurePresenter basicStackPlateConfigurePresenter;
	private CNCMachineConfigurePresenter cncMachineConfigurePresenter;
	private CNCMachineClampingsPresenter cncMachineClampingsPresenter;
	private PrageDeviceConfigurePresenter prageDeviceConfigurePresenter;
	
	public DeviceMenuPresenter(final DeviceMenuView view, final UserFramesConfigurePresenter userFramesConfigurePresenter,
			final BasicStackPlateConfigurePresenter basicStackPlateConfigurePresenter, final CNCMachineConfigurePresenter cncMachineConfigurePresenter,
				final CNCMachineClampingsPresenter cncMachineClamingsPresenter, final PrageDeviceConfigurePresenter prageDeviceConfigurePresenter,
					final DeviceManager deviceManager) {
		super(view);
		this.userFramesConfigurePresenter = userFramesConfigurePresenter;
		this.basicStackPlateConfigurePresenter = basicStackPlateConfigurePresenter;
		this.cncMachineConfigurePresenter = cncMachineConfigurePresenter;
		this.cncMachineClampingsPresenter = cncMachineClamingsPresenter;
		this.prageDeviceConfigurePresenter = prageDeviceConfigurePresenter;
		if (deviceManager.getPreProcessingDevices().size() == 0) {
			//TODO review if other pre process devices are available!
			getView().disablePrageMenuItem();
		}
		boolean stackPlatePresent = false;
		for (AbstractStackingDevice stackingDevice : deviceManager.getStackingFromDevices()) {
			if (stackingDevice instanceof BasicStackPlate) {
				stackPlatePresent = true;
			}
		}
		if (!stackPlatePresent) {
			getView().disableBasicStackPlateMenuItem();
		}
	}

	@Override
	public void setTextFieldListener(final TextInputControlListener listener) {
		userFramesConfigurePresenter.setTextFieldListener(listener);
		basicStackPlateConfigurePresenter.setTextFieldListener(listener);
		cncMachineConfigurePresenter.setTextFieldListener(listener);
		cncMachineClampingsPresenter.setTextFieldListener(listener);
		prageDeviceConfigurePresenter.setTextFieldListener(listener);
	}

	@Override
	protected void setPresenter() {
		getView().setPresenter(this);
	}

	@Override
	public void openFirst() {
		
	}

	@Override
	public void setBlocked(final boolean blocked) { }

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
	
	@Override
	public boolean isConfigured() {
		return false;
	}

	@Override
	public void unregisterListeners() {
		cncMachineConfigurePresenter.unregister();
	}

}
