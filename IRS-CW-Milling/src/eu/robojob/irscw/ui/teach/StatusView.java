package eu.robojob.irscw.ui.teach;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;
import eu.robojob.irscw.util.Translator;
import eu.robojob.irscw.util.UIConstants;

public class StatusView extends VBox {

	public enum Status {
		OK, ERROR, WARNING
	}
	
	private StatusPresenter presenter;
	
	private SVGPath infoBgPath;
	private SVGPath infoPath;
	private SVGPath errorBgPath;
	private SVGPath errorPath;
	private SVGPath alarmBgPath;
	private SVGPath alarmPath;
	private Label lblInfoTitle;
	private Label lblInfoMessage;
	private Label lblErrorTitle;
	private Label lblErrorMessage;
	private Label lblAlarmTitle;
	private Label lblAlarmMessage;
	private VBox vBoxMessages;
	private VBox vBoxInfo;
	private VBox vBoxError;
	private VBox vBoxAlarm;
	private Button btnCancel;
	
	private static final double BTN_WIDTH = UIConstants.BUTTON_HEIGHT * 3.5;
	private static final double BTN_HEIGHT = 40;
	private static final int LBL_WIDTH = 550;
	private static final int MESSAGES_HEIGHT = 220;
	
	private static final String STOP = "StatusView.stop";
	
	private static final String CIRCLE_ICON = "M 12.5,3e-7 C 5.5964408,3e-7 0,5.5964411 0,12.5 0,19.40356 5.5964408,25 12.5,25 19.403559,25 25,19.40356 25,12.5 25,5.5964411 19.403559,3e-7 12.5,3e-7 z";
	private static final String TRIANGLE_ICON = "M 12.5,1.03125 C 11.993062,1.0311198 11.509776,1.3702678 11.125,2.0625 L 0.3125,21.46875 C -0.45731218,22.853735 0.22861858,24 1.8125,24 l 21.375,0 c 1.584142,0 2.268771,-1.145744 1.5,-2.53125 L 13.90625,2.0625 C 13.521995,1.3697471 13.006938,1.0313802 12.5,1.03125 z";
	private static final String WARNING_ICON = "m 10.9375,7.15625 0,2.59375 0.625,6.96875 1.875,0 0.625,-6.96875 0,-2.59375 z m 0.125,11.15625 0,2.875 2.875,0 0,-2.875 z";
	private static final String INFO_ICON = "m 14.6875,3.0625 c -0.41375,0 -0.7865,0.16475 -1.125,0.53125 -0.33775,0.3665 -0.5,0.802 -0.5,1.28125 0,0.3855 0.09625,0.6855 0.3125,0.90625 0.21625,0.221 0.51475,0.34375 0.84375,0.34375 0.40375,0 0.747501,-0.20525 1.0625,-0.5625 0.315,-0.357 0.4685,-0.78625 0.46875,-1.3125 0,-0.38525 -0.09325,-0.66825 -0.28125,-0.875 -0.188,-0.20675 -0.452251,-0.3125 -0.78125,-0.3125 z m -0.875,5.5 c -0.7305,0 -2.2405,0.855251 -4.5625,2.59375 l 0.28125,0.5625 c 1.1935,-0.8175 1.949,-1.21875 2.25,-1.21875 0.2725,0 0.40625,0.17425 0.40625,0.53125 0,0.74275 -0.486,2.935 -1.46875,6.5625 -0.49825,1.8515 -0.75,3.07675 -0.75,3.6875 0,0.423 0.06675,0.74325 0.25,0.96875 0.183,0.22575 0.40225,0.34375 0.65625,0.34375 0.31,0 0.64225,-0.106 1,-0.3125 0.87425,-0.48875 2.000001,-1.26225 3.34375,-2.34375 L 14.9375,19.375 c -1.17525,0.84625 -1.94275,1.28125 -2.28125,1.28125 -0.2915,0 -0.4375,-0.18025 -0.4375,-0.5 0,-0.88325 0.55525,-3.38675 1.65625,-7.53125 0.43225,-1.626 0.65625,-2.7205 0.65625,-3.3125 0,-0.24425 -0.066,-0.43575 -0.1875,-0.5625 C 14.22175,8.623 14.037,8.5625 13.8125,8.5625 z";
	private static final String ERROR_ICON = "M 8.78125,6.3125 6.3125,8.78125 10.03125,12.5 6.3125,16.21875 8.78125,18.6875 12.5,14.96875 16.21875,18.6875 18.6875,16.21875 14.96875,12.5 18.6875,8.78125 16.21875,6.3125 12.5,10.03125 z";
			
