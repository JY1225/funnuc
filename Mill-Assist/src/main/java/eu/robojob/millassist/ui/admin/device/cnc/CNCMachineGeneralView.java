package eu.robojob.millassist.ui.admin.device.cnc;

import java.util.Set;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import eu.robojob.millassist.external.device.processing.cnc.AbstractCNCMachine;
import eu.robojob.millassist.external.device.processing.cnc.AbstractCNCMachine.WayOfOperating;
import eu.robojob.millassist.external.device.processing.cnc.milling.CNCMillingMachine;
import eu.robojob.millassist.ui.controls.FullTextField;
import eu.robojob.millassist.ui.controls.IntegerTextField;
import eu.robojob.millassist.ui.controls.NumericTextField;
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
	private Label lblNameWA1;
	private FullTextField fulltxtNameWA1;
	private Label lblUserFrameWA1;
	private ComboBox<String> cbbUserFrameWA1;
	private Label lblWayOfOperating;
	private RadioButton rbbWayOfOperatingStartStop;
	private RadioButton rbbWayOfOperatingMCodes;
	private ToggleGroup tgWayOfOperating;
	private Label lblClampingLengthR;
	private NumericTextField numTxtClampingLengthR;
	private Label lblClampingWidthR;
	private NumericTextField numTxtClampingWidthR;
	private Region spacer;
		
	private ObservableList<String> userFrameNames;
	
	private static final String NAME = "CNCMachineGeneralView.name";
	private static final String IP = "CNCMachineGeneralView.ipAddress";
	private static final String PORT = "CNCMachineGeneralView.port";
	private static final String USERFRAME = "CNCMachineGeneralView.userFrame";
	private static final String STATUS = "CNCMachineGeneralView.status";
	private static final String WA1 = "CNCMachineGeneralView.wa1";
	
	private static final String WAY_OF_OPERATING = "CNCMachineGeneralView.wayOfOperating";
	private static final String START_STOP = "CNCMachineGeneralView.startStop";
	private static final String M_CODES = "CNCMachineGeneralView.mCodes";
	private static final String STATUS_CONNECTED = "CNCMachineGeneralView.statusConnected";
	private static final String STATUS_DISCONNECTED = "CNCMachineGeneralView.statusDisconnected";
	private static final String CLAMPING_LENGTH_R = "CNCMachineGeneralView.clampingLengthR";
	private static final String CLAMPING_WIDTH_R = "CNCMachineGeneralView.clampingWidthR";
	
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
		
		lblNameWA1 = new Label(Translator.getTranslation(WA1));
		fulltxtNameWA1 = new FullTextField(100);
		lblUserFrameWA1 = new Label(Translator.getTranslation(USERFRAME));
		cbbUserFrameWA1 = new ComboBox<String>();
		cbbUserFrameWA1.setPrefSize(UIConstants.COMBO_WIDTH, UIConstants.COMBO_HEIGHT);
		cbbUserFrameWA1.setItems(userFrameNames);
		
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
		VBox vboxRadioButtonsWayOfOperating = new VBox();
		vboxRadioButtonsWayOfOperating.setSpacing(10);
		vboxRadioButtonsWayOfOperating.getChildren().addAll(rbbWayOfOperatingStartStop, rbbWayOfOperatingMCodes);
				
		
		lblClampingLengthR = new Label(Translator.getTranslation(CLAMPING_LENGTH_R));
		numTxtClampingLengthR = new NumericTextField(5);
		lblClampingWidthR = new Label(Translator.getTranslation(CLAMPING_WIDTH_R));
		numTxtClampingWidthR = new NumericTextField(5);
		
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
		add(lblNameWA1, column++, row);
		add(fulltxtNameWA1, column++, row, 4, 1);
		column = 0; row++;
		add(lblUserFrameWA1, column++, row);
		add(cbbUserFrameWA1, column++, row, 4, 1);
		column = 0; row++;
		add(lblWayOfOperating, column++, row);
		add(vboxRadioButtonsWayOfOperating, column++, row, 4, 1);
		column = 0; row++;
		add(lblClampingLengthR, column++, row);
		add(numTxtClampingLengthR, column++, row);
		column++;
		add(lblClampingWidthR, column++, row);
		add(numTxtClampingWidthR, column++, row);
	}
	
	public void refresh(final Set<String> userFrameNames, final AbstractCNCMachine cncMachine) {
		this.userFrameNames.clear();
		this.userFrameNames.addAll(userFrameNames);
		fulltxtName.setText(cncMachine.getName());
		fulltxtIp.setText(((CNCMillingMachine) cncMachine).getCNCMachineSocketCommunication().getExternalCommunicationThread().getSocketConnection().getIpAddress());
		itxtPort.setText(((CNCMillingMachine) cncMachine).getCNCMachineSocketCommunication().getExternalCommunicationThread().getSocketConnection().getPortNumber() + "");
		refreshStatus(cncMachine);
		fulltxtNameWA1.setText(cncMachine.getWorkAreas().get(0).getName());
		cbbUserFrameWA1.valueProperty().set(cncMachine.getWorkAreas().get(0).getUserFrame().getName());
		numTxtClampingLengthR.setText(cncMachine.getClampingLengthR() + "");
		numTxtClampingWidthR.setText(cncMachine.getClampingWidthR() + "");
		if (cncMachine.getWayOfOperating() == WayOfOperating.START_STOP) {
			rbbWayOfOperatingStartStop.selectedProperty().set(true);
			presenter.setWayOfOperating(WayOfOperating.START_STOP);
		} else if (cncMachine.getWayOfOperating() == WayOfOperating.M_CODES) {
			rbbWayOfOperatingMCodes.selectedProperty().set(true);
			presenter.setWayOfOperating(WayOfOperating.M_CODES);
		} else {
			throw new IllegalStateException("Unkown way of operating: " + cncMachine.getWayOfOperating());
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
		fulltxtNameWA1.setFocusListener(listener);
		fulltxtName.setFocusListener(listener);
		fulltxtIp.setFocusListener(listener);
		itxtPort.setFocusListener(listener);
		numTxtClampingLengthR.setFocusListener(listener);
		numTxtClampingWidthR.setFocusListener(listener);
	}
	
	public String getName() {
		return fulltxtName.getText();
	}
	
	public String getWA1() {
		return fulltxtNameWA1.getText();
	}
	
	public String getUserFrameName() {
		return cbbUserFrameWA1.getValue();
	}
	
	public String getIp() {
		return fulltxtIp.getText();
	}
	
	public int getPort() {
		return Integer.parseInt(itxtPort.getText());
	}
	
	public float getLengthR() {
		return Float.parseFloat(numTxtClampingLengthR.getText());
	}
	
	public float getWidthR() {
		return Float.parseFloat(numTxtClampingWidthR.getText());
	}
	
	public WayOfOperating getWayOfOperating() {
		if (rbbWayOfOperatingMCodes.selectedProperty().getValue()) {
			return WayOfOperating.M_CODES;
		} else {
			return WayOfOperating.START_STOP;
		}
	}
}
