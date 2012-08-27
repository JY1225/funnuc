package eu.robojob.irscw.ui;

import eu.robojob.irscw.ui.process.ProcessConfigurePresenter;
import eu.robojob.irscw.ui.process.ProcessMenuBarPresenter;

public class MainPresenter {

	private MainView view;
	
	private ProcessMenuBarPresenter processMenuBarPresenter;
	private ProcessConfigurePresenter processConfigurePresenter;
		
	public MainPresenter(MainView view) {
		this.view = view;
		view.setPresenter(this);
	}
	
	public void setProcessMenuBarPresenter(ProcessMenuBarPresenter processMenuBarPresenter) {
		this.processMenuBarPresenter = processMenuBarPresenter;
	}
	
	public void setProcessMainContentPresenter(ProcessConfigurePresenter processConfigurePresenter) {
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
