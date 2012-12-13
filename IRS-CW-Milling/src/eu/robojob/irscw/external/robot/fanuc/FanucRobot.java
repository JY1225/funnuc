package eu.robojob.irscw.external.robot.fanuc;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.robojob.irscw.external.communication.AbstractCommunicationException;
import eu.robojob.irscw.external.communication.socket.SocketConnection;
import eu.robojob.irscw.external.communication.socket.SocketDisconnectedException;
import eu.robojob.irscw.external.communication.socket.SocketResponseTimedOutException;
import eu.robojob.irscw.external.device.Clamping;
import eu.robojob.irscw.external.device.WorkArea;
import eu.robojob.irscw.external.robot.AbstractRobot;
import eu.robojob.irscw.external.robot.GripperBody;
import eu.robojob.irscw.external.robot.GripperHead;
import eu.robojob.irscw.external.robot.RobotActionException;
import eu.robojob.irscw.external.robot.RobotAlarm;
import eu.robojob.irscw.external.robot.RobotConstants;
import eu.robojob.irscw.external.robot.RobotMonitoringThread;
import eu.robojob.irscw.external.robot.RobotPickSettings;
import eu.robojob.irscw.external.robot.RobotPutSettings;
import eu.robojob.irscw.external.robot.RobotSocketCommunication;
import eu.robojob.irscw.positioning.Coordinates;
import eu.robojob.irscw.process.event.ActiveStepChangedEvent;
import eu.robojob.irscw.threading.ThreadManager;
import eu.robojob.irscw.workpiece.WorkPieceDimensions;

public class FanucRobot extends AbstractRobot {

	private RobotSocketCommunication fanucRobotCommunication;
	
	private static final int WRITE_VALUES_TIMEOUT = 5000;
	private static final int MOVE_TO_LOCATION_TIMEOUT = 3 * 60 * 1000;
	private static final int MOVE_FINISH_TIMEOUT = 3 * 60 * 1000;
	private static final int ASK_POSITION_TIMEOUT = 50000;
	private static final int PICK_TEACH_TIMEOUT = 10 * 60 * 1000;
	private static final int PUT_TEACH_TIMEOUT = 10 * 60 * 1000;
	private static final int ASK_STATUS_TIMEOUT = 5 * 1000;
	
	private static final int WRITE_REGISTER_TIMEOUT = 5000;
	private static final int PRAGE_TIMEOUT = 2 * 60 * 1000;
	
	private static final List<Integer> VALID_USERFRAMES = Arrays.asList(1, 3);
	
	private static final String HEAD_A_ID = "A";
	private static final String HEAD_B_ID = "B";
	
	private DecimalFormat df;
		
	private static Logger logger = LogManager.getLogger(FanucRobot.class.getName());
	
	private static final String EXCEPTION_PRAGE_TIMEOUT = "FanucRobot.prageTimeout";
	private static final String EXCEPTION_MOVE_TO_PICK_POSITION_TIMEOUT = "FanucRobot.moveToPickPositionTimeout";
	private static final String EXCEPTION_MOVE_TO_PUT_POSITION_TIMEOUT = "FanucRobot.moveToPutPositionTimeout";
	private static final String EXCEPTION_PUT_CLAMP_TIMEOUT = "FanucRobot.putClampTimeout";
	private static final String EXCEPTION_PICK_UNCLAMP_TIMEOUT = "FanucRobot.pickUnclampTimeout";
	private static final String EXCEPTION_TEACHING_TIMEOUT = "FanucRobot.teachingTimeout";
	private static final String EXCEPTION_AFTER_TEACHING_TIMEOUT = "FanucRobot.afterTeachingTimeout";
	private static final String EXCEPTION_MOVE_TO_POSITION_TIMEOUT = "FanucRobot.moveToPositionTimeout";
	private static final String EXCEPTION_MOVE_AWAY_TIMEOUT = "FanucRobot.moveAwayTimeout";
	
	public FanucRobot(final String id, final Set<GripperBody> gripperBodies, final GripperBody gripperBody, final SocketConnection socketConnection) {
		super(id, gripperBodies, gripperBody);
		this.fanucRobotCommunication = new RobotSocketCommunication(socketConnection, this);
		RobotMonitoringThread monitoringThread = new RobotMonitoringThread(this);
		ThreadManager.getInstance().submit(monitoringThread);
		df = new DecimalFormat("#.##");
		df.setDecimalSeparatorAlwaysShown(false);
		DecimalFormatSymbols custom = new DecimalFormatSymbols();
		custom.setDecimalSeparator('.');
		df.setDecimalFormatSymbols(custom);
	}
	
	public FanucRobot(final String id, final SocketConnection socketConnection) {
		this(id, null, null, socketConnection);
	}
	
