package eu.robojob.irscw.external.robot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;

import eu.robojob.irscw.external.communication.CommunicationException;
import eu.robojob.irscw.external.communication.DisconnectedException;
import eu.robojob.irscw.external.communication.ResponseTimedOutException;
import eu.robojob.irscw.external.communication.SocketConnection;
import eu.robojob.irscw.external.device.WorkArea;
import eu.robojob.irscw.positioning.Coordinates;
import eu.robojob.irscw.threading.ThreadManager;
import eu.robojob.irscw.workpiece.WorkPiece;
import eu.robojob.irscw.workpiece.WorkPieceDimensions;

public class FanucRobot extends AbstractRobot {

	private FanucRobotCommunication fanucRobotCommunication;
	
	private static final int WRITE_VALUES_TIMEOUT = 5000;
	private static final int PICK_TO_LOCATION_TIMEOUT = 100000;
	private static final int PICK_FINISH_TIMEOUT = 100000;
	private static final int ASK_POSITION_TIMEOUT = 50000;
	private static final int PICK_TEACH_TIMEOUT = 10*60*1000;
	private static final int PUT_TEACH_TIMEOUT = 10*60*1000;
	private static final int ASK_STATUS_TIMEOUT = 1*60*1000;
	
	private static final int TO_HOME_TIMEOUT = 100000;
	private static final int TO_JAW_CHANGE_TIMEOUT = 100000;
	
	private Set<FanucRobotListener> listeners;
	
	private FanucRobotStatus status;
	
