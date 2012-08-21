package eu.robojob.robotsimulator.fanucrobot;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.log4j.Logger;

import eu.robojob.devicesimulator.cncmillingmachine.communication.WaitAndRespondThread;
import eu.robojob.irscw.external.communication.SocketConnection;

public class FanucRobotSimulator {
	
	private static int portNumber = 1234;
	private static Logger logger = Logger.getLogger(FanucRobotSimulator.class.getName());

	public static void main(String[] args) {
		int counter = 0;
		logger.debug("started server-application");
		try {
			ServerSocket serverSocket = new ServerSocket(portNumber);
			logger.debug("created new serversocket on port: " + portNumber);
			while(true) {
				Socket socket = serverSocket.accept();
				logger.debug("new socket-connection!");
				counter++;
				SocketConnection socketConnection = new SocketConnection("connection " + counter + ": ", socket);
				WaitAndRespondThread thread = new WaitAndRespondThread(socketConnection, 2000);
				thread.run();
			}
		} catch (IOException e) {
			logger.error(e);
		}
	}

}
