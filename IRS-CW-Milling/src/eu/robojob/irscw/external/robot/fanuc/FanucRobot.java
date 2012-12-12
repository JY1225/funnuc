package eu.robojob.irscw.external.robot.fanuc;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.robojob.irscw.external.communication.AbstractCommunicationException;
import eu.robojob.irscw.external.communication.DisconnectedException;
import eu.robojob.irscw.external.communication.ResponseTimedOutException;
import eu.robojob.irscw.external.communication.SocketConnection;
import eu.robojob.irscw.external.device.Clamping;
import eu.robojob.irscw.external.device.WorkArea;
import eu.robojob.irscw.external.robot.AbstractRobot;
import eu.robojob.irscw.external.robot.Gripper;
import eu.robojob.irscw.external.robot.GripperBody;
import eu.robojob.irscw.external.robot.GripperHead;
import eu.robojob.irscw.external.robot.RobotActionException;
import eu.robojob.irscw.external.robot.RobotPickSettings;
import eu.robojob.irscw.external.robot.RobotPutSettings;
import eu.robojob.irscw.external.robot.RobotSettings;
import eu.robojob.irscw.positioning.Coordinates;
import eu.robojob.irscw.process.event.ActiveStepChangedEvent;
import eu.robojob.irscw.threading.ThreadManager;
import eu.robojob.irscw.workpiece.WorkPieceDimensions;

public class FanucRobot extends AbstractRobot {

	private FanucRobotCommunication fanucRobotCommunication;
	
	private static final int WRITE_VALUES_TIMEOUT = 5000;
	private static final int MOVE_TO_LOCATION_TIMEOUT = 3 * 60 * 1000;
	private static final int MOVE_FINISH_TIMEOUT = 3 * 60 * 1000;
	private static final int ASK_POSITION_TIMEOUT = 50000;
	private static final int PICK_TEACH_TIMEOUT = 10 * 60 * 1000;
	private static final int PUT_TEACH_TIMEOUT = 10 * 60 * 1000;
	private static final int ASK_STATUS_TIMEOUT = 5 * 1000;
	
	private static final int WRITE_REGISTER_TIMEOUT = 5000;
	private static final int PRAGE_TIMEOUT = 2 * 60 * 1000;
	
	private boolean stopAction;
	private Set<FanucRobotListener> listeners;
	
	private FanucRobotStatus status;
	
	private static Logger logger = LogManager.getLogger(FanucRobot.class.getName());
	
	private static final String EXCEPTION_DISCONNECTED_WHILE_WAITING = "FanucRobot.disconnectedWhileWaiting";
	private static final String EXCEPTION_PRAGE_TIMEOUT = "FanucRobot.prageTimeout";
	private static final String EXCEPTION_MOVE_TO_PICK_POSITION_TIMEOUT = "FanucRobot.moveToPickPositionTimeout";
	private static final String EXCEPTION_MOVE_TO_PUT_POSITION_TIMEOUT = "FanucRobot.moveToPutPositionTimeout";
	private static final String EXCEPTION_PUT_CLAMP_TIMEOUT = "FanucRobot.putClampTimeout";
	private static final String EXCEPTION_PICK_UNCLAMP_TIMEOUT = "FanucRobot.pickUnclampTimeout";
	private static final String EXCEPTION_TEACHING_TIMEOUT = "FanucRobot.teachingTimeout";
	private static final String EXCEPTION_AFTER_TEACHING_TIMEOUT = "FanucRobot.afterTeachingTimeout";
	private static final String EXCEPTION_MOVE_TO_POSITION_TIMEOUT = "FanucRobot.moveToPositionTimeout";
	private static final String EXCEPTION_MOVE_AWAY_TIMEOUT = "FanucRobot.moveAwayTimeout";
	
	private boolean statusChanged;
	private Object syncObject;
	
	public FanucRobot(String id, Set<GripperBody> gripperBodies, GripperBody gripperBody, SocketConnection socketConnection) {
		super(id, gripperBodies, gripperBody);
		this.statusChanged = false;
		syncObject = new Object();
		this.fanucRobotCommunication = new FanucRobotCommunication(socketConnection, this);
		this.listeners = new HashSet<FanucRobotListener>();
		FanucRobotMonitoringThread monitoringThread = new FanucRobotMonitoringThread(this);
		ThreadManager.getInstance().submit(monitoringThread);
		this.stopAction = false;
	}
	
	public FanucRobot(String id, SocketConnection socketConnection) {
		this(id, null, null, socketConnection);
	}
	
	public void updateStatus() throws AbstractCommunicationException {
		List<String> values = fanucRobotCommunication.readValues(FanucRobotConstants.COMMAND_ASK_STATUS, FanucRobotConstants.RESPONSE_ASK_STATUS, ASK_STATUS_TIMEOUT);
		int errorId = Integer.parseInt(values.get(0));
		int controllerValue = Integer.parseInt(values.get(1));
		int controllerString = Integer.parseInt(values.get(2));
		double zrest = Float.parseFloat(values.get(3));
		this.status = new FanucRobotStatus(errorId, controllerValue, controllerString, zrest, super.getSpeed());
	}
	
	public synchronized FanucRobotStatus getStatus() {
		return status;
	}
	
	public void addListener(FanucRobotListener listener) {
		logger.info("added listener: " + listener);
		listeners.add(listener);
	}
	
	public void removeListener(FanucRobotListener listener) {
		logger.info("removed listener: " + listener);
		listeners.remove(listener);
	}
	
