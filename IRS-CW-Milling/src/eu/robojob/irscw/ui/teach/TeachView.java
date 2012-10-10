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
	
	public static final int HEIGHT_TOP = 245;
	public static final int HEIGHT_BOTTOM = 300;
	public static final int WIDTH = 800;
	
	public TeachView() {
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

		bottom.setAlignment(Pos.TOP_CENTER);
		
		getChildren().add(bottom);
		bottom.setPrefHeight(HEIGHT_BOTTOM);
		bottom.setPrefWidth(WIDTH);
		VBox.setVgrow(bottom, Priority.ALWAYS);
		bottom.getStyleClass().add("teach-bottom");
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
}
