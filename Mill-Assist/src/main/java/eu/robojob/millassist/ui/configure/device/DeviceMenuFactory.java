package eu.robojob.millassist.ui.configure.device;

import java.util.HashMap;
import java.util.Map;

import eu.robojob.millassist.external.device.ClampingManner;
import eu.robojob.millassist.external.device.DeviceManager;
import eu.robojob.millassist.external.device.DeviceSettings;
import eu.robojob.millassist.external.device.stacking.conveyor.normal.Conveyor;
import eu.robojob.millassist.external.device.stacking.conveyor.normal.ConveyorSettings;
import eu.robojob.millassist.external.device.stacking.stackplate.AbstractStackPlateDeviceSettings;
import eu.robojob.millassist.external.device.stacking.stackplate.basicstackplate.BasicStackPlate;
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
import eu.robojob.millassist.ui.configure.device.processing.reversal.ReversalUnitConfigurePresenter;
import eu.robojob.millassist.ui.configure.device.processing.reversal.ReversalUnitConfigureView;
import eu.robojob.millassist.ui.configure.device.processing.reversal.ReversalUnitMenuPresenter;
import eu.robojob.millassist.ui.configure.device.processing.reversal.ReversalUnitPickPresenter;
import eu.robojob.millassist.ui.configure.device.processing.reversal.ReversalUnitPickView;
import eu.robojob.millassist.ui.configure.device.processing.reversal.ReversalUnitPutPresenter;
import eu.robojob.millassist.ui.configure.device.processing.reversal.ReversalUnitPutView;
import eu.robojob.millassist.ui.configure.device.stacking.ConfigureSmoothPresenter;
import eu.robojob.millassist.ui.configure.device.stacking.ConfigureSmoothView;
import eu.robojob.millassist.ui.configure.device.stacking.StackingDeviceConfigurePresenter;
import eu.robojob.millassist.ui.configure.device.stacking.StackingDeviceConfigureView;
import eu.robojob.millassist.ui.configure.device.stacking.StackingDeviceMenuView;
import eu.robojob.millassist.ui.configure.device.stacking.bin.OutputBinMenuPresenter;
import eu.robojob.millassist.ui.configure.device.stacking.conveyor.normal.ConveyorFinishedWorkPieceLayoutPresenter;
import eu.robojob.millassist.ui.configure.device.stacking.conveyor.normal.ConveyorMenuPresenter;
import eu.robojob.millassist.ui.configure.device.stacking.conveyor.normal.ConveyorRawWorkPieceOffsetPresenter;
import eu.robojob.millassist.ui.configure.device.stacking.conveyor.normal.ConveyorRawWorkPieceOffsetView;
import eu.robojob.millassist.ui.configure.device.stacking.conveyor.normal.ConveyorRawWorkPiecePresenter;
import eu.robojob.millassist.ui.configure.device.stacking.conveyor.normal.ConveyorRawWorkPieceView;
import eu.robojob.millassist.ui.configure.device.stacking.stackplate.BasicStackPlateLayoutPresenter;
import eu.robojob.millassist.ui.configure.device.stacking.stackplate.BasicStackPlateMenuPresenter;
import eu.robojob.millassist.ui.configure.device.stacking.stackplate.BasicStackPlateRawWorkPiecePresenter;
import eu.robojob.millassist.ui.configure.device.stacking.stackplate.BasicStackPlateRawWorkPieceView;
import eu.robojob.millassist.ui.general.AbstractFormPresenter;
import eu.robojob.millassist.ui.general.device.stacking.conveyor.normal.ConveyorFinishedWorkPieceLayoutView;
import eu.robojob.millassist.ui.general.device.stacking.conveyor.normal.ConveyorRawWorkPieceLayoutPresenter;
import eu.robojob.millassist.ui.general.device.stacking.conveyor.normal.ConveyorRawWorkPieceLayoutView;
import eu.robojob.millassist.ui.general.device.stacking.stackplate.BasicStackPlateLayoutView;
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
				case OUTPUT_BIN:
					menuPresenter = getOutputBinMenuPresenter(deviceInfo);
					break;
				case PRE_PROCESSING:
					menuPresenter = getPrageDeviceMenuPresenter(deviceInfo);
					break;
				case CONVEYOR:
					menuPresenter = getConveyorMenuPresenter(deviceInfo);
					break;
				case CONVEYOR_EATON:
					menuPresenter = getConveyorEatonMenuPresenter(deviceInfo);
					break;
				case POST_PROCESSING:
					menuPresenter = getReversalUnitMenuPresenter(deviceInfo);
					break;
				default:
					menuPresenter = null;
			}
			presentersBuffer.put(deviceInfo.getIndex(), menuPresenter);
		}
		return menuPresenter;
	}
	
	private OutputBinMenuPresenter getOutputBinMenuPresenter(final DeviceInformation deviceInfo) {
		StackingDeviceMenuView view = new StackingDeviceMenuView();
		OutputBinMenuPresenter menuPresenter = new OutputBinMenuPresenter(view, deviceInfo, getOutputBinConfigurePresenter(deviceInfo), 
				getOutputBinSmoothToPresenter(deviceInfo));
		return menuPresenter;
	}
	
	private ConfigureSmoothPresenter<OutputBinMenuPresenter> getOutputBinSmoothToPresenter(final DeviceInformation deviceInfo) {
		ConfigureSmoothView view = new ConfigureSmoothView();
		ConfigureSmoothPresenter<OutputBinMenuPresenter> presenter = new ConfigureSmoothPresenter<OutputBinMenuPresenter>(view, deviceInfo.getPutStep().getRobotSettings());
		return presenter;
	}
	
	private StackingDeviceConfigurePresenter getOutputBinConfigurePresenter(final DeviceInformation deviceInfo) {
		StackingDeviceConfigureView view = new StackingDeviceConfigureView();
		StackingDeviceConfigurePresenter presenter = new StackingDeviceConfigurePresenter(view, deviceInfo, deviceManager);
		return presenter;
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
	
	private ReversalUnitMenuPresenter getReversalUnitMenuPresenter(final DeviceInformation deviceInfo) {
		DeviceMenuView view = new DeviceMenuView(true, true, true);
		ReversalUnitMenuPresenter reversalUnitMenuPresenter = new ReversalUnitMenuPresenter(view, deviceInfo, getReversalUnitConfigurePresenter(deviceInfo),
				getReversalUnitPutPresenter(deviceInfo), getReversalUnitPickPresenter(deviceInfo));
		return reversalUnitMenuPresenter;
	}
	
	private ReversalUnitPickPresenter getReversalUnitPickPresenter(final DeviceInformation deviceInfo) {
		ReversalUnitPickView view = new ReversalUnitPickView();
		ReversalUnitPickPresenter presenter = new ReversalUnitPickPresenter(view, deviceInfo.getPickStep(), deviceInfo.getDeviceSettings());
		return presenter;
	}
	
	private ReversalUnitPutPresenter getReversalUnitPutPresenter(final DeviceInformation deviceInfo) {
		ReversalUnitPutView view = new ReversalUnitPutView();
		ReversalUnitPutPresenter presenter = new ReversalUnitPutPresenter(view, deviceInfo.getPutStep(), deviceInfo.getDeviceSettings());
		return presenter;
	}
	
	private ReversalUnitConfigurePresenter getReversalUnitConfigurePresenter(final DeviceInformation deviceInfo) {
		ReversalUnitConfigureView view = new ReversalUnitConfigureView(deviceInfo);
		ReversalUnitConfigurePresenter presenter = new ReversalUnitConfigurePresenter(view, deviceInfo, deviceManager);
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
		ConfigureSmoothView smoothView = new ConfigureSmoothView();
		ConfigureSmoothPresenter<BasicStackPlateMenuPresenter> smoothPickPresenter = null;
		ConfigureSmoothPresenter<BasicStackPlateMenuPresenter> smoothPutPresenter = null;
		if (deviceInfo.hasPickStep()) {
			smoothPickPresenter = new ConfigureSmoothPresenter<BasicStackPlateMenuPresenter>(smoothView, deviceInfo.getPickStep().getRobotSettings());
		} else if (deviceInfo.hasPutStep()) {
			smoothPutPresenter = new ConfigureSmoothPresenter<BasicStackPlateMenuPresenter>(smoothView, deviceInfo.getPutStep().getRobotSettings());
		} else {
			throw new IllegalStateException("No pick or put step for basic stack plate");
		}
		BasicStackPlateMenuPresenter basicStackPlateMenuPresenter = new BasicStackPlateMenuPresenter(stackingDeviceMenuView, deviceInfo, getBasicStackPlateConfigurePresenter(deviceInfo), 
				getBasicStackPlateWorkPiecePresenter(deviceInfo), getBasicStackPlateLayoutPresenter(deviceInfo), smoothPickPresenter, smoothPutPresenter);
		return basicStackPlateMenuPresenter;
	}
	
	// we always create a new, because the stacker can (and probably will) be used more than once (first and last)
	public StackingDeviceConfigurePresenter getBasicStackPlateConfigurePresenter(final DeviceInformation deviceInfo) {
		StackingDeviceConfigureView view = new StackingDeviceConfigureView();
		StackingDeviceConfigurePresenter basicStackPlateConfigurePresenter = new StackingDeviceConfigurePresenter(view, deviceInfo, deviceManager);
		return basicStackPlateConfigurePresenter;
	}
	
	public AbstractFormPresenter<?, BasicStackPlateMenuPresenter> getBasicStackPlateWorkPiecePresenter(final DeviceInformation deviceInfo) {
		if (deviceInfo.getPickStep() != null) {
			BasicStackPlateRawWorkPieceView view = new BasicStackPlateRawWorkPieceView();
			BasicStackPlateRawWorkPiecePresenter basicStackPlateWorkPiecePresenter = new BasicStackPlateRawWorkPiecePresenter(view, deviceInfo.getPickStep(), (AbstractStackPlateDeviceSettings) deviceInfo.getDeviceSettings());
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
	
	public eu.robojob.millassist.ui.configure.device.stacking.conveyor.eaton.ConveyorMenuPresenter getConveyorEatonMenuPresenter(final DeviceInformation deviceInfo) {
		StackingDeviceMenuView menuView = new StackingDeviceMenuView();
		eu.robojob.millassist.ui.configure.device.stacking.conveyor.eaton.ConveyorMenuPresenter conveyorMenuPresenter = null;
		ConfigureSmoothView smoothView = new ConfigureSmoothView();
		ConfigureSmoothPresenter<eu.robojob.millassist.ui.configure.device.stacking.conveyor.eaton.ConveyorMenuPresenter> smoothPresenter = null;
		if (deviceInfo.getPickStep() != null) {
			// first step
			smoothPresenter = new ConfigureSmoothPresenter<eu.robojob.millassist.ui.configure.device.stacking.conveyor.eaton.ConveyorMenuPresenter>(smoothView, deviceInfo.getPickStep().getRobotSettings());
			conveyorMenuPresenter = new eu.robojob.millassist.ui.configure.device.stacking.conveyor.eaton.ConveyorMenuPresenter(menuView, deviceInfo, getConveyorConfigurePresenter(deviceInfo), getConveyorEatonRawWorkPiecePresenter(deviceInfo), getEatonRawWorkPieceLayoutPresenter(deviceInfo), smoothPresenter, null);
		} else {
			// last step
			smoothPresenter = new ConfigureSmoothPresenter<eu.robojob.millassist.ui.configure.device.stacking.conveyor.eaton.ConveyorMenuPresenter>(smoothView, deviceInfo.getPutStep().getRobotSettings());
			conveyorMenuPresenter = new eu.robojob.millassist.ui.configure.device.stacking.conveyor.eaton.ConveyorMenuPresenter(menuView, deviceInfo, getConveyorConfigurePresenter(deviceInfo), null, getEatonFinishedWorkPieceLayoutPresenter(deviceInfo), null, smoothPresenter);
		}
		return conveyorMenuPresenter;
	}
	
	public eu.robojob.millassist.ui.configure.device.stacking.conveyor.eaton.ConveyorRawWorkPiecePresenter getConveyorEatonRawWorkPiecePresenter(final DeviceInformation deviceInfo) {
		if (deviceInfo.getPickStep() != null) {
			eu.robojob.millassist.ui.configure.device.stacking.conveyor.eaton.ConveyorRawWorkPieceView view = new eu.robojob.millassist.ui.configure.device.stacking.conveyor.eaton.ConveyorRawWorkPieceView();
			eu.robojob.millassist.ui.configure.device.stacking.conveyor.eaton.ConveyorRawWorkPiecePresenter presenter = new eu.robojob.millassist.ui.configure.device.stacking.conveyor.eaton.ConveyorRawWorkPiecePresenter(view, deviceInfo.getPickStep(), ((eu.robojob.millassist.external.device.stacking.conveyor.eaton.ConveyorSettings) deviceInfo.getDeviceSettings()));
			return presenter;
		} else {
			return null;
		}
	}
	
	public eu.robojob.millassist.ui.general.device.stacking.conveyor.eaton.ConveyorRawWorkPieceLayoutPresenter<eu.robojob.millassist.ui.configure.device.stacking.conveyor.eaton.ConveyorMenuPresenter> getEatonRawWorkPieceLayoutPresenter(final DeviceInformation deviceInfo) {
		eu.robojob.millassist.ui.general.device.stacking.conveyor.eaton.ConveyorRawWorkPieceLayoutView view = new eu.robojob.millassist.ui.general.device.stacking.conveyor.eaton.ConveyorRawWorkPieceLayoutView();
		eu.robojob.millassist.ui.general.device.stacking.conveyor.eaton.ConveyorRawWorkPieceLayoutPresenter<eu.robojob.millassist.ui.configure.device.stacking.conveyor.eaton.ConveyorMenuPresenter> presenter = new eu.robojob.millassist.ui.general.device.stacking.conveyor.eaton.ConveyorRawWorkPieceLayoutPresenter<eu.robojob.millassist.ui.configure.device.stacking.conveyor.eaton.ConveyorMenuPresenter>(view, (eu.robojob.millassist.external.device.stacking.conveyor.eaton.Conveyor) deviceInfo.getDevice());
		return presenter;
	}
	
	public eu.robojob.millassist.ui.general.device.stacking.conveyor.eaton.ConveyorFinishedWorkPieceLayoutPresenter<eu.robojob.millassist.ui.configure.device.stacking.conveyor.eaton.ConveyorMenuPresenter> getEatonFinishedWorkPieceLayoutPresenter(final DeviceInformation deviceInfo) {
		eu.robojob.millassist.ui.general.device.stacking.conveyor.eaton.ConveyorFinishedWorkPieceLayoutView view = new eu.robojob.millassist.ui.general.device.stacking.conveyor.eaton.ConveyorFinishedWorkPieceLayoutView();
		eu.robojob.millassist.ui.general.device.stacking.conveyor.eaton.ConveyorFinishedWorkPieceLayoutPresenter<eu.robojob.millassist.ui.configure.device.stacking.conveyor.eaton.ConveyorMenuPresenter> presenter = new eu.robojob.millassist.ui.general.device.stacking.conveyor.eaton.ConveyorFinishedWorkPieceLayoutPresenter<eu.robojob.millassist.ui.configure.device.stacking.conveyor.eaton.ConveyorMenuPresenter>(view, (eu.robojob.millassist.external.device.stacking.conveyor.eaton.Conveyor) deviceInfo.getDevice());
		return presenter;
	}
	
	public ConveyorMenuPresenter getConveyorMenuPresenter(final DeviceInformation deviceInfo) {
		StackingDeviceMenuView menuView = new StackingDeviceMenuView();
		ConveyorMenuPresenter presenter = null;
		ConfigureSmoothView smoothView = new ConfigureSmoothView();
		ConfigureSmoothPresenter<ConveyorMenuPresenter> smoothPresenter = null;
		if (deviceInfo.getPickStep() != null) {
			// first device 
			smoothPresenter = new ConfigureSmoothPresenter<ConveyorMenuPresenter>(smoothView, deviceInfo.getPickStep().getRobotSettings());
			presenter = new ConveyorMenuPresenter(menuView, deviceInfo, getConveyorConfigurePresenter(deviceInfo), getConveyorRawWorkPiecePresenter(deviceInfo), getRawWorkPieceLayoutPresenter(deviceInfo), getConveyorRawWorkPieceOffsetPresenter(deviceInfo), smoothPresenter, null);
		} else {
			// last device
			smoothPresenter = new ConfigureSmoothPresenter<ConveyorMenuPresenter>(smoothView, deviceInfo.getPutStep().getRobotSettings());
			presenter = new ConveyorMenuPresenter(menuView, deviceInfo, getConveyorConfigurePresenter(deviceInfo), null, getFinishedWorkPieceLayoutPresenter(deviceInfo), null, null, smoothPresenter);
		}
		return presenter;
	}
	
	public ConveyorRawWorkPieceOffsetPresenter getConveyorRawWorkPieceOffsetPresenter(final DeviceInformation deviceInfo) {
		ConveyorRawWorkPieceOffsetView view = new ConveyorRawWorkPieceOffsetView();
		ConveyorRawWorkPieceOffsetPresenter presenter = new ConveyorRawWorkPieceOffsetPresenter(view, (Conveyor) deviceInfo.getDevice(), deviceInfo.getPickStep().getProcessFlow());
		return presenter;
		
	}
	
	public ConveyorRawWorkPieceLayoutPresenter<ConveyorMenuPresenter> getRawWorkPieceLayoutPresenter(final DeviceInformation deviceInfo) {
		ConveyorRawWorkPieceLayoutView view = new ConveyorRawWorkPieceLayoutView();
		ConveyorRawWorkPieceLayoutPresenter<ConveyorMenuPresenter> presenter = new ConveyorRawWorkPieceLayoutPresenter<ConveyorMenuPresenter>(view, (Conveyor) deviceInfo.getDevice());
		return presenter;
	}
	
	public ConveyorFinishedWorkPieceLayoutPresenter getFinishedWorkPieceLayoutPresenter(final DeviceInformation deviceInfo) {
		ConveyorFinishedWorkPieceLayoutView view = new ConveyorFinishedWorkPieceLayoutView();
		ConveyorFinishedWorkPieceLayoutPresenter presenter = new ConveyorFinishedWorkPieceLayoutPresenter(view, (Conveyor) deviceInfo.getDevice(), deviceInfo.getPutStep());
		return presenter;
	}
	
	public StackingDeviceConfigurePresenter getConveyorConfigurePresenter(final DeviceInformation deviceInfo) {
		StackingDeviceConfigureView view = new StackingDeviceConfigureView();
		StackingDeviceConfigurePresenter presenter = new StackingDeviceConfigurePresenter(view, deviceInfo, deviceManager);
		return presenter;
	}
	
	public ConveyorRawWorkPiecePresenter getConveyorRawWorkPiecePresenter(final DeviceInformation deviceInfo) {
		if (deviceInfo.getPickStep() != null) {
			ConveyorRawWorkPieceView view = new ConveyorRawWorkPieceView();
			ConveyorRawWorkPiecePresenter presenter = new ConveyorRawWorkPiecePresenter(view, deviceInfo.getPickStep(), ((ConveyorSettings) deviceInfo.getDeviceSettings()));
			return presenter;
		} else {
			return null;
		}
	}
	
	public void clearBuffer() {
		for (AbstractMenuPresenter<?> presenter : presentersBuffer.values()) {
			presenter.unregisterListeners();
		}
		presentersBuffer.clear();
	}
}
