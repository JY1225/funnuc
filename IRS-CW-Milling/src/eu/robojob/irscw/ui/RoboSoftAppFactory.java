package eu.robojob.irscw.ui;

public class RoboSoftAppFactory {

	private MainPresenter mainPresenter;
	private ProcessMenuBarPresenter processMenuBarPresenter;
	private ProcessMainContentPresenter processMainContentPresenter;
	
	public MainPresenter getMainPresenter() {
		if (mainPresenter == null) {
			MainView mainView = new MainView();
			mainPresenter = new MainPresenter(mainView);
			mainPresenter.setProcessMainContentPresenter(getProcessMainContentPresenter());
			mainPresenter.setProcessMenuBarPresenter(getProcessMenuBarPresenter());
		}
		return mainPresenter;
	}
	
	public ProcessMenuBarPresenter getProcessMenuBarPresenter() {
		if (processMenuBarPresenter == null) {
			ProcessMenuBarView processMenuBarView = new ProcessMenuBarView();
			processMenuBarPresenter = new ProcessMenuBarPresenter(processMenuBarView, getProcessMainContentPresenter(), getMainPresenter());
		}
		return processMenuBarPresenter;
	}
	
	public ProcessMainContentPresenter getProcessMainContentPresenter() {
		if (processMainContentPresenter == null) {
			ProcessMainContentView processMainContentView = new ProcessMainContentView();
			processMainContentPresenter = new ProcessMainContentPresenter(processMainContentView);
		}
		return processMainContentPresenter;
	}
}
