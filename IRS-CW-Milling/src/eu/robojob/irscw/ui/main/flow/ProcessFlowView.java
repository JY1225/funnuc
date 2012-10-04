package eu.robojob.irscw.ui.main.flow;

import java.util.HashMap;
import java.util.Map;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import eu.robojob.irscw.external.device.DeviceType;
import eu.robojob.irscw.process.ProcessFlow;
import eu.robojob.irscw.ui.configure.ConfigureView;
import eu.robojob.irscw.ui.main.model.ProcessFlowAdapter;

public class ProcessFlowView extends GridPane  {
	
	private ProcessFlowAdapter processFlowAdapter;
	
	private ProcessFlowPresenter presenter;
	private Map<Integer, DeviceButton> deviceButtons;
	private Map<Integer, TransportButton> transportButtons;
	
	private Map<Integer, Region> progressDeviceRegions;
	private Map<Integer, Region> progressTransportRegions;
		
	private static final int maxDevicesFirstRow = 4;
	
	public ProcessFlowView() {
		deviceButtons = new HashMap<Integer, DeviceButton>();
		transportButtons = new HashMap<Integer, TransportButton>();
		progressDeviceRegions = new HashMap<Integer, Region>();
		progressTransportRegions = new HashMap<Integer, Region>();
	}
	
	public void setProcessFlow(ProcessFlow process) {
		processFlowAdapter = new ProcessFlowAdapter(process);
		buildView();
	}
	
	public void setPresenter(ProcessFlowPresenter presenter) {
		this.presenter = presenter;
	}

