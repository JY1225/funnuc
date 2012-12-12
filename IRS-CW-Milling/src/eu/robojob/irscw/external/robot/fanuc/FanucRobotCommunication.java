package eu.robojob.irscw.external.robot.fanuc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.robojob.irscw.external.communication.AbstractCommunicationException;
import eu.robojob.irscw.external.communication.DisconnectedException;
import eu.robojob.irscw.external.communication.ExternalCommunication;
import eu.robojob.irscw.external.communication.ResponseTimedOutException;
import eu.robojob.irscw.external.communication.SocketConnection;
import eu.robojob.irscw.positioning.Coordinates;

public class FanucRobotCommunication extends ExternalCommunication {

	private StringBuffer command;
	private FanucRobot fanucRobot;
	
	private static final Logger logger = LogManager.getLogger(FanucRobotCommunication.class.getName());
	
	public FanucRobotCommunication(SocketConnection socketConnection, FanucRobot fanucRobot) {
		super(socketConnection);
		this.fanucRobot = fanucRobot;
		this.command = new StringBuffer();
	}

	public synchronized void writeValues(int commandId, int ackId, int timeout, List<String> values) throws DisconnectedException, ResponseTimedOutException {
		command = new StringBuffer();
		command.append(commandId);
		command.append(";");
		for (String value : values) {
			command.append(value);
			command.append(";");
		}
		int waitedTime = 0;
		getExternalCommunicationThread().writeString(command.toString());
		do {
			if (getExternalCommunicationThread().hasNextMessage()) {
				String message = getExternalCommunicationThread().getNextMessage();
				message = message.replaceAll(" ", "");
				message = message.substring(0, message.length()-1);
				if (message.equals(ackId + ";")) {
					return;
				} else {
					throw new IllegalStateException("Illegal reponse: " + message + " but expected: " + ackId);
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
			} catch(InterruptedException e) {
				break;
			}
			waitedTime += timeToWait;
		} while (waitedTime < timeout);
		throw new ResponseTimedOutException(getExternalCommunicationThread().getSocketConnection());
	}

	public synchronized void writeCommand(int commandId, int ackId, int timeout) throws DisconnectedException, ResponseTimedOutException {
		writeValues(commandId, ackId, timeout, new ArrayList<String>());
	}
	
	public synchronized void writeValue(int commandId, int ackId, int timeout, String value) throws DisconnectedException, ResponseTimedOutException {
		List<String> values = new ArrayList<String>();
		values.add(value);
		writeValues(commandId, ackId, timeout, values);
	}
	
	public synchronized List<String> readValues(int commandId, int ackId, int timeout) throws DisconnectedException, ResponseTimedOutException {
		int waitedTime = 0;
		getExternalCommunicationThread().writeString(commandId + ";");
		do {
			if (getExternalCommunicationThread().hasNextMessage()) {
				String response = getExternalCommunicationThread().getNextMessage();
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
			if (timeToWait <= 0) {
				break;
			}
			try {
				Thread.sleep(timeToWait);
			} catch(InterruptedException e) {
				break;
			}
			waitedTime += timeToWait;
		} while (waitedTime <= timeout);
		throw new ResponseTimedOutException(getExternalCommunicationThread().getSocketConnection());
	}
	
	public List<String> parseResult(String response) {
		String[] values = response.split(";");
		return new ArrayList<String>(Arrays.asList(values));
	}
	
	public synchronized Coordinates getPosition(int waitTimeout) throws ResponseTimedOutException, DisconnectedException {
		long currentTime = System.currentTimeMillis();
		boolean timeout = false;
		while (!timeout) {
			if (System.currentTimeMillis() - currentTime >= waitTimeout) {
				timeout = true;
				break;
			}
			List<String> positionValues = readValues(FanucRobotConstants.COMMAND_ASK_POSITION, FanucRobotConstants.RESPONSE_ASK_POSITION, getDefaultWaitTimeout());
			float x = Float.parseFloat(positionValues.get(0));
			float y = Float.parseFloat(positionValues.get(1));
			float z = Float.parseFloat(positionValues.get(2));
			float w = Float.parseFloat(positionValues.get(3));
			float p = Float.parseFloat(positionValues.get(4));
			float r = Float.parseFloat(positionValues.get(5));
			return new Coordinates(x, y, z, w, p, r);
		}
		return null;
	}

	@Override
	public void connected() {
		logger.info("**Connected to robot!**");
		fanucRobot.processFanucRobotEvent(new FanucRobotEvent(fanucRobot, FanucRobotEvent.ROBOT_CONNECTED));
	}

	@Override
	public void disconnected() {
		fanucRobot.processFanucRobotEvent(new FanucRobotEvent(fanucRobot, FanucRobotEvent.ROBOT_DISCONNECTED));
	}

	@Override
	public void iOExceptionOccured(IOException e) {
		// we just log the error here
		logger.error(e);
		e.printStackTrace();
	}

	@Override
	public String toString() {
		return "Fanuc robot communication: " + getExternalCommunicationThread().getSocketConnection().toString();
	}
}
