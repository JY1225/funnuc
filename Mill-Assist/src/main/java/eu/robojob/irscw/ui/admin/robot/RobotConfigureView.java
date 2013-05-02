package eu.robojob.irscw.ui.admin.robot;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import eu.robojob.irscw.external.robot.AbstractRobot;
import eu.robojob.irscw.external.robot.fanuc.FanucRobot;
import eu.robojob.irscw.ui.controls.FullTextField;
import eu.robojob.irscw.ui.controls.IntegerTextField;
import eu.robojob.irscw.ui.controls.TextInputControlListener;
import eu.robojob.irscw.ui.general.AbstractFormView;
import eu.robojob.irscw.util.Translator;
import eu.robojob.irscw.util.UIConstants;

public class RobotConfigureView extends AbstractFormView<RobotConfigurePresenter> {

	private Label lblName;
	private FullTextField fulltxtName;
	private Label lblIpAddress;
	private FullTextField fulltxtIpAddress;
	private Label lblPort;
	private IntegerTextField itxtPort;
	private Label lblStatus;
	private Label lblStatusValue;
	private Label lblGripperHeads;
	private CheckBox cbGripperHeadA;
	private CheckBox cbGripperHeadB;
	private CheckBox cbGripperHeadC;
	private CheckBox cbGripperHeadD;
	private Region spacer;
	private Button btnSave;
	
	private AbstractRobot robot;
	
	private static final String NAME = "RobotConfigureView.name";
	private static final String IP = "RobotConfigureView.ip";
	private static final String PORT = "RobotConfigureView.port";
	private static final String STATUS = "RobotConfigureView.status";
	private static final String GRIPPER_HEADS = "RobotConfigureView.gripperHeads";
	private static final String SAVE = "RobotConfigureView.save";
	private static final String STATUS_CONNECTED = "RobotConfigureView.statusConnected";
	private static final String STATUS_DISCONNECTED = "RobotConfigureView.statusDisconnected";
	
	private static final int HGAP = 15;
	private static final int VGAP = 15;
	private static final String SAVE_PATH = "M 5.40625 0 L 5.40625 7.25 L 0 7.25 L 7.1875 14.40625 L 14.3125 7.25 L 9 7.25 L 9 0 L 5.40625 0 z M 7.1875 14.40625 L 0 14.40625 L 0 18 L 14.3125 18 L 14.3125 14.40625 L 7.1875 14.40625 z";
	
	public RobotConfigureView() {
		super();
		build();
	}
	
	@Override
	protected void build() {
		setHgap(HGAP);
		setVgap(VGAP);
		setAlignment(Pos.TOP_CENTER);
		setPadding(new Insets(25, 0, 0, 0));

		getChildren().clear();
		
		int column = 0;
		int row = 0;
		lblName = new Label(Translator.getTranslation(NAME));
		add(lblName, column++, row);
		fulltxtName = new FullTextField(100);
		fulltxtName.setPrefHeight(UIConstants.TEXT_FIELD_HEIGHT);
		add(fulltxtName, column++, row);
		spacer = new Region();
		spacer.setPrefWidth(25);
		add(spacer, column++, row);
		btnSave = createButton(SAVE_PATH, "form-button", Translator.getTranslation(SAVE), UIConstants.BUTTON_HEIGHT * 3, UIConstants.BUTTON_HEIGHT, new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent arg0) {
				getPresenter().saveData(fulltxtName.getText(), fulltxtIpAddress.getText(), Integer.parseInt(itxtPort.getText()), cbGripperHeadA.selectedProperty().get(), 
						cbGripperHeadB.selectedProperty().get(), cbGripperHeadC.selectedProperty().get(), cbGripperHeadD.selectedProperty().get());
			}
		});
		add(btnSave, column++, row);
		column = 0; 
		row++;
		lblIpAddress = new Label(Translator.getTranslation(IP));
		add(lblIpAddress, column++, row);
		fulltxtIpAddress = new FullTextField(15);
		fulltxtIpAddress.setPrefHeight(UIConstants.TEXT_FIELD_HEIGHT);
		add(fulltxtIpAddress, column++, row);
		column = 0;
		row++;
		lblPort = new Label(Translator.getTranslation(PORT));
		add(lblPort, column++, row);
		itxtPort = new IntegerTextField(5);
		itxtPort.setPrefHeight(UIConstants.TEXT_FIELD_HEIGHT);
		itxtPort.setPrefWidth(UIConstants.TEXT_FIELD_HEIGHT * 2);
		itxtPort.setMinWidth(UIConstants.TEXT_FIELD_HEIGHT * 2);
		itxtPort.setMaxWidth(UIConstants.TEXT_FIELD_HEIGHT * 2);
		add(itxtPort, column++, row);
		column = 0;
		row++;
		lblGripperHeads = new Label(Translator.getTranslation(GRIPPER_HEADS));
		add(lblGripperHeads, column++, row);
		HBox hboxHeads = new HBox();
		cbGripperHeadA = new CheckBox("A");
		cbGripperHeadA.setDisable(true);
		cbGripperHeadB = new CheckBox("B");
		cbGripperHeadB.setDisable(true);
		cbGripperHeadC = new CheckBox("C");
		cbGripperHeadC.setDisable(true);
		cbGripperHeadD = new CheckBox("D");
		cbGripperHeadD.setDisable(true);
		hboxHeads.getChildren().addAll(cbGripperHeadA, cbGripperHeadB, cbGripperHeadC, cbGripperHeadD);
		hboxHeads.setSpacing(20);
		add(hboxHeads, column++, row);
		column = 0;
		row++;
		lblStatus = new Label(Translator.getTranslation(STATUS));
		add(lblStatus, column++, row);
		lblStatusValue = new Label();
		add(lblStatusValue, column++, row);
	}

	@Override
	public void setTextFieldListener(final TextInputControlListener listener) {
		fulltxtName.setFocusListener(listener);
		fulltxtIpAddress.setFocusListener(listener);
		itxtPort.setFocusListener(listener);
		
	}

	@Override
	public void refresh() {
		fulltxtName.setText(robot.getName());
		if (robot instanceof FanucRobot) {
			FanucRobot fRobot = (FanucRobot) robot;
			fulltxtIpAddress.setText(fRobot.getRobotSocketCommunication().getExternalCommunicationThread().getSocketConnection().getIpAddress());
			itxtPort.setText("" + fRobot.getRobotSocketCommunication().getExternalCommunicationThread().getSocketConnection().getPortNumber());
		}
		if (robot.isConnected()) {
			lblStatusValue.setText(Translator.getTranslation(STATUS_CONNECTED));
			//TODO Add version information
		} else {
			lblStatusValue.setText(Translator.getTranslation(STATUS_DISCONNECTED));
		}
		cbGripperHeadA.setSelected((robot.getGripperBody().getGripperHeadByName("A") != null));
		cbGripperHeadB.setSelected((robot.getGripperBody().getGripperHeadByName("B") != null));
		cbGripperHeadC.setSelected((robot.getGripperBody().getGripperHeadByName("C") != null));
		cbGripperHeadD.setSelected((robot.getGripperBody().getGripperHeadByName("D") != null));
	}

	public void setRobot(final AbstractRobot robot) {
		this.robot = robot;
	}
}
