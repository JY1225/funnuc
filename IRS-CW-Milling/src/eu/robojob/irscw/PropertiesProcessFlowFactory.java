package eu.robojob.irscw;

import java.util.Properties;

import eu.robojob.irscw.external.device.ClampingManner;
import eu.robojob.irscw.external.device.ClampingManner.Type;
import eu.robojob.irscw.external.device.DeviceManager;
import eu.robojob.irscw.external.device.DevicePickSettings;
import eu.robojob.irscw.external.device.DevicePutSettings;
import eu.robojob.irscw.external.device.DeviceSettings;
import eu.robojob.irscw.external.device.WorkArea;
import eu.robojob.irscw.external.device.processing.ProcessingDeviceStartCyclusSettings;
import eu.robojob.irscw.external.device.processing.cnc.CNCMillingMachine;
import eu.robojob.irscw.external.device.processing.prage.PrageDevice;
import eu.robojob.irscw.external.device.stacking.BasicStackPlate;
import eu.robojob.irscw.external.device.stacking.BasicStackPlate.WorkPieceOrientation;
import eu.robojob.irscw.external.device.stacking.BasicStackPlateSettings;
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
import eu.robojob.irscw.workpiece.WorkPiece;
import eu.robojob.irscw.workpiece.WorkPieceDimensions;

public class PropertiesProcessFlowFactory {

	private DeviceManager deviceManager;
	private RobotManager robotManager;
	private ProcessFlowTimer processFlowTimer;
		
	public PropertiesProcessFlowFactory(DeviceManager deviceManager, RobotManager robotManager) {
		this.deviceManager = deviceManager;
		this.robotManager = robotManager;
	}
	
	public ProcessFlow loadProcessFlow(Properties properties) {
		// first read all parameters
		String processFlowId = (properties.getProperty("processflow.id")).toUpperCase();
		boolean tilted = Boolean.parseBoolean(properties.getProperty("basicstackplate.orientation.tilted"));
		float rawWpWidth = Float.parseFloat(properties.getProperty("workpiece.raw.width"));
		float rawWpLength = Float.parseFloat(properties.getProperty("workpiece.raw.length"));
		float rawWpHeight = Float.parseFloat(properties.getProperty("workpiece.raw.height"));
		WorkPieceDimensions rawWorkPieceDimensions = new WorkPieceDimensions(rawWpLength, rawWpWidth, rawWpHeight);
		float finishedWpWidth = Float.parseFloat(properties.getProperty("workpiece.finished.width"));
		float finishedWpLength = Float.parseFloat(properties.getProperty("workpiece.finished.length"));
		float finishedWpHeight = Float.parseFloat(properties.getProperty("workpiece.finished.height"));
		WorkPieceDimensions finishedWorkPieceDimensions = new WorkPieceDimensions(finishedWpLength, finishedWpWidth, finishedWpHeight);
		String clampingId = properties.getProperty("cncmachine.clampingid");
		boolean clampLength = Boolean.parseBoolean(properties.getProperty("cncmachine.clamping.length"));
		String robotHeadBeforeMachineId = properties.getProperty("robot.head.before");
		String robotHeadAfterMachineId = properties.getProperty("robot.head.after");
		String gripperHeadAId = properties.getProperty("robot.head.a.gripper");
		String gripperHeadBId = properties.getProperty("robot.head.b.gripper");
		boolean hasPrageStep = Boolean.parseBoolean(properties.getProperty("processflow.hasprage"));
		int processFlowAmount = Integer.parseInt(properties.getProperty("processflow.amount"));
		// relative teached offset pick stacker
		float teachedPickStackerX = Float.parseFloat(properties.getProperty("teachedoffset.stacker.pick.x"));
		float teachedPickStackerY = Float.parseFloat(properties.getProperty("teachedoffset.stacker.pick.y"));
		float teachedPickStackerZ = Float.parseFloat(properties.getProperty("teachedoffset.stacker.pick.z"));
		float teachedPickStackerR = Float.parseFloat(properties.getProperty("teachedoffset.stacker.pick.r"));
		Coordinates relativeTeachedPickStacker = new Coordinates(teachedPickStackerX, teachedPickStackerY, teachedPickStackerZ, 0, 0, teachedPickStackerR);
		// relative teached offset put prage
		Coordinates relativeTeachedPutPrage = null;
		if (hasPrageStep) {
			float teachedPutPrageX = Float.parseFloat(properties.getProperty("teachedoffset.prage.put.x"));
			float teachedPutPrageY = Float.parseFloat(properties.getProperty("teachedoffset.prage.put.y"));
			float teachedPutPrageZ = Float.parseFloat(properties.getProperty("teachedoffset.prage.put.z"));
			float teachedPutPrageR = Float.parseFloat(properties.getProperty("teachedoffset.prage.put.r"));
			relativeTeachedPutPrage = new Coordinates(teachedPutPrageX, teachedPutPrageY, teachedPutPrageZ, 0, 0, teachedPutPrageR);
		}
		// relative teached offset put machine
		float teachedPutMachineX = Float.parseFloat(properties.getProperty("teachedoffset.machine.put.x"));
		float teachedPutMachineY = Float.parseFloat(properties.getProperty("teachedoffset.machine.put.y"));
		float teachedPutMachineZ = Float.parseFloat(properties.getProperty("teachedoffset.machine.put.z"));
		float teachedPutMachineR = Float.parseFloat(properties.getProperty("teachedoffset.machine.put.r"));
		Coordinates relativeTeachedPutMachine = new Coordinates(teachedPutMachineX, teachedPutMachineY, teachedPutMachineZ, 0, 0, teachedPutMachineR);
		// relative teached offset pick machine
		float teachedPickMachineX = Float.parseFloat(properties.getProperty("teachedoffset.machine.pick.x"));
		float teachedPickMachineY = Float.parseFloat(properties.getProperty("teachedoffset.machine.pick.y"));
		float teachedPickMachineZ = Float.parseFloat(properties.getProperty("teachedoffset.machine.pick.z"));
		float teachedPickMachineR = Float.parseFloat(properties.getProperty("teachedoffset.machine.pick.r"));
		Coordinates relativeTeachedPickMachine = new Coordinates(teachedPickMachineX, teachedPickMachineY, teachedPickMachineZ, 0, 0, teachedPickMachineR);
		// relative teached offset put stacker
		float teachedPutStackerX = Float.parseFloat(properties.getProperty("teachedoffset.stacker.put.x"));
		float teachedPutStackerY = Float.parseFloat(properties.getProperty("teachedoffset.stacker.put.y"));
		float teachedPutStackerZ = Float.parseFloat(properties.getProperty("teachedoffset.stacker.put.z"));
		float teachedPutStackerR = Float.parseFloat(properties.getProperty("teachedoffset.stacker.put.r"));
		Coordinates relativeTeachedPutStacker = new Coordinates(teachedPutStackerX, teachedPutStackerY, teachedPutStackerZ, 0, 0, teachedPutStackerR);
		
		int finishedAmount = Integer.parseInt(properties.getProperty("processflow.finishedamount"));
		
		return createProcessFlow(processFlowId, processFlowAmount, finishedAmount, rawWorkPieceDimensions, finishedWorkPieceDimensions, tilted, hasPrageStep, clampingId, clampLength, robotHeadBeforeMachineId, robotHeadAfterMachineId, gripperHeadAId, gripperHeadBId, relativeTeachedPickStacker, relativeTeachedPutPrage, relativeTeachedPutMachine, relativeTeachedPickMachine, relativeTeachedPutStacker);
	}
	
