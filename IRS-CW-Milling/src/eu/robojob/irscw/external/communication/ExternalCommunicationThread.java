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
	
	private static final int CONNECTION_RETRY_INTERVAL = 1000;
	
	private SocketConnection socketConnection;
	
	private boolean alive;
	
	private LinkedList<Character> incommingCharacters;
	
	public ExternalCommunicationThread(SocketConnection socketConnection) {
		this.socketConnection = socketConnection;
		this.incommingCharacters = new LinkedList<Character>();
		this.alive = true;
		if (socketConnection == null) {
			throw new IllegalArgumentException("SocketConnection must be provided");
		}
	}
	
	@Override
	public void run() {
		logger.info(toString() + " started...");
		while(alive) {
			if (!socketConnection.isConnected()) {
				logger.info(socketConnection + " offline, trying to connect...");
				try {
					socketConnection.connect();
				} catch (IOException e) {
					logger.error("Could not connect : " + e);
					// wait CONNECTION_RETRY_INTERVAL before re-trying
					try {
						Thread.sleep(CONNECTION_RETRY_INTERVAL);
					} catch (InterruptedException e1) {
						// we got interrupted, so let's just stop executing!
						logger.info("Waiting for re-trying connection interrupted, so stopping thread...");
						alive = false;
					}
				}
			} else {
				// connected!
				// this thread should just try reading messages and add these to the reading buffer, will also serve as 
				// check for connection-liveness
				try {
					char icommingChar = socketConnection.read();
					putMessage(icommingChar);
				} catch (IOException e) {
					if (alive) {
						logger.error("Error while reading: " + e);
						e.printStackTrace();
						// disconnect to be sure
						socketConnection.disconnect();
					}
				} catch (DisconnectedException e) {
					throw new IllegalStateException("This catch shouldn't be reached as in case of disconnected socket, connection occurs");
				}
			}
		}
		logger.info(toString() + " ended...");
	}
	
	public synchronized boolean hasNextCharacter() {
		if (incommingCharacters.size() > 0) {
			return true;
		} else {
			return false;
		}
	}
	
	public synchronized char getNextCharacter() {
		if (hasNextCharacter()) {
			return incommingCharacters.removeFirst();
		} else {
			throw new IllegalStateException("No messages in queue");
		}
	}
	
	public synchronized void clearIncommingCharacterBuffer() {
		incommingCharacters.clear();
	}
	
	private synchronized void putMessage(char character) {
		incommingCharacters.addLast(character);
	}
	
	public void writeMessage(String message) throws DisconnectedException {
		socketConnection.sendString(message);
	}
	
	public void writeCharacter(char character) throws DisconnectedException {
		socketConnection.sendCharacter(character);
	}
	
	public void writeString(String message) throws DisconnectedException {
		socketConnection.sendString(message);
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
			alive = false;
			socketConnection.disconnect();
		}
	}
	
	public String toString() {
		return "CommunicatingThread: " + socketConnection;
	}
	
}
