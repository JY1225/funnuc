package eu.robojob.irscw.ui.general.flow;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import eu.robojob.irscw.external.device.DeviceType;
import eu.robojob.irscw.process.ProcessFlow;
import eu.robojob.irscw.ui.configure.ConfigureView;
import eu.robojob.irscw.ui.general.model.ProcessFlowAdapter;

public class ProcessFlowView extends GridPane  {
	
	private ProcessFlowAdapter processFlowAdapter;
	
	private AbstractProcessFlowPresenter presenter;
	private Map<Integer, DeviceButton> deviceButtons;
	private Map<Integer, TransportButton> transportButtons;
	
	private Map<Integer, Region> progressDeviceRegions;
	private Map<Integer, Region> progressTransportRegionsLeft;
	private Map<Integer, Region> progressTransportRegionsRight;
		
	private static final int HEIGHT = 10; 
	private static final int MAX_AMOUNT_DEVICES_ONE_ROW = 4;
	
	public ProcessFlowView() {
		deviceButtons = new HashMap<Integer, DeviceButton>();
		transportButtons = new HashMap<Integer, TransportButton>();
		progressDeviceRegions = new HashMap<Integer, Region>();
		progressTransportRegionsLeft = new HashMap<Integer, Region>();
		progressTransportRegionsRight = new HashMap<Integer, Region>();
	}
	
	public void setProcessFlow(ProcessFlow process) {
		processFlowAdapter = new ProcessFlowAdapter(process);
		buildView();
	}
	
	public void setPresenter(AbstractProcessFlowPresenter presenter) {
		this.presenter = presenter;
	}

