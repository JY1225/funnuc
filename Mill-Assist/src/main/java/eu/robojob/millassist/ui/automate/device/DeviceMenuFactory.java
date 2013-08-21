package eu.robojob.millassist.ui.automate.device;

import java.util.HashMap;
import java.util.Map;

import eu.robojob.millassist.external.device.stacking.conveyor.Conveyor;
import eu.robojob.millassist.external.device.stacking.stackplate.BasicStackPlate;
import eu.robojob.millassist.process.ProcessFlow;
import eu.robojob.millassist.ui.automate.AbstractMenuPresenter;
import eu.robojob.millassist.ui.automate.device.stacking.conveyor.ConveyorAmountsPresenter;
import eu.robojob.millassist.ui.automate.device.stacking.conveyor.ConveyorAmountsView;
import eu.robojob.millassist.ui.automate.device.stacking.conveyor.ConveyorMenuPresenter;
import eu.robojob.millassist.ui.automate.device.stacking.conveyor.ConveyorMenuView;
import eu.robojob.millassist.ui.automate.device.stacking.stackplate.BasicStackPlateLayoutPresenter;
import eu.robojob.millassist.ui.automate.device.stacking.stackplate.BasicStackPlateMenuPresenter;
import eu.robojob.millassist.ui.automate.device.stacking.stackplate.BasicStackPlateMenuView;
import eu.robojob.millassist.ui.automate.device.stacking.stackplate.BasicStackPlateRefillPresenter;
import eu.robojob.millassist.ui.automate.device.stacking.stackplate.BasicStackPlateRefillView;
import eu.robojob.millassist.ui.general.device.stacking.conveyor.ConveyorFinishedWorkPieceLayoutPresenter;
import eu.robojob.millassist.ui.general.device.stacking.conveyor.ConveyorFinishedWorkPieceLayoutView;
import eu.robojob.millassist.ui.general.device.stacking.conveyor.ConveyorRawWorkPieceLayoutPresenter;
import eu.robojob.millassist.ui.general.device.stacking.conveyor.ConveyorRawWorkPieceLayoutView;
import eu.robojob.millassist.ui.general.device.stacking.stackplate.BasicStackPlateLayoutView;
import eu.robojob.millassist.ui.general.model.DeviceInformation;

public class DeviceMenuFactory {
	
	private ProcessFlow processFlow;
	
	private Map<Integer, AbstractMenuPresenter<?>> presentersBuffer;
	
//	private static Logger logger = LogManager.getLogger(DeviceMenuFactory.class.getName());
	
	public DeviceMenuFactory(final ProcessFlow processFlow) {
		this.processFlow = processFlow;
		presentersBuffer = new HashMap<Integer, AbstractMenuPresenter<?>>();
	}
	
	public boolean hasDeviceMenu(final DeviceInformation deviceInfo) {
		switch(deviceInfo.getType()) {
			case BASIC_STACK_PLATE:
				return true;
			case CONVEYOR:
				return true;
			default:
				return false;
		}
	}
	
	public synchronized AbstractMenuPresenter<?> getDeviceMenu(final DeviceInformation deviceInfo) {
		AbstractMenuPresenter<?> menuPresenter = presentersBuffer.get(deviceInfo.getIndex());
		if (menuPresenter == null) {
			switch(deviceInfo.getType()) {
				case BASIC_STACK_PLATE:
					menuPresenter = getBasicStackPlateMenuPresenter(deviceInfo);
					break;
				case CONVEYOR:
					menuPresenter = getConveyorMenuPresenter(deviceInfo);
					break;
				default:
					menuPresenter = null;
			}
			presentersBuffer.put(deviceInfo.getIndex(), menuPresenter);
		}
		return menuPresenter;
	}
	
