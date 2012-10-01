package eu.robojob.irscw.ui.controls;

import java.util.HashMap;
import java.util.Map;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;

public class IconFlowSelector extends FlowPane {

	private Map<Integer, IconFlowSelectorItem> items;
	
	public IconFlowSelector() {
		items = new HashMap<Integer, IconFlowSelectorItem>();
	}
	
	public void addItem(int index, String name, String iconUrl, IconFlowSelectorItemChangedHandler handler) {
		IconFlowSelectorItem item = new IconFlowSelectorItem(index, name, iconUrl);
		handler.setIndex(index);
		handler.setName(name);
		item.addEventHandler(MouseEvent.MOUSE_CLICKED, handler);
	}
	
	public void iconClicked(int index) {
		for (IconFlowSelectorItem item : items.values()) {
			item.setSelected(false);
		}
		items.get(index).setSelected(true);
	}
	
	public void clearItems() {
		items.clear();
	}
	
}

