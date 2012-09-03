package eu.robojob.irscw.ui.process.flow;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.SVGPath;
import eu.robojob.irscw.external.device.AbstractDevice;
import eu.robojob.irscw.external.device.AbstractProcessingDevice;
import eu.robojob.irscw.ui.process.model.TransportInformation;

public class TransportButton extends Pane {

	private SVGPath arrowShape;
	private SVGPath questionMarkLeft;
	private SVGPath questionMarkRight;
	private SVGPath firstCircle;
	private SVGPath effect; 
			
	private String arrowPath = "M122.756-0.001c-9.793,0-17.878,7.286-19.144,16.732H81.748L72.14,7.949h-9.557l9.608,8.783H38.462 C37.197,7.285,29.112-0.001,19.32-0.001C8.649-0.001,0,8.648,0,19.318c0,10.67,8.649,19.319,19.32,19.319 c9.792,0,17.877-7.286,19.142-16.732h33.885l-9.764,8.926h9.557l9.764-8.926h21.709c1.266,9.446,9.351,16.732,19.144,16.732 c10.669,0,19.318-8.648,19.318-19.319C142.074,8.648,133.425-0.001,122.756-0.001z";
	private String questionMarkLeftPath = "M19.544,5.208c-7.78,0-14.086,6.306-14.086,14.086c0,7.78,6.306,14.086,14.086,14.086 c7.78,0,14.087-6.307,14.087-14.086C33.631,11.514,27.325,5.208,19.544,5.208z M19.208,27.095c-1.048,0-1.759-0.771-1.759-1.799 c0-1.068,0.731-1.799,1.759-1.799c1.068,0,1.76,0.731,1.779,1.799C20.988,26.324,20.296,27.095,19.208,27.095z M21.601,19.72 c-0.711,0.811-1.028,1.583-1.009,2.472v0.356h-2.628l-0.02-0.514c-0.06-1.008,0.277-2.036,1.167-3.084 c0.632-0.771,1.146-1.423,1.146-2.076c0-0.692-0.455-1.167-1.443-1.186c-0.652,0-1.443,0.237-1.957,0.593l-0.672-2.155 c0.731-0.415,1.897-0.81,3.302-0.81c2.609,0,3.815,1.443,3.815,3.084C23.301,17.901,22.352,18.89,21.601,19.72z";
	private String questionMarkRightPath = "M122.714,5.208c-7.78,0-14.087,6.306-14.087,14.086c0,7.78,6.307,14.086,14.087,14.086 S136.8,27.074,136.8,19.294C136.8,11.514,130.494,5.208,122.714,5.208z M122.377,27.095c-1.047,0-1.759-0.771-1.759-1.799 c0-1.068,0.731-1.799,1.759-1.799c1.069,0,1.76,0.731,1.78,1.799C124.157,26.324,123.465,27.095,122.377,27.095z M124.77,19.72 c-0.711,0.811-1.028,1.583-1.009,2.472v0.356h-2.629l-0.02-0.514c-0.059-1.008,0.277-2.036,1.167-3.084 c0.632-0.771,1.146-1.423,1.146-2.076c0-0.692-0.454-1.167-1.444-1.186c-0.651,0-1.443,0.237-1.957,0.593l-0.672-2.155 c0.731-0.415,1.898-0.81,3.301-0.81c2.61,0,3.816,1.443,3.816,3.084C126.471,17.901,125.521,18.89,124.77,19.72z";
	
	private String firstCirclePath = "M38.82,19.318c0,10.645-8.629,19.275-19.275,19.275c-10.646,0-19.275-8.63-19.275-19.275 S8.899,0.042,19.545,0.042C30.191,0.042,38.82,8.673,38.82,19.318z";
	private String secondCirclePath = "M141.989,19.294c0,10.645-8.629,19.275-19.275,19.275c-10.646,0-19.275-8.63-19.275-19.275 s8.63-19.275,19.275-19.275C133.36,0.019,141.989,8.649,141.989,19.294z";
	
	private static final double WIDTH = 142.275;
	
	private TransportInformation transportInfo;
	
	public TransportButton(TransportInformation transportInfo) {
		super();
		
		build();
		
		setTransportInformation(transportInfo);
		setEnabled(true);
	}
	
	private void build() {
		arrowShape = new SVGPath();
		arrowShape.setContent(arrowPath);
		arrowShape.getStyleClass().add("arrow-background");
		
		questionMarkLeft = new SVGPath();
		questionMarkLeft.setContent(questionMarkLeftPath);
		questionMarkLeft.getStyleClass().add("question-mark-shape");
		
		questionMarkRight = new SVGPath();
		questionMarkRight.setContent(questionMarkRightPath);
		questionMarkRight.getStyleClass().add("question-mark-shape");
		
		firstCircle = new SVGPath();
		firstCircle.setContent(firstCirclePath);
		firstCircle.getStyleClass().add("arrow-shape-active");
		
		this.getChildren().addAll(arrowShape);
		
		effect = new SVGPath();
		effect.setContent(arrowPath);
		effect.getStyleClass().add("arrow-effect");
		this.getChildren().add(effect);
		effect.toFront();
		
		this.getStyleClass().add("transport-button");
		
		this.setPrefWidth(WIDTH);
		
		this.setEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				effect.getStyleClass().remove("clicked");
				effect.getStyleClass().add("clicked");
			}
		});
		
		this.setEventHandler(MouseEvent.MOUSE_RELEASED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				effect.getStyleClass().remove("clicked");			
			}
		});
		
	}
	
	public void setTransportInformation(TransportInformation transportInfo) {
		this.transportInfo = transportInfo;
		if (transportInfo.getPickStep().getProcessFlow().needsTeaching()) {
			AbstractDevice device = transportInfo.getPickStep().getDevice();
			if (device instanceof AbstractProcessingDevice) {
				AbstractProcessingDevice procDevice = (AbstractProcessingDevice) device;
				if (procDevice.isInvasive()) {
					setLeftQuestionMarkActive(true);
				}
			}
		}
		
	}
	
	public void setOnAction(final EventHandler<ActionEvent> value) {
		this.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {			
			@Override
			public void handle(MouseEvent event) {
				value.handle(new ActionEvent());
			}
		});
	}
	
	public void setLeftQuestionMarkActive(boolean active) {
		this.getChildren().remove(questionMarkLeft);
		if (active) {
			this.getChildren().add(questionMarkLeft);
		}
	}
	
	public void setRightQuestionMarkActive(boolean active) {
		this.getChildren().remove(questionMarkRight);
		if (active) {
			this.getChildren().add(questionMarkRight);
		}
	}
	
	public void setEnabled(boolean active) {
		effect.getStyleClass().remove("arrow-disabled");
		arrowShape.getStyleClass().remove("arrow-background-disabled");
		if (active) {
		} else {
			effect.getStyleClass().add("arrow-disabled");
			arrowShape.getStyleClass().add("arrow-background-disabled");
		}
	}
	
	public void setFirstPartActive() {
		effect.getStyleClass().remove("arrow-disabled");
		effect.getStyleClass().add("arrow-disabled");
		this.getChildren().remove(firstCircle);
		this.getChildren().add(firstCircle);
	}
	
}
