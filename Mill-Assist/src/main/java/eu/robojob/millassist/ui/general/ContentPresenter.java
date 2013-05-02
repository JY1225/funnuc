package eu.robojob.millassist.ui.general;

import javafx.scene.Node;

public interface ContentPresenter {
	
	void setActive(boolean active);
	Node getView();
}
