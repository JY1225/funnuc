package eu.robojob.irscw.ui.teach;

import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;
import javafx.util.Duration;
import eu.robojob.irscw.util.Translator;
import eu.robojob.irscw.util.UIConstants;

public class StatusView extends VBox {

	private TeachPresenter presenter;
	
	private static final String loadingPath = "M 17.5 -0.03125 C 16.287045 -0.03125 15.3125 0.94766962 15.3125 2.15625 C 15.3125 3.3648304 16.287045 4.34375 17.5 4.34375 C 18.705299 4.34375 19.6875 3.3648304 19.6875 2.15625 C 19.6875 0.94766962 18.705299 -0.03125 17.5 -0.03125 z M 6.5625 4.34375 C 5.3543904 4.34375 4.375 5.3231395 4.375 6.53125 C 4.375 7.7393586 5.3543904 8.71875 6.5625 8.71875 C 7.7706094 8.71875 8.75 7.7393586 8.75 6.53125 C 8.75 5.3231395 7.7706094 4.34375 6.5625 4.34375 z M 28.4375 4.34375 C 27.877779 4.34375 27.333355 4.5739885 26.90625 5 C 26.052041 5.8563974 26.052041 7.2039163 26.90625 8.0625 C 27.760461 8.9145212 29.145791 8.9145212 30 8.0625 C 30.86296 7.2104788 30.86296 5.8542093 30 5 C 29.572895 4.5718003 28.997221 4.34375 28.4375 4.34375 z M 17.5 8.71875 C 12.663491 8.71875 8.75 12.636617 8.75 17.46875 C 8.75 22.300884 12.663491 26.21875 17.5 26.21875 C 22.327757 26.21875 26.25 22.300884 26.25 17.46875 C 26.25 12.636617 22.327757 8.71875 17.5 8.71875 z M 2.1875 15.28125 C 0.97454455 15.28125 1.4456029e-019 16.260169 0 17.46875 C 0 18.679517 0.97454455 19.65625 2.1875 19.65625 C 3.3927991 19.65625 4.375 18.679517 4.375 17.46875 C 4.375 16.260171 3.3927991 15.28125 2.1875 15.28125 z M 32.8125 15.28125 C 31.59845 15.28125 30.625 16.255795 30.625 17.46875 C 30.625 18.676235 31.611574 19.65625 32.8125 19.65625 C 34.025454 19.6585 35.013125 18.679517 35 17.46875 C 35.013125 16.260171 34.013422 15.28125 32.8125 15.28125 z M 6.5625 26.21875 C 5.3543904 26.21875 4.375 27.198138 4.375 28.40625 C 4.375 29.614359 5.3543904 30.59375 6.5625 30.59375 C 7.7706094 30.59375 8.75 29.614359 8.75 28.40625 C 8.75 27.198138 7.7706094 26.21875 6.5625 26.21875 z M 28.4375 26.21875 C 27.877779 26.21875 27.333355 26.447897 26.90625 26.875 C 26.052041 27.729209 26.052041 29.083293 26.90625 29.9375 C 27.760461 30.791711 29.145791 30.791711 30 29.9375 C 30.86296 29.083293 30.86296 27.729209 30 26.875 C 29.572895 26.447897 28.997221 26.21875 28.4375 26.21875 z M 17.5 30.59375 C 16.287045 30.59375 15.3125 31.568295 15.3125 32.78125 C 15.3125 33.989831 16.287045 34.96875 17.5 34.96875 C 18.712953 34.96875 19.6875 33.989831 19.6875 32.78125 C 19.6875 31.568295 18.712953 30.59375 17.5 30.59375 z";
	private SVGPath loading;
	
	private Label lblZRest;

	private Label lblAlarmMessage;
	private Label lblInfoMessage;
	private RotateTransition rotation;
	
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
		this.setAlignment(Pos.CENTER);
		
		loading = new SVGPath();
		loading.setContent(loadingPath);
		
		setMargin(loading, new Insets(30, 0, 0, 0));
		
		rotation = new RotateTransition(Duration.millis(2000), loading);
		rotation.setFromAngle(0);
		rotation.setToAngle(360);
		rotation.setInterpolator(Interpolator.LINEAR);
		rotation.setCycleCount(Timeline.INDEFINITE);
		
		setProcessStopped();
		
		lblZRest = new Label();
		lblZRest.getStyleClass().add("lbl-z-rest");
		lblZRest.setPrefSize(300, 20);
		lblZRest.setWrapText(true);
		
		lblAlarmMessage = new Label();
		lblAlarmMessage.getStyleClass().add("alarm-msg");
		lblAlarmMessage.setPrefSize(520, 30);
		lblAlarmMessage.setWrapText(true);
		setMargin(lblAlarmMessage, new Insets(20, 0, 20, 0));
		
		lblInfoMessage = new Label();
		lblInfoMessage.getStyleClass().add("info-msg");
		lblInfoMessage.setPrefSize(520, 60);
		lblInfoMessage.setWrapText(true);
		setMargin(lblInfoMessage, new Insets(0, 0, 20, 0));
		
		btnRestart = new Button();
		btnRestart.setPrefSize(BTN_WIDTH, BTN_HEIGHT);
		btnRestart.getStyleClass().add("teach-btn");
		Text txtPause = new Text(translator.getTranslation("restart"));
		txtPause.getStyleClass().add("teach-btn-text");
		btnRestart.setGraphic(txtPause);
		btnRestart.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				presenter.setActive(false);
				presenter.setActive(true);
			}
		});
		setMargin(btnRestart, new Insets(0, 0, 30, 0));

		
		this.getChildren().add(loading);
		this.getChildren().add(lblZRest);
		this.getChildren().add(lblAlarmMessage);
		this.getChildren().add(lblInfoMessage);
		this.getChildren().add(btnRestart);
	}
	
	public void setMessage(String message) {
		lblInfoMessage.setText(message);
	}
	
	public void setAlarmMessage(String message) {
		lblAlarmMessage.setText(message);
	}
	
	public void setProcessPaused() {
		loading.getStyleClass().remove("loading");
		loading.getStyleClass().remove("loading-inactive");
		rotation.pause();
		loading.getStyleClass().add("loading");
	}
	
	public void setProcessRunning() {
		loading.getStyleClass().remove("loading");
		loading.getStyleClass().remove("loading-inactive");
		rotation.play();
		loading.getStyleClass().add("loading");
	}
	
	public void setProcessStopped() {
		loading.getStyleClass().remove("loading");
		loading.getStyleClass().remove("loading-inactive");
		rotation.pause();
		loading.getStyleClass().add("loading-inactive");
	}
	
	public void setZRest(double zrest) {
		if (zrest > 0) {
			lblZRest.setText("Z resterend: " + zrest);
			lblZRest.setVisible(true);
		} else {
			lblZRest.setVisible(false);
		}
	}
}
