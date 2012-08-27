package eu.robojob.irscw.ui.process;

import org.apache.log4j.Logger;

import eu.robojob.irscw.ui.MainPresenter;

public class MenuBarPresenter {

	private static Logger logger = Logger.getLogger(MenuBarPresenter.class);
	
	private MenuBarView view;
	private ConfigurePresenter processMainContentPresenter; 
	private MainPresenter mainPresenter;
	
	public MenuBarPresenter(MenuBarView processMenuBarView, ConfigurePresenter processMainContentPresenter, 
			MainPresenter mainPresenter) {
		this.view = processMenuBarView;
		this.processMainContentPresenter = processMainContentPresenter;
		this.mainPresenter = mainPresenter;
		processMenuBarView.setPresenter(this);
	}
	
	public MenuBarView getView() {
		return view;
	}
	
	public void showAlarmsView() {
		logger.debug("show alarms clicked");
		processMainContentPresenter.showAlarmsView();
	}
	
	public void showConfigureView() {
		view.setConfigureActive();
		processMainContentPresenter.showConfigureView();
	}
	
	public void showTeachView() {
		view.setTeachActive();
		processMainContentPresenter.showTeachView();
	}
	
	public void showAutomateView() {
		view.setAutomateActive();
		processMainContentPresenter.showAutomateView();
	}
	
	public void showAdminView() {
		mainPresenter.showAdminView();
	}
	
}
