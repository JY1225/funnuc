package eu.robojob.irscw.external.robot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import eu.robojob.irscw.external.communication.CommunicationException;
import eu.robojob.irscw.external.communication.DisconnectedException;
import eu.robojob.irscw.external.communication.ExternalCommunication;
import eu.robojob.irscw.external.communication.ResponseTimedOutException;
import eu.robojob.irscw.external.communication.SocketConnection;

public class FanucRobotCommunication extends ExternalCommunication {

	private StringBuffer command;
	
	public FanucRobotCommunication(SocketConnection socketConnection) {
		super(socketConnection);
		this.command = new StringBuffer();
	}

	public void writeValues(int commandId, int ackId, int timeout, List<String> values) throws DisconnectedException, ResponseTimedOutException {
		command = new StringBuffer();
		command.append(commandId);
		command.append(";");
		for (String value : values) {
			command.append(value);
			command.append(";");
		}
		int waitedTime = 0;
		extCommThread.writeString(command.toString());
		do {
			if (extCommThread.hasNextMessage()) {
				String message = extCommThread.getNextMessage();
				message = message.replaceAll(" ", "");
				message = message.substring(0, message.length()-1);
				if (message.equals(ackId + ";")) {
					return;
				} else {
					throw new IllegalStateException("Illegal reponse");
				}
			}
			int timeToWait = READ_RETRY_INTERVAL;
			if (timeout - waitedTime < READ_RETRY_INTERVAL) {
				timeToWait = timeout - waitedTime;
			}
			try {
				Thread.sleep(timeToWait);
			} catch(InterruptedException e) {
				break;
			}
			waitedTime += timeToWait;
		} while (waitedTime < timeout);
		throw new ResponseTimedOutException(this);
	}

	public void writeCommand(int commandId, int ackId, int timeout) throws DisconnectedException, ResponseTimedOutException {
		writeValues(commandId, ackId, timeout, new ArrayList<String>());
	}
	
	public void writeValue(int commandId, int ackId, int timeout, String value) throws DisconnectedException, ResponseTimedOutException {
		List<String> values = new ArrayList<String>();
		values.add(value);
		writeValues(commandId, ackId, timeout, values);
	}
	
	public List<String> readValues(int commandId, int ackId, int timeout) throws DisconnectedException, ResponseTimedOutException {
		int waitedTime = 0;
		extCommThread.writeString(commandId + ";");
		do {
			if (extCommThread.hasNextMessage()) {
				String response = extCommThread.getNextMessage();
				response = response.replaceAll(" ", "");
				//response = response.substring(0, response.length()-1);
				if (response.startsWith(ackId + ";")) {
					return parseResult(response.substring((ackId + ";").length()));
				} else {
					throw new IllegalStateException("Illegal response");
				}
			}
			int timeToWait = READ_RETRY_INTERVAL;
			if (timeout - waitedTime < READ_RETRY_INTERVAL) {
				timeToWait = timeout - waitedTime;
			}
			try {
				Thread.sleep(timeToWait);
			} catch(InterruptedException e) {
				break;
			}
			waitedTime += timeToWait;
		} while (waitedTime <= timeout);
		throw new ResponseTimedOutException(this);
	}
	
	public List<String> parseResult(String response) {
		String[] values = response.split(";");
		return new ArrayList<String>(Arrays.asList(values));
	}
	
	public boolean checkStatusValue(int valueIndex, int value, int waitTimeout) throws CommunicationException, DisconnectedException {
		long currentTime = System.currentTimeMillis();
		boolean timeout = false;
		while (!timeout) {
			if (System.currentTimeMillis() - currentTime >= waitTimeout) {
				timeout = true;
				break;
			}
			List<String> statusValues = readValues(FanucRobotConstants.COMMAND_ASK_STATUS, FanucRobotConstants.RESPONSE_ASK_STATUS, getDefaultWaitTimeout());
			if (statusValues.get(valueIndex).equals("" + value)) {
				return true;
			}
		}
		return false;
	}
}
