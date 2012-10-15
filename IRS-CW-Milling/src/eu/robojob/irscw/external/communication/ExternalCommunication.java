package eu.robojob.irscw.external.communication;

import eu.robojob.irscw.threading.ThreadManager;

public class ExternalCommunication {

	public static final int READ_RETRY_INTERVAL = 500;
	public static final int DEFAULT_WAIT_TIMEOUT = 30000;
	
	private int defaultWaitTimeout;
	
	protected ExternalCommunicationThread extCommThread;
		
	public ExternalCommunication(SocketConnection socketConnection) {
		this.extCommThread = new ExternalCommunicationThread(socketConnection);
		ThreadManager.getInstance().submit(extCommThread);
		defaultWaitTimeout = DEFAULT_WAIT_TIMEOUT;
	}
	
	public boolean isConnected() {
		return extCommThread.isConnected();
	}
	
	public void disconnect() {
		extCommThread.disconnectAndStop();
	}
	
	public boolean hasMessage() {
		return extCommThread.hasNextMessage();
	}
	
	public String getNextMessage() {
		return extCommThread.getNextMessage();
	}
	
	public void writeMessage(String message) throws DisconnectedException {
		extCommThread.writeMessage(message);
	}
	
	public void setDefaultWaitTimeout(int defaultWaitTimeout) {
		this.defaultWaitTimeout = defaultWaitTimeout;
	}
	
	public int getDefaultWaitTimeout() {
		return defaultWaitTimeout;
	}
	
}
