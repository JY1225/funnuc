package eu.robojob.irscw.ui;

public class RoboSoftAppFactory {

	private MainPresenter mainPresenter;
	private ProcessMenuBarPresenter processMenuBarPresenter;
	private ProcessConfigurePresenter processConfigurePresenter;
	private KeyboardPresenter keyboardPresenter;
	
	public MainPresenter getMainPresenter() {
		if (mainPresenter == null) {
			MainView mainView = new MainView();
			mainPresenter = new MainPresenter(mainView);
			mainPresenter.setProcessMainContentPresenter(getProcessConfigurePresenter());
			mainPresenter.setProcessMenuBarPresenter(getProcessMenuBarPresenter());
		}
		return mainPresenter;
	}
	
	public ProcessMenuBarPresenter getProcessMenuBarPresenter() {
		if (processMenuBarPresenter == null) {
			ProcessMenuBarView processMenuBarView = new ProcessMenuBarView();
			processMenuBarPresenter = new ProcessMenuBarPresenter(processMenuBarView, getProcessConfigurePresenter(), getMainPresenter());
		}
		return processMenuBarPresenter;
	}
	
	public ProcessConfigurePresenter getProcessConfigurePresenter() {
		if (processConfigurePresenter == null) {
			ProcessConfigureView processConfigureView = new ProcessConfigureView();
			processConfigurePresenter = new ProcessConfigurePresenter(processConfigureView, getKeyboardPresenter());
		}
		return processConfigurePresenter;
	}
	
	public KeyboardPresenter getKeyboardPresenter() {
		if (keyboardPresenter == null) {
			KeyboardView keyboardView = new KeyboardView();
			keyboardPresenter = new KeyboardPresenter(keyboardView);
		}
		return keyboardPresenter;
	}
}
