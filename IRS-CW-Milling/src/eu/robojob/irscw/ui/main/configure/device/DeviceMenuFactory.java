package eu.robojob.irscw.ui.main.configure.device;

import eu.robojob.irscw.external.device.DeviceType;
import eu.robojob.irscw.ui.main.model.DeviceInformation;

public class DeviceMenuFactory {
	
	private CNCMillingMachineMenuPresenter cncMillingMachineMenuPresenter;	
	
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
			cncMillingMachineMenuPresenter = new CNCMillingMachineMenuPresenter(view, deviceInfo);
		}
		return cncMillingMachineMenuPresenter;
	}
}
