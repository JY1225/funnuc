package eu.robojob.millassist.ui.configure.device;

import java.util.HashMap;
import java.util.Map;

import eu.robojob.millassist.external.device.ClampingManner;
import eu.robojob.millassist.external.device.DeviceManager;
import eu.robojob.millassist.external.device.DeviceSettings;
import eu.robojob.millassist.external.device.stacking.stackplate.BasicStackPlate;
import eu.robojob.millassist.external.device.stacking.stackplate.BasicStackPlateSettings;
import eu.robojob.millassist.process.PickStep;
import eu.robojob.millassist.process.PutStep;
import eu.robojob.millassist.ui.configure.AbstractMenuPresenter;
import eu.robojob.millassist.ui.configure.device.processing.cnc.CNCMillingMachineConfigurePresenter;
import eu.robojob.millassist.ui.configure.device.processing.cnc.CNCMillingMachineConfigureView;
import eu.robojob.millassist.ui.configure.device.processing.cnc.CNCMillingMachineMenuPresenter;
import eu.robojob.millassist.ui.configure.device.processing.cnc.CNCMillingMachineMenuView;
import eu.robojob.millassist.ui.configure.device.processing.cnc.CNCMillingMachinePickPresenter;
import eu.robojob.millassist.ui.configure.device.processing.cnc.CNCMillingMachinePickView;
import eu.robojob.millassist.ui.configure.device.processing.cnc.CNCMillingMachinePutPresenter;
import eu.robojob.millassist.ui.configure.device.processing.cnc.CNCMillingMachinePutView;
import eu.robojob.millassist.ui.configure.device.processing.cnc.CNCMillingMachineWorkPiecePresenter;
import eu.robojob.millassist.ui.configure.device.processing.cnc.CNCMillingMachineWorkPieceView;
import eu.robojob.millassist.ui.configure.device.processing.prage.PrageDeviceConfigurePresenter;
import eu.robojob.millassist.ui.configure.device.processing.prage.PrageDeviceConfigureView;
import eu.robojob.millassist.ui.configure.device.processing.prage.PrageDeviceMenuPresenter;
import eu.robojob.millassist.ui.configure.device.stacking.BasicStackPlateConfigurePresenter;
import eu.robojob.millassist.ui.configure.device.stacking.BasicStackPlateConfigureView;
import eu.robojob.millassist.ui.configure.device.stacking.BasicStackPlateLayoutPresenter;
import eu.robojob.millassist.ui.configure.device.stacking.BasicStackPlateMenuPresenter;
import eu.robojob.millassist.ui.configure.device.stacking.BasicStackPlateRawWorkPiecePresenter;
import eu.robojob.millassist.ui.configure.device.stacking.BasicStackPlateRawWorkPieceView;
import eu.robojob.millassist.ui.configure.device.stacking.StackingDeviceMenuView;
import eu.robojob.millassist.ui.general.AbstractFormPresenter;
import eu.robojob.millassist.ui.general.device.stacking.BasicStackPlateLayoutView;
import eu.robojob.millassist.ui.general.model.DeviceInformation;

public class DeviceMenuFactory {
	
	private DeviceManager deviceManager;
	
	private Map<Integer, AbstractMenuPresenter<?>> presentersBuffer;
			
	public DeviceMenuFactory(final DeviceManager deviceManager) {
		this.deviceManager = deviceManager;
		presentersBuffer = new HashMap<Integer, AbstractMenuPresenter<?>>();
	}
	
	public synchronized AbstractMenuPresenter<?> getDeviceMenu(final DeviceInformation deviceInfo) {
		AbstractMenuPresenter<?> menuPresenter = presentersBuffer.get(deviceInfo.getIndex());
		if (menuPresenter == null) {
			switch(deviceInfo.getType()) {
				case CNC_MACHINE:
					menuPresenter = getCncMillingMachineMenuPresenter(deviceInfo);
					break;
				case BASIC_STACK_PLATE:
					menuPresenter = getBasicStackPlateMenuPresenter(deviceInfo);
					break;
				case PRE_PROCESSING:
					menuPresenter = getPrageDeviceMenuPresenter(deviceInfo);
					break;
				default:
					menuPresenter = null;
			}
			presentersBuffer.put(deviceInfo.getIndex(), menuPresenter);
		}
		return menuPresenter;
	}
	
	private PrageDeviceMenuPresenter getPrageDeviceMenuPresenter(final DeviceInformation deviceInfo) {
		DeviceMenuView view = new DeviceMenuView(false, false, true);
		PrageDeviceMenuPresenter prageDeviceMenuPresenter = new PrageDeviceMenuPresenter(view, deviceInfo, getPrageDeviceConfiguerPresenter(deviceInfo));
		return prageDeviceMenuPresenter;
	}
	
	private PrageDeviceConfigurePresenter getPrageDeviceConfiguerPresenter(final DeviceInformation deviceInfo) {
		PrageDeviceConfigureView view = new PrageDeviceConfigureView(deviceInfo);
		PrageDeviceConfigurePresenter presenter = new PrageDeviceConfigurePresenter(view, deviceInfo, deviceManager);
		return presenter;
	}
	
