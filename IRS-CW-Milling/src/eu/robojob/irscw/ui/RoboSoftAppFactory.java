package eu.robojob.irscw.ui;

import eu.robojob.irscw.external.device.DeviceManager;
import eu.robojob.irscw.external.robot.RobotManager;
import eu.robojob.irscw.external.robot.fanuc.FanucRobot;
import eu.robojob.irscw.process.ProcessFlow;
import eu.robojob.irscw.process.ProcessFlowManager;
import eu.robojob.irscw.process.ProcessFlowTimer;
import eu.robojob.irscw.ui.admin.AdminPresenter;
import eu.robojob.irscw.ui.admin.AdminView;
import eu.robojob.irscw.ui.admin.MainMenuPresenter;
import eu.robojob.irscw.ui.admin.MainMenuView;
import eu.robojob.irscw.ui.admin.SubMenuAdminView;
import eu.robojob.irscw.ui.admin.device.DeviceAdminPresenter;
import eu.robojob.irscw.ui.admin.device.DeviceMenuPresenter;
import eu.robojob.irscw.ui.admin.device.DeviceMenuView;
import eu.robojob.irscw.ui.admin.robot.RobotAdminPresenter;
import eu.robojob.irscw.ui.admin.robot.RobotConfigurePresenter;
import eu.robojob.irscw.ui.admin.robot.RobotConfigureView;
import eu.robojob.irscw.ui.admin.robot.RobotGripperPresenter;
import eu.robojob.irscw.ui.admin.robot.RobotGripperView;
import eu.robojob.irscw.ui.admin.robot.RobotMenuPresenter;
import eu.robojob.irscw.ui.admin.robot.RobotMenuView;
import eu.robojob.irscw.ui.automate.AutomatePresenter;
import eu.robojob.irscw.ui.automate.AutomateStatusPresenter;
import eu.robojob.irscw.ui.automate.AutomateStatusView;
import eu.robojob.irscw.ui.automate.TimingView;
import eu.robojob.irscw.ui.configure.ConfigurePresenter;
import eu.robojob.irscw.ui.configure.ConfigureView;
import eu.robojob.irscw.ui.configure.device.DeviceMenuFactory;
import eu.robojob.irscw.ui.configure.flow.ConfigureProcessFlowPresenter;
import eu.robojob.irscw.ui.configure.process.ProcessConfigurePresenter;
import eu.robojob.irscw.ui.configure.process.ProcessConfigureView;
import eu.robojob.irscw.ui.configure.process.ProcessMenuPresenter;
import eu.robojob.irscw.ui.configure.process.ProcessMenuView;
import eu.robojob.irscw.ui.configure.process.ProcessOpenPresenter;
import eu.robojob.irscw.ui.configure.process.ProcessOpenView;
import eu.robojob.irscw.ui.configure.process.ProcessSavePresenter;
import eu.robojob.irscw.ui.configure.process.ProcessSaveView;
import eu.robojob.irscw.ui.configure.transport.TransportMenuFactory;
import eu.robojob.irscw.ui.controls.keyboard.FullKeyboardPresenter;
import eu.robojob.irscw.ui.controls.keyboard.FullKeyboardView;
import eu.robojob.irscw.ui.controls.keyboard.FullKeyboardView.KeyboardType;
import eu.robojob.irscw.ui.controls.keyboard.NumericKeyboardPresenter;
import eu.robojob.irscw.ui.controls.keyboard.NumericKeyboardView;
import eu.robojob.irscw.ui.general.flow.FixedProcessFlowPresenter;
import eu.robojob.irscw.ui.general.flow.ProcessFlowView;
import eu.robojob.irscw.ui.general.status.DisconnectedDevicesView;
import eu.robojob.irscw.ui.general.status.StatusPresenter;
import eu.robojob.irscw.ui.general.status.StatusView;
import eu.robojob.irscw.ui.menu.MenuBarPresenter;
import eu.robojob.irscw.ui.menu.MenuBarView;
import eu.robojob.irscw.ui.robot.RobotPopUpPresenter;
import eu.robojob.irscw.ui.robot.RobotPopUpView;
import eu.robojob.irscw.ui.teach.GeneralInfoPresenter;
import eu.robojob.irscw.ui.teach.GeneralInfoView;
import eu.robojob.irscw.ui.teach.TeachPresenter;
import eu.robojob.irscw.ui.teach.TeachStatusPresenter;
import eu.robojob.irscw.ui.teach.TeachStatusView;

