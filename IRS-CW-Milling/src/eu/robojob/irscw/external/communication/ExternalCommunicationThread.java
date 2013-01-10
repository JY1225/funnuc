package eu.robojob.irscw.external.communication;

import java.io.IOException;
import java.util.LinkedList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.robojob.irscw.external.communication.socket.ExternalSocketCommunication;
import eu.robojob.irscw.external.communication.socket.SocketConnection;
import eu.robojob.irscw.external.communication.socket.SocketDisconnectedException;

/**
 * All external devices act as servers, so this communication-thread will continuously try to establish connection, read incoming messages, and will act as
 * a buffer for incoming messages
 */
public class ExternalCommunicationThread extends Thread {

	private static Logger logger = LogManager.getLogger(ExternalCommunicationThread.class.getName());
	
	private static final int CONNECTION_RETRY_INTERVAL = 1000;
	
	private SocketConnection socketConnection;
	
	/**
	 * this parameter will indicate liveliness of the thread, not the connection
	 */
	private boolean alive;
	
	private LinkedList<String> incommingMessages;
	private ExternalSocketCommunication externalCommunication;
	
	private boolean wasConnected;
	
	public ExternalCommunicationThread(final SocketConnection socketConnection, final ExternalSocketCommunication externalCommunication) {
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
		try {
			while (alive) {
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
							logger.error("IOException detected: " + e.getMessage() + " so disconnectiong...");
							// exception occurred, spread the word (disconnection occurs automatically)
							externalCommunication.iOExceptionOccured(e);
						}
					} catch (SocketDisconnectedException e) {
						// we got disconnected, retry connection
						logger.info("Disconnected during reading, about to retry connection...");
					}
				}
			}
		}  catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
		} catch (Throwable t) {
			logger.error(t);
			t.printStackTrace();
		}
		logger.info(toString() + " ended...");
	}
	
	public synchronized boolean hasNextMessage() {
		if (incommingMessages.size() > 0) {
			return true;
		} 
		return false;
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
	
	private synchronized void putMessage(final String message) {
		incommingMessages.addLast(message);
	}
	
	public synchronized void writeMessage(final String message) throws SocketDisconnectedException {
		socketConnection.send(message);
	}
	
	public synchronized void writeCharacter(final char character) throws SocketDisconnectedException {
		socketConnection.send(character);
	}
	
	public synchronized void writeString(final String message) throws SocketDisconnectedException {
		socketConnection.send(message);
	}
	
	public synchronized boolean isConnected() {
		return socketConnection.isConnected();
	}
	
	@Override
	public void interrupt() {
		disconnectAndStop();
		super.interrupt();
	}
	
	public synchronized void disconnectAndStop() {
		if (socketConnection.isConnected()) {
			disconnect();
		}
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
