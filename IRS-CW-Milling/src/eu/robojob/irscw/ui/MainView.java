package eu.robojob.irscw.ui;

import java.util.ArrayList;
import java.util.List;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

public class MainView extends StackPane {
	
	private MainPresenter presenter;
	private BorderPane mainPane;
	private StackPane contentPane;
	
	public MainView() {
		super();
		mainPane = new BorderPane();
		getChildren().add(mainPane);
		contentPane = new StackPane();
		mainPane.setCenter(contentPane);
		contentPane.setAlignment(Pos.TOP_LEFT);
		contentPane.toBack();
	}

	public void setHeader(Node header) {
		mainPane.setTop(header);
		header.toFront();
	}
	
	public void setContent(Node content) {
		contentPane.getChildren().clear();
		contentPane.getChildren().add(content);
	}
	
	public void addPopup(Node popup) {
		contentPane.getChildren().add(popup);
		popup.requestFocus();
	}
	
	public void closePopup(Node popup) {
		contentPane.getChildren().remove(popup);
	}
	
	public void closePopup() {
		List<Node> toRemove = new ArrayList<Node>();
		for (Node node : contentPane.getChildren()) {
			if (node instanceof PopUpView) {
				toRemove.add(node);
			}
		}
		contentPane.getChildren().removeAll(toRemove);
	}
	
	public void setPresenter(MainPresenter presenter) {
		this.presenter = presenter;
	}
	
	public MainPresenter getPresenter() {
		return presenter;
	}
}
