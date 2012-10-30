package eu.robojob.irscw.ui;

import javafx.application.Platform;
import eu.robojob.irscw.process.ProcessFlow;
import eu.robojob.irscw.ui.automate.AutomatePresenter;
import eu.robojob.irscw.ui.configure.ConfigurePresenter;
import eu.robojob.irscw.ui.teach.TeachPresenter;

public class MainPresenter {

	private MainView view;
	
	private ProcessFlow process;
	
	private MenuBarPresenter menuBarPresenter;
	private ConfigurePresenter configurePresenter;
	private TeachPresenter teachPresenter;
	private AutomatePresenter automatePresenter;
		
	public MainPresenter(MainView view, MenuBarPresenter menuBarPresenter, ConfigurePresenter configurePresenter, TeachPresenter teachPresenter, AutomatePresenter automatePresenter) {
		this.view = view;
		view.setPresenter(this);
		this.menuBarPresenter = menuBarPresenter;
		menuBarPresenter.setParent(this);
		this.configurePresenter = configurePresenter;
		configurePresenter.setParent(this);
		this.teachPresenter = teachPresenter;
		teachPresenter.setParent(this);
		this.automatePresenter = automatePresenter;
		automatePresenter.setParent(this);
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
		teachPresenter.setEnabled(false);
		refreshStatus();
	}
	
	public void refreshStatus() {
		menuBarPresenter.setConfigureButtonEnabled(true);
		menuBarPresenter.setTeachButtonEnabled(false);
		//menuBarPresenter.setAutomateButtonEnabled(false);
		if (configurePresenter.isConfigured()) {
			if (process.isConfigured()) {
				menuBarPresenter.setTeachButtonEnabled(true);
			} else {
				throw new IllegalStateException("Configuration UI says ok, but there is still data missing");
			}
		} 
		if (teachPresenter.isTeached()) {
			menuBarPresenter.setAutomateButtonEnabled(true);
		}
	}
	
	public void showTeach() {
		menuBarPresenter.showTeachView();
		view.setContent(teachPresenter.getView());
		teachPresenter.setEnabled(true);
		refreshStatus();
	}
	
	public void showAutomate() {
		menuBarPresenter.showAutomateView();
		view.setContent(automatePresenter.getView());
		refreshStatus();
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
		automatePresenter.loadProcessFlow(process);
	}
	
	public void showMessage(String message) {
		
	}
	
	public ProcessFlow getProcessFlow() {
		return process;
	}
	
	public void updateProcessConfiguredStatus(boolean configured) {
		
	}
	
	public void exit() {
		Platform.exit();
	}
}
