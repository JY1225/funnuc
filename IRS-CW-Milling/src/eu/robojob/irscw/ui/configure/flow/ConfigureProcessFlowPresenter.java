package eu.robojob.irscw.ui.configure.flow;

import eu.robojob.irscw.process.ProcessFlow;
import eu.robojob.irscw.ui.configure.ConfigurePresenter;
import eu.robojob.irscw.ui.configure.ConfigurePresenter.Mode;
import eu.robojob.irscw.ui.general.flow.AbstractProcessFlowPresenter;
import eu.robojob.irscw.ui.general.flow.ProcessFlowView;
import eu.robojob.irscw.ui.general.flow.ProcessFlowView.ProgressBarPieceMode;

public class ConfigureProcessFlowPresenter extends AbstractProcessFlowPresenter {
	
	private ConfigurePresenter parent;	
	private int focussedDevice;
	private int focussedTransport;
		
	public ConfigureProcessFlowPresenter(final ProcessFlowView view) {
		super(view);
		focussedDevice = -1;
		focussedTransport = -1;
		view.setPresenter(this);
	}
	
	public void setParent(final ConfigurePresenter parent) {
		this.parent = parent;
	}
	
	public void deviceClicked(final int index) {
		if (parent.getMode() == Mode.NORMAL) {
			getView().focusDevice(index);
			focussedDevice = index;
			focussedTransport = -1;
			parent.configureDevice(index);
		} else {
			focussedDevice = -1;
			focussedTransport = -1;
			if (parent.getMode() == Mode.REMOVE_DEVICE) {
				parent.removeDevice(index);
			} else {
				throw new IllegalStateException("Device [" + index + "] clicked, but state does not allow it");
			}
		}
	}
	
	public void removeFocus() {
		getView().focusAll();
	}
	
	public void transportClicked(final int index) {
		if (parent.getMode() == Mode.NORMAL) {
			getView().focusTransport(index);
			focussedDevice = -1;
			focussedTransport = index;
			parent.configureTransport(index);
		} else {
			focussedDevice = -1;
			focussedTransport = -1;
			if (parent.getMode() == Mode.ADD_DEVICE) {
				parent.addDevice(index);
			} else {
				throw new IllegalStateException("Transport [" + index + "] clicked, but state does not allow it.");
			}
		}
	}
	
	public void backgroundClicked() {
		if (parent.getMode() == Mode.NORMAL) {
			getView().focusAll();
			parent.configureProcess();
			focussedDevice = -1;
			focussedTransport = -1;
		}
	}
	
	public void loadProcessFlow(final ProcessFlow processFlow) {
		super.loadProcessFlow(processFlow);
		focussedDevice = -1;
		focussedTransport = -1;
	}
	
	public void setAddDeviceMode(final boolean addPreProcessPossible, final boolean addPostProcessPossible) {
		getView().showAddDevice(addPreProcessPossible, addPostProcessPossible);
		focussedDevice = -1;
		focussedTransport = -1;
	}
	
	public void setRemoveDeviceMode() {
		getView().showRemoveDevice();
		focussedDevice = -1;
		focussedTransport = -1;
	}
	
	public void setNormalMode() {
		getView().showNormal();
		focussedDevice = -1;
		focussedTransport = -1;
	}
	
	public void refresh() {
		super.refresh();
		if (focussedDevice != -1) {
			getView().focusDevice(focussedDevice);
		} else if (focussedTransport != -1) {
			getView().focusTransport(focussedTransport);
		}
	}
	
	public void setDeviceConfigured(final int deviceIndex, final boolean configured) {
		if (configured) {
			getView().setDeviceProgressBarPieceMode(deviceIndex, 0, ProgressBarPieceMode.GREEN);
		} else {
			getView().setDeviceProgressBarPieceMode(deviceIndex, 0, ProgressBarPieceMode.NONE);
		}
	}
	
	public void setTransportConfigured(final int transportIndex, final boolean configured) {
		if (configured) {
			getView().setTransportLeftProgressBarPieceMode(transportIndex, 0, ProgressBarPieceMode.GREEN);
			getView().setTransportRightProgressBarPieceMode(transportIndex, 0, ProgressBarPieceMode.GREEN);
		} else {
			getView().setTransportLeftProgressBarPieceMode(transportIndex, 0, ProgressBarPieceMode.NONE);
			getView().setTransportRightProgressBarPieceMode(transportIndex, 0, ProgressBarPieceMode.NONE);
		}
	}
	
}
