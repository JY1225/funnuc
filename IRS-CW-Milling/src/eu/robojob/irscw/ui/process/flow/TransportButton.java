package eu.robojob.irscw.ui.process.flow;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.SVGPath;
import javafx.scene.transform.Scale;

public class TransportButton extends Pane {

	private SVGPath arrowShape;
	private SVGPath questionMarkLeft;
	private SVGPath questionMarkRight;
	private SVGPath firstCircle;
			
	private String arrowPath = "M149.028,0c-11.889,0-21.704,8.846-23.24,20.314H99.243L87.579,9.651H75.978 l11.664,10.663H46.693C45.158,8.846,35.343,0,23.455,0C10.5,0,0,10.501,0,23.455s10.5,23.454,23.455,23.454 c11.889,0,21.704-8.846,23.239-20.313h41.138L75.978,37.431h11.602l11.854-10.836h26.355c1.536,11.468,11.352,20.313,23.24,20.313 c12.953,0,23.453-10.5,23.453-23.454S161.981,0,149.028,0z";
	private String questionMarkLeftPath = "M23.728,6.324c-9.445,0-17.101,7.656-17.101,17.101s7.656,17.101,17.101,17.101s17.102-7.656,17.102-17.101 S33.173,6.324,23.728,6.324z M23.319,32.896c-1.272,0-2.136-0.937-2.136-2.185c0-1.296,0.888-2.184,2.136-2.184 c1.296,0,2.137,0.888,2.16,2.184C25.479,31.959,24.64,32.896,23.319,32.896z M26.224,23.942c-0.863,0.984-1.248,1.921-1.225,3.001 v0.432h-3.191l-0.024-0.624c-0.072-1.224,0.336-2.472,1.417-3.745c0.768-0.936,1.392-1.728,1.392-2.52 c0-0.84-0.552-1.416-1.752-1.44c-0.792,0-1.752,0.288-2.376,0.72l-0.816-2.616c0.888-0.504,2.304-0.984,4.008-0.984 c3.167,0,4.632,1.752,4.632,3.744C28.288,21.734,27.136,22.935,26.224,23.942z";
	private String questionMarkRightPath = "M148.978,6.324c-9.445,0-17.102,7.656-17.102,17.101s7.656,17.101,17.102,17.101s17.102-7.656,17.102-17.101 S158.423,6.324,148.978,6.324z M148.569,32.896c-1.271,0-2.136-0.937-2.136-2.185c0-1.296,0.888-2.184,2.136-2.184 c1.297,0,2.137,0.888,2.16,2.184C150.729,31.959,149.89,32.896,148.569,32.896z M151.474,23.942 c-0.863,0.984-1.248,1.921-1.225,3.001v0.432h-3.191l-0.024-0.624c-0.071-1.224,0.337-2.472,1.417-3.745 c0.768-0.936,1.392-1.728,1.392-2.52c0-0.84-0.552-1.416-1.753-1.44c-0.791,0-1.752,0.288-2.375,0.72l-0.816-2.616 c0.888-0.504,2.304-0.984,4.008-0.984c3.168,0,4.633,1.752,4.633,3.744C153.538,21.734,152.386,22.935,151.474,23.942z";
	
	private String firstCirclePath = "M46.909,23.425c0,12.953-10.5,23.454-23.454,23.454 S0,36.378,0,23.425C0,10.472,10.5-0.029,23.455-0.029S46.909,10.472,46.909,23.425z";
	private String secondCirclePath = "M172.432,23.455c0,12.953-10.5,23.454-23.454,23.454 s-23.455-10.501-23.455-23.454c0-12.954,10.5-23.455,23.455-23.455S172.432,10.501,172.432,23.455z";
	
	private boolean showQuestionLeft;
	private boolean showQuestionRight;
	
	
	public TransportButton() {
		super();
		build();
	}
	
	private void build() {
		Scale scale = new Scale(0.75, 0.75);
		arrowShape = new SVGPath();
		arrowShape.setContent(arrowPath);
		arrowShape.getStyleClass().add("arrow-shape");
		arrowShape.getTransforms().add(scale);
		questionMarkLeft = new SVGPath();
		questionMarkLeft.setContent(questionMarkLeftPath);
		questionMarkLeft.getStyleClass().add("question-mark-shape");
		questionMarkLeft.getTransforms().add(scale);
		questionMarkRight = new SVGPath();
		questionMarkRight.setContent(questionMarkRightPath);
		questionMarkRight.getStyleClass().add("question-mark-shape");
		questionMarkRight.getTransforms().add(scale);
		firstCircle = new SVGPath();
		firstCircle.setContent(firstCirclePath);
		firstCircle.getStyleClass().add("finished");
		firstCircle.getTransforms().add(scale);
		this.getChildren().addAll(arrowShape);
	}
	
	public void setOnAction(EventHandler<MouseEvent> value) {
		this.addEventHandler(MouseEvent.MOUSE_CLICKED, value);
	}
	
}
