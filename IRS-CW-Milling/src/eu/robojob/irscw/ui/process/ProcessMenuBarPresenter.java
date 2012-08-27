package eu.robojob.irscw.ui.process;

import org.apache.log4j.Logger;

import eu.robojob.irscw.ui.MainPresenter;

public class ProcessMenuBarPresenter {

	private static Logger logger = Logger.getLogger(ProcessMenuBarPresenter.class);
	
	private ProcessMenuBarView view;
	private ProcessConfigurePresenter processMainContentPresenter; 
	private MainPresenter mainPresenter;
	
	public ProcessMenuBarPresenter(ProcessMenuBarView processMenuBarView, ProcessConfigurePresenter processMainContentPresenter, 
			MainPresenter mainPresenter) {
		this.view = processMenuBarView;
		this.processMainContentPresenter = processMainContentPresenter;
		this.mainPresenter = mainPresenter;
		processMenuBarView.setPresenter(this);
	}
	
	public ProcessMenuBarView getView() {
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
