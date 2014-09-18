package eu.robojob.millassist.ui.admin.device.cnc;

import java.util.Set;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import eu.robojob.millassist.external.device.processing.cnc.AbstractCNCMachine;
import eu.robojob.millassist.external.device.processing.cnc.AbstractCNCMachine.WayOfOperating;
import eu.robojob.millassist.ui.controls.FullTextField;
import eu.robojob.millassist.ui.controls.IntegerTextField;
import eu.robojob.millassist.ui.controls.TextInputControlListener;
import eu.robojob.millassist.util.Translator;
import eu.robojob.millassist.util.UIConstants;

public class CNCMachineGeneralView extends GridPane {
	
	private CNCMachineConfigurePresenter presenter;
	private Label lblName;
	private FullTextField fulltxtName;
	private Label lblIPAddress;
	private FullTextField fulltxtIp;
	private Label lblPort;
	private IntegerTextField itxtPort;
	private Label lblStatus;
	private Label lblStatusVal;
	private Label lblWayOfOperating;
	private Label lblDevInterface;
	private RadioButton rbbWayOfOperatingStartStop;
	private RadioButton rbbWayOfOperatingMCodes;
	private RadioButton rbbWayOfOperatingMCodesDualLoad;
	private RadioButton rbbOldDevInt, rbbNewDevInt;
	private ToggleGroup tgWayOfOperating, tgDevIntVersion;
	private Label lblClampingWidthR;
	private Button btnClampingWidthDeltaRp90;
	private Button btnClampingWidthDeltaRm90;
	private Region spacer;
	private int clampingWidthDeltaR; 
	private boolean devIntVersion;
		
	private ObservableList<String> userFrameNames;
	
	private static final String NAME = "CNCMachineGeneralView.name";
	private static final String IP = "CNCMachineGeneralView.ipAddress";
	private static final String PORT = "CNCMachineGeneralView.port";
	private static final String STATUS = "CNCMachineGeneralView.status";
	
	private static final String WAY_OF_OPERATING = "CNCMachineGeneralView.wayOfOperating";
	private static final String START_STOP = "CNCMachineGeneralView.startStop";
	private static final String M_CODES = "CNCMachineGeneralView.mCodes";
	private static final String M_CODES_DUAL_LOAD = "CNCMachineGeneralView.mCodesDualLoad";
	private static final String STATUS_CONNECTED = "CNCMachineGeneralView.statusConnected";
	private static final String STATUS_DISCONNECTED = "CNCMachineGeneralView.statusDisconnected";
	private static final String CLAMPING_WIDTH_R = "CNCMachineGeneralView.clampingWidthR";	
	private static final String LBL_DEV_INT = "CNCMachineGeneralView.deviceInterface";
	private static final String NEW_DEV_INT = "CNCMachineGeneralView.newDevInterface";
	private static final String OLD_DEV_INT = "CNCMachineGeneralView.oldDevInterface";
	
	private static final String CSS_CLASS_BUTTON = "form-button";
	private static final String CSS_CLASS_BUTTON_LABEL = "btn-start-label";

	protected static final String CSS_CLASS_FORM_BUTTON_BAR_LEFT = "form-button-bar-left";
	protected static final String CSS_CLASS_FORM_BUTTON_BAR_RIGHT = "form-button-bar-right";
	protected static final String CSS_CLASS_FORM_BUTTON_BAR_CENTER = "form-button-bar-center";
	protected static final String CSS_CLASS_FORM_BUTTON_ACTIVE = "form-button-active";
	
	public CNCMachineGeneralView() {
		this.userFrameNames = FXCollections.observableArrayList();
		build();
	}
	
	public void setPresenter(final CNCMachineConfigurePresenter presenter) {
		this.presenter = presenter;
	}
	
