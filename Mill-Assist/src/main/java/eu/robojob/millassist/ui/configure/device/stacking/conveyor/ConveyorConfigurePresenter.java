package eu.robojob.millassist.ui.configure.device.stacking.conveyor;

import eu.robojob.millassist.external.device.DeviceManager;
import eu.robojob.millassist.ui.general.AbstractFormPresenter;
import eu.robojob.millassist.ui.general.model.DeviceInformation;

public class ConveyorConfigurePresenter extends AbstractFormPresenter<ConveyorConfigureView, ConveyorMenuPresenter> {

	private DeviceInformation deviceInfo;

	public ConveyorConfigurePresenter(final ConveyorConfigureView view, final DeviceInformation deviceInfo, final DeviceManager deviceManager) {
		super(view);
		view.setDeviceInfo(deviceInfo);
		view.setStackingDeviceIds(deviceManager.getStackingDeviceNames());
		this.deviceInfo = deviceInfo;
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
