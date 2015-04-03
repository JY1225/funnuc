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
import eu.robojob.millassist.external.communication.socket.SocketWrongResponseException;
import eu.robojob.millassist.external.device.Clamping;
import eu.robojob.millassist.external.device.SimpleWorkArea;
import eu.robojob.millassist.external.robot.AbstractRobotActionSettings.ApproachType;
import eu.robojob.millassist.external.device.processing.reversal.ReversalUnit;
import eu.robojob.millassist.external.robot.AbstractRobot;
import eu.robojob.millassist.external.robot.GripperBody;
import eu.robojob.millassist.external.robot.GripperHead;
import eu.robojob.millassist.external.robot.RobotActionException;
import eu.robojob.millassist.external.robot.AirblowSquare;
import eu.robojob.millassist.external.robot.RobotAlarm;
import eu.robojob.millassist.external.robot.RobotConstants;
import eu.robojob.millassist.external.robot.RobotDataManager;
import eu.robojob.millassist.external.robot.RobotMonitoringThread;
import eu.robojob.millassist.external.robot.RobotPickSettings;
import eu.robojob.millassist.external.robot.RobotPutSettings;
import eu.robojob.millassist.external.robot.RobotSocketCommunication;
import eu.robojob.millassist.positioning.Config;
import eu.robojob.millassist.positioning.Coordinates;
import eu.robojob.millassist.positioning.RobotData.RobotIPPoint;
import eu.robojob.millassist.positioning.RobotData.RobotRefPoint;
import eu.robojob.millassist.positioning.RobotData.RobotSpecialPoint;
import eu.robojob.millassist.positioning.RobotData.RobotUserFrame;
import eu.robojob.millassist.positioning.RobotPosition;
import eu.robojob.millassist.threading.ThreadManager;
import eu.robojob.millassist.workpiece.IWorkPieceDimensions;
import eu.robojob.millassist.workpiece.WorkPiece;
import eu.robojob.millassist.workpiece.RectangularDimensions;
import eu.robojob.millassist.workpiece.WorkPiece.Dimensions;

public class FanucRobot extends AbstractRobot {

	private RobotSocketCommunication fanucRobotCommunication;
	
	private static final int WRITE_VALUES_TIMEOUT = 2 * 5000;
	private static final int MOVE_TO_LOCATION_TIMEOUT = 3 * 60 * 1000;
	private static final int CLAMP_ACK_REQUEST_TIMEOUT = 10 * 1000;
	private static final int MOVE_TO_IPPOINT_TIMEOUT = 3 * 60 * 1000;
	private static final int MOVE_FINISH_TIMEOUT = 3 * 60 * 1000;
	private static final int ASK_POSITION_TIMEOUT = 50000;
	private static final int ASK_STATUS_TIMEOUT = 2 * 5 * 1000;
	private static final int TEACH_TIMEOUT = 10 * 60 * 1000;
	
	private static final int WRITE_REGISTER_TIMEOUT = 2 * 5000;
	private static final int IOACTION_TIMEOUT = 2 * 60 * 1000;
	
	private static final List<Integer> VALID_USERFRAMES = Arrays.asList(1, 3, 4, 6, 11, 12, 13, 14);
	
	private static final String HEAD_A_ID = "A";
	private static final String HEAD_B_ID = "B";
	
	private DecimalFormat df;
	private DecimalFormat df2;
		
	private static Logger logger = LogManager.getLogger(FanucRobot.class.getName());
	
	public FanucRobot(final String name, final Set<GripperBody> gripperBodies, final GripperBody gripperBody, final float payload, 
	        final SocketConnection socketConnection, final boolean acceptData) {
		super(name, gripperBodies, gripperBody, payload, acceptData);
		this.fanucRobotCommunication = new RobotSocketCommunication(socketConnection, this);
		RobotMonitoringThread monitoringThread = new RobotMonitoringThread(this);
		ThreadManager.submit(monitoringThread);
		df = new DecimalFormat("#.###");
		df2 = new DecimalFormat("#");
		df.setDecimalSeparatorAlwaysShown(false);
		DecimalFormatSymbols custom = new DecimalFormatSymbols();
		custom.setDecimalSeparator('.');
		df.setDecimalFormatSymbols(custom);
	}
	
	public FanucRobot(final String name, final float payload, final SocketConnection socketConnection, final boolean acceptData) {
		this(name, null, null, payload, socketConnection, acceptData);
	}

	@Override
	public void updateStatusRestAndAlarms() throws AbstractCommunicationException, InterruptedException {
		List<String> values = fanucRobotCommunication.readValues(RobotConstants.COMMAND_ASK_STATUS, RobotConstants.RESPONSE_ASK_STATUS, ASK_STATUS_TIMEOUT);
		int errorId = Integer.parseInt(values.get(0));
		int controllerValue = Integer.parseInt(values.get(1));
		int controllerString = Integer.parseInt(values.get(2));
		double xRest = Float.parseFloat(values.get(3));
		double yRest = Float.parseFloat(values.get(4));
		double zRest = Float.parseFloat(values.get(5));
		setAlarms(RobotAlarm.parseFanucRobotAlarms(errorId, controllerValue, getRobotTimeout()));
		setStatus(controllerString);
		setRestValues(xRest, yRest, zRest);
	}

