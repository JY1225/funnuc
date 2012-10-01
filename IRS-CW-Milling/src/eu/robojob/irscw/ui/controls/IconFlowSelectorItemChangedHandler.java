package eu.robojob.irscw.ui.controls;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;


public abstract class IconFlowSelectorItemChangedHandler implements EventHandler<MouseEvent> {
	
	private int index;
	private String name;
	
	public IconFlowSelectorItemChangedHandler() {
		
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public void handle(MouseEvent event) {
		handle(event, index, name);
	}

	public abstract void handle(MouseEvent event, int index, String name);
}