package eu.robojob.irscw.external.robot;

import java.util.HashSet;
import java.util.Set;

import eu.robojob.irscw.external.AbstractServiceProvider;
import eu.robojob.irscw.external.communication.DisconnectedException;
import eu.robojob.irscw.external.communication.ResponseTimedOutException;
import eu.robojob.irscw.positioning.Coordinates;

public abstract class AbstractRobot extends AbstractServiceProvider {
	
	private GripperBody activeGripperBody;
	private Set<GripperBody> possibleGripperBodies;
	private int speed;
	
	public AbstractRobot(String id, Set<GripperBody> possibleGripperBodies, GripperBody activeGripperBody) {
		super(id);
		this.speed = 50;
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
	
	public void setActiveGripperBody(GripperBody body) {
		if (!possibleGripperBodies.contains(body)) {
			throw new IllegalArgumentException("Unknown GripperBody value.");
		}
		activeGripperBody = body;
	}
	
	public void setSpeed(int speedPercentage) throws DisconnectedException, ResponseTimedOutException {
		if ((speedPercentage < 0) || (speedPercentage > 100) || !((speedPercentage == 10) || (speedPercentage == 25) || (speedPercentage == 50) || (speedPercentage == 100))) {
			throw new IllegalArgumentException("Illegal speed value: " + speedPercentage + ", should be between 0 and 100");
		}
		this.speed = speedPercentage;
		sendSpeed(speedPercentage);
	}
	
	public int getSpeed() {
		return speed;
	}
	
	public Set<GripperBody> getPossibleGripperBodies() {
		return possibleGripperBodies;
	}

	public void setPossibleGripperBodies(Set<GripperBody> possibleGripperBodies) {
		this.possibleGripperBodies = possibleGripperBodies;
	}
	
	public abstract void restartProgram() throws DisconnectedException, ResponseTimedOutException;
	public abstract Coordinates getPosition() throws DisconnectedException, ResponseTimedOutException, RobotActionException;
	public abstract void sendSpeed(int speedPercentage) throws DisconnectedException, ResponseTimedOutException;
	public abstract void initiatePick(RobotPickSettings pickSettings) throws DisconnectedException, ResponseTimedOutException, RobotActionException, InterruptedException;
	public abstract void initiatePut(RobotPutSettings putSettings) throws DisconnectedException, ResponseTimedOutException, RobotActionException, InterruptedException;
	
	public abstract void finalizePut(RobotPutSettings putSettings) throws DisconnectedException, ResponseTimedOutException, RobotActionException, InterruptedException;
	public abstract void finalizePick(RobotPickSettings pickSettings) throws DisconnectedException, ResponseTimedOutException, RobotActionException, InterruptedException;
	
	public abstract void moveToAndWait(RobotPutSettings putSettings, boolean withPiece) throws DisconnectedException, ResponseTimedOutException, RobotActionException, InterruptedException;
	public abstract void teachedMoveToAndWait(RobotPutSettings putSettings, boolean withPiece) throws DisconnectedException, ResponseTimedOutException, RobotActionException, InterruptedException;
	public abstract void moveAway() throws DisconnectedException, ResponseTimedOutException, RobotActionException, InterruptedException;
	public abstract void teachedMoveAway() throws DisconnectedException, ResponseTimedOutException, RobotActionException, InterruptedException;
	
	public abstract void teachedMoveNoWait(RobotPutSettings putSettings, boolean withPiece) throws DisconnectedException, ResponseTimedOutException, RobotActionException, InterruptedException;
	
	public abstract void moveToHome() throws DisconnectedException, ResponseTimedOutException, RobotActionException;
	public abstract void moveToChangePoint() throws DisconnectedException, ResponseTimedOutException, RobotActionException;
	
	public abstract void initiateTeachedPick(RobotPickSettings pickSettings) throws DisconnectedException, ResponseTimedOutException, RobotActionException, InterruptedException;
	public abstract void initiateTeachedPut(RobotPutSettings putSettings) throws DisconnectedException, ResponseTimedOutException, RobotActionException, InterruptedException;
	
	public abstract void finalizeTeachedPick(RobotPickSettings pickSettings) throws DisconnectedException, ResponseTimedOutException, RobotActionException, InterruptedException;
	public abstract void finalizeTeachedPut(RobotPutSettings putSettings) throws DisconnectedException, ResponseTimedOutException, RobotActionException, InterruptedException;
	
	public abstract boolean validatePickSettings(RobotPickSettings pickSettings);
	public abstract boolean validatePutSettings(RobotPutSettings putSettings);
	
	public abstract void writeRegister(int registerNr, String value) throws DisconnectedException, ResponseTimedOutException, RobotActionException;
	public abstract void doPrage() throws DisconnectedException, ResponseTimedOutException, RobotActionException, InterruptedException;
	
	public abstract void continueProgram() throws DisconnectedException, ResponseTimedOutException;
	public abstract void abort() throws DisconnectedException, ResponseTimedOutException;
	
	public abstract void recalculateTCPs() throws DisconnectedException, ResponseTimedOutException;
	
	public abstract void stopCurrentAction();
	
	public abstract boolean isConnected();
	
	public String toString() {
		return "Robot: " + id;
	}
	
	public GripperBody getGripperBody() {
		return activeGripperBody;
	}

	public void setGripperBody(GripperBody gripperBody) {
		this.activeGripperBody = gripperBody;
	}
	
	public abstract RobotPickSettings getDefaultPickSettings();
	public abstract RobotPutSettings getDefaultPutSettings();
	
	public abstract void loadRobotSettings(RobotSettings robotSettings);
	public abstract RobotSettings getRobotSettings();
}