	@Override
	public void updateStatusZRestAndAlarms() throws AbstractCommunicationException, InterruptedException {
		List<String> values = fanucRobotCommunication.readValues(RobotConstants.COMMAND_ASK_STATUS, RobotConstants.RESPONSE_ASK_STATUS, ASK_STATUS_TIMEOUT);
		int errorId = Integer.parseInt(values.get(0));
		int controllerValue = Integer.parseInt(values.get(1));
		int controllerString = Integer.parseInt(values.get(2));
		double zRest = Float.parseFloat(values.get(3));
		setAlarms(RobotAlarm.parseFanucRobotAlarms(errorId, controllerValue));
		setStatus(controllerString);
		setZRest(zRest);
	}

	@Override
	public void sendSpeed(final int speedPercentage) throws SocketDisconnectedException, SocketResponseTimedOutException, InterruptedException {
		fanucRobotCommunication.writeValue(RobotConstants.COMMAND_SET_SPEED, RobotConstants.RESPONSE_SET_SPEED, WRITE_VALUES_TIMEOUT, speedPercentage + "");
	}
	
	@Override
	public Coordinates getPosition() throws SocketDisconnectedException, SocketResponseTimedOutException, RobotActionException, InterruptedException {
		Coordinates position = fanucRobotCommunication.getPosition(ASK_POSITION_TIMEOUT);
		return position;
	}
	
	@Override
	public void continueProgram() throws SocketDisconnectedException, SocketResponseTimedOutException, InterruptedException {
		fanucRobotCommunication.writeCommand(RobotConstants.COMMAND_CONTINUE, RobotConstants.RESPONSE_CONTINUE, WRITE_VALUES_TIMEOUT);
	}
	
	@Override
	public void abort() throws SocketDisconnectedException, SocketResponseTimedOutException, InterruptedException {
		fanucRobotCommunication.writeCommand(RobotConstants.COMMAND_ABORT, RobotConstants.RESPONSE_ABORT, WRITE_VALUES_TIMEOUT);
	}
	
	public synchronized void disconnect() {
		fanucRobotCommunication.disconnect();
	}
	
	@Override
	public void restartProgram() throws SocketDisconnectedException, SocketResponseTimedOutException, InterruptedException {
		fanucRobotCommunication.writeCommand(RobotConstants.COMMAND_RESTART_PROGRAM, RobotConstants.RESPONSE_RESTART_PROGRAM, WRITE_VALUES_TIMEOUT);
	}

	@Override
	public void reset() throws SocketDisconnectedException, SocketResponseTimedOutException, InterruptedException {
		fanucRobotCommunication.writeCommand(RobotConstants.COMMAND_RESET, RobotConstants.RESPONSE_RESET, WRITE_VALUES_TIMEOUT);
	}
	
	@Override
	public void writeRegister(final int registerNr, final String value) throws SocketDisconnectedException, SocketResponseTimedOutException, RobotActionException, InterruptedException {
		fanucRobotCommunication.writeValue(RobotConstants.COMMAND_WRITE_REGISTER, RobotConstants.RESPONSE_WRITE_REGISTER, WRITE_REGISTER_TIMEOUT, "" + registerNr);
	}

	@Override
	public void doPrage() throws SocketDisconnectedException, SocketResponseTimedOutException, RobotActionException, InterruptedException {
		writeCommand(RobotConstants.PERMISSIONS_COMMAND_DO_PRAGE);
		boolean prageSucceeded = waitForStatus(RobotConstants.STATUS_PRAGE_FINISHED, PRAGE_TIMEOUT);
		if (!prageSucceeded) {
			throw new RobotActionException(this, EXCEPTION_PRAGE_TIMEOUT);
		}
	}

	@Override
	public void initiatePick(final RobotPickSettings pickSettings) throws SocketDisconnectedException, SocketResponseTimedOutException, RobotActionException, InterruptedException {
		FanucRobotPickSettings fPickSettings = (FanucRobotPickSettings) pickSettings;		
		writeServiceGripperSet(false, pickSettings.getGripperHead().getId(), this.getGripperBody().getGripperHead(HEAD_A_ID), this.getGripperBody().getGripperHead(HEAD_B_ID), RobotConstants.SERVICE_GRIPPER_SERVICE_TYPE_PICK);
		int ppMode = RobotConstants.SERVICE_HANDLING_PP_MODE_ORDER_12;
		if (fPickSettings.isDoMachineAirblow()) {
			ppMode = ppMode | RobotConstants.SERVICE_HANDLING_PP_MODE_AIRBLOW;
		}
		writeServiceHandlingSet(pickSettings.isFreeAfter(), ppMode, pickSettings.getWorkPiece().getDimensions());
		Coordinates pickLocation = new Coordinates(fPickSettings.getLocation());
		writeServicePointSet(fPickSettings.getWorkArea(), pickLocation, fPickSettings.getSmoothPoint(), fPickSettings.getWorkPiece().getDimensions(), fPickSettings.getWorkArea().getActiveClamping());
		writeCommand(RobotConstants.PERMISSIONS_COMMAND_PICK);
		fanucRobotCommunication.writeValue(RobotConstants.COMMAND_START_SERVICE, RobotConstants.RESPONSE_START_SERVICE, WRITE_VALUES_TIMEOUT, "1");
		boolean waitingForRelease = waitForStatus(RobotConstants.STATUS_PICK_RELEASE_REQUEST, MOVE_TO_LOCATION_TIMEOUT);
		if (!waitingForRelease) {
			throw new RobotActionException(this, EXCEPTION_MOVE_TO_PICK_POSITION_TIMEOUT);
		}
	}
	
