package eu.robojob.millassist.ui.automate.device;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.robojob.millassist.external.device.stacking.stackplate.BasicStackPlate;
import eu.robojob.millassist.process.ProcessFlow;
import eu.robojob.millassist.ui.automate.AbstractMenuPresenter;
import eu.robojob.millassist.ui.automate.device.stacking.BasicStackPlateLayoutPresenter;
import eu.robojob.millassist.ui.automate.device.stacking.BasicStackPlateMenuPresenter;
import eu.robojob.millassist.ui.automate.device.stacking.BasicStackPlateMenuView;
import eu.robojob.millassist.ui.automate.device.stacking.BasicStackPlateRefillPresenter;
import eu.robojob.millassist.ui.automate.device.stacking.BasicStackPlateRefillView;
import eu.robojob.millassist.ui.general.device.stacking.BasicStackPlateLayoutView;
import eu.robojob.millassist.ui.general.model.DeviceInformation;

public class DeviceMenuFactory {
	
	private ProcessFlow processFlow;		
	private BasicStackPlateMenuPresenter buffer;
	
	private static Logger logger = LogManager.getLogger(DeviceMenuFactory.class.getName());
	
	public DeviceMenuFactory(final ProcessFlow processFlow) {
		this.processFlow = processFlow;
	}
	
	public boolean hasDeviceMenu(final DeviceInformation deviceInfo) {
		switch(deviceInfo.getType()) {
			case BASIC_STACK_PLATE:
				return true;
			default:
				return false;
		}
	}
	
	public synchronized AbstractMenuPresenter<?> getDeviceMenu(final DeviceInformation deviceInfo) {
		AbstractMenuPresenter<?> menuPresenter;
		switch(deviceInfo.getType()) {
			case BASIC_STACK_PLATE:
				if (buffer == null) {
					buffer = getBasicStackPlateMenuPresenter(deviceInfo);
				} 
				menuPresenter = buffer;
				break;
			default:
				menuPresenter = null;
		}
		return menuPresenter;
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
		if (buffer != null) {
			logger.info("Clearing buffer!!");
			buffer.unregisterListeners();
		}
		buffer = null;
	}
}