	public void processFanucRobotEvent(FanucRobotEvent event) {
		switch(event.getId()) {
			case FanucRobotEvent.ROBOT_CONNECTED:
				for (FanucRobotListener listener : listeners) {
					listener.robotConnected(event);
				}
				break;
			case FanucRobotEvent.ROBOT_DISCONNECTED:
				statusChanged();
				for (FanucRobotListener listener : listeners) {
					listener.robotDisconnected(event);
				}
				break;
			case FanucRobotEvent.ALARMS_OCCURED:
				if (((FanucRobotAlarmsOccuredEvent) event).getAlarms().size() > 0) {
					logger.info("ALARM!!");
				} else {
					logger.info("GEEN ALARMS MEER!");
				}
				
				for (FanucRobotListener listener : listeners) {
					listener.robotAlarmsOccured((FanucRobotAlarmsOccuredEvent) event);
				}
				break;
			case FanucRobotEvent.STATUS_CHANGED:
				statusChanged();
				for (FanucRobotListener listener : listeners) {
					listener.robotStatusChanged((FanucRobotStatusChangedEvent) event);
				}
				break;
			default:
					break;
		}
	}
	
	private void statusChanged() {
		synchronized(syncObject) {
			statusChanged = true;
			syncObject.notifyAll();
		}
	}
	

	@Override
	public void stopCurrentAction() {
		logger.info("stopping Fanuc Robot action");
		stopAction = true;
		try {
			fanucRobotCommunication.writeCommand(FanucRobotConstants.COMMAND_ABORT, FanucRobotConstants.RESPONSE_ABORT, WRITE_VALUES_TIMEOUT);
		} catch (DisconnectedException | ResponseTimedOutException e) {
			e.printStackTrace();
		}
		synchronized(syncObject) {
			syncObject.notifyAll();
		}
	}
	
	private boolean waitForStatus(int status, long timeout) throws RobotActionException, InterruptedException {
		long waitedTime = 0;
		stopAction = false;
		do {
			long lastTime = System.currentTimeMillis();
			if ((getStatus().getControllerString() & status) > 0) {
				return true;
			} else {
				if (!isConnected()) {
					throw new RobotActionException(this, EXCEPTION_DISCONNECTED_WHILE_WAITING);
				}
				try {
					statusChanged = false;
					if (timeout > waitedTime) {
						synchronized(syncObject) {
							syncObject.wait(timeout - waitedTime);
						}
					}
				} catch (InterruptedException e) {
					if (!statusChanged) {
						if (stopAction) {
							stopAction = false;
							throw e;
						} else {
							e.printStackTrace();
						}
					}
				} 
				if (stopAction) {
					stopAction = false;
					throw new InterruptedException();
				}
				if (!isConnected()) {
					throw new RobotActionException(this, EXCEPTION_DISCONNECTED_WHILE_WAITING);
				}
				waitedTime += (System.currentTimeMillis() - lastTime);
				if (statusChanged == true) {
					if ((getStatus().getControllerString() & status) > 0) {
						return true;
					}
				}
			}
		} while (waitedTime < timeout);
		return false;
	}

	@Override
	public void sendSpeed(int speedPercentage) throws DisconnectedException, ResponseTimedOutException {
		fanucRobotCommunication.writeValue(FanucRobotConstants.COMMAND_SET_SPEED, FanucRobotConstants.RESPONSE_SET_SPEED, WRITE_VALUES_TIMEOUT, speedPercentage + "");
		this.status.setSpeed(speedPercentage);
		processFanucRobotEvent(new FanucRobotStatusChangedEvent(this, status));
	}
	
	@Override
	public Coordinates getPosition() throws DisconnectedException, ResponseTimedOutException, RobotActionException {
		Coordinates position = fanucRobotCommunication.getPosition(ASK_POSITION_TIMEOUT);
		return position;
	}
	

	@Override
	public void continueProgram() throws DisconnectedException, ResponseTimedOutException {
		fanucRobotCommunication.writeCommand(FanucRobotConstants.COMMAND_CONTINUE, FanucRobotConstants.RESPONSE_CONTINUE, WRITE_VALUES_TIMEOUT);
	}
	
	@Override
	public void abort() throws DisconnectedException, ResponseTimedOutException {
		fanucRobotCommunication.writeCommand(FanucRobotConstants.COMMAND_ABORT, FanucRobotConstants.RESPONSE_ABORT, WRITE_VALUES_TIMEOUT);
	}
	
	public synchronized void disconnect() {
		fanucRobotCommunication.disconnect();
	}
	
	@Override
	public void restartProgram() throws DisconnectedException, ResponseTimedOutException {
		// write start service
		fanucRobotCommunication.writeCommand(FanucRobotConstants.COMMAND_RESET, FanucRobotConstants.RESPONSE_RESET, WRITE_VALUES_TIMEOUT);
		fanucRobotCommunication.writeCommand(FanucRobotConstants.COMMAND_RESTART_PROGRAM, FanucRobotConstants.RESPONSE_RESTART_PROGRAM, WRITE_VALUES_TIMEOUT);
		setSpeed(25);
	}

	@Override
	public void writeRegister(int registerNr, String value) throws DisconnectedException, ResponseTimedOutException, RobotActionException {
		List<String> values = new ArrayList<String>();
		values.add("" + registerNr);
		values.add(value);
		fanucRobotCommunication.writeValues(FanucRobotConstants.COMMAND_WRITE_REGISTER, FanucRobotConstants.RESPONSE_WRITE_REGISTER, WRITE_REGISTER_TIMEOUT, values);
	}

