package eu.robojob.irscw.ui.main.configure.device;

import eu.robojob.irscw.ui.main.model.DeviceInformation;

public class CNCMillingMachineMenuPresenter extends AbstractDeviceMenuPresenter {

	public CNCMillingMachineMenuPresenter(DeviceMenuView view, DeviceInformation deviceInfo) {
		super(view, deviceInfo);
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
	}

	@Override
	public void setBlocked(boolean blocked) {
		
	}

}
