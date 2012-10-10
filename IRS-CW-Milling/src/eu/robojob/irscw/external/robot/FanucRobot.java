package eu.robojob.irscw.external.robot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;

import eu.robojob.irscw.external.communication.ExternalCommunication;
import eu.robojob.irscw.external.communication.ExternalCommunicationThread;
import eu.robojob.irscw.external.communication.SocketConnection;
import eu.robojob.irscw.external.device.WorkArea;
import eu.robojob.irscw.positioning.Coordinates;
import eu.robojob.irscw.positioning.UserFrame;
import eu.robojob.irscw.threading.ThreadManager;
import eu.robojob.irscw.workpiece.WorkPiece;

public class FanucRobot extends AbstractRobot {

	private ExternalCommunication externalCommunication;
	
	/*private static final String STATUS = "STATUS";*/
	private static final String POSITION = "POSITION";
	/*private static final String PICK = "PICK";
	private static final String PUT = "PUT";
	private static final String RELEASE_PIECE = "RELEASE_PIECE";
	private static final String GRAB_PIECE = "GRAB_PIECE";
	private static final String MOVE_TO_SAFE_POINT = "MOVE_TO_SAFE_POINT";*/
	
	private static Logger logger = Logger.getLogger(FanucRobot.class);

	private static final int READ_TIMEOUT = 10000;
	
	public FanucRobot(String id, Set<GripperBody> gripperBodies, GripperBody gripperBody, SocketConnection socketConnection) {
		super(id, gripperBodies, gripperBody);
		ExternalCommunicationThread externalCommunicationThread = new ExternalCommunicationThread(socketConnection);
		ThreadManager.getInstance().submit(externalCommunicationThread);
		this.externalCommunication = new ExternalCommunication(externalCommunicationThread);
	}
	
	public FanucRobot(String id, SocketConnection socketConnection) {
		this(id, null, null, socketConnection);
	}
	
	@Override
	public String getStatus() throws IOException {
		if (!isConnected()) {
			throw new IOException(this + " was not connected");
		} else {
			//TODO
			return null;
		}
	}
	
	@Override
	public Coordinates getPosition() throws IOException {
		String response = externalCommunication.writeAndRead(POSITION, READ_TIMEOUT);
		String[] parsedResponse = response.split(";");
		Coordinates c = null;
		if (parsedResponse.length != 6) {
			throw new IllegalStateException("Corrupt response from robot");
		} else {
			Float[] convertedResponse = new Float[6];
			for (int i = 0; i < 6; i++)  {
				convertedResponse[i] = Float.parseFloat(parsedResponse[i]);
			}
			c = new Coordinates(convertedResponse[0], convertedResponse[1], convertedResponse[2], convertedResponse[3], convertedResponse[4], convertedResponse[5]);
		}
		logger.debug("got position from robot: " + response);
		return c;
	}

	@Override
	public void pick(AbstractRobotPickSettings pickSettings) throws IOException {
		FanucRobotPickSettings fanucPickSettings = (FanucRobotPickSettings) pickSettings;
		String response = externalCommunication.writeAndRead("PICK WITH GRIPPER: " + fanucPickSettings.getGripper().getId() + " ON HEAD: " + fanucPickSettings.getGripperHead().getId() +
				" ON LOCATION: " + fanucPickSettings.getLocation() + " WITH SMOOTH: " + fanucPickSettings.getSmoothPoint() + " IN WA: " + fanucPickSettings.getWorkArea().getId(), READ_TIMEOUT);
		logger.debug("response: " + response);
	}

	@Override
	public void put(AbstractRobotPutSettings putSettings) throws IOException {
		FanucRobotPutSettings fanucPutSettings = (FanucRobotPutSettings) putSettings;
		String response = externalCommunication.writeAndRead("PUT WITH GRIPPER: " + fanucPutSettings.getGripper().getId() + " ON HEAD: " + fanucPutSettings.getGripperHead().getId() +
				" ON LOCATION: " + fanucPutSettings.getLocation() + " WITH SMOOTH: " + fanucPutSettings.getSmoothPoint() + " IN WA: " + fanucPutSettings.getWorkArea().getId(), READ_TIMEOUT);
		logger.debug("response: " + response);
	}

	@Override
	public void releasePiece(AbstractRobotPutSettings putSettings) throws IOException {
		String response = externalCommunication.writeAndRead("RELEASE PIECE", READ_TIMEOUT);
		putSettings.getGripper().setWorkPiece(null);
		logger.debug("response: " + response);
	}

	@Override
	public void grabPiece(AbstractRobotPickSettings pickSettings) throws IOException {
		FanucRobotPickSettings fanucPickSettings = (FanucRobotPickSettings) pickSettings;
		pickSettings.getGripper().setWorkPiece(fanucPickSettings.getWorkPiece());
		String response = externalCommunication.writeAndRead("GRAB PIECE", READ_TIMEOUT);
		logger.debug("response: " + response);
	}

