package eu.robojob.millassist.external.robot.fanuc;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.robojob.millassist.external.communication.AbstractCommunicationException;
import eu.robojob.millassist.external.communication.socket.SocketConnection;
import eu.robojob.millassist.external.communication.socket.SocketDisconnectedException;
import eu.robojob.millassist.external.communication.socket.SocketResponseTimedOutException;
import eu.robojob.millassist.external.device.Clamping;
import eu.robojob.millassist.external.device.WorkArea;
import eu.robojob.millassist.external.robot.AbstractRobot;
import eu.robojob.millassist.external.robot.GripperBody;
import eu.robojob.millassist.external.robot.GripperHead;
import eu.robojob.millassist.external.robot.RobotActionException;
import eu.robojob.millassist.external.robot.RobotAlarm;
import eu.robojob.millassist.external.robot.RobotConstants;
import eu.robojob.millassist.external.robot.RobotMonitoringThread;
import eu.robojob.millassist.external.robot.RobotPickSettings;
import eu.robojob.millassist.external.robot.RobotPutSettings;
import eu.robojob.millassist.external.robot.RobotSocketCommunication;
import eu.robojob.millassist.positioning.Coordinates;
import eu.robojob.millassist.threading.ThreadManager;
import eu.robojob.millassist.workpiece.WorkPieceDimensions;

public class FanucRobot extends AbstractRobot {

	private RobotSocketCommunication fanucRobotCommunication;
	
	private static final int WRITE_VALUES_TIMEOUT = 5000;
	private static final int MOVE_TO_LOCATION_TIMEOUT = 3 * 60 * 1000;
	private static final int CLAMP_ACK_REQUEST_TIMEOUT = 10 * 1000;
	private static final int MOVE_TO_IPPOINT_TIMEOUT = 3 * 60 * 1000;
	private static final int MOVE_FINISH_TIMEOUT = 3 * 60 * 1000;
	private static final int ASK_POSITION_TIMEOUT = 50000;
	private static final int ASK_STATUS_TIMEOUT = 5 * 1000;
	private static final int TEACH_TIMEOUT = 10 * 60 * 1000;
	private static final float MAX_WORKPIECE_WEIGHT = 15;
	
	private static final int WRITE_REGISTER_TIMEOUT = 5000;
	private static final int IOACTION_TIMEOUT = 2 * 60 * 1000;
	
	private static final List<Integer> VALID_USERFRAMES = Arrays.asList(1, 3, 6);
	
	private static final String HEAD_A_ID = "A";
	private static final String HEAD_B_ID = "B";
	
	private DecimalFormat df;
		
	private static Logger logger = LogManager.getLogger(FanucRobot.class.getName());
	
	public FanucRobot(final String name, final Set<GripperBody> gripperBodies, final GripperBody gripperBody, final SocketConnection socketConnection) {
		super(name, gripperBodies, gripperBody);
		this.fanucRobotCommunication = new RobotSocketCommunication(socketConnection, this);
		RobotMonitoringThread monitoringThread = new RobotMonitoringThread(this);
		ThreadManager.submit(monitoringThread);
		df = new DecimalFormat("#.##");
		df.setDecimalSeparatorAlwaysShown(false);
		DecimalFormatSymbols custom = new DecimalFormatSymbols();
		custom.setDecimalSeparator('.');
		df.setDecimalFormatSymbols(custom);
	}
	
	public FanucRobot(final String name, final SocketConnection socketConnection) {
		this(name, null, null, socketConnection);
	}
	
	@Override
	public void updateStatusZRestAndAlarms() throws AbstractCommunicationException, InterruptedException {
		List<String> values = fanucRobotCommunication.readValues(RobotConstants.COMMAND_ASK_STATUS, RobotConstants.RESPONSE_ASK_STATUS, ASK_STATUS_TIMEOUT);
		int errorId = Integer.parseInt(values.get(0));
		int controllerValue = Integer.parseInt(values.get(1));
		int controllerString = Integer.parseInt(values.get(2));
		double zRest = Float.parseFloat(values.get(3));
		setAlarms(RobotAlarm.parseFanucRobotAlarms(errorId, controllerValue, getRobotTimeout()));
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
		setCurrentActionSettings(null);
		fanucRobotCommunication.writeCommand(RobotConstants.COMMAND_ABORT, RobotConstants.RESPONSE_ABORT, WRITE_VALUES_TIMEOUT);
		restartProgram();
	}
	
