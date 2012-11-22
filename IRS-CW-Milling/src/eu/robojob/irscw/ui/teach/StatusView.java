package eu.robojob.irscw.ui.teach;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
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
	
	private Button btnRestart;
	
	private static final double BTN_WIDTH = UIConstants.BUTTON_HEIGHT * 3.5;
	private static final double BTN_HEIGHT = 40;
		
	private Translator translator = Translator.getInstance();
	
	public StatusView() {
		build();
	}
	
	public void setPresenter(TeachPresenter presenter) {
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
		lblZRest.getStyleClass().add("lbl-z-rest");
		lblZRest.setPrefSize(100, 40);
		lblZRest.setMaxSize(100, 40);
		lblZRest.setMinSize(100, 40);
		lblZRest.setText("zakt nog");
		
		lblZRestValue = new Label();
		lblZRestValue.getStyleClass().add("lbl-z-rest");
		lblZRestValue.getStyleClass().add("lbl-z-rest-val");
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
		
		setMargin(hboxRobotStatus, new Insets(30, 0, 30, 0));
		
		setProcessStopped();
		
		lblMessage = new Label();
		lblMessage.getStyleClass().add("teach-msg");
		lblMessage.setPrefSize(500, 60);
		lblMessage.setWrapText(true);
		setMargin(lblMessage, new Insets(0, 0, 30, 0));
		
		btnRestart = new Button();
		btnRestart.setPrefSize(BTN_WIDTH, BTN_HEIGHT);
		btnRestart.getStyleClass().add("teach-btn");
		Text txtPause = new Text(translator.getTranslation("restart"));
		txtPause.getStyleClass().add("teach-btn-text");
		btnRestart.setGraphic(txtPause);
		btnRestart.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				presenter.stopTeaching();
			}
		});
		setMargin(btnRestart, new Insets(0, 0, 30, 0));

		this.getChildren().add(hboxRobotStatus);
		this.getChildren().add(lblMessage);
		this.getChildren().add(btnRestart);
	}
	
	public void setMessage(Status status, String message) {
		lblMessage.setText(message);
	}
	
	public void setProcessPaused() {
		loading.getStyleClass().remove("loading");
		loading.getStyleClass().remove("loading-inactive");
		loading.getStyleClass().add("loading");
	}
	
	public void setProcessRunning() {
		loading.getStyleClass().remove("loading");
		loading.getStyleClass().remove("loading-inactive");
		loading.getStyleClass().add("loading");
	}
	
	public void setProcessStopped() {
		loading.getStyleClass().remove("loading");
		loading.getStyleClass().remove("loading-inactive");
		loading.getStyleClass().add("loading-inactive");
	}
	
	public void setZRest(double zrest) {
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