	private ProcessFlow createProcessFlow(String processFlowId, int amount, int finishedAmount, WorkPieceDimensions rawDimensions, WorkPieceDimensions finishedDimensions, boolean tilted, boolean hasPrage, 
			String clampingId, boolean clampLength, String robotHeadIdBefore,
			String robotHeadIdAfter, String gripperHeadAId, String gripperHeadBId, Coordinates relativeTeachedPickStacker, Coordinates relativeTeachedPutPrage, 
				Coordinates relativeTeachedPutMachine, Coordinates relativeTeachedPickMachine, Coordinates relativeTeachedPutStacker) {
		ProcessFlow processFlow = new ProcessFlow(processFlowId);
		processFlow.setTotalAmount(amount);
		
		//----------ROBOT----------
		
		// Fanuc M20iA
		FanucRobot robot = (FanucRobot) robotManager.getRobotById("Fanuc M20iA");
		FanucRobotSettings robotSettings = (FanucRobotSettings) robot.getRobotSettings();
		robotSettings.setGripper(robot.getGripperBody().getGripperHead("A"), robot.getGripperBody().getGripper(gripperHeadAId));
		robotSettings.setGripper(robot.getGripperBody().getGripperHead("B"), robot.getGripperBody().getGripper(gripperHeadBId));
		processFlow.setRobotSettings(robot, robotSettings);
		
		//----------DEVICES----------
		
		// Basic Stack Plate
		BasicStackPlate stackPlate = (BasicStackPlate) deviceManager.getStackingFromDeviceById("IRS M Basic");
		BasicStackPlateSettings stackPlateSettings = (BasicStackPlateSettings) stackPlate.getDeviceSettings();
		stackPlateSettings.setAmount(amount);
		stackPlateSettings.setDimensions(rawDimensions);
		if (tilted) {
			stackPlateSettings.setOrientation(WorkPieceOrientation.TILTED);
		} else {
			stackPlateSettings.setOrientation(WorkPieceOrientation.HORIZONTAL);
		}
		processFlow.setDeviceSettings(stackPlate, stackPlateSettings);
		
		// Präge Device
		PrageDevice prageDevice = (PrageDevice) deviceManager.getPreProcessingDeviceById("Präge");
		DeviceSettings prageDeviceSettings = (DeviceSettings) prageDevice.getDeviceSettings();
		processFlow.setDeviceSettings(prageDevice, prageDeviceSettings);
		
		// CNC Milling Machine
		CNCMillingMachine cncMilling = (CNCMillingMachine) deviceManager.getCNCMachineById("Mazak VRX J500");
		DeviceSettings cncMillingSetting = cncMilling.getDeviceSettings();
		WorkArea mainWorkArea = cncMilling.getWorkAreaById("Mazak VRX Main");
		cncMillingSetting.setClamping(mainWorkArea, mainWorkArea.getClampingById(clampingId));
		processFlow.setDeviceSettings(cncMilling, cncMillingSetting);
		processFlow.setClampingType(new ClampingManner());
		if (clampLength) {
			processFlow.getClampingType().setType(Type.LENGTH);
		} else {
			processFlow.getClampingType().setType(Type.WIDTH);
		}
		
		//---------STEPS----------
		
		// PICK FROM STACKER
		// Device: Basic stack plate
		DevicePickSettings stackPlatePickSettings = new DevicePickSettings(stackPlate.getWorkAreaById("IRS M Basic"));
		// Robot: Fanuc Robot
		FanucRobotPickSettings robotPickSettings1 = new FanucRobot.FanucRobotPickSettings();
		robotPickSettings1.setGripperHead(robot.getGripperBody().getGripperHead(robotHeadIdBefore));
		robotPickSettings1.setSmoothPoint(new Coordinates(stackPlate.getWorkAreaById("IRS M Basic").getActiveClamping().getSmoothFromPoint()));
		robotPickSettings1.setWorkArea(stackPlate.getWorkAreaById("IRS M Basic"));
		WorkPiece rawWorkPiece = new WorkPiece(eu.robojob.irscw.workpiece.WorkPiece.Type.RAW, rawDimensions);
		robotPickSettings1.setWorkPiece(rawWorkPiece);		
		// Pick step
		PickStep pick1 = new PickStep(robot, stackPlate, stackPlatePickSettings, robotPickSettings1);
		
		// PUT AND WAIT ON PRÄGE DEVICE
		// Device: Präge device
		DevicePickSettings pragePickSettings = new DevicePickSettings(prageDevice.getWorkAreaById("Präge"));
		ProcessingDeviceStartCyclusSettings prageStartCyclusSettings = new ProcessingDeviceStartCyclusSettings(prageDevice.getWorkAreaById("Präge"));
		DevicePutSettings pragePutSettings = new DevicePutSettings(prageDevice.getWorkAreaById("Präge"));
		// Robot: Fanuc Robot
		// put and wait
		FanucRobotPutSettings robotPutSettings1 = new FanucRobot.FanucRobotPutSettings();
		robotPutSettings1.setGripperHead(robot.getGripperBody().getGripperHead(robotHeadIdBefore));
		robotPutSettings1.setSmoothPoint(new Coordinates(prageDevice.getWorkAreaById("Präge").getClampingById("Clamping 5").getSmoothToPoint()));
		robotPutSettings1.setWorkArea(prageDevice.getWorkAreaById("Präge"));
		// pick after wait
		FanucRobotPickSettings robotPickSettings2 = new FanucRobot.FanucRobotPickSettings();
		robotPickSettings2.setGripperHead(robot.getGripperBody().getGripperHead(robotHeadIdBefore));
		robotPickSettings2.setSmoothPoint(new Coordinates(prageDevice.getWorkAreaById("Präge").getClampingById("Clamping 5").getSmoothFromPoint()));
		robotPickSettings2.setWorkArea(prageDevice.getWorkAreaById("Präge"));
		robotPickSettings2.setWorkPiece(rawWorkPiece);
		// Put and wait step
		PutAndWaitStep putAndWait1 = new PutAndWaitStep(robot, prageDevice, pragePutSettings, robotPutSettings1);
		PickAfterWaitStep pickAfterWait1 = new PickAfterWaitStep(robot, prageDevice, pragePickSettings, robotPickSettings2);
		ProcessingStep processing1 = new ProcessingStep(prageDevice, prageStartCyclusSettings);
		
		// PUT IN CNC VRX 
		// Device: CNCMilling Machine
		DevicePutSettings cncPutSettings = new DevicePutSettings(cncMilling.getWorkAreaById("Mazak VRX Main"));
		// Robot: Fanuc Robot
		FanucRobotPutSettings robotPutSettings2 = new FanucRobot.FanucRobotPutSettings();
		robotPutSettings2.setGripperHead(robot.getGripperBody().getGripperHead(robotHeadIdBefore));
		robotPutSettings2.setSmoothPoint(new Coordinates(cncMilling.getWorkAreaById("Mazak VRX Main").getClampingById("Clamping 1").getSmoothToPoint()));
		robotPutSettings2.setWorkArea(cncMilling.getWorkAreaById("Mazak VRX Main"));
		robotPutSettings2.setDoMachineAirblow(true);
		// Put step
		PutStep put1 = new PutStep(robot, cncMilling, cncPutSettings, robotPutSettings2);

		// PROCESSING (CNC VRX)
		ProcessingDeviceStartCyclusSettings cncStartCyclusSettings =  new ProcessingDeviceStartCyclusSettings(cncMilling.getWorkAreaById("Mazak VRX Main"));
		ProcessingStep processing2 = new ProcessingStep(cncMilling, cncStartCyclusSettings);
		
		// PICK FROM CNC VRX
		// Device: CNCMilling Machine
		DevicePickSettings cncPickSettings = new DevicePickSettings(cncMilling.getWorkAreaById("Mazak VRX Main"));
		// Robot: Fanuc Robot
		FanucRobotPickSettings robotPickSettings3 = new FanucRobot.FanucRobotPickSettings();
		robotPickSettings3.setGripperHead(robot.getGripperBody().getGripperHead(robotHeadIdAfter));
		//robotPickSettings3.setGripperHead(robot.getGripperBody().getGripperHead("A"));
		robotPickSettings3.setSmoothPoint(new Coordinates(cncMilling.getWorkAreaById("Mazak VRX Main").getClampingById("Clamping 1").getSmoothFromPoint()));
		robotPickSettings3.setWorkArea(mainWorkArea);
		robotPickSettings3.setDoMachineAirblow(true);
		WorkPiece finishedWorkPiece = new WorkPiece(eu.robojob.irscw.workpiece.WorkPiece.Type.FINISHED, finishedDimensions);
		robotPickSettings3.setWorkPiece(finishedWorkPiece);
		// Pick step
		PickStep pick2 = new PickStep(robot, cncMilling, cncPickSettings, robotPickSettings3);

		// PUT ON BASIC STACKER
		// Device: Basic Stacker
		DevicePutSettings stackPlatePutSettings = new DevicePutSettings(stackPlate.getWorkAreaById("IRS M Basic"));
		// Robot: Fanuc Robot
		FanucRobotPutSettings robotPutSettings3 = new FanucRobot.FanucRobotPutSettings();
		robotPutSettings3.setGripperHead(robot.getGripperBody().getGripperHead(robotHeadIdAfter));
		//robotPutSettings3.setGripperHead(robot.getGripperBody().getGripperHead("A"));
		robotPutSettings3.setSmoothPoint(new Coordinates(stackPlate.getWorkAreaById("IRS M Basic").getActiveClamping().getSmoothToPoint()));
		robotPutSettings3.setWorkArea(stackPlate.getWorkAreaById("IRS M Basic"));			
		PutStep put2 = new PutStep(robot, stackPlate, stackPlatePutSettings, robotPutSettings3);
		

		// SET RELATIVE TEACHED OFFSETS
		pick1.setRelativeTeachedOffset(relativeTeachedPickStacker);
		putAndWait1.setRelativeTeachedOffset(relativeTeachedPutPrage);
		put1.setRelativeTeachedOffset(relativeTeachedPutMachine);
		pick2.setRelativeTeachedOffset(relativeTeachedPickMachine);
		put2.setRelativeTeachedOffset(relativeTeachedPutStacker);
		
		// create process flow:
		processFlow.addStep(pick1);
		if (hasPrage) {
			processFlow.addStep(putAndWait1);
			processFlow.addStep(processing1);
			processFlow.addStep(pickAfterWait1);
		}
		processFlow.addStep(put1);
		processFlow.addStep(processing2);
		processFlow.addStep(pick2);
		processFlow.addStep(put2);
		
		processFlow.loadAllSettings();
		
		processFlow.setFinishedAmount(finishedAmount);
					
		processFlowTimer = new ProcessFlowTimer(processFlow);
		
		return processFlow;
	}
	
	public ProcessFlowTimer getProcessFlowTimer() {
		return processFlowTimer;
	}
}
