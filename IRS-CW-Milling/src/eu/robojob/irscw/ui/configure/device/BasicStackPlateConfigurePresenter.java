package eu.robojob.irscw.ui.configure.device;

import eu.robojob.irscw.external.device.DeviceManager;
import eu.robojob.irscw.ui.configure.AbstractFormPresenter;
import eu.robojob.irscw.ui.main.model.DeviceInformation;

public class BasicStackPlateConfigurePresenter extends AbstractFormPresenter<BasicStackPlateConfigureView, BasicStackPlateMenuPresenter> {

	private DeviceInformation deviceInfo;
	
//	private static Logger logger = LogManager.getLogger(BasicStackPlateConfigurePresenter.class.getName());
	
	public BasicStackPlateConfigurePresenter(BasicStackPlateConfigureView view, DeviceInformation deviceInfo, DeviceManager deviceManager) {
		super(view);
		this.deviceInfo = deviceInfo;
		view.setDeviceInfo(deviceInfo);
		view.setStackingDeviceIds(deviceManager.getStackingDeviceIds());
		view.build();
	}

	@Override
	public void setPresenter() {
		view.setPresenter(this);
	}
	
	public void changedDevice(String deviceId) {
		// TODO implement!
	}

	@Override
	public boolean isConfigured() {
		if (deviceInfo.getDevice() != null) {
			return true;
		} else {
			return false;
		}
	}

}
