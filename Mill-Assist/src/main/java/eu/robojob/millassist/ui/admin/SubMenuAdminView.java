package eu.robojob.irscw.ui.admin;

import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import eu.robojob.irscw.ui.admin.robot.RobotAdminPresenter;
import eu.robojob.irscw.ui.general.AbstractFormView;

public class SubMenuAdminView extends HBox {

	private static final double MENU_WIDTH = 150;
	
	private RobotAdminPresenter presenter;
	
	private StackPane menuView;
	private StackPane contentView;
	
	public SubMenuAdminView() {
		super();
		build();
	}
	
	public void setPresenter(final RobotAdminPresenter presenter) {
		this.presenter = presenter;
	}
	
	public RobotAdminPresenter getPresenter() {
		return this.presenter;
	}

	private void build() {
		getChildren().clear();
		menuView = new StackPane();
		menuView.setPrefWidth(MENU_WIDTH);
		menuView.getStyleClass().add("admin-menu");
		getChildren().add(menuView);
		contentView = new StackPane();
		getChildren().add(contentView);
		HBox.setHgrow(contentView, Priority.ALWAYS);
	}
	
	public void setMenuView(final Node menuView) {
		this.menuView.getChildren().clear();
		this.menuView.getChildren().add(menuView);
	}
	
	public void setContentView(final AbstractFormView<?> contentView) {
		this.contentView.getChildren().clear();
		contentView.refresh();
		this.contentView.getChildren().add(contentView);
	}
}
