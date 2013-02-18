package eu.robojob.irscw.ui.admin.robot;

import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;

public class RobotAdminView extends HBox {

	private static final double MENU_WIDTH = 200;
	
	private RobotAdminPresenter presenter;
	
	private StackPane menuView;
	private StackPane contentView;
	
	public RobotAdminView() {
		super();
		build();
	}
	
	public void setPresenter(final RobotAdminPresenter presenter) {
		this.presenter = presenter;
	}

	private void build() {
		getChildren().clear();
		menuView = new StackPane();
		menuView.setPrefWidth(MENU_WIDTH);
		getChildren().add(menuView);
		contentView = new StackPane();
		getChildren().add(contentView);
		HBox.setHgrow(contentView, Priority.ALWAYS);
	}
	
	public void setMenuView(final Node menuView) {
		this.menuView.getChildren().clear();
		this.menuView.getChildren().add(menuView);
	}
	
	public void setContentView(final Node contentView) {
		this.contentView.getChildren().clear();
		this.contentView.getChildren().add(contentView);
	}
}
