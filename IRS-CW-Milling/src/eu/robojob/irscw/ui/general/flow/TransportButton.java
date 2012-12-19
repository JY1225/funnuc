package eu.robojob.irscw.ui.general.flow;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.SVGPath;
import javafx.scene.transform.Translate;
import eu.robojob.irscw.ui.general.model.TransportInformation;

public class TransportButton extends Pane {

	private SVGPath questionMarkLeft;
	private SVGPath questionMarkRight;
	private SVGPath pauseLeft;
	private SVGPath pauseRight;
	
	private Pane arrowShapePane;
			
	private String questionMarkLeftPath = "M19.544,5.208c-7.78,0-14.086,6.306-14.086,14.086c0,7.78,6.306,14.086,14.086,14.086 c7.78,0,14.087-6.307,14.087-14.086C33.631,11.514,27.325,5.208,19.544,5.208z M19.208,27.095c-1.048,0-1.759-0.771-1.759-1.799 c0-1.068,0.731-1.799,1.759-1.799c1.068,0,1.76,0.731,1.779,1.799C20.988,26.324,20.296,27.095,19.208,27.095z M21.601,19.72 c-0.711,0.811-1.028,1.583-1.009,2.472v0.356h-2.628l-0.02-0.514c-0.06-1.008,0.277-2.036,1.167-3.084 c0.632-0.771,1.146-1.423,1.146-2.076c0-0.692-0.455-1.167-1.443-1.186c-0.652,0-1.443,0.237-1.957,0.593l-0.672-2.155 c0.731-0.415,1.897-0.81,3.302-0.81c2.609,0,3.815,1.443,3.815,3.084C23.301,17.901,22.352,18.89,21.601,19.72z";
	private String questionMarkRightPath = "M122.714,5.208c-7.78,0-14.087,6.306-14.087,14.086c0,7.78,6.307,14.086,14.087,14.086 S136.8,27.074,136.8,19.294C136.8,11.514,130.494,5.208,122.714,5.208z M122.377,27.095c-1.047,0-1.759-0.771-1.759-1.799 c0-1.068,0.731-1.799,1.759-1.799c1.069,0,1.76,0.731,1.78,1.799C124.157,26.324,123.465,27.095,122.377,27.095z M124.77,19.72 c-0.711,0.811-1.028,1.583-1.009,2.472v0.356h-2.629l-0.02-0.514c-0.059-1.008,0.277-2.036,1.167-3.084 c0.632-0.771,1.146-1.423,1.146-2.076c0-0.692-0.454-1.167-1.444-1.186c-0.651,0-1.443,0.237-1.957,0.593l-0.672-2.155 c0.731-0.415,1.898-0.81,3.301-0.81c2.61,0,3.816,1.443,3.816,3.084C126.471,17.901,125.521,18.89,124.77,19.72z";
	
	private String pauzeLeftPath = "M 19.53125 5.09375 C 11.697031 5.09375 5.34375 11.445258 5.34375 19.28125 C 5.34375 27.113696 11.697031 33.46875 19.53125 33.46875 C 27.365469 33.46875 33.71875 27.113696 33.71875 19.28125 C 33.71875 11.445258 27.365469 5.09375 19.53125 5.09375 z M 14.75 12.90625 L 17.9375 12.90625 L 17.9375 25.6875 L 14.75 25.6875 L 14.75 12.90625 z M 21.125 12.90625 L 24.34375 12.90625 L 24.34375 25.6875 L 21.125 25.6875 L 21.125 12.90625 z";
	private String pauzeRightPath = "M 122.71875 5.09375 C 114.88453 5.09375 108.53125 11.445258 108.53125 19.28125 C 108.53125 27.113696 114.88453 33.46875 122.71875 33.46875 C 130.55297 33.46875 136.90625 27.113696 136.90625 19.28125 C 136.90625 11.445258 130.55297 5.09375 122.71875 5.09375 z M 117.9375 12.90625 L 121.125 12.90625 L 121.125 25.6875 L 117.9375 25.6875 L 117.9375 12.90625 z M 124.3125 12.90625 L 127.5 12.90625 L 127.5 25.6875 L 124.3125 25.6875 L 124.3125 12.90625 z";	
	
	private Label lblLeft;
	private Label lblRight;
	
	private static final double WIDTH = 142.275;
	
	private TransportInformation transportInfo;
	
