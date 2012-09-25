package eu.robojob.irscw.external.device;

import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;

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
		Pane pane = new Pane();
		pane.getStyleClass().add("yellow");
	
		Rectangle rectangle = new Rectangle(0, 0, (double) stacker.getLength(), (double) stacker.getWidth());
		rectangle.setFill(Color.LIGHTBLUE);
		pane.getChildren().add(rectangle);
		
		for (int i = 0; i < stacker.getVerticalHoleAmount(); i++) {
			float verticalPosition = i * stacker.getVerticalHoleDistance() + stacker.getVerticalPadding();
			for (int j = 0; j < stacker.getHorizontalHoleAmount(); j++) {
				float horizontalPosition = j * stacker.getHorizontalHoleDistance() + stacker.getHorizontalPadding();
				Circle circle = new Circle(horizontalPosition, verticalPosition, stacker.getStudDiameter()/2);
				studPositions.add(circle);
				pane.getChildren().add(circle);
			}
		}
	
		Scale s = new Scale(570 / rectangle.getWidth(), 300 / rectangle.getHeight());
		Scale t = new Scale(0.85, 0.85);
		pane.getTransforms().addAll(s, t);
		
		Scene scene = new Scene(pane, 570, 300);
		scene.getStylesheets().addAll("css/general-style.css", "css/header-style.css", "css/keyboard-style.css", "css/configure-style.css", "css/processflow-style.css");

		scene.setFill(Color.BLUE);
		
		stage.setScene(scene);
		stage.setResizable(false);
		stage.show();
	}

}
