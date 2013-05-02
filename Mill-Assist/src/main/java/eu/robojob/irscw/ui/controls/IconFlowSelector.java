package eu.robojob.irscw.ui.controls;

import java.util.HashMap;
import java.util.Map;

import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

public class IconFlowSelector extends ScrollPane {

	private Map<Integer, IconFlowSelectorItem> items;	
	private HBox box;
	
	private static final int PREF_HEIGHT = 145;
	private static final int PREF_HEIGHT_SCROLL = 175;
	private static final int SPACING = 5;
	private static final int PADDING = 10;
	
	private static final String CSS_CLASS_ICONFLOW_SELECTOR = "iconflow-selector";
	
	public IconFlowSelector() {
		super();
		this.setFitToHeight(true);
		this.setFitToWidth(true);
		this.getStyleClass().add(CSS_CLASS_ICONFLOW_SELECTOR);
		box = new HBox();
		box.setPadding(new Insets(PADDING, PADDING, PADDING, PADDING));
		items = new HashMap<Integer, IconFlowSelectorItem>();
		setPrefHeight(PREF_HEIGHT);
		box.setPrefHeight(PREF_HEIGHT);
		box.setFillHeight(true);
		box.setSpacing(SPACING);
		setContent(box);
		clearItems();
	}
	
	public void addItem(final int index, final String name, final String iconUrl, final EventHandler<MouseEvent> handler) {
		IconFlowSelectorItem item = new IconFlowSelectorItem(index, name, iconUrl);
		item.addEventHandler(MouseEvent.MOUSE_CLICKED, handler);
		items.put(index, item);
		if (items.size() > 4) {
			this.setPrefHeight(PREF_HEIGHT_SCROLL);
		} else {
			this.setPrefHeight(PREF_HEIGHT);
		}
		box.getChildren().add(item);
	}
	
	public void setSelected(final int index) {
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
	
	public void setSelected(final String id) {
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

