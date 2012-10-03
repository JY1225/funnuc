package eu.robojob.irscw.ui.main.configure.device;

import org.apache.log4j.Logger;

import eu.robojob.irscw.external.device.AbstractDevice;
import eu.robojob.irscw.external.device.DeviceManager;
import eu.robojob.irscw.ui.main.configure.AbstractFormPresenter;
import eu.robojob.irscw.ui.main.model.DeviceInformation;

public class BasicStackPlateConfigurePresenter extends AbstractFormPresenter<BasicStackPlateConfigureView, BasicStackPlateMenuPresenter> {

	private DeviceInformation deviceInfo;
	private DeviceManager deviceManager;
	
	private static Logger logger = Logger.getLogger(BasicStackPlateConfigurePresenter.class);
	
	public BasicStackPlateConfigurePresenter(BasicStackPlateConfigureView view, DeviceInformation deviceInfo, DeviceManager deviceManager) {
		super(view);
		this.deviceInfo = deviceInfo;
		this.deviceManager = deviceManager;
		view.setDeviceInfo(deviceInfo);
		view.setStackingDeviceIds(deviceManager.getStackingDeviceIds());
		view.build();
	}

	@Override
	public void setPresenter() {
		view.setPresenter(this);
	}
	
	public void changedDevice(String deviceId) {
		logger.debug("changed device to: " + deviceId);
		if (deviceId != null) {
			AbstractDevice device = deviceManager.getDeviceById(deviceId);
			if (deviceInfo.getDevice() != device) {
				// TODO: change device!
			}
		}
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
