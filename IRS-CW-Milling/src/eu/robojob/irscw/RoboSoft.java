package eu.robojob.irscw;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import org.apache.log4j.Logger;

import eu.robojob.irscw.ui.MainPresenter;
import eu.robojob.irscw.ui.RoboSoftAppFactory;

public class RoboSoft extends Application {

	static Logger logger = Logger.getLogger(RoboSoft.class.getName());

	@Override
	public void start(Stage arg0) throws Exception {
		RoboSoftAppFactory factory = new RoboSoftAppFactory();
		MainPresenter mainPresenter = factory.getMainPresenter();
		mainPresenter.showProcessView();
		Scene scene = new Scene(mainPresenter.getView(), 800, 600);
		
	}
	
	
/*	private static final int ROBOT_PORT = 1235;
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
	}*/
}
