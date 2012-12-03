package eu.robojob.irscw.ui;

import java.util.Properties;

import eu.robojob.irscw.PropertiesProcessFlowFactory;
import eu.robojob.irscw.external.device.ClampingType.Type;
import eu.robojob.irscw.external.device.DeviceManager;
import eu.robojob.irscw.external.device.WorkArea;
import eu.robojob.irscw.external.device.cnc.CNCMillingMachine;
import eu.robojob.irscw.external.device.cnc.CNCMillingMachine.CNCMillingMachinePickSettings;
import eu.robojob.irscw.external.device.cnc.CNCMillingMachine.CNCMillingMachinePutSettings;
import eu.robojob.irscw.external.device.cnc.CNCMillingMachine.CNCMillingMachineSettings;
import eu.robojob.irscw.external.device.cnc.CNCMillingMachine.CNCMillingMachineStartCylusSettings;
import eu.robojob.irscw.external.device.pre.PrageDevice;
import eu.robojob.irscw.external.device.pre.PrageDevice.PrageDevicePickSettings;
import eu.robojob.irscw.external.device.pre.PrageDevice.PrageDevicePutSettings;
import eu.robojob.irscw.external.device.pre.PrageDevice.PrageDeviceSettings;
import eu.robojob.irscw.external.device.pre.PrageDevice.PrageDeviceStartCyclusSettings;
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
import eu.robojob.irscw.process.PickAfterWaitStep;
import eu.robojob.irscw.process.PickStep;
import eu.robojob.irscw.process.ProcessFlow;
import eu.robojob.irscw.process.ProcessFlowTimer;
import eu.robojob.irscw.process.ProcessingStep;
import eu.robojob.irscw.process.PutAndWaitStep;
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
import eu.robojob.irscw.ui.teach.OffsetPresenter;
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
	
	private PropertiesProcessFlowFactory propertiesProcessFlowFactory;
	
	private ProcessFlowTimer processFlowTimer;
	
	Properties properties;
	
	public RoboSoftAppFactory(Properties properties) {
		this.properties = properties;
	}
	
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
					getProcessConfigurationMenuPresenter(), getDeviceMenuFactory(), getTransportMenuFactory(), getDeviceManager());
		}
		return configurePresenter;
	}
	
	public TeachPresenter getTeachPresenter() {
		if (teachPresenter == null) {
			TeachView view = new TeachView();
			DisconnectedDevicesView disconnectedDevicesView = new DisconnectedDevicesView();
			GeneralInfoView generalInfoView = new GeneralInfoView();
			StatusView statusView = new StatusView();
			OffsetPresenter offsetPresenter = new OffsetPresenter(getNumericKeyboardPresenter());
			teachPresenter = new TeachPresenter(view, getTeachProcessFlowPresenter(), getProcessFlow(), disconnectedDevicesView, generalInfoView, statusView, offsetPresenter);
		}
		return teachPresenter;
	}
	
	public AutomatePresenter getAutomatePresenter() {
		if (automatePresenter == null) {
			AutomateView view = new AutomateView();
			automatePresenter = new AutomatePresenter(view, getAutomateProcessFlowPresenter(), getProcessFlow(), getProcessFlowTimer());
		}
		return automatePresenter;
	}
	
	public RobotPopUpPresenter getRobotPopUpPresenter() {
		if (robotPopUpPresenter == null) {
			RobotPopUpView view = new RobotPopUpView();
			// TODO review: now fixed robot
			robotPopUpPresenter = new RobotPopUpPresenter(view, (FanucRobot) robotManager.getRobotById("Fanuc M20iA"), getProcessFlow());
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
		NumericKeyboardPresenter numericKeyboardPresenter = null;
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
			processOpenPresenter = new ProcessOpenPresenter(processOpenView, getProcessFlow(), getPropertiesProcessFlowFactory());
		}
		return processOpenPresenter;
	}
	
	public ProcessFlow getProcessFlow() {
		DeviceManager deviceMgr = getDeviceManager();
		RobotManager robotMgr = getRobotManager();
		if (processFlow == null) {
			
			
			//---------GENERAL---------
			
			Integer totalAmount = 4;
			
			processFlow = new ProcessFlow("MAZAK OPEN HOUSE");
			processFlow.setTotalAmount(totalAmount);
			
			WorkPieceDimensions rawDimensions = new WorkPieceDimensions(140f, 100f, 40);
			WorkPiece rawWorkPiece = new WorkPiece(WorkPiece.Type.RAW, rawDimensions);
			WorkPieceDimensions finishedDimensions = new WorkPieceDimensions(140f, 100f, 40);
			WorkPiece finishedWorkPiece = new WorkPiece(WorkPiece.Type.FINISHED, finishedDimensions);
			
			//----------ROBOT----------
			
			// Fanuc M20iA
			FanucRobot robot = (FanucRobot) robotMgr.getRobotById("Fanuc M20iA");
			FanucRobotSettings robotSettings = (FanucRobotSettings) robot.getRobotSettings();
			robotSettings.setGripper(robot.getGripperBody().getGripperHead("A"), robot.getGripperBody().getGripper("2P clamp grip A"));
			robotSettings.setGripper(robot.getGripperBody().getGripperHead("B"), robot.getGripperBody().getGripper("Vacuum grip"));
			processFlow.setRobotSettings(robot, robotSettings);
			
			
			//----------DEVICES----------
			
			// Basic Stack Plate
			BasicStackPlate stackPlate = (BasicStackPlate) deviceMgr.getStackingFromDeviceById("IRS M Basic");
			BasicStackPlateSettings stackPlateSettings = (BasicStackPlateSettings) stackPlate.getDeviceSettings();
			stackPlateSettings.setAmount(totalAmount);
			stackPlateSettings.setDimensions(rawDimensions);
			stackPlateSettings.setOrientation(WorkPieceOrientation.TILTED);
			processFlow.setDeviceSettings(stackPlate, stackPlateSettings);
			
			// Präge Device
			PrageDevice prageDevice = (PrageDevice) deviceMgr.getPreProcessingDeviceById("Präge");
			PrageDeviceSettings prageDeviceSettings = (PrageDeviceSettings) prageDevice.getDeviceSettings();
			processFlow.setDeviceSettings(prageDevice, prageDeviceSettings);
			
			// CNC Milling Machine
			CNCMillingMachine cncMilling = (CNCMillingMachine) deviceMgr.getCNCMachineById("Mazak VRX J500");
			CNCMillingMachineSettings cncMillingSetting = (CNCMillingMachineSettings) cncMilling.getDeviceSettings();
			WorkArea mainWorkArea = cncMilling.getWorkAreaById("Mazak VRX Main");
			cncMillingSetting.setClamping(mainWorkArea, mainWorkArea.getClampingById("Clamping 1"));
			processFlow.setDeviceSettings(cncMilling, cncMillingSetting);
			
			processFlow.getClampingType().setType(Type.LENGTH);
			
			//---------STEPS----------
			
			// PICK FROM STACKER
			// Device: Basic stack plate
			BasicStackPlatePickSettings stackPlatePickSettings = new BasicStackPlate.BasicStackPlatePickSettings(stackPlate.getWorkAreaById("IRS M Basic"));
			// Robot: Fanuc Robot
			FanucRobotPickSettings robotPickSettings1 = new FanucRobot.FanucRobotPickSettings();
			robotPickSettings1.setGripperHead(robot.getGripperBody().getGripperHead("A"));
			robotPickSettings1.setSmoothPoint(new Coordinates(stackPlate.getWorkAreaById("IRS M Basic").getActiveClamping().getSmoothFromPoint()));
			robotPickSettings1.setWorkArea(stackPlate.getWorkAreaById("IRS M Basic"));
			robotPickSettings1.setWorkPiece(rawWorkPiece);		
			// Pick step
			PickStep pick1 = new PickStep(robot, stackPlate, stackPlatePickSettings, robotPickSettings1);
			
			
			// PUT AND WAIT ON PRÄGE DEVICE
			// Device: Präge device
			PrageDevicePickSettings pragePickSettings = new PrageDevice.PrageDevicePickSettings(prageDevice.getWorkAreaById("Präge"));
			PrageDeviceStartCyclusSettings prageStartCyclusSettings = new PrageDevice.PrageDeviceStartCyclusSettings(prageDevice.getWorkAreaById("Präge"));
			PrageDevicePutSettings pragePutSettings = new PrageDevice.PrageDevicePutSettings(prageDevice.getWorkAreaById("Präge"));
			// Robot: Fanuc Robot
			// put and wait
			FanucRobotPutSettings robotPutSettings1 = new FanucRobot.FanucRobotPutSettings();
			robotPutSettings1.setGripperHead(robot.getGripperBody().getGripperHead("A"));
			robotPutSettings1.setSmoothPoint(new Coordinates(prageDevice.getWorkAreaById("Präge").getClampingById("Clamping 5").getSmoothToPoint()));
			robotPutSettings1.setWorkArea(prageDevice.getWorkAreaById("Präge"));
			// pick after wait
			FanucRobotPickSettings robotPickSettings2 = new FanucRobot.FanucRobotPickSettings();
			robotPickSettings2.setGripperHead(robot.getGripperBody().getGripperHead("A"));
			robotPickSettings2.setSmoothPoint(new Coordinates(prageDevice.getWorkAreaById("Präge").getClampingById("Clamping 5").getSmoothFromPoint()));
			robotPickSettings2.setWorkArea(prageDevice.getWorkAreaById("Präge"));
			robotPickSettings2.setWorkPiece(rawWorkPiece);
			// Put and wait step
			PutAndWaitStep putAndWait1 = new PutAndWaitStep(robot, prageDevice, pragePutSettings, robotPutSettings1);
			PickAfterWaitStep pickAfterWait1 = new PickAfterWaitStep(robot, prageDevice, pragePickSettings, robotPickSettings2);
			ProcessingStep processing1 = new ProcessingStep(prageDevice, prageStartCyclusSettings);
			
			
			// PUT IN CNC VRX 
			// Device: CNCMilling Machine
			CNCMillingMachinePutSettings cncPutSettings = new CNCMillingMachine.CNCMillingMachinePutSettings(cncMilling.getWorkAreaById("Mazak VRX Main"));
			// Robot: Fanuc Robot
			FanucRobotPutSettings robotPutSettings2 = new FanucRobot.FanucRobotPutSettings();
			robotPutSettings2.setGripperHead(robot.getGripperBody().getGripperHead("A"));
			robotPutSettings2.setSmoothPoint(new Coordinates(cncMilling.getWorkAreaById("Mazak VRX Main").getClampingById("Clamping 1").getSmoothToPoint()));
			robotPutSettings2.setWorkArea(cncMilling.getWorkAreaById("Mazak VRX Main"));
			robotPutSettings2.setDoMachineAirblow(true);
			// Put step
			PutStep put1 = new PutStep(robot, cncMilling, cncPutSettings, robotPutSettings2);
			
			
			// PROCESSING (CNC VRX)
			CNCMillingMachineStartCylusSettings cncStartCyclusSettings =  new CNCMillingMachine.CNCMillingMachineStartCylusSettings(cncMilling.getWorkAreaById("Mazak VRX Main"));
			ProcessingStep processing2 = new ProcessingStep(cncMilling, cncStartCyclusSettings);

			
			// PICK FROM CNC VRX
			// Device: CNCMilling Machine
			CNCMillingMachinePickSettings cncPickSettings = new CNCMillingMachinePickSettings(cncMilling.getWorkAreaById("Mazak VRX Main"));
			// Robot: Fanuc Robot
			FanucRobotPickSettings robotPickSettings3 = new FanucRobot.FanucRobotPickSettings();
			robotPickSettings3.setGripperHead(robot.getGripperBody().getGripperHead("B"));
			//robotPickSettings3.setGripperHead(robot.getGripperBody().getGripperHead("A"));
			robotPickSettings3.setSmoothPoint(new Coordinates(cncMilling.getWorkAreaById("Mazak VRX Main").getClampingById("Clamping 1").getSmoothFromPoint()));
			robotPickSettings3.setWorkArea(mainWorkArea);
			robotPickSettings3.setDoMachineAirblow(true);
			robotPickSettings3.setWorkPiece(finishedWorkPiece);
			// Pick step
			PickStep pick2 = new PickStep(robot, cncMilling, cncPickSettings, robotPickSettings3);
			
			
			// PUT ON BASIC STACKER
			// Device: Basic Stacker
			BasicStackPlatePutSettings stackPlatePutSettings = new BasicStackPlate.BasicStackPlatePutSettings(stackPlate.getWorkAreaById("IRS M Basic"));
			// Robot: Fanuc Robot
			FanucRobotPutSettings robotPutSettings3 = new FanucRobot.FanucRobotPutSettings();
			robotPutSettings3.setGripperHead(robot.getGripperBody().getGripperHead("B"));
			//robotPutSettings3.setGripperHead(robot.getGripperBody().getGripperHead("A"));
			robotPutSettings3.setSmoothPoint(new Coordinates(stackPlate.getWorkAreaById("IRS M Basic").getActiveClamping().getSmoothToPoint()));
			robotPutSettings3.setWorkArea(stackPlate.getWorkAreaById("IRS M Basic"));			
			PutStep put2 = new PutStep(robot, stackPlate, stackPlatePutSettings, robotPutSettings3);
			
			/*pick1.setTeachedOffset(new Coordinates());
			putAndWait1.setTeachedOffset(new Coordinates());
			put1.setTeachedOffset(new Coordinates());
			pick2.setTeachedOffset(new Coordinates());
			put2.setTeachedOffset(new Coordinates());*/
			Coordinates teachedFinishedOnstacker = new Coordinates(1.788f, 2.853f, -8.03f, 0, 0, 0);
			Coordinates teachedRawOnstacker = new Coordinates(0.0551f, 1.291f, -6.76f, 0, 0, 0);
			Coordinates teachedOnPrage = new Coordinates(0.55f, -1.3f, -13.76f, 0, 0, -360);
			
			Coordinates pickFromMachineOffset = new Coordinates(teachedOnPrage);
			pickFromMachineOffset.minus(teachedRawOnstacker);
			pickFromMachineOffset.plus(teachedFinishedOnstacker);
			
			pick1.setRelativeTeachedOffset(teachedRawOnstacker);
			putAndWait1.setRelativeTeachedOffset(teachedOnPrage);
			put1.setRelativeTeachedOffset(teachedOnPrage);
			pick2.setRelativeTeachedOffset(pickFromMachineOffset);
			put2.setRelativeTeachedOffset(teachedFinishedOnstacker);
			
			// create process flow:
			processFlow.addStep(pick1);
			processFlow.addStep(putAndWait1);
			processFlow.addStep(processing1);
			processFlow.addStep(pickAfterWait1);
			processFlow.addStep(put1);
			processFlow.addStep(processing2);
			processFlow.addStep(pick2);
			processFlow.addStep(put2);
			
			processFlow.loadAllSettings();
						
			processFlowTimer = new ProcessFlowTimer(processFlow);

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
			deviceManager = new DeviceManager(getRobotManager(), properties);
		}
		return deviceManager;
	}
	
	private RobotManager getRobotManager() {
		if (robotManager == null) {
			robotManager = new RobotManager(properties);
		}
		return robotManager;
	}
	
	private PropertiesProcessFlowFactory getPropertiesProcessFlowFactory() {
		if (propertiesProcessFlowFactory == null) {
			propertiesProcessFlowFactory = new PropertiesProcessFlowFactory(getDeviceManager(), getRobotManager());
		}
		return propertiesProcessFlowFactory;
	}
}
