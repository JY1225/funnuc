package eu.robojob.irscw.ui;

import javafx.scene.Node;

public interface ContentPresenter {
	
	void setActive(boolean active);
	Node getView();
}
