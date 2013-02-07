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
import eu.robojob.irscw.external.device.processing.cnc.milling.CNCMillingMachine;
import eu.robojob.irscw.external.device.processing.prage.PrageDevice;
import eu.robojob.irscw.external.device.stacking.BasicStackPlate;
import eu.robojob.irscw.external.device.stacking.BasicStackPlate.WorkPieceOrientation;
import eu.robojob.irscw.external.device.stacking.BasicStackPlateSettings;
import eu.robojob.irscw.external.robot.RobotManager;
import eu.robojob.irscw.external.robot.RobotSettings;
import eu.robojob.irscw.external.robot.fanuc.FanucRobot;
import eu.robojob.irscw.external.robot.fanuc.FanucRobotPickSettings;
import eu.robojob.irscw.external.robot.fanuc.FanucRobotPutSettings;
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
		
	public PropertiesProcessFlowFactory(final DeviceManager deviceManager, final RobotManager robotManager) {
		this.deviceManager = deviceManager;
		this.robotManager = robotManager;
	}
	
	public ProcessFlow loadProcessFlow(final Properties properties) {
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
	
	private ProcessFlow createProcessFlow(final String processFlowId, final int amount, final int finishedAmount, final WorkPieceDimensions rawDimensions, final WorkPieceDimensions finishedDimensions, final boolean tilted, final boolean hasPrage, 
			final String clampingId, final boolean clampLength, final String robotHeadIdBefore,
			final String robotHeadIdAfter, final String gripperHeadAId, final String gripperHeadBId, final Coordinates relativeTeachedPickStacker, final Coordinates relativeTeachedPutPrage, 
				final Coordinates relativeTeachedPutMachine, final Coordinates relativeTeachedPickMachine, final Coordinates relativeTeachedPutStacker) {
		ProcessFlow processFlow = new ProcessFlow(processFlowId);
		//processFlow.setTotalAmount(amount);
		
		//----------ROBOT----------
		
		// Fanuc M20iA
		FanucRobot robot = (FanucRobot) robotManager.getRobotByName("Fanuc M20iA");
		RobotSettings robotSettings = robot.getRobotSettings();
		robotSettings.setGripper(robot.getGripperBody().getGripperHeadByName("A"), null);
		robotSettings.setGripper(robot.getGripperBody().getGripperHeadByName("B"), null);
		processFlow.setRobotSettings(robot, robotSettings);
		
		//----------DEVICES----------
		
		// Basic Stack Plate
		BasicStackPlate stackPlate = (BasicStackPlate) deviceManager.getStackingFromDeviceByName("IRS M Basic");
		BasicStackPlateSettings stackPlateSettings = (BasicStackPlateSettings) stackPlate.getDeviceSettings();
		stackPlateSettings.setAmount(amount);
		stackPlateSettings.setRawWorkPieceDimensions(rawDimensions);
		if (tilted) {
			stackPlateSettings.setOrientation(WorkPieceOrientation.TILTED);
		} else {
			stackPlateSettings.setOrientation(WorkPieceOrientation.HORIZONTAL);
		}
		processFlow.setDeviceSettings(stackPlate, stackPlateSettings);
		
		// Pr�ge Device
		PrageDevice prageDevice = (PrageDevice) deviceManager.getPreProcessingDeviceByName("Pr�ge");
		DeviceSettings prageDeviceSettings = (DeviceSettings) prageDevice.getDeviceSettings();
		processFlow.setDeviceSettings(prageDevice, prageDeviceSettings);
		
		// CNC Milling Machine
		CNCMillingMachine cncMilling = (CNCMillingMachine) deviceManager.getCNCMachineByName("Mazak VRX J500");
		DeviceSettings cncMillingSetting = cncMilling.getDeviceSettings();
		WorkArea mainWorkArea = cncMilling.getWorkAreaByName("Mazak VRX Main");
		cncMillingSetting.setClamping(mainWorkArea, mainWorkArea.getClampingByName(clampingId));
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
		DevicePickSettings stackPlatePickSettings = new DevicePickSettings(stackPlate, stackPlate.getWorkAreaByName("IRS M Basic"));
		// Robot: Fanuc Robot
		FanucRobotPickSettings robotPickSettings1 = new FanucRobotPickSettings();
		robotPickSettings1.setGripperHead(robot.getGripperBody().getGripperHeadByName(robotHeadIdBefore));
		robotPickSettings1.setSmoothPoint(new Coordinates(stackPlate.getWorkAreaByName("IRS M Basic").getActiveClamping().getSmoothFromPoint()));
		robotPickSettings1.setWorkArea(stackPlate.getWorkAreaByName("IRS M Basic"));
		WorkPiece rawWorkPiece = new WorkPiece(eu.robojob.irscw.workpiece.WorkPiece.Type.RAW, rawDimensions);
		robotPickSettings1.setWorkPiece(rawWorkPiece);		
		// Pick step
		PickStep pick1 = new PickStep(stackPlatePickSettings, robotPickSettings1);
		
		// PUT AND WAIT ON PR�GE DEVICE
		// Device: Pr�ge device
		DevicePickSettings pragePickSettings = new DevicePickSettings(prageDevice, prageDevice.getWorkAreaByName("Pr�ge"));
		ProcessingDeviceStartCyclusSettings prageStartCyclusSettings = new ProcessingDeviceStartCyclusSettings(prageDevice, prageDevice.getWorkAreaByName("Pr�ge"));
		DevicePutSettings pragePutSettings = new DevicePutSettings(prageDevice, prageDevice.getWorkAreaByName("Pr�ge"));
		// Robot: Fanuc Robot
		// put and wait
		FanucRobotPutSettings robotPutSettings1 = new FanucRobotPutSettings();
		robotPutSettings1.setGripperHead(robot.getGripperBody().getGripperHeadByName(robotHeadIdBefore));
		robotPutSettings1.setSmoothPoint(new Coordinates(prageDevice.getWorkAreaByName("Pr�ge").getClampingByName("Clamping 5").getSmoothToPoint()));
		robotPutSettings1.setWorkArea(prageDevice.getWorkAreaByName("Pr�ge"));
		// pick after wait
		FanucRobotPickSettings robotPickSettings2 = new FanucRobotPickSettings();
		robotPickSettings2.setGripperHead(robot.getGripperBody().getGripperHeadByName(robotHeadIdBefore));
		robotPickSettings2.setSmoothPoint(new Coordinates(prageDevice.getWorkAreaByName("Pr�ge").getClampingByName("Clamping 5").getSmoothFromPoint()));
		robotPickSettings2.setWorkArea(prageDevice.getWorkAreaByName("Pr�ge"));
		robotPickSettings2.setWorkPiece(rawWorkPiece);
		// Put and wait step
		PutAndWaitStep putAndWait1 = new PutAndWaitStep(pragePutSettings, robotPutSettings1);
		PickAfterWaitStep pickAfterWait1 = new PickAfterWaitStep(pragePickSettings, robotPickSettings2);
		ProcessingStep processing1 = new ProcessingStep(prageStartCyclusSettings);
		
		// PUT IN CNC VRX 
		// Device: CNCMilling Machine
		DevicePutSettings cncPutSettings = new DevicePutSettings(cncMilling, cncMilling.getWorkAreaByName("Mazak VRX Main"));
		// Robot: Fanuc Robot
		FanucRobotPutSettings robotPutSettings2 = new FanucRobotPutSettings();
		robotPutSettings2.setGripperHead(robot.getGripperBody().getGripperHeadByName(robotHeadIdBefore));
		robotPutSettings2.setSmoothPoint(new Coordinates(cncMilling.getWorkAreaByName("Mazak VRX Main").getClampingByName("Clamping 1").getSmoothToPoint()));
		robotPutSettings2.setWorkArea(cncMilling.getWorkAreaByName("Mazak VRX Main"));
		robotPutSettings2.setDoMachineAirblow(true);
		// Put step
		PutStep put1 = new PutStep(cncPutSettings, robotPutSettings2);

		// PROCESSING (CNC VRX)
		ProcessingDeviceStartCyclusSettings cncStartCyclusSettings =  new ProcessingDeviceStartCyclusSettings(cncMilling, cncMilling.getWorkAreaByName("Mazak VRX Main"));
		ProcessingStep processing2 = new ProcessingStep(cncStartCyclusSettings);
		
		// PICK FROM CNC VRX
		// Device: CNCMilling Machine
		DevicePickSettings cncPickSettings = new DevicePickSettings(cncMilling, cncMilling.getWorkAreaByName("Mazak VRX Main"));
		// Robot: Fanuc Robot
		FanucRobotPickSettings robotPickSettings3 = new FanucRobotPickSettings();
		robotPickSettings3.setGripperHead(robot.getGripperBody().getGripperHeadByName(robotHeadIdAfter));
		//robotPickSettings3.setGripperHead(robot.getGripperBody().getGripperHead("A"));
		robotPickSettings3.setSmoothPoint(new Coordinates(cncMilling.getWorkAreaByName("Mazak VRX Main").getClampingByName("Clamping 1").getSmoothFromPoint()));
		robotPickSettings3.setWorkArea(mainWorkArea);
		robotPickSettings3.setDoMachineAirblow(true);
		WorkPiece finishedWorkPiece = new WorkPiece(eu.robojob.irscw.workpiece.WorkPiece.Type.FINISHED, finishedDimensions);
		robotPickSettings3.setWorkPiece(finishedWorkPiece);
		// Pick step
		PickStep pick2 = new PickStep(cncPickSettings, robotPickSettings3);

		// PUT ON BASIC STACKER
		// Device: Basic Stacker
		DevicePutSettings stackPlatePutSettings = new DevicePutSettings(stackPlate, stackPlate.getWorkAreaByName("IRS M Basic"));
		// Robot: Fanuc Robot
		FanucRobotPutSettings robotPutSettings3 = new FanucRobotPutSettings();
		robotPutSettings3.setGripperHead(robot.getGripperBody().getGripperHeadByName(robotHeadIdAfter));
		//robotPutSettings3.setGripperHead(robot.getGripperBody().getGripperHead("A"));
		robotPutSettings3.setSmoothPoint(new Coordinates(stackPlate.getWorkAreaByName("IRS M Basic").getActiveClamping().getSmoothToPoint()));
		robotPutSettings3.setWorkArea(stackPlate.getWorkAreaByName("IRS M Basic"));			
		PutStep put2 = new PutStep(stackPlatePutSettings, robotPutSettings3);
		

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
