package eu.robojob.irscw.ui.main.configure.device;

import eu.robojob.irscw.ui.main.model.DeviceInformation;

public class CNCMillingMachineMenuPresenter extends AbstractDeviceMenuPresenter {

	private CNCMillingMachineConfigurePresenter cncMillingMachineConfigurePresenter;
	
	public CNCMillingMachineMenuPresenter(DeviceMenuView view, DeviceInformation deviceInfo, CNCMillingMachineConfigurePresenter cncMillingMachineConfigurePresenter) {
		super(view, deviceInfo);
		this.cncMillingMachineConfigurePresenter = cncMillingMachineConfigurePresenter;
	}

	@Override
	public void configurePick() {
		view.setConfigurePickActive();
	}

	@Override
	public void configurePut() {
		view.setConfigurePutActive();
	}

	@Override
	public void configureDevice() {
		view.setProcessingActive();
		parent.setBottomRightView(cncMillingMachineConfigurePresenter.getView());
	}

	@Override
	public void setBlocked(boolean blocked) {
		
	}

}
