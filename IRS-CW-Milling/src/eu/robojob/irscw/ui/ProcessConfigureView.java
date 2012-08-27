package eu.robojob.irscw.ui;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class ProcessConfigureView extends VBox {
	
	private StackPane top;
	private VBox bottom;
	
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
		
		top = new StackPane();
		getChildren().add(top);
		
		bottom = new VBox();
		getChildren().add(bottom);
		VBox.setVgrow(bottom, Priority.ALWAYS);
	}

	public void addNodeToTop(Node top) {
		this.top.setPrefWidth(800);
		this.top.getChildren().add(top);
	}
	
	public void removeNode(Node node) {
		this.top.getChildren().remove(node);
	}
	
	public void setBottom(Node bottom) {
		this.bottom.getChildren().clear();
		this.bottom.getChildren().add(bottom);
	}
}
