package eu.robojob.irscw.ui.main.configure.device;

import eu.robojob.irscw.external.device.DeviceManager;
import eu.robojob.irscw.process.PickStep;
import eu.robojob.irscw.process.PutStep;
import eu.robojob.irscw.ui.main.configure.AbstractMenuPresenter;
import eu.robojob.irscw.ui.main.model.DeviceInformation;

public class DeviceMenuFactory {
	
	private CNCMillingMachineMenuPresenter cncMillingMachineMenuPresenter;	
	private DeviceManager deviceManager;
	private CNCMillingMachineConfigurePresenter cncMillingMachineConfigurePresenter;
	private CNCMillingMachinePickPresenter cncMillingMachinePickPresenter;
	private CNCMillingMachinePutPresenter cncMillingMachinePutPresenter;
	private BasicStackPlateMenuPresenter basicStackPlateMenuPresenter;
	
	public DeviceMenuFactory(DeviceManager deviceManager) {
		this.deviceManager = deviceManager;
	}
	
	public AbstractMenuPresenter<?> getDeviceMenu(DeviceInformation deviceInfo) {
		switch(deviceInfo.getType()) {
			case CNC_MACHINE:
				return getCncMillingMachineMenuPresenter(deviceInfo);
			case BASIC_STACK_PLATE:
				return getBasicStackPlateMenuPresenter(deviceInfo);
			default:
				return null;
		}
	}
	
	private CNCMillingMachineMenuPresenter getCncMillingMachineMenuPresenter(DeviceInformation deviceInfo) {
		if ((cncMillingMachineMenuPresenter == null)||(!cncMillingMachineMenuPresenter.getDeviceInformation().equals(deviceInfo))) {
			DeviceMenuView view = new DeviceMenuView();
			cncMillingMachineMenuPresenter = new CNCMillingMachineMenuPresenter(view, deviceInfo, getCncMillingMachineConfigurePresenter(deviceInfo), getCNCMillingMachinePickPresenter(deviceInfo.getPickStep()),
					getCNCMillingMachinePutPresenter(deviceInfo.getPutStep()));
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
	
	private CNCMillingMachinePickPresenter getCNCMillingMachinePickPresenter(PickStep pickStep) {
		if (cncMillingMachinePickPresenter == null) {
			CNCMillingMachinePickView view = new CNCMillingMachinePickView();
			cncMillingMachinePickPresenter = new CNCMillingMachinePickPresenter(view, pickStep);
		}
		return cncMillingMachinePickPresenter;
	}
	
	private CNCMillingMachinePutPresenter getCNCMillingMachinePutPresenter(PutStep putStep) {
		if (cncMillingMachinePutPresenter == null) {
			CNCMillingMachinePutView view = new CNCMillingMachinePutView();
			cncMillingMachinePutPresenter = new CNCMillingMachinePutPresenter(view, putStep);
		}
		return cncMillingMachinePutPresenter;
	}
	
	public BasicStackPlateMenuPresenter getBasicStackPlateMenuPresenter(DeviceInformation deviceInfo) {
		if (basicStackPlateMenuPresenter == null) {
			StackingDeviceMenuView stackingDeviceMenuView = new StackingDeviceMenuView();
			basicStackPlateMenuPresenter = new BasicStackPlateMenuPresenter(stackingDeviceMenuView,deviceInfo);
		}
		return basicStackPlateMenuPresenter;
	}
}
