package eu.robojob.irscw.external.robot;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import eu.robojob.irscw.external.communication.AbstractCommunicationException;
import eu.robojob.irscw.external.device.ClampingManner;
import eu.robojob.irscw.external.device.DeviceManager;
import eu.robojob.irscw.external.device.WorkArea;
import eu.robojob.irscw.external.device.processing.cnc.CNCMillingMachine;
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
	public void setup() throws FileNotFoundException, IOException {
		Properties properties = new Properties();
		properties.load(new FileInputStream(new File("C:\\RoboJob\\settings.properties")));
		this.robotManager = new RobotManager(properties);
		this.deviceManager = new DeviceManager(robotManager, properties);
		this.basicStackPlate = (BasicStackPlate) deviceManager.getDeviceById("IRS M Basic");
		WorkArea wa = basicStackPlate.getWorkAreaById("IRS M Basic");
		
		fanucRobot = (FanucRobot) robotManager.getRobotById("Fanuc M20iA");
		GripperHead head = fanucRobot.getGripperBody().getGripperHead("A");
		GripperHead head2 = fanucRobot.getGripperBody().getGripperHead("B");
		Gripper gripper = fanucRobot.getGripperBody().getGripper("Vacuum grip");
		
		WorkPiece wp = new WorkPiece(Type.RAW, new WorkPieceDimensions(100, 40, 100));
		pickSettings = new FanucRobotPickSettings(wa, head, wa.getActiveClamping().getSmoothFromPoint(), new Coordinates(5, 5, 5, 0, 0, 0),wp);
		
		this.cncMillingMachine = (CNCMillingMachine) deviceManager.getDeviceById("Mazak VRX J500");
		WorkArea wa2 = cncMillingMachine.getWorkAreaById("Mazak VRX Main");
		
		Coordinates point = cncMillingMachine.getPickLocation(wa2, new ClampingManner());
		System.out.println(point);
		point.setR(-45);
		putSettings = new FanucRobotPutSettings(wa2, head, wa2.getActiveClamping().getSmoothToPoint(), point);
		
		pickInMachineSettings = new FanucRobotPickSettings(wa2, head, wa2.getActiveClamping().getSmoothFromPoint(),cncMillingMachine.getPutLocation(wa2, wp.getDimensions(), new ClampingManner()), wp);
		
		pickInMachineW2Settings = new FanucRobotPickSettings(wa2, head2, wa2.getActiveClamping().getSmoothFromPoint(),cncMillingMachine.getPutLocation(wa2, wp.getDimensions(), new ClampingManner()), wp);
	}
	
	@Ignore
	@Test
	public void testPick() {
		try {
			fanucRobot.restartProgram();
			fanucRobot.initiatePick(pickSettings);
			fanucRobot.finalizePick(pickSettings);
		} catch (AbstractCommunicationException | RobotActionException | InterruptedException e) {
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
		} catch (AbstractCommunicationException | RobotActionException e) {
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
		} catch (AbstractCommunicationException | RobotActionException | InterruptedException e) {
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
		} catch (AbstractCommunicationException | RobotActionException | InterruptedException e) {
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
		} catch (AbstractCommunicationException | RobotActionException | InterruptedException e) {
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
		} catch (AbstractCommunicationException | RobotActionException | InterruptedException e) {
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
		} catch (AbstractCommunicationException | RobotActionException | InterruptedException e) {
			e.printStackTrace();
		} finally {
			fanucRobot.disconnect();
		}
	}
}
