package eu.robojob.irscw.ui;

import javafx.scene.Node;
import javafx.scene.layout.BorderPane;

public class MainView extends BorderPane {
	
	private MainPresenter presenter;
	
	public MainView() {
		this.getStyleClass().add("main");
	}

	public void setHeader(Node header) {
		setTop(header);
	}
	
	public void setContent(Node content) {
		setCenter(content);
	}
	
	public void setPresenter(MainPresenter presenter) {
		this.presenter = presenter;
	}
}
