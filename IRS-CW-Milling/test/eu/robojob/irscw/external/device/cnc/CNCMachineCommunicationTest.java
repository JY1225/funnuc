package eu.robojob.irscw.external.device.cnc;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import eu.robojob.irscw.external.communication.AbstractCommunicationException;
import eu.robojob.irscw.external.device.DeviceActionException;
import eu.robojob.irscw.external.device.DeviceManager;
import eu.robojob.irscw.external.device.DevicePickSettings;
import eu.robojob.irscw.external.device.DevicePutSettings;
import eu.robojob.irscw.external.device.DeviceSettings;
import eu.robojob.irscw.external.device.WorkArea;
import eu.robojob.irscw.external.device.processing.ProcessingDeviceStartCyclusSettings;
import eu.robojob.irscw.external.device.processing.cnc.milling.CNCMillingMachine;
import eu.robojob.irscw.external.robot.RobotManager;

public class CNCMachineCommunicationTest {

	private CNCMillingMachine cncMillingMachine;
	private DevicePutSettings putSettings;
	private DevicePickSettings pickSettings;
	private DeviceSettings cncMillingSetting;
	private ProcessingDeviceStartCyclusSettings startCyclusSettings;
	private RobotManager robotManager;
	private DeviceManager deviceManager;
	
	private static Logger logger = LogManager.getLogger(CNCMachineCommunicationTest.class.getName());
	
	@Before
	public void setup() throws IOException {
		Properties properties = new Properties();
		properties.load(new FileInputStream(new File("C:\\RoboJob\\settings.properties")));
		this.robotManager = new RobotManager(properties);
		this.deviceManager = new DeviceManager(robotManager, properties);
		cncMillingMachine = (CNCMillingMachine) deviceManager.getCNCMachineById("Mazak VRX J500");
		putSettings = new DevicePutSettings(cncMillingMachine.getWorkAreaById("Mazak VRX Main"));
		pickSettings = new DevicePickSettings(cncMillingMachine.getWorkAreaById("Mazak VRX Main"));
		startCyclusSettings = new ProcessingDeviceStartCyclusSettings(cncMillingMachine.getWorkAreaById("Mazak VRX Main"));
		cncMillingSetting = cncMillingMachine.getDeviceSettings();
		WorkArea mainWorkArea = cncMillingMachine.getWorkAreaById("Mazak VRX Main");
		cncMillingSetting.setClamping(mainWorkArea, mainWorkArea.getClampingById("Clamping 1"));
		cncMillingMachine.loadDeviceSettings(cncMillingSetting);
	}
	
	@Ignore
	@Test
	public void testPrepareForPut() {
		try {
			cncMillingMachine.prepareForPut(putSettings);
		} catch (AbstractCommunicationException | DeviceActionException | InterruptedException e) {
			e.printStackTrace();
		} finally {
			cncMillingMachine.disconnect();
		}
	}
	
	@Ignore
	@Test
	public void testPrepareForPick() {
		try {
			cncMillingMachine.prepareForPick(pickSettings);
		} catch (AbstractCommunicationException | DeviceActionException | InterruptedException e) {
			e.printStackTrace();
		} finally {
			cncMillingMachine.disconnect();
		}
	}
	
	@Ignore
	@Test
	public void testClamp() {
		try {
			cncMillingMachine.grabPiece(putSettings);
		} catch (AbstractCommunicationException | DeviceActionException | InterruptedException e) {
			e.printStackTrace();
		} finally {
			cncMillingMachine.disconnect();
		}
	}
	
	@Ignore
	@Test
	public void testUnClamp() {
		try {
			cncMillingMachine.releasePiece(pickSettings);
		} catch (AbstractCommunicationException | DeviceActionException | InterruptedException e) {
			e.printStackTrace();
		} finally {
			cncMillingMachine.disconnect();
		}
	}
	
	@Ignore
	@Test
	public void testCanPut() {
		try {
			logger.info("Can I ask you to prepare for put? : " + cncMillingMachine.canPut(putSettings));
		} catch (DeviceActionException | InterruptedException e) {
			e.printStackTrace();
		} finally {
			cncMillingMachine.disconnect();
		}
	}
	
	@Ignore
	@Test
	public void testStartCycle() {
		try {
			cncMillingMachine.startCyclus(startCyclusSettings);
		} catch (AbstractCommunicationException | DeviceActionException | InterruptedException e) {
			e.printStackTrace();
		} finally {
			cncMillingMachine.disconnect();
		}
	}
	
	@Ignore
	@Test
	public void testNCReset() {
		try {
			cncMillingMachine.nCReset();
		} catch (AbstractCommunicationException | InterruptedException e) {
			e.printStackTrace();
		} finally {
			cncMillingMachine.disconnect();
		}
	}
	
	@Test
	public void testPowerOff() {
		try {
			cncMillingMachine.powerOff();
		} catch (AbstractCommunicationException | InterruptedException e) {
			e.printStackTrace();
		} finally {
			cncMillingMachine.disconnect();
		}
	}
}
