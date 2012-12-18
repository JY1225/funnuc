package eu.robojob.irscw.ui;

import javafx.scene.Node;

public interface MainContentPresenter {

	/**
	 * Method used for indicating the presenter is active
	 */
	void setActive(boolean active);
	
	Node getView();
}
