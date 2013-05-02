package eu.robojob.irscw.ui.configure.device.stacking;

import eu.robojob.irscw.external.device.DeviceManager;
import eu.robojob.irscw.ui.general.AbstractFormPresenter;
import eu.robojob.irscw.ui.general.model.DeviceInformation;

public class BasicStackPlateConfigurePresenter extends AbstractFormPresenter<BasicStackPlateConfigureView, BasicStackPlateMenuPresenter> {

	private DeviceInformation deviceInfo;
		
	public BasicStackPlateConfigurePresenter(final BasicStackPlateConfigureView view, final DeviceInformation deviceInfo, final DeviceManager deviceManager) {
		super(view);
		this.deviceInfo = deviceInfo;
		view.setDeviceInfo(deviceInfo);
		view.setStackingDeviceIds(deviceManager.getStackingDeviceNames());
		view.build();
	}

	@Override
	public void setPresenter() {
		getView().setPresenter(this);
	}
	
	public void changedDevice(final String deviceId) {
		// TODO implement!
	}

	@Override
	public boolean isConfigured() {
		if (deviceInfo.getDevice() != null) {
			return true;
		}
		return false;
	}

}
