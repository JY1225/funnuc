package eu.robojob.irscw.ui.process.flow;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

public class ProcessFlowTransportButton extends StackPane {
	
	private Button arrow;
	
	private Label pauseBefore;
	private Label pauseAfter;
	
	private static int HEIGHT = 40;
	private static int WIDTH = 145;
	
	public ProcessFlowTransportButton() {
		buildView();
	}
	
	private void buildView() {
		arrow = new Button();
		arrow.getStyleClass().add("arrow-rightQ");
		arrow.setPrefSize(WIDTH, HEIGHT);
		this.getChildren().add(arrow);
		pauseBefore = new Label();
		pauseBefore.getStyleClass().add("pauze-label");
		this.getChildren().add(pauseBefore);
		pauseAfter = new Label();
		pauseAfter.getStyleClass().add("pauze-label");
		this.getChildren().add(pauseAfter);
	}

}
