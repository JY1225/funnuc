package eu.robojob.millassist.ui.alarms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;
import eu.robojob.millassist.external.device.AbstractDevice;
import eu.robojob.millassist.ui.general.PopUpView;
import eu.robojob.millassist.util.Translator;
import eu.robojob.millassist.util.UIConstants;

public class AlarmsPopUpView extends PopUpView<AlarmsPopUpPresenter> {

	private static final int WIDTH = 300;
	private static final int BUTTON_HEIGHT = UIConstants.BUTTON_HEIGHT + 5;
	private static final int MAX_HEIGHT = 400;

	private static final String NO_ALARMS = "AlarmsPopUpView.noAlarms";
	private static final String RESET = "Reset";
	private static final String NO_ALARMS_ICON = "M 25 0 L 8.5625 12.78125 L 0 8.25 L 8.5625 19.25 L 25 0 z";
	private static final String CSS_CLASS_OK_ICON = "icon-ok";
	private static final int MSG_MIN_HEIGHT = 45;
	private static final int ICON_PADDING = 15;
	private static final int ICON_WIDTH = 25;
	private static final String TRIANGLE_ICON = "M 12.5,1.03125 C 11.993062,1.0311198 11.509776,1.3702678 11.125,2.0625 L 0.3125,21.46875 C -0.45731218,22.853735 0.22861858,24 1.8125,24 l 21.375,0 c 1.584142,0 2.268771,-1.145744 1.5,-2.53125 L 13.90625,2.0625 C 13.521995,1.3697471 13.006938,1.0313802 12.5,1.03125 z";
	private static final String WARNING_ICON = "m 10.9375,7.15625 0,2.59375 0.625,6.96875 1.875,0 0.625,-6.96875 0,-2.59375 z m 0.125,11.15625 0,2.875 2.875,0 0,-2.875 z";
	protected static final String CSS_CLASS_WARNING_ICON = "warning-icon";
	protected static final String CSS_CLASS_WARNING_BG_ICON = "warning-bg-icon";
	protected static final String CSS_CLASS_INFO_MESSAGE = "info-msg";
	protected static final String CSS_CLASS_STATUS_MESSAGE = "status-msg";
	private static final String CSS_CLASS_ALARM_MSG = "alarm-popup-msg";
	private static final String CSS_CLASS_POPUP_BUTTON = "pop-up-btn";
	private static final String CSS_CLASS_POPUP_BUTTON_BOTTOM = "pop-up-btn-bottom";
	private static final String CSS_CLASS_SCROLLPANE = "scroll-pane-alarms";
	private SVGPath noAlarmsIconPath;
	private Label noAlarmsLabel;
	private HBox hboxNoAlarms;
	
	private VBox vboxChildren;
	private VBox vboxAlarmMsgs;
	private VBox vboxResetButtons;
	private ScrollPane spAlarmMsgs;
	
	private Map<AbstractDevice, Button> resetButtons;
	
	public AlarmsPopUpView() {
		super(0, 0, WIDTH, 0);
		this.setPrefHeight(USE_COMPUTED_SIZE);
		//this.setMaxHeight(USE_COMPUTED_SIZE);
		this.getStyleClass().add("alarms-popup");
		
		this.setAlignment(Pos.CENTER_LEFT);
		
		hboxNoAlarms = new HBox();
		hboxNoAlarms.setPrefHeight(55);
		noAlarmsIconPath = new SVGPath();
		noAlarmsIconPath.setContent(NO_ALARMS_ICON);
		noAlarmsIconPath.getStyleClass().add(CSS_CLASS_OK_ICON);
		noAlarmsLabel = new Label(Translator.getTranslation(NO_ALARMS));
		noAlarmsLabel.getStyleClass().add(CSS_CLASS_ALARM_MSG);
		hboxNoAlarms.getChildren().addAll(noAlarmsIconPath, noAlarmsLabel);
		hboxNoAlarms.setPadding(new Insets(15, 15, 15, 15));
		hboxNoAlarms.setSpacing(15);
	
		this.resetButtons = new HashMap<AbstractDevice, Button>();
		
		this.vboxChildren = new VBox();
		this.spAlarmMsgs = new ScrollPane();
		spAlarmMsgs.setHbarPolicy(ScrollBarPolicy.NEVER);
		spAlarmMsgs.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
		spAlarmMsgs.getStyleClass().add(CSS_CLASS_SCROLLPANE);
		spAlarmMsgs.setPannable(true);
		spAlarmMsgs.setFitToWidth(true);
		spAlarmMsgs.setMaxHeight(MAX_HEIGHT);
		this.vboxAlarmMsgs = new VBox();
		this.vboxResetButtons = new VBox();
		vboxAlarmMsgs.setPadding(new Insets(10, 0, 0, 0));
		spAlarmMsgs.setContent(vboxAlarmMsgs);
		this.getChildren().add(vboxChildren);
		vboxChildren.getChildren().add(vboxResetButtons);
		vboxChildren.getChildren().add(spAlarmMsgs);
		VBox.setMargin(spAlarmMsgs, new Insets(0, 3, 3, 3));
		vboxChildren.getChildren().add(hboxNoAlarms);
	}

