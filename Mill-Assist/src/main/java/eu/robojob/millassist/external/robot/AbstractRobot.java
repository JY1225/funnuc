package eu.robojob.millassist.external.robot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.robojob.millassist.external.AbstractServiceProvider;
import eu.robojob.millassist.external.communication.AbstractCommunicationException;
import eu.robojob.millassist.external.device.Clamping;
import eu.robojob.millassist.positioning.Coordinates;

public abstract class AbstractRobot extends AbstractServiceProvider {
	
	private GripperBody activeGripperBody;
	private Set<GripperBody> possibleGripperBodies;
	private int speed;
	private float payload;
	
	private Set<RobotListener> listeners;
	private boolean stopAction;
	private boolean statusChanged;
	private Object syncObject;
	
	private static Logger logger = LogManager.getLogger(AbstractRobot.class.getName());
	private static final String EXCEPTION_DISCONNECTED_WHILE_WAITING = "AbstractRobot.disconnectedWhileWaiting";
	
	private Set<RobotAlarm> alarms;
	private int currentStatus;
	private double zrest;
	
	private RobotAlarm robotTimeout;
	
	private AbstractRobotActionSettings<?> currentActionSettings;

	public AbstractRobot(final String name, final Set<GripperBody> possibleGripperBodies, final GripperBody activeGripperBody, 
			final float payload) {
		super(name);
		this.speed = 10;
		this.listeners = new HashSet<RobotListener>();
		this.statusChanged = false;
		this.syncObject = new Object();
		this.stopAction = false;
		this.alarms = new HashSet<RobotAlarm>();
		this.currentStatus = 0;
		this.currentActionSettings = null;
		this.payload = payload;
		this.zrest = -1;
		if (possibleGripperBodies != null) {
			this.possibleGripperBodies = possibleGripperBodies;
			this.activeGripperBody = possibleGripperBodies.iterator().next();
		} else {
			this.possibleGripperBodies = new HashSet<GripperBody>();
		}
		if (activeGripperBody != null) {
			setActiveGripperBody(activeGripperBody);
		}
	}
	
	public AbstractRobot(final String name, final float payload) {
		this(name, null, null, payload);
	}
	
	public void addListener(final RobotListener listener) {
		listeners.add(listener);
		logger.debug("Now listening to [" + toString() + "]: " + listener.toString());
	}
	
	public void removeListener(final RobotListener listener) {
		listeners.remove(listener);
		logger.debug("Stopped listening to [" + toString() + "]: " + listener.toString());
	}
	
	public void processRobotEvent(final RobotEvent event) {
		switch(event.getId()) {
			case RobotEvent.ROBOT_CONNECTED:
				for (RobotListener listener : listeners) {
					listener.robotConnected(event);
				}
				break;
			case RobotEvent.ROBOT_DISCONNECTED:
				statusChanged();
				for (RobotListener listener : listeners) {
					listener.robotDisconnected(event);
				}
				break;
			case RobotEvent.ALARMS_OCCURED:
				for (RobotListener listener : listeners) {
					listener.robotAlarmsOccured((RobotAlarmsOccuredEvent) event);
				}
				break;
			case RobotEvent.STATUS_CHANGED:
				statusChanged();
				for (RobotListener listener : listeners) {
					listener.robotStatusChanged(event);
				}
				break;
			case RobotEvent.ZREST_CHANGED:
				for (RobotListener listener : listeners) {
					listener.robotZRestChanged(event);
				}
				break;
			case RobotEvent.SPEED_CHANGED:
				for (RobotListener listener : listeners) {
					listener.robotSpeedChanged(event);
				}
				break;
			default:
				throw new IllegalArgumentException("Unknown event type.");
		}
	}
	
	private void statusChanged() {
		synchronized (syncObject) {
			statusChanged = true;
			syncObject.notifyAll();
		}
	}

	public void interruptCurrentAction() {
		logger.debug("Interrupting current action of: " + getName());
		setRobotTimeout(null);
		stopAction = true;
		try {
			abort();
		} catch (AbstractCommunicationException | InterruptedException e) {
			if (isConnected()) {
				e.printStackTrace();
				logger.error("Could not abort current action of [" + getName() + "] because of " + e.getMessage());
			}
		}
		synchronized (syncObject) {
			syncObject.notifyAll();
		}
	}
	
	public int getStatus() {
		return currentStatus;
	}
	
	public void setStatus(final int status) {
		this.currentStatus = status;
	}
	
	public double getZRest() {
		return zrest;
	}
	
	public void setZRest(final double zrest) {
		this.zrest = zrest;
	}
	
	public Set<RobotAlarm> getAlarms() {
		return alarms;
	}
	
	public void setAlarms(final Set<RobotAlarm> alarms) {
		this.alarms = alarms;
	}
	
