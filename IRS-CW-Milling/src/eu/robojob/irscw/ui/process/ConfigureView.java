package eu.robojob.irscw.ui.process;

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
	
	private static final int HEIGHT_TOP = 251;
	private static final int WIDTH = 800;
	
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
		
		bottom = new HBox();
		getChildren().add(bottom);
		VBox.setVgrow(bottom, Priority.ALWAYS);
		
		bottomLeft = new StackPane();
		bottom.getChildren().add(bottomLeft);
		bottomLeft.setPrefWidth(200);
		
		bottomRight = new Pane();
		bottom.getChildren().add(bottomRight);
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
