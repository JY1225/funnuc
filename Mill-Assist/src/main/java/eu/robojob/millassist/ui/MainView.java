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
import eu.robojob.millassist.util.SizeManager;

public class MainView extends BorderPane {
	
	private StackPane mainPane;
	private BorderPane contents;
	private MainPresenter presenter;
	private StackPane contentPane;
	
	public MainView() {
		super();
		this.setPrefSize(SizeManager.WIDTH, SizeManager.HEIGHT);
		this.setMinSize(SizeManager.WIDTH, SizeManager.HEIGHT);
		this.setMaxSize(SizeManager.WIDTH, SizeManager.HEIGHT);
		mainPane = new StackPane();
		contents = new BorderPane();
		mainPane.setPrefSize(SizeManager.WIDTH, SizeManager.HEIGHT);
		mainPane.setMinSize(SizeManager.WIDTH, SizeManager.HEIGHT);
		mainPane.setMaxSize(SizeManager.WIDTH, SizeManager.HEIGHT);
		contents.setPrefSize(SizeManager.WIDTH, SizeManager.HEIGHT);
		contents.setMinSize(SizeManager.WIDTH, SizeManager.HEIGHT);
		contents.setMaxSize(SizeManager.WIDTH, SizeManager.HEIGHT);
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
