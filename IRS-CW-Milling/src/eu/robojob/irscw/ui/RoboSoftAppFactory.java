package eu.robojob.irscw.ui;

import eu.robojob.irscw.external.device.DeviceManager;
import eu.robojob.irscw.external.device.WorkArea;
import eu.robojob.irscw.external.device.cnc.CNCMillingMachine;
import eu.robojob.irscw.external.device.cnc.CNCMillingMachine.CNCMillingMachineInterventionSettings;
import eu.robojob.irscw.external.device.cnc.CNCMillingMachine.CNCMillingMachinePickSettings;
import eu.robojob.irscw.external.device.cnc.CNCMillingMachine.CNCMillingMachinePutSettings;
import eu.robojob.irscw.external.device.cnc.CNCMillingMachine.CNCMillingMachineSettings;
import eu.robojob.irscw.external.device.cnc.CNCMillingMachine.CNCMillingMachineStartCylusSettings;
import eu.robojob.irscw.external.device.stacking.BasicStackPlate;
import eu.robojob.irscw.external.device.stacking.BasicStackPlate.BasicStackPlatePickSettings;
import eu.robojob.irscw.external.device.stacking.BasicStackPlate.BasicStackPlatePutSettings;
import eu.robojob.irscw.external.device.stacking.BasicStackPlate.BasicStackPlateSettings;
import eu.robojob.irscw.external.device.stacking.BasicStackPlate.WorkPieceOrientation;
import eu.robojob.irscw.external.robot.FanucRobot;
import eu.robojob.irscw.external.robot.FanucRobot.FanucRobotPickSettings;
import eu.robojob.irscw.external.robot.FanucRobot.FanucRobotPutSettings;
import eu.robojob.irscw.external.robot.FanucRobot.FanucRobotSettings;
import eu.robojob.irscw.external.robot.RobotManager;
import eu.robojob.irscw.positioning.Coordinates;
import eu.robojob.irscw.process.InterventionStep;
import eu.robojob.irscw.process.PickStep;
import eu.robojob.irscw.process.ProcessFlow;
import eu.robojob.irscw.process.ProcessingStep;
import eu.robojob.irscw.process.PutStep;
import eu.robojob.irscw.ui.automate.AutomatePresenter;
import eu.robojob.irscw.ui.automate.AutomateView;
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
import eu.robojob.irscw.ui.configure.transport.TransportMenuFactory;
import eu.robojob.irscw.ui.keyboard.KeyboardPresenter;
import eu.robojob.irscw.ui.keyboard.KeyboardView;
import eu.robojob.irscw.ui.keyboard.KeyboardView.KeyboardType;
import eu.robojob.irscw.ui.keyboard.NumericKeyboardPresenter;
import eu.robojob.irscw.ui.keyboard.NumericKeyboardView;
import eu.robojob.irscw.ui.main.flow.FixedProcessFlowPresenter;
import eu.robojob.irscw.ui.main.flow.ProcessFlowView;
import eu.robojob.irscw.ui.robot.RobotPopUpPresenter;
import eu.robojob.irscw.ui.robot.RobotPopUpView;
import eu.robojob.irscw.ui.teach.DisconnectedDevicesView;
import eu.robojob.irscw.ui.teach.GeneralInfoView;
import eu.robojob.irscw.ui.teach.StatusView;
import eu.robojob.irscw.ui.teach.TeachPresenter;
import eu.robojob.irscw.ui.teach.TeachView;
import eu.robojob.irscw.workpiece.WorkPiece;
import eu.robojob.irscw.workpiece.WorkPieceDimensions;

public class RoboSoftAppFactory {