	@Override
	public void sendSpeed(final int speedPercentage) throws SocketDisconnectedException, SocketResponseTimedOutException, InterruptedException, SocketWrongResponseException {
		fanucRobotCommunication.writeValue(RobotConstants.COMMAND_SET_SPEED, RobotConstants.RESPONSE_SET_SPEED, WRITE_VALUES_TIMEOUT, speedPercentage + "");
	}
	
	@Override
	public Coordinates getPosition() throws SocketDisconnectedException, SocketResponseTimedOutException, RobotActionException, InterruptedException, SocketWrongResponseException {
		Coordinates position = fanucRobotCommunication.getPosition(ASK_POSITION_TIMEOUT);
		return position;
	}
	
	@Override
	public void continueProgram() throws SocketDisconnectedException, SocketResponseTimedOutException, InterruptedException, SocketWrongResponseException {
		fanucRobotCommunication.writeCommand(RobotConstants.COMMAND_CONTINUE, RobotConstants.RESPONSE_CONTINUE, WRITE_VALUES_TIMEOUT);
	}
	
	@Override
	public void abort() throws InterruptedException, AbstractCommunicationException {
		setCurrentActionSettings(null);
		fanucRobotCommunication.writeCommand(RobotConstants.COMMAND_ABORT, RobotConstants.RESPONSE_ABORT, WRITE_VALUES_TIMEOUT);
		restartProgram();
		setSpeed(this.getSpeed());
	}
	
	public void disconnect() {
		fanucRobotCommunication.disconnect();
	}
	
	@Override
	public void restartProgram() throws SocketDisconnectedException, SocketResponseTimedOutException, InterruptedException, SocketWrongResponseException {
		setCurrentActionSettings(null);
		fanucRobotCommunication.writeCommand(RobotConstants.COMMAND_RESTART_PROGRAM, RobotConstants.RESPONSE_RESTART_PROGRAM, WRITE_VALUES_TIMEOUT);
		setCurrentActionSettings(null);
	}

	@Override
	public void reset() throws SocketDisconnectedException, SocketResponseTimedOutException, InterruptedException, SocketWrongResponseException {
		fanucRobotCommunication.writeCommand(RobotConstants.COMMAND_RESET, RobotConstants.RESPONSE_RESET, WRITE_VALUES_TIMEOUT);
	}
	
	@Override
	public void writeRegister(final int registerNr, final String value) throws SocketDisconnectedException, SocketResponseTimedOutException, RobotActionException, InterruptedException, SocketWrongResponseException {
		fanucRobotCommunication.writeValue(RobotConstants.COMMAND_WRITE_REGISTER, RobotConstants.RESPONSE_WRITE_REGISTER, WRITE_REGISTER_TIMEOUT, "" + registerNr);
	}
	
	public void openGripperA() throws SocketDisconnectedException, SocketResponseTimedOutException, InterruptedException, SocketWrongResponseException {
		List<String> values = new ArrayList<String>();
		values.add(2 + "");
		values.add(0 + "");
		fanucRobotCommunication.writeValues(RobotConstants.COMMAND_GRIPPER_ACTION, RobotConstants.RESPONSE_GRIPPER_ACTION, WRITE_VALUES_TIMEOUT, values);
	}
	
	public void closeGripperA() throws SocketDisconnectedException, SocketResponseTimedOutException, InterruptedException, SocketWrongResponseException {
		List<String> values = new ArrayList<String>();
		values.add(2 + "");
		values.add(1 + "");
		fanucRobotCommunication.writeValues(RobotConstants.COMMAND_GRIPPER_ACTION, RobotConstants.RESPONSE_GRIPPER_ACTION, WRITE_VALUES_TIMEOUT, values);
	}
	
	public void openGripperB() throws SocketDisconnectedException, SocketResponseTimedOutException, InterruptedException, SocketWrongResponseException {
		List<String> values = new ArrayList<String>();
		values.add(3 + "");
		values.add(0 + "");
		fanucRobotCommunication.writeValues(RobotConstants.COMMAND_GRIPPER_ACTION, RobotConstants.RESPONSE_GRIPPER_ACTION, WRITE_VALUES_TIMEOUT, values);
	}
	
	public void closeGripperB() throws SocketDisconnectedException, SocketResponseTimedOutException, InterruptedException, SocketWrongResponseException {
		List<String> values = new ArrayList<String>();
		values.add(3 + "");
		values.add(1 + "");
		fanucRobotCommunication.writeValues(RobotConstants.COMMAND_GRIPPER_ACTION, RobotConstants.RESPONSE_GRIPPER_ACTION, WRITE_VALUES_TIMEOUT, values);
	}
	
