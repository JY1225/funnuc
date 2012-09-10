package eu.robojob.irscw.ui.main.configure.device;

import org.apache.log4j.Logger;

import eu.robojob.irscw.external.device.DeviceManager;
import eu.robojob.irscw.ui.main.configure.AbstractFormPresenter;
import eu.robojob.irscw.ui.main.model.DeviceInformation;

public class CNCMillingMachineConfigurePresenter extends AbstractFormPresenter<CNCMillingMachineConfigureView, CNCMillingMachineMenuPresenter>{

	private DeviceInformation deviceInfo;
	private DeviceManager deviceManager;
	
	private static Logger logger = Logger.getLogger(CNCMillingMachineConfigurePresenter.class);
	
	public CNCMillingMachineConfigurePresenter(CNCMillingMachineConfigureView view, DeviceInformation deviceInfo, DeviceManager deviceManager) {
		super(view);
		this.deviceInfo = deviceInfo;
		this.deviceManager = deviceManager;
		view.setDeviceInfo(deviceInfo);
		view.setCNCMillingMachineIds(deviceManager.getCNCMachineIds());
		view.build();
	}

	@Override
	public void setPresenter() {
		view.setPresenter(this);
	}
	
	public void changedDevice(String deviceId) {
		logger.debug("changed device to: " + deviceId);
	}
	
	public void changedWorkArea(String workAreaId) {
		logger.debug("changed workarea to: " + workAreaId);
	}
	
}
