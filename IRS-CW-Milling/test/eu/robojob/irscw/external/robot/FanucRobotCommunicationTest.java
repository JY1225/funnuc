package eu.robojob.irscw.external.robot;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import eu.robojob.irscw.external.communication.CommunicationException;
import eu.robojob.irscw.external.device.DeviceManager;
import eu.robojob.irscw.external.device.WorkArea;
import eu.robojob.irscw.external.device.cnc.CNCMillingMachine;
import eu.robojob.irscw.external.device.stacking.BasicStackPlate;
import eu.robojob.irscw.external.robot.FanucRobot.FanucRobotPickSettings;
import eu.robojob.irscw.external.robot.FanucRobot.FanucRobotPutSettings;
import eu.robojob.irscw.positioning.Coordinates;
import eu.robojob.irscw.workpiece.WorkPiece;
import eu.robojob.irscw.workpiece.WorkPiece.Type;
import eu.robojob.irscw.workpiece.WorkPieceDimensions;

public class FanucRobotCommunicationTest {

	private FanucRobot fanucRobot;
	private FanucRobotPickSettings pickSettings;
	private FanucRobotPutSettings putSettings;
	private FanucRobotPickSettings pickInMachineSettings;
	private FanucRobotPickSettings pickInMachineW2Settings;
	
	private BasicStackPlate basicStackPlate;
	private CNCMillingMachine cncMillingMachine;
	private RobotManager robotManager;
	private DeviceManager deviceManager;
	
	@Before
	public void setup() {
		this.robotManager = new RobotManager();
		this.deviceManager = new DeviceManager(robotManager);
		this.basicStackPlate = (BasicStackPlate) deviceManager.getDeviceById("IRS M Basic");
		WorkArea wa = basicStackPlate.getWorkAreaById("IRS M Basic");
		
		fanucRobot = (FanucRobot) robotManager.getRobotById("Fanuc M20iA");
		GripperHead head = fanucRobot.getGripperBody().getGripperHead("A");
		GripperHead head2 = fanucRobot.getGripperBody().getGripperHead("B");
		Gripper gripper = fanucRobot.getGripperBody().getGripper("Vacuum grip");
		
		WorkPiece wp = new WorkPiece(Type.RAW, new WorkPieceDimensions(100, 40, 100));
		pickSettings = new FanucRobotPickSettings(wa, head, gripper, wa.getActiveClamping().getSmoothFromPoint(), new Coordinates(5, 5, 5, 0, 0, 0), wa.getActiveClamping(),wp);
		
		this.cncMillingMachine = (CNCMillingMachine) deviceManager.getDeviceById("Mazak VRX J500");
		WorkArea wa2 = cncMillingMachine.getWorkAreaById("Mazak VRX Main");
		
		Coordinates point = cncMillingMachine.getPickLocation(wa2);
		System.out.println(point);
		point.setR(-45);
		putSettings = new FanucRobotPutSettings(wa2, head, gripper, wa2.getActiveClamping().getSmoothToPoint(), point, wa2.getActiveClamping());
		
		pickInMachineSettings = new FanucRobotPickSettings(wa2, head, gripper, wa2.getActiveClamping().getSmoothFromPoint(),cncMillingMachine.getPutLocation(wa2, wp.getDimensions()), wa2.getActiveClamping(), wp);
		
		pickInMachineW2Settings = new FanucRobotPickSettings(wa2, head2, gripper, wa2.getActiveClamping().getSmoothFromPoint(),cncMillingMachine.getPutLocation(wa2, wp.getDimensions()), wa2.getActiveClamping(), wp);
	}
	
	@Ignore
	@Test
	public void testPick() {
		try {
			fanucRobot.restartProgram();
			fanucRobot.initiatePick(pickSettings);
			fanucRobot.finalizePick(pickSettings);
		} catch (CommunicationException | RobotActionException | InterruptedException e) {
			e.printStackTrace();
		} finally {
			fanucRobot.disconnect();
		}
	}
	
	@Ignore
	@Test
	public void testToHome() {
		try {
			fanucRobot.restartProgram();
			fanucRobot.moveToHome();
		} catch (CommunicationException | RobotActionException e) {
			e.printStackTrace();
		} finally {
			fanucRobot.disconnect();
		}
	}
	
	@Test
	public void testPut() {
		try {
			fanucRobot.restartProgram();
			fanucRobot.initiatePick(pickSettings);
			fanucRobot.finalizePick(pickSettings);
			fanucRobot.initiatePut(putSettings);
			fanucRobot.finalizePut(putSettings);
		} catch (CommunicationException | RobotActionException | InterruptedException e) {
			e.printStackTrace();
		} finally {
			fanucRobot.disconnect();
		}
	}
	
	@Ignore
	@Test
	public void testTeachPick() {
		try {
			fanucRobot.restartProgram();
			fanucRobot.initiateTeachedPick(pickSettings);
			fanucRobot.finalizeTeachedPick(pickSettings);
		} catch (CommunicationException | RobotActionException | InterruptedException e) {
			e.printStackTrace();
		} finally {
			fanucRobot.disconnect();
		}
	}
	
	@Ignore
	@Test
	public void testTeachPut() {
		try {
			fanucRobot.restartProgram();
			fanucRobot.initiateTeachedPick(pickSettings);
			fanucRobot.finalizeTeachedPick(pickSettings);
			fanucRobot.initiateTeachedPut(putSettings);
			fanucRobot.finalizeTeachedPut(putSettings);
		} catch (CommunicationException | RobotActionException | InterruptedException e) {
			e.printStackTrace();
		} finally {
			fanucRobot.disconnect();
		}
	}
	
	@Ignore
	@Test
	public void testTeachPickInMachine() {
		try {
			fanucRobot.restartProgram();
			fanucRobot.initiateTeachedPick(pickInMachineSettings);
			fanucRobot.finalizeTeachedPick(pickInMachineSettings);
		} catch (CommunicationException | RobotActionException | InterruptedException e) {
			e.printStackTrace();
		} finally {
			fanucRobot.disconnect();
		}
	}
	
	@Test
	@Ignore
	public void testTeachPickInMachineWithSecondHead() {
		try {
			fanucRobot.restartProgram();
			fanucRobot.initiateTeachedPick(pickInMachineW2Settings);
			fanucRobot.finalizeTeachedPick(pickInMachineW2Settings);
		} catch (CommunicationException | RobotActionException | InterruptedException e) {
			e.printStackTrace();
		} finally {
			fanucRobot.disconnect();
		}
	}
}