	@Override
	public void doPrage() throws DisconnectedException, ResponseTimedOutException, RobotActionException, InterruptedException {
		writeCommand(FanucRobotConstants.PERMISSIONS_COMMAND_DO_PRAGE);
		boolean prageSucceeded = waitForStatus(FanucRobotConstants.STATUS_PRAGE_FINISHED, PRAGE_TIMEOUT);
		if (!prageSucceeded) {
			logger.info("Troubles!");
			throw new RobotActionException(this, EXCEPTION_PRAGE_TIMEOUT);
		}
	}

	@Override
	public void initiatePick(RobotPickSettings pickSettings) throws DisconnectedException, ResponseTimedOutException, RobotActionException, InterruptedException {
		FanucRobotPickSettings fPickSettings = (FanucRobotPickSettings) pickSettings;		
		// write service gripper set
		writeServiceGripperSet(false, pickSettings.getGripperHead().getId(), this.getGripperBody().getGripperHead("A"), this.getGripperBody().getGripperHead("B"), FanucRobotConstants.SERVICE_GRIPPER_SERVICE_TYPE_PICK);
		// write service handling set
		int ppMode = FanucRobotConstants.SERVICE_HANDLING_PP_MODE_ORDER_12;
		if (fPickSettings.doMachineAirblow) {
			ppMode = ppMode | FanucRobotConstants.SERVICE_HANDLING_PP_MODE_AIRBLOW;
			logger.info("ALSO SENT: AIRBLOW!!");
		}
		writeServiceHandlingSet(pickSettings.isFreeAfter(), ppMode, pickSettings.getWorkPiece().getDimensions());
		// write service point set
		Coordinates pickLocation = new Coordinates(fPickSettings.getLocation());
		//pickLocation.offset(new Coordinates(0, 0, fPickSettings.getWorkPiece().getDimensions().getHeight(), 0, 0, 0));
		writeServicePointSet(fPickSettings.getWorkArea(), pickLocation, fPickSettings.getSmoothPoint(), fPickSettings.getWorkPiece().getDimensions(), fPickSettings.getWorkArea().getActiveClamping());
		// write command
		writeCommand(FanucRobotConstants.PERMISSIONS_COMMAND_PICK);
		// write start service
		fanucRobotCommunication.writeValue(FanucRobotConstants.COMMAND_START_SERVICE, FanucRobotConstants.RESPONSE_START_SERVICE, WRITE_VALUES_TIMEOUT, "1");
		// we now wait for the robot to indicate he moved to its location
		boolean waitingForRelease = waitForStatus(FanucRobotConstants.STATUS_PICK_RELEASE_REQUEST, MOVE_TO_LOCATION_TIMEOUT);
		if (!waitingForRelease) {
			throw new RobotActionException(this, EXCEPTION_MOVE_TO_PICK_POSITION_TIMEOUT);
		}
	}
	
	@Override
	public void initiatePut(RobotPutSettings putSettings) throws DisconnectedException, ResponseTimedOutException, RobotActionException, InterruptedException {
		FanucRobotPutSettings fPutSettings = (FanucRobotPutSettings) putSettings;
		// write service gripper set
		writeServiceGripperSet(false, putSettings.getGripperHead().getId(), this.getGripperBody().getGripperHead("A"), this.getGripperBody().getGripperHead("B"), FanucRobotConstants.SERVICE_GRIPPER_SERVICE_TYPE_PUT);
		// write service handling set
		int ppMode = FanucRobotConstants.SERVICE_HANDLING_PP_MODE_ORDER_12;
		if (fPutSettings.doMachineAirblow) {
			ppMode = ppMode | FanucRobotConstants.SERVICE_HANDLING_PP_MODE_AIRBLOW;
			logger.info("ALSO SENT: AIRBLOW!!");
		}
		writeServiceHandlingSet(putSettings.isFreeAfter(), ppMode, fPutSettings.getGripperHead().getGripper().getWorkPiece().getDimensions());
		// write service point set
		if (fPutSettings.getGripperHead().getGripper().getWorkPiece() == null) {
			throw new IllegalStateException("When executing put, the gripper should contain a workpiece");
		}
		writeServicePointSet(fPutSettings.getWorkArea(), fPutSettings.getLocation(), fPutSettings.getSmoothPoint(), fPutSettings.getGripperHead().getGripper().getWorkPiece().getDimensions(), fPutSettings.getWorkArea().getActiveClamping());
		// write command
		writeCommand(FanucRobotConstants.PERMISSIONS_COMMAND_PUT);
		fanucRobotCommunication.writeValue(FanucRobotConstants.COMMAND_START_SERVICE, FanucRobotConstants.RESPONSE_START_SERVICE, WRITE_VALUES_TIMEOUT, "1");
		boolean waitingForRelease = waitForStatus(FanucRobotConstants.STATUS_PUT_CLAMP_REQUEST, MOVE_TO_LOCATION_TIMEOUT);
		if (!waitingForRelease) {
			throw new RobotActionException(this, EXCEPTION_MOVE_TO_PUT_POSITION_TIMEOUT);
		}
	}

	@Override
	public void finalizePut(RobotPutSettings putSettings) throws DisconnectedException, ResponseTimedOutException, RobotActionException, InterruptedException {
		writeCommand(FanucRobotConstants.PERMISSIONS_COMMAND_PUT_CLAMP_ACK);
		boolean waitingForPickFinished = waitForStatus(FanucRobotConstants.STATUS_PUT_FINISHED, MOVE_FINISH_TIMEOUT);
		if (waitingForPickFinished) {
			return;
		} else {
			throw new RobotActionException(this, EXCEPTION_PUT_CLAMP_TIMEOUT);
		}
	}

