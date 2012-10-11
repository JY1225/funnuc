package eu.robojob.simulators.threading;

import java.io.IOException;

import javafx.application.Platform;

import org.apache.log4j.Logger;

import eu.robojob.irscw.external.communication.SocketConnection;
import eu.robojob.simulators.ui.MessagingPresenter;

public class ListeningThread extends Thread {

	private SocketConnection connection;
	private MessagingPresenter messagingPresenter;
	
	private String type;
	
	private boolean alive;
	
	private String ipAddress;
	private int portNumber;
	
	private static final Logger logger = Logger.getLogger(ListeningThread.class);
	
	public ListeningThread(String ipAddress, int portNumber, String type, MessagingPresenter messagingPresenter) {
		this.ipAddress = ipAddress;
		this.portNumber = portNumber;
		this.connection = null;
		this.type = type;
		this.messagingPresenter = messagingPresenter;
		alive = true;
	}
	
	@Override
	public void run() {
		while(alive) {
			if (connection == null) {
				if (type.equals("Server")) {
					connection = new SocketConnection(SocketConnection.Type.SERVER, "Server", ipAddress, portNumber);
				} else if (type.equals("Client")) {
					connection = new SocketConnection(SocketConnection.Type.CLIENT, "Client", portNumber);
				} else {
					throw new IllegalStateException("Unknown connection type");
				}
				try {
					connection.connect();
					logMessage("Verbonden!\n");
					setConnected(true);
				} catch (IOException e) {
					logException(e);
					logger.error(e);
					alive = false;
				}
			} else {
				String inputString;
				try {
					if (connection.isConnected()) {
						logger.info("about to read");
						inputString = connection.readString();
						logger.info("read string: " + inputString);
						logRead(inputString);
					} else {
						logMessage("DISCONNECTED\n");
						setConnected(false);
						alive = false;
					}
				} catch (Exception e) {
					logException(e);
					logger.error(e);
					// alive = false;
				}
			}
		}
	}
	
	@Override
	public void interrupt() {
		logger.info("intterupting thread");
		super.interrupt();
		connection.disconnect();
		this.alive = false;
	}
	
	public void closeConnection() {
		logger.info("about to close connection");
		logMessage("About to close connection\n");
		connection.disconnect();
		logMessage("Connection closed\n");
	}
	
	private void logRead(String inputString) {
		logMessage("IN: \t" + inputString + "\n");
	}
	
	private void logException(Exception e) {
		logMessage("ERROR: \t" + e.getMessage() + "\n");
		logger.error(e);
	}
	
	private void logMessage(final String message) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				messagingPresenter.addToLog(message);
			}
		});
	}
	
	public SocketConnection getConnection() {
		return connection;
	}

	private void setConnected(final boolean connected) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				messagingPresenter.setConnected(connected);
			}
		});
	}
}
