package eu.robojob.irscw.ui;

import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class ProcessConfigureView extends VBox {
	
	private Pane top;
	private Pane bottom;
	
	private ProcessConfigurePresenter presenter;
	
	public ProcessConfigureView () {
		buildView();
	}
	
	public void setPresenter(ProcessConfigurePresenter presenter) {
		this.presenter = presenter;
	}
	
	protected void buildView() {
		top = new Pane();
		getChildren().add(top);
		setVgrow(top, Priority.ALWAYS);
		
		bottom = new Pane();
		getChildren().add(bottom);
		setVgrow(bottom, Priority.ALWAYS);
	}

	public void setTop(Node top) {
		this.top.getChildren().add(top);
	}
	
	public void setBottom(Node bottom) {
		this.bottom.getChildren().add(bottom);
	}
}