	private MainPresenter mainPresenter;
	private MenuBarPresenter menuBarPresenter;
	private ConfigurePresenter configurePresenter;
	private KeyboardPresenter keyboardPresenter;
	private ProcessConfigurePresenter processConfigurationPresenter;
	private TeachPresenter teachPresenter;
	private AutomatePresenter automatePresenter;
	private NumericKeyboardPresenter numericKeyboardPresenter;
	private ConfigureProcessFlowPresenter configureProcessFlowPresenter;
	private FixedProcessFlowPresenter teachProcessFlowPresenter;
	private FixedProcessFlowPresenter automateProcessFlowPresenter;
	private RobotPopUpPresenter robotPopUpPresenter;
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
			mainPresenter = new MainPresenter(mainView, getMenuBarPresenter(), getConfigurePresenter(), getTeachPresenter(), getAutomatePresenter(), getRobotPopUpPresenter());
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
			configurePresenter = new ConfigurePresenter(processConfigureView, getKeyboardPresenter(), getNumericKeyboardPresenter(), getConfigureProcessFlowPresenter(), 
					getProcessConfigurationMenuPresenter(), getDeviceMenuFactory(), getTransportMenuFactory());
		}
		return configurePresenter;
	}
	
	public TeachPresenter getTeachPresenter() {
		if (teachPresenter == null) {
			TeachView view = new TeachView();
			DisconnectedDevicesView disconnectedDevicesView = new DisconnectedDevicesView();
			GeneralInfoView generalInfoView = new GeneralInfoView();
			StatusView statusView = new StatusView();
			teachPresenter = new TeachPresenter(view, getTeachProcessFlowPresenter(), getProcessFlow(), disconnectedDevicesView, generalInfoView, statusView);
		}
		return teachPresenter;
	}
	
	public AutomatePresenter getAutomatePresenter() {
		if (automatePresenter == null) {
			AutomateView view = new AutomateView();
			automatePresenter = new AutomatePresenter(view, getAutomateProcessFlowPresenter(), getProcessFlow());
		}
		return automatePresenter;
	}
	
	public RobotPopUpPresenter getRobotPopUpPresenter() {
		if (robotPopUpPresenter == null) {
			RobotPopUpView view = new RobotPopUpView();
			// TODO review: now fixed robot
			robotPopUpPresenter = new RobotPopUpPresenter(view, (FanucRobot) robotManager.getRobotById("Fanuc M20iA"));
		}
		return robotPopUpPresenter;
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
	
	public ConfigureProcessFlowPresenter getConfigureProcessFlowPresenter() {
		if (configureProcessFlowPresenter == null) {
			ProcessFlowView processFlowView = new ProcessFlowView();
			configureProcessFlowPresenter = new ConfigureProcessFlowPresenter(processFlowView);
		}
		return configureProcessFlowPresenter;
	}
	
	public FixedProcessFlowPresenter getTeachProcessFlowPresenter() {
		if (teachProcessFlowPresenter == null) {
			ProcessFlowView processFlowView = new ProcessFlowView();
			teachProcessFlowPresenter = new FixedProcessFlowPresenter(processFlowView, true);
		}
		return teachProcessFlowPresenter;
	}
	
	public FixedProcessFlowPresenter getAutomateProcessFlowPresenter() {
		if (automateProcessFlowPresenter == null) {
			ProcessFlowView processFlowView = new ProcessFlowView();
			automateProcessFlowPresenter = new FixedProcessFlowPresenter(processFlowView, false);
		}
		return automateProcessFlowPresenter;
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
			
			processFlow = new ProcessFlow("MAZAK OPEN HOUSE");
			
			// Fanuc M20iA
			FanucRobot robot = (FanucRobot) robotMgr.getRobotById("Fanuc M20iA");
			FanucRobotSettings robotSettings = (FanucRobotSettings) robot.getRobotSettings();
			processFlow.setRobotSettings(robot, robotSettings);
			
			// Basic Stack Plate
			BasicStackPlate stackPlate = (BasicStackPlate) deviceMgr.getStackingFromDeviceById("IRS M Basic");
			BasicStackPlatePickSettings stackPlatePickSettings = new BasicStackPlate.BasicStackPlatePickSettings(stackPlate.getWorkAreaById("IRS M Basic"));
			BasicStackPlateSettings stackPlateSettings = (BasicStackPlateSettings) stackPlate.getDeviceSettings();
			stackPlateSettings.setAmount(3);
			processFlow.setTotalAmount(3);
			stackPlateSettings.setDimensions(new WorkPieceDimensions(200, 127.5f, 100));
			stackPlateSettings.setOrientation(WorkPieceOrientation.TILTED);
			processFlow.setDeviceSettings(stackPlate, stackPlateSettings);
			
			// Mazak VRX J500
			CNCMillingMachine cncMilling = (CNCMillingMachine) deviceMgr.getCNCMachineById("Mazak VRX J500");
			CNCMillingMachinePutSettings cncPutSettings = new CNCMillingMachine.CNCMillingMachinePutSettings(cncMilling.getWorkAreaById("Mazak VRX Main"));
			CNCMillingMachineSettings cncMillingSetting = (CNCMillingMachineSettings) cncMilling.getDeviceSettings();
			WorkArea mainWorkArea = cncMilling.getWorkAreaById("Mazak VRX Main");
			cncMillingSetting.setClamping(mainWorkArea, mainWorkArea.getClampingById("Clamping 1"));
			processFlow.setDeviceSettings(cncMilling, cncMillingSetting);
			CNCMillingMachineStartCylusSettings cncStartCyclusSettings =  new CNCMillingMachine.CNCMillingMachineStartCylusSettings(mainWorkArea);
			CNCMillingMachineInterventionSettings cncInterventionSettings = new CNCMillingMachineInterventionSettings(mainWorkArea);
			InterventionStep intervention = new InterventionStep(cncMilling, robot, cncInterventionSettings, 10);
			CNCMillingMachinePickSettings cncPickSettings = new CNCMillingMachinePickSettings(mainWorkArea);
			
			// Robot settings for pick from Basic Stacker and put in CNC Milling Machine
			// pick
			FanucRobotPickSettings robotPickSettings1 = new FanucRobot.FanucRobotPickSettings();
			robotPickSettings1.setGripperHead(robot.getGripperBody().getGripperHead("A"));
			robotPickSettings1.setGripper(robot.getGripperBody().getGripper("Vacuum grip"));
			robotPickSettings1.setSmoothPoint(new Coordinates(stackPlate.getWorkAreaById("IRS M Basic").getActiveClamping().getSmoothFromPoint()));
			robotPickSettings1.setWorkArea(stackPlate.getWorkAreaById("IRS M Basic"));
			robotPickSettings1.setClampHeight(stackPlate.getWorkAreaById("IRS M Basic").getActiveClamping().getHeight());
			WorkPieceDimensions dimensions1 = new WorkPieceDimensions(200, 127.5f, 100);
			WorkPiece workPiece1 = new WorkPiece(WorkPiece.Type.RAW, dimensions1);
			robotPickSettings1.setWorkPiece(workPiece1);
			// general
			robotSettings.setGripper(robot.getGripperBody().getGripperHead("A"), robot.getGripperBody().getGripper("Vacuum grip"));
			// put
			FanucRobotPutSettings robotPutSettings1 = new FanucRobot.FanucRobotPutSettings();
			robotPutSettings1.setGripperHead(robot.getGripperBody().getGripperHead("A"));
			robotPutSettings1.setGripper(robot.getGripperBody().getGripper("Vacuum grip"));
			robotPutSettings1.setSmoothPoint(new Coordinates(mainWorkArea.getClampingById("Clamping 1").getSmoothToPoint()));
			robotPutSettings1.setClampHeight(mainWorkArea.getClampingById("Clamping 1").getHeight());
			robotPutSettings1.setWorkArea(mainWorkArea);
			robotPutSettings1.setDoMachineAirblow(true);
			
			// PICK 1
			PickStep pick1 = new PickStep(robot, stackPlate, stackPlatePickSettings, robotPickSettings1);
			// PUT 1
			PutStep put1 = new PutStep(robot, cncMilling, cncPutSettings, robotPutSettings1);
			
			// PROCESSING 1 
			ProcessingStep processing1 = new ProcessingStep(cncMilling, cncStartCyclusSettings);
			
			// Robot settings for pick from CNC Milling Machine and put in Basic Stacker
			//pick
			FanucRobotPickSettings robotPickSettings2 = new FanucRobot.FanucRobotPickSettings();
			robotPickSettings2.setGripperHead(robot.getGripperBody().getGripperHead("B"));
			robotPickSettings2.setGripper(robot.getGripperBody().getGripper("2P clamp grip"));
			robotPickSettings2.setSmoothPoint(new Coordinates(mainWorkArea.getClampingById("Clamping 1").getSmoothFromPoint()));
			robotPickSettings2.setClampHeight(mainWorkArea.getClampingById("Clamping 1").getHeight());
			robotPickSettings2.setWorkArea(mainWorkArea);
			WorkPieceDimensions dimensions2 = new WorkPieceDimensions(140, 80, 100);
			WorkPiece workPiece2 = new WorkPiece(WorkPiece.Type.FINISHED, dimensions2);
			robotPickSettings2.setWorkPiece(workPiece2);
			// general
			robotSettings.setGripper(robot.getGripperBody().getGripperHead("B"), robot.getGripperBody().getGripper("2P clamp grip"));
			// put
			FanucRobotPutSettings robotPutSettings2 = new FanucRobot.FanucRobotPutSettings();
			robotPutSettings2.setGripperHead(robot.getGripperBody().getGripperHead("B"));
			robotPutSettings2.setGripper(robot.getGripperBody().getGripper("2P clamp grip"));
			robotPutSettings2.setSmoothPoint(new Coordinates(stackPlate.getWorkAreaById("IRS M Basic").getActiveClamping().getSmoothToPoint()));
			robotPutSettings2.setWorkArea(stackPlate.getWorkAreaById("IRS M Basic"));			
			robotPutSettings2.setClampHeight(stackPlate.getWorkAreaById("IRS M Basic").getActiveClamping().getHeight());
			BasicStackPlatePutSettings stackPlatePutSettings = new BasicStackPlate.BasicStackPlatePutSettings(stackPlate.getWorkAreaById("IRS M Basic"));
			
			// PICK 2
			PickStep pick2 = new PickStep(robot, cncMilling, cncPickSettings, robotPickSettings2);
			PutStep put2 = new PutStep(robot, stackPlate, stackPlatePutSettings, robotPutSettings2);
			
			
			
			
			// creating process flow
			processFlow.addStep(pick1);
			processFlow.addStep(put1);
			processFlow.addStep(processing1);
			//processFlow.addStep(intervention);
			processFlow.addStep(pick2);
			processFlow.addStep(put2);
			
			processFlow.loadAllSettings();
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
