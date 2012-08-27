package eu.robojob.irscw.ui;

import eu.robojob.irscw.ui.keyboard.KeyboardPresenter;
import eu.robojob.irscw.ui.keyboard.KeyboardView;
import eu.robojob.irscw.ui.keyboard.KeyboardView.KeyboardType;
import eu.robojob.irscw.ui.process.ConfigurePresenter;
import eu.robojob.irscw.ui.process.ConfigureView;
import eu.robojob.irscw.ui.process.MenuBarPresenter;
import eu.robojob.irscw.ui.process.MenuBarView;
import eu.robojob.irscw.ui.process.ProcessConfigurationPresenter;
import eu.robojob.irscw.ui.process.ProcessConfigurationView;

public class RoboSoftAppFactory {

	private MainPresenter mainPresenter;
	private MenuBarPresenter menuBarPresenter;
	private ConfigurePresenter configurePresenter;
	private KeyboardPresenter keyboardPresenter;
	private ProcessConfigurationPresenter processConfigurationPresenter;
	
	public MainPresenter getMainPresenter() {
		if (mainPresenter == null) {
			MainView mainView = new MainView();
			mainPresenter = new MainPresenter(mainView);
			mainPresenter.setProcessMainContentPresenter(getConfigurePresenter());
			mainPresenter.setProcessMenuBarPresenter(getMenuBarPresenter());
		}
		return mainPresenter;
	}
	
	public MenuBarPresenter getMenuBarPresenter() {
		if (menuBarPresenter == null) {
			MenuBarView processMenuBarView = new MenuBarView();
			menuBarPresenter = new MenuBarPresenter(processMenuBarView, getConfigurePresenter(), getMainPresenter());
		}
		return menuBarPresenter;
	}
	
	public ConfigurePresenter getConfigurePresenter() {
		if (configurePresenter == null) {
			ConfigureView processConfigureView = new ConfigureView();
			configurePresenter = new ConfigurePresenter(processConfigureView, getKeyboardPresenter(), getProcessConfigurationPresenter());
		}
		return configurePresenter;
	}
	
	public KeyboardPresenter getKeyboardPresenter() {
		if (keyboardPresenter == null) {
			KeyboardView keyboardView = new KeyboardView(KeyboardType.QWERTY_DE);
			keyboardPresenter = new KeyboardPresenter(keyboardView);
		}
		return keyboardPresenter;
	}
	
	public ProcessConfigurationPresenter getProcessConfigurationPresenter() {
		if (processConfigurationPresenter == null) {
			ProcessConfigurationView processConfigurationView = new ProcessConfigurationView();
			processConfigurationPresenter = new ProcessConfigurationPresenter(processConfigurationView);
		}
		return processConfigurationPresenter;
	}
}
