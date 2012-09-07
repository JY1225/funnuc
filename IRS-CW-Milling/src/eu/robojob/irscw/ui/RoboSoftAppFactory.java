package eu.robojob.irscw.ui;

import eu.robojob.irscw.external.device.CNCMillingMachine;
import eu.robojob.irscw.external.device.Conveyor;
import eu.robojob.irscw.external.device.EmbossingDevice;
import eu.robojob.irscw.external.robot.FanucRobot;
import eu.robojob.irscw.process.InterventionStep;
import eu.robojob.irscw.process.PickStep;
import eu.robojob.irscw.process.ProcessFlow;
import eu.robojob.irscw.process.ProcessingStep;
import eu.robojob.irscw.process.PutStep;
import eu.robojob.irscw.ui.keyboard.KeyboardPresenter;
import eu.robojob.irscw.ui.keyboard.KeyboardView;
import eu.robojob.irscw.ui.keyboard.KeyboardView.KeyboardType;
import eu.robojob.irscw.ui.keyboard.NumericKeyboardPresenter;
import eu.robojob.irscw.ui.keyboard.NumericKeyboardView;
import eu.robojob.irscw.ui.main.MenuBarPresenter;
import eu.robojob.irscw.ui.main.MenuBarView;
import eu.robojob.irscw.ui.main.configure.ConfigurePresenter;
import eu.robojob.irscw.ui.main.configure.ConfigureView;
import eu.robojob.irscw.ui.main.configure.process.ProcessConfigurePresenter;
import eu.robojob.irscw.ui.main.configure.process.ProcessConfigureView;
import eu.robojob.irscw.ui.main.configure.process.ProcessMenuPresenter;
import eu.robojob.irscw.ui.main.configure.process.ProcessMenuView;
import eu.robojob.irscw.ui.main.configure.process.ProcessOpenPresenter;
import eu.robojob.irscw.ui.main.configure.process.ProcessOpenView;
import eu.robojob.irscw.ui.main.flow.ProcessFlowPresenter;
import eu.robojob.irscw.ui.main.flow.ProcessFlowView;

public class RoboSoftAppFactory {

	private MainPresenter mainPresenter;
	private MenuBarPresenter menuBarPresenter;
	private ConfigurePresenter configurePresenter;
	private KeyboardPresenter keyboardPresenter;
	private ProcessConfigurePresenter processConfigurationPresenter;
	private NumericKeyboardPresenter numericKeyboardPresenter;
	private ProcessFlowPresenter processFlowPresenter;
	private ProcessMenuPresenter processConfigurationMenuPresenter;
	private ProcessOpenPresenter processOpenPresenter;
	private ProcessFlow processFlow;
	
	public MainPresenter getMainPresenter() {
		if (mainPresenter == null) {
			MainView mainView = new MainView();
			mainPresenter = new MainPresenter(mainView);
			mainPresenter.setProcessMainContentPresenter(getConfigurePresenter());
			mainPresenter.setProcessMenuBarPresenter(getMenuBarPresenter());
			mainPresenter.loadProcessFlow(getProcessFlow());
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
	
	public ProcessConfigurePresenter getProcessConfigurePresenter() {
		if (processConfigurationPresenter == null) {
			ProcessConfigureView processConfigurationView = new ProcessConfigureView();
			processConfigurationPresenter = new ProcessConfigurePresenter(processConfigurationView, getProcessFlow());
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
			processConfigurationMenuPresenter = new ProcessMenuPresenter(processConfigurationMenuView, getProcessFlow(), getProcessConfigurePresenter(), getProcessOpenPresenter());
		}
		return processConfigurationMenuPresenter;
	}
	
	public ProcessOpenPresenter getProcessOpenPresenter() {
		if (processOpenPresenter == null) {
			ProcessOpenView processOpenView = new ProcessOpenView();
			processOpenPresenter = new ProcessOpenPresenter(processOpenView);
		}
		return processOpenPresenter;
	}
	
	public ProcessFlow getProcessFlow() {
		if (processFlow == null) {
			processFlow = new ProcessFlow("Mazak demo");
			Conveyor conveyor = new Conveyor("conveyor", null);
			EmbossingDevice embossing = new EmbossingDevice("embossing tes tes test", null);
			CNCMillingMachine cncMilling = new CNCMillingMachine("cnc", null);
			FanucRobot robot = new FanucRobot("robot", null);
			PickStep pick1 = new PickStep(robot, null, conveyor, null, null);
			PutStep put1 = new PutStep(robot, null, embossing, null, null);
			ProcessingStep processing1 = new ProcessingStep(embossing, null);
			PickStep pick2 = new PickStep(robot, null, embossing, null, null);
			PutStep put2 = new PutStep(robot, null, cncMilling, null, null);
			ProcessingStep processing2 = new ProcessingStep(cncMilling, null);
			InterventionStep intervention = new InterventionStep(cncMilling, null, 10);
			PickStep pick3 = new PickStep(robot, null, cncMilling, null, null);
			PutStep put3 = new PutStep(robot, null, conveyor, null, null);
			processFlow.addStep(pick1);
			processFlow.addStep(put1);
			processFlow.addStep(processing1);
			processFlow.addStep(pick2);
			processFlow.addStep(put2);
			processFlow.addStep(processing2);
			processFlow.addStep(intervention);
			processFlow.addStep(pick3);
			processFlow.addStep(put3);
		}
		return processFlow;
	}
}