	@Override
	public void finalizePick(RobotPickSettings pickSettings) throws DisconnectedException, ResponseTimedOutException, RobotActionException, InterruptedException {
		pickSettings.getGripperHead().getGripper().setWorkPiece(pickSettings.getWorkPiece());
		writeCommand(FanucRobotConstants.PERMISSIONS_COMMAND_PICK_RELEASE_ACK);
		boolean waitingForPickFinished = waitForStatus(FanucRobotConstants.STATUS_PICK_FINISHED, MOVE_FINISH_TIMEOUT);
		if (waitingForPickFinished) {
			return;
		} else {
			throw new RobotActionException(this, EXCEPTION_PICK_UNCLAMP_TIMEOUT);
		}
	}
	
	@Override
	public void initiateTeachedPick(RobotPickSettings pickSettings) throws DisconnectedException, ResponseTimedOutException, RobotActionException, InterruptedException {
		FanucRobotPickSettings fPickSettings = (FanucRobotPickSettings) pickSettings;		
		// write service gripper set
		writeServiceGripperSet(false, pickSettings.getGripperHead().getId(), this.getGripperBody().getGripperHead("A"), this.getGripperBody().getGripperHead("B"), FanucRobotConstants.SERVICE_GRIPPER_SERVICE_TYPE_PICK);
		// write service handling set
		int ppMode = FanucRobotConstants.SERVICE_HANDLING_PP_MODE_TEACH | FanucRobotConstants.SERVICE_HANDLING_PP_MODE_ORDER_12;
		writeServiceHandlingSet(pickSettings.isFreeAfter(), ppMode, pickSettings.getWorkPiece().getDimensions());
		// write service point set
		Coordinates pickLocation = new Coordinates(fPickSettings.getLocation());
		//pickLocation.offset(new Coordinates(0, 0, fPickSettings.getWorkPiece().getDimensions().getHeight(), 0, 0, 0));
		writeServicePointSet(fPickSettings.getWorkArea(), pickLocation, fPickSettings.getSmoothPoint(), fPickSettings.getWorkPiece().getDimensions(), fPickSettings.getWorkArea().getActiveClamping());
		// write command
		writeCommand(FanucRobotConstants.PERMISSIONS_COMMAND_PICK);
		// write start service
		fanucRobotCommunication.writeValue(FanucRobotConstants.COMMAND_START_SERVICE, FanucRobotConstants.RESPONSE_START_SERVICE, WRITE_VALUES_TIMEOUT, "1");
		// we now wait for the robot to indicate he moved to its location
		boolean waitForTeachingNeeded = waitForStatus(FanucRobotConstants.STATUS_AWAITING_TEACHING, PICK_TEACH_TIMEOUT);
		pickSettings.getStep().getProcessFlow().processProcessFlowEvent(new ActiveStepChangedEvent(pickSettings.getStep().getProcessFlow(), pickSettings.getStep(), ActiveStepChangedEvent.TEACHING_NEEDED));
		if (!waitForTeachingNeeded) {
			throw new RobotActionException(this, EXCEPTION_MOVE_TO_PICK_POSITION_TIMEOUT);
		} else {
			boolean waitingForTeachingFinished = waitForStatus(FanucRobotConstants.STATUS_TEACHING_FINISHED, PICK_TEACH_TIMEOUT);
			pickSettings.getStep().getProcessFlow().processProcessFlowEvent(new ActiveStepChangedEvent(pickSettings.getStep().getProcessFlow(), pickSettings.getStep(), ActiveStepChangedEvent.TEACHING_FINISHED));
			if (!waitingForTeachingFinished) {
				throw new RobotActionException(this, EXCEPTION_TEACHING_TIMEOUT);
			} else {
				boolean waitingForPickFinished = waitForStatus(FanucRobotConstants.STATUS_PICK_RELEASE_REQUEST, MOVE_TO_LOCATION_TIMEOUT);
				if (waitingForPickFinished) {
					return;
				} else {
					throw new RobotActionException(this, EXCEPTION_AFTER_TEACHING_TIMEOUT);
				}
			}
		}
	}

