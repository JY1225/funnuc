package eu.robojob.irscw.external.communication.socket;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.robojob.irscw.external.communication.ExternalCommunicationThread;
import eu.robojob.irscw.threading.ThreadManager;

public abstract class ExternalSocketCommunication {

	public static final int READ_RETRY_INTERVAL = 50;
	public static final int DEFAULT_WAIT_TIMEOUT = 10000;
	
	private int defaultWaitTimeout;
	
	private ExternalCommunicationThread extCommThread;
	
	private static Logger logger = LogManager.getLogger(ExternalSocketCommunication.class.getName());
		
	public ExternalSocketCommunication(final SocketConnection socketConnection) {
		this.extCommThread = new ExternalCommunicationThread(socketConnection, this);
		ThreadManager.submit(extCommThread);
		defaultWaitTimeout = DEFAULT_WAIT_TIMEOUT;
	}
	
	public ExternalCommunicationThread getExternalCommunicationThread() {
		return extCommThread;
	}
	
	public boolean isConnected() {
		return extCommThread.isConnected();
	}
	
	public synchronized void disconnect() {
		extCommThread.disconnect();
	}
	
	public synchronized boolean hasMessage() {
		return extCommThread.hasNextMessage();
	}
	
	public synchronized String getNextMessage() {
		return extCommThread.getNextMessage();
	}
	
	public synchronized void writeMessage(final String message) throws SocketDisconnectedException {
		extCommThread.writeMessage(message);
	}
	
	public void setDefaultWaitTimeout(final int defaultWaitTimeout) {
		this.defaultWaitTimeout = defaultWaitTimeout;
	}
	
	public int getDefaultWaitTimeout() {
		return defaultWaitTimeout;
	}
	
	protected String awaitResponse(final String command, final int timeout) throws SocketResponseTimedOutException, InterruptedException, SocketDisconnectedException {
		int waitedTime = 0;
		while (waitedTime <= timeout) {
			if (!getExternalCommunicationThread().isConnected()) {
				// no longer connected
				throw new SocketDisconnectedException(getExternalCommunicationThread().getSocketConnection());
			}
			if (getExternalCommunicationThread().hasNextMessage()) {
				String response = getExternalCommunicationThread().getNextMessage();
				response = response.replaceAll("[^A-Za-z0-9,;\\.]", "");
				if (response.startsWith(command)) {
					return response;
				} else {
					logger.error("Got wrong response: " + response + ", command was: " + command);
					throw new IllegalStateException("Got wrong response: " + response + ", command was: " + command);
				}
			}
			int timeToWait = READ_RETRY_INTERVAL;
			if (timeout - waitedTime < READ_RETRY_INTERVAL) {
				timeToWait = timeout - waitedTime;
			}
			if (timeToWait <= 0) {
				break;
			}
			try {
				Thread.sleep(timeToWait);
			} catch (InterruptedException e) {
				throw e;
			}
			waitedTime += timeToWait;
		}
		throw new SocketResponseTimedOutException(getExternalCommunicationThread().getSocketConnection());
	}
	
	/**
	 * This message will be called when the communication thread established connection.
	 */
	public abstract void connected();
	/**
	 * This message will be called when the communication thread established disconnection.
	 */
	public abstract void disconnected();
	/**
	 * This message will be called when an IOException occurred in the communication thread.
	 */
	public abstract void iOExceptionOccured(IOException e);
	@Override public abstract String toString();
	
}