	private static Logger logger = Logger.getLogger(FanucRobot.class);
	
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
	}
	
	public FanucRobot(String id, SocketConnection socketConnection) {
		this(id, null, null, socketConnection);
	}
	
	public void updateStatus() throws CommunicationException {
		List<String> values = fanucRobotCommunication.readValues(FanucRobotConstants.COMMAND_ASK_STATUS, FanucRobotConstants.RESPONSE_ASK_STATUS, ASK_STATUS_TIMEOUT);
		int errorId = Integer.parseInt(values.get(0));
		int controllerValue = Integer.parseInt(values.get(1));
		int controllerString = Integer.parseInt(values.get(2));
		double zrest = Float.parseFloat(values.get(3));
		this.status = new FanucRobotStatus(errorId, controllerValue, controllerString, zrest);
	}
	
	public synchronized FanucRobotStatus getStatus() {
		return status;
	}
	
	public void addListener(FanucRobotListener listener) {
		listeners.add(listener);
	}
	
	public void removeListener(FanucRobotListener listener) {
		listeners.remove(listener);
	}
	
	public void processFanucRobotEvent(FanucRobotEvent event) {
		System.out.println("processing new event!");
		switch(event.getId()) {
			case FanucRobotEvent.ROBOT_CONNECTED:
				System.out.println("CONNECTED: " + toString());
				for (FanucRobotListener listener : listeners) {
					listener.robotConnected(event);
				}
				break;
			case FanucRobotEvent.ROBOT_DISCONNECTED:
				System.out.println("DISCONNECTED: " + toString());
				for (FanucRobotListener listener : listeners) {
					listener.robotDisconnected(event);
				}
				break;
			case FanucRobotEvent.ALARMS_OCCURED:
				System.out.println("ALARMS CHANGED: " + ((FanucRobotAlarmsOccuredEvent) event).getAlarms());
				for (FanucRobotListener listener : listeners) {
					listener.robotAlarmsOccured((FanucRobotAlarmsOccuredEvent) event);
				}
				break;
			case FanucRobotEvent.STATUS_CHANGED:
				System.out.println("STATUS CHANGED: " +  toString() + " - " + ((FanucRobotStatusChangedEvent) event).toString());
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
	
	private boolean waitForStatus(int status, long timeout) {
		long waitedTime = 0;
		do {
			long lastTime = System.currentTimeMillis();
			if ((getStatus().getControllerString() & status) > 0) {
				return true;
			} else {
				try {
					statusChanged = false;
					if (timeout > waitedTime) {
						synchronized(syncObject) {
							syncObject.wait(timeout - waitedTime);
						}
					}
				} catch (InterruptedException e) {
					if (!statusChanged) {
						break;
					}
				} 
				if (statusChanged == true) {
					waitedTime += System.currentTimeMillis() - lastTime;
					if ((getStatus().getControllerString() & status) > 0) {
						return true;
					}
				}
			}
		} while (waitedTime < timeout);
		return false;
	}

	@Override
	public void setSpeed(int speedPercentage) throws CommunicationException {
		super.setSpeed(speedPercentage);
		fanucRobotCommunication.writeValue(FanucRobotConstants.COMMAND_SET_SPEED, FanucRobotConstants.RESPONSE_SET_SPEED, WRITE_VALUES_TIMEOUT, speedPercentage + "");
	}
	
	@Override
	public Coordinates getPosition() throws CommunicationException, RobotActionException {
		Coordinates position = fanucRobotCommunication.getPosition(ASK_POSITION_TIMEOUT);
		return position;
	}
	
	public synchronized void disconnect() {
		fanucRobotCommunication.disconnect();
	}
	
	@Override
	public void restartProgram() throws CommunicationException {
		// write start service
		fanucRobotCommunication.writeCommand(FanucRobotConstants.COMMAND_RESTART_PROGRAM, FanucRobotConstants.RESPONSE_RESTART_PROGRAM, WRITE_VALUES_TIMEOUT);
	}

	@Override
	public void initiatePick(AbstractRobotPickSettings pickSettings) throws CommunicationException, RobotActionException {
		FanucRobotPickSettings fPickSettings = (FanucRobotPickSettings) pickSettings;		
		// write service gripper set
		writeServiceGripperSet(fPickSettings.getGripperHead(), fPickSettings.getGripper(), FanucRobotConstants.SERVICE_GRIPPER_SERVICE_TYPE_PICK);
		// write service handling set
		writeServiceHandlingSet(FanucRobotConstants.SERVICE_HANDLING_PP_MODE_ORDER_12);
		// write service point set
		writeServicePointSet(fPickSettings.getWorkArea(), fPickSettings.getLocation(), fPickSettings.getSmoothPoint(), fPickSettings.getWorkPiece().getDimensions(), fPickSettings.getClampHeight());
		// write command
		writeCommand(FanucRobotConstants.PERMISSIONS_COMMAND_PICK);
		// write start service
		fanucRobotCommunication.writeValue(FanucRobotConstants.COMMAND_START_SERVICE, FanucRobotConstants.RESPONSE_START_SERVICE, WRITE_VALUES_TIMEOUT, "1");
		// we now wait for the robot to indicate he moved to its location
		boolean waitingForRelease = waitForStatus(FanucRobotConstants.STATUS_PICK_RELEASE_REQUEST, PICK_TO_LOCATION_TIMEOUT);
		if (!waitingForRelease) {
			logger.info("Troubles!");
			throw new RobotActionException();
		}
	}
	
	@Override
	public void initiatePut(AbstractRobotPutSettings putSettings) throws CommunicationException, RobotActionException {
		FanucRobotPutSettings fPutSettings = (FanucRobotPutSettings) putSettings;
		// write service gripper set
		writeServiceGripperSet(fPutSettings.getGripperHead(), fPutSettings.getGripper(), FanucRobotConstants.SERVICE_GRIPPER_SERVICE_TYPE_PUT);
		// write service handling set
		writeServiceHandlingSet(FanucRobotConstants.SERVICE_HANDLING_PP_MODE_ORDER_12);
		// write service point set
		if (fPutSettings.getGripper().getWorkPiece() == null) {
			throw new IllegalStateException("When executing put, the gripper should contain a workpiece");
		}
		writeServicePointSet(fPutSettings.getWorkArea(), fPutSettings.getLocation(), fPutSettings.getSmoothPoint(), fPutSettings.getGripper().getWorkPiece().getDimensions(), fPutSettings.getClampHeight());
		// write command
		writeCommand(FanucRobotConstants.PERMISSIONS_COMMAND_PUT);
		//TODO in progress
		fanucRobotCommunication.writeValue(FanucRobotConstants.COMMAND_START_SERVICE, FanucRobotConstants.RESPONSE_START_SERVICE, WRITE_VALUES_TIMEOUT, "1");
		boolean waitingForRelease = waitForStatus(FanucRobotConstants.STATUS_PUT_CLAMP_REQUEST, PICK_TO_LOCATION_TIMEOUT);
		if (!waitingForRelease) {
			logger.info("Troubles!");
			throw new RobotActionException();
		}
	}

	@Override
	public void finalizePut(AbstractRobotPutSettings putSettings) throws CommunicationException, RobotActionException {
		writeCommand(FanucRobotConstants.PERMISSIONS_COMMAND_PUT_CLAMP_ACK);
		logger.info("waiting for put to finish!");
		boolean waitingForPickFinished = waitForStatus(FanucRobotConstants.STATUS_PUT_FINISHED, PICK_FINISH_TIMEOUT);
		if (waitingForPickFinished) {
			logger.info("Pick finished!");
			return;
		} else {
			throw new RobotActionException();
		}
	}

	@Override
	public void finalizePick(AbstractRobotPickSettings pickSettings) throws CommunicationException, RobotActionException {
		pickSettings.getGripper().setWorkPiece(pickSettings.getWorkPiece());
		writeCommand(FanucRobotConstants.PERMISSIONS_COMMAND_PICK_RELEASE_ACK);
		logger.info("waiting for pick to finish!");
		boolean waitingForPickFinished = waitForStatus(FanucRobotConstants.STATUS_PICK_FINISHED, PICK_FINISH_TIMEOUT);
		if (waitingForPickFinished) {
			logger.info("Pick finished!");
			return;
		} else {
			throw new RobotActionException();
		}
	}
	
	@Override
	public void initiateTeachedPick(AbstractRobotPickSettings pickSettings)
			throws CommunicationException, RobotActionException {
		FanucRobotPickSettings fPickSettings = (FanucRobotPickSettings) pickSettings;		
		// write service gripper set
		writeServiceGripperSet(fPickSettings.getGripperHead(), fPickSettings.getGripper(), FanucRobotConstants.SERVICE_GRIPPER_SERVICE_TYPE_PICK);
		// write service handling set
		writeServiceHandlingSet(FanucRobotConstants.SERVICE_HANDLING_PP_MODE_TEACH);
		// write service point set
		writeServicePointSet(fPickSettings.getWorkArea(), fPickSettings.getLocation(), fPickSettings.getSmoothPoint(), fPickSettings.getWorkPiece().getDimensions(), fPickSettings.getClampHeight());
		// write command
		writeCommand(FanucRobotConstants.PERMISSIONS_COMMAND_PICK);
		// write start service
		fanucRobotCommunication.writeValue(FanucRobotConstants.COMMAND_START_SERVICE, FanucRobotConstants.RESPONSE_START_SERVICE, WRITE_VALUES_TIMEOUT, "1");
		// we now wait for the robot to indicate he moved to its location
		boolean waitingForTeachingFinished = waitForStatus(FanucRobotConstants.STATUS_PICK_POSITION_TEACHED, PICK_TEACH_TIMEOUT);
		if (!waitingForTeachingFinished) {
			logger.info("Troubles!");
			throw new RobotActionException();
		} else {
			logger.info("TEACHINGOK!");
			boolean waitingForPickFinished = waitForStatus(FanucRobotConstants.STATUS_PICK_RELEASE_REQUEST, PICK_TO_LOCATION_TIMEOUT);
			if (waitingForPickFinished) {
				logger.info("Pick finished!");
				return;
			} else {
				throw new RobotActionException();
			}
		}
	}

	@Override
	public void initiateTeachedPut(AbstractRobotPutSettings putSettings)
			throws CommunicationException, RobotActionException {
		FanucRobotPutSettings fPutSettings = (FanucRobotPutSettings) putSettings;
		// write service gripper set
		writeServiceGripperSet(fPutSettings.getGripperHead(), fPutSettings.getGripper(), FanucRobotConstants.SERVICE_GRIPPER_SERVICE_TYPE_PUT);
		// write service handling set
		writeServiceHandlingSet(FanucRobotConstants.SERVICE_HANDLING_PP_MODE_TEACH);
		// write service point set
		if (fPutSettings.getGripper().getWorkPiece() == null) {
			throw new IllegalStateException("When executing put, the gripper should contain a workpiece");
		}
		writeServicePointSet(fPutSettings.getWorkArea(), fPutSettings.getLocation(), fPutSettings.getSmoothPoint(), fPutSettings.getGripper().getWorkPiece().getDimensions(), fPutSettings.getClampHeight());
		// write command
		writeCommand(FanucRobotConstants.PERMISSIONS_COMMAND_PUT);
		//TODO in progress
		fanucRobotCommunication.writeValue(FanucRobotConstants.COMMAND_START_SERVICE, FanucRobotConstants.RESPONSE_START_SERVICE, WRITE_VALUES_TIMEOUT, "1");
		boolean waitingForRelease = waitForStatus(FanucRobotConstants.STATUS_PUT_POSITION_TEACHED, PUT_TEACH_TIMEOUT);
		if (!waitingForRelease) {
			logger.info("Troubles!");
			throw new RobotActionException();
		}
	}

	@Override
	public void finalizeTeachedPick(AbstractRobotPickSettings pickSettings)
			throws CommunicationException, RobotActionException {
		finalizePick(pickSettings);
	}

	@Override
	public void finalizeTeachedPut(AbstractRobotPutSettings putSettings)
			throws CommunicationException, RobotActionException {
		finalizePut(putSettings);
	}

	private void writeServiceGripperSet(GripperHead gHead, Gripper gripper, int serviceType) throws DisconnectedException, ResponseTimedOutException {
		List<String> values = new ArrayList<String>();
		boolean a = false;
		if (gHead.getId().equals("A")) {
			a = true;
		} else if (gHead.getId().equals("B")) {
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
		if (a) {
			values.add("" + (int) Math.floor(gripper.getHeight()));
			values.add("0");
		} else {
			values.add("0");
			values.add("" + (int) Math.floor(gripper.getHeight()));
		}
		// changing jaws will not be necessary
		values.add("0");
		// outer gripper type will be used
		values.add("1");
		fanucRobotCommunication.writeValues(FanucRobotConstants.COMMAND_WRITE_SERVICE_GRIPPER, FanucRobotConstants.RESPONSE_WRITE_SERVICE_GRIPPER, WRITE_VALUES_TIMEOUT, values);
	}
	
	private void writeServiceHandlingSet(int serviceHandlingPPMode) throws CommunicationException {
		List<String> values = new ArrayList<String>();
		// free after this service ; WP thickness ;  WP Z grip ; grip Z face till front ; dx correction P1 ; dy correction P1 ; dx correction P2 ; dy correction P2 ; dW correction ;
		//    dP correction ; robot speed ; payload 1 ; payload 2 ; soft float range ; soft float force ; PP mode ; bar move distance
		values.add("1");
		values.add("0");
		values.add("0");
		values.add("0");
		values.add("0");
		values.add("0");
		values.add("0");
		values.add("0");
		values.add("0");
		values.add("0");
		if ((getSpeed() < 25) || (getSpeed() > 100)) {
			setSpeed(50);
		}
		values.add(getSpeed() + ""); // robot speed is set to 50 for now! 
		values.add("0");
		values.add("0");
		values.add("0");
		values.add("0");
		values.add("" + serviceHandlingPPMode);
		values.add("0");
		fanucRobotCommunication.writeValues(FanucRobotConstants.COMMAND_WRITE_SERVICE_HANDLING, FanucRobotConstants.RESPONSE_WRITE_SERVICE_HANDLING, WRITE_VALUES_TIMEOUT, values);
	}
	
	private void writeServicePointSet(WorkArea workArea, Coordinates location, Coordinates smoothPoint, WorkPieceDimensions dimensions, float clampHeight) throws DisconnectedException, ResponseTimedOutException {
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
		values.add("" + location.getX());
		values.add("" + location.getY());
		values.add("" + location.getZ());
		values.add("" + location.getR());
		values.add("" + (dimensions.getHeight() + location.getZ()));
		// TODO we take 20 as safety add z for now
		values.add("" + 20);
		values.add("" + smoothPoint.getX());
		values.add("" + smoothPoint.getY());
		values.add("" + smoothPoint.getZ());
		// we take 1 as tangent
		values.add("1");
		// we take xyz allowed as xyz for stacker and xy for machine
		if (userFrameId == 1) {
			values.add("" + FanucRobotConstants.SERVICE_POINT_XYZ_ALLOWED_XYZ);
		} else if (userFrameId == 3) {
			values.add("" + FanucRobotConstants.SERVICE_POINT_XYZ_ALLOWED_XY);
			//values.add("" + FanucRobotConstants.SERVICE_POINT_XYZ_ALLOWED_XYZ);
		} else {
			throw new IllegalStateException("Should not be here! Illegal Userframe id");
		}
		if (clampHeight != Float.NaN) {
			values.add("" + clampHeight);
		} else {
			throw new IllegalArgumentException("Invalid clamp height!");
		}
		values.add("0");
		values.add("0");
		values.add("0");
		values.add("0");
		logger.info("ServicePoint: " + values);
		fanucRobotCommunication.writeValues(FanucRobotConstants.COMMAND_WRITE_SERVICE_POINT, FanucRobotConstants.RESPONSE_WRITE_SERVICE_POINT, WRITE_VALUES_TIMEOUT, values);
	}

	private void writeCommand(int permission) throws DisconnectedException, ResponseTimedOutException {
		// permission
		List<String> values = new ArrayList<String>();
		values.add("" + permission);
		fanucRobotCommunication.writeValues(FanucRobotConstants.COMMAND_SET_PERMISSIONS, FanucRobotConstants.RESPONSE_SET_PERMISSIONS, WRITE_VALUES_TIMEOUT, values);
	}
	
	@Override
	public synchronized void moveToHome() throws CommunicationException, RobotActionException {
		//we now use a speed of 50%
		if ((getSpeed() < 25) || (getSpeed() > 100)) {
			setSpeed(50);
		}
		fanucRobotCommunication.writeValue(FanucRobotConstants.COMMAND_TO_HOME, FanucRobotConstants.RESPONSE_TO_HOME, TO_HOME_TIMEOUT, "" + getSpeed());
		//TODO there's no way of knowing the robot is in its home point, so for now, we just leave him there
	}

	@Override
	public synchronized void moveToChangePoint() throws CommunicationException,
			RobotActionException {
		if ((getSpeed() < 25) || (getSpeed() > 100)) {
			setSpeed(50);
		}
		fanucRobotCommunication.writeValue(FanucRobotConstants.COMMAND_TO_JAW_CHANGE, FanucRobotConstants.RESPONSE_TO_JAW_CHANGE, TO_JAW_CHANGE_TIMEOUT, "" + getSpeed());
	}

	public static class FanucRobotPickSettings extends AbstractRobotPickSettings {

		public FanucRobotPickSettings(WorkArea workArea, GripperHead gripperHead, Gripper gripper, Coordinates smoothPoint, Coordinates location, float clampHeight, WorkPiece workPiece) {
			super(workArea, gripperHead, gripper, smoothPoint, location, clampHeight, workPiece);
		}
		
		public FanucRobotPickSettings() {
			super(null, null, null, null, null, Float.NaN, null);
		}
		
	}
	public static class FanucRobotPutSettings extends AbstractRobotPutSettings {

		public FanucRobotPutSettings(WorkArea workArea, GripperHead gripperHead, Gripper gripper, Coordinates smoothPoint, Coordinates location, float clampHeight) {
			super(workArea, gripperHead, gripper, smoothPoint, location, clampHeight);
		}
		
		public FanucRobotPutSettings() {
			super(null, null, null, null, null, Float.NaN);
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
		return fanucRobotCommunication.isConnected();
	}

}