	protected boolean waitForStatus(final int status, final long timeout) throws RobotActionException, InterruptedException {
		long waitedTime = 0;
		stopAction = false;
		// check status before we start
		if ((currentStatus & status) > 0) {
			return true;
		}
		// also check connection status
		if (!isConnected()) {
			throw new RobotActionException(this, EXCEPTION_DISCONNECTED_WHILE_WAITING);
		}
		while ((timeout == 0) || ((timeout > 0) && (waitedTime < timeout))) {
			// start waiting
			statusChanged = false;
			if ((timeout == 0) || ((timeout > 0) && (timeout > waitedTime))) {
				long timeBeforeWait = System.currentTimeMillis();
				synchronized (syncObject) {
					if ((currentStatus & status) > 0) {
						return true;
					}
					if (timeout > 0) {
						syncObject.wait(timeout - waitedTime);
					} else {
						syncObject.wait();
					}
				}
				// at this point the wait is finished, either by a notify (status changed, or request to stop), or by a timeout
				if (stopAction) {
					stopAction = false;
					throw new InterruptedException("Waiting for status: " + status + " got interrupted");
				}
				// just to be sure, check connection
				if (!isConnected()) {
					throw new RobotActionException(this, EXCEPTION_DISCONNECTED_WHILE_WAITING);
				}
				// check if status has changed
				if ((statusChanged) && ((currentStatus & status) > 0)) {
					statusChanged = false;
					return true;
				}
				// update waited time
				waitedTime += System.currentTimeMillis() - timeBeforeWait;
			} else {
				return false;
			}
		} 
		return false;
	}
	
	protected void waitForStatus(final int status) throws RobotActionException, InterruptedException {
		waitForStatus(status, 0);
	}
	
	public synchronized boolean isExecutionInProgress() {
		if (currentActionSettings == null) {
			return false;
		}
		return true;
	}

	public AbstractRobotActionSettings<?> getCurrentActionSettings() {
		return currentActionSettings;
	}

	public synchronized void setCurrentActionSettings(final AbstractRobotActionSettings<?> currentActionSettings) {
		this.currentActionSettings = currentActionSettings;
	}

	public void setActiveGripperBody(final GripperBody body) {
		if (!possibleGripperBodies.contains(body)) {
			throw new IllegalArgumentException("Unknown GripperBody value.");
		}
		activeGripperBody = body;
	}
	
	public void setSpeed(final int speedPercentage) throws AbstractCommunicationException, InterruptedException {
		if ((speedPercentage < 0) || (speedPercentage > 100) || !((speedPercentage == 5) || (speedPercentage == 10) || (speedPercentage == 25) || (speedPercentage == 50) || (speedPercentage == 75) || (speedPercentage == 100))) {
			throw new IllegalArgumentException("Illegal speed value: " + speedPercentage + ", should be between 0 and 100");
		}
		this.speed = speedPercentage;
		sendSpeed(speedPercentage);
		processRobotEvent(new RobotEvent(this, RobotEvent.SPEED_CHANGED));
	}
	
	public int getSpeed() {
		return speed;
	}
	
	public Set<GripperBody> getPossibleGripperBodies() {
		return possibleGripperBodies;
	}

	public void setPossibleGripperBodies(final Set<GripperBody> possibleGripperBodies) {
		this.possibleGripperBodies = possibleGripperBodies;
	}
	
	public GripperBody getGripperBodyById(final int id) {
		for (GripperBody gripperBody : possibleGripperBodies) {
			if (gripperBody.getId() == id) {
				return gripperBody;
			}
		}
		return null;
	}
	
	public GripperHead getGripperHeadById(final int id) {
		for (GripperBody gripperBody : possibleGripperBodies) {
			GripperHead head = gripperBody.getGripperHeadById(id);
			if (head != null) {
				return head;
			}
		}
		return null;
	}
	
	public void setRobotTimeout(final RobotAlarm robotTimeout) {
		this.robotTimeout = robotTimeout;
	}
	
	public RobotAlarm getRobotTimeout() {
		return robotTimeout;
	}
		
	public abstract void updateStatusZRestAndAlarms() throws AbstractCommunicationException, InterruptedException;
	public abstract void restartProgram() throws AbstractCommunicationException, InterruptedException;
	public abstract void reset() throws AbstractCommunicationException, InterruptedException;
	public abstract Coordinates getPosition() throws AbstractCommunicationException, RobotActionException, InterruptedException;
	public abstract void sendSpeed(int speedPercentage) throws AbstractCommunicationException, InterruptedException;
	public abstract void writeRegister(int registerNr, String value) throws AbstractCommunicationException, RobotActionException, InterruptedException;
	public abstract void continueProgram() throws AbstractCommunicationException, InterruptedException;
	public abstract void abort() throws AbstractCommunicationException, InterruptedException;
	public abstract void recalculateTCPs() throws AbstractCommunicationException, InterruptedException;	
	public abstract boolean isConnected();
	public abstract void disconnect();
	public abstract void moveToHome() throws AbstractCommunicationException, RobotActionException, InterruptedException;
	public abstract void moveToChangePoint() throws AbstractCommunicationException, RobotActionException, InterruptedException;
	public abstract void moveToCustomPosition() throws AbstractCommunicationException, RobotActionException, InterruptedException;
	
