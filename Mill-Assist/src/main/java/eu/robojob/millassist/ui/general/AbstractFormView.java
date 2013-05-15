package eu.robojob.millassist.ui.general;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import eu.robojob.millassist.ui.controls.TextInputControlListener;

public abstract class AbstractFormView<T extends AbstractFormPresenter<?, ?>> extends VBox {

	private T presenter;
	private SVGPath alarmBgPath;
	private SVGPath alarmPath;
	private Label lblAlarmMessage;
	private HBox hBoxAlarm;
	private GridPane gpContents;
	
	private static final int ICON_WIDTH = 20;
	private static final int ICON_MARGIN = 6;
	private static final int ALARM_HEIGHT = 50;
	private static final int PADDING_BOTTOM = 10;
	private static final int ICON_PADDING = 10;
	
	private static final String TRIANGLE_ICON = "M 12.5,1.03125 C 11.993062,1.0311198 11.509776,1.3702678 11.125,2.0625 L 0.3125,21.46875 C -0.45731218,22.853735 0.22861858,24 1.8125,24 l 21.375,0 c 1.584142,0 2.268771,-1.145744 1.5,-2.53125 L 13.90625,2.0625 C 13.521995,1.3697471 13.006938,1.0313802 12.5,1.03125 z";
	private static final String WARNING_ICON = "m 10.9375,7.15625 0,2.59375 0.625,6.96875 1.875,0 0.625,-6.96875 0,-2.59375 z m 0.125,11.15625 0,2.875 2.875,0 0,-2.875 z";
	
	private static final String CSS_CLASS_FORM_BUTTON_ICON = "form-button-icon";
	protected static final String CSS_CLASS_FORM_BUTTON_LABEL = "form-button-label";
	private static final String CSS_CLASS_FORM_BUTTON_PANEL = "form-button-panel";
	
	protected static final String CSS_CLASS_FORM_BUTTON = "form-button";
	protected static final String CSS_CLASS_SAVE_BUTTON = "save-btn";
	protected static final String CSS_CLASS_DELETE_BUTTON = "delete-btn";
	
	protected static final String CSS_CLASS_FORM_BUTTON_ACTIVE = "form-button-active";
	protected static final String CSS_CLASS_FORM_LABEL = "form-label";
	protected static final String CSS_CLASS_FORM_FULLTEXTFIELD = "form-full-textfield";
	protected static final String CSS_CLASS_FORM_ICON = "form-icon";
	protected static final String CSS_CLASS_FORM_BUTTON_BAR_LEFT = "form-button-bar-left";
	protected static final String CSS_CLASS_FORM_BUTTON_BAR_RIGHT = "form-button-bar-right";
	protected static final String CSS_CLASS_FORM_BUTTON_BAR_CENTER = "form-button-bar-center";
	protected static final String CSS_CLASS_CENTER_TEXT = "center-text";
	
	protected static final String CSS_CLASS_WARNING_ICON = "warning-icon";
	protected static final String CSS_CLASS_WARNING_BG_ICON = "warning-bg-icon";
	protected static final String CSS_CLASS_INFO_MESSAGE = "info-msg";
	protected static final String CSS_CLASS_STATUS_MESSAGE = "status-msg";
	private static final String CSS_CLASS_INFO_BORDER_BOTTOM = "info-border-bottom";
	private static final String CSS_CLASS_WARNING_CONFIG = "warning-config";
	
	public AbstractFormView() {
		super();
		setAlignment(Pos.CENTER);
		buildAlarmHBox();
		gpContents = new GridPane();
		getChildren().add(gpContents);
		gpContents.setAlignment(Pos.CENTER);
		this.setMaxWidth(Double.MAX_VALUE);
		this.setMaxHeight(Double.MAX_VALUE);
		setVgrow(gpContents, Priority.ALWAYS);
		this.setFillWidth(true);
	}
	