	@Override
	public void initiatePut(final RobotPutSettings putSettings) throws SocketDisconnectedException, SocketResponseTimedOutException, RobotActionException, InterruptedException {
		FanucRobotPutSettings fPutSettings = (FanucRobotPutSettings) putSettings;
		writeServiceGripperSet(false, putSettings.getGripperHead().getId(), this.getGripperBody().getGripperHead(HEAD_A_ID), this.getGripperBody().getGripperHead(HEAD_B_ID), RobotConstants.SERVICE_GRIPPER_SERVICE_TYPE_PUT);
		int ppMode = RobotConstants.SERVICE_HANDLING_PP_MODE_ORDER_12;
		if (fPutSettings.isDoMachineAirblow()) {
			ppMode = ppMode | RobotConstants.SERVICE_HANDLING_PP_MODE_AIRBLOW;
		}
		writeServiceHandlingSet(putSettings.isFreeAfter(), ppMode, fPutSettings.getGripperHead().getGripper().getWorkPiece().getDimensions());
		if (fPutSettings.getGripperHead().getGripper().getWorkPiece() == null) {
			throw new IllegalStateException(toString() + " executing put, but the gripper [" + fPutSettings.getGripperHead().getGripper() + "] should contain a workpiece.");
		}
		writeServicePointSet(fPutSettings.getWorkArea(), fPutSettings.getLocation(), fPutSettings.getSmoothPoint(), fPutSettings.getGripperHead().getGripper().getWorkPiece().getDimensions(), fPutSettings.getWorkArea().getActiveClamping());
		writeCommand(RobotConstants.PERMISSIONS_COMMAND_PUT);
		fanucRobotCommunication.writeValue(RobotConstants.COMMAND_START_SERVICE, RobotConstants.RESPONSE_START_SERVICE, WRITE_VALUES_TIMEOUT, "1");
		boolean waitingForRelease = waitForStatus(RobotConstants.STATUS_PUT_CLAMP_REQUEST, MOVE_TO_LOCATION_TIMEOUT);
		if (!waitingForRelease) {
			throw new RobotActionException(this, EXCEPTION_MOVE_TO_PUT_POSITION_TIMEOUT);
		}
	}

	@Override
	public void finalizePut(final RobotPutSettings putSettings) throws SocketDisconnectedException, SocketResponseTimedOutException, RobotActionException, InterruptedException {
		writeCommand(RobotConstants.PERMISSIONS_COMMAND_PUT_CLAMP_ACK);
		boolean waitingForPickFinished = waitForStatus(RobotConstants.STATUS_PUT_FINISHED, MOVE_FINISH_TIMEOUT);
		if (waitingForPickFinished) {
			return;
		} else {
			throw new RobotActionException(this, EXCEPTION_PUT_CLAMP_TIMEOUT);
		}
	}

	@Override
	public void finalizePick(final RobotPickSettings pickSettings) throws SocketDisconnectedException, SocketResponseTimedOutException, RobotActionException, InterruptedException {
		pickSettings.getGripperHead().getGripper().setWorkPiece(pickSettings.getWorkPiece());
		writeCommand(RobotConstants.PERMISSIONS_COMMAND_PICK_RELEASE_ACK);
		boolean waitingForPickFinished = waitForStatus(RobotConstants.STATUS_PICK_FINISHED, MOVE_FINISH_TIMEOUT);
		if (waitingForPickFinished) {
			return;
		} else {
			throw new RobotActionException(this, EXCEPTION_PICK_UNCLAMP_TIMEOUT);
		}
	}
	
