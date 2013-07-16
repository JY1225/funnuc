package eu.robojob.millassist.ui.configure.device.stacking.stackplate;

import eu.robojob.millassist.external.device.DeviceManager;
import eu.robojob.millassist.ui.general.AbstractFormPresenter;
import eu.robojob.millassist.ui.general.model.DeviceInformation;

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
		// throw new IllegalStateException("Not yet implemented");
	}

	@Override
	public boolean isConfigured() {
		if (deviceInfo.getDevice() != null) {
			return true;
		}
		return false;
	}

}
