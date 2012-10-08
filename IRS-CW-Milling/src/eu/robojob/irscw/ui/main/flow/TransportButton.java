package eu.robojob.irscw.ui.main.flow;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.shape.SVGPath;
import javafx.scene.transform.Translate;
import eu.robojob.irscw.ui.main.model.TransportInformation;

public class TransportButton extends Pane {

	private SVGPath questionMarkLeft;
	private SVGPath questionMarkRight;
	private SVGPath pauseLeft;
	private SVGPath pauseRight;
	
	private Region arrowRegion;
			
	private String arrowPath = "M122.756-0.001c-9.793,0-17.878,7.286-19.144,16.732H81.748L72.14,7.949h-9.557l9.608,8.783H38.462 C37.197,7.285,29.112-0.001,19.32-0.001C8.649-0.001,0,8.648,0,19.318c0,10.67,8.649,19.319,19.32,19.319 c9.792,0,17.877-7.286,19.142-16.732h33.885l-9.764,8.926h9.557l9.764-8.926h21.709c1.266,9.446,9.351,16.732,19.144,16.732 c10.669,0,19.318-8.648,19.318-19.319C142.074,8.648,133.425-0.001,122.756-0.001z";
	private String questionMarkLeftPath = "M19.544,5.208c-7.78,0-14.086,6.306-14.086,14.086c0,7.78,6.306,14.086,14.086,14.086 c7.78,0,14.087-6.307,14.087-14.086C33.631,11.514,27.325,5.208,19.544,5.208z M19.208,27.095c-1.048,0-1.759-0.771-1.759-1.799 c0-1.068,0.731-1.799,1.759-1.799c1.068,0,1.76,0.731,1.779,1.799C20.988,26.324,20.296,27.095,19.208,27.095z M21.601,19.72 c-0.711,0.811-1.028,1.583-1.009,2.472v0.356h-2.628l-0.02-0.514c-0.06-1.008,0.277-2.036,1.167-3.084 c0.632-0.771,1.146-1.423,1.146-2.076c0-0.692-0.455-1.167-1.443-1.186c-0.652,0-1.443,0.237-1.957,0.593l-0.672-2.155 c0.731-0.415,1.897-0.81,3.302-0.81c2.609,0,3.815,1.443,3.815,3.084C23.301,17.901,22.352,18.89,21.601,19.72z";
	private String questionMarkRightPath = "M122.714,5.208c-7.78,0-14.087,6.306-14.087,14.086c0,7.78,6.307,14.086,14.087,14.086 S136.8,27.074,136.8,19.294C136.8,11.514,130.494,5.208,122.714,5.208z M122.377,27.095c-1.047,0-1.759-0.771-1.759-1.799 c0-1.068,0.731-1.799,1.759-1.799c1.069,0,1.76,0.731,1.78,1.799C124.157,26.324,123.465,27.095,122.377,27.095z M124.77,19.72 c-0.711,0.811-1.028,1.583-1.009,2.472v0.356h-2.629l-0.02-0.514c-0.059-1.008,0.277-2.036,1.167-3.084 c0.632-0.771,1.146-1.423,1.146-2.076c0-0.692-0.454-1.167-1.444-1.186c-0.651,0-1.443,0.237-1.957,0.593l-0.672-2.155 c0.731-0.415,1.898-0.81,3.301-0.81c2.61,0,3.816,1.443,3.816,3.084C126.471,17.901,125.521,18.89,124.77,19.72z";
	
	private String pauzeLeftPath = "M 19.53125 5.09375 C 11.697031 5.09375 5.34375 11.445258 5.34375 19.28125 C 5.34375 27.113696 11.697031 33.46875 19.53125 33.46875 C 27.365469 33.46875 33.71875 27.113696 33.71875 19.28125 C 33.71875 11.445258 27.365469 5.09375 19.53125 5.09375 z M 14.75 12.90625 L 17.9375 12.90625 L 17.9375 25.6875 L 14.75 25.6875 L 14.75 12.90625 z M 21.125 12.90625 L 24.34375 12.90625 L 24.34375 25.6875 L 21.125 25.6875 L 21.125 12.90625 z";
	private String pauzeRightPath = "M 122.71875 5.09375 C 114.88453 5.09375 108.53125 11.445258 108.53125 19.28125 C 108.53125 27.113696 114.88453 33.46875 122.71875 33.46875 C 130.55297 33.46875 136.90625 27.113696 136.90625 19.28125 C 136.90625 11.445258 130.55297 5.09375 122.71875 5.09375 z M 117.9375 12.90625 L 121.125 12.90625 L 121.125 25.6875 L 117.9375 25.6875 L 117.9375 12.90625 z M 124.3125 12.90625 L 127.5 12.90625 L 127.5 25.6875 L 124.3125 25.6875 L 124.3125 12.90625 z";	
	
	private String firstCirclePath = "M38.82,19.318c0,10.645-8.629,19.275-19.275,19.275c-10.646,0-19.275-8.63-19.275-19.275 S8.899,0.042,19.545,0.042C30.191,0.042,38.82,8.673,38.82,19.318z";
	/*private String secondCirclePath = "M141.989,19.294c0,10.645-8.629,19.275-19.275,19.275c-10.646,0-19.275-8.63-19.275-19.275 s8.63-19.275,19.275-19.275C133.36,0.019,141.989,8.649,141.989,19.294z";*/
	
	private Label lblLeft;
	private Label lblRight;
	
	private static final double WIDTH = 142.275;
	
	private TransportInformation transportInfo;
	
	private EventHandler<MouseEvent> handlerPressed;
	private EventHandler<MouseEvent> handlerReleased;
	
