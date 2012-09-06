package eu.robojob.irscw.ui.main.configure.device;

import eu.robojob.irscw.ui.main.configure.AbstractMenuPresenter;
import eu.robojob.irscw.ui.main.model.DeviceInformation;

public class DeviceMenuPresenter extends AbstractMenuPresenter<DeviceMenuView> {

	private DeviceInformation deviceInfo;
	
	public DeviceMenuPresenter(DeviceMenuView view, DeviceInformation deviceInfo) {
		super(view);
		this.deviceInfo = deviceInfo;
		view.setDeviceInfo(deviceInfo);
	}

	@Override
	protected void setPresenter() {
		view.setPresenter(this);
	}

	public void configurePick() {
		view.setConfigurePickActive();
	}
	
	public void configurePut() {
		view.setConfigurePutActive();
	}
	
	public void configureDevice() {
		view.setProcessingActive();
	}
	
	@Override
	public void openFirst() {
		if (deviceInfo.getProcessingStep() != null) {
			configureDevice();
		} else if (deviceInfo.getPickStep() != null) {
			configurePick();
		} else if (deviceInfo.getPutStep() != null) {
			configurePut();
		}
	}
}