	@Override
	public void initiateTeachedPut(RobotPutSettings putSettings) throws DisconnectedException, ResponseTimedOutException, RobotActionException, InterruptedException {
		FanucRobotPutSettings fPutSettings = (FanucRobotPutSettings) putSettings;
		// write service gripper set
		writeServiceGripperSet(false, putSettings.getGripperHead().getId(), this.getGripperBody().getGripperHead("A"), this.getGripperBody().getGripperHead("B"), FanucRobotConstants.SERVICE_GRIPPER_SERVICE_TYPE_PUT);
		// write service handling set
		int ppMode = FanucRobotConstants.SERVICE_HANDLING_PP_MODE_TEACH | FanucRobotConstants.SERVICE_HANDLING_PP_MODE_ORDER_12;
		writeServiceHandlingSet(putSettings.isFreeAfter(), ppMode, fPutSettings.getGripperHead().getGripper().getWorkPiece().getDimensions());
		// write service point set
		if (fPutSettings.getGripperHead().getGripper().getWorkPiece() == null) {
			throw new IllegalStateException("When executing put, the gripper should contain a workpiece");
		}
		writeServicePointSet(fPutSettings.getWorkArea(), fPutSettings.getLocation(), fPutSettings.getSmoothPoint(), fPutSettings.getGripperHead().getGripper().getWorkPiece().getDimensions(), fPutSettings.getWorkArea().getActiveClamping());
		// write command
		writeCommand(FanucRobotConstants.PERMISSIONS_COMMAND_PUT);
		fanucRobotCommunication.writeValue(FanucRobotConstants.COMMAND_START_SERVICE, FanucRobotConstants.RESPONSE_START_SERVICE, WRITE_VALUES_TIMEOUT, "1");
		boolean waitingForTeachingNeeded = waitForStatus(FanucRobotConstants.STATUS_AWAITING_TEACHING, PUT_TEACH_TIMEOUT);
		putSettings.getStep().getProcessFlow().processProcessFlowEvent(new ActiveStepChangedEvent(putSettings.getStep().getProcessFlow(), putSettings.getStep(), ActiveStepChangedEvent.TEACHING_NEEDED));
		if (!waitingForTeachingNeeded) {
			logger.info("Troubles");
			throw new RobotActionException(this, EXCEPTION_MOVE_TO_PUT_POSITION_TIMEOUT);
		} else {
			boolean waitingForRelease = waitForStatus(FanucRobotConstants.STATUS_TEACHING_FINISHED, PUT_TEACH_TIMEOUT);
			putSettings.getStep().getProcessFlow().processProcessFlowEvent(new ActiveStepChangedEvent(putSettings.getStep().getProcessFlow(), putSettings.getStep(), ActiveStepChangedEvent.TEACHING_FINISHED));
			if (!waitingForRelease) {
				logger.info("Troubles!");
				throw new RobotActionException(this, EXCEPTION_TEACHING_TIMEOUT);
			}
		}
	}

	@Override
	public void finalizeTeachedPick(RobotPickSettings pickSettings) throws DisconnectedException, ResponseTimedOutException, RobotActionException, InterruptedException {
		finalizePick(pickSettings);
	}

	@Override
	public void finalizeTeachedPut(RobotPutSettings putSettings) throws DisconnectedException, ResponseTimedOutException, RobotActionException, InterruptedException {
		finalizePut(putSettings);
	}
	

	@Override
	public void moveToAndWait(RobotPutSettings putSettings, boolean withPiece) throws DisconnectedException, ResponseTimedOutException, RobotActionException, InterruptedException {
		FanucRobotPutSettings fPutSettings = (FanucRobotPutSettings) putSettings;
		// write service gripper set
		writeServiceGripperSet(false, putSettings.getGripperHead().getId(), this.getGripperBody().getGripperHead("A"), this.getGripperBody().getGripperHead("B"), FanucRobotConstants.SERVICE_GRIPPER_SERVICE_TYPE_MOVE_WAIT);
		// write service handling set
		int ppMode = FanucRobotConstants.SERVICE_HANDLING_PP_MODE_ORDER_12;
		if (withPiece) {
			ppMode = ppMode | FanucRobotConstants.SERVICE_HANDLING_PP_MODE_PIECE;
		}
		writeServiceHandlingSet(putSettings.isFreeAfter(), ppMode, fPutSettings.getGripperHead().getGripper().getWorkPiece().getDimensions());
		// write service point set
		if (fPutSettings.getGripperHead().getGripper().getWorkPiece() == null) {
			throw new IllegalStateException("When executing move-to-and-wait, the gripper should contain a workpiece");
		}
		writeServicePointSet(fPutSettings.getWorkArea(), fPutSettings.getLocation(), fPutSettings.getSmoothPoint(), fPutSettings.getGripperHead().getGripper().getWorkPiece().getDimensions(), fPutSettings.getWorkArea().getActiveClamping());
		// write command
		writeCommand(FanucRobotConstants.PERMISSIONS_COMMAND_MOVEWAIT);
		fanucRobotCommunication.writeValue(FanucRobotConstants.COMMAND_START_SERVICE, FanucRobotConstants.RESPONSE_START_SERVICE, WRITE_VALUES_TIMEOUT, "1");
		boolean waitingForLocation = waitForStatus(FanucRobotConstants.STATUS_WAITING_AFTER_MOVE, MOVE_TO_LOCATION_TIMEOUT);
		if (!waitingForLocation) {
			throw new RobotActionException(this, EXCEPTION_MOVE_TO_POSITION_TIMEOUT);
		}
	}

