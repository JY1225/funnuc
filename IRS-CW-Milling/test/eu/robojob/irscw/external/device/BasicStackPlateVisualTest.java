package eu.robojob.irscw.external.device;

import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;
import eu.robojob.irscw.external.device.BasicStackPlate.WorkPieceOrientation;
import eu.robojob.irscw.workpiece.WorkPieceDimensions;

public class BasicStackPlateVisualTest extends Application {

	private BasicStackPlate stacker;
	private List<Circle> studPositions;
	
	public BasicStackPlateVisualTest() {
		this.stacker = new BasicStackPlate("basic stacker", 27, 7, 10, 15, 45, 40, 35, 0, 0.25f);
		studPositions = new ArrayList<Circle>();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(Stage stage) throws Exception {
		Group group = new Group();
	
		Rectangle rectangle = new Rectangle(0, 0, (double) stacker.getLength(), (double) stacker.getWidth());
		rectangle.setFill(Color.LIGHTBLUE);
		group.getChildren().add(rectangle);
		
		stacker.configureRawWorkpieces(WorkPieceOrientation.HORIZONTAL, new WorkPieceDimensions(120, 80, 1), 20);
		
		for (StudPosition[] horizontalPositions: stacker.getStudPositions()) {
			for (StudPosition position : horizontalPositions) {
				Circle circle = new Circle(position.getCenterPosition().getX(), position.getCenterPosition().getY(), stacker.getHoleDiameter()/2);
				studPositions.add(circle);
				switch(position.getStudType()) {
					case NONE:
						circle.setFill(Color.GRAY);
						break;
					case NORMAL:
						circle.setRadius(stacker.getStudDiameter()/2);
						circle.setFill(Color.GREEN);
						break;
					default:
						throw new IllegalStateException("unknown stud type");
				}
				group.getChildren().add(circle);
			}
		}
		
		for (StackingPosition stackingPosition: stacker.getRawStackingPositions()) {
			Rectangle wp = new Rectangle(stackingPosition.getPosition().getX() - stackingPosition.getDimensions().getLength()/2, 
					stackingPosition.getPosition().getY()- stackingPosition.getDimensions().getWidth()/2, 
					stackingPosition.getDimensions().getLength(), stackingPosition.getDimensions().getWidth());
			group.getChildren().add(wp);
		}
	
		Scale s = new Scale(570 / rectangle.getWidth(), 300 / rectangle.getHeight());
		Scale t = new Scale(0.85, 0.85);
						
		group.getTransforms().addAll(s);
		
		Scene scene = new Scene(group, 570, 300);
		scene.getStylesheets().addAll("css/general-style.css", "css/header-style.css", "css/keyboard-style.css", "css/configure-style.css", "css/processflow-style.css");

		//scene.setFill(Color.BLUE);
		
		stage.setScene(scene);
		stage.setResizable(false);
		stage.show();
	}

}
