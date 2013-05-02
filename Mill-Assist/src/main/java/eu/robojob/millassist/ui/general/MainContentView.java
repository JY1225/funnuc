package eu.robojob.millassist.ui.general;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class MainContentView extends VBox {

	private StackPane top;
	private StackPane spBottom;
	private GridPane gpBottom;
	private HBox hbBottom;
	private StackPane bottomLeft;
	private Pane bottomRight;
	
	public static final int HEIGHT_TOP = 255;
	public static final int HEIGHT_BOTTOM = 300;
	public static final int WIDTH = 800;

	public static final int WIDTH_BOTTOM_LEFT = 210;
	
	private static final String CSS_CLASS_BOTTOM_LEFT_PANE = "bottom-left";
	private static final String CSS_CLASS_BOTTOM_RIGHT_PANE = "bottom-right";
	private static final String CSS_CLASS_TOP_PANEL = "top-panel";
	private static final String CSS_CLASS_CONTENT_BOTTOM = "content-bottom";
	public static final String CSS_CLASS_TEACH_MESSAGE = "content-msg";
	public static final String CSS_CLASS_TEACH_BUTTON_TEXT = "content-btn-text";
	public static final String CSS_CLASS_TEACH_BUTTON = "content-btn";
	public static final String CSS_CLASS_INFO_MESSAGE_TITLE = "info-msg-title";

	public MainContentView() {
		build();
	}
	
	private void build() {
		this.setFillWidth(true);
		this.setAlignment(Pos.CENTER);
		top = new StackPane();
		getChildren().add(top);
		top.setPrefHeight(HEIGHT_TOP);
		top.setPrefWidth(WIDTH);
		top.getStyleClass().add(CSS_CLASS_TOP_PANEL);
		
		spBottom = new StackPane();
		spBottom.setPrefHeight(HEIGHT_BOTTOM);
		spBottom.setPrefWidth(WIDTH);
		getChildren().add(spBottom);
		VBox.setVgrow(spBottom, Priority.ALWAYS);

		gpBottom = new GridPane();
		gpBottom.setAlignment(Pos.TOP_CENTER);
		gpBottom.setPrefHeight(HEIGHT_BOTTOM);
		gpBottom.setPrefWidth(WIDTH);
		gpBottom.getStyleClass().add(CSS_CLASS_CONTENT_BOTTOM);
		
		spBottom.getChildren().add(gpBottom);
		
		hbBottom = new HBox();
		bottomLeft = new StackPane();
		hbBottom.getChildren().add(bottomLeft);
		bottomLeft.setPrefWidth(WIDTH_BOTTOM_LEFT);
		bottomLeft.getStyleClass().add(CSS_CLASS_BOTTOM_LEFT_PANE);
		
		bottomRight = new Pane();
		hbBottom.getChildren().add(bottomRight);
		bottomRight.getStyleClass().add(CSS_CLASS_BOTTOM_RIGHT_PANE);
		bottomRight.setPrefWidth(WIDTH - WIDTH_BOTTOM_LEFT);
		bottomRight.setPrefHeight(HEIGHT_BOTTOM);
	}
	
	public void addNodeToTop(final Node node) {
		this.top.getChildren().add(node);
	}
	
	public void removeNodeFromTop(final Node node) {
		this.top.getChildren().remove(node);
	}
	
	public void setTop(final Node node) {
		this.top.getChildren().clear();
		this.top.getChildren().add(node);
	}
	
	public void addNodeToBottom(final Node node) {
		this.gpBottom.getChildren().add(node);
	}
	
	public void removeNodeFromBottom(final Node node) {
		this.gpBottom.getChildren().remove(node);
	}
	
	public void setBottom(final Node bottom) {
		this.gpBottom.getChildren().clear();
		this.gpBottom.getChildren().add(bottom);
	}
	
	public void showBottomHBox() {
		this.spBottom.getChildren().remove(hbBottom);
		this.spBottom.getChildren().add(hbBottom);
	}
	
	public void hideBottomHBox() {
		this.spBottom.getChildren().remove(hbBottom);
	}
	
	public void addNodeToBottomLeft(final Node node) {
		bottomLeft.getChildren().add(node);
	}
	
	public void removeNodeFromBottomLeft(final Node node) {
		bottomLeft.getChildren().remove(node);
	}
	
	public void setBottomLeft(final Node node) {
		this.bottomLeft.getChildren().clear();
		this.bottomLeft.getChildren().add(node);
	}
	
	public void setBottomRight(final Node node) {
		this.bottomRight.getChildren().clear();
		this.bottomRight.getChildren().add(node);
	}
}
