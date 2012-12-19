package eu.robojob.irscw.ui;

import javafx.scene.Node;

public interface MainContentPresenter {

	void setActive(boolean active);
	void setParent(MainPresenter mainPresenter);
	Node getView();
}
