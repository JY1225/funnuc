package eu.robojob.millassist.ui.automate.device;

import eu.robojob.millassist.external.device.stacking.BasicStackPlate;
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
	
	public DeviceMenuFactory(final ProcessFlow processFlow) {
		this.processFlow = processFlow;
	}
	
	public synchronized AbstractMenuPresenter<?> getDeviceMenu(final DeviceInformation deviceInfo) {
		AbstractMenuPresenter<?> menuPresenter;
		switch(deviceInfo.getType()) {
			case BASIC_STACK_PLATE:
				if (buffer == null) {
					menuPresenter = getBasicStackPlateMenuPresenter(deviceInfo);
				} else {
					menuPresenter = buffer;
				}
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
		buffer.unregisterListeners();
		buffer = null;
	}
}