	@Override
	public void initiateTeachedPick(final RobotPickSettings pickSettings) throws SocketDisconnectedException, SocketResponseTimedOutException, RobotActionException, InterruptedException {
		FanucRobotPickSettings fPickSettings = (FanucRobotPickSettings) pickSettings;		
		writeServiceGripperSet(false, pickSettings.getGripperHead().getId(), this.getGripperBody().getGripperHead(HEAD_A_ID), this.getGripperBody().getGripperHead(HEAD_B_ID), RobotConstants.SERVICE_GRIPPER_SERVICE_TYPE_PICK);
		int ppMode = RobotConstants.SERVICE_HANDLING_PP_MODE_TEACH | RobotConstants.SERVICE_HANDLING_PP_MODE_ORDER_12;
		writeServiceHandlingSet(pickSettings.isFreeAfter(), ppMode, pickSettings.getWorkPiece().getDimensions());
		Coordinates pickLocation = new Coordinates(fPickSettings.getLocation());
		writeServicePointSet(fPickSettings.getWorkArea(), pickLocation, fPickSettings.getSmoothPoint(), fPickSettings.getWorkPiece().getDimensions(), fPickSettings.getWorkArea().getActiveClamping());
		writeCommand(RobotConstants.PERMISSIONS_COMMAND_PICK);
		fanucRobotCommunication.writeValue(RobotConstants.COMMAND_START_SERVICE, RobotConstants.RESPONSE_START_SERVICE, WRITE_VALUES_TIMEOUT, "1");
		boolean waitForTeachingNeeded = waitForStatus(RobotConstants.STATUS_AWAITING_TEACHING, PICK_TEACH_TIMEOUT);
		pickSettings.getStep().getProcessFlow().processProcessFlowEvent(new ActiveStepChangedEvent(pickSettings.getStep().getProcessFlow(), pickSettings.getStep(), ActiveStepChangedEvent.TEACHING_NEEDED));
		if (!waitForTeachingNeeded) {
			throw new RobotActionException(this, EXCEPTION_MOVE_TO_PICK_POSITION_TIMEOUT);
		} else {
			boolean waitingForTeachingFinished = waitForStatus(RobotConstants.STATUS_TEACHING_FINISHED, PICK_TEACH_TIMEOUT);
			pickSettings.getStep().getProcessFlow().processProcessFlowEvent(new ActiveStepChangedEvent(pickSettings.getStep().getProcessFlow(), pickSettings.getStep(), ActiveStepChangedEvent.TEACHING_FINISHED));
			if (!waitingForTeachingFinished) {
				throw new RobotActionException(this, EXCEPTION_TEACHING_TIMEOUT);
			} else {
				boolean waitingForPickFinished = waitForStatus(RobotConstants.STATUS_PICK_RELEASE_REQUEST, MOVE_TO_LOCATION_TIMEOUT);
				if (waitingForPickFinished) {
					return;
				} else {
					throw new RobotActionException(this, EXCEPTION_AFTER_TEACHING_TIMEOUT);
				}
			}
		}
	}

	@Override
	public void initiateTeachedPut(final RobotPutSettings putSettings) throws SocketDisconnectedException, SocketResponseTimedOutException, RobotActionException, InterruptedException {
		FanucRobotPutSettings fPutSettings = (FanucRobotPutSettings) putSettings;
		writeServiceGripperSet(false, putSettings.getGripperHead().getId(), this.getGripperBody().getGripperHead(HEAD_A_ID), this.getGripperBody().getGripperHead(HEAD_B_ID), RobotConstants.SERVICE_GRIPPER_SERVICE_TYPE_PUT);
		int ppMode = RobotConstants.SERVICE_HANDLING_PP_MODE_TEACH | RobotConstants.SERVICE_HANDLING_PP_MODE_ORDER_12;
		writeServiceHandlingSet(putSettings.isFreeAfter(), ppMode, fPutSettings.getGripperHead().getGripper().getWorkPiece().getDimensions());
		if (fPutSettings.getGripperHead().getGripper().getWorkPiece() == null) {
			throw new IllegalStateException(toString() + " executing put, but the gripper [" + fPutSettings.getGripperHead().getGripper() + "] should contain a workpiece.");
		}
		writeServicePointSet(fPutSettings.getWorkArea(), fPutSettings.getLocation(), fPutSettings.getSmoothPoint(), fPutSettings.getGripperHead().getGripper().getWorkPiece().getDimensions(), fPutSettings.getWorkArea().getActiveClamping());
		writeCommand(RobotConstants.PERMISSIONS_COMMAND_PUT);
		fanucRobotCommunication.writeValue(RobotConstants.COMMAND_START_SERVICE, RobotConstants.RESPONSE_START_SERVICE, WRITE_VALUES_TIMEOUT, "1");
		boolean waitingForTeachingNeeded = waitForStatus(RobotConstants.STATUS_AWAITING_TEACHING, PUT_TEACH_TIMEOUT);
		putSettings.getStep().getProcessFlow().processProcessFlowEvent(new ActiveStepChangedEvent(putSettings.getStep().getProcessFlow(), putSettings.getStep(), ActiveStepChangedEvent.TEACHING_NEEDED));
		if (!waitingForTeachingNeeded) {
			throw new RobotActionException(this, EXCEPTION_MOVE_TO_PUT_POSITION_TIMEOUT);
		} else {
			boolean waitingForRelease = waitForStatus(RobotConstants.STATUS_TEACHING_FINISHED, PUT_TEACH_TIMEOUT);
			putSettings.getStep().getProcessFlow().processProcessFlowEvent(new ActiveStepChangedEvent(putSettings.getStep().getProcessFlow(), putSettings.getStep(), ActiveStepChangedEvent.TEACHING_FINISHED));
			if (!waitingForRelease) {
				throw new RobotActionException(this, EXCEPTION_TEACHING_TIMEOUT);
			}
		}
	}

