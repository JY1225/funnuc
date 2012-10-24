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
	
	private BasicStackPlate basicStackPlate;
	private CNCMillingMachine cncMillingMachine;
	private RobotManager robotManager;
	private DeviceManager deviceManager;
	
	@Before
	public void setup() {
		this.robotManager = new RobotManager();
		this.deviceManager = new DeviceManager();
		this.basicStackPlate = (BasicStackPlate) deviceManager.getDeviceById("IRS M Basic");
		WorkArea wa = basicStackPlate.getWorkAreaById("IRS M Basic");
		
		fanucRobot = (FanucRobot) robotManager.getRobotById("Fanuc M20iA");
		GripperHead head = fanucRobot.getGripperBody().getGripperHead("A");
		Gripper gripper = fanucRobot.getGripperBody().getGripper("Vacuum grip");
		
		WorkPiece wp = new WorkPiece(Type.RAW, new WorkPieceDimensions(100, 40, 100));
		pickSettings = new FanucRobotPickSettings(wa, head, gripper, new Coordinates(10, 10, 10, 0, 0, 0), new Coordinates(5, 5, 5, 0, 0, 0), wp);
		
		this.cncMillingMachine = (CNCMillingMachine) deviceManager.getDeviceById("Mazak VRX J500");
		WorkArea wa2 = cncMillingMachine.getWorkAreaById("Mazak VRX Main");
		
		putSettings = new FanucRobotPutSettings(wa2, head, gripper, new Coordinates(20, 20, 20, 0, 0, 0), new Coordinates(5, 5, 5, 0, 0, 0));
	}
	
	@Ignore
	@Test
	public void testPick() {
		try {
			fanucRobot.restartProgram();
			fanucRobot.initiatePick(pickSettings);
			fanucRobot.finalizePick(pickSettings);
		} catch (CommunicationException | RobotActionException e) {
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
	
	@Ignore
	@Test
	public void testPut() {
		try {
			fanucRobot.restartProgram();
			fanucRobot.initiatePick(pickSettings);
			fanucRobot.finalizePick(pickSettings);
			fanucRobot.initiatePut(putSettings);
			fanucRobot.finalizePut(putSettings);
		} catch (CommunicationException | RobotActionException e) {
			e.printStackTrace();
		} finally {
			fanucRobot.disconnect();
		}
	}
	
	@Test
	public void testTeachPick() {
		try {
			fanucRobot.restartProgram();
			fanucRobot.initiateTeachedPick(pickSettings);
			fanucRobot.finalizeTeachedPick(pickSettings);
		} catch (CommunicationException | RobotActionException e) {
			e.printStackTrace();
		} finally {
			fanucRobot.disconnect();
		}
	}
}
