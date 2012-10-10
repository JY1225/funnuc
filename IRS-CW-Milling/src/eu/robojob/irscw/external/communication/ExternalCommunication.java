package eu.robojob.irscw.external.communication;

public class ExternalCommunication {

	private static final int READ_RETRY_INTERVAL = 500;
	
	private ExternalCommunicationThread extCommThread;
	
	public ExternalCommunication(ExternalCommunicationThread extCommThread) {
		this.extCommThread = extCommThread;
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
	
	public void writeMessage(String message) {
		extCommThread.writeMessage(message);
	}
	
	public String writeAndRead(String message, int timeout) {
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
			
		return readMessage;
	}
}