	@Override
	public void finalizeTeachedPick(final RobotPickSettings pickSettings) throws SocketDisconnectedException, SocketResponseTimedOutException, RobotActionException, InterruptedException {
		finalizePick(pickSettings);
	}

	@Override
	public void finalizeTeachedPut(final RobotPutSettings putSettings) throws SocketDisconnectedException, SocketResponseTimedOutException, RobotActionException, InterruptedException {
		finalizePut(putSettings);
	}

	@Override
	public void moveToAndWait(final RobotPutSettings putSettings, final boolean withPiece) throws SocketDisconnectedException, SocketResponseTimedOutException, RobotActionException, InterruptedException {
		FanucRobotPutSettings fPutSettings = (FanucRobotPutSettings) putSettings;
		writeServiceGripperSet(false, putSettings.getGripperHead().getId(), this.getGripperBody().getGripperHead(HEAD_A_ID), this.getGripperBody().getGripperHead(HEAD_B_ID), RobotConstants.SERVICE_GRIPPER_SERVICE_TYPE_MOVE_WAIT);
		int ppMode = RobotConstants.SERVICE_HANDLING_PP_MODE_ORDER_12;
		if (withPiece) {
			ppMode = ppMode | RobotConstants.SERVICE_HANDLING_PP_MODE_PIECE;
			if (fPutSettings.getGripperHead().getGripper().getWorkPiece() == null) {
				throw new IllegalStateException(toString() + " executing move-and-wait with piece , but the gripper [" + fPutSettings.getGripperHead().getGripper() + "] should contain a workpiece.");
			}
		}
		writeServiceHandlingSet(putSettings.isFreeAfter(), ppMode, fPutSettings.getGripperHead().getGripper().getWorkPiece().getDimensions());
		writeServicePointSet(fPutSettings.getWorkArea(), fPutSettings.getLocation(), fPutSettings.getSmoothPoint(), fPutSettings.getGripperHead().getGripper().getWorkPiece().getDimensions(), fPutSettings.getWorkArea().getActiveClamping());
		writeCommand(RobotConstants.PERMISSIONS_COMMAND_MOVEWAIT);
		fanucRobotCommunication.writeValue(RobotConstants.COMMAND_START_SERVICE, RobotConstants.RESPONSE_START_SERVICE, WRITE_VALUES_TIMEOUT, "1");
		boolean waitingForLocation = waitForStatus(RobotConstants.STATUS_WAITING_AFTER_MOVE, MOVE_TO_LOCATION_TIMEOUT);
		if (!waitingForLocation) {
			throw new RobotActionException(this, EXCEPTION_MOVE_TO_POSITION_TIMEOUT);
		}
	}

	@Override
	public void teachedMoveToAndWait(final RobotPutSettings putSettings, final boolean withPiece) throws SocketDisconnectedException, SocketResponseTimedOutException, RobotActionException, InterruptedException {
		FanucRobotPutSettings fPutSettings = (FanucRobotPutSettings) putSettings;
		writeServiceGripperSet(false, putSettings.getGripperHead().getId(), this.getGripperBody().getGripperHead(HEAD_A_ID), this.getGripperBody().getGripperHead(HEAD_B_ID), RobotConstants.SERVICE_GRIPPER_SERVICE_TYPE_MOVE_WAIT);
		int ppMode = RobotConstants.SERVICE_HANDLING_PP_MODE_TEACH | RobotConstants.SERVICE_HANDLING_PP_MODE_ORDER_12;
		if (withPiece) {
			ppMode = ppMode | RobotConstants.SERVICE_HANDLING_PP_MODE_PIECE;
			if (fPutSettings.getGripperHead().getGripper().getWorkPiece() == null) {
				throw new IllegalStateException(toString() + " executing move-and-wait with piece, but the gripper [" + fPutSettings.getGripperHead().getGripper() + "] should contain a workpiece.");
			}
		}
		writeServiceHandlingSet(putSettings.isFreeAfter(), ppMode, fPutSettings.getGripperHead().getGripper().getWorkPiece().getDimensions());
		writeServicePointSet(fPutSettings.getWorkArea(), fPutSettings.getLocation(), fPutSettings.getSmoothPoint(), fPutSettings.getGripperHead().getGripper().getWorkPiece().getDimensions(), fPutSettings.getWorkArea().getActiveClamping());
		writeCommand(RobotConstants.PERMISSIONS_COMMAND_MOVEWAIT);
		fanucRobotCommunication.writeValue(RobotConstants.COMMAND_START_SERVICE, RobotConstants.RESPONSE_START_SERVICE, WRITE_VALUES_TIMEOUT, "1");
		boolean waitingForTeachingNeeded = waitForStatus(RobotConstants.STATUS_AWAITING_TEACHING, MOVE_TO_LOCATION_TIMEOUT);
		putSettings.getStep().getProcessFlow().processProcessFlowEvent(new ActiveStepChangedEvent(putSettings.getStep().getProcessFlow(), putSettings.getStep(), ActiveStepChangedEvent.TEACHING_NEEDED));
		if (!waitingForTeachingNeeded) {
			throw new RobotActionException(this, EXCEPTION_MOVE_TO_POSITION_TIMEOUT);
		} else {
			boolean waitingForRelease = waitForStatus(RobotConstants.STATUS_TEACHING_FINISHED, PUT_TEACH_TIMEOUT);
			putSettings.getStep().getProcessFlow().processProcessFlowEvent(new ActiveStepChangedEvent(putSettings.getStep().getProcessFlow(), putSettings.getStep(), ActiveStepChangedEvent.TEACHING_FINISHED));
			if (!waitingForRelease) {
				throw new RobotActionException(this, EXCEPTION_TEACHING_TIMEOUT);
			} else {
				boolean waitingForLocation = waitForStatus(RobotConstants.STATUS_WAITING_AFTER_MOVE, MOVE_TO_LOCATION_TIMEOUT);
				if (!waitingForLocation) {
					throw new RobotActionException(this, EXCEPTION_AFTER_TEACHING_TIMEOUT);
				}
			}
		}
	}

