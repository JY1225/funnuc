package eu.robojob.millassist.ui.general;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.robojob.millassist.RoboSoft;
import eu.robojob.millassist.ui.controls.keyboard.NumericKeyboardView;
import eu.robojob.millassist.util.SizeManager;

public class MainContentView extends StackPane {

	private StackPane top;
	private StackPane spBottom;
	private GridPane gpBottom;
	private HBox hbBottom;
	private StackPane bottomLeft;
	private Pane bottomRight;
	private VBox mainContent;
	private StackPane keyboardPane;
	
	private static final String CSS_CLASS_BOTTOM_LEFT_PANE = "bottom-left";
	private static final String CSS_CLASS_BOTTOM_RIGHT_PANE = "bottom-right";
	private static final String CSS_CLASS_TOP_PANEL = "top-panel";
	private static final String CSS_CLASS_CONTENT_BOTTOM = "content-bottom";
	public static final String CSS_CLASS_TEACH_MESSAGE = "content-msg";
	public static final String CSS_CLASS_TEACH_BUTTON_TEXT = "content-btn-text";
	public static final String CSS_CLASS_TEACH_BUTTON = "content-btn";
	public static final String CSS_CLASS_INFO_MESSAGE_TITLE = "info-msg-title";


	private static Logger logger = LogManager.getLogger(RoboSoft.class.getName());
	
	public MainContentView() {
		build();
	}
	
	private void build() {
		keyboardPane = new StackPane();
		keyboardPane.setMaxHeight(300);
		StackPane.setAlignment(keyboardPane, Pos.BOTTOM_LEFT);
		this.mainContent = new VBox();
		getChildren().add(mainContent);
		mainContent.setFillWidth(true);
		mainContent.setAlignment(Pos.CENTER);
		top = new StackPane();
		this.mainContent.getChildren().add(top);
		top.setPrefHeight(SizeManager.HEIGHT_TOP);
		top.setPrefWidth(SizeManager.WIDTH);
		top.getStyleClass().add(CSS_CLASS_TOP_PANEL);
		
		spBottom = new StackPane();
		spBottom.setPrefHeight(SizeManager.HEIGHT_BOTTOM);
		spBottom.setPrefWidth(SizeManager.WIDTH);
		this.mainContent.getChildren().add(spBottom);
		VBox.setVgrow(spBottom, Priority.ALWAYS);

		gpBottom = new GridPane();
		gpBottom.setAlignment(Pos.TOP_CENTER);
		gpBottom.setPrefHeight(SizeManager.HEIGHT_BOTTOM);
		gpBottom.setPrefWidth(SizeManager.WIDTH);
		gpBottom.getStyleClass().add(CSS_CLASS_CONTENT_BOTTOM);
		
		spBottom.getChildren().add(gpBottom);
		
		hbBottom = new HBox();
		bottomLeft = new StackPane();
		hbBottom.getChildren().add(bottomLeft);
		bottomLeft.setPrefWidth(SizeManager.WIDTH_BOTTOM_LEFT);
		bottomLeft.getStyleClass().add(CSS_CLASS_BOTTOM_LEFT_PANE);
		
		bottomRight = new StackPane();
		hbBottom.getChildren().add(bottomRight);
		bottomRight.getStyleClass().add(CSS_CLASS_BOTTOM_RIGHT_PANE);
		bottomRight.setPrefWidth(SizeManager.WIDTH-SizeManager.WIDTH_BOTTOM_LEFT);
		bottomRight.setPrefHeight(SizeManager.HEIGHT_BOTTOM);
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
		hideBottomHBox();
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
		showBottomHBox();
		this.bottomLeft.getChildren().clear();
		this.bottomLeft.getChildren().add(node);
	}
	
	public void setBottomRight(final Node node) {
		showBottomHBox();
		this.bottomRight.getChildren().clear();
		this.bottomRight.getChildren().add(node);
	}
	
	public void setBottomLeftEnabled(final boolean enabled) {
		this.bottomLeft.setDisable(!enabled);
	}
	
	public void showKeyboardPane(final Node keyboardNode, final boolean top) {
		if (top) {
			StackPane.setAlignment(keyboardPane, Pos.TOP_LEFT);
		} else {
			StackPane.setAlignment(keyboardPane, Pos.BOTTOM_LEFT);
		}
		getChildren().remove(keyboardPane);
		keyboardPane.getChildren().clear();
		keyboardPane.getChildren().add(keyboardNode);
		getChildren().add(keyboardPane);
		if (keyboardNode instanceof NumericKeyboardView) {
			keyboardPane.setMaxWidth(200);
			keyboardPane.setMaxHeight(300);
		} else {
			keyboardPane.setMaxWidth(USE_PREF_SIZE);
			keyboardPane.setMaxHeight(250);
		}
	}
	
	public void closeKeyboard() {
		getChildren().remove(keyboardPane);
		requestFocus();
	}
}
