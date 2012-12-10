package eu.robojob.irscw.external.communication;

import java.io.IOException;
import java.util.LinkedList;

import org.apache.log4j.Logger;

/**
 * All external devices act as servers, so this communication-thread will continuously try to establish connection, read incoming messages, and will act as
 * a buffer for incoming messages
 */
public class ExternalCommunicationThread extends Thread {

	private static final Logger logger = Logger.getLogger(ExternalCommunicationThread.class);
	
	private static final int CONNECTION_RETRY_INTERVAL = 1000;
	
	private SocketConnection socketConnection;
	
	/**
	 * this parameter will indicate liveliness of the thread, not the connection
	 */
	private boolean alive;
	
	private LinkedList<String> incommingMessages;
	private ExternalCommunication externalCommunication;
	
	private boolean wasConnected;
	
	public ExternalCommunicationThread(SocketConnection socketConnection, ExternalCommunication externalCommunication) {
		this.socketConnection = socketConnection;
		this.incommingMessages = new LinkedList<String>();
		this.alive = true;
		this.wasConnected = false;
		this.externalCommunication = externalCommunication;
		if (socketConnection == null) {
			throw new IllegalArgumentException("A valid SocketConnection-object must be provided");
		}
	}
	
	@Override
	public void run() {
		while(alive) {
			if (!socketConnection.isConnected()) {
				// not connected...
				if (wasConnected) {
					wasConnected = false;
					logger.info("Disconnected from " + socketConnection + ".");
					// disconnected, so spread the word
					externalCommunication.disconnected();
				}
				try {
					socketConnection.connect();
					wasConnected = true;
					logger.info("Connected to " + socketConnection + ".");
					// connected, so spread the word
					externalCommunication.connected();
				} catch (IOException e) {
					try {
						Thread.sleep(CONNECTION_RETRY_INTERVAL);
					} catch (InterruptedException e1) {
						// we got interrupted, so let's just stop executing! Are already disconnected so no need to call disconnect().
						alive = false;
					}
				}
			} else {
				// connected...
				// this thread should just try reading messages and add these to the reading buffer, will also serve as 
				// check for connection-liveness
				try {
					String icommingMessage = socketConnection.readMessage();
					putMessage(icommingMessage);
				} catch (IOException e) {
					if (alive) {
						logger.info("IOException detected: " + e.getMessage());
						e.printStackTrace();
						// exception occurred, spread the word and disconnect
						externalCommunication.iOExceptionOccured(e);
						socketConnection.disconnect();
					}
				} catch (DisconnectedException e) {
					throw new IllegalStateException("This catch shouldn't be reached as in case of disconnection, we don't want to read, but we want to connect first.");
				}
			}
		}
		logger.info(toString() + " ended...");
	}
	
	public synchronized boolean hasNextMessage() {
		if (incommingMessages.size() > 0) {
			return true;
		} else {
			return false;
		}
	}
	
	public SocketConnection getSocketConnection() {
		return socketConnection;
	}
	
	public synchronized String getNextMessage() {
		if (hasNextMessage()) {
			return incommingMessages.removeFirst();
		} else {
			throw new IllegalStateException("No messages in queue");
		}
	}
	
	public synchronized void clearIncommingBuffer() {
		incommingMessages.clear();
	}
	
	private synchronized void putMessage(String message) {
		incommingMessages.addLast(message);
	}
	
	public synchronized void writeMessage(String message) throws DisconnectedException {
		socketConnection.send(message);
	}
	
	public synchronized void writeCharacter(char character) throws DisconnectedException {
		socketConnection.send(character);
	}
	
	public synchronized void writeString(String message) throws DisconnectedException {
		socketConnection.send(message);
	}
	
	public synchronized boolean isConnected() {
		return socketConnection.isConnected();
	}
	
	@Override
	public void interrupt() {
		disconnectAndStop();
	}
	
	public synchronized void disconnectAndStop() {
		disconnect();
		alive = false;
	}
	
	public synchronized void disconnect() {
		if (socketConnection.isConnected()) {
			socketConnection.disconnect();
		}
	}
	
	public String toString() {
		return "ExternalCommunicationThread: " + socketConnection;
	}
	
}
