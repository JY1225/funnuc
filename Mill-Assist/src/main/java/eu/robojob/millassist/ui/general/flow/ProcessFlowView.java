package eu.robojob.millassist.ui.general.flow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import eu.robojob.millassist.external.device.EDeviceGroup;
import eu.robojob.millassist.process.ProcessFlow;
import eu.robojob.millassist.ui.general.model.ProcessFlowAdapter;
import eu.robojob.millassist.util.SizeManager;

public class ProcessFlowView extends StackPane {

	public enum ProgressBarPieceMode {
		GREEN, YELLOW, NONE
	}
	
	private GridPane gpFlow;
	
	protected ProcessFlowAdapter processFlowAdapter;
	private AbstractProcessFlowPresenter presenter;
	
	private List<DeviceButton> deviceButtons;
	private List<TransportButton> transportButtons;
	
	private List<List<Region>> deviceProgressRegions;
	private List<List<Region>> transportProgressRegionsLeft;
	private List<List<Region>> transportProgressRegionsRight;
		
	private Label lblProcessName;
		
	private static final int GAP = 14; 
	private static final int PROGRESS_BAR_HEIGHT = 6;
	private static final int PROGRESS_BAR_MARGIN_BOTTOM = 7;
	private static final int PROGRESS_BAR_REGION_HEIGHT = 30;
	private static final int LBL_MARGIN = 8;
	private static final String CSS_CLASS_PROCESSFLOW_VIEW = "process-flow-view";
	private static final String CSS_CLASS_PROGRESS_BAR_PIECE = "progressbar-piece";
	private static final String CSS_CLASS_PROGRESS_BAR_PIECE_FIRST = "progressbar-piece-first";
	private static final String CSS_CLASS_PROGRESS_BAR_PIECE_LAST = "progressbar-piece-last";
	private static final String CSS_CLASS_PROGRESS_BAR_UNFOCUSSED = "progressbar-piece-unfocussed";
	private static final String CSS_CLASS_PROGRESS_BAR_PIECE_GREEN = "progressbar-piece-green";
	private static final String CSS_CLASS_PROGRESS_BAR_PIECE_YELLOW = "progressbar-piece-yellow";
	private static final String CSS_CLASS_PROCESS_NAME = "process-name";
	private static final String CSS_CLASS_PROCESS_NAME_UNSAVED = "unsaved";
	protected int progressBarAmount;
			
	public ProcessFlowView(final int progressBarAmount) {
		this.progressBarAmount = progressBarAmount;
		refresh();
	}
	
	public void loadProcessFlow(final ProcessFlow processFlow) {
		this.processFlowAdapter = new ProcessFlowAdapter(processFlow);
		refresh();
	}
	
	public void refresh() {
		this.deviceButtons = new ArrayList<DeviceButton>();
		this.transportButtons = new ArrayList<TransportButton>();
		this.deviceProgressRegions = new ArrayList<List<Region>>();
		this.transportProgressRegionsLeft = new ArrayList<List<Region>>();
		this.transportProgressRegionsRight = new ArrayList<List<Region>>();
		if (processFlowAdapter != null) {
			build();
		}
	}
	
	public void refreshProcessFlowName() {
		lblProcessName.setText(processFlowAdapter.getProcessFlow().getName());
	}
	
