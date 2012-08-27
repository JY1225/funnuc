package eu.robojob.irscw.ui;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class ProcessConfigureView extends VBox {
	
	private GridPane top;
	private GridPane bottom;
	
	private ProcessConfigurePresenter presenter;
	
	public ProcessConfigureView () {
		buildView();
	}
	
	public void setPresenter(ProcessConfigurePresenter presenter) {
		this.presenter = presenter;
	}
	
	protected void buildView() {
		this.setFillWidth(true);
		this.setAlignment(Pos.CENTER);
		
		top = new GridPane();
		getChildren().add(top);
		
		bottom = new GridPane();
		getChildren().add(bottom);
		VBox.setVgrow(bottom, Priority.ALWAYS);
	}

	public void setTop(Node top) {
		this.top.setPrefWidth(800);
		this.top.getStyleClass().add("yellow");
		this.top.getChildren().clear();
		this.top.getChildren().add(top);
	}
	
	public void setBottom(Node bottom) {
		this.bottom.getChildren().clear();
		this.bottom.getChildren().clear();
		this.bottom.getChildren().add(bottom);
	}
}
