package eu.robojob.irscw.ui.general.flow;

import java.util.ArrayList;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import eu.robojob.irscw.external.device.DeviceType;
import eu.robojob.irscw.process.ProcessFlow;
import eu.robojob.irscw.ui.configure.ConfigureView;
import eu.robojob.irscw.ui.general.model.ProcessFlowAdapter;

public class ProcessFlowView extends GridPane {

	public enum ProgressBarPieceMode {
		GREEN, YELLOW, NONE
	}
	
	private ProcessFlowAdapter processFlowAdapter;
	private AbstractProcessFlowPresenter presenter;
	
	private List<DeviceButton> deviceButtons;
	private List<TransportButton> transportButtons;
	
	private List<List<Region>> deviceProgressRegions;
	private List<List<Region>> transportProgressRegionsLeft;
	private List<List<Region>> transportProgressRegionsRight;
		
	private static final int GAP = 10; 
	private static final int PROGRESS_BAR_HEIGHT = 7;
	private static final int PROGRESS_BAR_MARGIN_BOTTOM = 7;
	private static final String CSS_CLASS_PROCESSFLOW_VIEW = "process-flow-view";
	private static final String CSS_CLASS_PROGRESS_BAR_PIECE = "progressbar-piece";
	private static final String CSS_CLASS_PROGRESS_BAR_PIECE_FIRST = "progressbar-piece-first";
	private static final String CSS_CLASS_PROGRESS_BAR_PIECE_LAST = "progressbar-piece-last";
	private static final String CSS_CLASS_PROGRESS_BAR_UNFOCUSSED = "progressbar-piece-unfocussed";
	private static final String CSS_CLASS_PROGRESS_BAR_PIECE_GREEN = "progressbar-piece-green";
	private static final String CSS_CLASS_PROGRESS_BAR_PIECE_YELLOW = "progressbar-piece-yellow";
	private int progressBarAmount;
	
	private boolean showQuestionMarks;
	
	public ProcessFlowView(final int progressBarAmount) {
		this.progressBarAmount = progressBarAmount;
		this.showQuestionMarks = false;
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
		this.showQuestionMarks(showQuestionMarks);
	}
	
	private void build() {
		this.getChildren().clear();
		setVgap(GAP);
		setPadding(new Insets(GAP, 0, GAP, 0));
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
		this.setAlignment(Pos.CENTER);
		this.setPrefHeight(ConfigureView.HEIGHT_TOP);
		this.setPrefWidth(ConfigureView.WIDTH);
		this.getStyleClass().add(CSS_CLASS_PROCESSFLOW_VIEW);
		this.setEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(final MouseEvent event) {
				ProcessFlowView.this.requestFocus();
				presenter.backgroundClicked();
				event.consume();
			}
		});
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
		this.add(device, column, row);
		deviceButtons.add(device);
		VBox progressVBox = new VBox();
		this.add(progressVBox, column, (1 + row));
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
		this.add(transport, column, row);
		transportButtons.add(transport);
		HBox progressHBox = new HBox();
		this.add(progressHBox, column, (1 + row));
		//left
		VBox progress1VBox = new VBox();
		HBox.setHgrow(progress1VBox, Priority.ALWAYS);
		progressHBox.getChildren().add(progress1VBox);
		//right
		VBox progress2VBox = new VBox();
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
			vbox1.getChildren().add(region1);
			VBox.setMargin(region1, new Insets(0, 0, PROGRESS_BAR_MARGIN_BOTTOM, 0));
			regions1.add(i, region1);
			Region region2 = new Region();
			region2.getStyleClass().add(CSS_CLASS_PROGRESS_BAR_PIECE);
			region2.setPrefHeight(PROGRESS_BAR_HEIGHT);
			vbox2.getChildren().add(region2);
			regions2.add(i, region2);
			VBox.setMargin(region2, new Insets(0, 0, PROGRESS_BAR_MARGIN_BOTTOM, 0));
		}
	}
	
	public void showQuestionMarks(final boolean showQuestionMarks) {
		this.showQuestionMarks = showQuestionMarks;
		for (TransportButton transportButton : transportButtons) {
			if (showQuestionMarks) {
				transportButton.showTeach();
			} else {
				transportButton.showPause();
			}
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
				throw new IllegalArgumentException("Unkown ProgressBarPieceMode: " + mode);
		}
		transportProgressRegionsRight.get(transportIndex).get(progressBarIndex).getStyleClass().removeAll(CSS_CLASS_PROGRESS_BAR_PIECE_GREEN, CSS_CLASS_PROGRESS_BAR_PIECE_YELLOW);
		transportProgressRegionsRight.get(transportIndex).get(progressBarIndex).getStyleClass().add(cssClassName);
	}
	
	public void showRemoveDevice() {
		unfocusAll();
		for (DeviceButton deviceButton : deviceButtons) {
			if ((deviceButton.getDeviceInformation().getType() == DeviceType.POST_PROCESSING) || (deviceButton.getDeviceInformation().getType() == DeviceType.PRE_PROCESSING)) {
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
		for (int i = 0; i < processFlowAdapter.getTransportStepCount(); i++) {
			if (i < processFlowAdapter.getCNCMachineIndex()) {
				if (addPreProcessPossible) {
					transportButtons.get(i).setFocussed(true);
					transportButtons.get(i).setDisable(false);
				}
			} else {
				if (addPostProcessPossible) {
					transportButtons.get(i).setFocussed(true);
					transportButtons.get(i).setDisable(false);
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
