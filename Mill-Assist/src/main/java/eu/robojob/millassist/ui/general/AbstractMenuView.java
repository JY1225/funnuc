package eu.robojob.millassist.ui.general;

import java.util.HashMap;
import java.util.Map;

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

public abstract class AbstractMenuView<T extends AbstractMenuPresenter<?>> extends VBox {

	private T presenter;
		
	private static String arrowRightPath = "M 1.6875 0 L 0 1.65625 L 3.375 5 L 0.0625 8.3125 L 1.71875 10 L 6.65625 5.0625 L 6.5625 4.96875 L 6.625 4.90625 L 1.6875 0 z ";
	
	private static final int TEXT_BUTTON_WIDTH = 149;
	private static final int BUTTON_WIDTH = 209;
	private static final int ICON_BUTTON_WIDTH = 49;
	private static final int BUTTON_HEIGHT = 43;
	
	private static final int ICON_WIDTH = 20;
	private static final int ICON_MARGIN = 6;
	private static final int ICON_ARROW_WIDTH = 10;
	
	private static final String CSS_CLASS_MENU_ICON = "menu-icon";
	private static final String CSS_CLASS_LEFT_MENU = "left-menu";
	private static final String CSS_CLASS_LEFT_MENU_ITEM_LABEL = "left-menu-item-label";
	private static final String CSS_CLASS_LEFT_MENU_ITEM_PANEL = "left-menu-item-panel";
	private static final String CSS_CLASS_LEFT_MENU_BUTTON = "left-menu-button";
	private static final String CSS_CLASS_LEFT_MENU_TOP = "left-menu-top";
	private static final String CSS_CLASS_LEFT_MENU_BOTH = "left-menu-both";
	private static final String CSS_CLASS_LEFT_MENU_BOTTOM = "left-menu-bottom";
	private static final String CSS_CLASS_MENU_ITEM_SELECTED = "menu-item-selected";
	
	private Map<Integer, Button> menuItems;
	
	public AbstractMenuView() {
		super();
		setAlignment(Pos.TOP_CENTER);
		this.getStyleClass().add(CSS_CLASS_LEFT_MENU);
		this.menuItems = new HashMap<Integer, Button>();
	}
	
	public void setPresenter(final T presenter) {
		this.presenter = presenter;
	}
	
	public T getPresenter() {
		return presenter;
	}
	
	protected abstract void build();
	
	protected void addMenuItem(final int index, final String iconPath, final String text, final boolean isRightNav, final EventHandler<ActionEvent> clickedEventHandler) {
		if ((index < 0) || (index > getChildren().size()) || ((getChildren().size() > 0) && (index < getChildren().size() - 1) && getChildren().get(index) != null)) {
			throw new IllegalArgumentException("Wrong index value ["  + index + "].");
		}
		this.setPadding(new Insets(1, 1, 0, 0));
		Button button = new Button();
		HBox hbox = new HBox();
		StackPane iconPane = new StackPane();
		SVGPath icon = new SVGPath();
		icon.setContent(iconPath);
		icon.getStyleClass().add(CSS_CLASS_MENU_ICON);
		hbox.setAlignment(Pos.CENTER);
		iconPane.getChildren().add(icon);
		iconPane.setPrefSize(ICON_WIDTH + 2 * ICON_MARGIN, BUTTON_HEIGHT);
		hbox.getChildren().add(iconPane);
		Label label = new Label(text);
		label.getStyleClass().add(CSS_CLASS_LEFT_MENU_ITEM_LABEL);
		label.setPrefSize(BUTTON_WIDTH - 2 * ICON_WIDTH - 4 * ICON_MARGIN, BUTTON_HEIGHT);
		//Added for test purposes
		label.setLabelFor(button);
		hbox.getChildren().add(label);
		HBox.setHgrow(label, Priority.ALWAYS);
		StackPane arrowPane = new StackPane();
		arrowPane.setPrefSize(ICON_ARROW_WIDTH + 2 * ICON_MARGIN, BUTTON_HEIGHT);
		if (isRightNav) {
			SVGPath rightArrow = new SVGPath();
			rightArrow.setContent(arrowRightPath);
			arrowPane.getChildren().add(rightArrow);
			rightArrow.getStyleClass().add(CSS_CLASS_MENU_ICON);
		}
		hbox.getChildren().add(arrowPane);
		hbox.setPrefSize(BUTTON_WIDTH, BUTTON_HEIGHT);
		hbox.getStyleClass().add(CSS_CLASS_LEFT_MENU_ITEM_PANEL);
		button.setGraphic(hbox);
		button.setOnAction(clickedEventHandler);
		button.getStyleClass().add(CSS_CLASS_LEFT_MENU_BUTTON);
		if (index == 0) {
			button.getStyleClass().add(CSS_CLASS_LEFT_MENU_TOP);
			button.getStyleClass().add(CSS_CLASS_LEFT_MENU_BOTH);
		}
		if (index == 1) {
			getChildren().get(0).getStyleClass().remove(CSS_CLASS_LEFT_MENU_BOTH);
		}
		if (index == getChildren().size()) {
			if (index > 0) {
				getChildren().get(index - 1).getStyleClass().remove(CSS_CLASS_LEFT_MENU_BOTTOM);
			}
			button.getStyleClass().add(CSS_CLASS_LEFT_MENU_BOTTOM);
		}
		menuItems.put(index, button);
		getChildren().add(button);
	}
	
