package eu.robojob.irscw.external.device.cnc;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import eu.robojob.irscw.external.communication.CommunicationException;
import eu.robojob.irscw.external.device.DeviceActionException;
import eu.robojob.irscw.external.device.DeviceManager;
import eu.robojob.irscw.external.device.WorkArea;
import eu.robojob.irscw.external.device.cnc.CNCMillingMachine.CNCMillingMachinePickSettings;
import eu.robojob.irscw.external.device.cnc.CNCMillingMachine.CNCMillingMachinePutSettings;
import eu.robojob.irscw.external.device.cnc.CNCMillingMachine.CNCMillingMachineSettings;
import eu.robojob.irscw.external.device.cnc.CNCMillingMachine.CNCMillingMachineStartCylusSettings;
import eu.robojob.irscw.external.robot.RobotManager;

public class CNCMachineCommunicationTest {

	private CNCMillingMachine cncMillingMachine;
	private CNCMillingMachinePutSettings putSettings;
	private CNCMillingMachinePickSettings pickSettings;
	private CNCMillingMachineSettings cncMillingSetting;
	private CNCMillingMachineStartCylusSettings startCyclusSettings;
	private RobotManager robotManager;
	private DeviceManager deviceManager;
	
	private static Logger logger = Logger.getLogger(CNCMachineCommunicationTest.class);
	
	@Before
	public void setup() {
		this.robotManager = new RobotManager();
		this.deviceManager = new DeviceManager(robotManager);
		cncMillingMachine = (CNCMillingMachine) deviceManager.getCNCMachineById("Mazak VRX J500");
		putSettings = new CNCMillingMachine.CNCMillingMachinePutSettings(cncMillingMachine.getWorkAreaById("Mazak VRX Main"));
		pickSettings = new CNCMillingMachinePickSettings(cncMillingMachine.getWorkAreaById("Mazak VRX Main"));
		startCyclusSettings = new CNCMillingMachineStartCylusSettings(cncMillingMachine.getWorkAreaById("Mazak VRX Main"));
		cncMillingSetting = (CNCMillingMachineSettings) cncMillingMachine.getDeviceSettings();
		WorkArea mainWorkArea = cncMillingMachine.getWorkAreaById("Mazak VRX Main");
		cncMillingSetting.setClamping(mainWorkArea, mainWorkArea.getClampingById("Clamping 1"));
		cncMillingMachine.loadDeviceSettings(cncMillingSetting);
	}
	
	@Ignore
	@Test
	public void testPrepareForPut() {
		try {
			cncMillingMachine.prepareForPut(putSettings);
		} catch (CommunicationException | DeviceActionException | InterruptedException e) {
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
		} catch (CommunicationException | DeviceActionException | InterruptedException e) {
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
		} catch (CommunicationException | DeviceActionException | InterruptedException e) {
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
		} catch (CommunicationException | DeviceActionException | InterruptedException e) {
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
		} catch (CommunicationException | InterruptedException e) {
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
		} catch (CommunicationException | DeviceActionException | InterruptedException e) {
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
		} catch (CommunicationException | InterruptedException e) {
			e.printStackTrace();
		} finally {
			cncMillingMachine.disconnect();
		}
	}
	
	@Test
	public void testPowerOff() {
		try {
			cncMillingMachine.powerOff();
		} catch (CommunicationException | InterruptedException e) {
			e.printStackTrace();
		} finally {
			cncMillingMachine.disconnect();
		}
	}
}
