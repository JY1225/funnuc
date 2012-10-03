package eu.robojob.irscw.ui.main.flow;

import org.apache.log4j.Logger;

import eu.robojob.irscw.process.ProcessFlow;
import eu.robojob.irscw.ui.main.configure.ConfigurePresenter;
import eu.robojob.irscw.ui.main.configure.ConfigurePresenter.Mode;

public class ProcessFlowPresenter {

	private ProcessFlowView view;
	private ConfigurePresenter parent;
	
	private static Logger logger = Logger.getLogger(ProcessFlowPresenter.class);
	
	private int focussedDevice;
	private int focussedTransport;
	
	public ProcessFlowPresenter(ProcessFlowView view) {
		this.view = view;
		focussedDevice = -1;
		focussedTransport = -1;
		view.setPresenter(this);
	}
	
	public void setParent(ConfigurePresenter parent) {
		this.parent = parent;
	}
	
	public ProcessFlowView getView() {
		return view;
	}
	
	public void deviceClicked(int index) {
		if (parent.getMode() == Mode.NORMAL) {
			logger.debug("Clicked device with index: " + index);
			view.focusDevice(index);
			focussedDevice = index;
			focussedTransport = -1;
			parent.configureDevice(index);
		} else {
			if (parent.getMode() == Mode.REMOVE_DEVICE) {
				parent.removeDevice(index);
			} else {
				throw new IllegalStateException("Device clicked, but state does not allow it");
			}
		}
	}
	
	public void transportClicked(int index) {
		if (parent.getMode() == Mode.NORMAL) {
			logger.debug("Clicked transport with index: " + index);
			view.focusTransport(index);
			focussedDevice = -1;
			focussedTransport = index;
			parent.configureTransport(index);
		} else {
			if (parent.getMode() == Mode.ADD_DEVICE) {
				parent.addDevice(index);
			} else {
				throw new IllegalStateException("Transport clicked, but state does not allow it.");
			}
		}
	}
	
	public void backgroundClicked() {
		if (parent.getMode() == Mode.NORMAL) {
			logger.debug("Clicked process-flow background");
			view.focusAll();
			parent.configureProcess();
		}
	}
	
	public void loadProcessFlow(ProcessFlow processFlow) {
		view.setProcessFlow(processFlow);
	}
	
	public void setAddDeviceMode() {
		view.setAddDeviceMode();
	}
	
	public void setRemoveDeviceMode() {
		view.setRemoveDeviceMode();
	}
	
	public void setNormalMode() {
		view.setNormalMode();
	}
	
	public void refresh() {
		view.buildView();
		if (focussedDevice != -1) {
			view.focusDevice(focussedDevice);
		} else if (focussedTransport != -1) {
			view.focusTransport(focussedTransport);
		}
	}
	
}
