package eu.robojob.irscw.ui.configure.device;

import java.util.HashMap;
import java.util.Map;

import eu.robojob.irscw.external.device.BasicStackPlate;
import eu.robojob.irscw.external.device.BasicStackPlate.BasicStackPlateSettings;
import eu.robojob.irscw.external.device.CNCMillingMachine.CNCMillingMachineSettings;
import eu.robojob.irscw.external.device.DeviceManager;
import eu.robojob.irscw.process.PickStep;
import eu.robojob.irscw.process.PutStep;
import eu.robojob.irscw.ui.configure.AbstractMenuPresenter;
import eu.robojob.irscw.ui.main.model.DeviceInformation;

public class DeviceMenuFactory {
	
	private DeviceManager deviceManager;
	
	private Map<DeviceInformation, AbstractMenuPresenter<?>> presentersBuffer;
	
	public DeviceMenuFactory(DeviceManager deviceManager) {
		this.deviceManager = deviceManager;
		presentersBuffer = new HashMap<DeviceInformation, AbstractMenuPresenter<?>>();
	}
	
	public AbstractMenuPresenter<?> getDeviceMenu(DeviceInformation deviceInfo) {
		AbstractMenuPresenter<?> menuPresenter = presentersBuffer.get(deviceInfo);
		if (menuPresenter == null) {
			switch(deviceInfo.getType()) {
				case CNC_MACHINE:
					menuPresenter = getCncMillingMachineMenuPresenter(deviceInfo);
					break;
				case BASIC_STACK_PLATE:
					menuPresenter = getBasicStackPlateMenuPresenter(deviceInfo);
					break;
				default:
					menuPresenter = null;
			}
			presentersBuffer.put(deviceInfo, menuPresenter);
		}
		return menuPresenter;
	}
	
	private CNCMillingMachineMenuPresenter getCncMillingMachineMenuPresenter(DeviceInformation deviceInfo) {
		DeviceMenuView view = new DeviceMenuView();
		CNCMillingMachineMenuPresenter cncMillingMachineMenuPresenter = new CNCMillingMachineMenuPresenter(view, deviceInfo, getCncMillingMachineConfigurePresenter(deviceInfo), getCNCMillingMachinePickPresenter(deviceInfo.getPickStep(), (CNCMillingMachineSettings) deviceInfo.getDeviceSettings()),
				getCNCMillingMachinePutPresenter(deviceInfo.getPutStep(), (CNCMillingMachineSettings) deviceInfo.getDeviceSettings()));
		return cncMillingMachineMenuPresenter;
	}
	
	private CNCMillingMachineConfigurePresenter getCncMillingMachineConfigurePresenter(DeviceInformation deviceInfo) {
		CNCMillingMachineConfigureView view = new CNCMillingMachineConfigureView();
		CNCMillingMachineConfigurePresenter cncMillingMachineConfigurePresenter = new CNCMillingMachineConfigurePresenter(view, deviceInfo, deviceManager);
		return cncMillingMachineConfigurePresenter;
	}
	
	private CNCMillingMachinePickPresenter getCNCMillingMachinePickPresenter(PickStep pickStep, CNCMillingMachineSettings deviceSettings) {
		CNCMillingMachinePickView view = new CNCMillingMachinePickView();
		CNCMillingMachinePickPresenter cncMillingMachinePickPresenter = new CNCMillingMachinePickPresenter(view, pickStep, deviceSettings);
		return cncMillingMachinePickPresenter;
	}
	
	private CNCMillingMachinePutPresenter getCNCMillingMachinePutPresenter(PutStep putStep, CNCMillingMachineSettings deviceSettings) {
		CNCMillingMachinePutView view = new CNCMillingMachinePutView();
		CNCMillingMachinePutPresenter cncMillingMachinePutPresenter = new CNCMillingMachinePutPresenter(view, putStep, deviceSettings);
		return cncMillingMachinePutPresenter;
	}
	
	public BasicStackPlateMenuPresenter getBasicStackPlateMenuPresenter(DeviceInformation deviceInfo) {
		StackingDeviceMenuView stackingDeviceMenuView = new StackingDeviceMenuView();
		BasicStackPlateMenuPresenter basicStackPlateMenuPresenter = new BasicStackPlateMenuPresenter(stackingDeviceMenuView,deviceInfo, getBasicStackPlateConfigurePresenter(deviceInfo), 
				getBasicStackPlateWorkPiecePresenter(deviceInfo), getBasicStackPlateLayoutPresenter(deviceInfo));
		return basicStackPlateMenuPresenter;
	}
	
	// we always create a new, because the stacker can (and probably will) be used more than once (first and last)
	public BasicStackPlateConfigurePresenter getBasicStackPlateConfigurePresenter(DeviceInformation deviceInfo) {
		BasicStackPlateConfigureView view = new BasicStackPlateConfigureView();
		BasicStackPlateConfigurePresenter basicStackPlateConfigurePresenter = new BasicStackPlateConfigurePresenter(view, deviceInfo, deviceManager);
		return basicStackPlateConfigurePresenter;
	}
	
	public BasicStackPlateWorkPiecePresenter getBasicStackPlateWorkPiecePresenter(DeviceInformation deviceInfo) {
		if (deviceInfo.getPickStep() != null) {
			BasicStackPlateWorkPieceView view = new BasicStackPlateWorkPieceView();
			BasicStackPlateWorkPiecePresenter basicStackPlateWorkPiecePresenter = new BasicStackPlateWorkPiecePresenter(view, deviceInfo.getPickStep(), (BasicStackPlateSettings) deviceInfo.getDeviceSettings());
			return basicStackPlateWorkPiecePresenter;
		} else {
			return null;
		}
	}
	
	public BasicStackPlateLayoutPresenter getBasicStackPlateLayoutPresenter(DeviceInformation deviceInfo) {
		BasicStackPlateLayoutView view = new BasicStackPlateLayoutView();
		BasicStackPlateLayoutPresenter basicStackPlateLayoutPresenter = new BasicStackPlateLayoutPresenter(view, (BasicStackPlate) deviceInfo.getDevice());
		return basicStackPlateLayoutPresenter;
	}
}
