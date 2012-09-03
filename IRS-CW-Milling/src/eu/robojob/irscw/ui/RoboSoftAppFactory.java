package eu.robojob.irscw.ui;

import eu.robojob.irscw.ui.keyboard.KeyboardPresenter;
import eu.robojob.irscw.ui.keyboard.KeyboardView;
import eu.robojob.irscw.ui.keyboard.KeyboardView.KeyboardType;
import eu.robojob.irscw.ui.keyboard.NumericKeyboardPresenter;
import eu.robojob.irscw.ui.keyboard.NumericKeyboardView;
import eu.robojob.irscw.ui.process.MenuBarPresenter;
import eu.robojob.irscw.ui.process.MenuBarView;
import eu.robojob.irscw.ui.process.configure.ConfigurePresenter;
import eu.robojob.irscw.ui.process.configure.ConfigureView;
import eu.robojob.irscw.ui.process.configure.ProcessConfigurationMenuPresenter;
import eu.robojob.irscw.ui.process.configure.ProcessConfigurationMenuView;
import eu.robojob.irscw.ui.process.configure.ProcessConfigurationPresenter;
import eu.robojob.irscw.ui.process.configure.ProcessConfigurationView;
import eu.robojob.irscw.ui.process.flow.ProcessFlowPresenter;
import eu.robojob.irscw.ui.process.flow.ProcessFlowView;

public class RoboSoftAppFactory {

	private MainPresenter mainPresenter;
	private MenuBarPresenter menuBarPresenter;
	private ConfigurePresenter configurePresenter;
	private KeyboardPresenter keyboardPresenter;
	private ProcessConfigurationPresenter processConfigurationPresenter;
	private NumericKeyboardPresenter numericKeyboardPresenter;
	private ProcessFlowPresenter processFlowPresenter;
	private ProcessConfigurationMenuPresenter processConfigurationMenuPresenter;
	
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
			configurePresenter = new ConfigurePresenter(processConfigureView, getKeyboardPresenter(), getNumericKeyboardPresenter(), getProcessFlowPresenter(), getProcessConfigurationMenuPresenter(), getProcessConfigurationPresenter());
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
	
	public ProcessConfigurationMenuPresenter getProcessConfigurationMenuPresenter() {
		if (processConfigurationMenuPresenter == null) {
			ProcessConfigurationMenuView processConfigurationMenuView = new ProcessConfigurationMenuView();
			processConfigurationMenuPresenter = new ProcessConfigurationMenuPresenter(processConfigurationMenuView);
		}
		return processConfigurationMenuPresenter;
	}
}
