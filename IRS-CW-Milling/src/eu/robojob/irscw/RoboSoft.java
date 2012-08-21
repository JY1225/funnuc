package eu.robojob.irscw;

import java.io.IOException;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;

import eu.robojob.irscw.external.communication.SocketConnection;
import eu.robojob.irscw.external.device.CNCMillingMachine;
import eu.robojob.irscw.external.device.CNCMillingMachine.CNCMillingMachinePickSettings;
import eu.robojob.irscw.external.device.CNCMillingMachine.CNCMillingMachinePutSettings;
import eu.robojob.irscw.external.device.CNCMillingMachine.CNCMillingMachineStartCylusSettings;
import eu.robojob.irscw.external.device.WorkArea;
import eu.robojob.irscw.external.device.Zone;
import eu.robojob.irscw.external.robot.FanucRobot;
import eu.robojob.irscw.external.robot.FanucRobot.FanucRobotPickSettings;
import eu.robojob.irscw.external.robot.FanucRobot.FanucRobotPutSettings;
import eu.robojob.irscw.external.robot.Gripper;
import eu.robojob.irscw.external.robot.GripperBody;
import eu.robojob.irscw.external.robot.GripperHead;
import eu.robojob.irscw.positioning.UserFrame;
import eu.robojob.irscw.process.FixedAmountJob;
import eu.robojob.irscw.process.PickStep;
import eu.robojob.irscw.process.ProcessFlow;
import eu.robojob.irscw.process.ProcessingStep;
import eu.robojob.irscw.process.PutStep;

public class RoboSoft {

	static Logger logger = Logger.getLogger(RoboSoft.class.getName());
	
	private static final int ROBOT_PORT = 1235;
	private static final int CNC_MACHINE_PORT = 1234;
	
	public static void main(String[] args) throws UnknownHostException, IOException {
		SocketConnection robotSocketConnection = new SocketConnection("Robot connection", "127.0.0.1", ROBOT_PORT);
		robotSocketConnection.connect();
		FanucRobot fanucRobot = new FanucRobot("Fanuc robot", robotSocketConnection);
		Gripper gripperA = new Gripper("Gripper A", 20, "Test gripper A");
		Gripper gripperB = new Gripper("Gripper B", 20, "Test gripper B");
		GripperBody gripperBody = new GripperBody(1, "Default gripper body");
		GripperHead gripperHead = new GripperHead(2, gripperA);
		gripperBody.addGripperHead(gripperHead);
		SocketConnection machineSocketConnection = new SocketConnection("Machine connection", "127.0.0.1", CNC_MACHINE_PORT);
		machineSocketConnection.connect();
		CNCMillingMachine cncMillingMachine = new CNCMillingMachine("CNC Milling Machine", machineSocketConnection);
		Zone zone1 = new Zone("zone 1", cncMillingMachine);
		UserFrame userFrame2 = new UserFrame(2, 20);
		UserFrame userFrame3 = new UserFrame(3, 15);
		WorkArea workArea1 = new WorkArea("WorkArea 1-A", zone1, userFrame2);
		WorkArea workArea2 = new WorkArea("WorkArea 1-B", zone1, userFrame3);
		
		ProcessFlow processFlow = new ProcessFlow();
		
		CNCMillingMachinePickSettings pickSettings = new CNCMillingMachinePickSettings(workArea1);
		FanucRobotPickSettings robotPickSettings = new FanucRobotPickSettings(workArea1, gripperA, gripperBody, gripperHead);
		PickStep pickStep = new PickStep(fanucRobot, gripperA, cncMillingMachine, pickSettings, robotPickSettings);
		
		CNCMillingMachinePutSettings putSettings = new CNCMillingMachinePutSettings(workArea2);
		FanucRobotPutSettings robotPutSettings = new FanucRobotPutSettings(workArea2, gripperA, gripperBody, gripperHead);
		PutStep putStep = new PutStep(fanucRobot, gripperA, cncMillingMachine, putSettings, robotPutSettings);
		
		CNCMillingMachineStartCylusSettings startCylusSettings = new CNCMillingMachineStartCylusSettings(workArea2);
		ProcessingStep processingStep = new ProcessingStep(cncMillingMachine, startCylusSettings);
		
		processFlow.addStep(pickStep);
		processFlow.addStep(putStep);
		processFlow.addStep(processingStep);
		
		FixedAmountJob job = new FixedAmountJob(processFlow, 20);
		job.startExecution();
	}
}
