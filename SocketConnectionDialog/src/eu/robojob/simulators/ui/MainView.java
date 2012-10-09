package eu.robojob.simulators.ui;

import javafx.scene.Node;
import javafx.scene.layout.BorderPane;

public class MainView extends BorderPane {

	private MainPresenter presenter;
	
	public MainView() {
		super();
		setMinSize(640, 480);
	}
	
	public void setPresenter(MainPresenter presenter) {
		this.presenter = presenter;
	}
	
	public void setCenterView(Node node) {
		this.getChildren().clear();
		this.setCenter(node);
		this.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
	}
	
	public MainPresenter getPresenter() {
		return presenter;
	}
}