	public abstract void initiatePut(RobotPutSettings putSettings, Clamping clamping) throws AbstractCommunicationException, RobotActionException, InterruptedException;
	public abstract void continuePutTillAtLocation() throws AbstractCommunicationException, RobotActionException, InterruptedException;
	public abstract void continuePutTillClampAck() throws AbstractCommunicationException, RobotActionException, InterruptedException;
	public abstract void continuePutTillIPPoint() throws AbstractCommunicationException, RobotActionException, InterruptedException;
	public abstract void finalizePut() throws AbstractCommunicationException, RobotActionException, InterruptedException;
	
	public abstract void initiatePick(RobotPickSettings pickSettings, Clamping clamping) throws AbstractCommunicationException, RobotActionException, InterruptedException;
	public abstract void continuePickTillAtLocation() throws AbstractCommunicationException, RobotActionException, InterruptedException;
	public abstract void continuePickTillUnclampAck() throws AbstractCommunicationException, RobotActionException, InterruptedException;
	public abstract void continuePickTillIPPoint() throws AbstractCommunicationException, RobotActionException, InterruptedException;
	public abstract void finalizePick() throws AbstractCommunicationException, RobotActionException, InterruptedException;
	
	public abstract void initiateMoveWithPiece(RobotPutSettings putSettings) throws AbstractCommunicationException, RobotActionException, InterruptedException;
	public abstract void initiateMoveWithPieceNoAction(RobotPutSettings putSettings) throws AbstractCommunicationException, RobotActionException, InterruptedException;
	public abstract void initiateMoveWithoutPieceNoAction(final RobotPutSettings putSettings) throws AbstractCommunicationException, RobotActionException, InterruptedException;
	public abstract void continueMoveTillAtLocation() throws AbstractCommunicationException, RobotActionException, InterruptedException;
	public abstract void continueMoveTillWait() throws AbstractCommunicationException, RobotActionException, InterruptedException;
	public abstract void performIOAction() throws AbstractCommunicationException, RobotActionException, InterruptedException;
	public abstract void continueMoveWithPieceTillIPPoint() throws AbstractCommunicationException, RobotActionException, InterruptedException;
	public abstract void continueMoveWithoutPieceTillIPPoint() throws AbstractCommunicationException, RobotActionException, InterruptedException;
	public abstract void finalizeMovePiece() throws AbstractCommunicationException, RobotActionException, InterruptedException;
	
	public String toString() {
		return "Robot: " + getName();
	}
	
	public GripperBody getGripperBody() {
		return activeGripperBody;
	}

	public void setGripperBody(final GripperBody gripperBody) {
		this.activeGripperBody = gripperBody;
	}
	
	public void loadRobotSettings(final RobotSettings robotSettings) {
		List<Gripper> usedGrippers = new ArrayList<Gripper>();
		setGripperBody(robotSettings.getGripperBody());
		for (Entry<GripperHead, Gripper> entry : robotSettings.getGrippers().entrySet()) {
			if (usedGrippers.contains(entry.getValue())) {
				logger.debug("gripper already used on other head");
			} else {
				entry.getKey().setGripper(entry.getValue());
				usedGrippers.add(entry.getValue());
			}
			
		}
	}

	public RobotSettings getRobotSettings() {
		Map<GripperHead, Gripper> grippers = new HashMap<GripperHead, Gripper>();
		for (GripperHead head : getGripperBody().getGripperHeads()) {
			grippers.put(head, head.getGripper());
		}
		return new RobotSettings(getGripperBody(), grippers);
	}

	public boolean validatePickSettings(final RobotPickSettings pickSettings) {
		if ((pickSettings.getGripperHead() != null) && (getGripperBody().getActiveGripper(pickSettings.getGripperHead()) != null)
				&& (pickSettings.getGripperHead().getGripper() != null) 
				&& (getGripperBody().getActiveGripper(pickSettings.getGripperHead()).equals(pickSettings.getGripperHead().getGripper()))
				//&& (pickSettings.getSmoothPoint() != null)
				&& (pickSettings.getWorkArea() != null)
				&& (pickSettings.getWorkPiece() != null)
			) {
			return true;
		}
		return false;	
	}

	public boolean validatePutSettings(final RobotPutSettings putSettings) {
		if ((putSettings.getGripperHead() != null) && (getGripperBody().getActiveGripper(putSettings.getGripperHead()) != null)
				&& (putSettings.getGripperHead().getGripper() != null) 
				&& (getGripperBody().getActiveGripper(putSettings.getGripperHead()).equals(putSettings.getGripperHead().getGripper()))
				//&& (putSettings.getSmoothPoint() != null)
				&& (putSettings.getWorkArea() != null)
			) {
			return true;
		}
		return false;
	}

	public abstract RobotPickSettings getDefaultPickSettings();
	public abstract RobotPutSettings getDefaultPutSettings();
	public float getMaxWorkPieceWeight() {
		return payload;
	}
	
	public float getPayload() {
		return payload;
	}
	
	public void setPayload(final float payload) {
		this.payload = payload;
	}
	
}
