package eu.robojob.irscw.external.robot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;

import eu.robojob.irscw.external.communication.CommunicationException;
import eu.robojob.irscw.external.communication.ExternalCommunication;
import eu.robojob.irscw.external.communication.SocketConnection;
import eu.robojob.irscw.external.device.WorkArea;
import eu.robojob.irscw.positioning.Coordinates;
import eu.robojob.irscw.positioning.UserFrame;
import eu.robojob.irscw.workpiece.WorkPiece;

public class FanucRobot extends AbstractRobot {

	private ExternalCommunication externalCommunication;
	
	private static final String POSITION = "POSITION";
	
	private static Logger logger = Logger.getLogger(FanucRobot.class);

	private static final int READ_TIMEOUT = 10000;
	
	public FanucRobot(String id, Set<GripperBody> gripperBodies, GripperBody gripperBody, SocketConnection socketConnection) {
		super(id, gripperBodies, gripperBody);
		this.externalCommunication = new ExternalCommunication(socketConnection);
	}
	
	public FanucRobot(String id, SocketConnection socketConnection) {
		this(id, null, null, socketConnection);
	}
	
	@Override
	public String getStatus() throws CommunicationException, RobotActionException {
		return null;
	}
	
	@Override
	public Coordinates getPosition() throws CommunicationException, RobotActionException {
		return null;
	}

	@Override
	public void pick(AbstractRobotPickSettings pickSettings) throws CommunicationException, RobotActionException {
	}

	@Override
	public void put(AbstractRobotPutSettings putSettings) throws CommunicationException, RobotActionException {
	}

	@Override
	public void releasePiece(AbstractRobotPutSettings putSettings) throws CommunicationException, RobotActionException {
	}

	@Override
	public void grabPiece(AbstractRobotPickSettings pickSettings) throws CommunicationException, RobotActionException {
	}

	@Override
	public void moveToHome() throws CommunicationException, RobotActionException {
	}
	

	@Override
	public void moveTo(UserFrame uf, Coordinates coordinates, AbstractRobotActionSettings transportSettings) throws CommunicationException, RobotActionException {
	}

	@Override
	public void setTeachModeEnabled(boolean enable) throws CommunicationException, RobotActionException {
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
