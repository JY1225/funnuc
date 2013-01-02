package eu.robojob.irscw.ui.teach;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import eu.robojob.irscw.util.Translator;
import eu.robojob.irscw.util.UIConstants;

public class StatusView extends VBox {

	public enum Status {
		OK, ERROR, WARNING
	}
	
	private TeachPresenter presenter;
	private Region loading;
	private Label lblZRest;
	private Label lblZRestValue;
	private Label lblMessage;
	private Label lblAlarmMessage;
	private Button btnRestart;
	
	private static final double BTN_WIDTH = UIConstants.BUTTON_HEIGHT * 3.5;
	private static final double BTN_HEIGHT = 40;
	
	private static final String CSS_CLASS_LOADING = "loading";
	private static final String CSS_CLASS_LOADING_INACTIVE = "loading-inactive";
	private static final String CSS_CLASS_Z_REST = "lbl-z-rest";
	private static final String CSS_CLASS_Z_REST_VAL = "lbl-z-rest-val";
	private static final String CSS_CLASS_MSG_NORMAL = "message-normal";
	private static final String CSS_CLASS_MSG_ERROR = "message-error";
	
	private static final String DROPS_ANOTHER = "StatusView.dropsAnother";
	private static final String RESTART = "StatusView.restart";
			
	public StatusView() {
		build();
	}
	
	public void setPresenter(final TeachPresenter presenter) {
		this.presenter = presenter;
	}
	
	private void build() {
		
		this.setFillWidth(true);
		this.setPrefSize(520, 300);
		this.setAlignment(Pos.CENTER);
		
		HBox hboxRobotStatus = new HBox();
		
		loading = new Region();
		loading.setPrefSize(40, 40);
		loading.setMaxSize(40, 40);

		lblZRest = new Label();
		lblZRest.getStyleClass().add(CSS_CLASS_Z_REST);
		lblZRest.setPrefSize(100, 40);
		lblZRest.setMaxSize(100, 40);
		lblZRest.setMinSize(100, 40);
		lblZRest.setText(Translator.getTranslation(DROPS_ANOTHER));
		
		lblZRestValue = new Label();
		lblZRestValue.getStyleClass().add(CSS_CLASS_Z_REST);
		lblZRestValue.getStyleClass().add(CSS_CLASS_Z_REST_VAL);
		lblZRestValue.setPrefSize(100, 40);
		lblZRestValue.setMaxSize(100, 40);
		lblZRestValue.setMinSize(100, 40);
		
		hboxRobotStatus.setPrefSize(280, 40);
		hboxRobotStatus.setMaxSize(280, 40);
		hboxRobotStatus.setMinSize(280, 40);
		hboxRobotStatus.setAlignment(Pos.CENTER);
		
		hboxRobotStatus.getChildren().add(lblZRest);
		hboxRobotStatus.getChildren().add(loading);
		hboxRobotStatus.getChildren().add(lblZRestValue);
		
		lblZRestValue.setVisible(false);
		lblZRest.setVisible(false);
		
		setMargin(hboxRobotStatus, new Insets(30, 0, 30, 0));
		
		setProcessStopped();
		
		lblMessage = new Label();
		lblMessage.getStyleClass().addAll(TeachView.CSS_CLASS_TEACH_MESSAGE, CSS_CLASS_MSG_NORMAL);
		lblMessage.setPrefSize(500, 80);
		lblMessage.setWrapText(true);
		lblAlarmMessage = new Label();
		lblAlarmMessage.getStyleClass().addAll(TeachView.CSS_CLASS_TEACH_MESSAGE, CSS_CLASS_MSG_ERROR);
		lblAlarmMessage.setPrefSize(500, 80);
		lblAlarmMessage.setWrapText(true);
		
		StackPane spMessages = new StackPane();
		spMessages.getChildren().add(lblMessage);
		spMessages.getChildren().add(lblAlarmMessage);
		lblAlarmMessage.setVisible(false);
		
		setMargin(spMessages, new Insets(0, 0, 30, 0));
		
		btnRestart = new Button();
		btnRestart.setPrefSize(BTN_WIDTH, BTN_HEIGHT);
		btnRestart.getStyleClass().add(TeachView.CSS_CLASS_TEACH_BUTTON);
		Text txtPause = new Text(Translator.getTranslation(RESTART));
		txtPause.getStyleClass().add(TeachView.CSS_CLASS_TEACH_BUTTON_TEXT);
		btnRestart.setGraphic(txtPause);
		btnRestart.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent arg0) {
				presenter.stopTeaching();
			}
		});
		setMargin(btnRestart, new Insets(0, 0, 30, 0));

		this.getChildren().add(hboxRobotStatus);
		this.getChildren().add(spMessages);
		this.getChildren().add(btnRestart);
	}
	
	public void setMessage(final String message) {
		lblMessage.setText(message);
	}
	
	public void setAlarmMessage(final String message) {
		lblAlarmMessage.setText(message);
		lblAlarmMessage.setVisible(true);
	}
	
	public void hideAlarmMessage() {
		lblAlarmMessage.setVisible(false);
	}
	
	public void setProcessPaused() {
		loading.getStyleClass().remove(CSS_CLASS_LOADING);
		loading.getStyleClass().remove(CSS_CLASS_LOADING_INACTIVE);
		loading.getStyleClass().add(CSS_CLASS_LOADING);
	}
	
	public void setProcessRunning() {
		loading.getStyleClass().remove(CSS_CLASS_LOADING);
		loading.getStyleClass().remove(CSS_CLASS_LOADING_INACTIVE);
		loading.getStyleClass().add(CSS_CLASS_LOADING);
	}
	
	public void setProcessStopped() {
		loading.getStyleClass().remove(CSS_CLASS_LOADING);
		loading.getStyleClass().remove(CSS_CLASS_LOADING_INACTIVE);
		loading.getStyleClass().add(CSS_CLASS_LOADING_INACTIVE);
	}
	
	public void setZRest(final double zrest) {
		if (zrest > 0) {
			lblZRestValue.setText(zrest + " mm");
			lblZRestValue.setVisible(true);
			lblZRest.setVisible(true);
		} else {
			lblZRestValue.setVisible(false);
			lblZRest.setVisible(false);
		}
	}
}
