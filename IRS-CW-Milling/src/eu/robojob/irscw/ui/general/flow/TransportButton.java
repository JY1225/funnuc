package eu.robojob.irscw.ui.general.flow;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import eu.robojob.irscw.ui.general.model.TransportInformation;

public class TransportButton extends StackPane {

	private Region shapeRegion;
	private Label lblLeft;
	private Label lblRight;
	
	public static final double WIDTH = 120;
	private static final double SHAPE_HEIGHT = 7;
	
	private TransportInformation transportInfo;
	
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
		this.getChildren().add(shapeRegion);
		this.setPrefWidth(WIDTH);
		this.setMinWidth(WIDTH);
				
		handlerPressed = new EventHandler<MouseEvent>() {
			@Override
			public void handle(final MouseEvent event) {
				TransportButton.this.requestFocus();
				shapeRegion.getStyleClass().remove("arrow-clicked");
				shapeRegion.getStyleClass().add("arrow-clicked");
				event.consume();
			}
		};
		
		handlerReleased = new EventHandler<MouseEvent>() {
			@Override
			public void handle(final MouseEvent event) {
				shapeRegion.getStyleClass().remove("arrow-clicked");	
				event.consume();
			}
		};
		
		this.addEventHandler(MouseEvent.MOUSE_PRESSED, handlerPressed);
		this.addEventHandler(MouseEvent.MOUSE_RELEASED, handlerReleased);
		
	}
	
	public void setTransportInformation(final TransportInformation transportInfo) {
		// TODO this logic should be contained in the steps
		this.transportInfo = transportInfo;
		
		if (transportInfo.getPickStep().getDeviceSettings() != null) {
			if (transportInfo.getPickStep().needsTeaching()) {
				setLeftQuestionMarkActive(true);
			} else {
				setLeftQuestionMarkActive(false);
			}
		}
		if (transportInfo.getPutStep().getDeviceSettings() != null) {
			if (transportInfo.getPutStep().needsTeaching()) {
				setRightQuestionMarkActive(true);
			} else {
				setRightQuestionMarkActive(false);
			}
		}
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
	
	public void setLeftQuestionMarkActive(final boolean active) {
		
	}
	
	public void setRightQuestionMarkActive(final boolean active) {
		
	}
	
	public void setLeftPauseActive(final boolean active) {
		
	}
	
	public void setLeftLabel(final String text) {
		
		
	}
	
	public void setRightPauseActive(final boolean active) {
		
	}
	
	public void setRightLabel(final String text)  {
		
	}
	
	public void showPause() {
		showPauseLeft();
		showPauseRight();
	}
	
	public void showTeach() {
		showQuestionMarkLeft();
		showQuestionMarkRight();
	}
	
	public void showPauseLeft() {
		
	}
	
	public void showPauseRight() {
		
	}
	
	public void showQuestionMarkLeft() {
		
	}
	
	public void showQuestionMarkRight() {
		
	}
	
	public void setFocussed(final boolean active) {
		this.getStyleClass().remove("transport-unfocussed");
		if (!active) {
			this.getStyleClass().add("transport-unfocussed");
		}
	}
	
	public void setClickable(final boolean clickable) {
		this.removeEventHandler(MouseEvent.MOUSE_PRESSED, handlerPressed);
		this.removeEventHandler(MouseEvent.MOUSE_RELEASED, handlerReleased);
		if (clickable) {
			this.setEventHandler(MouseEvent.MOUSE_PRESSED, handlerPressed);
			this.setEventHandler(MouseEvent.MOUSE_RELEASED, handlerReleased);
		} else {
		}
	}
	
}