public class RoboSoftAppFactory {

	private MainPresenter mainPresenter;
	private MenuBarPresenter menuBarPresenter;
	private ConfigurePresenter configurePresenter;
	private ProcessConfigurePresenter processConfigurationPresenter;
	private TeachPresenter teachPresenter;
	private AutomatePresenter automatePresenter;
	private ConfigureProcessFlowPresenter configureProcessFlowPresenter;
	private FixedProcessFlowPresenter teachProcessFlowPresenter;
	private FixedProcessFlowPresenter automateProcessFlowPresenter;
	private RobotPopUpPresenter robotPopUpPresenter;
	private ProcessMenuPresenter processConfigurationMenuPresenter;
	private ProcessOpenPresenter processOpenPresenter;
	private ProcessSavePresenter processSavePresenter;
	private AdminPresenter adminPresenter;
	private MainMenuPresenter mainMenuPresenter;
	private RobotAdminPresenter robotAdminPresenter;
	private RobotConfigurePresenter robotConfigurePresenter;
	private RobotGripperPresenter robotGripperPresenter;
	private DeviceAdminPresenter deviceAdminPresenter;
	
	private ProcessFlow processFlow;
	private ProcessFlowTimer processFlowTimer;

	private ProcessFlowManager processFlowManager;
	private DeviceManager deviceManager;
	private RobotManager robotManager;
	
	private DeviceMenuFactory deviceMenuFactory;
	private TransportMenuFactory transportMenuFactory;
	
	private KeyboardType keyboardType;
		
	public RoboSoftAppFactory(final DeviceManager deviceManager, final RobotManager robotManager, 
			final ProcessFlowManager processFlowManager, final KeyboardType keyboardType) {
		this.deviceManager = deviceManager;
		this.robotManager = robotManager;
		this.processFlowManager = processFlowManager;
		this.keyboardType = keyboardType;
	}
	