	protected void buildView() {
		this.getChildren().clear();
		this.setVgap(20);
		setPadding(new Insets(20, 0, 20, 0));
		int column = 0;
		int row = 0;
		for (int i = 0; i < processFlowAdapter.getDeviceStepCount(); i++) {
			DeviceButton device = new DeviceButton(processFlowAdapter.getDeviceInformation(i));
			this.add(device, column, row);
			deviceButtons.put(i, device);
			Region progressDeviceRegion = new Region();
			progressDeviceRegion.getStyleClass().add("progressbar-piece");
			if (i == 0) {
				progressDeviceRegion.getStyleClass().add("progressbar-first");
			}
			if (i == processFlowAdapter.getDeviceStepCount() - 1) {
				progressDeviceRegion.getStyleClass().add("progressbar-last");
			}
			progressDeviceRegion.setPrefHeight(20);
			this.add(progressDeviceRegion, column, row + 1);
			progressDeviceRegions.put(i, progressDeviceRegion);
			device.setOnAction(new DeviceEventHandler(i));
			column++;
			if ((i+1)%maxDevicesFirstRow == 0) {
				column = 1;
				row++;
			}
			if (i < processFlowAdapter.getTransportStepCount()) {
				TransportButton transport = new TransportButton(processFlowAdapter.getTransportInformation(i));
				transport.showPause();
				this.add(transport, column, row);
				Region progressTransportRegion = new Region();
				progressTransportRegion.getStyleClass().add("progressbar-piece");
				this.add(progressTransportRegion, column, row + 1);
				progressTransportRegions.put(i, progressTransportRegion);
				transportButtons.put(i, transport);
				transport.setOnAction(new TransportEventHandler(i));
				transport.toBack();
				//setMargin(transport, new Insets(10, 0, 0, 0));
				column++;
			}
		}
		this.setAlignment(Pos.CENTER);
		this.setPrefHeight(ConfigureView.HEIGHT_TOP);
		this.setPrefWidth(ConfigureView.WIDTH);
		this.getStyleClass().add("process-flow-view");
		this.setEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent> () {
			@Override
			public void handle(MouseEvent arg0) {
				presenter.backgroundClicked();
				arg0.consume();
			}
		});
	}
	
	public void focusDevice(int index) {
		if ((index<0) || (index>=processFlowAdapter.getDeviceStepCount()) || (deviceButtons.get(index) == null)) {
			throw new IllegalArgumentException("Index is out of bounds or incorrect.");
		} else {
			for (TransportButton transport : transportButtons.values()) {
				transport.setFocussed(false);
			}
			for (int i : deviceButtons.keySet()) {
				if (i == index) {
					deviceButtons.get(i).setFocussed(true);
				} else {
					deviceButtons.get(i).setFocussed(false);
				}
			}
		}
	}
	
	public void focusTransport(int index) {
		if ((index<0) || (index>=processFlowAdapter.getTransportStepCount()) || (transportButtons.get(index) == null)) {
			throw new IllegalArgumentException("Index is out of bounds or incorrect.");
		} else {
			for (DeviceButton device : deviceButtons.values()) {
				device.setFocussed(false);
			}
			for (int i : transportButtons.keySet()) {
				if (i == index) {
					transportButtons.get(i).setFocussed(true);
				} else {
					transportButtons.get(i).setFocussed(false);
				}
			}
		}
	}
	
	public void focusAll() {
		for (DeviceButton device : deviceButtons.values()) {
			device.setFocussed(true);
		}
		for (TransportButton transport : transportButtons.values()) {
			transport.setFocussed(true);
		}
	}
	
	public void setDeviceProgressGreen(int deviceIndex) {
		setDeviceProgressNone(deviceIndex);
		if (deviceIndex == 0) {
			progressDeviceRegions.get(deviceIndex).getStyleClass().add("progressbar-first-green");
		} else if (deviceIndex == processFlowAdapter.getDeviceStepCount() -1) {
			progressDeviceRegions.get(deviceIndex).getStyleClass().add("progressbar-last-green");
		} else {
			progressDeviceRegions.get(deviceIndex).getStyleClass().add("progressbar-piece-green");
		}
	}
	
	public void setDeviceProgressYellow(int deviceIndex) {
		setDeviceProgressNone(deviceIndex);
		if (deviceIndex == 0) {
			progressDeviceRegions.get(deviceIndex).getStyleClass().add("progressbar-first-yellow");
		} else if (deviceIndex == processFlowAdapter.getDeviceStepCount() -1) {
			progressDeviceRegions.get(deviceIndex).getStyleClass().add("progressbar-last-yellow");
		} else {
			progressDeviceRegions.get(deviceIndex).getStyleClass().add("progressbar-piece-yellow");
		}
	}
	
	public void setDeviceProgressNone(int deviceIndex) {
		if (deviceIndex == 0) {
			progressDeviceRegions.get(deviceIndex).getStyleClass().remove("progressbar-first-green");
			progressDeviceRegions.get(deviceIndex).getStyleClass().remove("progressbar-first-yellow");
		} else if (deviceIndex == processFlowAdapter.getDeviceStepCount() -1) {
			progressDeviceRegions.get(deviceIndex).getStyleClass().remove("progressbar-last-green");
			progressDeviceRegions.get(deviceIndex).getStyleClass().remove("progressbar-last-yellow");
		} else {
			progressDeviceRegions.get(deviceIndex).getStyleClass().remove("progressbar-piece-green");
			progressDeviceRegions.get(deviceIndex).getStyleClass().remove("progressbar-piece-yellow");
		}
	}
	
	public void setTransportProgressGreen(int transportIndex) {
		setTransportProgressNone(transportIndex);
		progressTransportRegions.get(transportIndex).getStyleClass().add("progressbar-piece-green");
	}
	
	public void setTransportProgressYellow(int transportIndex) {
		setTransportProgressNone(transportIndex);
		progressTransportRegions.get(transportIndex).getStyleClass().add("progressbar-piece-yellow");
	}
	
	public void setTransportProgressNone(int transportIndex) {
		progressTransportRegions.get(transportIndex).getStyleClass().remove("progressbar-piece-green");
		progressTransportRegions.get(transportIndex).getStyleClass().remove("progressbar-piece-yellow");
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
	
	public void setRemoveDeviceMode() {
		for (DeviceButton deviceButton : deviceButtons.values()) {
			if ((deviceButton.getDeviceInformation().getType() == DeviceType.POST_PROCESSING) || (deviceButton.getDeviceInformation().getType() == DeviceType.PRE_PROCESSING)) {
				deviceButton.setFocussed(true);
				deviceButton.setDisable(false);
			} else {
				deviceButton.setFocussed(false);
				deviceButton.setDisable(true);
			}
		}
		for (TransportButton transportButton : transportButtons.values()) {
			transportButton.setFocussed(false);
			transportButton.setDisable(true);
		}
	}
	
	public void setAddDeviceMode() {
		for (DeviceButton deviceButton : deviceButtons.values()) {
			deviceButton.setFocussed(false);
			deviceButton.setDisable(true);
		}
		for (TransportButton transportButton : transportButtons.values()) {
			transportButton.setFocussed(true);
			transportButton.setDisable(false);
		}
	}
	
	public void setNormalMode() {
		for (DeviceButton deviceButton : deviceButtons.values()) {
			deviceButton.setFocussed(true);
			deviceButton.setDisable(false);
		}
		for (TransportButton transportButton : transportButtons.values()) {
			transportButton.setFocussed(true);
			transportButton.setDisable(false);
		}
	}
}