	public void updateAlarms(final Set<String> alarmMessages) {
		List<String> sortedMessages = new ArrayList<String>(alarmMessages);
		Collections.sort(sortedMessages);
		vboxAlarmMsgs.getChildren().clear();
		if (alarmMessages.size() == 0) {
			hboxNoAlarms.setVisible(true);
			hboxNoAlarms.setManaged(true);
			vboxAlarmMsgs.setVisible(false);
			vboxAlarmMsgs.setManaged(false);
		} else {
			for (String alarmMsg : sortedMessages) {
				vboxAlarmMsgs.getChildren().add(getAlarmHBox(alarmMsg));
			}
			hboxNoAlarms.setVisible(false);
			hboxNoAlarms.setManaged(false);
			vboxAlarmMsgs.setVisible(true);
			vboxAlarmMsgs.setManaged(true);
		}
		if (alarmMessages.size() * MSG_MIN_HEIGHT + 20 > MAX_HEIGHT) {
			spAlarmMsgs.setPrefHeight(MAX_HEIGHT);
			spAlarmMsgs.setMinHeight(MAX_HEIGHT);
		} else {
			spAlarmMsgs.setPrefHeight(alarmMessages.size() * MSG_MIN_HEIGHT + 20);
			spAlarmMsgs.setMinHeight(alarmMessages.size() * MSG_MIN_HEIGHT + 20);
		}
	}
	
	public void updateResetButtons(final Set<AbstractDevice> devices) {
		resetButtons.clear();
		vboxResetButtons.getChildren().clear();
		int counter = 0;
		for (final AbstractDevice device : devices) {
			Button btnReset = new Button();
			btnReset.setGraphic(new Text(RESET + " " + device.getName()));
			btnReset.setPrefSize(WIDTH, BUTTON_HEIGHT);
			btnReset.setMinSize(WIDTH, BUTTON_HEIGHT);
			btnReset.getStyleClass().add(CSS_CLASS_POPUP_BUTTON);
			if (counter == 0) {
				btnReset.getStyleClass().add("pop-up-first");
			}
			if (counter == devices.size() - 1) {
				btnReset.getStyleClass().add(CSS_CLASS_POPUP_BUTTON_BOTTOM);
			}
			btnReset.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(final ActionEvent arg0) {
					getPresenter().resetDevice(device);
				}
			});
			if (!device.isConnected()) {
				btnReset.setDisable(true);
			}
			resetButtons.put(device, btnReset);
			vboxResetButtons.getChildren().add(btnReset);
			counter++;
		}
	}
	
	public void setDeviceConnected(final AbstractDevice device, final boolean connected) {
		if (resetButtons.containsKey(device)) {
			resetButtons.get(device).setDisable(!connected);
		}
	}
	
	private HBox getAlarmHBox(final String message) {
		// icon
		SVGPath alarmBgPath = new SVGPath();
		alarmBgPath.setContent(TRIANGLE_ICON);
		alarmBgPath.getStyleClass().add(CSS_CLASS_WARNING_BG_ICON);
		SVGPath alarmPath = new SVGPath();
		alarmPath.setContent(WARNING_ICON);
		alarmPath.getStyleClass().add(CSS_CLASS_WARNING_ICON);
		Pane alarmIconPane = new Pane();
		alarmIconPane.getChildren().addAll(alarmBgPath, alarmPath);
		// label
		Label lblAlarmMessage = new Label();
		lblAlarmMessage.setWrapText(true);
		lblAlarmMessage.getStyleClass().addAll(CSS_CLASS_INFO_MESSAGE, CSS_CLASS_STATUS_MESSAGE);
		// hbox
		HBox hBoxAlarm = new HBox();
		hBoxAlarm.setMinHeight(MSG_MIN_HEIGHT);
		HBox.setMargin(alarmIconPane, new Insets(0, ICON_PADDING, 0, 0));
		hBoxAlarm.getChildren().addAll(alarmIconPane, lblAlarmMessage);
		hBoxAlarm.setAlignment(Pos.CENTER_LEFT);
		hBoxAlarm.setPadding(new Insets(5, 10, 5, 15));
		lblAlarmMessage.setMaxWidth(WIDTH - ICON_WIDTH - ICON_PADDING);
		lblAlarmMessage.getStyleClass().add(CSS_CLASS_ALARM_MSG);
		lblAlarmMessage.setText(message);
		HBox.setMargin(lblAlarmMessage, new Insets(0, 0, 10, 0));
		hBoxAlarm.setPrefWidth(WIDTH);
		hBoxAlarm.setMaxWidth(WIDTH);
		return hBoxAlarm;
	}
}
