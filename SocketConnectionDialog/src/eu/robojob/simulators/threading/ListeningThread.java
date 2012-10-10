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
				try {
					Socket socket = null;
					if (type.equals("Server")) {
						ServerSocket serverSocket = new ServerSocket(portNumber);
						logMessage("Wachten op client connectie...\n");
						socket = serverSocket.accept();
					} else if (type.equals("Client")) {
						socket = new Socket("127.0.0.1", portNumber);
					} else {
						throw new IllegalStateException("Unknown type");
					}
					connection = new SocketConnection("Socket connection dialog", socket);
					logMessage("Verbonden!\n");
					setConnected(true);
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				String inputString;
				try {
					inputString = connection.readString();
					logRead(inputString);
				} catch (Exception e) {
					logException(e);
				}
			}
		}
	}
	
	@Override
	public void interrupt() {
		super.interrupt();
		this.alive = false;
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
