package eu.robojob.irscw.external.robot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import eu.robojob.irscw.external.communication.socket.ExternalSocketCommunication;
import eu.robojob.irscw.external.communication.socket.SocketConnection;
import eu.robojob.irscw.external.communication.socket.SocketDisconnectedException;
import eu.robojob.irscw.external.communication.socket.SocketResponseTimedOutException;
import eu.robojob.irscw.positioning.Coordinates;

public class RobotSocketCommunication extends ExternalSocketCommunication {

	private StringBuffer command;
	private AbstractRobot robot;
		
	public RobotSocketCommunication(final SocketConnection socketConnection, final AbstractRobot fanucRobot) {
		super(socketConnection);
		this.robot = fanucRobot;
		this.command = new StringBuffer();
	}

	public synchronized void writeValues(final int commandId, final int ackId, final int timeout, final List<String> values) throws SocketDisconnectedException, SocketResponseTimedOutException, InterruptedException {
		command = new StringBuffer();
		command.append(commandId);
		command.append(";");
		for (String value : values) {
			command.append(value);
			command.append(";");
		}
		getExternalCommunicationThread().clearIncommingBuffer();
		getExternalCommunicationThread().writeString(command.toString());
		awaitResponse(ackId + ";", timeout);
	}
	
	public synchronized void writeCommand(final int commandId, final int ackId, final int timeout) throws SocketDisconnectedException, SocketResponseTimedOutException, InterruptedException {
		writeValues(commandId, ackId, timeout, new ArrayList<String>());
	}
	
	public synchronized void writeValue(final int commandId, final int ackId, final int timeout, final String value) throws SocketDisconnectedException, SocketResponseTimedOutException, InterruptedException {
		List<String> values = new ArrayList<String>();
		values.add(value);
		writeValues(commandId, ackId, timeout, values);
	}
	
	public synchronized List<String> readValues(final int commandId, final int ackId, final int timeout) throws SocketDisconnectedException, SocketResponseTimedOutException, InterruptedException {
		getExternalCommunicationThread().writeString(commandId + ";");
		String responseString = awaitResponse(ackId + ";", timeout);
		return parseResult(responseString.substring((ackId + ";").length()));
	}
	
	public List<String> parseResult(final String response) {
		String[] values = response.split(";");
		return new ArrayList<String>(Arrays.asList(values));
	}
	
	public synchronized Coordinates getPosition(final int waitTimeout) throws SocketResponseTimedOutException, SocketDisconnectedException, InterruptedException {
		long currentTime = System.currentTimeMillis();
		boolean timeout = false;
		while (!timeout) {
			if (System.currentTimeMillis() - currentTime >= waitTimeout) {
				timeout = true;
				break;
			}
			List<String> positionValues = readValues(RobotConstants.COMMAND_ASK_POSITION, RobotConstants.RESPONSE_ASK_POSITION, getDefaultWaitTimeout());
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
		robot.processRobotEvent(new RobotEvent(robot, RobotEvent.ROBOT_CONNECTED));
	}

	@Override
	public void disconnected() {
		robot.processRobotEvent(new RobotEvent(robot, RobotEvent.ROBOT_DISCONNECTED));
	}

	@Override
	public void iOExceptionOccured(final IOException e) {
		// this exception is already logged, and the robot was disconnected as a result
		// TODO handle this error in more detail if needed
	}

	@Override
	public String toString() {
		return "Fanuc robot communication: " + getExternalCommunicationThread().getSocketConnection().toString();
	}
}
