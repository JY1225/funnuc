package eu.robojob.irscw.ui;

import org.apache.log4j.Logger;

public class ProcessMenuBarPresenter {

	private static Logger logger = Logger.getLogger(ProcessMenuBarPresenter.class);
	
	private ProcessMenuBarView processMenuBarView;
	private ProcessMainContentPresenter processMainContentPresenter; 
	private MainPresenter mainPresenter;
	
	public ProcessMenuBarPresenter(ProcessMenuBarView processMenuBarView, ProcessMainContentPresenter processMainContentPresenter, 
			MainPresenter mainPresenter) {
		this.processMenuBarView = processMenuBarView;
		this.processMainContentPresenter = processMainContentPresenter;
		this.mainPresenter = mainPresenter;
		processMenuBarView.setPresenter(this);
	}
	
	public ProcessMenuBarView getView() {
		return processMenuBarView;
	}
	
	public void showAlarmsView() {
		logger.debug("show alarms clicked");
		processMainContentPresenter.showAlarmsView();
	}
	
	public void showConfigureView() {
		processMainContentPresenter.showConfigureView();
	}
	
	public void showTeachView() {
		processMainContentPresenter.showTeachView();
	}
	
	public void showAutomateView() {
		processMainContentPresenter.showAutomateView();
	}
	
	public void showAdminView() {
		mainPresenter.showAdminView();
	}
	
}