	public ConveyorMenuPresenter getConveyorMenuPresenter(final DeviceInformation deviceInfo) {
		ConveyorMenuView conveyorMenuView = new ConveyorMenuView();
		ConveyorMenuPresenter conveyorMenuPresenter;
		if (deviceInfo.getPickStep() != null) {
			// first device
			conveyorMenuPresenter = new ConveyorMenuPresenter(conveyorMenuView, getRawWorkPieceLayoutPresenter(deviceInfo), getAmountsPresenter(deviceInfo.getPickStep().getProcessFlow(), (Conveyor) deviceInfo.getDevice()));
		} else {
			// last device
			conveyorMenuPresenter = new ConveyorMenuPresenter(conveyorMenuView, getFinishedWorkPieceLayoutPresenter(deviceInfo), getAmountsPresenter(deviceInfo.getPutStep().getProcessFlow(), (Conveyor) deviceInfo.getDevice()));
		}
		return conveyorMenuPresenter;
	}
	
	public ConveyorAmountsPresenter getAmountsPresenter(final ProcessFlow processFlow, final Conveyor conveyor) {
		ConveyorAmountsView amountsView = new ConveyorAmountsView();
		ConveyorAmountsPresenter amountsPresenter = new ConveyorAmountsPresenter(amountsView, processFlow, conveyor);
		return amountsPresenter;
	}
	
	public ConveyorRawWorkPieceLayoutPresenter<ConveyorMenuPresenter> getRawWorkPieceLayoutPresenter(final DeviceInformation deviceInfo) {
		ConveyorRawWorkPieceLayoutView view = new ConveyorRawWorkPieceLayoutView();
		ConveyorRawWorkPieceLayoutPresenter<ConveyorMenuPresenter> presenter = new ConveyorRawWorkPieceLayoutPresenter<ConveyorMenuPresenter>(view, (Conveyor) deviceInfo.getDevice());
		presenter.hideButtons();
		return presenter;
	}
	
	public ConveyorFinishedWorkPieceLayoutPresenter<ConveyorMenuPresenter> getFinishedWorkPieceLayoutPresenter(final DeviceInformation deviceInfo) {
		ConveyorFinishedWorkPieceLayoutView view = new ConveyorFinishedWorkPieceLayoutView();
		ConveyorFinishedWorkPieceLayoutPresenter<ConveyorMenuPresenter> presenter = new ConveyorFinishedWorkPieceLayoutPresenter<ConveyorMenuPresenter>(view, (Conveyor) deviceInfo.getDevice());
		return presenter;
	}
	
	public BasicStackPlateMenuPresenter getBasicStackPlateMenuPresenter(final DeviceInformation deviceInfo) {
		BasicStackPlateMenuView stackingDeviceMenuView = new BasicStackPlateMenuView();
		BasicStackPlateMenuPresenter basicStackPlateMenuPresenter = new BasicStackPlateMenuPresenter(stackingDeviceMenuView, getBasicStackPlateLayoutPresenter(deviceInfo), getBasicStackPlateRefillPresenter(deviceInfo));
		return basicStackPlateMenuPresenter;
	}
	
	public BasicStackPlateLayoutPresenter getBasicStackPlateLayoutPresenter(final DeviceInformation deviceInfo) {
		BasicStackPlateLayoutView<BasicStackPlateLayoutPresenter> view = new BasicStackPlateLayoutView<BasicStackPlateLayoutPresenter>();
		BasicStackPlateLayoutPresenter basicStackPlateLayoutPresenter = new BasicStackPlateLayoutPresenter(view, (BasicStackPlate) deviceInfo.getDevice());
		return basicStackPlateLayoutPresenter;
	}
	
	public BasicStackPlateRefillPresenter getBasicStackPlateRefillPresenter(final DeviceInformation deviceInfo) {
		BasicStackPlateRefillView view = new BasicStackPlateRefillView();
		BasicStackPlateRefillPresenter basicStackPlateRefillPresenter = new BasicStackPlateRefillPresenter(view, (BasicStackPlate) deviceInfo.getDevice(), processFlow);
		return basicStackPlateRefillPresenter;
	}
	
	public void clearBuffer() {
		for (AbstractMenuPresenter<?> presenter : presentersBuffer.values()) {
			presenter.unregisterListeners();
		}
		presentersBuffer.clear();
	}
}
