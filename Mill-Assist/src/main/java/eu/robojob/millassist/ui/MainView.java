package eu.robojob.millassist.ui;

import java.util.ArrayList;
import java.util.List;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import eu.robojob.millassist.ui.general.PopUpView;
import eu.robojob.millassist.ui.general.dialog.AbstractDialogView;
import eu.robojob.millassist.ui.menu.MenuBarView;

public class MainView extends BorderPane {
	
	private StackPane mainPane;
	private BorderPane contents;
	private MainPresenter presenter;
	private StackPane contentPane;
	
	public MainView() {
		super();
		this.setPrefSize(800, 600);
		this.setMinSize(800, 600);
		this.setMaxSize(800, 600);
		mainPane = new StackPane();
		contents = new BorderPane();
		mainPane.setPrefSize(800, 600);
		mainPane.setMinSize(800, 600);
		mainPane.setMaxSize(800, 600);
		contents.setPrefSize(800, 600);
		contents.setMinSize(800, 600);
		contents.setMaxSize(800, 600);
		this.setCenter(mainPane);
		mainPane.getChildren().add(contents);
		contentPane = new StackPane();
		contents.setCenter(contentPane);
		contentPane.setAlignment(Pos.TOP_LEFT);
		contentPane.toBack();
	}

	public void setMenuBarView(final MenuBarView menuBarView) {
		contents.setTop(menuBarView);
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
	
	public void showDialog(final AbstractDialogView<?> dialog) {
		mainPane.getChildren().add(dialog);
	}
	
	public void hideDialog() {
		List<Node> toRemove = new ArrayList<Node>();
		for (Node node : mainPane.getChildren()) {
			if (node instanceof AbstractDialogView<?>) {
				toRemove.add(node);
			}
		}
		mainPane.getChildren().removeAll(toRemove);
	}
}
