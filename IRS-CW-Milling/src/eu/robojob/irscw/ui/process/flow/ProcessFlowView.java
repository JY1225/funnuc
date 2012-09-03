package eu.robojob.irscw.ui.process.flow;

import java.util.HashMap;
import java.util.Map;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.GridPane;

import org.apache.log4j.Logger;

import eu.robojob.irscw.process.ProcessFlow;
import eu.robojob.irscw.ui.process.model.ProcessFlowAdapter;

//for now, we only allow one row, in the future, multiple rows could be possible
// to accomplish this, more HBox's are to be added and the components should be
// distributed amongst them	
public class ProcessFlowView extends GridPane  {
	
	private static Logger logger = Logger.getLogger(ProcessFlowView.class);
	private ProcessFlowAdapter processFlowAdapter;
	
	private ProcessFlowPresenter presenter;
	private Map<Integer, DeviceButton> deviceButtons;
	private Map<Integer, TransportButton> transportButtons;
		
	public ProcessFlowView() {
		deviceButtons = new HashMap<Integer, DeviceButton>();
		transportButtons = new HashMap<Integer, TransportButton>();
	}
	
	public void setProcessFlow(ProcessFlow process) {
		processFlowAdapter = new ProcessFlowAdapter(process);
		buildView();
	}
	
	public void setPresenter(ProcessFlowPresenter presenter) {
		this.presenter = presenter;
	}

	private void buildView() {
		setPadding(new Insets(20, 10, 20, 10));
		int column = 0;
		int row = 0;
		for (int i = 0; i < processFlowAdapter.getDeviceStepCount(); i++) {
			DeviceButton device = new DeviceButton(processFlowAdapter.getDeviceInformation(i));
			this.add(device, column, row);
			deviceButtons.put(i, device);
			device.setOnAction(new DeviceEventHandler(i));
			device.toBack();
			column++;
			if (i < processFlowAdapter.getTransportStepCount()) {
				TransportButton transport = new TransportButton(processFlowAdapter.getTransportInformation(i));
				this.add(transport, column, row);
				transportButtons.put(i, transport);
				transport.setOnAction(new TransportEventHandler(i));
				setMargin(transport, new Insets(10, 0, 0, 1));
				column++;
			}
		}
		this.setAlignment(Pos.CENTER);
	}
	
	public void focusDevice(int index) {
		if ((index<0) || (index>=processFlowAdapter.getDeviceStepCount()) || (deviceButtons.get(index) == null)) {
			throw new IllegalArgumentException("Index is out of bounds or incorrect.");
		} else {
			for (TransportButton transport : transportButtons.values()) {
				transport.setEnabled(false);
			}
			for (int i : deviceButtons.keySet()) {
				if (i == index) {
					deviceButtons.get(i).setEnabled(true);
				} else {
					deviceButtons.get(i).setEnabled(false);
				}
			}
		}
	}
	
	public void focusTransport(int index) {
		if ((index<0) || (index>=processFlowAdapter.getTransportStepCount()) || (transportButtons.get(index) == null)) {
			throw new IllegalArgumentException("Index is out of bounds or incorrect.");
		} else {
			for (DeviceButton device : deviceButtons.values()) {
				device.setEnabled(false);
			}
			for (int i : transportButtons.keySet()) {
				if (i == index) {
					transportButtons.get(i).setEnabled(true);
				} else {
					transportButtons.get(i).setEnabled(false);
				}
			}
		}
	}

	private class DeviceEventHandler implements EventHandler<ActionEvent> {
		private int index;
		public DeviceEventHandler(int index) {
			this.index = index;
		}
		@Override
		public void handle(ActionEvent event) {
			presenter.deviceClicked(index);
		}
	}
	
	private class TransportEventHandler implements EventHandler<ActionEvent> {
		private int index;
		public TransportEventHandler(int index) {
			this.index = index;
		}
		@Override
		public void handle(ActionEvent event) {
			presenter.transportClicked(index);
		}	
	}
	
	public void startDeviceAnimation(int index) {
		if ((index<0) || (index>=processFlowAdapter.getDeviceStepCount()) || (deviceButtons.get(index) == null)) {
			throw new IllegalArgumentException("Index is out of bounds or incorrect.");
		} else {
			deviceButtons.get(index).animate();
		}
	}
}

