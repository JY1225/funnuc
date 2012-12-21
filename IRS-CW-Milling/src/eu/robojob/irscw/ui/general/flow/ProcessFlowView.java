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
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import eu.robojob.irscw.process.ProcessFlow;
import eu.robojob.irscw.ui.configure.ConfigureView;
import eu.robojob.irscw.ui.general.model.ProcessFlowAdapter;

public class ProcessFlowView extends GridPane {

	private ProcessFlowAdapter processFlowAdapter;
	private AbstractProcessFlowPresenter presenter;
	
	private List<DeviceButton> deviceButtons;
	private List<TransportButton> transportButtons;
	
	private List<VBox> deviceProgressRegionsWrappers;		// these VBoxes will contain the progress-bar Regions
	private List<VBox> transportProgressRegionsLeftWrappers;
	private List<VBox> transportProgressRegionsRightWrappers;
	
	private List<List<Region>> deviceProgressRegions;
	private List<List<Region>> transportProgressRegionsLeft;
	private List<List<Region>> transportProgressRegionsRight;
	
	private static final int GAP = 10; 
	private static final String CSS_CLASS_PROCESSFLOW_VIEW = "process-flow-view";
	private int progressBarAmount;
	
	public ProcessFlowView(final ProcessFlow processFlow, final int progressBarAmount) {
		this.processFlowAdapter = new ProcessFlowAdapter(processFlow);
		this.deviceButtons = new ArrayList<DeviceButton>();
		this.transportButtons = new ArrayList<TransportButton>();
		this.deviceProgressRegionsWrappers = new ArrayList<VBox>();
		this.transportProgressRegionsLeftWrappers = new ArrayList<VBox>();
		this.transportProgressRegionsRightWrappers = new ArrayList<VBox>();
		this.deviceProgressRegions = new ArrayList<List<Region>>();
		this.transportProgressRegionsLeft = new ArrayList<List<Region>>();
		this.transportProgressRegionsRight = new ArrayList<List<Region>>();
		build();
		this.progressBarAmount = progressBarAmount;
	}
	
	private void build() {
		this.getChildren().clear();
		deviceProgressRegionsWrappers.clear();
		transportProgressRegionsLeftWrappers.clear();
		transportProgressRegionsRightWrappers.clear();
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
		setupProgressBarRegions();
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
		deviceProgressRegionsWrappers.add(progressVBox);
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
		progressHBox.getChildren().add(progress1VBox);
		transportProgressRegionsLeftWrappers.add(progress1VBox);
		//right
		VBox progress2VBox = new VBox();
		progressHBox.getChildren().add(progress2VBox);
		transportProgressRegionsRightWrappers.add(progress2VBox);
	}
	
	private void setupProgressBarRegions() {
		//FIXME implement
	}
	
	public void showQuestionMarks(final boolean showQuestionMarks) {
		for (TransportButton transportButton : transportButtons) {
			if (showQuestionMarks) {
				transportButton.showTeach();
			} else {
				transportButton.showPause();
			}
		}
	}
	
	public void focusDevice(final int index) {
		if ((index < 0) || (index >= processFlowAdapter.getDeviceStepCount()) || (deviceButtons.get(index) == null)) {
			throw new IllegalArgumentException("Incorrect index [" + index + "].");
		}
		for (TransportButton transport : transportButtons) {
			transport.setFocussed(false);
		}
		for (DeviceButton device : deviceButtons) {
			device.setFocussed(false);
		}
		deviceButtons.get(index).setFocussed(true);
	}
	
	public AbstractProcessFlowPresenter getPresenter() {
		return presenter;
	}
}
