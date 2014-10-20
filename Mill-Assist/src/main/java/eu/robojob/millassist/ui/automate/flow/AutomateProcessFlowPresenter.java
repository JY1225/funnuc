package eu.robojob.millassist.ui.automate.flow;

import eu.robojob.millassist.process.ProcessFlow;
import eu.robojob.millassist.ui.automate.AutomatePresenter;
import eu.robojob.millassist.ui.automate.device.DeviceMenuFactory;
import eu.robojob.millassist.ui.general.flow.DeviceButton;
import eu.robojob.millassist.ui.general.flow.FixedProcessFlowPresenter;
import eu.robojob.millassist.ui.general.flow.TransportButton;

public class AutomateProcessFlowPresenter extends FixedProcessFlowPresenter {
	
	private AutomatePresenter parent;	
	private int selectedDevice;
	private DeviceMenuFactory deviceMenuFactory;
		
	public AutomateProcessFlowPresenter(final AutomateProcessFlowView view, final DeviceMenuFactory deviceMenuFactory) {
		super(view);
		view.setPresenter(this);
		this.selectedDevice = -1;
		this.deviceMenuFactory = deviceMenuFactory;
	}
	
	public void setParent(final AutomatePresenter parent) {
		this.parent = parent;
	}
	
	public void deviceClicked(final int index) {
		if (parent.showDeviceMenu(index)) {
			getView().focusDevice(index);
			selectedDevice = index;
		} else {
			selectedDevice = -1;
		}
	}
	
	public void removeFocus() {
		getView().focusAll();
	}
	
	public void backgroundClicked() {
		getView().focusAll();
		selectedDevice = -1;
		parent.closeDeviceMenu();
	}
	
	public void loadProcessFlow(final ProcessFlow processFlow) {
		super.loadProcessFlow(processFlow);
	}
	
	public void refresh() {
		//setNormalMode();
		super.refresh();
		if (selectedDevice != -1) {
			getView().focusDevice(selectedDevice);
		} 
	}
	
	public void buildFinished() {
		for (DeviceButton deviceButton : getView().getDeviceButtons()) {
			if (deviceMenuFactory.hasDeviceMenu(deviceButton.getDeviceInformation())) {
				deviceButton.setClickable(true);
			} else {
				deviceButton.setClickable(false);
			}
		}
		for (TransportButton transportButton : getView().getTransportButtons()) {
			transportButton.setClickable(false);
		}
	}

	@Override
	public void transportClicked(final int transportIndex) { }
	
}