	@Override
	public void initiatePut(final RobotPutSettings putSettings, Clamping clamping) throws AbstractCommunicationException, RobotActionException, InterruptedException {
		if (isExecutionInProgress()) {
			throw new IllegalStateException("Already performing action, with setting: " + getCurrentActionSettings());
		} else {
			setCurrentActionSettings(putSettings);
		}
		FanucRobotPutSettings fPutSettings = (FanucRobotPutSettings) putSettings;
		writeServiceGripperSet(putSettings.getGripperHead().getName(), this.getGripperBody().getGripperHeadByName(HEAD_A_ID), this.getGripperBody().getGripperHeadByName(HEAD_B_ID), RobotConstants.SERVICE_GRIPPER_SERVICE_TYPE_PUT, putSettings.isGripInner());
		int ppMode = RobotConstants.SERVICE_HANDLING_PP_MODE_ORDER_12;
		if (fPutSettings.isReleaseBeforeMachine()) {
			ppMode = RobotConstants.SERVICE_HANDLING_PP_MODE_ORDER_21;
		}
		if (fPutSettings.isRobotAirblow()) {
			writeAirblowPointSet(clamping, putSettings.getAirblowSquare(clamping.getId()));
			ppMode = ppMode | RobotConstants.SERVICE_HANDLING_PP_MODE_AIRBLOW;
		}
		if (fPutSettings.isTeachingNeeded()) {
			ppMode = ppMode | RobotConstants.SERVICE_HANDLING_PP_MODE_TEACH;
		}
		if (fPutSettings.getTurnInMachineBeforePut()) {
			ppMode = ppMode | RobotConstants.SERVICE_HANDLING_PP_MODE_TIM;
		}
		writeServiceHandlingSet(putSettings.isFreeAfter(), ppMode, fPutSettings.getGripperHead().getGripper().getWorkPiece().getDimensions(), -fPutSettings.getGripperHead().getGripper().getWorkPiece().getWeight(), putSettings.getApproachType());
		if (fPutSettings.getGripperHead().getGripper().getWorkPiece() == null) {
			throw new IllegalStateException(toString() + " executing put, but the gripper [" + fPutSettings.getGripperHead().getGripper() + "] should contain a workpiece.");
		}
		Coordinates smooth = fPutSettings.getSmoothPoint();
		if (smooth == null) {
			smooth = fPutSettings.getWorkArea().getDefaultClamping().getSmoothToPoint();
		}
		writeServicePointSet(fPutSettings.getWorkArea(), fPutSettings.getLocation(), smooth, fPutSettings.getGripperHead().getGripper().getWorkPiece().getDimensions(), 
				fPutSettings.getWorkArea().getDefaultClamping(), putSettings.getApproachType());
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
		boolean waitingForPutFinished = waitForStatus(RobotConstants.STATUS_PUT_FINISHED, MOVE_FINISH_TIMEOUT);
		if (!waitingForPutFinished) {
			setRobotTimeout(new RobotAlarm(RobotAlarm.FINALIZE_PUT_TIMEOUT));
			waitForStatus(RobotConstants.STATUS_PUT_FINISHED);
			setRobotTimeout(null);
		}
		setCurrentActionSettings(null);
	}