	private void buildAlarmHBox() {
		// icon
		alarmBgPath = new SVGPath();
		alarmBgPath.setContent(TRIANGLE_ICON);
		alarmBgPath.getStyleClass().add(CSS_CLASS_WARNING_BG_ICON);
		alarmPath = new SVGPath();
		alarmPath.setContent(WARNING_ICON);
		alarmPath.getStyleClass().add(CSS_CLASS_WARNING_ICON);
		Pane alarmIconPane = new Pane();
		alarmIconPane.getChildren().addAll(alarmBgPath, alarmPath);
		// label
		lblAlarmMessage = new Label();
		lblAlarmMessage.setWrapText(true);
		lblAlarmMessage.getStyleClass().addAll(CSS_CLASS_INFO_MESSAGE, CSS_CLASS_STATUS_MESSAGE);
		// hbox
		hBoxAlarm = new HBox();
		hBoxAlarm.setMinHeight(ALARM_HEIGHT);
		hBoxAlarm.setMaxHeight(ALARM_HEIGHT);
		hBoxAlarm.setPrefHeight(ALARM_HEIGHT);
		HBox.setMargin(alarmIconPane, new Insets(0, ICON_PADDING, 0, 0));
		hBoxAlarm.getChildren().addAll(alarmIconPane, lblAlarmMessage);
		hBoxAlarm.getStyleClass().add(CSS_CLASS_INFO_BORDER_BOTTOM);
		hBoxAlarm.setAlignment(Pos.BOTTOM_LEFT);
		hBoxAlarm.setPadding(new Insets(10, 0, PADDING_BOTTOM, 0));
		lblAlarmMessage.setMaxWidth(450 - ICON_WIDTH - ICON_PADDING);
		lblAlarmMessage.getStyleClass().add(CSS_CLASS_WARNING_CONFIG);
		hBoxAlarm.setPrefWidth(500);
		hBoxAlarm.setMaxWidth(500);
						
		getChildren().add(hBoxAlarm);
		hideNotification();
	}
	
	public void showNotification(final String notification) {
		lblAlarmMessage.setText(notification);
		hBoxAlarm.setVisible(true);
		hBoxAlarm.setManaged(true);
	}
	
	public void hideNotification() {
		hBoxAlarm.setVisible(false);
		hBoxAlarm.setManaged(false);
	}
	
	public GridPane getContents() {
		return gpContents;
	}
	
	protected abstract void build();
	
	public abstract void setTextFieldListener(TextInputControlListener listener);
	
	public void setPresenter(final T presenter) {
		this.presenter = presenter;
	}
	
	public T getPresenter() {
		return presenter;
	}
	
	public static Button createButton(final String iconPath, final String iconClass, final String text, final double width, final double height, final EventHandler<ActionEvent> handler, final double iconWidth) {
		Button button = new Button();
		HBox hbox = new HBox();
		StackPane iconPane = new StackPane();
		SVGPath icon = new SVGPath();
		icon.setContent(iconPath);
		icon.getStyleClass().addAll(CSS_CLASS_FORM_BUTTON_ICON, iconClass);
		hbox.setAlignment(Pos.CENTER_LEFT);
		iconPane.getChildren().add(icon);
		iconPane.setPrefSize(iconWidth, height);
		HBox.setMargin(iconPane, new Insets(0, 0, 0, ICON_MARGIN));
		hbox.getChildren().add(iconPane);
		Label label = new Label(text);
		label.getStyleClass().add(CSS_CLASS_FORM_BUTTON_LABEL);
		label.setPrefSize(width - iconWidth - 3 * ICON_MARGIN, height);
		label.setAlignment(Pos.CENTER);
		hbox.getChildren().add(label);
		HBox.setHgrow(label, Priority.ALWAYS);
		hbox.setPrefSize(width, height);
		hbox.getStyleClass().add(CSS_CLASS_FORM_BUTTON_PANEL);
		button.setOnAction(handler);
		button.setGraphic(hbox);
		button.getStyleClass().add(CSS_CLASS_FORM_BUTTON);
		return button;
	}
	
	public static Button createButton(final String iconPath, final String iconClass, final String text, final double width, final double height, final EventHandler<ActionEvent> handler) {
		return createButton(iconPath, iconClass, text, width, height, handler, ICON_WIDTH);
	}
	
	public static Button createButton(final String text, final double width, final double height, final EventHandler<ActionEvent> handler) {
		Button button = new Button();
		HBox hbox = new HBox();
		hbox.setAlignment(Pos.CENTER_LEFT);
		Label label = new Label(text);
		label.getStyleClass().add(CSS_CLASS_FORM_BUTTON_LABEL);
		label.setPrefSize(width, height);
		label.setAlignment(Pos.CENTER);
		hbox.getChildren().add(label);
		HBox.setHgrow(label, Priority.ALWAYS);
		hbox.setPrefSize(width, height);
		hbox.getStyleClass().add(CSS_CLASS_FORM_BUTTON_PANEL);
		button.setOnAction(handler);
		button.setGraphic(hbox);
		button.getStyleClass().add(CSS_CLASS_FORM_BUTTON);
		return button;
	}
	
	public abstract void refresh();
}
