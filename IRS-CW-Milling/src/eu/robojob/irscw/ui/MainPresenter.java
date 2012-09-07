package eu.robojob.irscw.ui;

import eu.robojob.irscw.process.ProcessFlow;
import eu.robojob.irscw.ui.main.MenuBarPresenter;
import eu.robojob.irscw.ui.main.configure.ConfigurePresenter;

public class MainPresenter {

	private MainView view;
	
	private ProcessFlow process;
	
	private MenuBarPresenter processMenuBarPresenter;
	private ConfigurePresenter configurePresenter;
		
	public MainPresenter(MainView view) {
		this.view = view;
		view.setPresenter(this);
		this.process = null;
	}
	
	public void setProcessMenuBarPresenter(MenuBarPresenter processMenuBarPresenter) {
		this.processMenuBarPresenter = processMenuBarPresenter;
	}
	
	public void setProcessMainContentPresenter(ConfigurePresenter configurePresenter) {
		this.configurePresenter = configurePresenter;
	}
	
	public void showProcessConfigureView() {
		view.setHeader(processMenuBarPresenter.getView());
		processMenuBarPresenter.showConfigureView();
		view.setContent(configurePresenter.getView());
	}
	
	public void showAdminView() {
		
	}
	
	public MainView getView() {
		return view;
	}
	
	public void loadProcessFlow(ProcessFlow process) {
		this.process = process;
		configurePresenter.loadProcessFlow(process);
	}
	
	public void showMessage(String message) {
		
	}
}