	protected void addIconMenuItem(final int index, final String iconPath, final EventHandler<ActionEvent> clickedEventHandler) {
		if ((index < 0) || (index > getChildren().size()) || ((getChildren().size() > 0) && (index < getChildren().size() - 1) && getChildren().get(index) != null)) {
			throw new IllegalArgumentException("Wrong index value ["  + index + "].");
		}
		Button button = new Button();
		HBox hbox = new HBox();
		StackPane iconPane = new StackPane();
		SVGPath icon = new SVGPath();
		icon.setContent(iconPath);
		icon.getStyleClass().add(CSS_CLASS_MENU_ICON);
		hbox.setAlignment(Pos.CENTER);
		iconPane.getChildren().add(icon);
		iconPane.setPrefSize(ICON_WIDTH + 2 * ICON_MARGIN, BUTTON_HEIGHT);
		hbox.getChildren().add(iconPane);
		hbox.setPrefSize(ICON_BUTTON_WIDTH, BUTTON_HEIGHT);
		button.setGraphic(hbox);
		button.setOnAction(clickedEventHandler);
		button.getStyleClass().add(CSS_CLASS_LEFT_MENU_BUTTON);
		if (index == 0) {
			button.getStyleClass().add(CSS_CLASS_LEFT_MENU_TOP);
			button.getStyleClass().add(CSS_CLASS_LEFT_MENU_BOTH);
		}
		if (index == 1) {
			getChildren().get(0).getStyleClass().remove(CSS_CLASS_LEFT_MENU_BOTH);
		}
		if (index == getChildren().size()) {
			if (index > 0) {
				getChildren().get(index - 1).getStyleClass().remove(CSS_CLASS_LEFT_MENU_BOTTOM);
			}
			button.getStyleClass().add(CSS_CLASS_LEFT_MENU_BOTTOM);
		}
		menuItems.put(index, button);
		getChildren().add(button);
	}
	
	protected void addTextMenuItem(final int index, final String text, final boolean isRightNav, final EventHandler<ActionEvent> clickedEventHandler) {
		if ((index < 0) || (index > getChildren().size()) || ((getChildren().size() > 0) && (index < getChildren().size() - 1) && getChildren().get(index) != null)) {
			throw new IllegalArgumentException("Wrong index value ["  + index + "].");
		}
		Button button = new Button();
		HBox hbox = new HBox();
		hbox.setAlignment(Pos.CENTER);
		Label label = new Label(text);
		label.getStyleClass().add(CSS_CLASS_LEFT_MENU_ITEM_LABEL);
		label.setPrefSize(TEXT_BUTTON_WIDTH - ICON_WIDTH - 3 * ICON_MARGIN, BUTTON_HEIGHT);
		hbox.getChildren().add(label);
		HBox.setHgrow(label, Priority.ALWAYS);
		StackPane arrowPane = new StackPane();
		arrowPane.setPrefSize(ICON_ARROW_WIDTH + 2 * ICON_MARGIN, BUTTON_HEIGHT);
		if (isRightNav) {
			SVGPath rightArrow = new SVGPath();
			rightArrow.setContent(arrowRightPath);
			arrowPane.getChildren().add(rightArrow);
			rightArrow.getStyleClass().add(CSS_CLASS_MENU_ICON);
		}
		hbox.getChildren().add(arrowPane);
		hbox.setPrefSize(TEXT_BUTTON_WIDTH, BUTTON_HEIGHT);
		hbox.getStyleClass().add(CSS_CLASS_LEFT_MENU_ITEM_PANEL);
		button.setGraphic(hbox);
		button.setOnAction(clickedEventHandler);
		button.getStyleClass().add(CSS_CLASS_LEFT_MENU_BUTTON);
		if (index == 0) {
			button.getStyleClass().add(CSS_CLASS_LEFT_MENU_TOP);
			button.getStyleClass().add(CSS_CLASS_LEFT_MENU_BOTH);
		}
		if (index == 1) {
			getChildren().get(0).getStyleClass().remove(CSS_CLASS_LEFT_MENU_BOTH);
		}
		if (index == getChildren().size()) {
			if (index > 0) {
				getChildren().get(index - 1).getStyleClass().remove(CSS_CLASS_LEFT_MENU_BOTTOM);
			}
			button.getStyleClass().add(CSS_CLASS_LEFT_MENU_BOTTOM);
		}
		menuItems.put(index, button);
		getChildren().add(button);
	}
	
	protected void setMenuItemSelected(final int index) {
		if ((index < 0) || (index > getChildren().size())) {
			throw new IllegalArgumentException("Index out of bounds [" + index + "].");
		}
		for (Node node : getChildren()) {
			node.getStyleClass().remove(CSS_CLASS_MENU_ITEM_SELECTED);
		}
		getChildren().get(index).getStyleClass().add(CSS_CLASS_MENU_ITEM_SELECTED);
	}
	
	public Button getMenuItem(final int index) {
		return menuItems.get(index);
	}
	
	public int getMenuItemsSize() {
		return menuItems.size();
	}
	
}
