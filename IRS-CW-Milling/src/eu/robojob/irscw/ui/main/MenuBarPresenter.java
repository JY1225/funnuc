package eu.robojob.irscw.ui.main;

import org.apache.log4j.Logger;

import eu.robojob.irscw.ui.MainPresenter;
import eu.robojob.irscw.ui.main.configure.ConfigurePresenter;

public class MenuBarPresenter {

	private static Logger logger = Logger.getLogger(MenuBarPresenter.class);
	
	private MenuBarView view;
	private ConfigurePresenter configurePresenter; 
	private MainPresenter mainPresenter;
	
	public MenuBarPresenter(MenuBarView processMenuBarView, ConfigurePresenter configurePresenter, 
			MainPresenter mainPresenter) {
		this.view = processMenuBarView;
		this.configurePresenter = configurePresenter;
		configurePresenter.setParent(this);
		this.mainPresenter = mainPresenter;
		processMenuBarView.setPresenter(this);
	}
	
	public MenuBarView getView() {
		return view;
	}
	
	public void showAlarmsView() {
		logger.debug("show alarms clicked");
		configurePresenter.showAlarmsView();
	}
	
	public void showConfigureView() {
		view.setConfigureActive();
		configurePresenter.showConfigureView();
	}
	
	public void showTeachView() {
		view.setTeachActive();
		configurePresenter.showTeachView();
	}
	
	public void showAutomateView() {
		view.setAutomateActive();
		configurePresenter.showAutomateView();
	}
	
	public void showAdminView() {
		mainPresenter.showAdminView();
	}
	
	public void setMenuBarEnabled(boolean enabled) {
		view.setEnabled(enabled);
	}
	
}
