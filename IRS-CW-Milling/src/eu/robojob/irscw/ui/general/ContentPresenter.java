package eu.robojob.irscw.ui.general;

import javafx.scene.Node;

public interface ContentPresenter {
	
	void setActive(boolean active);
	Node getView();
}