	public MainPresenter getMainPresenter() {
		if (mainPresenter == null) {
			MainView mainView = new MainView();
			mainPresenter = new MainPresenter(mainView, getMenuBarPresenter(), getConfigurePresenter(), getTeachPresenter(), getAutomatePresenter(), 
					getRobotPopUpPresenter(), getAdminPresenter());
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
	
	public ProcessFlowTimer getProcessFlowTimer() {
		if (processFlowTimer == null) {
			processFlowTimer = new ProcessFlowTimer(getProcessFlow());
		}
		return processFlowTimer;
	}
	
	public ConfigurePresenter getConfigurePresenter() {
		if (configurePresenter == null) {
			ConfigureView processConfigureView = new ConfigureView();
			configurePresenter = new ConfigurePresenter(processConfigureView, getKeyboardPresenter(), getNumericKeyboardPresenter(), getConfigureProcessFlowPresenter(), 
					getProcessConfigurationMenuPresenter(), getDeviceMenuFactory(), getTransportMenuFactory(), deviceManager);
		}
		return configurePresenter;
	}
	
	public TeachPresenter getTeachPresenter() {
		if (teachPresenter == null) {
			MainContentView view = new MainContentView();
			DisconnectedDevicesView disconnectedDevicesView = new DisconnectedDevicesView();
			teachPresenter = new TeachPresenter(view, getTeachProcessFlowPresenter(), getProcessFlow(), disconnectedDevicesView, getGeneralInfoPresenter(), getTeachStatusPresenter());
		}
		return teachPresenter;
	}
	
	private GeneralInfoPresenter getGeneralInfoPresenter() {
		GeneralInfoView generalInfoView = new GeneralInfoView(getProcessFlow());
		GeneralInfoPresenter generalInfoPresenter = new GeneralInfoPresenter(generalInfoView);
		return generalInfoPresenter;
	}
	
	private TeachStatusPresenter getTeachStatusPresenter() {
		TeachStatusView teachStatusView = new TeachStatusView();
		TeachStatusPresenter teachStatusPresenter = new TeachStatusPresenter(teachStatusView, getStatusPresenter());
		return teachStatusPresenter;
	}
	
	private StatusPresenter getStatusPresenter() {
		StatusView statusView = new StatusView();
		StatusPresenter statusPresenter = new StatusPresenter(statusView);
		return statusPresenter;
	}
	
	private AutomateStatusPresenter getAutomateStatusPresenter() {
		AutomateStatusView automateStatusView = new AutomateStatusView();
		TimingView timingView = new TimingView();
		AutomateStatusPresenter automateStatusPresenter = new AutomateStatusPresenter(automateStatusView, getStatusPresenter(), timingView);
		return automateStatusPresenter;
	}
	
	public AutomatePresenter getAutomatePresenter() {
		if (automatePresenter == null) {
			MainContentView view = new MainContentView();
			DisconnectedDevicesView disconnectedDevicesView = new DisconnectedDevicesView();
			automatePresenter = new AutomatePresenter(view, getAutomateProcessFlowPresenter(), disconnectedDevicesView,
					getProcessFlow(), getProcessFlowTimer(), getAutomateStatusPresenter());
		}
		return automatePresenter;
	}
	
	public RobotPopUpPresenter getRobotPopUpPresenter() {
		if (robotPopUpPresenter == null) {
			RobotPopUpView view = new RobotPopUpView();
			// TODO review: now fixed robot
			robotPopUpPresenter = new RobotPopUpPresenter(view, (FanucRobot) robotManager.getRobotByName("Fanuc M20iA"), getProcessFlow());
		}
		return robotPopUpPresenter;
	}
	
	public FullKeyboardPresenter getKeyboardPresenter() {
		FullKeyboardView keyboardView = new FullKeyboardView(keyboardType);
		FullKeyboardPresenter keyboardPresenter = new FullKeyboardPresenter(keyboardView);
		return keyboardPresenter;
	}
	
	public NumericKeyboardPresenter getNumericKeyboardPresenter() {
		NumericKeyboardView numericKeyboardView = new NumericKeyboardView();
		NumericKeyboardPresenter numericKeyboardPresenter = new NumericKeyboardPresenter(numericKeyboardView);
		return numericKeyboardPresenter;
	}
	
	public ProcessConfigurePresenter getProcessConfigurePresenter() {
		if (processConfigurationPresenter == null) {
			ProcessConfigureView processConfigurationView = new ProcessConfigureView();
			processConfigurationPresenter = new ProcessConfigurePresenter(processConfigurationView, getProcessFlow());
		}
		return processConfigurationPresenter;
	}
	
	public ConfigureProcessFlowPresenter getConfigureProcessFlowPresenter() {
		if (configureProcessFlowPresenter == null) {
			ProcessFlowView processFlowView = new ProcessFlowView(1);
			configureProcessFlowPresenter = new ConfigureProcessFlowPresenter(processFlowView);
		}
		return configureProcessFlowPresenter;
	}
	
	public FixedProcessFlowPresenter getTeachProcessFlowPresenter() {
		if (teachProcessFlowPresenter == null) {
			ProcessFlowView processFlowView = new ProcessFlowView(1);
			teachProcessFlowPresenter = new FixedProcessFlowPresenter(processFlowView);
		}
		return teachProcessFlowPresenter;
	}
	
	public FixedProcessFlowPresenter getAutomateProcessFlowPresenter() {
		if (automateProcessFlowPresenter == null) {
			ProcessFlowView processFlowView = new ProcessFlowView(2);
			automateProcessFlowPresenter = new FixedProcessFlowPresenter(processFlowView);
		}
		return automateProcessFlowPresenter;
	}
	
	public ProcessMenuPresenter getProcessConfigurationMenuPresenter() {
		if (processConfigurationMenuPresenter == null) {
			ProcessMenuView processConfigurationMenuView = new ProcessMenuView();
			processConfigurationMenuPresenter = new ProcessMenuPresenter(processConfigurationMenuView, getProcessConfigurePresenter(), getProcessSavePresenter(), 
					getProcessOpenPresenter(), getProcessFlow(), processFlowManager);
		}
		return processConfigurationMenuPresenter;
	}
	
	public ProcessSavePresenter getProcessSavePresenter() {
		if (processSavePresenter == null) {
			ProcessSaveView processSaveView = new ProcessSaveView();
			processSavePresenter = new ProcessSavePresenter(processSaveView, processFlowManager, getProcessFlow());
		}
		return processSavePresenter;
	}
	
	public ProcessOpenPresenter getProcessOpenPresenter() {
		if (processOpenPresenter == null) {
			ProcessOpenView processOpenView = new ProcessOpenView();
			//TODO update!
			processOpenPresenter = new ProcessOpenPresenter(processOpenView, getProcessFlow(), processFlowManager);
		}
		return processOpenPresenter;
	}
	
	public ProcessFlow getProcessFlow() {
		if (processFlow == null) {
			processFlow = processFlowManager.getLastProcessFlow();
			processFlow.initialize();
			if (processFlow == null) {
				processFlow = processFlowManager.createNewProcessFlow();
			}
		}
		return processFlow;
	}
	
	private DeviceMenuFactory getDeviceMenuFactory() {
		if (deviceMenuFactory == null) {
			deviceMenuFactory = new DeviceMenuFactory(deviceManager);
		}
		return deviceMenuFactory;
	}
	
	private TransportMenuFactory getTransportMenuFactory() {
		if (transportMenuFactory == null) {
			transportMenuFactory = new TransportMenuFactory(getProcessFlow());
		}
		return transportMenuFactory;
	}
	
	private AdminPresenter getAdminPresenter() {
		if (adminPresenter == null) {
			AdminView view = new AdminView();
			adminPresenter = new AdminPresenter(view, getMainMenuPresenter(), getKeyboardPresenter(), getNumericKeyboardPresenter());
		}
		return adminPresenter;
	}
	
	private MainMenuPresenter getMainMenuPresenter() {
		if (mainMenuPresenter == null) {
			MainMenuView view = new MainMenuView();
			mainMenuPresenter = new MainMenuPresenter(view, getRobotAdminPresenter(), getDeviceAdminPresenter());
		}
		return mainMenuPresenter;
	}
	
	private DeviceAdminPresenter getDeviceAdminPresenter() {
		if (deviceAdminPresenter == null) {
			SubMenuAdminView view = new SubMenuAdminView(); 
			DeviceMenuView menuView = new DeviceMenuView();
			DeviceMenuPresenter deviceMenuPresenter = new DeviceMenuPresenter(menuView);
			deviceAdminPresenter = new DeviceAdminPresenter(view, deviceMenuPresenter);
		}
		return deviceAdminPresenter;
	}
	
	private RobotAdminPresenter getRobotAdminPresenter() {
		if (robotAdminPresenter == null) {
			SubMenuAdminView view = new SubMenuAdminView();
			RobotMenuView menuView = new RobotMenuView();
			RobotMenuPresenter robotMenuPresenter = new RobotMenuPresenter(menuView, getRobotConfigurePresenter(), getRobotGripperPresenter());
			robotAdminPresenter = new RobotAdminPresenter(view, robotMenuPresenter);
		}
		return robotAdminPresenter;
	}
	
	private RobotConfigurePresenter getRobotConfigurePresenter() {
		if (robotConfigurePresenter == null) {
			RobotConfigureView view = new RobotConfigureView();
			robotConfigurePresenter = new RobotConfigurePresenter(view, robotManager);
		}
		return robotConfigurePresenter;
	}
	
	private RobotGripperPresenter getRobotGripperPresenter() {
		if (robotGripperPresenter == null) {
			RobotGripperView view = new RobotGripperView();
			robotGripperPresenter = new RobotGripperPresenter(view, robotManager);
		}
		return robotGripperPresenter;
	}
}
