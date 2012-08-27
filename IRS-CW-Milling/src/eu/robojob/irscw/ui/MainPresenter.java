package eu.robojob.irscw.ui;

import eu.robojob.irscw.ui.process.ConfigurePresenter;
import eu.robojob.irscw.ui.process.MenuBarPresenter;

public class MainPresenter {

	private MainView view;
	
	private MenuBarPresenter processMenuBarPresenter;
	private ConfigurePresenter processConfigurePresenter;
		
	public MainPresenter(MainView view) {
		this.view = view;
		view.setPresenter(this);
	}
	
	public void setProcessMenuBarPresenter(MenuBarPresenter processMenuBarPresenter) {
		this.processMenuBarPresenter = processMenuBarPresenter;
	}
	
	public void setProcessMainContentPresenter(ConfigurePresenter processConfigurePresenter) {
		this.processConfigurePresenter = processConfigurePresenter;
	}
	
	public void showProcessView() {
		view.setHeader(processMenuBarPresenter.getView());
		view.setContent(processConfigurePresenter.getView());
	}
	
	public void showAdminView() {
		
	}
	
	public MainView getView() {
		return view;
	}
	
}