	private CNCMillingMachineMenuPresenter getCncMillingMachineMenuPresenter(final DeviceInformation deviceInfo) {
		CNCMillingMachineMenuView view = new CNCMillingMachineMenuView();
		CNCMillingMachineMenuPresenter cncMillingMachineMenuPresenter = new CNCMillingMachineMenuPresenter(view, deviceInfo, getCncMillingMachineConfigurePresenter(deviceInfo), getCNCMillingMachinePickPresenter(deviceInfo.getPickStep(), deviceInfo.getDeviceSettings()),
				getCNCMillingMachinePutPresenter(deviceInfo.getPutStep(), deviceInfo.getDeviceSettings()), getCncMillingMachineWorkPiecePresenter(deviceInfo));
		return cncMillingMachineMenuPresenter;
	}
	
	private CNCMillingMachineWorkPiecePresenter getCncMillingMachineWorkPiecePresenter(final DeviceInformation deviceInfo) {
		CNCMillingMachineWorkPieceView view = new CNCMillingMachineWorkPieceView();
		CNCMillingMachineWorkPiecePresenter presenter = new CNCMillingMachineWorkPiecePresenter(view, deviceInfo.getPickStep(), deviceInfo.getDeviceSettings());
		return presenter;
	}
	
	private CNCMillingMachineConfigurePresenter getCncMillingMachineConfigurePresenter(final DeviceInformation deviceInfo) {
		CNCMillingMachineConfigureView view = new CNCMillingMachineConfigureView();
		CNCMillingMachineConfigurePresenter cncMillingMachineConfigurePresenter = new CNCMillingMachineConfigurePresenter(view, deviceInfo, deviceManager);
		return cncMillingMachineConfigurePresenter;
	}
	
	private CNCMillingMachinePickPresenter getCNCMillingMachinePickPresenter(final PickStep pickStep, final DeviceSettings deviceSettings) {
		CNCMillingMachinePickView view = new CNCMillingMachinePickView();
		CNCMillingMachinePickPresenter cncMillingMachinePickPresenter = new CNCMillingMachinePickPresenter(view, pickStep, deviceSettings);
		return cncMillingMachinePickPresenter;
	}
	
	private CNCMillingMachinePutPresenter getCNCMillingMachinePutPresenter(final PutStep putStep, final DeviceSettings deviceSettings) {
		CNCMillingMachinePutView view = new CNCMillingMachinePutView();
		CNCMillingMachinePutPresenter cncMillingMachinePutPresenter = new CNCMillingMachinePutPresenter(view, putStep, deviceSettings);
		return cncMillingMachinePutPresenter;
	}
	
	public BasicStackPlateMenuPresenter getBasicStackPlateMenuPresenter(final DeviceInformation deviceInfo) {
		StackingDeviceMenuView stackingDeviceMenuView = new StackingDeviceMenuView();
		BasicStackPlateMenuPresenter basicStackPlateMenuPresenter = new BasicStackPlateMenuPresenter(stackingDeviceMenuView, deviceInfo, getBasicStackPlateConfigurePresenter(deviceInfo), 
				getBasicStackPlateWorkPiecePresenter(deviceInfo), getBasicStackPlateLayoutPresenter(deviceInfo));
		return basicStackPlateMenuPresenter;
	}
	
	// we always create a new, because the stacker can (and probably will) be used more than once (first and last)
	public BasicStackPlateConfigurePresenter getBasicStackPlateConfigurePresenter(final DeviceInformation deviceInfo) {
		BasicStackPlateConfigureView view = new BasicStackPlateConfigureView();
		BasicStackPlateConfigurePresenter basicStackPlateConfigurePresenter = new BasicStackPlateConfigurePresenter(view, deviceInfo, deviceManager);
		return basicStackPlateConfigurePresenter;
	}
	
	public AbstractFormPresenter<?, BasicStackPlateMenuPresenter> getBasicStackPlateWorkPiecePresenter(final DeviceInformation deviceInfo) {
		if (deviceInfo.getPickStep() != null) {
			BasicStackPlateRawWorkPieceView view = new BasicStackPlateRawWorkPieceView();
			BasicStackPlateRawWorkPiecePresenter basicStackPlateWorkPiecePresenter = new BasicStackPlateRawWorkPiecePresenter(view, deviceInfo.getPickStep(), (BasicStackPlateSettings) deviceInfo.getDeviceSettings());
			return basicStackPlateWorkPiecePresenter;
		} else {
			return null;
		}
	}
	
	public BasicStackPlateLayoutPresenter getBasicStackPlateLayoutPresenter(final DeviceInformation deviceInfo) {
		BasicStackPlateLayoutView<BasicStackPlateLayoutPresenter> view = new BasicStackPlateLayoutView<BasicStackPlateLayoutPresenter>();
		ClampingManner clampingType = null;
		if (deviceInfo.getPickStep() != null) {
			clampingType = deviceInfo.getPickStep().getProcessFlow().getClampingType();
		} else if (deviceInfo.getPutStep() != null) {
			clampingType = deviceInfo.getPutStep().getProcessFlow().getClampingType();
		}
		BasicStackPlateLayoutPresenter basicStackPlateLayoutPresenter = new BasicStackPlateLayoutPresenter(view, (BasicStackPlate) deviceInfo.getDevice(), clampingType);
		return basicStackPlateLayoutPresenter;
	}
	
	public void clearBuffer() {
		for (AbstractMenuPresenter<?> presenter : presentersBuffer.values()) {
			presenter.unregisterListeners();
		}
		presentersBuffer.clear();
	}
}
