package eu.robojob.millassist.ui;

import java.util.ArrayList;
import java.util.List;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import eu.robojob.millassist.ui.general.PopUpView;
import eu.robojob.millassist.ui.menu.MenuBarView;

public class MainView extends BorderPane {
	
	private MainPresenter presenter;
	private StackPane contentPane;
	
	public MainView() {
		super();
		this.setPrefSize(800, 600);
		this.setMinSize(800, 600);
		this.setMaxSize(800, 600);
		contentPane = new StackPane();
		this.setCenter(contentPane);
		contentPane.setAlignment(Pos.TOP_LEFT);
		contentPane.toBack();
	}

	public void setMenuBarView(final MenuBarView menuBarView) {
		this.setTop(menuBarView);
		menuBarView.toFront();
	}
	
	public void setContentView(final Node contentView) {
		contentPane.getChildren().clear();
		contentPane.getChildren().add(contentView);
	}
	
	public void addPopUpView(final PopUpView<?> pupUpView) {
		contentPane.getChildren().add(pupUpView);
		pupUpView.requestFocus();
	}
	
	public void closePopup(final PopUpView<?> pupUpView) {
		contentPane.getChildren().remove(pupUpView);
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
	
	public void setPresenter(final MainPresenter presenter) {
		this.presenter = presenter;
	}
	
	public MainPresenter getPresenter() {
		return presenter;
	}
}
