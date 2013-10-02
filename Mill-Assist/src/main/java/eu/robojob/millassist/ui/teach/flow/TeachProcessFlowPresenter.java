package eu.robojob.millassist.ui.teach.flow;

import javafx.application.Platform;
import eu.robojob.millassist.process.ProcessFlow;
import eu.robojob.millassist.ui.general.flow.DeviceButton;
import eu.robojob.millassist.ui.general.flow.FixedProcessFlowPresenter;
import eu.robojob.millassist.ui.general.flow.TransportButton;
import eu.robojob.millassist.ui.teach.TeachPresenter;

public class TeachProcessFlowPresenter extends FixedProcessFlowPresenter {

	private TeachPresenter parent;
	private int selectedTransport;
	
	public TeachProcessFlowPresenter(final TeachProcessFlowView view) {
		super(view);
		view.setPresenter(this);
		this.selectedTransport = -1;
	}
	
	public void setParent(final TeachPresenter parent) {
		this.parent = parent;
	}
	
	public void loadProcessFlow(final ProcessFlow processFlow) {
		super.loadProcessFlow(processFlow);
	}
	
	public void refresh() {
		super.refresh();
		if (selectedTransport != -1) {
			getView().focusTransport(selectedTransport);
		} 
	}
	
	public void transportClicked(final int index) {
		Platform.runLater(new Thread() {
			@Override
			public void run() {
				if (parent.showTransportMenu(index)) {
					getView().focusTransport(index);
					selectedTransport = index;
				} else {
					selectedTransport = -1;
				}
			}
		});
	}
	
	public void buildFinished() {
		for (DeviceButton deviceButton : getView().getDeviceButtons()) {
			deviceButton.setClickable(false);
		}
		for (TransportButton transportButton : getView().getTransportButtons()) {
			transportButton.setClickable(true);
		}
	}
	
	public void setRunning(final boolean running) {
		for (TransportButton transportButton : getView().getTransportButtons()) {
			transportButton.setClickable(!running);
		}
	}
	
	@Override
	public void setNoneActive() {
		selectedTransport = -1;
		getView().focusAll();
	}

	public void backgroundClicked() {
		selectedTransport = -1;
		parent.closeTransportMenu();
	}
}