	private EventHandler<MouseEvent> handlerPressed;
	private EventHandler<MouseEvent> handlerReleased;
	
	public TransportButton(final TransportInformation transportInfo) {
		build();
		setTransportInformation(transportInfo);
		setFocused(true);
	}
	
	private void build() {
		arrowShapePane = new Pane();
		arrowShapePane.getStyleClass().add("arrowp");
		
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
				
		arrowShapePane.setPrefSize(142.275, 38.838);
		this.getChildren().add(arrowShapePane);

		Pane arrowPane = new Pane();
		SVGPath arrowShape = new SVGPath();
		arrowShape.setContent("M 19.3125 0 C 8.6424224 0 0 8.6771304 0 19.34375 C 0 30.010371 8.6424224 38.625 19.3125 38.625 C 29.103653 38.625 37.172612 31.349258 38.4375 21.90625 L 72.34375 21.90625 L 62.5625 30.84375 L 72.125 30.84375 L 81.90625 21.90625 L 103.59375 21.90625 C 104.85965 31.349258 112.95785 38.625 122.75 38.625 C 133.41809 38.625 142.09375 30.01137 142.09375 19.34375 C 142.09275 8.6771304 133.41808 0 122.75 0 C 112.95784 0 104.85965 7.3069923 103.59375 16.75 L 81.75 16.75 L 72.125 7.96875 L 62.5625 7.96875 L 72.1875 16.75 L 38.4375 16.75 C 37.172612 7.305993 29.103653 0 19.3125 0 z");
		arrowShape.getStyleClass().add("arrow-shape");
		SVGPath arrowShine = new SVGPath();
		arrowShine.setContent("M 19.25 0.6875 C 8.5787329 0.6875 -0.03125 9.0174716 -0.03125 19.28125 C -0.03125 19.420425 -0.00315433 19.54906 0 19.6875 C 0.22970865 9.6154873 8.7234327 1.5 19.25 1.5 C 29.042246 1.5 37.141219 8.5064108 38.40625 17.59375 L 72.15625 17.59375 L 71.21875 16.78125 L 38.40625 16.78125 C 37.141219 7.693912 29.042246 0.6875 19.25 0.6875 z M 122.71875 0.6875 C 112.9255 0.6875 104.82854 7.6948735 103.5625 16.78125 L 81.6875 16.78125 L 72.09375 8.34375 L 62.53125 8.34375 L 63.46875 9.15625 L 72.09375 9.15625 L 81.6875 17.59375 L 103.5625 17.59375 C 104.82854 8.5073723 112.9255 1.5 122.71875 1.5 C 133.24335 1.5 141.76846 9.6154873 142 19.6875 C 142.003 19.549047 142.03125 19.420438 142.03125 19.28125 C 142.03035 9.0174716 133.38802 0.6875 122.71875 0.6875 z");
		arrowShine.getStyleClass().add("arrow-shine");
		arrowPane.getChildren().add(arrowShape);
		arrowPane.getChildren().add(arrowShine);
		arrowShapePane.getChildren().add(arrowPane);
		
		this.getStyleClass().add("transport-button");
		
		this.setPrefWidth(WIDTH);
		this.setMaxHeight(38.838);
				
		handlerPressed = new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				TransportButton.this.requestFocus();
				arrowShapePane.getStyleClass().remove("arrow-clicked");
				arrowShapePane.getStyleClass().add("arrow-clicked");
				event.consume();
			}
		};
		
		handlerReleased = new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				arrowShapePane.getStyleClass().remove("arrow-clicked");	
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
		this.getStyleClass().remove("transport-unfocussed");
		if (!active) {
			this.getStyleClass().add("transport-unfocussed");
		}
	}
	
	public void setClickable(boolean clickable) {
		this.removeEventHandler(MouseEvent.MOUSE_PRESSED, handlerPressed);
		this.removeEventHandler(MouseEvent.MOUSE_RELEASED, handlerReleased);
		arrowShapePane.getStyleClass().remove("arrow-noclick");
		if (clickable) {
			this.setEventHandler(MouseEvent.MOUSE_PRESSED, handlerPressed);
			this.setEventHandler(MouseEvent.MOUSE_RELEASED, handlerReleased);
		} else {
			arrowShapePane.getStyleClass().add("arrow-noclick");
		}
	}
	
}
