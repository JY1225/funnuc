package eu.robojob.irscw.ui;

import eu.robojob.irscw.external.device.AbstractDevice;
import eu.robojob.irscw.external.device.BasicStackPlate;
import eu.robojob.irscw.external.device.CNCMillingMachine;
import eu.robojob.irscw.external.device.DeviceManager;
import eu.robojob.irscw.external.robot.FanucRobot;
import eu.robojob.irscw.external.robot.FanucRobot.FanucRobotPickSettings;
import eu.robojob.irscw.external.robot.FanucRobot.FanucRobotPutSettings;
import eu.robojob.irscw.external.robot.RobotManager;
import eu.robojob.irscw.process.InterventionStep;
import eu.robojob.irscw.process.PickStep;
import eu.robojob.irscw.process.ProcessFlow;
import eu.robojob.irscw.process.ProcessingStep;
import eu.robojob.irscw.process.PutStep;
import eu.robojob.irscw.ui.configure.ConfigurePresenter;
import eu.robojob.irscw.ui.configure.ConfigureView;
import eu.robojob.irscw.ui.configure.device.DeviceMenuFactory;
import eu.robojob.irscw.ui.configure.process.ProcessConfigurePresenter;
import eu.robojob.irscw.ui.configure.process.ProcessConfigureView;
import eu.robojob.irscw.ui.configure.process.ProcessMenuPresenter;
import eu.robojob.irscw.ui.configure.process.ProcessMenuView;
import eu.robojob.irscw.ui.configure.process.ProcessOpenPresenter;
import eu.robojob.irscw.ui.configure.process.ProcessOpenView;
import eu.robojob.irscw.ui.configure.transport.TransportMenuFactory;
import eu.robojob.irscw.ui.keyboard.KeyboardPresenter;
import eu.robojob.irscw.ui.keyboard.KeyboardView;
import eu.robojob.irscw.ui.keyboard.KeyboardView.KeyboardType;
import eu.robojob.irscw.ui.keyboard.NumericKeyboardPresenter;
import eu.robojob.irscw.ui.keyboard.NumericKeyboardView;
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
	
	private DeviceManager deviceManager;
	private RobotManager robotManager;
	private DeviceMenuFactory deviceMenuFactory;
	private TransportMenuFactory transportMenuFactory;
	
	public MainPresenter getMainPresenter() {
		if (mainPresenter == null) {
			MainView mainView = new MainView();
			mainPresenter = new MainPresenter(mainView, getMenuBarPresenter(), getConfigurePresenter());
			mainPresenter.loadProcessFlow(getProcessFlow());
		}
		return mainPresenter;
	}
	
	public MenuBarPresenter getMenuBarPresenter() {
		if (menuBarPresenter == null) {
			MenuBarView processMenuBarView = new MenuBarView();
			menuBarPresenter = new MenuBarPresenter(processMenuBarView);
		}
		return menuBarPresenter;
	}
	
	public ConfigurePresenter getConfigurePresenter() {
		if (configurePresenter == null) {
			ConfigureView processConfigureView = new ConfigureView();
			configurePresenter = new ConfigurePresenter(processConfigureView, getKeyboardPresenter(), getNumericKeyboardPresenter(), getProcessFlowPresenter(), 
					getProcessConfigurationMenuPresenter(), getDeviceMenuFactory(), getTransportMenuFactory());
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
			processConfigurationMenuPresenter = new ProcessMenuPresenter(processConfigurationMenuView, getProcessConfigurePresenter(), getProcessOpenPresenter());
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
		DeviceManager deviceMgr = getDeviceManager();
		RobotManager robotMgr = getRobotManager();
		if (processFlow == null) {
			processFlow = new ProcessFlow("Mazak demo");
			
			FanucRobot robot = (FanucRobot) robotMgr.getRobotById("fanuc M110");
			processFlow.setRobotSettings(robot, robot.getRobotSettings());
			
			FanucRobotPickSettings pickSettings1 = new FanucRobot.FanucRobotPickSettings();
			pickSettings1.setGripperHead(robot.getGripperBody().getGripperHead("A"));
			
			AbstractDevice device = deviceMgr.getStackingFromDeviceById("basic stack plate");
			PickStep pick1 = new PickStep(robot, device, new BasicStackPlate.BasicStackPlatePickSettings(null, null), pickSettings1);
			processFlow.setDeviceSettings(device, device.getDeviceSettings());
			
			FanucRobotPutSettings putSettings1 = new FanucRobot.FanucRobotPutSettings();
			putSettings1.setGripperHead(robot.getGripperBody().getGripperHead("A"));
			
			AbstractDevice device2 = deviceMgr.getCNCMachineById("Mazak integrex");
			PutStep put2 = new PutStep(robot, device2, new CNCMillingMachine.CNCMillingMachinePutSettings(null, null), putSettings1);
			processFlow.setDeviceSettings(device2, device2.getDeviceSettings());
			ProcessingStep processing2 = new ProcessingStep( deviceMgr.getCNCMachineById("Mazak integrex"), new CNCMillingMachine.CNCMillingMachineStartCylusSettings(null));
			InterventionStep intervention = new InterventionStep( deviceMgr.getCNCMachineById("Mazak integrex"), new CNCMillingMachine.CNCMillingMachineInterventionSettings(null), 10);
			
			FanucRobotPickSettings pickSettings2 = new FanucRobot.FanucRobotPickSettings();
			pickSettings2.setGripperHead(robot.getGripperBody().getGripperHead("B"));
			
			PickStep pick3 = new PickStep(robot, deviceMgr.getCNCMachineById("Mazak integrex"), new CNCMillingMachine.CNCMillingMachinePickSettings(null, null), pickSettings2);
			
			FanucRobotPutSettings putSettings2 = new FanucRobot.FanucRobotPutSettings();
			putSettings2.setGripperHead(robot.getGripperBody().getGripperHead("B"));
			
			PutStep put3 = new PutStep(robot, deviceMgr.getStackingToDeviceById("basic stack plate"), new BasicStackPlate.BasicStackPlatePutSettings(null, null), putSettings2);
			processFlow.addStep(pick1);
			//processFlow.addStep(put1);
			//processFlow.addStep(processing1);
			//processFlow.addStep(pick2);
			processFlow.addStep(put2);
			processFlow.addStep(processing2);
			processFlow.addStep(intervention);
			processFlow.addStep(pick3);
			processFlow.addStep(put3);
		}
		return processFlow;
	}
	
	private DeviceMenuFactory getDeviceMenuFactory() {
		if (deviceMenuFactory == null) {
			deviceMenuFactory = new DeviceMenuFactory(getDeviceManager());
		}
		return deviceMenuFactory;
	}
	
	private TransportMenuFactory getTransportMenuFactory() {
		if (transportMenuFactory == null) {
			transportMenuFactory = new TransportMenuFactory(getProcessFlow());
		}
		return transportMenuFactory;
	}
	
	private DeviceManager getDeviceManager() {
		if (deviceManager == null) {
			deviceManager = new DeviceManager();
		}
		return deviceManager;
	}
	
	private RobotManager getRobotManager() {
		if (robotManager == null) {
			robotManager = new RobotManager();
		}
		return robotManager;
	}
}
