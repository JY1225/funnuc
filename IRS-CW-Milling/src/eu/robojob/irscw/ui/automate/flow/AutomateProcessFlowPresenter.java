package eu.robojob.irscw.ui.automate.flow;

import eu.robojob.irscw.process.ProcessFlow;
import eu.robojob.irscw.ui.automate.AutomatePresenter;
import eu.robojob.irscw.ui.general.flow.FixedProcessFlowPresenter;
import eu.robojob.irscw.ui.general.flow.ProcessFlowView;

public class AutomateProcessFlowPresenter extends FixedProcessFlowPresenter {
	
	private AutomatePresenter parent;	
	private int selectedDevice;
	
	public AutomateProcessFlowPresenter(final ProcessFlowView view) {
		super(view);
		view.setPresenter(this);
		this.selectedDevice = -1;
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

	@Override
	public void transportClicked(final int transportIndex) { }
	
}
