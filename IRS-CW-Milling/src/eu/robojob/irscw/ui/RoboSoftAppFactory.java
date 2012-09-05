package eu.robojob.irscw.ui;

import eu.robojob.irscw.ui.keyboard.KeyboardPresenter;
import eu.robojob.irscw.ui.keyboard.KeyboardView;
import eu.robojob.irscw.ui.keyboard.KeyboardView.KeyboardType;
import eu.robojob.irscw.ui.keyboard.NumericKeyboardPresenter;
import eu.robojob.irscw.ui.keyboard.NumericKeyboardView;
import eu.robojob.irscw.ui.main.MenuBarPresenter;
import eu.robojob.irscw.ui.main.MenuBarView;
import eu.robojob.irscw.ui.main.configure.ConfigurePresenter;
import eu.robojob.irscw.ui.main.configure.ConfigureView;
import eu.robojob.irscw.ui.main.configure.process.ProcessMenuPresenter;
import eu.robojob.irscw.ui.main.configure.process.ProcessMenuView;
import eu.robojob.irscw.ui.main.configure.process.ProcessConfigurationPresenter;
import eu.robojob.irscw.ui.main.configure.process.ProcessConfigurationView;
import eu.robojob.irscw.ui.main.flow.ProcessFlowPresenter;
import eu.robojob.irscw.ui.main.flow.ProcessFlowView;

public class RoboSoftAppFactory {

	private MainPresenter mainPresenter;
	private MenuBarPresenter menuBarPresenter;
	private ConfigurePresenter configurePresenter;
	private KeyboardPresenter keyboardPresenter;
	private ProcessConfigurationPresenter processConfigurationPresenter;
	private NumericKeyboardPresenter numericKeyboardPresenter;
	private ProcessFlowPresenter processFlowPresenter;
	private ProcessMenuPresenter processConfigurationMenuPresenter;
	
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
			configurePresenter = new ConfigurePresenter(processConfigureView, getKeyboardPresenter(), getNumericKeyboardPresenter(), getProcessFlowPresenter(), getProcessConfigurationMenuPresenter());
		}
		return configurePresenter;
	}
	
	public KeyboardPresenter getKeyboardPresenter() {
		if (keyboardPresenter == null) {
			KeyboardView keyboardView = new KeyboardView(KeyboardType.AZERTY);
			keyboardPresenter = new KeyboardPresenter(keyboardView);
		}
		return keyboardPresenter;
	}
	
	public NumericKeyboardPresenter getNumericKeyboardPresenter() {
		if (numericKeyboardPresenter == null) {
			NumericKeyboardView numericKeyboardView = new NumericKeyboardView();
			numericKeyboardPresenter = new NumericKeyboardPresenter(numericKeyboardView);
		}
		return numericKeyboardPresenter;
	}
	
	public ProcessConfigurationPresenter getProcessConfigurationPresenter() {
		if (processConfigurationPresenter == null) {
			ProcessConfigurationView processConfigurationView = new ProcessConfigurationView();
			processConfigurationPresenter = new ProcessConfigurationPresenter(processConfigurationView);
		}
		return processConfigurationPresenter;
	}
	
	public ProcessFlowPresenter getProcessFlowPresenter() {
		if (processFlowPresenter == null) {
			ProcessFlowView processFlowView = new ProcessFlowView();
			processFlowPresenter = new ProcessFlowPresenter(processFlowView);
		}
		return processFlowPresenter;
	}
	
	public ProcessMenuPresenter getProcessConfigurationMenuPresenter() {
		if (processConfigurationMenuPresenter == null) {
			ProcessMenuView processConfigurationMenuView = new ProcessMenuView();
			processConfigurationMenuPresenter = new ProcessMenuPresenter(processConfigurationMenuView, getProcessConfigurationPresenter());
		}
		return processConfigurationMenuPresenter;
	}
}
