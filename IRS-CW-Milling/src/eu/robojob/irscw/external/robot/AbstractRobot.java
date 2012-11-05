package eu.robojob.irscw.external.robot;

import java.util.HashSet;
import java.util.Set;

import eu.robojob.irscw.external.AbstractServiceProvider;
import eu.robojob.irscw.external.communication.CommunicationException;
import eu.robojob.irscw.external.device.WorkArea;
import eu.robojob.irscw.positioning.Coordinates;
import eu.robojob.irscw.process.PickStep;
import eu.robojob.irscw.process.PutStep;
import eu.robojob.irscw.workpiece.WorkPiece;

public abstract class AbstractRobot extends AbstractServiceProvider {
	
	private GripperBody activeGripperBody;
	
	private Set<GripperBody> possibleGripperBodies;
	
	private int speed;
	
	public AbstractRobot(String id, Set<GripperBody> possibleGripperBodies, GripperBody activeGripperBody) {
		super(id);
		if (possibleGripperBodies != null) {
			this.possibleGripperBodies = possibleGripperBodies;
		} else {
			this.possibleGripperBodies = new HashSet<GripperBody>();
		}
		if (activeGripperBody != null) {
			setActiveGripperBody(activeGripperBody);
		}
	}
	
	public AbstractRobot(String id) {
		this(id, null, null);
	}
	
	public void setSpeed(int speedPercentage) throws CommunicationException {
		if ((speedPercentage < 0) || (speedPercentage > 100) || !((speedPercentage == 10) || (speedPercentage == 25) || (speedPercentage == 50) || (speedPercentage == 100))) {
			throw new IllegalArgumentException("Illegal speed value: " + speedPercentage + ", should be between 0 and 100");
		}
		this.speed = speedPercentage;
	}
	
	public int getSpeed() {
		return speed;
	}
	
	public void setActiveGripperBody(GripperBody body) {
		if (!possibleGripperBodies.contains(body)) {
			throw new IllegalArgumentException("Unknown GripperBody value");
		}
		
		activeGripperBody = body;
	}
	
	public Set<GripperBody> getPossibleGripperBodies() {
		return possibleGripperBodies;
	}

	public void setPossibleGripperBodies(Set<GripperBody> possibleGripperBodies) {
		this.possibleGripperBodies = possibleGripperBodies;
	}
	
	public abstract void restartProgram() throws CommunicationException;
	public abstract Coordinates getPosition() throws CommunicationException, RobotActionException;
	
	public abstract void initiatePick(AbstractRobotPickSettings pickSettings) throws CommunicationException, RobotActionException;
	public abstract void initiatePut(AbstractRobotPutSettings putSettings) throws CommunicationException, RobotActionException;
	
	public abstract void finalizePut(AbstractRobotPutSettings putSettings) throws CommunicationException, RobotActionException;
	public abstract void finalizePick(AbstractRobotPickSettings pickSettings) throws CommunicationException, RobotActionException;
	
	public abstract void moveToHome() throws CommunicationException, RobotActionException;
	public abstract void moveToChangePoint() throws CommunicationException, RobotActionException;
	
	public abstract void initiateTeachedPick(AbstractRobotPickSettings pickSettings) throws CommunicationException, RobotActionException;
	public abstract void initiateTeachedPut(AbstractRobotPutSettings putSettings) throws CommunicationException, RobotActionException;
	
	public abstract void finalizeTeachedPick(AbstractRobotPickSettings pickSettings) throws CommunicationException, RobotActionException;
	public abstract void finalizeTeachedPut(AbstractRobotPutSettings putSettings) throws CommunicationException, RobotActionException;
	
	public abstract boolean validatePickSettings(AbstractRobotPickSettings pickSettings);
	public abstract boolean validatePutSettings(AbstractRobotPutSettings putSettings);
	
	public abstract boolean isConnected();
	
	public String toString() {
		return "Robot: " + id;
	}
	
	public static abstract class AbstractRobotActionSettings {
		protected WorkArea workArea;
		protected GripperHead gripperHead;
		protected Gripper gripper;
		protected Coordinates smoothPoint;
		protected Coordinates location;
		protected float clampHeight; 
		
		public AbstractRobotActionSettings(WorkArea workArea, GripperHead gripperHead, Gripper gripper, Coordinates smoothPoint, Coordinates location, float clampHeight) {
			this.workArea = workArea;
			this.gripper = gripper;
			this.gripperHead = gripperHead;
			this.smoothPoint = smoothPoint;
			this.location = location;
			this.clampHeight = clampHeight;
		}
		public WorkArea getWorkArea() {
			return workArea;
		}
		public Gripper getGripper() {
			return gripper;
		}
		public GripperHead getGripperHead() {
			return gripperHead;
		}
		public void setGripperHead(GripperHead gripperHead) {
			this.gripperHead = gripperHead;
		}
		public Coordinates getSmoothPoint() {
			return smoothPoint;
		}
		public void setSmoothPoint(Coordinates smoothPoint) {
			this.smoothPoint = smoothPoint;
		}
		public Coordinates getLocation() {
			return location;
		}
		public void setLocation(Coordinates location) {
			this.location = location;
		}
		public void setWorkArea(WorkArea workArea) {
			this.workArea = workArea;
		}
		public void setGripper(Gripper gripper) {
			this.gripper = gripper;
		}
		public float getClampHeight() {
			return clampHeight;
		}
		public void setClampHeight(float clampHeight) {
			this.clampHeight = clampHeight;
		}
	}
	
	public static abstract class AbstractRobotPickSettings extends AbstractRobotActionSettings {
		protected PickStep pickStep;
		protected WorkPiece workPiece;

		public AbstractRobotPickSettings(WorkArea workArea, GripperHead gripperHead, Gripper gripper, Coordinates smoothPoint, Coordinates location, float clampHeight, WorkPiece workPiece) {
			super(workArea, gripperHead, gripper, smoothPoint, location, clampHeight);
			this.workPiece = workPiece;
		}

		public WorkPiece getWorkPiece() {
			return workPiece;
		}

		public void setWorkPiece(WorkPiece workPiece) {
			this.workPiece = workPiece;
		}

		public PickStep getPickStep() {
			return pickStep;
		}

		public void setPickStep(PickStep pickStep) {
			this.pickStep = pickStep;
		}
		
	}
	
	// dimensions for put follow from pick
	public static abstract class AbstractRobotPutSettings extends AbstractRobotActionSettings {
		
		protected PutStep putStep;
		
		public AbstractRobotPutSettings(WorkArea workArea, GripperHead gripperHead, Gripper gripper, Coordinates smoothPoint, Coordinates location, float clampHeight) {
			super(workArea, gripperHead, gripper, smoothPoint, location, clampHeight);
		}

		public PutStep getPutStep() {
			return putStep;
		}

		public void setPutStep(PutStep putStep) {
			this.putStep = putStep;
		}
		
	}
	
	public static abstract class AbstractRobotSettings {
	}
	
	public GripperBody getGripperBody() {
		return activeGripperBody;
	}

	public void setGripperBody(GripperBody gripperBody) {
		this.activeGripperBody = gripperBody;
	}
	
	public abstract AbstractRobotPickSettings getDefaultPickSettings();
	public abstract AbstractRobotPutSettings getDefaultPutSettings();
	
	public abstract void loadRobotSettings(AbstractRobotSettings robotSettings);
	public abstract AbstractRobotSettings getRobotSettings();
}