	@Override
	public void moveToHome() throws IOException {
		String response = externalCommunication.writeAndRead("MOVE TO HOME", READ_TIMEOUT);
		logger.debug("response: " + response);
	}
	

	@Override
	public void moveTo(UserFrame uf, Coordinates coordinates, AbstractRobotActionSettings transportSettings) {
		String response = externalCommunication.writeAndRead("MOVE TO UF: " + uf.getIdNumber() + " COORDINATES: " + coordinates + " WITH GRIPPER: " + transportSettings.getGripper().getId() + 
				" ON GRIPPER HEAD: " + transportSettings.getGripperHead().getId(), READ_TIMEOUT);
		logger.debug("response: " + response);
	}

	@Override
	public void setTeachModeEnabled(boolean enable) {
		String response = externalCommunication.writeAndRead("SET TEACH MODE: " + enable, READ_TIMEOUT);
		logger.debug("response: " + response);
	}
	
	public static class FanucRobotPickSettings extends AbstractRobotPickSettings {

		public FanucRobotPickSettings(WorkArea workArea, GripperHead gripperHead, Gripper gripper, Coordinates smoothPoint, Coordinates location, WorkPiece workPiece) {
			super(workArea, gripperHead, gripper, smoothPoint, location, workPiece);
		}
		
		public FanucRobotPickSettings() {
			super(null, null, null, null, null, null);
		}
		
	}
	public static class FanucRobotPutSettings extends AbstractRobotPutSettings {

		public FanucRobotPutSettings(WorkArea workArea, GripperHead gripperHead, Gripper gripper, Coordinates smoothPoint, Coordinates location) {
			super(workArea, gripperHead, gripper, smoothPoint, location);
		}
		
		public FanucRobotPutSettings() {
			super(null, null, null, null, null);
		}
	}
	
	public class FanucRobotSettings extends AbstractRobotSettings {
		
		protected GripperBody gripperBody;
		protected Map<GripperHead, Gripper> grippers;
		
		public FanucRobotSettings(GripperBody gripperBody, Map<GripperHead, Gripper> grippers) {
			this.gripperBody = gripperBody;
			this.grippers = grippers;
		}

		public void setGripper(GripperHead head, Gripper gripper) {
			grippers.put(head, gripper);
		}
		
		public Gripper getGripper(GripperHead head) {
			return grippers.get(head);
		}

		public GripperBody getGripperBody() {
			return gripperBody;
		}

		public Map<GripperHead, Gripper> getGrippers() {
			return grippers;
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
			List<Gripper> usedGrippers = new ArrayList<Gripper>();
			setGripperBody(settings.gripperBody);
			for (Entry<GripperHead, Gripper> entry : settings.getGrippers().entrySet()) {
				if (usedGrippers.contains(entry.getValue())) {
					logger.debug("gripper already used on other head");
				} else {
					entry.getKey().setGripper(entry.getValue());
					usedGrippers.add(entry.getValue());
				}
				
			}
		} else {
			throw new IllegalArgumentException("Unknown robot settings");
		}
	}

	@Override
	public AbstractRobotSettings getRobotSettings() {
		Map<GripperHead, Gripper> grippers = new HashMap<GripperHead, Gripper>();
		for(GripperHead head : getGripperBody().getGripperHeads()) {
			grippers.put(head, head.getGripper());
		}
		return new FanucRobotSettings(getGripperBody(), grippers);
	}

	@Override
	public boolean validatePickSettings(AbstractRobotPickSettings pickSettings) {
		FanucRobotPickSettings fanucPickSettings = (FanucRobotPickSettings) pickSettings;
		if ( 
				(fanucPickSettings.getGripperHead() != null) &&
				(fanucPickSettings.getGripper() != null) && 
				(getGripperBody().getActiveGripper(fanucPickSettings.getGripperHead()).equals(fanucPickSettings.getGripper())) &&
				(fanucPickSettings.getSmoothPoint() != null) &&
				(fanucPickSettings.getWorkArea() != null) &&
				(fanucPickSettings.getWorkPiece() != null)
			) {
			return true;
		} else {
			return false;
		}
				
	}

	@Override
	public boolean validatePutSettings(AbstractRobotPutSettings putSettings) {
		FanucRobotPutSettings fanucPutSettings = (FanucRobotPutSettings) putSettings;
		if ( 
				(fanucPutSettings.getGripperHead() != null) &&
				(fanucPutSettings.getGripper() != null) && 
				(getGripperBody().getActiveGripper(fanucPutSettings.getGripperHead()).equals(fanucPutSettings.getGripper())) &&
				(fanucPutSettings.getSmoothPoint() != null) &&
				(fanucPutSettings.getWorkArea() != null)
			) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean isConnected() {
		return externalCommunication.isConnected();
	}

}
