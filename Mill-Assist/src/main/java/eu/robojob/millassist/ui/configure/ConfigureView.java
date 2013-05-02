package eu.robojob.millassist.ui.configure;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class ConfigureView extends VBox {
	
	private StackPane top;
	private HBox bottom;
	private StackPane bottomLeft;
	private Pane bottomRight;
	
	private ConfigurePresenter presenter;
	
	public static final int HEIGHT_TOP = 255;
	public static final int HEIGHT_BOTTOM = 300;
	public static final int WIDTH = 800;
	public static final int WIDTH_BOTTOM_LEFT = 210;
	
	private static final String CSS_CLASS_TOP_PANE = "top-panel";
	private static final String CSS_CLASS_BOTTOM_PANE = "bottompane";
	private static final String CSS_CLASS_BOTTOM_LEFT_PANE = "bottom-left";
	private static final String CSS_CLASS_BOTTOM_RIGHT_PANE = "bottom-right";
	
	public ConfigureView() {
		buildView();
	}
	
	public void setPresenter(final ConfigurePresenter presenter) {
		this.presenter = presenter;
	}
	
	public ConfigurePresenter getPresenter() {
		return presenter;
	}
	
	protected void buildView() {
		this.setFillWidth(true);
		this.setAlignment(Pos.CENTER);
		
		top = new StackPane();
		getChildren().add(top);
		top.setPrefHeight(HEIGHT_TOP);
		top.setPrefWidth(WIDTH);
		top.getStyleClass().add(CSS_CLASS_TOP_PANE);
		
		bottom = new HBox();
		getChildren().add(bottom);
		bottom.setPrefHeight(HEIGHT_BOTTOM);
		bottom.setMinHeight(HEIGHT_BOTTOM);
		bottom.setMaxHeight(HEIGHT_BOTTOM);
		bottom.setPrefWidth(WIDTH);
		VBox.setVgrow(bottom, Priority.ALWAYS);
		bottom.getStyleClass().add(CSS_CLASS_BOTTOM_PANE);
				
		bottomLeft = new StackPane();
		bottom.getChildren().add(bottomLeft);
		bottomLeft.setPrefWidth(WIDTH_BOTTOM_LEFT);
		bottomLeft.getStyleClass().add(CSS_CLASS_BOTTOM_LEFT_PANE);
		
		bottomRight = new Pane();
		bottom.getChildren().add(bottomRight);
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
	
	public void addNodeToBottomLeft(final Node node) {
		this.bottomLeft.getChildren().add(node);
	}
	
	public void removeNodeFromBottomLeft(final Node node) {
		this.bottomLeft.getChildren().remove(node);
	}
	
	public void setBottomLeft(final Node bottom) {
		this.bottomLeft.getChildren().clear();
		this.bottomLeft.getChildren().add(bottom);
	}
	
	public void setBottomRight(final Node node) {
		this.bottomRight.getChildren().clear();
		this.bottomRight.getChildren().add(node);
	}
	
	public void setBottomLeftEnabled(final boolean enabled) {
		this.bottomLeft.setDisable(!enabled);
	}
}
