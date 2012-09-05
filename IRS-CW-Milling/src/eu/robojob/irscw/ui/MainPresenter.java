package eu.robojob.irscw.ui;

import eu.robojob.irscw.ui.main.MenuBarPresenter;
import eu.robojob.irscw.ui.main.configure.ConfigurePresenter;

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
	
	public void showProcessConfigureView() {
		view.setHeader(processMenuBarPresenter.getView());
		processMenuBarPresenter.showConfigureView();
		view.setContent(processConfigurePresenter.getView());
	}
	
	public void showAdminView() {
		
	}
	
	public MainView getView() {
		return view;
	}
	
}