	private static final String CSS_CLASS_INFO_BG_ICON = "info-bg-icon";
	private static final String CSS_CLASS_INFO_ICON = "info-icon";
	private static final String CSS_CLASS_WARNING_ICON = "warning-icon";
	private static final String CSS_CLASS_WARNING_BG_ICON = "warning-bg-icon";
	private static final String CSS_CLASS_ERROR_ICON = "error-icon";
	private static final String CSS_CLASS_ERROR_BG_ICON = "error-bg-icon";
	private static final String CSS_CLASS_TEACH_BUTTON = "teach-btn";
	private static final String CSS_CLASS_TEACH_BUTTON_TEXT = "teach-btn-text";
	
	private static final String STATUS_TITLE = "StatusView.statusTitle";
	private static final String ERROR_TITLE = "StatusView.errorTitle";
	private static final String ALARM_TITLE = "StatusView.alarmTitle";
	
	public StatusView() {
		build();
	}
	
	public void setPresenter(final StatusPresenter presenter) {
		this.presenter = presenter;
	}
	
	private void build() {
		
		this.setFillWidth(true);
		this.setPrefSize(LBL_WIDTH, 300);
		this.setAlignment(Pos.CENTER);
		
		infoBgPath = new SVGPath();
		infoBgPath.setContent(CIRCLE_ICON);
		infoBgPath.getStyleClass().add(CSS_CLASS_INFO_BG_ICON);
		infoPath = new SVGPath();
		infoPath.setContent(INFO_ICON);
		infoPath.getStyleClass().add(CSS_CLASS_INFO_ICON);
		Pane infoIconPane = new Pane();
		infoIconPane.getChildren().addAll(infoBgPath, infoPath);
		HBox.setMargin(infoIconPane, new Insets(0, 10, 0, 0));
		lblInfoTitle = new Label();
		lblInfoTitle.getStyleClass().add(TeachView.CSS_CLASS_INFO_MESSAGE_TITLE);
		lblInfoTitle.setText(Translator.getTranslation(STATUS_TITLE));
		lblInfoMessage = new Label();
		lblInfoMessage.setPrefWidth(LBL_WIDTH);
		lblInfoMessage.setWrapText(true);
		lblInfoMessage.getStyleClass().add(TeachView.CSS_CLASS_TEACH_MESSAGE);
		HBox hBoxInfo = new HBox();
		hBoxInfo.getChildren().addAll(infoIconPane, lblInfoTitle);
		vBoxInfo =  new VBox();
		vBoxInfo.getChildren().addAll(hBoxInfo, lblInfoMessage);
		VBox.setMargin(vBoxInfo, new Insets(0, 0, 20, 0));
		
		errorBgPath = new SVGPath();
		errorBgPath.setContent(CIRCLE_ICON);
		errorBgPath.getStyleClass().add(CSS_CLASS_ERROR_BG_ICON);
		errorPath = new SVGPath();
		errorPath.setContent(ERROR_ICON);
		errorPath.getStyleClass().add(CSS_CLASS_ERROR_ICON);
		Pane errorIconPane = new Pane();
		errorIconPane.getChildren().addAll(errorBgPath, errorPath);
		HBox.setMargin(errorIconPane, new Insets(0, 10, 0, 0));
		lblErrorTitle = new Label();
		lblErrorTitle.getStyleClass().add(TeachView.CSS_CLASS_INFO_MESSAGE_TITLE);
		lblErrorTitle.setText(Translator.getTranslation(ERROR_TITLE));
		lblErrorMessage = new Label();
		lblErrorMessage.setPrefWidth(LBL_WIDTH);
		lblErrorMessage.setWrapText(true);
		lblErrorMessage.getStyleClass().add(TeachView.CSS_CLASS_TEACH_MESSAGE);
		HBox hBoxError = new HBox();
		hBoxError.getChildren().addAll(errorIconPane, lblErrorTitle);
		vBoxError = new VBox();
		vBoxError.getChildren().addAll(hBoxError, lblErrorMessage);
		VBox.setMargin(vBoxError, new Insets(0, 0, 20, 0));
		
		alarmBgPath = new SVGPath();
		alarmBgPath.setContent(TRIANGLE_ICON);
		alarmBgPath.getStyleClass().add(CSS_CLASS_WARNING_BG_ICON);
		alarmPath = new SVGPath();
		alarmPath.setContent(WARNING_ICON);
		alarmPath.getStyleClass().add(CSS_CLASS_WARNING_ICON);
		Pane alarmIconPane = new Pane();
		alarmIconPane.getChildren().addAll(alarmBgPath, alarmPath);
		HBox.setMargin(alarmIconPane, new Insets(0, 10, 0, 0));
		lblAlarmTitle = new Label();
		lblAlarmTitle.getStyleClass().add(TeachView.CSS_CLASS_INFO_MESSAGE_TITLE);
		lblAlarmTitle.setText(Translator.getTranslation(ALARM_TITLE));
		lblAlarmMessage = new Label();
		lblAlarmMessage.setPrefWidth(LBL_WIDTH);
		lblAlarmMessage.setWrapText(true);
		lblAlarmMessage.getStyleClass().add(TeachView.CSS_CLASS_TEACH_MESSAGE);
		HBox hBoxAlarm = new HBox();
		hBoxAlarm.getChildren().addAll(alarmIconPane, lblAlarmTitle);
		vBoxAlarm = new VBox();
		vBoxAlarm.getChildren().addAll(hBoxAlarm, lblAlarmMessage);
		VBox.setMargin(vBoxAlarm, new Insets(0, 0, 20, 0));
		
		btnCancel = new Button();
		btnCancel.setPrefSize(BTN_WIDTH, BTN_HEIGHT);
		Text txtRestart = new Text(Translator.getTranslation(STOP));
		txtRestart.getStyleClass().add(CSS_CLASS_TEACH_BUTTON_TEXT);
		btnCancel.setGraphic(txtRestart);
		btnCancel.getStyleClass().add(CSS_CLASS_TEACH_BUTTON);
		btnCancel.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent arg0) {
				presenter.stopTeaching();
			}
		});
		
		vBoxMessages = new VBox();
		vBoxMessages.setPrefHeight(MESSAGES_HEIGHT);
		vBoxMessages.setAlignment(Pos.CENTER);
		
		vBoxMessages.getChildren().add(vBoxInfo);
		vBoxMessages.getChildren().add(vBoxError);
		vBoxMessages.getChildren().add(vBoxAlarm);
		getChildren().add(vBoxMessages);
		getChildren().add(btnCancel);
		
		setErrorMessage(null);
		setAlarmMessage(null);
	}
	
	public void setInfoMessage(final String message) {
		if (message == null) {
			vBoxInfo.setVisible(false);
			vBoxInfo.setManaged(false);
		} else {
			vBoxInfo.setVisible(true);
			vBoxInfo.setManaged(true);
			lblInfoMessage.setText(message);
		}
	}
	
	public void setAlarmMessage(final String message) {
		if (message == null) {
			vBoxAlarm.setVisible(false);
			vBoxAlarm.setManaged(false);
		} else {
			vBoxAlarm.setVisible(true);
			vBoxAlarm.setManaged(true);
			lblAlarmMessage.setText(message);
		}
	}
	
	public void setErrorMessage(final String message) {
		if (message == null) {
			vBoxError.setVisible(false);
			vBoxError.setManaged(false);
		} else {
			vBoxError.setVisible(true);
			vBoxError.setManaged(true);
			lblErrorMessage.setText(message);
		}
	}
	
	public void setZRest(final double zrest) {
	}
}
