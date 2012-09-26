package eu.robojob.irscw.ui.main.configure;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import eu.robojob.irscw.util.Translator;

public abstract class AbstractMenuView<T extends AbstractMenuPresenter<?>> extends VBox {

	protected T presenter;
	
	protected Translator translator = Translator.getInstance();
	
	private static String arrowRightPath = "M 1.6875 0 L 0 1.65625 L 3.375 5 L 0.0625 8.3125 L 1.71875 10 L 6.65625 5.0625 L 6.5625 4.96875 L 6.625 4.90625 L 1.6875 0 z ";
	
	private static int BUTTON_WIDTH = 210;
	private static int BUTTON_HEIGHT = 45;
	
	private static int ICON_WIDTH = 20;
	private static int ICON_MARGIN = 6;
	private static int ICON_ARROW_WIDTH = 10;
	
	public AbstractMenuView() {
		super();
		setAlignment(Pos.TOP_CENTER);
		setPadding(new Insets(15, 0, 0, 0));
		this.getStyleClass().add("left-menu");
	}
	
	public void setPresenter(T presenter) {
		this.presenter = presenter;
	}
	
	protected abstract void build();
	
	protected void addMenuItem(int index, String iconPath, String text, boolean isRightNav, EventHandler<ActionEvent> clickedEventHandler) {
		if ((index < 0) || (index > getChildren().size()) || ((getChildren().size() > 0) && (index < getChildren().size() -1) && getChildren().get(index) != null)) {
			throw new IllegalArgumentException("Wrong index value!: "  + index);
		}
		Button button = new Button();
		HBox hbox = new HBox();
		StackPane iconPane = new StackPane();
		SVGPath icon = new SVGPath();
		icon.setContent(iconPath);
		icon.getStyleClass().add("menu-icon");
		hbox.setAlignment(Pos.CENTER);
		iconPane.getChildren().add(icon);
		iconPane.setPrefSize(ICON_WIDTH + 2* ICON_MARGIN, BUTTON_HEIGHT);
		hbox.getChildren().add(iconPane);
		Label label = new Label(text);
		label.getStyleClass().add("left-menu-item-label");
		label.setPrefSize(BUTTON_WIDTH - 2*ICON_WIDTH - 4*ICON_MARGIN, BUTTON_HEIGHT);
		hbox.getChildren().add(label);
		HBox.setHgrow(label, Priority.ALWAYS);
		StackPane arrowPane = new StackPane();
		arrowPane.setPrefSize(ICON_ARROW_WIDTH + 2*ICON_MARGIN, BUTTON_HEIGHT);
		if (isRightNav) {
			SVGPath rightArrow = new SVGPath();
			rightArrow.setContent(arrowRightPath);
			arrowPane.getChildren().add(rightArrow);
			rightArrow.getStyleClass().add("menu-icon");
		}
		hbox.getChildren().add(arrowPane);
		hbox.setPrefSize(BUTTON_WIDTH, BUTTON_HEIGHT);
		hbox.getStyleClass().add("left-menu-item-panel");
		button.setGraphic(hbox);
		button.setOnAction(clickedEventHandler);
		button.getStyleClass().add("left-menu-button");
		if (index == 0) {
			button.getStyleClass().add("left-menu-top");
			button.getStyleClass().add("left-menu-both");
		}
		if (index == 1) {
			getChildren().get(0).getStyleClass().remove("left-menu-both");
		}
		if (index == getChildren().size()) {
			if (index > 0) {
				getChildren().get(index-1).getStyleClass().remove("left-menu-bottom");
			}
			button.getStyleClass().add("left-menu-bottom");
		}
		getChildren().add(button);
	}
	
	protected void setMenuItemSelected(int index) {
		if ((index < 0) || (index > getChildren().size())) {
			throw new IllegalArgumentException("Index out of bounds!");
		}
		for (Node node : getChildren()){
			node.getStyleClass().remove("menu-item-selected");
		}
		getChildren().get(index).getStyleClass().add("menu-item-selected");
	}
	
}
