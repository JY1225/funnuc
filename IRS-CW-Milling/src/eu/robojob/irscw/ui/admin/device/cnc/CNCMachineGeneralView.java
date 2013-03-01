package eu.robojob.irscw.ui.admin.device.cnc;

import java.util.Set;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import eu.robojob.irscw.external.device.processing.cnc.AbstractCNCMachine;
import eu.robojob.irscw.external.device.processing.cnc.milling.CNCMillingMachine;
import eu.robojob.irscw.ui.controls.FullTextField;
import eu.robojob.irscw.ui.controls.IntegerTextField;
import eu.robojob.irscw.ui.controls.TextInputControlListener;
import eu.robojob.irscw.util.Translator;
import eu.robojob.irscw.util.UIConstants;

public class CNCMachineGeneralView extends GridPane {

	private Label lblName;
	private FullTextField fulltxtName;
	private Label lblIPAddress;
	private FullTextField fulltxtIp;
	private Label lblPort;
	private IntegerTextField itxtPort;
	private Label lblUserFrame;
	private ComboBox<String> cbbUserFrame;
	private Label lblStatus;
	private Label lblStatusVal;
		
	private ObservableList<String> userFrameNames;
	
	private static final String NAME = "CNCMachineGeneralView.name";
	private static final String IP = "CNCMachineGeneralView.ipAddress";
	private static final String PORT = "CNCMachineGeneralView.port";
	private static final String USERFRAME = "CNCMachineGeneralView.userFrame";
	private static final String STATUS = "CNCMachineGeneralView.status";
	
	private static final String STATUS_CONNECTED = "CNCMachineGeneralView.statusConnected";
	private static final String STATUS_DISCONNECTED = "CNCMachineGeneralView.statusDisconnected";
	
	public CNCMachineGeneralView() {
		this.userFrameNames = FXCollections.observableArrayList();
		build();
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
		lblUserFrame = new Label(Translator.getTranslation(USERFRAME));
		cbbUserFrame = new ComboBox<String>();
		cbbUserFrame.setPrefSize(UIConstants.COMBO_WIDTH, UIConstants.COMBO_HEIGHT);
		cbbUserFrame.setItems(userFrameNames);
		lblStatus = new Label(Translator.getTranslation(STATUS));
		lblStatusVal = new Label();
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
		add(lblUserFrame, column++, row);
		add(cbbUserFrame, column++, row);
		column = 0; row++;
		add(lblStatus, column++, row);
		add(lblStatusVal, column++, row);
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
		cbbUserFrame.valueProperty().set(cncMachine.getWorkAreas().get(0).getUserFrame().getName());
	}
	
	public void setTextFieldListener(final TextInputControlListener listener) {
		fulltxtName.setFocusListener(listener);
		fulltxtIp.setFocusListener(listener);
		itxtPort.setFocusListener(listener);
	}
	
}
