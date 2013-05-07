package eu.robojob.millassist.ui.general.flow;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.SVGPath;
import eu.robojob.millassist.ui.general.model.TransportInformation;

public class TransportButton extends StackPane {

	private Region shapeRegion;
	private SVGPath svgPauseLeft;
	private HBox hboxPauseLeft;
	private Label lblLeft;
	private SVGPath svgPauseRight;
	private HBox hboxPauseRight;
	private Label lblRight;
	
	public static final double WIDTH = 120;
	private static final double SHAPE_HEIGHT = 7;
	private static final String PAUSE_ICON = "M 0,0 0,10 2.5,10 2.5,0 0,0 z M 5,0 5,10 7.5,10 7.5,0 5,0 z";
	private TransportInformation transportInfo;
	private static final double PAUSE_ICON_WIDTH = 7.5;
	private static final double PAUSE_ICON_LENGTH = 10;
	private static final double PAUSE_HBOX_WIDTH = 50;
	private static final double PAUSE_HBOX_HEIGHT = 20;
	private static final double PAUSE_LBL_MARGIN = 30;
	
	private static final String CSS_CLASS_TRANSPORT_LABEL = "transport-label";
	private static final String CSS_CLASS_PAUSE_SHAPE = "pause-shape";
	private static final String CSS_CLASS_UNCLICKABLE = "unclickable";

	private EventHandler<MouseEvent> handlerPressed;
	private EventHandler<MouseEvent> handlerReleased;
	
	public TransportButton(final TransportInformation transportInfo) {
		build();
		setTransportInformation(transportInfo);
		setFocused(true);
	}
	
	private void build() {
		shapeRegion = new Region();
		shapeRegion.setPrefSize(WIDTH, SHAPE_HEIGHT);
		shapeRegion.setMinSize(WIDTH, SHAPE_HEIGHT);
		shapeRegion.setMaxSize(WIDTH, SHAPE_HEIGHT);
		shapeRegion.getStyleClass().add("transport-button");
		this.getStyleClass().add("transport-button-wrapper");
		this.getChildren().add(shapeRegion);
		this.setPrefWidth(WIDTH);
		this.setMinWidth(WIDTH);
				
		handlerPressed = new EventHandler<MouseEvent>() {
			@Override
			public void handle(final MouseEvent event) {
				TransportButton.this.requestFocus();
				event.consume();
			}
		};
		
		handlerReleased = new EventHandler<MouseEvent>() {
			@Override
			public void handle(final MouseEvent event) {
				event.consume();
			}
		};
		
		this.addEventHandler(MouseEvent.MOUSE_PRESSED, handlerPressed);
		this.addEventHandler(MouseEvent.MOUSE_RELEASED, handlerReleased);
		
		svgPauseLeft = new SVGPath();
		svgPauseLeft.setContent(PAUSE_ICON);
		svgPauseLeft.getStyleClass().add(CSS_CLASS_PAUSE_SHAPE);
		Pane paneIconLeft = new Pane();
		paneIconLeft.getChildren().add(svgPauseLeft);
		paneIconLeft.setPrefSize(PAUSE_ICON_WIDTH, PAUSE_ICON_LENGTH);
		paneIconLeft.setMinSize(PAUSE_ICON_WIDTH, PAUSE_ICON_LENGTH);
		paneIconLeft.setMaxSize(PAUSE_ICON_WIDTH, PAUSE_ICON_LENGTH);
		lblLeft = new Label();
		lblLeft.getStyleClass().add(CSS_CLASS_TRANSPORT_LABEL);
		this.getChildren().add(lblLeft);
		hboxPauseLeft = new HBox();
		hboxPauseLeft.getChildren().add(paneIconLeft);
		hboxPauseLeft.getChildren().add(lblLeft);
		hboxPauseLeft.setPrefSize(PAUSE_HBOX_WIDTH, PAUSE_HBOX_HEIGHT);
		hboxPauseLeft.setMaxSize(PAUSE_HBOX_WIDTH, PAUSE_HBOX_HEIGHT);
		hboxPauseLeft.setMinSize(PAUSE_HBOX_WIDTH, PAUSE_HBOX_HEIGHT);
		hboxPauseLeft.setAlignment(Pos.CENTER_LEFT);
		StackPane.setMargin(hboxPauseLeft, new Insets(0, 0, PAUSE_LBL_MARGIN, 5));
		
		svgPauseRight = new SVGPath();
		svgPauseRight.setContent(PAUSE_ICON);
		svgPauseRight.getStyleClass().add(CSS_CLASS_PAUSE_SHAPE);
		Pane paneIconRight = new Pane();
		paneIconRight.getChildren().add(svgPauseRight);
		paneIconRight.setPrefSize(PAUSE_ICON_WIDTH, PAUSE_ICON_LENGTH);
		paneIconRight.setMinSize(PAUSE_ICON_WIDTH, PAUSE_ICON_LENGTH);
		paneIconRight.setMaxSize(PAUSE_ICON_WIDTH, PAUSE_ICON_LENGTH);
		lblRight = new Label();
		lblRight.getStyleClass().add(CSS_CLASS_TRANSPORT_LABEL);
		this.getChildren().add(lblRight);
		hboxPauseRight = new HBox();
		hboxPauseRight.getChildren().add(paneIconRight);
		hboxPauseRight.getChildren().add(lblRight);
		hboxPauseRight.setPrefSize(PAUSE_HBOX_WIDTH, PAUSE_HBOX_HEIGHT);
		hboxPauseRight.setMaxSize(PAUSE_HBOX_WIDTH, PAUSE_HBOX_HEIGHT);
		hboxPauseRight.setMinSize(PAUSE_HBOX_WIDTH, PAUSE_HBOX_HEIGHT);
		hboxPauseRight.setAlignment(Pos.CENTER_RIGHT);
		StackPane.setMargin(hboxPauseRight, new Insets(0, 5, PAUSE_LBL_MARGIN, 0));
		
		this.getChildren().add(hboxPauseLeft);
		this.getChildren().add(hboxPauseRight);
		StackPane.setAlignment(hboxPauseLeft, Pos.CENTER_LEFT);
		StackPane.setAlignment(hboxPauseRight, Pos.CENTER_RIGHT);
		
		setLeftPauseActive(false);
		setRightPauseActive(false);
	}
	
