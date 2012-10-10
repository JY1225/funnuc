package eu.robojob.simulators.threading;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javafx.application.Platform;

import org.apache.log4j.Logger;

import eu.robojob.irscw.external.communication.SocketConnection;
import eu.robojob.simulators.ui.MessagingPresenter;

public class ListeningThread extends Thread {

	private SocketConnection connection;
	private MessagingPresenter messagingPresenter;
	
	private String type;
	
	private boolean alive;
	
	private int portNumber;
	
	private static final Logger logger = Logger.getLogger(ListeningThread.class);
	
	public ListeningThread(int portNumber, String type, MessagingPresenter messagingPresenter) {
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
					connection = new SocketConnection(SocketConnection.Type.SERVER, "Server", portNumber);
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
						inputString = connection.readString();
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
		try {
			connection.disconnect();
		} catch (IOException e) {
			logger.error(e);
		}	
		this.alive = false;
	}
	
	public void closeConnection() {
		logger.info("about to close connection");
		logMessage("About to close connection\n");
		try {
			connection.disconnect();
			logMessage("Connection closed\n");
		} catch (IOException e) {
			logger.error(e);
		}
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
