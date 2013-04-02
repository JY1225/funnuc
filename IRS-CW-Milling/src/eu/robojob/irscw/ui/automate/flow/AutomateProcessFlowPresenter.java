package eu.robojob.irscw.ui.automate.flow;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.robojob.irscw.process.ProcessFlow;
import eu.robojob.irscw.ui.automate.AutomatePresenter;
import eu.robojob.irscw.ui.automate.device.DeviceMenuFactory;
import eu.robojob.irscw.ui.general.flow.DeviceButton;
import eu.robojob.irscw.ui.general.flow.FixedProcessFlowPresenter;
import eu.robojob.irscw.ui.general.flow.TransportButton;

public class AutomateProcessFlowPresenter extends FixedProcessFlowPresenter {
	
	private AutomatePresenter parent;	
	private int selectedDevice;
	private DeviceMenuFactory deviceMenuFactory;
	
	private static Logger logger = LogManager.getLogger(AutomateProcessFlowPresenter.class.getName());
	
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
		logger.info("Build finished!");
		for (DeviceButton deviceButton : getView().getDeviceButtons()) {
			if (deviceMenuFactory.getDeviceMenu(deviceButton.getDeviceInformation()) != null) {
				logger.info("Set clickable true!!");
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
