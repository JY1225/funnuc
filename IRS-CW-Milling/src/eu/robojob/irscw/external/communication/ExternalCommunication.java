package eu.robojob.irscw.external.communication;

import java.io.IOException;

import eu.robojob.irscw.threading.ThreadManager;

public abstract class ExternalCommunication {

	public static final int READ_RETRY_INTERVAL = 50;
	public static final int DEFAULT_WAIT_TIMEOUT = 10000;
	
	private int defaultWaitTimeout;
	
	protected ExternalCommunicationThread extCommThread;
		
	public ExternalCommunication(SocketConnection socketConnection) {
		this.extCommThread = new ExternalCommunicationThread(socketConnection, this);
		ThreadManager.getInstance().submit(extCommThread);
		defaultWaitTimeout = DEFAULT_WAIT_TIMEOUT;
	}
	
	public synchronized boolean isConnected() {
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
	
	public synchronized void writeMessage(String message) throws DisconnectedException {
		extCommThread.writeMessage(message);
	}
	
	public void setDefaultWaitTimeout(int defaultWaitTimeout) {
		this.defaultWaitTimeout = defaultWaitTimeout;
	}
	
	public int getDefaultWaitTimeout() {
		return defaultWaitTimeout;
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
	
}
