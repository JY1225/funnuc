package eu.robojob.irscw.ui;

import javafx.scene.Node;

public interface MainContentPresenter {

	/**
	 * Method used for indicating the presenter is active
	 * @param active
	 */
	public void setActive(boolean active);
	
	public Node getView();
}