	public void setTransportInformation(final TransportInformation transportInfo) {
		this.transportInfo = transportInfo;
		checkTransportInfo();
	}
	
	private void checkTransportInfo() {
		if (transportInfo.getInterventionBeforePick() != null) {
			setLeftPauseActive(true);
			setLeftLabel("" + transportInfo.getInterventionBeforePick().getFrequency());
		}
		if (transportInfo.getInterventionAfterPut() != null) {
			setRightPauseActive(true);
			setRightLabel("" + transportInfo.getInterventionAfterPut().getFrequency());
		}
	}
	
	public TransportInformation getTransportInformation() {
		return transportInfo;
	}
	
	public void setOnAction(final EventHandler<ActionEvent> value) {
		this.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {			
			@Override
			public void handle(final MouseEvent event) {
				value.handle(new ActionEvent());
				event.consume();
			}
		});
	}
	
	public void setLeftPauseActive(final boolean active) {
		hboxPauseLeft.setVisible(active);
	}
	
	public void setLeftLabel(final String text) {
		lblLeft.setText(text);		
	}
	
	public void setRightPauseActive(final boolean active) {
		hboxPauseRight.setVisible(active);
	}
	
	public void setRightLabel(final String text)  {
		lblRight.setText(text);
	}
	
	public void showPause() {
		checkTransportInfo();
	}
	
	public void showPauseLeft() {
		hboxPauseLeft.setVisible(true);
	}
	
	public void showPauseRight() {
		hboxPauseRight.setVisible(true);
	}
	
	public void setFocussed(final boolean active) {
		shapeRegion.getStyleClass().remove("transport-unfocussed");
		hboxPauseLeft.getStyleClass().remove("transport-unfocussed");
		hboxPauseRight.getStyleClass().remove("transport-unfocussed");
		if (!active) {
			shapeRegion.getStyleClass().add("transport-unfocussed");
			hboxPauseLeft.getStyleClass().add("transport-unfocussed");
			hboxPauseRight.getStyleClass().add("transport-unfocussed");
		}
	}
	
	public void setClickable(final boolean clickable) {
		shapeRegion.getStyleClass().remove(CSS_CLASS_UNCLICKABLE);
		hboxPauseLeft.getStyleClass().remove(CSS_CLASS_UNCLICKABLE);
		hboxPauseRight.getStyleClass().remove(CSS_CLASS_UNCLICKABLE);
		if (!clickable) {
			shapeRegion.getStyleClass().add(CSS_CLASS_UNCLICKABLE);
			hboxPauseLeft.getStyleClass().add(CSS_CLASS_UNCLICKABLE);
			hboxPauseRight.getStyleClass().add(CSS_CLASS_UNCLICKABLE);
		}
	}
	
}
