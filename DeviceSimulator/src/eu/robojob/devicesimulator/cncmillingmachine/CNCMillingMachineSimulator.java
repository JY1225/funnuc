package eu.robojob.devicesimulator.cncmillingmachine;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.log4j.Logger;

import eu.robojob.devicesimulator.cncmillingmachine.communication.WaitAndRespondThread;
import eu.robojob.irscw.external.communication.SocketConnection;

public class CNCMillingMachineSimulator {
	
	private static int portNumber = 49153;
	private static Logger logger = Logger.getLogger(CNCMillingMachineSimulator.class);
	
	public static void main(String[] args) {
		logger.debug("started 'CNC Milling Machine Simulator'");
		try {
			ServerSocket serverSocket = new ServerSocket(portNumber);
			logger.debug("created new serversocket on port: " + portNumber);
			Socket socket = serverSocket.accept();
			logger.debug("new socket-connection!");
			SocketConnection socketConnection = new SocketConnection("CNC Milling Machine connection", socket);
			WaitAndRespondThread thread = new WaitAndRespondThread(socketConnection, 2000);
			thread.run();
		} catch (IOException e) {
			logger.error(e);
		}
	}
}
