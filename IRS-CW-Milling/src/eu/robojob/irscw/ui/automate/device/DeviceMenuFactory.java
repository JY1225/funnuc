package eu.robojob.irscw.ui.automate.device;

import java.util.HashMap;
import java.util.Map;

import eu.robojob.irscw.external.device.stacking.BasicStackPlate;
import eu.robojob.irscw.ui.automate.AbstractMenuPresenter;
import eu.robojob.irscw.ui.automate.device.stacking.BasicStackPlateLayoutPresenter;
import eu.robojob.irscw.ui.automate.device.stacking.BasicStackPlateMenuPresenter;
import eu.robojob.irscw.ui.automate.device.stacking.BasicStackPlateMenuView;
import eu.robojob.irscw.ui.automate.device.stacking.BasicStackPlateRefillPresenter;
import eu.robojob.irscw.ui.automate.device.stacking.BasicStackPlateRefillView;
import eu.robojob.irscw.ui.general.device.stacking.BasicStackPlateLayoutView;
import eu.robojob.irscw.ui.general.model.DeviceInformation;

public class DeviceMenuFactory {
	
	private Map<Integer, AbstractMenuPresenter<?>> presentersBuffer;
			
	public DeviceMenuFactory() {
		presentersBuffer = new HashMap<Integer, AbstractMenuPresenter<?>>();
	}
	
	public void reset() {
		presentersBuffer.clear();
	}
	
	public synchronized AbstractMenuPresenter<?> getDeviceMenu(final DeviceInformation deviceInfo) {
		AbstractMenuPresenter<?> menuPresenter = presentersBuffer.get(deviceInfo.getIndex());
		if (menuPresenter == null) {
			switch(deviceInfo.getType()) {
				case BASIC_STACK_PLATE:
					menuPresenter = getBasicStackPlateMenuPresenter(deviceInfo);
					break;
				default:
					menuPresenter = null;
			}
			presentersBuffer.put(deviceInfo.getIndex(), menuPresenter);
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
		BasicStackPlateRefillPresenter basicStackPlateRefillPresenter = new BasicStackPlateRefillPresenter(view, (BasicStackPlate) deviceInfo.getDevice());
		return basicStackPlateRefillPresenter;
	}
	
	public void clearBuffer() {
		presentersBuffer.clear();
	}
}
