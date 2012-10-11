package eu.robojob.irscw.external.communication;

import eu.robojob.irscw.threading.ThreadManager;

public class ExternalCommunication {

	private static final int READ_RETRY_INTERVAL = 500;
	private static final int DEFAULT_WAIT_TIMEOUT = 30000;
	
	private int defaultWaitTimeout;
	
	private ExternalCommunicationThread extCommThread;
		
	public ExternalCommunication(SocketConnection socketConnection) {
		this.extCommThread = new ExternalCommunicationThread(socketConnection);
		ThreadManager.getInstance().submit(extCommThread);
		defaultWaitTimeout = DEFAULT_WAIT_TIMEOUT;
	}
	
	public boolean isConnected() {
		return extCommThread.isConnected();
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
	
	public String writeAndRead(String message) throws DisconnectedException, ResponseTimedOutException {
		return writeAndRead(message, defaultWaitTimeout);
	}
	
	public String writeAndRead(String message, int timeout) throws DisconnectedException, ResponseTimedOutException {
		writeMessage(message);
		int waitingTime = 0;
		String readMessage = null;
		do {
			if (hasMessage()) {
				readMessage = getNextMessage();
				break;
			}
			int timeToWait = READ_RETRY_INTERVAL;
			if (timeout - waitingTime < READ_RETRY_INTERVAL) {
				timeToWait = timeout - waitingTime;
			}
			try {
				Thread.sleep(timeToWait);
			} catch (InterruptedException e) {
				break;
			}
			waitingTime += timeToWait;
		} while (waitingTime <= timeout);
		if (readMessage != null) {
			return readMessage;
		} else {
			throw new ResponseTimedOutException(this);
		}
	}
}
