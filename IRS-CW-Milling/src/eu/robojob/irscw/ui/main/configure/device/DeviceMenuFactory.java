package eu.robojob.irscw.ui.main.configure.device;

import eu.robojob.irscw.external.device.DeviceManager;
import eu.robojob.irscw.external.device.DeviceType;
import eu.robojob.irscw.ui.main.model.DeviceInformation;

public class DeviceMenuFactory {
	
	private CNCMillingMachineMenuPresenter cncMillingMachineMenuPresenter;	
	private DeviceManager deviceManager;
	private CNCMillingMachineConfigurePresenter cncMillingMachineConfigurePresenter;
	
	public DeviceMenuFactory(DeviceManager deviceManager) {
		this.deviceManager = deviceManager;
	}
	
	public AbstractDeviceMenuPresenter getDeviceMenu(DeviceInformation deviceInfo) {
		if (deviceInfo.getType() == DeviceType.CNC_MACHINE) {
			return getCncMillingMachineMenuPresenter(deviceInfo);
		} else {
			return null;
		}
	}
	
	private CNCMillingMachineMenuPresenter getCncMillingMachineMenuPresenter(DeviceInformation deviceInfo) {
		if ((cncMillingMachineMenuPresenter == null)||(!cncMillingMachineMenuPresenter.getDeviceInformation().equals(deviceInfo))) {
			DeviceMenuView view = new DeviceMenuView();
			cncMillingMachineMenuPresenter = new CNCMillingMachineMenuPresenter(view, deviceInfo, getCncMillingMachineConfigurePresenter(deviceInfo));
		}
		return cncMillingMachineMenuPresenter;
	}
	
	private CNCMillingMachineConfigurePresenter getCncMillingMachineConfigurePresenter(DeviceInformation deviceInfo) {
		if (cncMillingMachineConfigurePresenter == null) {
			CNCMillingMachineConfigureView view = new CNCMillingMachineConfigureView();
			cncMillingMachineConfigurePresenter = new CNCMillingMachineConfigurePresenter(view, deviceInfo, getDeviceManager());
		}
		return cncMillingMachineConfigurePresenter;
	}
	
	private DeviceManager getDeviceManager() {
		if (deviceManager == null) {
			deviceManager = new DeviceManager();
		}
		return deviceManager;
	}
}
