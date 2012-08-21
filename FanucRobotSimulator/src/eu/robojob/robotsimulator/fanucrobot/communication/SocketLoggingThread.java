package eu.robojob.robotsimulator.fanucrobot.communication;

import java.io.IOException;

import org.apache.log4j.Logger;

import eu.robojob.irscw.external.communication.SocketConnection;

public class SocketLoggingThread implements Runnable {

	private SocketConnection socketConnection;
	private static Logger logger = Logger.getLogger(SocketLoggingThread.class.getName());
	
	public SocketLoggingThread(SocketConnection socketConnection) {
		this.socketConnection = socketConnection;
	}
	
	@Override
	public void run() {
		while (socketConnection.isConnected()) {
			String inputString;
			try {
				inputString = socketConnection.readString();
				logger.info("received message: " + inputString);
			} catch (IOException e) {
				logger.error("Socket disconnected");
			}
		}
		logger.info("socketConnection: " + socketConnection + " is no longer connected");
	}

}
