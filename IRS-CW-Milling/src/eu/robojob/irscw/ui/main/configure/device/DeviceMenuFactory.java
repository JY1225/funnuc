package eu.robojob.irscw.ui.main.configure.device;

import eu.robojob.irscw.external.device.BasicStackPlate;
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
	private BasicStackPlateConfigurePresenter basicStackPlateConfigurePresenter;
	private BasicStackPlateWorkPiecePresenter basicStackPlateWorkPiecePresenter;
	private BasicStackPlateLayoutPresenter basicStackPlateLayoutPresenter;
	
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
		StackingDeviceMenuView stackingDeviceMenuView = new StackingDeviceMenuView();
		basicStackPlateMenuPresenter = new BasicStackPlateMenuPresenter(stackingDeviceMenuView,deviceInfo, getBasicStackPlateConfigurePresenter(deviceInfo), 
				getBasicStackPlateWorkPiecePresenter(deviceInfo), getBasicStackPlateLayoutPresenter(deviceInfo));
		return basicStackPlateMenuPresenter;
	}
	
	// we always create a new, because the stacker can (and probably will) be used more than once (first and last)
	public BasicStackPlateConfigurePresenter getBasicStackPlateConfigurePresenter(DeviceInformation deviceInfo) {
		BasicStackPlateConfigureView view = new BasicStackPlateConfigureView();
		basicStackPlateConfigurePresenter = new BasicStackPlateConfigurePresenter(view, deviceInfo, getDeviceManager());
		return basicStackPlateConfigurePresenter;
	}
	
	public BasicStackPlateWorkPiecePresenter getBasicStackPlateWorkPiecePresenter(DeviceInformation deviceInfo) {
		if (deviceInfo.getPickStep() != null) {
			BasicStackPlateWorkPieceView view = new BasicStackPlateWorkPieceView();
			basicStackPlateWorkPiecePresenter = new BasicStackPlateWorkPiecePresenter(view, deviceInfo.getPickStep());
			return basicStackPlateWorkPiecePresenter;
		} else {
			return null;
		}
	}
	
	public BasicStackPlateLayoutPresenter getBasicStackPlateLayoutPresenter(DeviceInformation deviceInfo) {
		BasicStackPlateLayoutView view = new BasicStackPlateLayoutView();
		basicStackPlateLayoutPresenter = new BasicStackPlateLayoutPresenter(view, (BasicStackPlate) deviceInfo.getDevice());
		return basicStackPlateLayoutPresenter;
	}
}