	@Override
	public void teachedMoveToAndWait(RobotPutSettings putSettings, boolean withPiece) throws DisconnectedException, ResponseTimedOutException, RobotActionException, InterruptedException {
		FanucRobotPutSettings fPutSettings = (FanucRobotPutSettings) putSettings;
		// write service gripper set
		writeServiceGripperSet(false, putSettings.getGripperHead().getId(), this.getGripperBody().getGripperHead("A"), this.getGripperBody().getGripperHead("B"), FanucRobotConstants.SERVICE_GRIPPER_SERVICE_TYPE_MOVE_WAIT);
		// write service handling set
		int ppMode = FanucRobotConstants.SERVICE_HANDLING_PP_MODE_TEACH | FanucRobotConstants.SERVICE_HANDLING_PP_MODE_ORDER_12;
		if (withPiece) {
			ppMode = ppMode | FanucRobotConstants.SERVICE_HANDLING_PP_MODE_PIECE;
		}
		writeServiceHandlingSet(putSettings.isFreeAfter(), ppMode, fPutSettings.getGripperHead().getGripper().getWorkPiece().getDimensions());
		// write service point set
		if (fPutSettings.getGripperHead().getGripper().getWorkPiece() == null) {
			throw new IllegalStateException("When executing put, the gripper should contain a workpiece");
		}
		writeServicePointSet(fPutSettings.getWorkArea(), fPutSettings.getLocation(), fPutSettings.getSmoothPoint(), fPutSettings.getGripperHead().getGripper().getWorkPiece().getDimensions(), fPutSettings.getWorkArea().getActiveClamping());
		// write command
		writeCommand(FanucRobotConstants.PERMISSIONS_COMMAND_MOVEWAIT);
		fanucRobotCommunication.writeValue(FanucRobotConstants.COMMAND_START_SERVICE, FanucRobotConstants.RESPONSE_START_SERVICE, WRITE_VALUES_TIMEOUT, "1");
		boolean waitingForTeachingNeeded = waitForStatus(FanucRobotConstants.STATUS_AWAITING_TEACHING, MOVE_TO_LOCATION_TIMEOUT);
		putSettings.getStep().getProcessFlow().processProcessFlowEvent(new ActiveStepChangedEvent(putSettings.getStep().getProcessFlow(), putSettings.getStep(), ActiveStepChangedEvent.TEACHING_NEEDED));
		if (!waitingForTeachingNeeded) {
			throw new RobotActionException(this, EXCEPTION_MOVE_TO_POSITION_TIMEOUT);
		} else {
			boolean waitingForRelease = waitForStatus(FanucRobotConstants.STATUS_TEACHING_FINISHED, PUT_TEACH_TIMEOUT);
			putSettings.getStep().getProcessFlow().processProcessFlowEvent(new ActiveStepChangedEvent(putSettings.getStep().getProcessFlow(), putSettings.getStep(), ActiveStepChangedEvent.TEACHING_FINISHED));
			if (!waitingForRelease) {
				logger.info("Troubles!");
				throw new RobotActionException(this, EXCEPTION_TEACHING_TIMEOUT);
			} else {
				boolean waitingForLocation = waitForStatus(FanucRobotConstants.STATUS_WAITING_AFTER_MOVE, MOVE_TO_LOCATION_TIMEOUT);
				if (!waitingForLocation) {
					logger.info("Troubles!");
					throw new RobotActionException(this, EXCEPTION_AFTER_TEACHING_TIMEOUT);
				}
			}
		}
	}

	@Override
	public void teachedMoveNoWait(RobotPutSettings putSettings, boolean withPiece) throws DisconnectedException, ResponseTimedOutException, RobotActionException, InterruptedException {
		FanucRobotPutSettings fPutSettings = (FanucRobotPutSettings) putSettings;
		// write service gripper set
		writeServiceGripperSet(false, putSettings.getGripperHead().getId(), this.getGripperBody().getGripperHead("A"), this.getGripperBody().getGripperHead("B"), FanucRobotConstants.SERVICE_GRIPPER_SERVICE_TYPE_MOVE_WAIT);
		// write service handling set
		int ppMode = FanucRobotConstants.SERVICE_HANDLING_PP_MODE_TEACH | FanucRobotConstants.SERVICE_HANDLING_PP_MODE_ORDER_12 | FanucRobotConstants.SERVICE_HANDLING_PP_MODE_NO_WAIT;
		if (withPiece) {
			ppMode = ppMode | FanucRobotConstants.SERVICE_HANDLING_PP_MODE_PIECE;
		}
		writeServiceHandlingSet(putSettings.isFreeAfter(), ppMode, fPutSettings.getGripperHead().getGripper().getWorkPiece().getDimensions());
		// write service point set
		if (fPutSettings.getGripperHead().getGripper().getWorkPiece() == null) {
			throw new IllegalStateException("When executing put, the gripper should contain a workpiece");
		}
		writeServicePointSet(fPutSettings.getWorkArea(), fPutSettings.getLocation(), fPutSettings.getSmoothPoint(), fPutSettings.getGripperHead().getGripper().getWorkPiece().getDimensions(), fPutSettings.getWorkArea().getActiveClamping());
		// write command
		writeCommand(FanucRobotConstants.PERMISSIONS_COMMAND_MOVEWAIT);
		fanucRobotCommunication.writeValue(FanucRobotConstants.COMMAND_START_SERVICE, FanucRobotConstants.RESPONSE_START_SERVICE, WRITE_VALUES_TIMEOUT, "1");
		boolean waitingForTeachingNeeded = waitForStatus(FanucRobotConstants.STATUS_AWAITING_TEACHING, MOVE_TO_LOCATION_TIMEOUT);
		putSettings.getStep().getProcessFlow().processProcessFlowEvent(new ActiveStepChangedEvent(putSettings.getStep().getProcessFlow(), putSettings.getStep(), ActiveStepChangedEvent.TEACHING_NEEDED));
		if (!waitingForTeachingNeeded) {
			throw new RobotActionException(this, EXCEPTION_MOVE_TO_POSITION_TIMEOUT);
		} else {
			boolean waitingForRelease = waitForStatus(FanucRobotConstants.STATUS_TEACHING_FINISHED, PUT_TEACH_TIMEOUT);
			putSettings.getStep().getProcessFlow().processProcessFlowEvent(new ActiveStepChangedEvent(putSettings.getStep().getProcessFlow(), putSettings.getStep(), ActiveStepChangedEvent.TEACHING_FINISHED));
			if (!waitingForRelease) {
				throw new RobotActionException(this, EXCEPTION_TEACHING_TIMEOUT);
			} else {
				boolean waitingForLocation = waitForStatus(FanucRobotConstants.STATUS_WAITING_AFTER_MOVE, MOVE_TO_LOCATION_TIMEOUT);
				if (!waitingForLocation) {
					throw new RobotActionException(this, EXCEPTION_AFTER_TEACHING_TIMEOUT);
				}
			}
		}
	}
	
