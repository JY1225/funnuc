package eu.robojob.millassist.ui.admin.device.cnc;

import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import eu.robojob.millassist.ui.controls.IntegerTextField;
import eu.robojob.millassist.ui.controls.TextInputControlListener;
import eu.robojob.millassist.util.Translator;

public class CNCMachinePartsOperationView extends GridPane {

	private ToggleGroup tgWayOfInterfacing;
	
	private Label lblAmountOfDoors;
	private IntegerTextField itxtAmountOfDoors;
	private Label lblWayOfInterfacing;
	private RadioButton rbbDirect;
	private RadioButton rbbRobotInterface;
	private Label lblCombinedOpenCloseDoorLEDs;
	private CheckBox cbbCombinedOpenCloseDoorLEDs;
	private Label lblOneFoodPedalForChuck;
	private CheckBox cbbOneFootPedalForChuck;
	private Label lblNegativeActiveNCAlarm;
	private CheckBox cbbNegativeActiveNCAlarm;
	private Label lblUseRobotServiceRequest;
	private CheckBox cbbUseRobotServiceRequest;
	
	private static final String AMOUNT_OF_DOORS = "CNCMachinePartsOperationView.amountOfDoors";
	private static final String WAY_OF_INTERFACING = "CNCMachinePartsOperationView.wayOfInterfacing";
	private static final String DIRECT = "CNCMachinePartsOperationView.direct";
	private static final String ROBOT_INTERFACE = "CNCMachinePartsOperationView.robotInterface";
	private static final String COMBINED_OPEN_CLOSE_DOOR_LEDS = "CNCMachinePartsOperationView.combinedOpenCloseDoorLEDs";
	private static final String ONE_FOOT_PEDAL_FOR_CHUCK = "CNCMachinePartsOperationView.oneFootPedalForChuck";
	private static final String NEGATIVE_ACTIVE_NC_ALARM = "CNCMachinePartsOperationView.negativeActiveNCAlarm";
	private static final String USE_ROBOT_SERVICE_REQUEST = "CNCMachinePartsOperationView.useRobotServiceRequest";
	
	public CNCMachinePartsOperationView() {
		build();
	}
	
	private void build() {
		setAlignment(Pos.TOP_CENTER);
		setVgap(15);
		setHgap(15);
		lblAmountOfDoors = new Label(Translator.getTranslation(AMOUNT_OF_DOORS));
		itxtAmountOfDoors = new IntegerTextField(2);
		lblWayOfInterfacing = new Label(Translator.getTranslation(WAY_OF_INTERFACING));
		tgWayOfInterfacing = new ToggleGroup();
		rbbDirect = new RadioButton(Translator.getTranslation(DIRECT));
		rbbDirect.setToggleGroup(tgWayOfInterfacing);
		rbbRobotInterface = new RadioButton(Translator.getTranslation(ROBOT_INTERFACE));
		rbbRobotInterface.setToggleGroup(tgWayOfInterfacing);
		VBox vboxRadioButtons = new VBox();
		vboxRadioButtons.getChildren().addAll(rbbDirect, rbbRobotInterface);
		vboxRadioButtons.setSpacing(10);
		lblCombinedOpenCloseDoorLEDs = new Label(Translator.getTranslation(COMBINED_OPEN_CLOSE_DOOR_LEDS));
		cbbCombinedOpenCloseDoorLEDs = new CheckBox();
		lblOneFoodPedalForChuck = new Label(Translator.getTranslation(ONE_FOOT_PEDAL_FOR_CHUCK));
		cbbOneFootPedalForChuck = new CheckBox();
		lblNegativeActiveNCAlarm = new Label(Translator.getTranslation(NEGATIVE_ACTIVE_NC_ALARM));
		cbbNegativeActiveNCAlarm = new CheckBox();
		lblUseRobotServiceRequest = new Label(Translator.getTranslation(USE_ROBOT_SERVICE_REQUEST));
		cbbUseRobotServiceRequest = new CheckBox();
		int column = 0;
		int row = 0;
		add(lblAmountOfDoors, column++, row);
		add(itxtAmountOfDoors, column++, row);
		column = 0; row++;
		add(lblWayOfInterfacing, column++, row);
		add(vboxRadioButtons, column++, row);
		column = 0; row++;
		add(lblCombinedOpenCloseDoorLEDs, column++, row, 2, 1);
		column++;
		add(cbbCombinedOpenCloseDoorLEDs, column++, row);
		column = 0; row++;
		add(lblOneFoodPedalForChuck, column++, row, 2, 1);
		column++;
		add(cbbOneFootPedalForChuck, column++, row);
		column = 0; row++;
		add(lblNegativeActiveNCAlarm, column++, row, 2, 1);
		column++;
		add(cbbNegativeActiveNCAlarm, column++, row);
		column = 0; row++;
		add(lblUseRobotServiceRequest, column++, row, 2, 1);
		column++;
		add(cbbUseRobotServiceRequest, column++, row);
	}
	
	public void setTextFieldListener(final TextInputControlListener listener) {
		itxtAmountOfDoors.setFocusListener(listener);
	}

}