	@Override
	public void teachedMoveNoWait(final RobotPutSettings putSettings, final boolean withPiece) throws SocketDisconnectedException, SocketResponseTimedOutException, RobotActionException, InterruptedException {
		FanucRobotPutSettings fPutSettings = (FanucRobotPutSettings) putSettings;
		writeServiceGripperSet(false, putSettings.getGripperHead().getId(), this.getGripperBody().getGripperHead(HEAD_A_ID), this.getGripperBody().getGripperHead(HEAD_B_ID), RobotConstants.SERVICE_GRIPPER_SERVICE_TYPE_MOVE_WAIT);
		int ppMode = RobotConstants.SERVICE_HANDLING_PP_MODE_TEACH | RobotConstants.SERVICE_HANDLING_PP_MODE_ORDER_12 | RobotConstants.SERVICE_HANDLING_PP_MODE_NO_WAIT;
		if (withPiece) {
			ppMode = ppMode | RobotConstants.SERVICE_HANDLING_PP_MODE_PIECE;
			if (fPutSettings.getGripperHead().getGripper().getWorkPiece() == null) {
				throw new IllegalStateException(toString() + " executing move-and-wait with piece, but the gripper [" + fPutSettings.getGripperHead().getGripper() + "] should contain a workpiece.");
			}
		}
		writeServiceHandlingSet(putSettings.isFreeAfter(), ppMode, fPutSettings.getGripperHead().getGripper().getWorkPiece().getDimensions());
		writeServicePointSet(fPutSettings.getWorkArea(), fPutSettings.getLocation(), fPutSettings.getSmoothPoint(), fPutSettings.getGripperHead().getGripper().getWorkPiece().getDimensions(), fPutSettings.getWorkArea().getActiveClamping());
		writeCommand(RobotConstants.PERMISSIONS_COMMAND_MOVEWAIT);
		fanucRobotCommunication.writeValue(RobotConstants.COMMAND_START_SERVICE, RobotConstants.RESPONSE_START_SERVICE, WRITE_VALUES_TIMEOUT, "1");
		boolean waitingForTeachingNeeded = waitForStatus(RobotConstants.STATUS_AWAITING_TEACHING, MOVE_TO_LOCATION_TIMEOUT);
		putSettings.getStep().getProcessFlow().processProcessFlowEvent(new ActiveStepChangedEvent(putSettings.getStep().getProcessFlow(), putSettings.getStep(), ActiveStepChangedEvent.TEACHING_NEEDED));
		if (!waitingForTeachingNeeded) {
			throw new RobotActionException(this, EXCEPTION_MOVE_TO_POSITION_TIMEOUT);
		} else {
			boolean waitingForRelease = waitForStatus(RobotConstants.STATUS_TEACHING_FINISHED, PUT_TEACH_TIMEOUT);
			putSettings.getStep().getProcessFlow().processProcessFlowEvent(new ActiveStepChangedEvent(putSettings.getStep().getProcessFlow(), putSettings.getStep(), ActiveStepChangedEvent.TEACHING_FINISHED));
			if (!waitingForRelease) {
				throw new RobotActionException(this, EXCEPTION_TEACHING_TIMEOUT);
			} else {
				boolean waitingForLocation = waitForStatus(RobotConstants.STATUS_WAITING_AFTER_MOVE, MOVE_TO_LOCATION_TIMEOUT);
				if (!waitingForLocation) {
					throw new RobotActionException(this, EXCEPTION_AFTER_TEACHING_TIMEOUT);
				}
			}
		}
	}
	