	@Override
	public void moveAway() throws DisconnectedException, ResponseTimedOutException, RobotActionException, InterruptedException {
		writeCommand(FanucRobotConstants.PERMISSIONS_COMMAND_MOVEWAIT_CONTINUE);
		boolean waitingForPickFinished = waitForStatus(FanucRobotConstants.STATUS_MOVEWAIT_FINISHED, MOVE_FINISH_TIMEOUT);
		if (waitingForPickFinished) {
			return;
		} else {
			throw new RobotActionException(this, EXCEPTION_MOVE_AWAY_TIMEOUT);
		}
	}

	@Override
	public void teachedMoveAway() throws DisconnectedException, ResponseTimedOutException, RobotActionException, InterruptedException {
		throw new IllegalStateException("Why would you want to do this?");
	}

	private void writeServiceGripperSet(boolean jawChange, String headId, GripperHead gHeadA, GripperHead gHeadB, int serviceType) throws DisconnectedException, ResponseTimedOutException {
		List<String> values = new ArrayList<String>();
		boolean a = false;
		if (headId.equals("A")) {
			a = true;
		} else if (headId.equals("B")) {
			a = false;
		} else {
			throw new IllegalArgumentException("Gripper head id should be 'A' or 'B'.");
		}
		// service type ; main grip id ; sub a grip id ; sub b grip id ; grip type ; sub a height ; sub b height ; exchange jaws ; inner/outer gripper type ;
		values.add("" + serviceType);
		values.add("0");
		values.add("1");
		values.add("2");
		if (a) {
			values.add("2");
		} else {
			values.add("3");
		}
		values.add("" + (int) Math.floor(gHeadA.getGripper().getHeight()));
		values.add("" + (int) Math.floor(gHeadB.getGripper().getHeight()));
		// changing jaws will not be necessary
		if (jawChange) {
			values.add("1");
		} else {
			values.add("0");
		}
		// outer gripper type will be used
		values.add("0");
		logger.debug("wrote service gripper: " + values);
		fanucRobotCommunication.writeValues(FanucRobotConstants.COMMAND_WRITE_SERVICE_GRIPPER, FanucRobotConstants.RESPONSE_WRITE_SERVICE_GRIPPER, WRITE_VALUES_TIMEOUT, values);
	}
	
	private void writeServiceHandlingSet(boolean freeAfterService, int serviceHandlingPPMode, WorkPieceDimensions dimensions) throws DisconnectedException, ResponseTimedOutException {
		List<String> values = new ArrayList<String>();
		// free after this service ; WP thickness ;  WP Z grip ; grip Z face till front ; dx correction P1 ; dy correction P1 ; dx correction P2 ; dy correction P2 ; dW correction ;
		//    dP correction ; robot speed ; payload 1 ; payload 2 ; soft float range ; soft float force ; PP mode ; bar move distance
		if (freeAfterService) {
			values.add("1");
		} else {
			values.add("0");
		}
		values.add("" + dimensions.getHeight());
		values.add("0");
		values.add("0");
		values.add("0");
		values.add("0");
		values.add("0");
		values.add("0");
		values.add("0");
		values.add("0");
		if ((getSpeed() < 10) || (getSpeed() > 100)) {
			setSpeed(50);
		}
		values.add(getSpeed() + ""); // robot speed is set to 50 for now! 
		values.add("0");
		values.add("0");
		values.add("0");
		values.add("0");
		values.add("" + serviceHandlingPPMode);
		values.add("0");
		logger.debug("wrote service handling: " + values);
		fanucRobotCommunication.writeValues(FanucRobotConstants.COMMAND_WRITE_SERVICE_HANDLING, FanucRobotConstants.RESPONSE_WRITE_SERVICE_HANDLING, WRITE_VALUES_TIMEOUT, values);
	}
	
