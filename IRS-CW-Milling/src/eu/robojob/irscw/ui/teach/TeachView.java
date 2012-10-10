package eu.robojob.irscw.ui.teach;

import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import javafx.util.Duration;
import eu.robojob.irscw.util.Translator;
import eu.robojob.irscw.util.UIConstants;

public class TeachView extends VBox {

	private StackPane top;
	private GridPane bottom;
	
	private TeachPresenter presenter;
	
	public static final int HEIGHT_TOP = 245;
	public static final int HEIGHT_BOTTOM = 300;
	public static final int WIDTH = 800;
	
	private static final String arrowRightPath = "M 7.53125 -0.03125 L 7.53125 5.5625 L 0 5.5625 L 0 9.3125 L 7.53125 9.3125 L 7.53125 14.9375 L 15 7.4375 L 7.53125 -0.03125 z";
	private static final String loadingPath = "M 15 -0.03125 C 13.960313 -0.03125 13.125 0.80781244 13.125 1.84375 C 13.125 2.8796875 13.960313 3.71875 15 3.71875 C 16.033125 3.71875 16.875 2.8796875 16.875 1.84375 C 16.875 0.80781244 16.033125 -0.03125 15 -0.03125 z M 5.625 3.71875 C 4.5894661 3.71875 3.75 4.5582161 3.75 5.59375 C 3.75 6.6292839 4.5894661 7.46875 5.625 7.46875 C 6.6605339 7.46875 7.5 6.6292839 7.5 5.59375 C 7.5 4.5582161 6.6605339 3.71875 5.625 3.71875 z M 24.375 3.71875 C 23.895234 3.71875 23.428594 3.8848437 23.0625 4.25 C 22.330313 4.9840625 22.330313 6.1703125 23.0625 6.90625 C 23.794688 7.6365625 24.986563 7.6365625 25.71875 6.90625 C 26.458438 6.1759375 26.458438 5.0134375 25.71875 4.28125 C 25.352656 3.9142187 24.854766 3.71875 24.375 3.71875 z M 15 7.46875 C 10.854375 7.46875 7.5 10.826875 7.5 14.96875 C 7.5 19.110625 10.854375 22.46875 15 22.46875 C 19.138125 22.46875 22.5 19.110625 22.5 14.96875 C 22.5 10.826875 19.138125 7.46875 15 7.46875 z M 1.875 13.09375 C 0.83531244 13.09375 0 13.932812 0 14.96875 C 0 16.006563 0.83531244 16.84375 1.875 16.84375 C 2.908125 16.84375 3.75 16.006563 3.75 14.96875 C 3.75 13.932813 2.908125 13.09375 1.875 13.09375 z M 28.125 13.09375 C 27.084375 13.09375 26.25 13.929062 26.25 14.96875 C 26.25 16.00375 27.095625 16.84375 28.125 16.84375 C 29.164687 16.84565 30.01125 16.006563 30 14.96875 C 30.01125 13.932813 29.154375 13.09375 28.125 13.09375 z M 5.625 22.46875 C 4.5894661 22.46875 3.75 23.308216 3.75 24.34375 C 3.75 25.379284 4.5894661 26.21875 5.625 26.21875 C 6.6605339 26.21875 7.5 25.379284 7.5 24.34375 C 7.5 23.308216 6.6605339 22.46875 5.625 22.46875 z M 24.375 22.46875 C 23.895234 22.46875 23.428594 22.633906 23.0625 23 C 22.330313 23.732187 22.330313 24.924063 23.0625 25.65625 C 23.794688 26.388437 24.986563 26.388437 25.71875 25.65625 C 26.458438 24.924063 26.458438 23.732187 25.71875 23 C 25.352656 22.633906 24.854766 22.46875 24.375 22.46875 z M 15 26.21875 C 13.960313 26.21875 13.125 27.054063 13.125 28.09375 C 13.125 29.129688 13.960313 29.96875 15 29.96875 C 16.039687 29.96875 16.875 29.129688 16.875 28.09375 C 16.875 27.054063 16.039687 26.21875 15 26.21875 z";
	
	private SVGPath arrowRight;
	private SVGPath loading;
	
	private VBox vbInfo;
	private Label lblInfoMessage;
	private Button btnStart;
	private Label lblStart;
	
	private Translator translator;
	private RotateTransition rotation;
	
	
	private static final double BUTTON_WIDTH = UIConstants.BUTTON_HEIGHT * 4;
	
	public TeachView() {
		translator = Translator.getInstance();
		build();
	}
	
