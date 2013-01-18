package eu.robojob.irscw.ui;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class MainContentView extends VBox {

	private StackPane top;
	private GridPane bottom;
	
	public static final int HEIGHT_TOP = 245;
	public static final int HEIGHT_BOTTOM = 300;
	public static final int WIDTH = 800;
	
	private static final String CSS_CLASS_TOP_PANEL = "top-panel";
	private static final String CSS_CLASS_TEACH_BOTTOM = "content-bottom";
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
		bottom = new GridPane();
		bottom.setAlignment(Pos.TOP_CENTER);
		getChildren().add(bottom);
		bottom.setPrefHeight(HEIGHT_BOTTOM);
		bottom.setPrefWidth(WIDTH);
		VBox.setVgrow(bottom, Priority.ALWAYS);
		bottom.getStyleClass().add(CSS_CLASS_TEACH_BOTTOM);
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
		this.bottom.getChildren().add(node);
	}
	
	public void removeNodeFromBottom(final Node node) {
		this.bottom.getChildren().remove(node);
	}
	
	public void setBottom(final Node bottom) {
		this.bottom.getChildren().clear();
		this.bottom.getChildren().add(bottom);
	}
}