	private void writeServicePointSet(WorkArea workArea, Coordinates location, Coordinates smoothPoint, WorkPieceDimensions dimensions, Clamping clamping) throws DisconnectedException, ResponseTimedOutException {
		List<String> values = new ArrayList<String>();
		// user frame location ; x offset ; y offset ; z offset ; r offset ; z-safe plane offset ; safety add z ; smooth x ; smooth y ; smooth z ; tangent to/from ; xyz allowed ;
		// clamp height ; bar break iterations ; bar break main axis ; bar break angle ; bar move length
		int userFrameId = workArea.getUserFrame().getIdNumber();
		//UF: stacker = 1; Machine = 3
		if ((userFrameId != 1) && (userFrameId != 3)) {
			throw new IllegalArgumentException("Illegal Userframe id");
		} else {
			values.add("" + userFrameId);
		}
		//TODO check the offsets, for now we take 0
		DecimalFormat df = new DecimalFormat("#.##");
		df.setDecimalSeparatorAlwaysShown(false);
		DecimalFormatSymbols custom = new DecimalFormatSymbols();
		custom.setDecimalSeparator('.');
		df.setDecimalFormatSymbols(custom);
		values.add("" + df.format(location.getX()));
		values.add("" + df.format(location.getY()));
		values.add("" + df.format(location.getZ()));
		values.add("" + df.format(location.getR()));
		if (location.getZ() > 0) {
			values.add("" + (df.format(dimensions.getHeight() + location.getZ())));
		} else {
			values.add("" + df.format(dimensions.getHeight()));
		}
		// TODO we take 10 as safety add z for now
		if (smoothPoint.getZ() > 20) {
			values.add("" + df.format(smoothPoint.getZ()));
		} else {
			values.add("20");
		}
		values.add("" + df.format(smoothPoint.getX()));
		values.add("" + df.format(smoothPoint.getY()));
		values.add("" + df.format(smoothPoint.getZ()));
		// we take 1 as tangent
		values.add("1");
		// we take xyz allowed as xyz for stacker and xy for machine
		if (userFrameId == 1) {
			values.add("" + FanucRobotConstants.SERVICE_POINT_XYZ_ALLOWED_XYZ);
		} else if (userFrameId == 3) {
			values.add("" + FanucRobotConstants.SERVICE_POINT_XYZ_ALLOWED_XYZ);
			//values.add("" + FanucRobotConstants.SERVICE_POINT_XYZ_ALLOWED_XYZ);
		} else {
			throw new IllegalStateException("Should not be here! Illegal Userframe id");
		}
		if (clamping != null) {
			values.add("" + df.format((clamping.getHeight() + clamping.getRelativePosition().getZ())));
		} else {
			throw new IllegalArgumentException("Invalid clamp height!");
		}
		values.add("0");
		values.add("0");
		values.add("0");
		values.add("0");
		logger.debug("wrote service point: " + values);
		fanucRobotCommunication.writeValues(FanucRobotConstants.COMMAND_WRITE_SERVICE_POINT, FanucRobotConstants.RESPONSE_WRITE_SERVICE_POINT, WRITE_VALUES_TIMEOUT, values);
	}

	private void writeCommand(int permission) throws DisconnectedException, ResponseTimedOutException {
		// permission
		List<String> values = new ArrayList<String>();
		values.add("" + permission);
		fanucRobotCommunication.writeValues(FanucRobotConstants.COMMAND_SET_PERMISSIONS, FanucRobotConstants.RESPONSE_SET_PERMISSIONS, WRITE_VALUES_TIMEOUT, values);
	}
	
	@Override
	public synchronized void moveToHome() throws DisconnectedException, ResponseTimedOutException, RobotActionException {
		//we now use a speed of 50%
		if ((getSpeed() < 10) || (getSpeed() > 100)) {
			setSpeed(50);
		}
		fanucRobotCommunication.writeValue(FanucRobotConstants.COMMAND_TO_HOME, FanucRobotConstants.RESPONSE_TO_HOME, WRITE_VALUES_TIMEOUT, "" + getSpeed());
		//TODO there's no way of knowing the robot is in its home point, so for now, we just leave him there
	}

	@Override
	public synchronized void moveToChangePoint() throws DisconnectedException, ResponseTimedOutException, RobotActionException {
		if ((getSpeed() < 10) || (getSpeed() > 100)) {
			setSpeed(50);
		}
		fanucRobotCommunication.writeValue(FanucRobotConstants.COMMAND_TO_JAW_CHANGE, FanucRobotConstants.RESPONSE_TO_JAW_CHANGE, WRITE_VALUES_TIMEOUT, "" + getSpeed());
	}

	@Override
	public RobotPickSettings getDefaultPickSettings() {
		return new FanucRobotPickSettings();
	}

	@Override
	public RobotPutSettings getDefaultPutSettings() {
		return new FanucRobotPutSettings();
	}

	@Override
	public void loadRobotSettings(RobotSettings robotSettings) {
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

	@Override
	public RobotSettings getRobotSettings() {
		Map<GripperHead, Gripper> grippers = new HashMap<GripperHead, Gripper>();
		for(GripperHead head : getGripperBody().getGripperHeads()) {
			grippers.put(head, head.getGripper());
		}
		return new RobotSettings(getGripperBody(), grippers);
	}

	@Override
	public boolean validatePickSettings(RobotPickSettings pickSettings) {
		FanucRobotPickSettings fanucPickSettings = (FanucRobotPickSettings) pickSettings;
		if ( 
				(fanucPickSettings.getGripperHead() != null) &&
				(getGripperBody().getActiveGripper(fanucPickSettings.getGripperHead()).equals(fanucPickSettings.getGripperHead().getGripper())) &&
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
	public boolean validatePutSettings(RobotPutSettings putSettings) {
		FanucRobotPutSettings fanucPutSettings = (FanucRobotPutSettings) putSettings;
		if ( 
				(fanucPutSettings.getGripperHead() != null) &&
				(getGripperBody().getActiveGripper(fanucPutSettings.getGripperHead()).equals(fanucPutSettings.getGripperHead().getGripper())) &&
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
		return fanucRobotCommunication.isConnected();
	}

	@Override
	public void recalculateTCPs() throws DisconnectedException, ResponseTimedOutException {
		writeServiceGripperSet(false, this.getGripperBody().getGripperHead("A").getId(), this.getGripperBody().getGripperHead("A"), this.getGripperBody().getGripperHead("B"), FanucRobotConstants.SERVICE_GRIPPER_SERVICE_TYPE_JAW_CHANGE);
		fanucRobotCommunication.writeCommand(FanucRobotConstants.COMMAND_RECALC_TCPS, FanucRobotConstants.RESPONSE_RECALC_TCPS, WRITE_VALUES_TIMEOUT);
	}

}
