package eu.robojob.irscw.ui;

import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

public class MainView extends StackPane {
	
	private MainPresenter presenter;
	private BorderPane mainPane;
	
	public MainView() {
		super();
		mainPane = new BorderPane();
		getChildren().add(mainPane);
	}

	public void setHeader(Node header) {
		mainPane.setTop(header);
		header.toFront();
	}
	
	public void setContent(Node content) {
		mainPane.setCenter(content);
		content.toBack();
	}
	
	public void setPresenter(MainPresenter presenter) {
		this.presenter = presenter;
	}
}