	public TransportButton(TransportInformation transportInfo) {
		super();
		
		build();
		
		setTransportInformation(transportInfo);
		setFocused(true);
	}
	
	private void build() {
	
		arrowRegion = new Region();
		arrowRegion.getStyleClass().add("arrowp");
		
		questionMarkLeft = new SVGPath();
		questionMarkLeft.setContent(questionMarkLeftPath);
		questionMarkLeft.getStyleClass().add("question-mark-shape");
		
		questionMarkRight = new SVGPath();
		questionMarkRight.setContent(questionMarkRightPath);
		questionMarkRight.getStyleClass().add("question-mark-shape");
		
		pauseLeft = new SVGPath();
		pauseLeft.setContent(pauzeLeftPath);
		pauseLeft.getStyleClass().add("pause-shape");
		
		lblLeft = new Label();
		lblLeft.getStyleClass().add("transport-label");
		lblLeft.setPrefWidth(28.373);
		Translate translate = new Translate(5.358, -25);
		lblLeft.getTransforms().add(translate);
		lblRight = new Label();
		lblRight.getStyleClass().add("transport-label");
		Translate translate2 = new Translate(108.527, -25);
		lblRight.getTransforms().add(translate2);
		lblRight.setPrefWidth(28.373);
		
		pauseRight = new SVGPath();
		pauseRight.setContent(pauzeRightPath);
		pauseRight.getStyleClass().add("pause-shape");
		
		//this.getChildren().addAll(arrowShape);
		arrowRegion.setPrefSize(142.275, 38.838);
		this.getChildren().add(arrowRegion);
		
		this.getStyleClass().add("transport-button");
		
		this.setPrefWidth(WIDTH);
		this.setMaxHeight(38.838);
				
		handlerPressed = new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				arrowRegion.getStyleClass().remove("arrow-clicked");
				arrowRegion.getStyleClass().add("arrow-clicked");
				event.consume();
			}
		};
		
		handlerReleased = new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				arrowRegion.getStyleClass().remove("arrow-clicked");	
				event.consume();
			}
		};
		
		this.addEventHandler(MouseEvent.MOUSE_PRESSED, handlerPressed);
		this.addEventHandler(MouseEvent.MOUSE_RELEASED, handlerReleased);
		
	}
	
	public void setTransportInformation(TransportInformation transportInfo) {
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
			public void handle(MouseEvent event) {
				value.handle(new ActionEvent());
				event.consume();
			}
		});
	}
	
	public void setLeftQuestionMarkActive(boolean active) {
		this.getChildren().remove(questionMarkLeft);
		if (active) {
			this.getChildren().add(questionMarkLeft);
			questionMarkLeft.toBack();
		}
	}
	
	public void setRightQuestionMarkActive(boolean active) {
		this.getChildren().remove(questionMarkRight);
		if (active) {
			this.getChildren().add(questionMarkRight);
			questionMarkRight.toBack();
		}
	}
	
	public void setLeftPauseActive(boolean active) {
		this.getChildren().remove(pauseLeft);
		if (active) {
			this.getChildren().add(pauseLeft);
			pauseLeft.toBack();
		}
	}
	
	public void setLeftLabel(String text) {
		this.getChildren().remove(lblLeft);
		if (text != null) {
			lblLeft.setText(text);
			this.getChildren().add(lblLeft);
		}
		
	}
	
	public void setRightPauseActive(boolean active) {
		this.getChildren().remove(pauseRight);
		if (active) {
			this.getChildren().add(pauseRight);
			pauseRight.toBack();
		}
	}
	
	public void setRightLabel(String text)  {
		this.getChildren().remove(lblRight);
		if (text != null) {
			lblRight.setText(text);
			this.getChildren().add(lblRight);
		}
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
		questionMarkLeft.toBack();
		pauseLeft.toFront();
		lblLeft.getStyleClass().remove("hidden");
	}
	
	public void showPauseRight() {
		questionMarkRight.toBack();
		pauseRight.toFront();
		lblRight.getStyleClass().remove("hidden");
	}
	
	public void showQuestionMarkLeft() {
		pauseLeft.toBack();
		questionMarkLeft.toFront();
		lblLeft.getStyleClass().remove("hidden");
		lblLeft.getStyleClass().add("hidden");
	}
	
	public void showQuestionMarkRight() {
		pauseRight.toBack();
		questionMarkRight.toFront();
		lblRight.getStyleClass().remove("hidden");
		lblRight.getStyleClass().add("hidden");
	}
	
	public void setFocussed(boolean active) {
		arrowRegion.getStyleClass().remove("arrow-disabled");
		lblLeft.getStyleClass().remove("transport-label-disabled");
		lblRight.getStyleClass().remove("transport-label-disabled");
		if (active) {
		} else {
			arrowRegion.getStyleClass().add("arrow-disabled");
			lblLeft.getStyleClass().add("transport-label-disabled");
			lblRight.getStyleClass().add("transport-label-disabled");
		}
	}
	
	public void setClickable(boolean clickable) {
		this.removeEventHandler(MouseEvent.MOUSE_PRESSED, handlerPressed);
		this.removeEventHandler(MouseEvent.MOUSE_RELEASED, handlerReleased);
		arrowRegion.getStyleClass().remove("arrow-noclick");
		if (clickable) {
			this.setEventHandler(MouseEvent.MOUSE_PRESSED, handlerPressed);
			this.setEventHandler(MouseEvent.MOUSE_RELEASED, handlerReleased);
		} else {
			arrowRegion.getStyleClass().add("arrow-noclick");
		}
	}
	
}
