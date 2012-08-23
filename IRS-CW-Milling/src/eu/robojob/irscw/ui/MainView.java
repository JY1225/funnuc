package eu.robojob.irscw.ui;

import javafx.scene.Node;
import javafx.scene.layout.BorderPane;

public class MainView extends BorderPane {
	
	private MainPresenter presenter;
	
	public MainView() {
	}

	public void setHeader(Node header) {
		setHeader(header);
	}
	
	public void setContent(Node content) {
		setContent(content);
	}
	
	public void setPresenter(MainPresenter presenter) {
		this.presenter = presenter;
	}
}
