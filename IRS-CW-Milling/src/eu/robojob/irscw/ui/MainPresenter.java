package eu.robojob.irscw.ui;

import eu.robojob.irscw.process.ProcessFlow;
import eu.robojob.irscw.ui.main.configure.ConfigurePresenter;

public class MainPresenter {

	private MainView view;
	
	private ProcessFlow process;
	
	private MenuBarPresenter menuBarPresenter;
	private ConfigurePresenter configurePresenter;
		
	public MainPresenter(MainView view, MenuBarPresenter menuBarPresenter, ConfigurePresenter configurePresenter) {
		this.view = view;
		view.setPresenter(this);
		this.menuBarPresenter = menuBarPresenter;
		menuBarPresenter.setParent(this);
		this.configurePresenter = configurePresenter;
		configurePresenter.setParent(this);
		
		this.process = null;

		view.setHeader(menuBarPresenter.getView());
	}
	
	public void setProcessMenuBarPresenter(MenuBarPresenter processMenuBarPresenter) {
		this.menuBarPresenter = processMenuBarPresenter;
	}
	
	public void setProcessMainContentPresenter(ConfigurePresenter configurePresenter) {
		this.configurePresenter = configurePresenter;
	}

	public void showConfigure() {
		menuBarPresenter.showConfigureView();
		view.setContent(configurePresenter.getView());
	}
	
	public void showTeach() {
		menuBarPresenter.showTeachView();
	}
	
	public void showAutomate() {
		menuBarPresenter.showAutomateView();
	}
	
	public void showAlarms() {
		menuBarPresenter.showAlarmsView();
	}
	
	public void showAdmin() {
		menuBarPresenter.showAdminView();
	}
	
	public MainView getView() {
		return view;
	}
	
	public void setMenuBarEnabled(boolean enabled) {
		menuBarPresenter.setMenuBarEnabled(enabled);
	}
	
	public void loadProcessFlow(ProcessFlow process) {
		this.process = process;
		configurePresenter.loadProcessFlow(process);
	}
	
	public void showMessage(String message) {
		
	}
	
	public ProcessFlow getProcessFlow() {
		return process;
	}
}
