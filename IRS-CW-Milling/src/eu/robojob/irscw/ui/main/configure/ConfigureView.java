package eu.robojob.irscw.ui.main.configure;

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
	
	public static final int HEIGHT_TOP = 245;
	public static final int HEIGHT_BOTTOM = 300;
	public static final int WIDTH = 800;
	public static final int WIDTH_BOTTOM_LEFT = 230;
	
	public ConfigureView () {
		buildView();
	}
	
	public void setPresenter(ConfigurePresenter presenter) {
		this.presenter = presenter;
	}
	
	protected void buildView() {
		this.setFillWidth(true);
		this.setAlignment(Pos.CENTER);
		
		top = new StackPane();
		getChildren().add(top);
		top.setPrefHeight(HEIGHT_TOP);
		top.setPrefWidth(WIDTH);
		top.getStyleClass().add("top-panel");
		
		bottom = new HBox();
		getChildren().add(bottom);
		bottom.setPrefHeight(HEIGHT_BOTTOM);
		bottom.setPrefWidth(WIDTH);
		VBox.setVgrow(bottom, Priority.ALWAYS);
		bottom.getStyleClass().add("bottompane");
		
		bottomLeft = new StackPane();
		bottom.getChildren().add(bottomLeft);
		bottomLeft.setPrefWidth(WIDTH_BOTTOM_LEFT);
		bottomLeft.getStyleClass().add("bottom-left");
		
		bottomRight = new Pane();
		bottom.getChildren().add(bottomRight);
		bottomRight.getStyleClass().add("bottom-right");
		bottomRight.setPrefWidth(WIDTH-WIDTH_BOTTOM_LEFT);

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
	
	public void addNodeToBottomLeft(Node node) {
		this.bottomLeft.getChildren().add(node);
	}
	
	public void removeNodeFromBottomLeft(Node node) {
		this.bottomLeft.getChildren().remove(node);
	}
	
	public void setBottomLeft(Node bottom) {
		this.bottomLeft.getChildren().clear();
		this.bottomLeft.getChildren().add(bottom);
	}
	
	public void setBottomRight(Node node) {
		this.bottomRight.getChildren().clear();
		this.bottomRight.getChildren().add(node);
	}
}