	protected void build() {
		this.getChildren().clear();
		gpFlow = new GridPane();
		lblProcessName = new Label();
		//This indicates the name of the active process. It will be shown in the bottom right corner of the flow region.
		lblProcessName.getStyleClass().remove(CSS_CLASS_PROCESS_NAME_UNSAVED);
		if (processFlowAdapter.getProcessFlow().hasChangesSinceLastSave()) {
			lblProcessName.setText("*" + processFlowAdapter.getProcessFlow().getName());
			lblProcessName.getStyleClass().add(CSS_CLASS_PROCESS_NAME_UNSAVED);
		} else {
			lblProcessName.setText(processFlowAdapter.getProcessFlow().getName());
		}
		lblProcessName.getStyleClass().add(CSS_CLASS_PROCESS_NAME);
		StackPane.setAlignment(lblProcessName, Pos.TOP_RIGHT);
		StackPane.setMargin(lblProcessName, new Insets(LBL_MARGIN, LBL_MARGIN, LBL_MARGIN, LBL_MARGIN));
		gpFlow.getChildren().clear();
		gpFlow.setVgap(GAP);
		gpFlow.setPadding(new Insets(GAP, 50, GAP, 50));
		int column = 0;
		int row = 0;
		for (int i = 0; i < processFlowAdapter.getDeviceStepCount(); i++) {		
			setupDevice(i, column, row);
			// transport
			column++;
			if (i < processFlowAdapter.getTransportStepCount()) {
				setupTransport(i, column, row);
			}
			column++;
		}
		gpFlow.setAlignment(Pos.TOP_CENTER);
		gpFlow.setPrefHeight(SizeManager.HEIGHT_TOP);
		gpFlow.setPrefWidth(SizeManager.WIDTH);
		gpFlow.getStyleClass().add(CSS_CLASS_PROCESSFLOW_VIEW);
		gpFlow.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(final MouseEvent event) {
				ProcessFlowView.this.requestFocus();
				presenter.backgroundClicked();
				event.consume();
			}
		});
		ScrollPane scrollPane = new ScrollPane();
		scrollPane.setContent(gpFlow);
		this.getChildren().add(scrollPane);
		this.getChildren().add(lblProcessName);
		scrollPane.setVbarPolicy(ScrollBarPolicy.NEVER);
		scrollPane.setHbarPolicy(ScrollBarPolicy.ALWAYS);
		scrollPane.setFitToHeight(false);
		scrollPane.setFitToWidth(false);
		scrollPane.setPannable(false);
		this.setPrefWidth(SizeManager.WIDTH);
		this.setPrefHeight(SizeManager.HEIGHT_TOP);
	}
	
	public List<DeviceButton> getDeviceButtons() {
		return deviceButtons;
	}
	
	public List<TransportButton> getTransportButtons() {
		return transportButtons;
	}
	
	private void setupDevice(final int index, final int column, final int row) {
		// device
		DeviceButton device = new DeviceButton(processFlowAdapter.getDeviceInformation(index));
		device.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent arg0) {
				getPresenter().deviceClicked(index);
			}
		});
		gpFlow.add(device, column, row);
		deviceButtons.add(device);
		device.toFront();
		VBox progressVBox = new VBox();
		progressVBox.setMinHeight(PROGRESS_BAR_REGION_HEIGHT);
		progressVBox.setPrefHeight(PROGRESS_BAR_REGION_HEIGHT);
		gpFlow.add(progressVBox, column, (1 + row));
		setupDeviceProgressBarRegions(index, progressVBox);
	}
	
	private void setupDeviceProgressBarRegions(final int index, final VBox vBox) {
		List<Region> regions = new ArrayList<Region>();
		deviceProgressRegions.add(index, regions);
		for (int i = 0; i < progressBarAmount; i++) {
			Region region = new Region();
			region.getStyleClass().add(CSS_CLASS_PROGRESS_BAR_PIECE);
			if (index == 0) {
				region.getStyleClass().add(CSS_CLASS_PROGRESS_BAR_PIECE_FIRST);
			} else if (index == (processFlowAdapter.getDeviceStepCount() - 1)) {
				region.getStyleClass().add(CSS_CLASS_PROGRESS_BAR_PIECE_LAST);
			}
			region.setPrefHeight(PROGRESS_BAR_HEIGHT);
			region.setMinHeight(PROGRESS_BAR_HEIGHT);
			vBox.getChildren().add(region);
			VBox.setMargin(region, new Insets(0, 0, PROGRESS_BAR_MARGIN_BOTTOM, 0));
			regions.add(region);
		}
	}
	
	private void setupTransport(final int index, final int column, final int row) {
		TransportButton transport = new TransportButton(processFlowAdapter.getTransportInformation(index));
		transport.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent arg0) {
				getPresenter().transportClicked(index);
			}
		});
		gpFlow.add(transport, column, row);
		transport.toBack();
		GridPane.setMargin(transport, new Insets(0, -2, 0, -2));
		transportButtons.add(transport);
		HBox progressHBox = new HBox();
		gpFlow.add(progressHBox, column, (1 + row));
		//left
		VBox progress1VBox = new VBox();
		progress1VBox.setMinHeight(PROGRESS_BAR_REGION_HEIGHT);
		progress1VBox.setPrefHeight(PROGRESS_BAR_REGION_HEIGHT);
		HBox.setHgrow(progress1VBox, Priority.ALWAYS);
		progressHBox.getChildren().add(progress1VBox);
		//right
		VBox progress2VBox = new VBox();
		progress2VBox.setMinHeight(PROGRESS_BAR_REGION_HEIGHT);
		progress2VBox.setPrefHeight(PROGRESS_BAR_REGION_HEIGHT);
		HBox.setHgrow(progress2VBox, Priority.ALWAYS);
		progressHBox.getChildren().add(progress2VBox);
		setupTransportProgressBarRegions(index, progress1VBox, progress2VBox);
	}
	
	private void setupTransportProgressBarRegions(final int index, final VBox vbox1, final VBox vbox2) {
		List<Region> regions1 = new ArrayList<Region>();
		transportProgressRegionsLeft.add(index, regions1);
		List<Region> regions2 = new ArrayList<Region>();
		transportProgressRegionsRight.add(index, regions2);
		for (int i = 0; i < progressBarAmount; i++) {
			Region region1 = new Region();
			region1.getStyleClass().add(CSS_CLASS_PROGRESS_BAR_PIECE);
			region1.setPrefHeight(PROGRESS_BAR_HEIGHT);
			region1.setMinHeight(PROGRESS_BAR_HEIGHT);
			vbox1.getChildren().add(region1);
			VBox.setMargin(region1, new Insets(0, 0, PROGRESS_BAR_MARGIN_BOTTOM, 0));
			regions1.add(i, region1);
			Region region2 = new Region();
			region2.getStyleClass().add(CSS_CLASS_PROGRESS_BAR_PIECE);
			region2.setPrefHeight(PROGRESS_BAR_HEIGHT);
			region2.setMinHeight(PROGRESS_BAR_HEIGHT);
			vbox2.getChildren().add(region2);
			regions2.add(i, region2);
			VBox.setMargin(region2, new Insets(0, 0, PROGRESS_BAR_MARGIN_BOTTOM, 0));
		}
	}
	
	private void unfocusAll() {
		for (TransportButton transport : transportButtons) {
			transport.setFocussed(false);
		}
		for (DeviceButton device : deviceButtons) {
			device.setFocussed(false);
		}
		for (List<Region> regions : deviceProgressRegions) {
			for (Region region : regions) {
				region.getStyleClass().remove(CSS_CLASS_PROGRESS_BAR_UNFOCUSSED);
				region.getStyleClass().add(CSS_CLASS_PROGRESS_BAR_UNFOCUSSED);
			}
		}
		for (List<Region> regions : transportProgressRegionsLeft) {
			for (Region region : regions) {
				region.getStyleClass().remove(CSS_CLASS_PROGRESS_BAR_UNFOCUSSED);
				region.getStyleClass().add(CSS_CLASS_PROGRESS_BAR_UNFOCUSSED);
			}
		}
		for (List<Region> regions : transportProgressRegionsRight) {
			for (Region region : regions) {
				region.getStyleClass().remove(CSS_CLASS_PROGRESS_BAR_UNFOCUSSED);
				region.getStyleClass().add(CSS_CLASS_PROGRESS_BAR_UNFOCUSSED);
			}
		}
	}
	
	public void focusDevice(final int index) {
		if ((index < 0) || (index >= processFlowAdapter.getDeviceStepCount()) || (deviceButtons.get(index) == null)) {
			throw new IllegalArgumentException("Incorrect index [" + index + "].");
		}
		unfocusAll();
		deviceButtons.get(index).setFocussed(true);
		for (Region region : deviceProgressRegions.get(index)) {
			region.getStyleClass().remove(CSS_CLASS_PROGRESS_BAR_UNFOCUSSED);
		}
	}
	
	public void focusTransport(final int index) {
		if ((index < 0) || (index >= processFlowAdapter.getDeviceStepCount()) || (deviceButtons.get(index) == null)) {
			throw new IllegalArgumentException("Incorrect index [" + index + "].");
		}
		unfocusAll();
		transportButtons.get(index).setFocussed(true);
		for (Region region : transportProgressRegionsLeft.get(index)) {
			region.getStyleClass().remove(CSS_CLASS_PROGRESS_BAR_UNFOCUSSED);
		}
		for (Region region : transportProgressRegionsRight.get(index)) {
			region.getStyleClass().remove(CSS_CLASS_PROGRESS_BAR_UNFOCUSSED);
		}
	}
	
	public void focusAll() {
		for (DeviceButton device : deviceButtons) {
			device.setFocussed(true);
		}
		for (TransportButton transport : transportButtons) {
			transport.setFocussed(true);
		}
		for (List<Region> regions : deviceProgressRegions) {
			for (Region region : regions) {
				region.getStyleClass().remove(CSS_CLASS_PROGRESS_BAR_UNFOCUSSED);
			}
		}
		for (List<Region> regions : transportProgressRegionsLeft) {
			for (Region region : regions) {
				region.getStyleClass().remove(CSS_CLASS_PROGRESS_BAR_UNFOCUSSED);
			}
		}
		for (List<Region> regions : transportProgressRegionsRight) {
			for (Region region : regions) {
				region.getStyleClass().remove(CSS_CLASS_PROGRESS_BAR_UNFOCUSSED);
			}
		}
	}
	
	public void setDeviceAnimation(final int index, final boolean animate) {
		if ((index < 0) || (index >= processFlowAdapter.getDeviceStepCount()) || (deviceButtons.get(index) == null)) {
			throw new IllegalArgumentException("Incorrect index [" + index + "].");
		} else {
			deviceButtons.get(index).animate(animate);
		}
	}
	
	public AbstractProcessFlowPresenter getPresenter() {
		return presenter;
	}
	
	public void setPresenter(final AbstractProcessFlowPresenter presenter) {
		this.presenter = presenter;
	}
	
	public void setAllProgressBarPiecesModeNone() {
		for (List<Region> regions : deviceProgressRegions) {
			for (Region region : regions) {
				region.getStyleClass().removeAll(CSS_CLASS_PROGRESS_BAR_PIECE_GREEN, CSS_CLASS_PROGRESS_BAR_PIECE_YELLOW);
			}
		}
		for (List<Region> regions : transportProgressRegionsLeft) {
			for (Region region : regions) {
				region.getStyleClass().removeAll(CSS_CLASS_PROGRESS_BAR_PIECE_GREEN, CSS_CLASS_PROGRESS_BAR_PIECE_YELLOW);
			}
		}
		for (List<Region> regions : transportProgressRegionsRight) {
			for (Region region : regions) {
				region.getStyleClass().removeAll(CSS_CLASS_PROGRESS_BAR_PIECE_GREEN, CSS_CLASS_PROGRESS_BAR_PIECE_YELLOW);
			}
		}
	}
	
	public void stopAllAnimations() {
		for (DeviceButton deviceButton : deviceButtons) {
			deviceButton.animate(false);
		}
	}
	
	public void setAllProgressBarPiecesModeNone(final int progressBarIndex) {
		for (List<Region> regions : deviceProgressRegions) {
			regions.get(progressBarIndex).getStyleClass().removeAll(CSS_CLASS_PROGRESS_BAR_PIECE_GREEN, CSS_CLASS_PROGRESS_BAR_PIECE_YELLOW);
		}
		for (List<Region> regions : transportProgressRegionsLeft) {
			regions.get(progressBarIndex).getStyleClass().removeAll(CSS_CLASS_PROGRESS_BAR_PIECE_GREEN, CSS_CLASS_PROGRESS_BAR_PIECE_YELLOW);
		}
		for (List<Region> regions : transportProgressRegionsRight) {
			regions.get(progressBarIndex).getStyleClass().removeAll(CSS_CLASS_PROGRESS_BAR_PIECE_GREEN, CSS_CLASS_PROGRESS_BAR_PIECE_YELLOW);
		}
	}
		
	public void setDeviceProgressBarPieceMode(final int deviceIndex, final int progressBarIndex, final ProgressBarPieceMode mode) {
		String cssClassName = null;
		switch (mode) {
			case GREEN:
				cssClassName = CSS_CLASS_PROGRESS_BAR_PIECE_GREEN;
				break;
			case YELLOW:
				cssClassName = CSS_CLASS_PROGRESS_BAR_PIECE_YELLOW;
				break;
			case NONE:
				break;
			default:
				throw new IllegalArgumentException("Unkown ProgressBarPieceMode: " + mode);
		}
		deviceProgressRegions.get(deviceIndex).get(progressBarIndex).getStyleClass().removeAll(CSS_CLASS_PROGRESS_BAR_PIECE_GREEN, CSS_CLASS_PROGRESS_BAR_PIECE_YELLOW);
		deviceProgressRegions.get(deviceIndex).get(progressBarIndex).getStyleClass().add(cssClassName);
	}
	
	public void setTransportLeftProgressBarPieceMode(final int transportIndex, final int progressBarIndex, final ProgressBarPieceMode mode) {
		String cssClassName = null;
		switch (mode) {
			case GREEN:
				cssClassName = CSS_CLASS_PROGRESS_BAR_PIECE_GREEN;
				break;
			case YELLOW:
				cssClassName = CSS_CLASS_PROGRESS_BAR_PIECE_YELLOW;
				break;
			case NONE:
				break;
			default:
				throw new IllegalArgumentException("Unkown ProgressBarPieceMode: " + mode);
		}
		transportProgressRegionsLeft.get(transportIndex).get(progressBarIndex).getStyleClass().removeAll(CSS_CLASS_PROGRESS_BAR_PIECE_GREEN, CSS_CLASS_PROGRESS_BAR_PIECE_YELLOW);
		transportProgressRegionsLeft.get(transportIndex).get(progressBarIndex).getStyleClass().add(cssClassName);
	}
	
	public void setTransportRightProgressBarPieceMode(final int transportIndex, final int progressBarIndex, final ProgressBarPieceMode mode) {
		String cssClassName = null;
		switch (mode) {
			case GREEN:
				cssClassName = CSS_CLASS_PROGRESS_BAR_PIECE_GREEN;
				break;
			case YELLOW:
				cssClassName = CSS_CLASS_PROGRESS_BAR_PIECE_YELLOW;
				break;
			case NONE:
				break;
			default:
				throw new IllegalArgumentException("Unknown ProgressBarPieceMode: " + mode);
		}
		transportProgressRegionsRight.get(transportIndex).get(progressBarIndex).getStyleClass().removeAll(CSS_CLASS_PROGRESS_BAR_PIECE_GREEN, CSS_CLASS_PROGRESS_BAR_PIECE_YELLOW);
		transportProgressRegionsRight.get(transportIndex).get(progressBarIndex).getStyleClass().add(cssClassName);
	}
	
	public void showRemoveDevice() {
		unfocusAll();
		Map<EDeviceGroup, DeviceButton> uniqueGroupDevices = new HashMap<EDeviceGroup, DeviceButton>();
		for (DeviceButton deviceButton : deviceButtons) {
			//TODO - this allows only the last device of the POST_PROCESSING devices to be deleted - no choice
			if (deviceButton.getDeviceInformation().getType().equals(EDeviceGroup.POST_PROCESSING)) {
				if (uniqueGroupDevices.containsKey(EDeviceGroup.POST_PROCESSING)) {
					uniqueGroupDevices.get(EDeviceGroup.POST_PROCESSING).setFocussed(false);
					uniqueGroupDevices.get(EDeviceGroup.POST_PROCESSING).setDisable(true);
				} 
				deviceButton.setFocussed(true);
				deviceButton.setDisable(false);
				uniqueGroupDevices.put(EDeviceGroup.POST_PROCESSING, deviceButton);
			} else if (deviceButton.getDeviceInformation().getType().equals(EDeviceGroup.PRE_PROCESSING)) {
				if (uniqueGroupDevices.containsKey(EDeviceGroup.PRE_PROCESSING)) {
					uniqueGroupDevices.get(EDeviceGroup.PRE_PROCESSING).setFocussed(false);
					uniqueGroupDevices.get(EDeviceGroup.PRE_PROCESSING).setDisable(true);
				} 
				uniqueGroupDevices.put(EDeviceGroup.PRE_PROCESSING, deviceButton);
				deviceButton.setFocussed(true);
				deviceButton.setDisable(false);
			} else {
				deviceButton.setFocussed(false);
				deviceButton.setDisable(true);
			}
		}
		for (TransportButton transportButton : transportButtons) {
			transportButton.setFocussed(false);
			transportButton.setDisable(true);
		}
	}
	
	public void showAddDevice(final boolean addPreProcessPossible, final boolean addPostProcessPossible) {
		unfocusAll();
		TransportButton btnTransport;
		for (int i = 0; i < processFlowAdapter.getTransportStepCount(); i++) {
			btnTransport = transportButtons.get(i);
			if (i < processFlowAdapter.getCNCMachineIndex()) {
				if (addPreProcessPossible) {
					btnTransport.setFocussed(true);
					btnTransport.setDisable(false);
				} else {
					btnTransport.setDisable(true);
				}
			} else {
				//TODO - this only works for reversalUnits or postProcessing device that needs to be place after the last step
				if (addPostProcessPossible && i >= processFlowAdapter.getLastCNCMachineIndex()) {
					btnTransport.setFocussed(true);
					btnTransport.setDisable(false);
				} else {
					btnTransport.setDisable(true);
				}
			}
		}
	}
	
	public void showNormal() {
		focusAll();
	}
	
	public void disableClickable() {
		for (DeviceButton deviceButton : deviceButtons) {
			deviceButton.setClickable(false);
		}
		for (TransportButton transportButton : transportButtons) {
			transportButton.setClickable(false);
		}
	}
	
	public void animateDevice(final int deviceIndex, final boolean doAnimation) {
		deviceButtons.get(deviceIndex).animate(doAnimation);
	}
}
