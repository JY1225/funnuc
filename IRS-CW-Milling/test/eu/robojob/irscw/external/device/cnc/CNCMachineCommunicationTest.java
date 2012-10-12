package eu.robojob.irscw.external.device.cnc;

import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import eu.robojob.irscw.external.communication.CommunicationException;
import eu.robojob.irscw.external.communication.DisconnectedException;
import eu.robojob.irscw.external.communication.SocketConnection;
import eu.robojob.irscw.external.communication.SocketConnection.Type;

public class CNCMachineCommunicationTest {

	private CNCMachineCommunication cncMachineCommunication;
	
	private static Logger logger = Logger.getLogger(CNCMachineCommunicationTest.class);
	
	@Before
	public void setup() {
		SocketConnection socketConnection = new SocketConnection(Type.CLIENT, "CNC machine connection", "10.10.40.12", 2010);
		cncMachineCommunication = new CNCMachineCommunication(socketConnection);
	}
	
	@Test
	public void testWriteRead() {
		int waitTimes = 0;
		while (waitTimes < 5) {
			if (cncMachineCommunication.isConnected()) {
				break;
			} else {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		if (cncMachineCommunication.isConnected()) {
			logger.info("Connected");
			int[] array = {2, 4};
			try {
				cncMachineCommunication.writeRegisters(11, array);
			} catch (DisconnectedException e) {
				e.printStackTrace();
			} catch (CommunicationException e) {
				e.printStackTrace();
			}
			try {
				List<Integer> registerValues = cncMachineCommunication.readRegisters(1, 4);
				for (int value : registerValues) {
					logger.info("Register value: " + value);
				}
			} catch (DisconnectedException e) {
				e.printStackTrace();
			} catch (CommunicationException e) {
				e.printStackTrace();
			}
			cncMachineCommunication.disconnect();
		}
	}
}
