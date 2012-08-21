package eu.robojob.devicesimulator.cncmillingmachine.communication;

import java.io.IOException;

import org.apache.log4j.Logger;

import eu.robojob.irscw.external.communication.SocketConnection;

public class WaitAndRespondThread implements Runnable {
	
	private SocketConnection socketConnection;
	private static Logger logger = Logger.getLogger(WaitAndRespondThread.class.getName());

	@Override
	public void run() {
		while (socketConnection.isConnected()) {
			String inputString;
			try {
				inputString = socketConnection.readString();
				logger.info("received message: " + inputString);
				try {
					Thread.sleep(2000);
					socketConnection.sendString("OK");
				} catch (InterruptedException e) {
					logger.error(e);
				}
			} catch (IOException e) {
				logger.error("Socket disconnected");
			}
		}
		logger.info("socketConnection: " + socketConnection + " is no longer connected");
	}

}