	public void buildView() {
		this.getChildren().clear();
		this.setVgap(HEIGHT);
		setPadding(new Insets(HEIGHT, 0, HEIGHT, 0));
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
			progressDeviceRegion.setPrefHeight(HEIGHT);
			this.add(progressDeviceRegion, column, row + 1);
			progressDeviceRegions.put(i, progressDeviceRegion);
			device.setOnAction(new DeviceEventHandler(i));
			column++;
			if ((i+1)%MAX_AMOUNT_DEVICES_ONE_ROW == 0) {
				column = 1;
				row++;
			}
			if (i < processFlowAdapter.getTransportStepCount()) {
				TransportButton transport = new TransportButton(processFlowAdapter.getTransportInformation(i));
				transport.showPause();
				this.add(transport, column, row);
				HBox progressTransportHBox = new HBox();
				Region progressTransportRegion1 = new Region();
				progressTransportRegion1.setPrefHeight(HEIGHT);
				HBox.setHgrow(progressTransportRegion1, Priority.ALWAYS);
				progressTransportRegion1.getStyleClass().add("progressbar-piece");
				progressTransportRegion1.getStyleClass().addAll("progressbar-piece-1of2");
				Region progressTransportRegion2 = new Region();
				HBox.setHgrow(progressTransportRegion2, Priority.ALWAYS);
				progressTransportRegion2.setPrefHeight(HEIGHT);
				progressTransportRegion2.getStyleClass().add("progressbar-piece");
				progressTransportRegion2.getStyleClass().addAll("progressbar-piece-2of2");
				progressTransportHBox.getChildren().addAll(progressTransportRegion1, progressTransportRegion2);
				this.add(progressTransportHBox, column, row + 1);
				progressTransportRegionsLeft.put(i, progressTransportRegion1);
				progressTransportRegionsRight.put(i, progressTransportRegion2);
				transportButtons.put(i, transport);
				transport.setOnAction(new TransportEventHandler(i));
				transport.toBack();
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
				ProcessFlowView.this.requestFocus();
				presenter.backgroundClicked();
				arg0.consume();
			}
		});
	}
	
	public void showQuestionMarks(boolean showQuestionMarks) {
		if (showQuestionMarks) {
			for (TransportButton transportButton : transportButtons.values()) {
				transportButton.showTeach();
			}
		} else {
			for (TransportButton transportButton : transportButtons.values()) {
				transportButton.showPause();
			}
		}
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
			for (Region region : progressTransportRegionsLeft.values()) {
				region.getStyleClass().remove("progressbar-piece-unfocussed");
				region.getStyleClass().add("progressbar-piece-unfocussed");
			}
			for (Region region : progressTransportRegionsRight.values()) {
				region.getStyleClass().remove("progressbar-piece-unfocussed");
				region.getStyleClass().add("progressbar-piece-unfocussed");
			}
			for (Entry<Integer, Region> entry : progressDeviceRegions.entrySet()) {
				entry.getValue().getStyleClass().remove("progressbar-piece-unfocussed");
				if (entry.getKey() != index) {
					entry.getValue().getStyleClass().add("progressbar-piece-unfocussed");
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
			for (Region region : progressDeviceRegions.values()) {
				region.getStyleClass().remove("progressbar-piece-unfocussed");
				region.getStyleClass().add("progressbar-piece-unfocussed");
			}
			for (Entry<Integer, Region> entry : progressTransportRegionsLeft.entrySet()) {
				entry.getValue().getStyleClass().remove("progressbar-piece-unfocussed");
				if (entry.getKey() != index) {
					entry.getValue().getStyleClass().add("progressbar-piece-unfocussed");
				}
			}
			for (Entry<Integer, Region> entry : progressTransportRegionsRight.entrySet()) {
				entry.getValue().getStyleClass().remove("progressbar-piece-unfocussed");
				if (entry.getKey() != index) {
					entry.getValue().getStyleClass().add("progressbar-piece-unfocussed");
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
		for (Region region : progressDeviceRegions.values()) {
			region.getStyleClass().remove("progressbar-piece-unfocussed");
		}
		for (Region region : progressTransportRegionsLeft.values()) {
			region.getStyleClass().remove("progressbar-piece-unfocussed");
		}
		for (Region region : progressTransportRegionsRight.values()) {
			region.getStyleClass().remove("progressbar-piece-unfocussed");
		}
	}
	
	public void setAllProgressNone() {
		for (int i : progressDeviceRegions.keySet()) {
			setDeviceProgressNone(i);
		}
		for (int i : progressTransportRegionsRight.keySet()) {
			setTransportProgressNone(i);
		}
		for (DeviceButton device : deviceButtons.values()) {
			device.animate(false);
		}
	}
	
	public void setDeviceProgressGreen(int deviceIndex) {
		setDeviceProgressNone(deviceIndex);
		progressDeviceRegions.get(deviceIndex).getStyleClass().add("progressbar-green");
	}
	
	public void setDeviceProgressYellow(int deviceIndex) {
		progressDeviceRegions.get(deviceIndex).getStyleClass().add("progressbar-yellow");
	}
	
	public void setDeviceProgressNone(int deviceIndex) {
		progressDeviceRegions.get(deviceIndex).getStyleClass().remove("progressbar-green");
		progressDeviceRegions.get(deviceIndex).getStyleClass().remove("progressbar-yellow");
	}
	
	public void setTransportProgressGreen(int transportIndex) {
		setTransportProgressNone(transportIndex);
		progressTransportRegionsLeft.get(transportIndex).getStyleClass().add("progressbar-green");
		progressTransportRegionsRight.get(transportIndex).getStyleClass().add("progressbar-green");
	}
	
	public void setTransportProgressYellow(int transportIndex) {
		setTransportProgressNone(transportIndex);
		progressTransportRegionsLeft.get(transportIndex).getStyleClass().add("progressbar-yellow");
		progressTransportRegionsRight.get(transportIndex).getStyleClass().add("progressbar-yellow");
	}
	
	public void setTransportProgressNone(int transportIndex) {
		progressTransportRegionsLeft.get(transportIndex).getStyleClass().remove("progressbar-green");
		progressTransportRegionsLeft.get(transportIndex).getStyleClass().remove("progressbar-yellow");
		progressTransportRegionsRight.get(transportIndex).getStyleClass().remove("progressbar-green");
		progressTransportRegionsRight.get(transportIndex).getStyleClass().remove("progressbar-yellow");
	}
	
	public void setTransportProgressFirstGreen(int transportIndex) {
		setTransportProgressNone(transportIndex);
		progressTransportRegionsLeft.get(transportIndex).getStyleClass().add("progressbar-green");
	}
	
	public void setTransportProgressFirstYellow(int transportIndex) {
		setTransportProgressNone(transportIndex);
		progressTransportRegionsLeft.get(transportIndex).getStyleClass().add("progressbar-yellow");
	}
	
	public void setTransportProgressSecondYellow(int transportIndex) {
		setTransportProgressNone(transportIndex);
		progressTransportRegionsLeft.get(transportIndex).getStyleClass().add("progressbar-green");
		progressTransportRegionsRight.get(transportIndex).getStyleClass().add("progressbar-yellow");
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
			deviceButtons.get(index).animate(true);
		}
	}
	
	public void stopDeviceAnimation(int index) {
		if ((index<0) || (index>=processFlowAdapter.getDeviceStepCount()) || (deviceButtons.get(index) == null)) {
			throw new IllegalArgumentException("Index is out of bounds or incorrect.");
		} else {
			deviceButtons.get(index).animate(false);
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
		for (Region region : progressDeviceRegions.values()) {
			region.getStyleClass().remove("progressbar-piece-unfocussed");
			region.getStyleClass().add("progressbar-piece-unfocussed");
		}
		for (Entry<Integer, Region> entry : progressTransportRegionsLeft.entrySet()) {
			entry.getValue().getStyleClass().remove("progressbar-piece-unfocussed");
			entry.getValue().getStyleClass().add("progressbar-piece-unfocussed");
		}
		for (Entry<Integer, Region> entry : progressTransportRegionsRight.entrySet()) {
			entry.getValue().getStyleClass().remove("progressbar-piece-unfocussed");
			entry.getValue().getStyleClass().add("progressbar-piece-unfocussed");
		}
	}
	
	public void setAddDeviceMode(boolean addPreProcessPossible, boolean addPostProcessPossible) {
		for (DeviceButton deviceButton : deviceButtons.values()) {
			deviceButton.setFocussed(false);
			deviceButton.setDisable(true);
		}
		for (Entry<Integer, TransportButton> entry : transportButtons.entrySet()) {
			if ((entry.getKey() < processFlowAdapter.getCNCMachineIndex() && addPreProcessPossible)) {
				entry.getValue().setFocussed(true);
				entry.getValue().setDisable(false);
			} else if (addPostProcessPossible) {
				entry.getValue().setFocussed(true);
				entry.getValue().setDisable(false);
			} else {
				entry.getValue().setFocussed(false);
				entry.getValue().setDisable(true);
			}
		}
		for (Region region : progressDeviceRegions.values()) {
			region.getStyleClass().remove("progressbar-piece-unfocussed");
			region.getStyleClass().add("progressbar-piece-unfocussed");
		}
		for (Entry<Integer, Region> entry : progressTransportRegionsLeft.entrySet()) {
			entry.getValue().getStyleClass().remove("progressbar-piece-unfocussed");
			entry.getValue().getStyleClass().add("progressbar-piece-unfocussed");
		}
		for (Entry<Integer, Region> entry : progressTransportRegionsRight.entrySet()) {
			entry.getValue().getStyleClass().remove("progressbar-piece-unfocussed");
			entry.getValue().getStyleClass().add("progressbar-piece-unfocussed");
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
		for (Region region : progressDeviceRegions.values()) {
			region.getStyleClass().remove("progressbar-piece-unfocussed");
		}
		for (Entry<Integer, Region> entry : progressTransportRegionsLeft.entrySet()) {
			entry.getValue().getStyleClass().remove("progressbar-piece-unfocussed");
		}
		for (Entry<Integer, Region> entry : progressTransportRegionsRight.entrySet()) {
			entry.getValue().getStyleClass().remove("progressbar-piece-unfocussed");
		}
	}
	
	public void disableClickable() {
		for (DeviceButton deviceButton : deviceButtons.values()) {
			deviceButton.setClickable(false);
		}
		for (TransportButton transportButton : transportButtons.values()) {
			transportButton.setClickable(false);
		}
	}
}

