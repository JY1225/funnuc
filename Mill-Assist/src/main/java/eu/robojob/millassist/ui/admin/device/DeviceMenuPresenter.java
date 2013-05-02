package eu.robojob.millassist.ui.admin.device;

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
				final CNCMachineClampingsPresenter cncMachineClamingsPresenter, final PrageDeviceConfigurePresenter prageDeviceConfigurePresenter) {
		super(view);
		this.userFramesConfigurePresenter = userFramesConfigurePresenter;
		this.basicStackPlateConfigurePresenter = basicStackPlateConfigurePresenter;
		this.cncMachineConfigurePresenter = cncMachineConfigurePresenter;
		this.cncMachineClampingsPresenter = cncMachineClamingsPresenter;
		this.prageDeviceConfigurePresenter = prageDeviceConfigurePresenter;
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

}
