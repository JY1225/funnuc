package eu.robojob.irscw.external.device.cnc;

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

public class CNCMachineCommunicationTest {

	private CNCMillingMachine cncMillingMachine;
	private CNCMillingMachinePutSettings putSettings;
	private CNCMillingMachinePickSettings pickSettings;
	private CNCMillingMachineSettings cncMillingSetting;
	private DeviceManager deviceManager;
	
	//private static Logger logger = Logger.getLogger(CNCMachineCommunicationTest.class);
	
	@Before
	public void setup() {
		this.deviceManager = new DeviceManager();
		cncMillingMachine = (CNCMillingMachine) deviceManager.getCNCMachineById("Mazak VRX J500");
		putSettings = new CNCMillingMachine.CNCMillingMachinePutSettings(cncMillingMachine.getWorkAreaById("Mazak VRX Main"));
		pickSettings = new CNCMillingMachinePickSettings(cncMillingMachine.getWorkAreaById("Mazak VRX Main"));
		cncMillingSetting = (CNCMillingMachineSettings) cncMillingMachine.getDeviceSettings();
		WorkArea mainWorkArea = cncMillingMachine.getWorkAreaById("Mazak VRX Main");
		cncMillingSetting.setClamping(mainWorkArea, mainWorkArea.getClampingById("Clamping 1"));
		cncMillingMachine.loadDeviceSettings(cncMillingSetting);
	}
	
	
	@Test
	public void testPrepareForPut() {
		try {
			cncMillingMachine.prepareForPut(putSettings);
		} catch (CommunicationException | DeviceActionException e) {
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
		} catch (CommunicationException | DeviceActionException e) {
			e.printStackTrace();
		} finally {
			cncMillingMachine.disconnect();
		}
	}
	
}
