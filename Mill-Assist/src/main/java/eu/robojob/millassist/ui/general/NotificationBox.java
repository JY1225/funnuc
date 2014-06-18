package eu.robojob.millassist.ui.general;

import eu.robojob.millassist.util.Translator;
import eu.robojob.millassist.util.UIConstants;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;

/**
 * This class represents the notification box on top of the possible configuration regions. The NotificationView can never
 * occur on its own; it is always part of an AbstractFormView. 
 * 
 * @author Kristof.Helsen
 *
 */
public class NotificationBox extends HBox {
	
	private Button btnYes;
	private Button btnNo;
	private HBox confirmationBox;
	private Pane iconPane;
	private Text lblAlarmMessage;
	private SVGPath alarmBgPath, alarmPath, alarmOKPath;
	
	private AbstractFormView<?> abstractFormView;
	
	private static final String NO = "NotificationBox.no";
	private static final String YES = "NotificationBox.yes";
	
	private static final int ICON_WIDTH = 20;
	private static final int BUTTON_WIDTH = 60;
	private static final int ALARM_HEIGHT = 45;
	private static final int PADDING_BOTTOM = 10;
	private static final int PADDING_RIGHT = 5;
	private static final int ICON_PADDING = 10;
	
	private static final String TRIANGLE_ICON = "M 12.5,1.03125 C 11.993062,1.0311198 11.509776,1.3702678 11.125,2.0625 L 0.3125,21.46875 C -0.45731218,22.853735 0.22861858,24 1.8125,24 l 21.375,0 c 1.584142,0 2.268771,-1.145744 1.5,-2.53125 L 13.90625,2.0625 C 13.521995,1.3697471 13.006938,1.0313802 12.5,1.03125 z";
	private static final String WARNING_ICON = "m 10.9375,7.15625 0,2.59375 0.625,6.96875 1.875,0 0.625,-6.96875 0,-2.59375 z m 0.125,11.15625 0,2.875 2.875,0 0,-2.875 z";
	private static final String NO_ALARMS_ICON = "M 25 0 L 8.5625 12.78125 L 0 8.25 L 8.5625 19.25 L 25 0 z";
	
	protected static final String CSS_CLASS_INFO_MESSAGE = "info-msg";
	protected static final String CSS_CLASS_STATUS_MESSAGE = "status-msg";
	protected static final String CSS_CLASS_WARNING_ICON = "warning-icon";
	protected static final String CSS_CLASS_WARNING_BG_ICON = "warning-bg-icon";
	private static final String CSS_CLASS_OK_ICON = "icon-ok";
	private static final String CSS_CLASS_INFO_BORDER_BOTTOM = "info-border-bottom";
	private static final String CSS_CLASS_WARNING_CONFIG = "warning-config";
	
	public NotificationBox(AbstractFormView<?> abstractFormView) {
		buildNotificationBox();
		this.abstractFormView = abstractFormView;
	}
	
	private void buildNotificationBox() {
		// icons
		buildWarningIcon();
		buildOKIcon();
		iconPane = new Pane();
		// label
		buildWarningTextLabel();
		// confirmation box - which holds the buttons 
		buildConfirmationHBox();
		//hbox
		setMinHeight(ALARM_HEIGHT);
		setMaxHeight(ALARM_HEIGHT);
		setPrefHeight(ALARM_HEIGHT);

		setMargin(iconPane, new Insets(0, ICON_PADDING, 5, 0));
		setMargin(confirmationBox, new Insets(0, PADDING_RIGHT, 0, 0));
		// show the border line of the notification box
		getStyleClass().add(CSS_CLASS_INFO_BORDER_BOTTOM);
		setAlignment(Pos.CENTER_LEFT);
		setPadding(new Insets(10, 0, PADDING_BOTTOM, 10));
		
		//Add all children: Icon - Label - Confirmation
		getChildren().addAll(iconPane, lblAlarmMessage, confirmationBox);
	}
	
	private void buildConfirmationHBox() {
		confirmationBox = new HBox();
		
		btnNo = AbstractFormView.createButton(Translator.getTranslation(NO), BUTTON_WIDTH, UIConstants.BUTTON_HEIGHT, new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent arg0) {
				abstractFormView.getPresenter().doNoAction();
			}
		});
		
		btnYes = AbstractFormView.createButton(Translator.getTranslation(YES), BUTTON_WIDTH, UIConstants.BUTTON_HEIGHT, new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent arg0) {
				abstractFormView.getPresenter().doYesAction();
				//We have to continue our action (close application, saveAsNew, new process, open process) - depending on which presenter was used
			}
		});
		confirmationBox.getChildren().addAll(btnNo, btnYes);
		confirmationBox.setAlignment(Pos.CENTER_LEFT);
	}
	
	private void buildWarningIcon() {
		alarmBgPath = new SVGPath();
		alarmBgPath.setContent(TRIANGLE_ICON);
		alarmBgPath.getStyleClass().add(CSS_CLASS_WARNING_BG_ICON);
		alarmPath = new SVGPath();
		alarmPath.setContent(WARNING_ICON);
		alarmPath.getStyleClass().add(CSS_CLASS_WARNING_ICON);
	}
	
	private void buildOKIcon() {
		alarmOKPath = new SVGPath();
		alarmOKPath.setContent(NO_ALARMS_ICON);
		alarmOKPath.getStyleClass().add(CSS_CLASS_OK_ICON);
	}
	
	//FIXME - Font should be white
	private void buildWarningTextLabel() {
		lblAlarmMessage = new Text();
		lblAlarmMessage.getStyleClass().addAll(CSS_CLASS_INFO_MESSAGE, CSS_CLASS_STATUS_MESSAGE);
		lblAlarmMessage.setWrappingWidth(450 - ICON_WIDTH - ICON_PADDING);
		lblAlarmMessage.getStyleClass().add(CSS_CLASS_WARNING_CONFIG);
		setHgrow(lblAlarmMessage, Priority.ALWAYS);
	}
	
	private void showCorrectWarningIcon(boolean isWarning) {
		iconPane.getChildren().clear();
		if(isWarning) {
			iconPane.getChildren().addAll(alarmBgPath, alarmPath);
		} else {
			iconPane.getChildren().addAll(alarmOKPath);
		}
	}
	
	private void showConfirmationBox(boolean flag) {
		confirmationBox.setVisible(flag);
		confirmationBox.setManaged(flag);
	}
	
	/**
	 * This methods fills the content of the notification box with the correct icon, text and confirmation buttons if needed
	 * 
	 * @param notification    - String that holds the text to be displayed in the notification
	 * @param isWarning       - Flag to indicate that the notification should be a warning or a confirmation
	 * @param askConfirmation - Flag that allows the user to choose whether to continue the action or to abort (e.g. to save changes)
	 */
	public void showNotification(final String notification, boolean isWarning, boolean askConfirmation) {
		lblAlarmMessage.setText(notification);
		showCorrectWarningIcon(isWarning);
		showConfirmationBox(askConfirmation);
	}
	
}
