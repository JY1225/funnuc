package eu.robojob.irscw.ui;

import eu.robojob.irscw.process.ProcessFlow;
import eu.robojob.irscw.ui.configure.ConfigurePresenter;
import eu.robojob.irscw.ui.teach.TeachPresenter;

public class MainPresenter {

	private MainView view;
	
	private ProcessFlow process;
	
	private MenuBarPresenter menuBarPresenter;
	private ConfigurePresenter configurePresenter;
	private TeachPresenter teachPresenter;
		
	public MainPresenter(MainView view, MenuBarPresenter menuBarPresenter, ConfigurePresenter configurePresenter, TeachPresenter teachPresenter) {
		this.view = view;
		view.setPresenter(this);
		this.menuBarPresenter = menuBarPresenter;
		menuBarPresenter.setParent(this);
		this.configurePresenter = configurePresenter;
		configurePresenter.setParent(this);
		this.teachPresenter = teachPresenter;
		teachPresenter.setParent(this);
		
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
		refreshStatus();
	}
	
	public void refreshStatus() {
		menuBarPresenter.setConfigureButtonEnabled(true);
		menuBarPresenter.setTeachButtonEnabled(false);
		menuBarPresenter.setAutomateButtonEnabled(false);
		if (configurePresenter.isConfigured()) {
			menuBarPresenter.setTeachButtonEnabled(true);
		}
	}
	
	public void showTeach() {
		menuBarPresenter.showTeachView();
		view.setContent(teachPresenter.getView());
		refreshStatus();
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
		teachPresenter.loadProcessFlow(process);
	}
	
	public void showMessage(String message) {
		
	}
	
	public ProcessFlow getProcessFlow() {
		return process;
	}
	
	public void updateProcessConfiguredStatus(boolean configured) {
		
	}
}