	@Override
	public void moveAway() throws SocketDisconnectedException, SocketResponseTimedOutException, RobotActionException, InterruptedException {
		writeCommand(RobotConstants.PERMISSIONS_COMMAND_MOVEWAIT_CONTINUE);
		boolean waitingForPickFinished = waitForStatus(RobotConstants.STATUS_MOVEWAIT_FINISHED, MOVE_FINISH_TIMEOUT);
		if (waitingForPickFinished) {
			return;
		} else {
			throw new RobotActionException(this, EXCEPTION_MOVE_AWAY_TIMEOUT);
		}
	}

	@Override
	public void teachedMoveAway() throws SocketDisconnectedException, SocketResponseTimedOutException, RobotActionException, InterruptedException {
		throw new IllegalStateException("Why would you want to do this?");
	}

	private void writeServiceGripperSet(final boolean jawChange, final String headId, final GripperHead gHeadA, final GripperHead gHeadB, final int serviceType) throws SocketDisconnectedException, SocketResponseTimedOutException, InterruptedException {
		List<String> values = new ArrayList<String>();
		boolean a = false;
		if (headId.equals(HEAD_A_ID)) {
			a = true;
		} else if (headId.equals(HEAD_B_ID)) {
			a = false;
		} else {
			throw new IllegalArgumentException("Gripper head id should be " + HEAD_A_ID + " or " + HEAD_B_ID + ".");
		}
		// service type ; main grip id ; sub a grip id ; sub b grip id ; grip type ; sub a height ; sub b height ; exchange jaws ; inner/outer gripper type ;
		values.add("" + serviceType);	// service type
		values.add("0");				// main grip id
		values.add("1");				// sub a grip id
		values.add("2");				// sub b grip id
		if (a) {						// grip type
			values.add("2");		
		} else {
			values.add("3");
		}
		values.add("" + (int) Math.floor(gHeadA.getGripper().getHeight()));		// sub a height
		values.add("" + (int) Math.floor(gHeadB.getGripper().getHeight()));		// sub b height
		if (jawChange) {				// exchange jaws
			values.add("1");
		} else {
			values.add("0");
		}
		// outer gripper type will be used
		values.add("0");				// inner/outer gripper type
		logger.debug("Writing service gripper set: " + values);
		fanucRobotCommunication.writeValues(RobotConstants.COMMAND_WRITE_SERVICE_GRIPPER, RobotConstants.RESPONSE_WRITE_SERVICE_GRIPPER, WRITE_VALUES_TIMEOUT, values);
	}
	
	private void writeServiceHandlingSet(final boolean freeAfterService, final int serviceHandlingPPMode, final WorkPieceDimensions dimensions) throws SocketDisconnectedException, SocketResponseTimedOutException, InterruptedException {
		List<String> values = new ArrayList<String>();
		// free after this service ; WP thickness ;  WP Z grip ; grip Z face till front ; dx correction P1 ; dy correction P1 ; dx correction P2 ; dy correction P2 ; dW correction ;
		//    dP correction ; robot speed ; payload 1 ; payload 2 ; soft float range ; soft float force ; PP mode ; bar move distance
		if (freeAfterService) {				// free after this service
			values.add("1");
		} else {
			values.add("0");
		}
		values.add(df.format(dimensions.getHeight()));	// WP thickness
		values.add("0");					// WP Z grip
		values.add("0");					// grip Z face till front
		values.add("0");					// dx correction P1
		values.add("0");					// dy correction P1
		values.add("0");					// dx correction P2
		values.add("0");					// dy correction P2
		values.add("0");					// dw correction
		values.add("0");					// dp correction
		if ((getSpeed() < 10) || (getSpeed() > 100)) {
			throw new IllegalStateException("The current speed value: [" + getSpeed() + "] is illegal.");
		}
		values.add(getSpeed() + "");		// robot speed
		values.add("0");					// payload 1
		values.add("0");					// payload 2
		values.add("0");					// soft float range
		values.add("0");					// soft float force
		values.add("" + serviceHandlingPPMode);		// PP mode
		values.add("0");					// bar move distance
		logger.debug("Writing service handling set: " + values);
		fanucRobotCommunication.writeValues(RobotConstants.COMMAND_WRITE_SERVICE_HANDLING, RobotConstants.RESPONSE_WRITE_SERVICE_HANDLING, WRITE_VALUES_TIMEOUT, values);
	}
	