	public void build() {
		setVgap(20);
		setHgap(20);
		setAlignment(Pos.TOP_CENTER);
		lblName = new Label(Translator.getTranslation(NAME));
		fulltxtName = new FullTextField(100);
		fulltxtName.setPrefSize(200, UIConstants.TEXT_FIELD_HEIGHT);
		lblIPAddress = new Label(Translator.getTranslation(IP));
		fulltxtIp = new FullTextField(15);
		fulltxtIp.setPrefSize(200, UIConstants.TEXT_FIELD_HEIGHT);
		lblPort = new Label(Translator.getTranslation(PORT));
		itxtPort = new IntegerTextField(5);
		itxtPort.setPrefSize(50, UIConstants.TEXT_FIELD_HEIGHT);
		lblStatus = new Label(Translator.getTranslation(STATUS));
		lblStatusVal = new Label();
		spacer = new Region();
		spacer.setPrefWidth(10);
		spacer.setMinWidth(10);
		spacer.setMaxWidth(10);
		
		tgWayOfOperating = new ToggleGroup();
		lblWayOfOperating = new Label(Translator.getTranslation(WAY_OF_OPERATING));
		rbbWayOfOperatingStartStop = new RadioButton(Translator.getTranslation(START_STOP));
		rbbWayOfOperatingStartStop.setToggleGroup(tgWayOfOperating);
		rbbWayOfOperatingStartStop.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(final ObservableValue<? extends Boolean> arg0, final Boolean oldValue, final Boolean newValue) {
				if (newValue) {
					presenter.setWayOfOperating(WayOfOperating.START_STOP);
				}
			}
		});
		rbbWayOfOperatingMCodes = new RadioButton(Translator.getTranslation(M_CODES));
		rbbWayOfOperatingMCodes.setToggleGroup(tgWayOfOperating);
		rbbWayOfOperatingMCodes.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(final ObservableValue<? extends Boolean> arg0, final Boolean oldValue, final Boolean newValue) {
				if (newValue) {
					presenter.setWayOfOperating(WayOfOperating.M_CODES);
				}
			}
		});
		rbbWayOfOperatingMCodesDualLoad = new RadioButton(Translator.getTranslation(M_CODES_DUAL_LOAD));
		rbbWayOfOperatingMCodesDualLoad.setToggleGroup(tgWayOfOperating);
		rbbWayOfOperatingMCodesDualLoad.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(final ObservableValue<? extends Boolean> arg0, final Boolean oldValue, final Boolean newValue) {
				if (newValue) {
					presenter.setWayOfOperating(WayOfOperating.M_CODES_DUAL_LOAD);
				}
			}
		});
		VBox vboxRadioButtonsWayOfOperating = new VBox();
		vboxRadioButtonsWayOfOperating.setSpacing(10);
		vboxRadioButtonsWayOfOperating.getChildren().addAll(rbbWayOfOperatingStartStop, rbbWayOfOperatingMCodes, rbbWayOfOperatingMCodesDualLoad);
				
		lblClampingWidthR = new Label(Translator.getTranslation(CLAMPING_WIDTH_R));
		btnClampingWidthDeltaRm90 = createButton("-90°", CSS_CLASS_FORM_BUTTON_BAR_LEFT, UIConstants.BUTTON_HEIGHT * 2, new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent arg0) {
				clampingWidthDeltaR = -90;
				btnClampingWidthDeltaRm90.getStyleClass().add(CSS_CLASS_FORM_BUTTON_ACTIVE);
				btnClampingWidthDeltaRp90.getStyleClass().remove(CSS_CLASS_FORM_BUTTON_ACTIVE);
			}
		});
		btnClampingWidthDeltaRp90 = createButton("+90°", CSS_CLASS_FORM_BUTTON_BAR_RIGHT, UIConstants.BUTTON_HEIGHT * 2, new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent arg0) {
				clampingWidthDeltaR = 90;
				btnClampingWidthDeltaRm90.getStyleClass().remove(CSS_CLASS_FORM_BUTTON_ACTIVE);
				btnClampingWidthDeltaRp90.getStyleClass().add(CSS_CLASS_FORM_BUTTON_ACTIVE);
			}
		});
		
		tgDevIntVersion = new ToggleGroup();
		lblDevInterface = new Label(Translator.getTranslation(LBL_DEV_INT));
		rbbOldDevInt = new RadioButton(Translator.getTranslation(OLD_DEV_INT));
		rbbOldDevInt.setToggleGroup(tgDevIntVersion);
		rbbOldDevInt.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(final ObservableValue<? extends Boolean> arg0, final Boolean oldValue, final Boolean newValue) {
				if (newValue) {
					devIntVersion = false;
				}
			}
		});
		rbbNewDevInt = new RadioButton(Translator.getTranslation(NEW_DEV_INT));
		rbbNewDevInt.setToggleGroup(tgDevIntVersion);
		rbbNewDevInt.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(final ObservableValue<? extends Boolean> arg0, final Boolean oldValue, final Boolean newValue) {
				if (newValue) {
					devIntVersion = true;
				}
			}
		});
		VBox vboxRadioButtonsDevIntVersion = new VBox();
		vboxRadioButtonsDevIntVersion.setSpacing(10);
		vboxRadioButtonsDevIntVersion.getChildren().addAll(rbbOldDevInt, rbbNewDevInt);
		
		this.clampingWidthDeltaR = 0;
		
		int row = 0;
		int column = 0;
		add(lblName, column++, row);
		add(fulltxtName, column++, row, 4, 1);
		column = 0; row++;
		add(lblIPAddress, column++, row);
		add(fulltxtIp, column++, row, 4, 1);
		column = 0; row++;
		add(lblPort, column++, row);
		add(itxtPort, column++, row);
		add(spacer, column++, row);
		add(lblStatus, column++, row);
		add(lblStatusVal, column++, row);
		column = 0; row++;
		add(lblWayOfOperating, column++, row);
		add(vboxRadioButtonsWayOfOperating, column++, row, 4, 1);
		column = 0; row++;
		add(lblClampingWidthR, column++, row);
		
		HBox hboxWidthOffsetButtons = new HBox();
		hboxWidthOffsetButtons.getChildren().addAll(btnClampingWidthDeltaRm90, btnClampingWidthDeltaRp90);
		hboxWidthOffsetButtons.setSpacing(0);
		hboxWidthOffsetButtons.setAlignment(Pos.CENTER_LEFT);
		add(hboxWidthOffsetButtons, column++, row, 3, 1);
		
		column = 0; row++;
		add(lblDevInterface, column++, row);
		add(vboxRadioButtonsDevIntVersion, column, row, 4, 1);
	}
	
	public void refresh(final Set<String> userFrameNames, final AbstractCNCMachine cncMachine) {
		this.userFrameNames.clear();
		this.userFrameNames.addAll(userFrameNames);
		fulltxtName.setText(cncMachine.getName());
		fulltxtIp.setText(cncMachine.getCNCMachineSocketCommunication().getExternalCommunicationThread().getSocketConnection().getIpAddress());
		itxtPort.setText(cncMachine.getCNCMachineSocketCommunication().getExternalCommunicationThread().getSocketConnection().getPortNumber() + "");
		refreshStatus(cncMachine);
		btnClampingWidthDeltaRp90.getStyleClass().remove(CSS_CLASS_FORM_BUTTON_ACTIVE);
		btnClampingWidthDeltaRm90.getStyleClass().remove(CSS_CLASS_FORM_BUTTON_ACTIVE);
		if (cncMachine.getClampingWidthR() == 90) {
			clampingWidthDeltaR = 90;
			btnClampingWidthDeltaRp90.getStyleClass().add(CSS_CLASS_FORM_BUTTON_ACTIVE);
		} else if (cncMachine.getClampingWidthR() == -90) {
			btnClampingWidthDeltaRm90.getStyleClass().add(CSS_CLASS_FORM_BUTTON_ACTIVE);
			clampingWidthDeltaR = 90;			
		} else {
			throw new IllegalStateException("Clamping width delta R machine should be 90 or -90.");
		}
		if (cncMachine.getWayOfOperating() == WayOfOperating.START_STOP) {
			rbbWayOfOperatingStartStop.selectedProperty().set(true);
			presenter.setWayOfOperating(WayOfOperating.START_STOP);
		} else if (cncMachine.getWayOfOperating() == WayOfOperating.M_CODES) {
			rbbWayOfOperatingMCodes.selectedProperty().set(true);
			presenter.setWayOfOperating(WayOfOperating.M_CODES);
		} else if (cncMachine.getWayOfOperating() == WayOfOperating.M_CODES_DUAL_LOAD) {
			rbbWayOfOperatingMCodesDualLoad.selectedProperty().set(true);
			presenter.setWayOfOperating(WayOfOperating.M_CODES_DUAL_LOAD);
		} else {
			throw new IllegalStateException("Unknown way of operating: " + cncMachine.getWayOfOperating());
		}
		if (devIntVersion) {
			rbbNewDevInt.selectedProperty().set(true);
		} else {
			rbbOldDevInt.selectedProperty().set(true);
		}
	}
	
	public void refreshStatus(final AbstractCNCMachine cncMachine) {
		if(cncMachine.isConnected()) {
			lblStatusVal.setText(Translator.getTranslation(STATUS_CONNECTED));
		} else {
			lblStatusVal.setText(Translator.getTranslation(STATUS_DISCONNECTED));	
		}
	}
	
	public void setTextFieldListener(final TextInputControlListener listener) {
		fulltxtName.setFocusListener(listener);
		fulltxtIp.setFocusListener(listener);
		itxtPort.setFocusListener(listener);
	}
	
	public String getName() {
		return fulltxtName.getText();
	}
	
	public String getIp() {
		return fulltxtIp.getText();
	}
	
	public int getPort() {
		return Integer.parseInt(itxtPort.getText());
	}
	
	public int getWidthR() {
		return clampingWidthDeltaR;
	}
	
	public boolean getNewDevInt() {
		return devIntVersion;
	}
	
	public void setNewDevInt(boolean isNewDevInt) {
		this.devIntVersion = isNewDevInt;
	}
	
	public WayOfOperating getWayOfOperating() {
		if (rbbWayOfOperatingMCodes.selectedProperty().getValue()) {
			return WayOfOperating.M_CODES;
		} else if (rbbWayOfOperatingMCodesDualLoad.selectedProperty().getValue()) {
			return WayOfOperating.M_CODES_DUAL_LOAD;
		} else {
			return WayOfOperating.START_STOP;
		}
	}
	
	private static Button createButton(final String text, final String cssClass, final double width, final EventHandler<ActionEvent> action) {
		Button button = new Button();
		Label label = new Label(text);
		label.getStyleClass().add(CSS_CLASS_BUTTON_LABEL);
		label.setAlignment(Pos.CENTER);
		label.setPrefSize(width, UIConstants.BUTTON_HEIGHT);
		button.setGraphic(label);
		button.setOnAction(action);
		button.setPrefSize(width, UIConstants.BUTTON_HEIGHT);
		button.getStyleClass().addAll(CSS_CLASS_BUTTON, cssClass);
		return button;
	}
}
