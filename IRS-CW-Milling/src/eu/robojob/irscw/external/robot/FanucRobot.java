package eu.robojob.irscw.external.robot;

import java.io.IOException;

import eu.robojob.irscw.external.communication.SocketConnection;
import eu.robojob.irscw.external.device.WorkArea;
import eu.robojob.irscw.positioning.Coordinates;

public class FanucRobot extends AbstractRobot {

	private SocketConnection socketConnection; 
	
	private static final String STATUS = "STATUS";
	private static final String POSITION = "POSITION";
	private static final String PICK = "PICK";
	private static final String PUT = "PUT";
	private static final String RELEASE_PIECE = "RELEASE_PIECE";
	private static final String GRAB_PIECE = "GRAB_PIECE";
	private static final String MOVE_TO_SAFE_POINT = "MOVE_TO_SAFE_POINT";

	public FanucRobot(String id, GripperBody gripperBody, SocketConnection socketConnection) {
		super(id, gripperBody);
		this.socketConnection = socketConnection;
	}
	
	public FanucRobot(String id, SocketConnection socketConnection) {
		this(id, null, socketConnection);
	}
	
	@Override
	public String getStatus() throws IOException {
		if (!socketConnection.isConnected()) {
			throw new IOException(this + " was not connected");
		} else {
			return socketConnection.synchronizedSendAndRead(STATUS);
		}
	}
	
	//TODO parse result to coordinates object
	@Override
	public Coordinates getPosition() throws IOException {
		if (!socketConnection.isConnected()) {
			throw new IOException(this + " was not connected");
		} else {
			String response = socketConnection.synchronizedSendAndRead(POSITION);
			return null;
		}
	}

	@Override
	public void pick(AbstractRobotPickSettings pickSettings) throws IOException {
		if (!socketConnection.isConnected()) {
			throw new IOException(this + " was not connected");
		} else {
			String response = socketConnection.synchronizedSendAndRead(PICK);
		}
	}

	@Override
	public void put(AbstractRobotPutSettings putSettings) throws IOException {
		if (!socketConnection.isConnected()) {
			throw new IOException(this + " was not connected");
		} else {
			String response = socketConnection.synchronizedSendAndRead(PUT);
		}
	}

	@Override
	public void releasePiece(AbstractRobotPutSettings putSettings)
			throws IOException {
		if (!socketConnection.isConnected()) {
			throw new IOException(this + " was not connected");
		} else {
			String response = socketConnection.synchronizedSendAndRead(RELEASE_PIECE);
		}
	}

	@Override
	public void grabPiece(AbstractRobotPickSettings pickSettings)
			throws IOException {
		if (!socketConnection.isConnected()) {
			throw new IOException(this + " was not connected");
		} else {
			String response = socketConnection.synchronizedSendAndRead(GRAB_PIECE);
		}
	}

	@Override
	public void moveToSafePoint() throws IOException {
		if (!socketConnection.isConnected()) {
			throw new IOException(this + " was not connected");
		} else {
			String response = socketConnection.synchronizedSendAndRead(MOVE_TO_SAFE_POINT);
		}
	}
	
	public static class FanucRobotPickSettings extends AbstractRobotPickSettings {

		public FanucRobotPickSettings(WorkArea workArea, Gripper gripper, Coordinates smoothPoint, Coordinates location) {
			super(workArea, gripper, smoothPoint, location);
		}
		
		public FanucRobotPickSettings() {
			super(null, null, null, null);
		}
		
	}
	public static class FanucRobotPutSettings extends AbstractRobotPutSettings {

		public FanucRobotPutSettings(WorkArea workArea, Gripper gripper, Coordinates smoothPoint, Coordinates location) {
			super(workArea, gripper, smoothPoint, location);
		}
		
		public FanucRobotPutSettings() {
			super(null, null, null, null);
		}
	}
	@Override
	public AbstractRobotPickSettings getDefaultPickSettings() {
		return new FanucRobotPickSettings();
	}

	@Override
	public AbstractRobotPutSettings getDefaultPutSettings() {
		return new FanucRobotPutSettings();
	}

}