	@Override
	public void initiatePick(final RobotPickSettings pickSettings, Clamping clamping) throws AbstractCommunicationException, RobotActionException, InterruptedException {
		if (isExecutionInProgress()) {
			throw new IllegalStateException("Already performing action, with setting: " + getCurrentActionSettings());
		} else {
			setCurrentActionSettings(pickSettings);
		}
		RobotPickSettings fPickSettings = (RobotPickSettings) pickSettings;		
		writeServiceGripperSet(pickSettings.getGripperHead().getName(), this.getGripperBody().getGripperHeadByName(HEAD_A_ID), this.getGripperBody().getGripperHeadByName(HEAD_B_ID), RobotConstants.SERVICE_GRIPPER_SERVICE_TYPE_PICK, pickSettings.isGripInner());
		int ppMode = RobotConstants.SERVICE_HANDLING_PP_MODE_ORDER_12;
		if (fPickSettings.isRobotAirblow()) {
			writeAirblowPointSet(clamping, pickSettings.getAirblowSquare(clamping.getId()));
			ppMode = ppMode | RobotConstants.SERVICE_HANDLING_PP_MODE_AIRBLOW;
		}
		if (fPickSettings.isTeachingNeeded()) {
			ppMode = ppMode | RobotConstants.SERVICE_HANDLING_PP_MODE_TEACH;
		}
		if (fPickSettings.getTurnInMachineBeforePick()) {
			ppMode = ppMode | RobotConstants.SERVICE_HANDLING_PP_MODE_TIM;
		}
		Coordinates smooth = fPickSettings.getSmoothPoint();
		if (smooth == null) {
			smooth = fPickSettings.getWorkArea().getDefaultClamping().getSmoothFromPoint();
		}
		ApproachType approachType = pickSettings.getApproachType();
		writeServiceHandlingSet(pickSettings.isFreeAfter(), ppMode, pickSettings.getWorkPiece().getDimensions(), pickSettings.getWorkPiece().getWeight(), approachType);
		Coordinates pickLocation = new Coordinates(fPickSettings.getLocation());
		writeServicePointSet(fPickSettings.getWorkArea(), pickLocation, smooth, fPickSettings.getWorkPiece().getDimensions(), 
				fPickSettings.getWorkArea().getDefaultClamping(), approachType);
		logger.info("About to write start service!");
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
		writeServiceGripperSet(putSettings.getGripperHead().getName(), this.getGripperBody().getGripperHeadByName(HEAD_A_ID), this.getGripperBody().getGripperHeadByName(HEAD_B_ID), RobotConstants.SERVICE_GRIPPER_SERVICE_TYPE_MOVE_WAIT, putSettings.isGripInner());
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
			smooth = fPutSettings.getWorkArea().getDefaultClamping().getSmoothToPoint();
		}
		ApproachType approachType = putSettings.getApproachType();
		writeServiceHandlingSet(putSettings.isFreeAfter(), ppMode, fPutSettings.getGripperHead().getGripper().getWorkPiece().getDimensions(), 0.0f, approachType);
		writeServicePointSet(fPutSettings.getWorkArea(), fPutSettings.getLocation(), smooth, fPutSettings.getGripperHead().getGripper().getWorkPiece().getDimensions(), 
				fPutSettings.getWorkArea().getDefaultClamping(), approachType);
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
		writeServiceGripperSet(putSettings.getGripperHead().getName(), this.getGripperBody().getGripperHeadByName(HEAD_A_ID), this.getGripperBody().getGripperHeadByName(HEAD_B_ID), RobotConstants.SERVICE_GRIPPER_SERVICE_TYPE_MOVE_WAIT, putSettings.isGripInner());
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
			smooth = fPutSettings.getWorkArea().getDefaultClamping().getSmoothToPoint();
		}
		ppMode = ppMode | RobotConstants.SERVICE_HANDLING_PP_MODE_NO_WAIT;
		ApproachType approachType = putSettings.getApproachType();	
		writeServiceHandlingSet(putSettings.isFreeAfter(), ppMode, fPutSettings.getGripperHead().getGripper().getWorkPiece().getDimensions(), 0.0f, approachType);
		writeServicePointSet(fPutSettings.getWorkArea(), fPutSettings.getLocation(), smooth, fPutSettings.getGripperHead().getGripper().getWorkPiece().getDimensions(), 
				fPutSettings.getWorkArea().getDefaultClamping(), approachType);
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
		writeServiceGripperSet(putSettings.getGripperHead().getName(), this.getGripperBody().getGripperHeadByName(HEAD_A_ID), this.getGripperBody().getGripperHeadByName(HEAD_B_ID), RobotConstants.SERVICE_GRIPPER_SERVICE_TYPE_MOVE_WAIT, 
				putSettings.isGripInner());
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
			smooth = fPutSettings.getWorkArea().getDefaultClamping().getSmoothToPoint();
		}
		ApproachType approachType = putSettings.getApproachType();
		writeServiceHandlingSet(putSettings.isFreeAfter(), ppMode, fPutSettings.getGripperHead().getGripper().getWorkPiece().getDimensions(), 0.0f, approachType);
		writeServicePointSet(fPutSettings.getWorkArea(), fPutSettings.getLocation(), smooth, fPutSettings.getGripperHead().getGripper().getWorkPiece().getDimensions(), 
				fPutSettings.getWorkArea().getDefaultClamping(), approachType);
		fanucRobotCommunication.writeValue(RobotConstants.COMMAND_START_SERVICE, RobotConstants.RESPONSE_START_SERVICE, WRITE_VALUES_TIMEOUT, "1");
	}
	
	private void writeServiceGripperSet(final String headId, final GripperHead gHeadA, final GripperHead gHeadB, final int serviceType, 
			final boolean gripInner) throws SocketDisconnectedException, SocketResponseTimedOutException, InterruptedException, SocketWrongResponseException {
		List<String> values = new ArrayList<String>();
		boolean a = false;
		if (headId.equals(HEAD_A_ID)) {
			a = true;
		} else if (headId.equals(HEAD_B_ID)) {
			a = false;
		} else {
			throw new IllegalArgumentException("Gripper head id should be " + HEAD_A_ID + " or " + HEAD_B_ID + ".");
		}
		// service type ; grip select ; a height ; b height ; inner/outer gripper type ;
		values.add("" + serviceType);	// service type
		if (a) {						// Selected gripper (2 = A - 3 = B - 4 = C)
			values.add("2");		
		} else {
			values.add("3");
		}
		values.add("" + (int) Math.floor(gHeadA.getGripper().getHeight()));		// a height
		if (gHeadB != null) {
			values.add("" + (int) Math.floor(gHeadB.getGripper().getHeight()));		// b height
		} else {
			values.add("0");		// b height
		}
		// inner/outer gripper type
		if (gripInner) {
			values.add("1");			// inner
		} else {
			values.add("0");			// outer
		}
		logger.debug("Writing service gripper set: " + values);
		fanucRobotCommunication.writeValues(RobotConstants.COMMAND_WRITE_SERVICE_GRIPPER, RobotConstants.RESPONSE_WRITE_SERVICE_GRIPPER, WRITE_VALUES_TIMEOUT, values);
	}

	private void writeServiceHandlingSet(final boolean freeAfterService, final int serviceHandlingPPMode, final IWorkPieceDimensions dimensions, final float weight2, final ApproachType approachType)
			throws SocketDisconnectedException, SocketResponseTimedOutException, InterruptedException, SocketWrongResponseException {
		List<String> values = new ArrayList<String>();
		// free after this service ; shape WP ;  WP length ; WP width ; WP height ;  
		//;  ; dx correction P1 ; dy correction P1 ; dx correction P2 ; dy correction P2 ; dW correction ;
		//    dP correction ; robot speed ; payload 1 ; payload 2 ; PP mode ; positioning type (approach)
		if (freeAfterService) {				// free after this service
			values.add("1");
		} else {
			values.add("0");
		}
		if (dimensions instanceof RectangularDimensions) {
			values.add("1");	// select shape (Box - 1)
			values.add(df.format(Math.max(dimensions.getDimension(Dimensions.LENGTH), dimensions.getDimension(Dimensions.WIDTH))));	// WP length (WP diameter)
			values.add(df.format(Math.min(dimensions.getDimension(Dimensions.LENGTH), dimensions.getDimension(Dimensions.WIDTH))));	// WP width
			values.add(df.format(dimensions.getDimension(Dimensions.HEIGHT)));	// WP height
		} else {
			values.add("2");	// select shape (Round - 2)
			values.add(df.format(dimensions.getDimension(Dimensions.DIAMETER)));	// WP length (WP diameter)
			values.add(df.format(dimensions.getDimension(Dimensions.DIAMETER)));	// WP width
			values.add(df.format(dimensions.getDimension(Dimensions.HEIGHT)));	// WP height
		}
		values.add("0");					// gripped height

		if ((getSpeed() < 5) || (getSpeed() > 100)) {
			throw new IllegalStateException("The current speed value: [" + getSpeed() + "] is illegal.");
		}
		WorkPiece wp1 = null;
		WorkPiece wp2 = null;
		if ((getGripperBody().getGripperHeadByName("A") != null) && (getGripperBody().getGripperHeadByName("A").getGripper() != null)) {
			wp1 = getGripperBody().getGripperHeadByName("A").getGripper().getWorkPiece();
		}
		if ((getGripperBody().getGripperHeadByName("B") != null) && (getGripperBody().getGripperHeadByName("B").getGripper() != null)) {
			wp2 = getGripperBody().getGripperHeadByName("B").getGripper().getWorkPiece();
		}
		float payLoad1 = 0.0f;
		float payLoad2 = 0.0f;
		if (wp1 != null) {
			payLoad1 = wp1.getWeight() * 10;
			payLoad2 = wp1.getWeight() * 10;
		}
		if (wp2 != null) {
			payLoad1 += wp2.getWeight() * 10;
			payLoad2 += wp2.getWeight() * 10;
		}
		payLoad2 = payLoad2 + weight2;
		values.add(getSpeed() + "");						// robot speed
		values.add(df2.format(Math.ceil(payLoad1)));		// payload 1
		values.add(df2.format(Math.ceil(payLoad2)));		// payload 2
		values.add("" + serviceHandlingPPMode);				// PP mode
		values.add("" + approachType.getId());				// positioning type (approach empty/top/bottom)
		logger.debug("Writing service handling set: " + values);
		fanucRobotCommunication.writeValues(RobotConstants.COMMAND_WRITE_SERVICE_HANDLING, RobotConstants.RESPONSE_WRITE_SERVICE_HANDLING, WRITE_VALUES_TIMEOUT, values);
	}
	
	private void writeServicePointSet(final SimpleWorkArea workArea, final Coordinates location, final Coordinates smoothPoint, final IWorkPieceDimensions dimensions, 
			final Clamping clamping, final ApproachType approachType) throws SocketDisconnectedException, SocketResponseTimedOutException, InterruptedException, SocketWrongResponseException {
		List<String> values = new ArrayList<String>();
		// user frame id ; x destination ; y destination ; z destination ; w destination, p destination, r destination ; z-safe plane ; safety add z ; smooth x ; smooth y ; smooth z ; 
		// approachStrategy ; clamp height 
		int userFrameId = workArea.getWorkAreaManager().getUserFrame().getNumber();
		//UF: stacker = 1; Machine = 3
		if (!VALID_USERFRAMES.contains(userFrameId)) {
			throw new IllegalArgumentException("Illegal Userframe id: " + userFrameId + " should be 1 or 3 or 6.");
		} else {
			values.add("" + userFrameId);			// select user frame
		}
		values.add(df.format(location.getX()));		// destination x
		values.add(df.format(location.getY()));		// destination y 
		values.add(df.format(location.getZ()));		// destination z 
		values.add(df.format(location.getW()));		// destination w 
		values.add(df.format(location.getP()));		// destination p 
		values.add(df.format(location.getR()));		// destination r 			
		
		float zSafePlane = workArea.getWorkAreaManager().getZone().getDevice().getZSafePlane(dimensions, workArea, approachType);
		
		values.add(df.format(zSafePlane));	// z-safe plane
		
		// Safety add Z: use UF value, compare to smooth and use the largest
		if (smoothPoint.getZ() > workArea.getWorkAreaManager().getUserFrame().getzSafeDistance()) {	// safety add z
			values.add(df.format(smoothPoint.getZ()));
		} else {
			values.add("" + workArea.getWorkAreaManager().getUserFrame().getzSafeDistance());
		}
		values.add(df.format(smoothPoint.getX()));	// smooth x
		values.add(df.format(smoothPoint.getY()));	// smooth y
		values.add(df.format(smoothPoint.getZ()));	// smooth z
		//TODO review if this strategy is always safe
		// The approach strategy can be overwritten by the robot in case the height of the IP-point is lower than the reversal unit + height of workpiece
		if (workArea.getWorkAreaManager().getZone().getDevice() instanceof ReversalUnit) {
			switch (approachType) {
			case BOTTOM:
				values.add("" + RobotConstants.SERVICE_POINT_XYZ_ALLOWED_XZX);	// eerst X, dan zakken en X wegbewegen
				break;
			case FRONT:
			case LEFT:
				values.add("" + RobotConstants.SERVICE_POINT_XYZ_ALLOWED_Z);	// eerst XY, dan Z zakken
				break;
			default:
				values.add("" + RobotConstants.SERVICE_POINT_XYZ_ALLOWED_XY);	// first z safe, aftwards XY movements
				break;
			}
		} else {
			values.add("" + RobotConstants.SERVICE_POINT_XYZ_ALLOWED_XYZ);	// xyz allowed
		}
		values.add("" + df.format((clamping.getHeight() + clamping.getRelativePosition().getZ())));	// clamp height (we need to include the relative position, height is measured from z = 0)
		logger.debug("Writing service point: " + values);
		fanucRobotCommunication.writeValues(RobotConstants.COMMAND_WRITE_SERVICE_POINT, RobotConstants.RESPONSE_WRITE_SERVICE_POINT, WRITE_VALUES_TIMEOUT, values);
	}
	
	private void writeAirblowPointSet(final Clamping clamping, final AirblowSquare airblowSettings) throws SocketDisconnectedException, SocketResponseTimedOutException, InterruptedException, SocketWrongResponseException {
		Coordinates bottom = Coordinates.add(airblowSettings.getBottomCoord(), clamping.getRelativePosition());
		Coordinates top = Coordinates.add(airblowSettings.getTopCoord(), clamping.getRelativePosition());
		List<String> values = new ArrayList<String>();
		//XYZ
		values.add(df.format(bottom.getX()));
		values.add(df.format(bottom.getY()));
		values.add(df.format(bottom.getZ()));
		//WPR
		values.add("0");
		values.add("0");
		values.add("0");
		//XYZ
		values.add(df.format(top.getX()));
		values.add(df.format(top.getY()));
		values.add(df.format(bottom.getZ()));
		//WPR
		values.add("0");
		values.add("0");
		values.add("0");
		logger.debug("Writing airblow points: " + values);
		fanucRobotCommunication.writeValues(RobotConstants.COMMAND_WRITE_AIRBLOW, RobotConstants.RESPONSE_WRITE_AIRBLOW, WRITE_VALUES_TIMEOUT, values);
	}

	private void writeCommand(final int permission) throws SocketDisconnectedException, SocketResponseTimedOutException, InterruptedException, SocketWrongResponseException {
		List<String> values = new ArrayList<String>();
		values.add("" + permission);
		fanucRobotCommunication.writeValues(RobotConstants.COMMAND_SET_PERMISSIONS, RobotConstants.RESPONSE_SET_PERMISSIONS, WRITE_VALUES_TIMEOUT, values);
	}
	
	@Override
	public void moveToHome() throws SocketDisconnectedException, SocketResponseTimedOutException, RobotActionException, InterruptedException, SocketWrongResponseException {
		if ((getSpeed() < 5) || (getSpeed() > 100)) {
			throw new IllegalStateException("The current speed value: [" + getSpeed() + "] is illegal.");
		}
		fanucRobotCommunication.writeValue(RobotConstants.COMMAND_TO_HOME, RobotConstants.RESPONSE_TO_HOME, WRITE_VALUES_TIMEOUT, "" + getSpeed());
		//TODO there's no way of knowing the robot is in its home point
	}

	@Override
	public void moveToChangePoint() throws SocketDisconnectedException, SocketResponseTimedOutException, RobotActionException, InterruptedException, SocketWrongResponseException {
		fanucRobotCommunication.writeCommand(RobotConstants.COMMAND_JAW_CH, RobotConstants.RESPONSE_JAW_CH, WRITE_VALUES_TIMEOUT);
	}

	@Override
	public boolean isConnected() {
		return fanucRobotCommunication.isConnected();
	}

	@Override
	public void recalculateTCPs() throws SocketDisconnectedException, SocketResponseTimedOutException, InterruptedException, SocketWrongResponseException {
		logger.debug("About to recalculate TCPs.");
		writeServiceGripperSet(this.getGripperBody().getGripperHeadByName(HEAD_A_ID).getName(), this.getGripperBody().getGripperHeadByName(HEAD_A_ID), this.getGripperBody().getGripperHeadByName(HEAD_B_ID), RobotConstants.SERVICE_GRIPPER_SERVICE_TYPE_JAW_CHANGE, false);
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
		return new FanucRobotPickSettings(this, null, null, null, null, null, false, false);
	}

	@Override
	public RobotPutSettings getDefaultPutSettings() {
		return new FanucRobotPutSettings(this, null, null, null, null, false, false, false);
	}

	public RobotSocketCommunication getRobotSocketCommunication() {
	    return this.fanucRobotCommunication;
	}

	@Override
	public void moveToCustomPosition() throws AbstractCommunicationException, RobotActionException, InterruptedException {
	    fanucRobotCommunication.writeCommand(RobotConstants.COMMAND_TO_CUSTOM_POS, RobotConstants.RESPONSE_TO_CUSTOM_POS, WRITE_VALUES_TIMEOUT);
	}

	private List<String> getPositionValues(Coordinates coord, Config config) {
	    List<String> values = new ArrayList<String>();
	    values.add(df.format(coord.getX()));
	    values.add(df.format(coord.getY()));
	    values.add(df.format(coord.getZ()));
	    values.add(df.format(coord.getW()));
	    values.add(df.format(coord.getP()));
	    values.add(df.format(coord.getR()));
	    values.add(df.format(config.getCfgFlip()));
	    values.add(df.format(config.getCfgUp()));
	    values.add(df.format(config.getCfgFront()));
	    values.add(df.format(config.getCfgTurn1()));
	    values.add(df.format(config.getCfgTurn2()));
	    values.add(df.format(config.getCfgTurn3()));
	    return values;
	}

	@Override
	public void writeUserFrame(RobotUserFrame userframe, final RobotPosition position) throws AbstractCommunicationException, RobotActionException, InterruptedException {
        List<String> values = new ArrayList<String>();
        values.add("" + userframe.getUfNr());
        // Position
        Coordinates coord = position.getPosition();
        Config config = position.getConfiguration();
        values.addAll(getPositionValues(coord, config));
        fanucRobotCommunication.writeValues(RobotConstants.COMMAND_WRITE_USERFRAME, RobotConstants.RESPONSE_WRITE_USERFRAME, WRITE_VALUES_TIMEOUT, values);
	}

	@Override
	public void readUserFrame(RobotUserFrame userframe) throws AbstractCommunicationException,
			RobotActionException, InterruptedException {
        List<String> values = new ArrayList<String>();
        values.add("" + userframe.getUfNr());
        List<String> result = fanucRobotCommunication.readValues(RobotConstants.COMMAND_READ_USERFRAME, RobotConstants.RESPONSE_READ_USERFRAME, ASK_POSITION_TIMEOUT, values);
        // index 0 is responseId, index 1 is userframeID
        float x = Float.parseFloat(result.get(2));
        float y = Float.parseFloat(result.get(3));
        float z = Float.parseFloat(result.get(4));
        float w = Float.parseFloat(result.get(5));
        float p = Float.parseFloat(result.get(6));
        float r = Float.parseFloat(result.get(7));
        Coordinates coord = new Coordinates(x,y,z,w,p,r);
        int cfg1 = Integer.parseInt(result.get(8));
        int cfg2 = Integer.parseInt(result.get(9));
        int cfg3 = Integer.parseInt(result.get(10));
        int cfg4 = Integer.parseInt(result.get(11));
        int cfg5 = Integer.parseInt(result.get(12));
        int cfg6 = Integer.parseInt(result.get(13));
        Config config = new Config(cfg1, cfg2, cfg3, cfg4, cfg5, cfg6);
        RobotDataManager.addUserframe(userframe, new RobotPosition(coord, config));
        logger.debug("read " + userframe.toString());
	}

    @Override
    public void writeIPPoint(RobotIPPoint ipPoint, final RobotPosition position) throws AbstractCommunicationException, RobotActionException, InterruptedException {
        List<String> values = new ArrayList<String>();
        values.add("" + ipPoint.getUfNr());
        values.add("" + ipPoint.getTfNr());
        values.add("" + ipPoint.getPosType().getId());
        // Position
        Coordinates coord = position.getPosition();
        Config config = position.getConfiguration();
        values.addAll(getPositionValues(coord, config));
        fanucRobotCommunication.writeValues(RobotConstants.COMMAND_WRITE_IP_POINT, RobotConstants.RESPONSE_WRITE_IP_POINT, WRITE_VALUES_TIMEOUT, values);
    }

    @Override
    public void readIPPoint(RobotIPPoint ipPoint) throws AbstractCommunicationException, RobotActionException, InterruptedException {
        List<String> values = new ArrayList<String>();
        values.add("" + ipPoint.getUfNr());
        values.add("" + ipPoint.getTfNr());
        values.add("" + ipPoint.getPosType().getId());
        List<String> result = fanucRobotCommunication.readValues(RobotConstants.COMMAND_READ_IP_POINT, RobotConstants.RESPONSE_READ_IP_POINT, ASK_POSITION_TIMEOUT, values);
        // index 0 is responseId, index 1 is ufNr, index 2 is tfNr, index 3 is ApproachType
        float x = Float.parseFloat(result.get(4));
        float y = Float.parseFloat(result.get(5));
        float z = Float.parseFloat(result.get(6));
        float w = Float.parseFloat(result.get(7));
        float p = Float.parseFloat(result.get(8));
        float r = Float.parseFloat(result.get(9));
        Coordinates coord = new Coordinates(x,y,z,w,p,r);
        int cfg1 = Integer.parseInt(result.get(10));
        int cfg2 = Integer.parseInt(result.get(11));
        int cfg3 = Integer.parseInt(result.get(12));
        int cfg4 = Integer.parseInt(result.get(13));
        int cfg5 = Integer.parseInt(result.get(14));
        int cfg6 = Integer.parseInt(result.get(15));
        Config config = new Config(cfg1, cfg2, cfg3, cfg4, cfg5, cfg6);
        RobotDataManager.addIPPoint(ipPoint, new RobotPosition(coord, config));
        logger.debug("read " + ipPoint.toString());
    }

    @Override
    public void writeRPPoint(RobotRefPoint rpPoint, final RobotPosition position) throws AbstractCommunicationException, RobotActionException, InterruptedException {
        List<String> values = new ArrayList<String>();
        values.add("" + rpPoint.getUfNr());
        values.add("" + rpPoint.getTfNr());
        values.add("" + rpPoint.getOriginalTfNr());
        // Position
        Coordinates coord = position.getPosition();
        Config config = position.getConfiguration();
        values.addAll(getPositionValues(coord, config));
        fanucRobotCommunication.writeValues(RobotConstants.COMMAND_WRITE_REF_POINT, RobotConstants.RESPONSE_WRITE_REF_POINT, WRITE_VALUES_TIMEOUT, values);
    }

    @Override
    public void readRPPoint(RobotRefPoint rpPoint) throws AbstractCommunicationException, RobotActionException, InterruptedException {
        List<String> values = new ArrayList<String>();
        values.add("" + rpPoint.getUfNr());
        values.add("" + rpPoint.getTfNr());
        values.add("" + rpPoint.getOriginalTfNr());
        List<String> result = fanucRobotCommunication.readValues(RobotConstants.COMMAND_READ_REF_POINT, RobotConstants.RESPONSE_READ_REF_POINT, ASK_POSITION_TIMEOUT, values);
        // index 0 is responseId, index 1 is ufNr, index 2 is tfNr, index 3 is originalTfNr
        float x = Float.parseFloat(result.get(4));
        float y = Float.parseFloat(result.get(5));
        float z = Float.parseFloat(result.get(6));
        float w = Float.parseFloat(result.get(7));
        float p = Float.parseFloat(result.get(8));
        float r = Float.parseFloat(result.get(9));
        Coordinates coord = new Coordinates(x,y,z,w,p,r);
        int cfg1 = Integer.parseInt(result.get(10));
        int cfg2 = Integer.parseInt(result.get(11));
        int cfg3 = Integer.parseInt(result.get(12));
        int cfg4 = Integer.parseInt(result.get(13));
        int cfg5 = Integer.parseInt(result.get(14));
        int cfg6 = Integer.parseInt(result.get(15));
        Config config = new Config(cfg1, cfg2, cfg3, cfg4, cfg5, cfg6);
        RobotDataManager.addRPPoint(rpPoint, new RobotPosition(coord, config));
        logger.debug("read " + rpPoint.toString());
    }

    @Override
    public void writeSpecialPoint(RobotSpecialPoint specialPoint, final RobotPosition position) throws AbstractCommunicationException, RobotActionException, InterruptedException {
        List<String> values = new ArrayList<String>();
        values.add("" + specialPoint.getId());
        // Position
        Coordinates coord = position.getPosition();
        Config config = position.getConfiguration();
        values.addAll(getPositionValues(coord, config));
        fanucRobotCommunication.writeValues(RobotConstants.COMMAND_WRITE_SPECIAL_POINT, RobotConstants.RESPONSE_WRITE_SPECIAL_POINT, WRITE_VALUES_TIMEOUT, values);
    }

    @Override
    public void readSpecialPoint(RobotSpecialPoint specialPoint) throws AbstractCommunicationException, RobotActionException, InterruptedException {
        List<String> values = new ArrayList<String>();
        values.add("" + specialPoint.getId());
        List<String> result = fanucRobotCommunication.readValues(RobotConstants.COMMAND_READ_SPECIAL_POINT, RobotConstants.RESPONSE_READ_SPECIAL_POINT, ASK_POSITION_TIMEOUT, values);
        // index 0 is responseId, index 1 is specialId
        float x = Float.parseFloat(result.get(2));
        float y = Float.parseFloat(result.get(3));
        float z = Float.parseFloat(result.get(4));
        float w = Float.parseFloat(result.get(5));
        float p = Float.parseFloat(result.get(6));
        float r = Float.parseFloat(result.get(7));
        Coordinates coord = new Coordinates(x,y,z,w,p,r);
        int cfg1 = Integer.parseInt(result.get(8));
        int cfg2 = Integer.parseInt(result.get(9));
        int cfg3 = Integer.parseInt(result.get(10));
        int cfg4 = Integer.parseInt(result.get(11));
        int cfg5 = Integer.parseInt(result.get(12));
        int cfg6 = Integer.parseInt(result.get(13));
        Config config = new Config(cfg1, cfg2, cfg3, cfg4, cfg5, cfg6);
        RobotDataManager.addSpecialPoint(specialPoint, new RobotPosition(coord, config));
        logger.debug("read " + specialPoint.toString());
    }	
}
