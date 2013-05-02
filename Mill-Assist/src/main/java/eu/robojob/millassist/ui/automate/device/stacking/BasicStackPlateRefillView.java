package eu.robojob.millassist.ui.automate.device.stacking;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.shape.SVGPath;
import eu.robojob.millassist.external.device.stacking.BasicStackPlate;
import eu.robojob.millassist.ui.controls.IntegerTextField;
import eu.robojob.millassist.ui.controls.TextInputControlListener;
import eu.robojob.millassist.ui.general.AbstractFormView;
import eu.robojob.millassist.util.Translator;
import eu.robojob.millassist.util.UIConstants;

public class BasicStackPlateRefillView extends AbstractFormView<BasicStackPlateRefillPresenter> {

	private BasicStackPlate basicStackPlate;
	private Label lblAmount;
	private IntegerTextField itfAmount;
	private Button btnMax;
	private Button btnRefill;
	
	private static final String AMOUNT = "BasicStackPlateRefillView.amount";
	private static final String MAX = "BasicStackPlateRefillView.max";
	private static final String REFILL = "BasicStackPlateRefillView.refill";
	
	private SVGPath alarmBgPath;
	private SVGPath alarmPath;
	private Label lblAlarmMessage;
	private HBox hBoxAlarm;
	private static final int MSG_MIN_HEIGHT = 40;
	private static final int PADDING_BOTTOM = 10;
	private static final int ICON_PADDING = 10;
	private static final int ICON_WIDTH = 25;
	private static final String TRIANGLE_ICON = "M 12.5,1.03125 C 11.993062,1.0311198 11.509776,1.3702678 11.125,2.0625 L 0.3125,21.46875 C -0.45731218,22.853735 0.22861858,24 1.8125,24 l 21.375,0 c 1.584142,0 2.268771,-1.145744 1.5,-2.53125 L 13.90625,2.0625 C 13.521995,1.3697471 13.006938,1.0313802 12.5,1.03125 z";
	private static final String WARNING_ICON = "m 10.9375,7.15625 0,2.59375 0.625,6.96875 1.875,0 0.625,-6.96875 0,-2.59375 z m 0.125,11.15625 0,2.875 2.875,0 0,-2.875 z";
	protected static final String CSS_CLASS_WARNING_ICON = "warning-icon";
	protected static final String CSS_CLASS_WARNING_BG_ICON = "warning-bg-icon";
	protected static final String CSS_CLASS_INFO_MESSAGE = "info-msg";
	protected static final String CSS_CLASS_STATUS_MESSAGE = "status-msg";
	private static final String CSS_CLASS_INFO_BORDER_BOTTOM = "info-border-bottom";
	private static final String CSS_CLASS_WARNING_CONFIG = "warning-config";
	
	public BasicStackPlateRefillView() {
		build();
	}
	
	public void setBasicStackPlate(final BasicStackPlate basicStackPlate) {
		this.basicStackPlate = basicStackPlate;
	}
	
	@Override
	protected void build() {
		setVgap(15);
		setHgap(15);
		
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
		hBoxAlarm.setMinHeight(MSG_MIN_HEIGHT);
		hBoxAlarm.setMaxHeight(MSG_MIN_HEIGHT);
		HBox.setMargin(alarmIconPane, new Insets(0, ICON_PADDING, 0, 0));
		hBoxAlarm.getChildren().addAll(alarmIconPane, lblAlarmMessage);
		hBoxAlarm.getStyleClass().add(CSS_CLASS_INFO_BORDER_BOTTOM);
		hBoxAlarm.setAlignment(Pos.TOP_LEFT);
		hBoxAlarm.setPadding(new Insets(0, 0, PADDING_BOTTOM, 0));
		lblAlarmMessage.setMaxWidth(350 - ICON_WIDTH - ICON_PADDING);
		lblAlarmMessage.getStyleClass().add(CSS_CLASS_WARNING_CONFIG);
		hBoxAlarm.setPrefWidth(350);
		hBoxAlarm.setMaxWidth(350);
				
		lblAmount = new Label(Translator.getTranslation(AMOUNT));
		lblAmount.getStyleClass().add(CSS_CLASS_FORM_LABEL);
		itfAmount = new IntegerTextField(4);
		btnMax = createButton(Translator.getTranslation(MAX), UIConstants.BUTTON_HEIGHT*2, UIConstants.BUTTON_HEIGHT, new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent arg0) {
				refreshMax();
			}
		});
		btnRefill = createButton(Translator.getTranslation(REFILL), UIConstants.BUTTON_HEIGHT*3, UIConstants.BUTTON_HEIGHT, new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent arg0) {
				getPresenter().refill(Integer.parseInt(itfAmount.getText()));
			}
		});
		int row = 0; int column = 0;
		add(hBoxAlarm, column++, row, 3, 1);
		row++; column = 0;
		add(lblAmount, column++, row);
		GridPane.setMargin(lblAmount, new Insets(0, 0, 0, 50));
		add(itfAmount, column++, row);
		add(btnMax, column++, row);
		row++; column = 0;
		add(btnRefill, column++, row, 3, 1);
		GridPane.setMargin(btnRefill, new Insets(0, 115, 0, 115));
		GridPane.setHalignment(btnRefill, HPos.CENTER);
		
		hideNotification();

	}

	@Override
	public void setTextFieldListener(final TextInputControlListener listener) {
		itfAmount.setFocusListener(listener);
	}

	@Override
	public void refresh() {
		refreshMax();
		hideNotification();
	}
	
	private void refreshMax() {
		itfAmount.setText("" + basicStackPlate.getFinishedWorkPiecesPresentAmount());
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
}
