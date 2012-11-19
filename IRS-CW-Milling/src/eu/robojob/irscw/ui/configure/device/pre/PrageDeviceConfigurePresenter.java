package eu.robojob.irscw.ui.configure.device.pre;

import eu.robojob.irscw.external.device.DeviceManager;
import eu.robojob.irscw.ui.configure.AbstractFormPresenter;
import eu.robojob.irscw.ui.main.model.DeviceInformation;

public class PrageDeviceConfigurePresenter extends AbstractFormPresenter<PrageDeviceConfigureView, PrageDeviceMenuPresenter> {

	private DeviceInformation deviceInfo;
	
	public PrageDeviceConfigurePresenter(PrageDeviceConfigureView view, DeviceInformation deviceInfo, DeviceManager deviceManager) {
		super(view);
		this.deviceInfo = deviceInfo;
		view.setPreProcessingDeviceIds(deviceManager.getPreProcessingDeviceIds());
		view.build();
	}
	
	public void changedDevice(String deviceId) {
		// TODO: changed device!
	}

	@Override
	public void setPresenter() {
		view.setPresenter(this);
	}

	@Override
	public boolean isConfigured() {
		return true;
	}

}