	public void disconnect() {
		fanucRobotCommunication.disconnect();
	}
	
	@Override
	public void restartProgram() throws SocketDisconnectedException, SocketResponseTimedOutException, InterruptedException {
		fanucRobotCommunication.writeCommand(RobotConstants.COMMAND_RESTART_PROGRAM, RobotConstants.RESPONSE_RESTART_PROGRAM, WRITE_VALUES_TIMEOUT);
		setCurrentActionSettings(null);
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
	public void initiatePut(final RobotPutSettings putSettings) throws AbstractCommunicationException, RobotActionException, InterruptedException {
		if (isExecutionInProgress()) {
			throw new IllegalStateException("Already performing action, with setting: " + getCurrentActionSettings());
		} else {
			setCurrentActionSettings(putSettings);
		}
		FanucRobotPutSettings fPutSettings = (FanucRobotPutSettings) putSettings;
		writeServiceGripperSet(false, putSettings.getGripperHead().getName(), this.getGripperBody().getGripperHeadByName(HEAD_A_ID), this.getGripperBody().getGripperHeadByName(HEAD_B_ID), RobotConstants.SERVICE_GRIPPER_SERVICE_TYPE_PUT);
		int ppMode = RobotConstants.SERVICE_HANDLING_PP_MODE_ORDER_12;
		if (fPutSettings.isDoMachineAirblow()) {
			ppMode = ppMode | RobotConstants.SERVICE_HANDLING_PP_MODE_AIRBLOW;
		}
		if (fPutSettings.isTeachingNeeded()) {
			ppMode = ppMode | RobotConstants.SERVICE_HANDLING_PP_MODE_TEACH;
		}
		writeServiceHandlingSet(putSettings.isFreeAfter(), ppMode, fPutSettings.getGripperHead().getGripper().getWorkPiece().getDimensions());
		if (fPutSettings.getGripperHead().getGripper().getWorkPiece() == null) {
			throw new IllegalStateException(toString() + " executing put, but the gripper [" + fPutSettings.getGripperHead().getGripper() + "] should contain a workpiece.");
		}
		Coordinates smooth = fPutSettings.getSmoothPoint();
		if (smooth == null) {
			smooth = fPutSettings.getWorkArea().getActiveClamping().getSmoothToPoint();
		}
		writeServicePointSet(fPutSettings.getWorkArea(), fPutSettings.getLocation(), fPutSettings.getStep().getRelativeTeachedOffset(), fPutSettings.getSmoothPoint(), fPutSettings.getGripperHead().getGripper().getWorkPiece().getDimensions(), fPutSettings.getWorkArea().getActiveClamping());
		fanucRobotCommunication.writeValue(RobotConstants.COMMAND_START_SERVICE, RobotConstants.RESPONSE_START_SERVICE, WRITE_VALUES_TIMEOUT, "1");
	}
	
	@Override
	public void continuePutTillAtLocation() throws AbstractCommunicationException, RobotActionException, InterruptedException {
		writeCommand(RobotConstants.PERMISSIONS_COMMAND_PUT);
		if (getCurrentActionSettings() == null) {
			throw new InterruptedException();
		}
		if (getCurrentActionSettings().isTeachingNeeded()) {
			boolean waitingForTeachingNeeded = waitForStatus(RobotConstants.STATUS_AWAITING_TEACHING, MOVE_TO_LOCATION_TIMEOUT);
			if (!waitingForTeachingNeeded) {
				setRobotTimeout(new RobotAlarm(RobotAlarm.MOVE_TO_PUT_POSITION_TIMEOUT));
				waitForStatus(RobotConstants.STATUS_AWAITING_TEACHING);
				setRobotTimeout(null);
			} 
		} else {
			boolean waitingForRelease = waitForStatus(RobotConstants.STATUS_PUT_CLAMP_REQUEST, MOVE_TO_LOCATION_TIMEOUT);
			if (!waitingForRelease) {
				setRobotTimeout(new RobotAlarm(RobotAlarm.MOVE_TO_PUT_POSITION_TIMEOUT));
				waitForStatus(RobotConstants.STATUS_PUT_CLAMP_REQUEST);
				setRobotTimeout(null);
			}
		}
	}

	@Override
	public void continuePutTillClampAck() throws AbstractCommunicationException, RobotActionException, InterruptedException {
		if (getCurrentActionSettings() == null) {
			throw new InterruptedException();
		}
		if (getCurrentActionSettings().isTeachingNeeded()) {
			boolean waitingForRelease = waitForStatus(RobotConstants.STATUS_PUT_CLAMP_REQUEST, TEACH_TIMEOUT);
			if (!waitingForRelease) {
				setRobotTimeout(new RobotAlarm(RobotAlarm.TEACH_TIMEOUT));
				waitForStatus(RobotConstants.STATUS_PUT_CLAMP_REQUEST);
				setRobotTimeout(null);
			}
		} else {
			boolean waitingForRelease = waitForStatus(RobotConstants.STATUS_PUT_CLAMP_REQUEST, CLAMP_ACK_REQUEST_TIMEOUT);
			if (!waitingForRelease) {
				setRobotTimeout(new RobotAlarm(RobotAlarm.CLAMP_ACK_REQUEST_TIMEOUT));
				waitForStatus(RobotConstants.STATUS_PUT_CLAMP_REQUEST);
				setRobotTimeout(null);
			}
		}
	}

	@Override
	public void continuePutTillIPPoint() throws AbstractCommunicationException, RobotActionException, InterruptedException {
		if (getCurrentActionSettings() == null) {
			throw new InterruptedException();
		}
		writeCommand(RobotConstants.PERMISSIONS_COMMAND_PUT_CLAMP_ACK);
		boolean waitingForPickFinished = waitForStatus(RobotConstants.STATUS_PUT_OUT_OF_MACHINE, MOVE_FINISH_TIMEOUT);
		if (!waitingForPickFinished) {
			setRobotTimeout(new RobotAlarm(RobotAlarm.MOVE_TO_IPPOINT_PUT_TIMEOUT));
			waitForStatus(RobotConstants.STATUS_PUT_OUT_OF_MACHINE);
			setRobotTimeout(null);
		}
	}

	@Override
	public void finalizePut() throws AbstractCommunicationException, RobotActionException, InterruptedException {
		if (getCurrentActionSettings() == null) {
			throw new InterruptedException();
		}
		boolean waitingForPickFinished = waitForStatus(RobotConstants.STATUS_PUT_FINISHED, MOVE_FINISH_TIMEOUT);
		if (!waitingForPickFinished) {
			setRobotTimeout(new RobotAlarm(RobotAlarm.FINALIZE_PUT_TIMEOUT));
			waitForStatus(RobotConstants.STATUS_PUT_FINISHED);
			setRobotTimeout(null);
		}
		setCurrentActionSettings(null);
	}

	@Override
	public void initiatePick(final RobotPickSettings pickSettings) throws AbstractCommunicationException, RobotActionException, InterruptedException {
		if (isExecutionInProgress()) {
			throw new IllegalStateException("Already performing action, with setting: " + getCurrentActionSettings());
		} else {
			setCurrentActionSettings(pickSettings);
		}
		RobotPickSettings fPickSettings = (RobotPickSettings) pickSettings;		
		writeServiceGripperSet(false, pickSettings.getGripperHead().getName(), this.getGripperBody().getGripperHeadByName(HEAD_A_ID), this.getGripperBody().getGripperHeadByName(HEAD_B_ID), RobotConstants.SERVICE_GRIPPER_SERVICE_TYPE_PICK);
		int ppMode = RobotConstants.SERVICE_HANDLING_PP_MODE_ORDER_12;
		if (fPickSettings.isDoMachineAirblow()) {
			ppMode = ppMode | RobotConstants.SERVICE_HANDLING_PP_MODE_AIRBLOW;
		}
		if (fPickSettings.isTeachingNeeded()) {
			ppMode = ppMode | RobotConstants.SERVICE_HANDLING_PP_MODE_TEACH;
		}
		Coordinates smooth = fPickSettings.getSmoothPoint();
		if (smooth == null) {
			smooth = fPickSettings.getWorkArea().getActiveClamping().getSmoothFromPoint();
		}
		writeServiceHandlingSet(pickSettings.isFreeAfter(), ppMode, pickSettings.getWorkPiece().getDimensions());
		Coordinates pickLocation = new Coordinates(fPickSettings.getLocation());
		writeServicePointSet(fPickSettings.getWorkArea(), pickLocation, fPickSettings.getStep().getRelativeTeachedOffset(), smooth, fPickSettings.getWorkPiece().getDimensions(), fPickSettings.getWorkArea().getActiveClamping());
		fanucRobotCommunication.writeValue(RobotConstants.COMMAND_START_SERVICE, RobotConstants.RESPONSE_START_SERVICE, WRITE_VALUES_TIMEOUT, "1");
	}
	
	@Override
	public void continuePickTillAtLocation() throws AbstractCommunicationException, RobotActionException, InterruptedException {
		writeCommand(RobotConstants.PERMISSIONS_COMMAND_PICK);
		if (getCurrentActionSettings() == null) {
			throw new InterruptedException();
		}
		if (getCurrentActionSettings().isTeachingNeeded()) {
			boolean waitingForTeachingNeeded = waitForStatus(RobotConstants.STATUS_AWAITING_TEACHING, MOVE_TO_LOCATION_TIMEOUT);
			if (!waitingForTeachingNeeded) {
				setRobotTimeout(new RobotAlarm(RobotAlarm.MOVE_TO_PICK_POSITION_TIMEOUT));
				waitForStatus(RobotConstants.STATUS_AWAITING_TEACHING);
				setRobotTimeout(null);
			} 
		} else {
			boolean waitingForRelease = waitForStatus(RobotConstants.STATUS_PICK_RELEASE_REQUEST, MOVE_TO_LOCATION_TIMEOUT);
			if (!waitingForRelease) {
				setRobotTimeout(new RobotAlarm(RobotAlarm.MOVE_TO_PICK_POSITION_TIMEOUT));
				waitForStatus(RobotConstants.STATUS_PICK_RELEASE_REQUEST);
				setRobotTimeout(null);
			}
		}
	}
	
	@Override
	public void continuePickTillUnclampAck() throws AbstractCommunicationException, RobotActionException, InterruptedException {
		if (getCurrentActionSettings() == null) {
			throw new InterruptedException();
		}
		if (getCurrentActionSettings().isTeachingNeeded()) {
			boolean waitingForRelease = waitForStatus(RobotConstants.STATUS_PICK_RELEASE_REQUEST, TEACH_TIMEOUT);
			if (!waitingForRelease) {
				setRobotTimeout(new RobotAlarm(RobotAlarm.TEACH_TIMEOUT));
				waitForStatus(RobotConstants.STATUS_PICK_RELEASE_REQUEST);
				setRobotTimeout(null);
			}
		} else {
			boolean waitingForRelease = waitForStatus(RobotConstants.STATUS_PICK_RELEASE_REQUEST, CLAMP_ACK_REQUEST_TIMEOUT);
			if (!waitingForRelease) {
				setRobotTimeout(new RobotAlarm(RobotAlarm.UNCLAMP_ACK_REQUEST_TIMEOUT));
				waitForStatus(RobotConstants.STATUS_PICK_RELEASE_REQUEST);
				setRobotTimeout(null);
			}
		}
		
	}

	@Override
	public void continuePickTillIPPoint() throws AbstractCommunicationException, RobotActionException, InterruptedException {
		if (getCurrentActionSettings() == null) {
			throw new InterruptedException();
		}
		writeCommand(RobotConstants.PERMISSIONS_COMMAND_PICK_RELEASE_ACK);
		boolean waitingForPickFinished = waitForStatus(RobotConstants.STATUS_PICK_OUT_OF_MACHINE, MOVE_TO_IPPOINT_TIMEOUT);
		if (!waitingForPickFinished) {
			setRobotTimeout(new RobotAlarm(RobotAlarm.MOVE_TO_IPPOINT_PICK_TIMEOUT));
			waitForStatus(RobotConstants.STATUS_PICK_OUT_OF_MACHINE);
			setRobotTimeout(null);
		}
	}

	@Override
	public void finalizePick() throws AbstractCommunicationException, RobotActionException, InterruptedException {
		if (getCurrentActionSettings() == null) {
			throw new InterruptedException();
		}
		boolean waitingForPickFinished = waitForStatus(RobotConstants.STATUS_PICK_FINISHED, MOVE_FINISH_TIMEOUT);
		if (!waitingForPickFinished) {
			setRobotTimeout(new RobotAlarm(RobotAlarm.FINALIZE_PICK_TIMEOUT));
			waitForStatus(RobotConstants.STATUS_PICK_FINISHED);
			setRobotTimeout(null);
		}
		setCurrentActionSettings(null);
	}
	
	@Override
	public void initiateMoveWithPiece(final RobotPutSettings putSettings) throws AbstractCommunicationException, RobotActionException, InterruptedException {
		if (isExecutionInProgress()) {
			throw new IllegalStateException("Already performing action, with setting: " + getCurrentActionSettings());
		} else {
			setCurrentActionSettings(putSettings);
		}
		FanucRobotPutSettings fPutSettings = (FanucRobotPutSettings) putSettings;
		writeServiceGripperSet(false, putSettings.getGripperHead().getName(), this.getGripperBody().getGripperHeadByName(HEAD_A_ID), this.getGripperBody().getGripperHeadByName(HEAD_B_ID), RobotConstants.SERVICE_GRIPPER_SERVICE_TYPE_MOVE_WAIT);
		int ppMode = RobotConstants.SERVICE_HANDLING_PP_MODE_ORDER_12;
		ppMode = ppMode | RobotConstants.SERVICE_HANDLING_PP_MODE_PIECE;
		if (fPutSettings.getGripperHead().getGripper().getWorkPiece() == null) {
			throw new IllegalStateException(toString() + " executing move-and-wait with piece , but the gripper [" + fPutSettings.getGripperHead().getGripper() + "] should contain a workpiece.");
		}
		if (fPutSettings.isTeachingNeeded()) {
			ppMode = ppMode | RobotConstants.SERVICE_HANDLING_PP_MODE_TEACH;
		}
		Coordinates smooth = fPutSettings.getSmoothPoint();
		if (smooth == null) {
			smooth = fPutSettings.getWorkArea().getActiveClamping().getSmoothToPoint();
		}
		writeServiceHandlingSet(putSettings.isFreeAfter(), ppMode, fPutSettings.getGripperHead().getGripper().getWorkPiece().getDimensions());
		writeServicePointSet(fPutSettings.getWorkArea(), fPutSettings.getLocation(), fPutSettings.getStep().getRelativeTeachedOffset(), smooth, fPutSettings.getGripperHead().getGripper().getWorkPiece().getDimensions(), fPutSettings.getWorkArea().getActiveClamping());
		fanucRobotCommunication.writeValue(RobotConstants.COMMAND_START_SERVICE, RobotConstants.RESPONSE_START_SERVICE, WRITE_VALUES_TIMEOUT, "1");
	}
	
	@Override
	public void initiateMoveWithPieceNoAction(final RobotPutSettings putSettings) throws AbstractCommunicationException, RobotActionException, InterruptedException {
		if (isExecutionInProgress()) {
			throw new IllegalStateException("Already performing action, with setting: " + getCurrentActionSettings());
		} else {
			setCurrentActionSettings(putSettings);
		}
		FanucRobotPutSettings fPutSettings = (FanucRobotPutSettings) putSettings;
		writeServiceGripperSet(false, putSettings.getGripperHead().getName(), this.getGripperBody().getGripperHeadByName(HEAD_A_ID), this.getGripperBody().getGripperHeadByName(HEAD_B_ID), RobotConstants.SERVICE_GRIPPER_SERVICE_TYPE_MOVE_WAIT);
		int ppMode = RobotConstants.SERVICE_HANDLING_PP_MODE_ORDER_12;
		ppMode = ppMode | RobotConstants.SERVICE_HANDLING_PP_MODE_PIECE;
		if (fPutSettings.getGripperHead().getGripper().getWorkPiece() == null) {
			throw new IllegalStateException(toString() + " executing move-and-wait with piece , but the gripper [" + fPutSettings.getGripperHead().getGripper() + "] should contain a workpiece.");
		}
		if (fPutSettings.isTeachingNeeded()) {
			ppMode = ppMode | RobotConstants.SERVICE_HANDLING_PP_MODE_TEACH;
		}
		Coordinates smooth = fPutSettings.getSmoothPoint();
		if (smooth == null) {
			smooth = fPutSettings.getWorkArea().getActiveClamping().getSmoothToPoint();
		}
		ppMode = ppMode | RobotConstants.SERVICE_HANDLING_PP_MODE_NO_WAIT;
		writeServiceHandlingSet(putSettings.isFreeAfter(), ppMode, fPutSettings.getGripperHead().getGripper().getWorkPiece().getDimensions());
		writeServicePointSet(fPutSettings.getWorkArea(), fPutSettings.getLocation(), fPutSettings.getStep().getRelativeTeachedOffset(), smooth, fPutSettings.getGripperHead().getGripper().getWorkPiece().getDimensions(), fPutSettings.getWorkArea().getActiveClamping());
		fanucRobotCommunication.writeValue(RobotConstants.COMMAND_START_SERVICE, RobotConstants.RESPONSE_START_SERVICE, WRITE_VALUES_TIMEOUT, "1");
	}
	
	@Override
	public void continueMoveTillAtLocation() throws AbstractCommunicationException, RobotActionException, InterruptedException {
		writeCommand(RobotConstants.PERMISSIONS_COMMAND_MOVEWAIT);
		if (getCurrentActionSettings() == null) {
			throw new InterruptedException();
		}
		if (getCurrentActionSettings().isTeachingNeeded()) {
			boolean waitingForTeachingNeeded = waitForStatus(RobotConstants.STATUS_AWAITING_TEACHING, MOVE_TO_LOCATION_TIMEOUT);
			if (!waitingForTeachingNeeded) {
				setRobotTimeout(new RobotAlarm(RobotAlarm.MOVE_TO_POSITION_TIMEOUT));
				waitForStatus(RobotConstants.STATUS_AWAITING_TEACHING);
				setRobotTimeout(null);
			} 
		} else {
			continueMoveTillWait();
		}
	}
	
	@Override
	public void continueMoveTillWait() throws AbstractCommunicationException, RobotActionException, InterruptedException {
		if (getCurrentActionSettings() == null) {
			throw new InterruptedException();
		}
		if (getCurrentActionSettings().isTeachingNeeded()) {
			boolean waitingForLocation = waitForStatus(RobotConstants.STATUS_WAITING_AFTER_MOVE, TEACH_TIMEOUT);
			if (!waitingForLocation) {
				setRobotTimeout(new RobotAlarm(RobotAlarm.TEACH_TIMEOUT));
				waitForStatus(RobotConstants.STATUS_WAITING_AFTER_MOVE);
				setRobotTimeout(null);
			}
		} else {
			boolean waitingForLocation = waitForStatus(RobotConstants.STATUS_WAITING_AFTER_MOVE, MOVE_TO_LOCATION_TIMEOUT);
			if (!waitingForLocation) {
				setRobotTimeout(new RobotAlarm(RobotAlarm.MOVE_TO_POSITION_TIMEOUT));
				waitForStatus(RobotConstants.STATUS_WAITING_AFTER_MOVE);
				setRobotTimeout(null);
			}
		}
	}

	@Override
	public void performIOAction() throws AbstractCommunicationException, RobotActionException, InterruptedException {
		writeCommand(RobotConstants.PERMISSIONS_COMMAND_IOACTION);
		boolean prageSucceeded = waitForStatus(RobotConstants.STATUS_IOACTION_FINISHED, IOACTION_TIMEOUT);
		if (!prageSucceeded) {
			setRobotTimeout(new RobotAlarm(RobotAlarm.IOACTION_TIMEOUT));
			waitForStatus(RobotConstants.STATUS_IOACTION_FINISHED);
			setRobotTimeout(null);
		}
	}

	@Override
	public void continueMoveWithPieceTillIPPoint() throws AbstractCommunicationException, RobotActionException, InterruptedException {
		if (getCurrentActionSettings() == null) {
			throw new InterruptedException();
		}
		writeCommand(RobotConstants.PERMISSIONS_COMMAND_MOVEWAIT_CONTINUE);
		boolean waitingForPickFinished = waitForStatus(RobotConstants.STATUS_PICK_OUT_OF_MACHINE, MOVE_TO_IPPOINT_TIMEOUT);
		if (!waitingForPickFinished) {
			setRobotTimeout(new RobotAlarm(RobotAlarm.MOVE_TO_IPPOINT_MOVEWITHPIECE_TIMEOUT));
			waitForStatus(RobotConstants.STATUS_PICK_OUT_OF_MACHINE);
			setRobotTimeout(null);
		}
	}
	
	@Override
	public void continueMoveWithoutPieceTillIPPoint() throws AbstractCommunicationException, RobotActionException, InterruptedException {
		if (getCurrentActionSettings() == null) {
			throw new InterruptedException();
		}
		writeCommand(RobotConstants.PERMISSIONS_COMMAND_MOVEWAIT_CONTINUE);
		boolean waitingForPickFinished = waitForStatus(RobotConstants.STATUS_PUT_OUT_OF_MACHINE, MOVE_TO_IPPOINT_TIMEOUT);
		if (!waitingForPickFinished) {
			setRobotTimeout(new RobotAlarm(RobotAlarm.MOVE_TO_IPPOINT_MOVEWITHPIECE_TIMEOUT));
			waitForStatus(RobotConstants.STATUS_PUT_OUT_OF_MACHINE);
			setRobotTimeout(null);
		}
	}

	@Override
	public void finalizeMovePiece() throws AbstractCommunicationException, RobotActionException, InterruptedException {
		if (getCurrentActionSettings() == null) {
			throw new InterruptedException();
		}
		boolean waitingForPickFinished = waitForStatus(RobotConstants.STATUS_MOVEWAIT_FINISHED, MOVE_FINISH_TIMEOUT);
		if (!waitingForPickFinished) {
			setRobotTimeout(new RobotAlarm(RobotAlarm.FINALIZE_MOVEWITHPIECE_TIMEOUT));
			waitForStatus(RobotConstants.STATUS_MOVEWAIT_FINISHED);
			setRobotTimeout(null);
		}
		setCurrentActionSettings(null);
	}
	
	@Override
	public void initiateMoveWithoutPieceNoAction(final RobotPutSettings putSettings) throws AbstractCommunicationException, RobotActionException, InterruptedException {
		if (isExecutionInProgress()) {
			throw new IllegalStateException("Already performing action, with setting: " + getCurrentActionSettings());
		} else {
			setCurrentActionSettings(putSettings);
		}
		FanucRobotPutSettings fPutSettings = (FanucRobotPutSettings) putSettings;
		writeServiceGripperSet(false, putSettings.getGripperHead().getName(), this.getGripperBody().getGripperHeadByName(HEAD_A_ID), this.getGripperBody().getGripperHeadByName(HEAD_B_ID), RobotConstants.SERVICE_GRIPPER_SERVICE_TYPE_MOVE_WAIT);
		int ppMode = RobotConstants.SERVICE_HANDLING_PP_MODE_ORDER_12;
		if (fPutSettings.getGripperHead().getGripper().getWorkPiece() == null) {
			throw new IllegalStateException(toString() + " executing move-and-wait with piece , but the gripper [" + fPutSettings.getGripperHead().getGripper() + "] should contain a workpiece.");
		}
		if (fPutSettings.isTeachingNeeded()) {
			ppMode = ppMode | RobotConstants.SERVICE_HANDLING_PP_MODE_TEACH;
		}
		ppMode = ppMode | RobotConstants.SERVICE_HANDLING_PP_MODE_NO_WAIT;
		Coordinates smooth = fPutSettings.getSmoothPoint();
		if (smooth == null) {
			smooth = fPutSettings.getWorkArea().getActiveClamping().getSmoothToPoint();
		}
		writeServiceHandlingSet(putSettings.isFreeAfter(), ppMode, fPutSettings.getGripperHead().getGripper().getWorkPiece().getDimensions());
		writeServicePointSet(fPutSettings.getWorkArea(), fPutSettings.getLocation(), fPutSettings.getStep().getRelativeTeachedOffset(), smooth, fPutSettings.getGripperHead().getGripper().getWorkPiece().getDimensions(), fPutSettings.getWorkArea().getActiveClamping());
		fanucRobotCommunication.writeValue(RobotConstants.COMMAND_START_SERVICE, RobotConstants.RESPONSE_START_SERVICE, WRITE_VALUES_TIMEOUT, "1");
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
	
	private void writeServicePointSet(final WorkArea workArea, final Coordinates location, final Coordinates relativeTeachedCoordinates, final Coordinates smoothPoint, final WorkPieceDimensions dimensions, final Clamping clamping) throws SocketDisconnectedException, SocketResponseTimedOutException, InterruptedException {
		List<String> values = new ArrayList<String>();
		// user frame location ; x offset ; y offset ; z offset ; r offset ; z-safe plane offset ; safety add z ; smooth x ; smooth y ; smooth z ; tangent to/from ; xyz allowed ;
		// clamp height ; bar break iterations ; bar break main axis ; bar break angle ; bar move length
		int userFrameId = workArea.getUserFrame().getNumber();
		//UF: stacker = 1; Machine = 3
		if (!VALID_USERFRAMES.contains(userFrameId)) {
			throw new IllegalArgumentException("Illegal Userframe id: " + userFrameId + " should be 1 or 3 or 6.");
		} else {
			values.add("" + userFrameId);					// user frame location
		}
		values.add(df.format(location.getX()));		// x offset
		values.add(df.format(location.getY()));		// y offset
		values.add(df.format(location.getZ()));		// z offset
		values.add(df.format(location.getR()));		// r offset							
		if ((relativeTeachedCoordinates == null) || (relativeTeachedCoordinates.getZ() >= 0)) {
			values.add(df.format(dimensions.getHeight() + location.getZ() + clamping.getHeight()));	// z safe plane offset 
		} else {
			values.add(df.format(dimensions.getHeight() + location.getZ() - relativeTeachedCoordinates.getZ() + clamping.getHeight()));	// z safe plane offset 
		}
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
	public void moveToHome() throws SocketDisconnectedException, SocketResponseTimedOutException, RobotActionException, InterruptedException {
		if ((getSpeed() < 10) || (getSpeed() > 100)) {
			throw new IllegalStateException("The current speed value: [" + getSpeed() + "] is illegal.");
		}
		fanucRobotCommunication.writeValue(RobotConstants.COMMAND_TO_HOME, RobotConstants.RESPONSE_TO_HOME, WRITE_VALUES_TIMEOUT, "" + getSpeed());
		//TODO there's no way of knowing the robot is in its home point
	}

	@Override
	public void moveToChangePoint() throws SocketDisconnectedException, SocketResponseTimedOutException, RobotActionException, InterruptedException {
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
		logger.debug("About to recalculate TCPs.");
		writeServiceGripperSet(false, this.getGripperBody().getGripperHeadByName(HEAD_A_ID).getName(), this.getGripperBody().getGripperHeadByName(HEAD_A_ID), this.getGripperBody().getGripperHeadByName(HEAD_B_ID), RobotConstants.SERVICE_GRIPPER_SERVICE_TYPE_JAW_CHANGE);
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

	@Override
	public RobotPickSettings getDefaultPickSettings() {
		return new FanucRobotPickSettings(this, null, null, null, null, null, false);
	}

	@Override
	public RobotPutSettings getDefaultPutSettings() {
		return new FanucRobotPutSettings(this, null, null, null, null, false);
	}

	public RobotSocketCommunication getRobotSocketCommunication() {
		return this.fanucRobotCommunication;
	}

	@Override
	public float getMaxWorkPieceWeight() {
		return MAX_WORKPIECE_WEIGHT;
	}
	
}
