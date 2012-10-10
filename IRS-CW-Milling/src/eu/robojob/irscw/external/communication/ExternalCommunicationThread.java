package eu.robojob.irscw.external.communication;

import java.io.IOException;
import java.util.LinkedList;

import org.apache.log4j.Logger;

/**
 * All devices act as servers, so this communication-thread will try to keep the connection alive, and will act as
 * a buffer for communicating with them
 * @author Peter
 */
public class ExternalCommunicationThread extends Thread {

	private static final Logger logger = Logger.getLogger(ExternalCommunicationThread.class);
	
	private static final int CONNECTION_RETRY_INTERVAL = 5000;
	
	private SocketConnection socketConnection;
	
	private boolean alive;
	
	private LinkedList<String> incommingMessages;
	
	public ExternalCommunicationThread(SocketConnection socketConnection) {
		this.socketConnection = socketConnection;
		this.incommingMessages = new LinkedList<String>();
		if (socketConnection != null) {
			alive = true;
		} else {
			logger.info("empty socket connection");
			alive = false;
		}
	}
	
	@Override
	public void run() {
		logger.info("Starting communication thread");
		while(alive) {
			if (!socketConnection.isConnected()) {
				logger.info("Connection " + socketConnection + " offline, trying to connect...");
				try {
					socketConnection.connect();
				} catch (IOException e) {
					logger.error("Could not connect : " + e);
					// wait CONNECTION_RETRY_INTERVAL before re-trying
					try {
						Thread.sleep(CONNECTION_RETRY_INTERVAL);
					} catch (InterruptedException e1) {
						// we got interrupted, so let's just stop executing!
						logger.error("Woken up! " + e1);
						alive = false;
					}
				}
			} else {
				// connected!
				// this thread should just try reading messages and add these to the reading buffer
				try {
					String icommingMsg = socketConnection.readString();
					putMessage(icommingMsg);
				} catch (IOException e) {
					logger.error("Error while reading: " + e);
				}
			}
		}
		logger.info("Ending communication thread");
	}
	
	public synchronized boolean hasNextMessage() {
		if (incommingMessages.size() > 0) {
			return true;
		} else {
			return false;
		}
	}
	
	public synchronized String getNextMessage() {
		if (hasNextMessage()) {
			return incommingMessages.removeFirst();
		} else {
			throw new IllegalStateException("No messages in queue");
		}
	}
	
	public synchronized void putMessage(String message) {
		incommingMessages.addLast(message);
	}
	
	public void writeMessage(String message) {
		if (socketConnection.isConnected()) {
			socketConnection.sendString(message);
		} else {
			throw new IllegalStateException("Can't send message, connection is down");
		}
	}
	
	public boolean isConnected() {
		return socketConnection.isConnected();
	}
	
	@Override
	public void interrupt() {
		disconnectAndStop();
	}
	
	public void disconnectAndStop() {
		if (socketConnection.isConnected()) {
			try {
				socketConnection.disconnect();
			} catch (IOException e) {
				logger.error(e);
			} finally {
				alive = false;
			}
		}
	}
	
}
