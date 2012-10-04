package eu.robojob.irscw.external.robot;

import java.io.IOException;
import java.util.Set;

import eu.robojob.irscw.external.communication.SocketConnection;
import eu.robojob.irscw.external.device.WorkArea;
import eu.robojob.irscw.positioning.Coordinates;
import eu.robojob.irscw.workpiece.WorkPieceDimensions;

public class FanucRobot extends AbstractRobot {

	private SocketConnection socketConnection; 
	
	private static final String STATUS = "STATUS";
	private static final String POSITION = "POSITION";
	private static final String PICK = "PICK";
	private static final String PUT = "PUT";
	private static final String RELEASE_PIECE = "RELEASE_PIECE";
	private static final String GRAB_PIECE = "GRAB_PIECE";
	private static final String MOVE_TO_SAFE_POINT = "MOVE_TO_SAFE_POINT";

	public FanucRobot(String id, Set<GripperBody> gripperBodies, GripperBody gripperBody, SocketConnection socketConnection) {
		super(id, gripperBodies, gripperBody);
		this.socketConnection = socketConnection;
	}
	
	public FanucRobot(String id, SocketConnection socketConnection) {
		this(id, null, null, socketConnection);
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

		public FanucRobotPickSettings(WorkArea workArea, GripperHead gripperHead, Gripper gripper, Coordinates smoothPoint, Coordinates location, WorkPieceDimensions workPieceDimensions) {
			super(workArea, gripperHead, gripper, smoothPoint, location, workPieceDimensions);
		}
		
		public FanucRobotPickSettings() {
			super(null, null, null, null, null, null);
		}
		
	}
	public static class FanucRobotPutSettings extends AbstractRobotPutSettings {

		public FanucRobotPutSettings(WorkArea workArea, GripperHead gripperHead, Gripper gripper, Coordinates smoothPoint, Coordinates location, WorkPieceDimensions workPieceDimensions) {
			super(workArea, gripperHead, gripper, smoothPoint, location, workPieceDimensions);
		}
		
		public FanucRobotPutSettings() {
			super(null, null, null, null, null, null);
		}
	}
	
	public class FanucRobotSettings extends AbstractRobotSettings {
		protected GripperHead head;
		protected Gripper gripper;
		
		public FanucRobotSettings(GripperHead head, Gripper gripper) {
			this.head = head;
			this.gripper = gripper;
		}

		public GripperHead getHead() {
			return head;
		}

		public Gripper getGripper() {
			return gripper;
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

	@Override
	public void loadRobotSettings(AbstractRobotSettings robotSettings) {
		if (robotSettings instanceof FanucRobotSettings) {
			FanucRobotSettings settings = (FanucRobotSettings) robotSettings;
			settings.getHead().setGripper(settings.getGripper());
		} else {
			throw new IllegalArgumentException("Unknown robot settings");
		}
	}

}
