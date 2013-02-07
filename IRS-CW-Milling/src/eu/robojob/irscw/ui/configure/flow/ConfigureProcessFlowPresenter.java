package eu.robojob.irscw.ui.configure.flow;

import eu.robojob.irscw.process.ProcessFlow;
import eu.robojob.irscw.ui.configure.ConfigurePresenter;
import eu.robojob.irscw.ui.configure.ConfigurePresenter.Mode;
import eu.robojob.irscw.ui.general.flow.AbstractProcessFlowPresenter;
import eu.robojob.irscw.ui.general.flow.ProcessFlowView;
import eu.robojob.irscw.ui.general.flow.ProcessFlowView.ProgressBarPieceMode;

public class ConfigureProcessFlowPresenter extends AbstractProcessFlowPresenter {
	
	private ConfigurePresenter parent;	
	private int selectedDevice;
	private int selectedTransport;
	
	public ConfigureProcessFlowPresenter(final ProcessFlowView view) {
		super(view);
		view.setPresenter(this);
		this.selectedDevice = -1;
		this.selectedTransport = -1;
	}
	
	public void setParent(final ConfigurePresenter parent) {
		this.parent = parent;
	}
	
	public void deviceClicked(final int index) {
		if (parent.getMode() == Mode.NORMAL) {
			getView().focusDevice(index);
			selectedDevice = index;
			selectedTransport = -1;
			parent.configureDevice(index);
		} else {
			if (parent.getMode() == Mode.REMOVE_DEVICE) {
				parent.removeDevice(index);
			}
		}
	}
	
	public void removeFocus() {
		getView().focusAll();
	}
	
	public void transportClicked(final int index) {
		if (parent.getMode() == Mode.NORMAL) {
			getView().focusTransport(index);
			selectedDevice = -1;
			selectedTransport = index;
			parent.configureTransport(index);
		} else {
			if (parent.getMode() == Mode.ADD_DEVICE) {
				parent.addDevice(index);
			}
		}
	}
	
	public void backgroundClicked() {
		if (parent.getMode() == Mode.NORMAL) {
			getView().focusAll();
			selectedDevice = -1;
			selectedTransport = -1;
			parent.configureProcess();
		}
	}
	
	public void loadProcessFlow(final ProcessFlow processFlow) {
		super.loadProcessFlow(processFlow);
		}
	
	public void setAddDeviceMode(final boolean addPreProcessPossible, final boolean addPostProcessPossible) {
		getView().showAddDevice(addPreProcessPossible, addPostProcessPossible);
		}
	
	public void setRemoveDeviceMode() {
		getView().showRemoveDevice();
		}
	
	public void setNormalMode() {
		getView().showNormal();
	}
	
	public void refresh() {
		//setNormalMode();
		super.refresh();
		if (selectedDevice != -1) {
			getView().focusDevice(selectedDevice);
		} else if (selectedTransport != -1) {
			getView().focusTransport(selectedTransport);
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
