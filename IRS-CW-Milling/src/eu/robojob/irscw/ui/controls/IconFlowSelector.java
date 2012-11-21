package eu.robojob.irscw.ui.controls;

import java.util.HashMap;
import java.util.Map;

import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

public class IconFlowSelector extends ScrollPane {

	private Map<Integer, IconFlowSelectorItem> items;
	
//	private static Logger logger = Logger.getLogger(IconFlowSelector.class);
	
	private HBox box;
	
	public IconFlowSelector() {
		super();
		box = new HBox();
		box.setPadding(new Insets(10, 10, 10, 10));
		items = new HashMap<Integer, IconFlowSelectorItem>();
		setPrefHeight(145);
		box.setSpacing(0);
		setContent(box);
		clearItems();
	}
	
	public void addItem(int index, String name, String iconUrl, IconFlowSelectorItemChangedHandler handler) {
		this.getStyleClass().add("iconflow-selector");
		box.getStyleClass().add("iconflow-selector-hbox");
		IconFlowSelectorItem item = new IconFlowSelectorItem(index, name, iconUrl);
		handler.setIndex(index);
		handler.setName(name);
		item.addEventHandler(MouseEvent.MOUSE_CLICKED, handler);
		items.put(index, item);
		box.getChildren().add(item);
	}
	
	public void setSelected(int index) {
		for (IconFlowSelectorItem item : items.values()) {
			item.setSelected(false);
		}
		items.get(index).setSelected(true);
	}
	
	public void deselectAll() {
		for (IconFlowSelectorItem item : items.values()) {
			item.setSelected(false);
		}
	}
	
	public void setSelected(String id) {
		for (IconFlowSelectorItem item : items.values()) {
			item.setSelected(false);
			if (item.getName().equals(id)) {
				item.setSelected(true);
			}
		}
	}
	
	public void clearItems() {
		box.getChildren().clear();
		items.clear();
	}
	
}

