package eu.robojob.irscw.ui.admin.device.cnc;

import java.util.Set;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import eu.robojob.irscw.external.device.processing.cnc.AbstractCNCMachine;
import eu.robojob.irscw.external.device.processing.cnc.milling.CNCMillingMachine;
import eu.robojob.irscw.ui.controls.FullTextField;
import eu.robojob.irscw.ui.controls.IntegerTextField;
import eu.robojob.irscw.ui.controls.NumericTextField;
import eu.robojob.irscw.ui.controls.TextInputControlListener;
import eu.robojob.irscw.ui.general.AbstractFormView;
import eu.robojob.irscw.util.Translator;
import eu.robojob.irscw.util.UIConstants;

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
	private Label lblClampingLengthR;
	private NumericTextField numTxtClampingLengthR;
	private Label lblnumTxtClampingWidthR;
	private NumericTextField numTxtClampingWidthR;
	private Button btnSave;
		
	private ObservableList<String> userFrameNames;
	
	private static final String NAME = "CNCMachineGeneralView.name";
	private static final String IP = "CNCMachineGeneralView.ipAddress";
	private static final String PORT = "CNCMachineGeneralView.port";
	private static final String USERFRAME = "CNCMachineGeneralView.userFrame";
	private static final String STATUS = "CNCMachineGeneralView.status";
	private static final String SAVE = "CNCMachineGeneralView.save";
	private static final String WA1 = "CNCMachineGeneralView.wa1";
	
	private static final String STATUS_CONNECTED = "CNCMachineGeneralView.statusConnected";
	private static final String STATUS_DISCONNECTED = "CNCMachineGeneralView.statusDisconnected";
	private static final String CLAMPING_LENGTH_R = "CNCMachineGeneralView.clampingLengthR";
	private static final String CLAMPING_WIDTH_R = "CNCMachineGeneralView.clampingWidthR";
	
	private static final String SAVE_PATH = "M 5.40625 0 L 5.40625 7.25 L 0 7.25 L 7.1875 14.40625 L 14.3125 7.25 L 9 7.25 L 9 0 L 5.40625 0 z M 7.1875 14.40625 L 0 14.40625 L 0 18 L 14.3125 18 L 14.3125 14.40625 L 7.1875 14.40625 z";
	private static final double BTN_HEIGHT = UIConstants.BUTTON_HEIGHT;
	private static final double BTN_WIDTH = BTN_HEIGHT * 3;
	
	public CNCMachineGeneralView() {
		this.userFrameNames = FXCollections.observableArrayList();
		build();
	}
	
	public void setPresenter(final CNCMachineConfigurePresenter presenter) {
		this.presenter = presenter;
	}
	
	public void build() {
		setVgap(15);
		setHgap(15);
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
		
		lblNameWA1 = new Label(Translator.getTranslation(WA1));
		fulltxtNameWA1 = new FullTextField(100);
		lblUserFrameWA1 = new Label(Translator.getTranslation(USERFRAME));
		cbbUserFrameWA1 = new ComboBox<String>();
		cbbUserFrameWA1.setPrefSize(UIConstants.COMBO_WIDTH, UIConstants.COMBO_HEIGHT);
		cbbUserFrameWA1.setItems(userFrameNames);
		
		lblClampingLengthR = new Label(Translator.getTranslation(CLAMPING_LENGTH_R));
		numTxtClampingLengthR = new NumericTextField(5);
		lblnumTxtClampingWidthR = new Label(Translator.getTranslation(CLAMPING_WIDTH_R));
		numTxtClampingWidthR = new NumericTextField(5);
		
		btnSave = AbstractFormView.createButton(SAVE_PATH, "", Translator.getTranslation(SAVE), BTN_WIDTH, BTN_HEIGHT, new EventHandler<ActionEvent>() {	
			@Override
			public void handle(final ActionEvent arg0) {
				presenter.saveData(fulltxtName.getText(), fulltxtIp.getText(), Integer.parseInt(itxtPort.getText()), fulltxtNameWA1.getText(), cbbUserFrameWA1.valueProperty().get(),
						Float.parseFloat(numTxtClampingLengthR.getText()), Float.parseFloat(numTxtClampingWidthR.getText()));
			}
		});
		GridPane.setHalignment(btnSave, HPos.CENTER);
		
		int row = 0;
		int column = 0;
		add(lblName, column++, row);
		add(fulltxtName, column++, row);
		column = 0; row++;
		add(lblIPAddress, column++, row);
		add(fulltxtIp, column++, row);
		column = 0; row++;
		add(lblPort, column++, row);
		add(itxtPort, column++, row);
		column = 0; row++;
		add(lblStatus, column++, row);
		add(lblStatusVal, column++, row);
		column = 0; row++;
		add(lblNameWA1, column++, row);
		add(fulltxtNameWA1, column++, row);
		column = 0; row++;
		add(lblUserFrameWA1, column++, row);
		add(cbbUserFrameWA1, column++, row);
		column = 0; row++;
		add(lblClampingLengthR, column++, row);
		add(numTxtClampingLengthR, column++, row);
		column = 0; row++;
		add(lblnumTxtClampingWidthR, column++, row);
		add(numTxtClampingWidthR, column++, row);
		column = 0; row++;
		add(btnSave, column++, row, 2, 1);
	}
	
	public void refresh(final Set<String> userFrameNames, final AbstractCNCMachine cncMachine) {
		this.userFrameNames.clear();
		this.userFrameNames.addAll(userFrameNames);
		fulltxtName.setText(cncMachine.getName());
		fulltxtIp.setText(((CNCMillingMachine) cncMachine).getCNCMachineSocketCommunication().getExternalCommunicationThread().getSocketConnection().getIpAddress());
		itxtPort.setText(((CNCMillingMachine) cncMachine).getCNCMachineSocketCommunication().getExternalCommunicationThread().getSocketConnection().getPortNumber() + "");
		if(cncMachine.isConnected()) {
			lblStatusVal.setText(Translator.getTranslation(STATUS_CONNECTED));
		} else {
			lblStatusVal.setText(Translator.getTranslation(STATUS_DISCONNECTED));	
		}
		fulltxtNameWA1.setText(cncMachine.getWorkAreas().get(0).getName());
		cbbUserFrameWA1.valueProperty().set(cncMachine.getWorkAreas().get(0).getUserFrame().getName());
		numTxtClampingLengthR.setText(cncMachine.getClampingLengthR() + "");
		numTxtClampingWidthR.setText(cncMachine.getClampingWidthR() + "");
	}
	
	public void setTextFieldListener(final TextInputControlListener listener) {
		fulltxtNameWA1.setFocusListener(listener);
		fulltxtName.setFocusListener(listener);
		fulltxtIp.setFocusListener(listener);
		itxtPort.setFocusListener(listener);
		numTxtClampingLengthR.setFocusListener(listener);
		numTxtClampingWidthR.setFocusListener(listener);
	}
	
}