	private void build() {
		this.setFillWidth(true);
		this.setAlignment(Pos.CENTER);
		
		top = new StackPane();
		getChildren().add(top);
		top.setPrefHeight(HEIGHT_TOP);
		top.setPrefWidth(WIDTH);
		top.getStyleClass().add("top-panel");
		
		bottom = new GridPane();

		loading = new SVGPath();
		loading.setContent(loadingPath);
		
		rotation = new RotateTransition(Duration.millis(2000), loading);
		rotation.setFromAngle(0);
		rotation.setToAngle(360);
		rotation.setInterpolator(Interpolator.LINEAR);
		rotation.setCycleCount(Timeline.INDEFINITE);
		//rotation.play();
		
		setProcessPaused(true);
		
		vbInfo = new VBox();
		lblInfoMessage = new Label(translator.getTranslation("teach-info"));
		lblInfoMessage.getStyleClass().add("info-msg");
		lblInfoMessage.setPrefSize(520, 130);
		lblInfoMessage.setWrapText(true);
		btnStart = new Button();
		arrowRight = new SVGPath();
		arrowRight.setContent(arrowRightPath);
		arrowRight.getStyleClass().add("btn-start-icon");
		HBox hboxStart = new HBox();
		lblStart = new Label(translator.getTranslation("start"));
		lblStart.getStyleClass().add("btn-start-label");
		hboxStart.getChildren().add(lblStart);
		lblStart.setPrefSize(BUTTON_WIDTH - 40, UIConstants.BUTTON_HEIGHT);
		hboxStart.getChildren().add(arrowRight);
		hboxStart.setPrefSize(BUTTON_WIDTH, UIConstants.BUTTON_HEIGHT);
		hboxStart.setAlignment(Pos.CENTER);
		HBox.setHgrow(lblStart, Priority.ALWAYS);
		btnStart.setGraphic(hboxStart);
		btnStart.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				presenter.startFlow();
			}
		});
		btnStart.setPrefSize(BUTTON_WIDTH, UIConstants.BUTTON_HEIGHT);
		btnStart.getStyleClass().add("btn-start");
		vbInfo.setAlignment(Pos.TOP_CENTER);
		vbInfo.getChildren().add(loading);
		vbInfo.getChildren().add(lblInfoMessage);
		vbInfo.getChildren().add(btnStart);
		vbInfo.setPadding(new Insets(50, 0, 0, 0));
		
		bottom.add(vbInfo, 0, 0);
		bottom.setAlignment(Pos.TOP_CENTER);
		
		getChildren().add(bottom);
		bottom.setPrefHeight(HEIGHT_BOTTOM);
		bottom.setPrefWidth(WIDTH);
		VBox.setVgrow(bottom, Priority.ALWAYS);
		bottom.getStyleClass().add("teach-bottom");
	}
	
	public void showTeachInfo() {
		lblInfoMessage.setText(translator.getTranslation("teach-message"));
		vbInfo.getChildren().remove(btnStart);
		vbInfo.getChildren().add(btnStart);
		lblStart.setText(translator.getTranslation("teach-finished"));
		btnStart.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				presenter.continueFlow();
			}
		});
	}
	
	public void showInfo(String info) {
		if (vbInfo.getChildren().contains(btnStart)) {
			vbInfo.getChildren().remove(btnStart);
		}
		lblInfoMessage.setText(info);
	}
	
	public void setProcessPaused(boolean paused) {
		loading.getStyleClass().remove("loading");
		loading.getStyleClass().remove("loading-inactive");
		if (paused) {
			rotation.pause();
			loading.getStyleClass().add("loading-inactive");
		} else {
			rotation.play();
			loading.getStyleClass().add("loading");
		}
	}
	
	public void addNodeToTop(Node node) {
		this.top.getChildren().add(node);
	}
	
	public void removeNodeFromTop(Node node) {
		this.top.getChildren().remove(node);
	}
	
	public void setTop(Node node) {
		this.top.getChildren().clear();
		this.top.getChildren().add(node);
	}
	
	public void addNodeToBottom(Node node) {
		this.bottom.getChildren().add(node);
	}
	
	public void removeNodeFromBottom(Node node) {
		this.bottom.getChildren().remove(node);
	}
	
	public void setBottom(Node bottom) {
		this.bottom.getChildren().clear();
		this.bottom.getChildren().add(bottom);
	}
	
	public void setPresenter(TeachPresenter presenter) {
		this.presenter = presenter;
	}
}