	private void writeServicePointSet(final WorkArea workArea, final Coordinates location, final Coordinates smoothPoint, final WorkPieceDimensions dimensions, final Clamping clamping) throws SocketDisconnectedException, SocketResponseTimedOutException, InterruptedException {
		List<String> values = new ArrayList<String>();
		// user frame location ; x offset ; y offset ; z offset ; r offset ; z-safe plane offset ; safety add z ; smooth x ; smooth y ; smooth z ; tangent to/from ; xyz allowed ;
		// clamp height ; bar break iterations ; bar break main axis ; bar break angle ; bar move length
		int userFrameId = workArea.getUserFrame().getIdNumber();
		//UF: stacker = 1; Machine = 3
		if (!VALID_USERFRAMES.contains(userFrameId)) {
			throw new IllegalArgumentException("Illegal Userframe id: " + userFrameId + " should be 1 or 3.");
		} else {
			values.add("" + userFrameId);					// user frame location
		}
		values.add(df.format(location.getX()));		// x offset
		values.add(df.format(location.getY()));		// y offset
		values.add(df.format(location.getZ()));		// z offset
		values.add(df.format(location.getR()));		// r offset							
		values.add(df.format(dimensions.getHeight() + location.getZ()));	// z safe plane offset
		if (smoothPoint.getZ() > workArea.getUserFrame().getzSafeDistance()) {	// safety add z
			values.add(df.format(smoothPoint.getZ()));
		} else {
			values.add("" + workArea.getUserFrame().getzSafeDistance());
		}
		values.add(df.format(smoothPoint.getX()));	// smooth x
		values.add(df.format(smoothPoint.getY()));	// smooth y
		values.add(df.format(smoothPoint.getZ()));	// smooth z
		values.add("1");							// tangent to/from
		//TODO review if this strategy is always safe
		values.add("" + RobotConstants.SERVICE_POINT_XYZ_ALLOWED_XYZ);	// xyz allowed
		values.add("" + df.format((clamping.getHeight() + clamping.getRelativePosition().getZ())));	// clamp height (we need to include the relative position, height is measured from z = 0)
		values.add("0");	// bar break iterations
		values.add("0");	// bar break main axis
		values.add("0");	// bar break angle
		values.add("0");	// bar move length
		logger.debug("Writing service point: " + values);
		fanucRobotCommunication.writeValues(RobotConstants.COMMAND_WRITE_SERVICE_POINT, RobotConstants.RESPONSE_WRITE_SERVICE_POINT, WRITE_VALUES_TIMEOUT, values);
	}

	private void writeCommand(final int permission) throws SocketDisconnectedException, SocketResponseTimedOutException, InterruptedException {
		List<String> values = new ArrayList<String>();
		values.add("" + permission);
		fanucRobotCommunication.writeValues(RobotConstants.COMMAND_SET_PERMISSIONS, RobotConstants.RESPONSE_SET_PERMISSIONS, WRITE_VALUES_TIMEOUT, values);
	}
	
	@Override
	public synchronized void moveToHome() throws SocketDisconnectedException, SocketResponseTimedOutException, RobotActionException, InterruptedException {
		if ((getSpeed() < 10) || (getSpeed() > 100)) {
			throw new IllegalStateException("The current speed value: [" + getSpeed() + "] is illegal.");
		}
		fanucRobotCommunication.writeValue(RobotConstants.COMMAND_TO_HOME, RobotConstants.RESPONSE_TO_HOME, WRITE_VALUES_TIMEOUT, "" + getSpeed());
		//TODO there's no way of knowing the robot is in its home point
	}

	@Override
	public synchronized void moveToChangePoint() throws SocketDisconnectedException, SocketResponseTimedOutException, RobotActionException, InterruptedException {
		if ((getSpeed() < 10) || (getSpeed() > 100)) {
			throw new IllegalStateException("The current speed value: [" + getSpeed() + "] is illegal.");
		}
		fanucRobotCommunication.writeValue(RobotConstants.COMMAND_TO_JAW_CHANGE, RobotConstants.RESPONSE_TO_JAW_CHANGE, WRITE_VALUES_TIMEOUT, "" + getSpeed());
	}

	@Override
	public boolean isConnected() {
		return fanucRobotCommunication.isConnected();
	}

	@Override
	public void recalculateTCPs() throws SocketDisconnectedException, SocketResponseTimedOutException, InterruptedException {
		writeServiceGripperSet(false, this.getGripperBody().getGripperHead(HEAD_A_ID).getId(), this.getGripperBody().getGripperHead(HEAD_A_ID), this.getGripperBody().getGripperHead(HEAD_B_ID), RobotConstants.SERVICE_GRIPPER_SERVICE_TYPE_JAW_CHANGE);
		fanucRobotCommunication.writeCommand(RobotConstants.COMMAND_RECALC_TCPS, RobotConstants.RESPONSE_RECALC_TCPS, WRITE_VALUES_TIMEOUT);
	}

	@Override
	public boolean validatePickSettings(final RobotPickSettings pickSettings) {
		if (super.validatePickSettings(pickSettings) && (pickSettings instanceof FanucRobotPickSettings)) {
			return true;
		}
		return false;
	}

	@Override
	public boolean validatePutSettings(final RobotPutSettings putSettings) {
		if (super.validatePutSettings(putSettings) && (putSettings instanceof FanucRobotPutSettings)) {
			return true;
		}
		return false;
	}
}
