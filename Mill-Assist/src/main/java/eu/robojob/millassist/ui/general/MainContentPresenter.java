package eu.robojob.millassist.ui.general;

import eu.robojob.millassist.ui.MainPresenter;


public interface MainContentPresenter extends ContentPresenter {

	void setParent(MainPresenter mainPresenter);
	MainPresenter getParent();
}
