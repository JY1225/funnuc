package eu.robojob.millassist.external.device.stacking.conveyor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Callable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.robojob.millassist.external.communication.AbstractCommunicationException;
import eu.robojob.millassist.external.communication.socket.SocketConnection;
import eu.robojob.millassist.external.communication.socket.SocketDisconnectedException;
import eu.robojob.millassist.external.communication.socket.SocketResponseTimedOutException;
import eu.robojob.millassist.external.device.Clamping;
import eu.robojob.millassist.external.device.ClampingManner;
import eu.robojob.millassist.external.device.DeviceActionException;
import eu.robojob.millassist.external.device.DeviceInterventionSettings;
import eu.robojob.millassist.external.device.DevicePickSettings;
import eu.robojob.millassist.external.device.DevicePutSettings;
import eu.robojob.millassist.external.device.DeviceSettings;
import eu.robojob.millassist.external.device.DeviceType;
import eu.robojob.millassist.external.device.WorkArea;
import eu.robojob.millassist.external.device.Zone;
import eu.robojob.millassist.external.device.stacking.AbstractStackingDevice;
import eu.robojob.millassist.external.device.stacking.IncorrectWorkPieceDataException;
import eu.robojob.millassist.external.device.stacking.StackingPosition;
import eu.robojob.millassist.positioning.Coordinates;
import eu.robojob.millassist.process.ProcessFlow;
import eu.robojob.millassist.threading.ThreadManager;
import eu.robojob.millassist.workpiece.WorkPiece;
import eu.robojob.millassist.workpiece.WorkPiece.Material;
import eu.robojob.millassist.workpiece.WorkPiece.Type;
import eu.robojob.millassist.workpiece.WorkPieceDimensions;

public class Conveyor extends AbstractStackingDevice {
	
	private WorkPiece rawWorkPiece;
	private WorkPiece finishedWorkPiece;
	private WorkArea rawWorkArea;
	private WorkArea finishedWorkArea;
	private int lastFinishedWorkPieceIndex;
	private float nomSpeed1;
	private float nomSpeed2;
	private List<Integer> sensorValues;
	private int amount;
	private boolean statusChanged;
	private Object syncObject;
	private boolean stopAction;
	
	private float workPieceShift;
	
	private List<ConveyorListener> listeners;
	private int currentStatus;
	private Set<ConveyorAlarm> currentAlarms;
	
	private ConveyorAlarm conveyorTimeout;
	private ConveyorLayout layout;
	
	private ConveyorSocketCommunication socketCommunication;
	
	private static Logger logger = LogManager.getLogger(Conveyor.class.getName());
	
	private static final String EXCEPTION_DISCONNECTED_WHILE_WAITING = "Conveyor.disconnectedWhileWaiting";
	private static final String EXCEPTION_WHILE_WAITING = "Conveyor.exceptionWhileWaiting";
				
	public Conveyor(final String name, final Set<Zone> zones, final WorkArea rawWorkArea, final WorkArea finishedWorkArea, 
			final ConveyorLayout layout, final SocketConnection socketConnection, final float nomSpeed1, final float nomSpeed2) {
		super(name, zones);
		this.rawWorkArea = rawWorkArea;
		this.finishedWorkArea = finishedWorkArea;
		this.statusChanged = false;
		this.syncObject = new Object();
		this.stopAction = false;
		this.lastFinishedWorkPieceIndex = 0;
		this.layout = layout;
		layout.setParent(this);
		this.nomSpeed1 = nomSpeed1;
		this.currentAlarms = new HashSet<ConveyorAlarm>();
		this.nomSpeed2 = nomSpeed2;
		this.listeners = new ArrayList<ConveyorListener>();
		this.sensorValues = new ArrayList<Integer>();
		this.socketCommunication = new ConveyorSocketCommunication(socketConnection, this);
		this.workPieceShift = 20;
		ConveyorMonitoringThread monitoringThread = new ConveyorMonitoringThread(this);
		ThreadManager.submit(monitoringThread);
	}
	
	public Conveyor(final String name, final WorkArea rawWorkArea, final WorkArea finishedWorkArea, final ConveyorLayout layout, 
			final SocketConnection socketConnection, final float nomSpeed1, final float nomSpeed2) {
		this(name, new HashSet<Zone>(), rawWorkArea, finishedWorkArea, layout, socketConnection, nomSpeed1, nomSpeed2);
	}	
	
	public ConveyorLayout getLayout() {
		return layout;
	}
	
	public void updateStatusAndAlarms() throws AbstractCommunicationException, InterruptedException {
		int statusInt = (socketCommunication.readRegisters(ConveyorConstants.STATUS_REG, 1)).get(0);
		setStatus(statusInt);
		List<Integer> alarmInts = socketCommunication.readRegisters(ConveyorConstants.ALARMS_REG, 1);
		int alarmReg1 = alarmInts.get(0);
		setAlarms(ConveyorAlarm.parseConveyorAlarms(alarmReg1, statusInt, getConveyorTimeout()));
		this.sensorValues = socketCommunication.readRegisters(ConveyorConstants.SENSOR_1_REG, 4);
		//TODO what if more or less supports?
		Boolean[] currentSupportStatus = new Boolean[3];
		currentSupportStatus[0] = (statusInt & ConveyorConstants.SUPPORT_1_STATUS) > 0; 
		currentSupportStatus[1] = (statusInt & ConveyorConstants.SUPPORT_2_STATUS) > 0;
		currentSupportStatus[2] = (statusInt & ConveyorConstants.SUPPORT_3_STATUS) > 0;
		layout.setCurrentSupportStatus(currentSupportStatus);
	}
	
	public void configureSupports() {
		try {
			configureSupports(layout.getRequestedSupportStatus());
		} catch (SocketResponseTimedOutException | SocketDisconnectedException
				| InterruptedException e) {
			e.printStackTrace();
			logger.error(e);
		}
	}
	
	public boolean isRawConveyorEmpty() {
		for (ConveyorAlarm alarm : getAlarms()) {
			if (alarm.getId() == ConveyorAlarm.ALR_RAW_CONV_EMPTY) {
				return true;
			}
		}
		return false;
	}
	
	public void allSupportsDown() {
		Boolean[] allDown = new Boolean[3];
		Arrays.fill(allDown, false);
		try {
			configureSupports(allDown);
		} catch (SocketResponseTimedOutException | SocketDisconnectedException
				| InterruptedException e) {
			e.printStackTrace();
			logger.error(e);
		}
	}
	
	private void configureSupports(final Boolean[] requestedSupportState) throws SocketResponseTimedOutException, SocketDisconnectedException, InterruptedException {
		int command = 0;
		command = command | ConveyorConstants.UPDATE_SUPPORTS;
		if (requestedSupportState[0]) {
			command = command | ConveyorConstants.SUPPORT_1_REQ_VAL;
		}
		if (requestedSupportState[1]) {
			command = command | ConveyorConstants.SUPPORT_2_REQ_VAL;
		}
		if (requestedSupportState[2]) {
			command = command | ConveyorConstants.SUPPORT_3_REQ_VAL;
		}
		int[] values = new int[1];
		values[0] = command;
		socketCommunication.writeRegisters(ConveyorConstants.COMMAND_REG, values);
	}
	
	@Override
	public void clearDeviceSettings() {
		this.rawWorkPiece = new WorkPiece(WorkPiece.Type.RAW, new WorkPieceDimensions(), Material.OTHER, 0.0f);
		this.finishedWorkPiece = new WorkPiece(WorkPiece.Type.FINISHED, new WorkPieceDimensions(), Material.OTHER, 0.0f);
		layout.clearSettings();
		notifyLayoutChanged();
	}
	
	public float getNomSpeedRawConveyor() {
		return nomSpeed1;
	}
	
	public float getNomSpeedFinishedConveyor() {
		return nomSpeed2;
	}
	
	public void disconnect() {
		socketCommunication.disconnect();
	}
	
	public List<Integer> getSensorValues() {
		return sensorValues;
	}
	
	public void setStatus(final int status) {
		this.currentStatus = status;
	}
	
	public void setAlarms(final Set<ConveyorAlarm> alarms) {
		this.currentAlarms = alarms;
	}
	
	public int getStatus() {
		return currentStatus;
	}

	public Set<ConveyorAlarm> getAlarms() {
		return currentAlarms;
	}

	public ConveyorAlarm getConveyorTimeout() {
		return conveyorTimeout;
	}
	
	public boolean isModeAuto() {
		return (currentStatus & ConveyorConstants.MODE) > 0;
	}

	public WorkArea getRawWorkArea() {
		return rawWorkArea;
	}

	public WorkArea getFinishedWorkArea() {
		return finishedWorkArea;
	}

	public boolean isInterlockRaw() {
		return (currentStatus & ConveyorConstants.CONV_RAW_INTERLOCK) > 0;
	}
	
	public boolean isInterlockFinished() {
		return (currentStatus & ConveyorConstants.CONV_FINISHED_INTERLOCK) > 0;
	}
	
	public boolean isMovingRaw() {
		return (currentStatus & ConveyorConstants.CONV_RAW_MOV) > 0;
	}

	public boolean isMovingFinished() {
		return (currentStatus & ConveyorConstants.CONV_FINISHED_MOV) > 0;
	}
	
	@Override public void reset() throws AbstractCommunicationException, DeviceActionException, InterruptedException { 
		int command = 0;
		command = command | ConveyorConstants.RESET_ALARMS;
		int[] values = {command};
		socketCommunication.writeRegisters(ConveyorConstants.COMMAND_REG, values);
	}
	
	@Override
	public void prepareForProcess(final ProcessFlow process) throws AbstractCommunicationException, InterruptedException {
		writeRawWorkPieceLength();
		writeFinishedWorkPieceLength();
	}
	
	public void writeRawWorkPieceLength() throws SocketResponseTimedOutException, SocketDisconnectedException, InterruptedException {
		int workPieceLength = (int) Math.round(rawWorkPiece.getDimensions().getLength());
		workPieceLength +=  workPieceShift;
		int[] length = {workPieceLength};
		socketCommunication.writeRegisters(ConveyorConstants.LENGTH_WP_RAW, length);
	}
	
	public void writeFinishedWorkPieceLength() throws SocketResponseTimedOutException, SocketDisconnectedException, InterruptedException {
		int workPieceLength = (int) Math.round(finishedWorkPiece.getDimensions().getLength());
		workPieceLength +=  workPieceShift;
		int[] length = {workPieceLength};
		socketCommunication.writeRegisters(ConveyorConstants.LENGTH_WP_FINISHED_SHIFT, length);
	}
	
	public float getWorkPieceShift() {
		return workPieceShift;
	}

	@Override
	public boolean canPick(final DevicePickSettings pickSettings) throws AbstractCommunicationException, DeviceActionException {
		// only if mode = auto, work piece in position and interlock
		return (((currentStatus & ConveyorConstants.RAW_WP_IN_POSITION) > 0) && isModeAuto());
	}

	@Override
	public boolean canPut(final DevicePutSettings putSettings) throws AbstractCommunicationException, DeviceActionException,
			InterruptedException {
		boolean noSpace = false;
		for (ConveyorAlarm alarm : currentAlarms) {
			if (alarm.getId() == ConveyorAlarm.ALR_FINISHED_CONV_FULL) {
				if (lastFinishedWorkPieceIndex >= layout.getStackingPositionsFinishedWorkPieces().size() - 1) {
					noSpace = true;
				}
			}
		}
		return (isModeAuto() && isInterlockFinished() && !noSpace);
	}

	@Override
	public boolean canIntervention(final DeviceInterventionSettings interventionSettings) throws AbstractCommunicationException, DeviceActionException {
		if (interventionSettings.getWorkArea().equals(rawWorkArea)) {
			return isInterlockRaw();
		} else if (interventionSettings.getWorkArea().equals(finishedWorkArea)) {
			return isInterlockFinished();
		} else {
			throw new IllegalArgumentException("Illegal workarea: " + interventionSettings.getWorkArea());
		}
	}

	@Override
	public void prepareForPick(final DevicePickSettings pickSettings) throws AbstractCommunicationException, DeviceActionException,
			InterruptedException {
		// wait until workpiece in position, obtain interlock
		logger.info("Waiting for raw work piece in position status and mode = auto.");
		waitForStatus((ConveyorConstants.RAW_WP_IN_POSITION | ConveyorConstants.MODE));
		logger.info("Work piece in position and mode = auto.");
		int command = 0;
		command = command | ConveyorConstants.RQST_INTERLOCK_RAW;
		int[] commandReg = {command};
		logger.info("Sending interlock raw conveyor command.");
		socketCommunication.writeRegisters(ConveyorConstants.COMMAND_REG, commandReg);
		logger.info("Waiting for confirmation raw conveyor interlock.");
		waitForStatus(ConveyorConstants.CONV_RAW_INTERLOCK);
		logger.info("Obtained interlock, prepare for pick is ready");		
	}

	@Override
	public void prepareForPut(final DevicePutSettings putSettings) throws AbstractCommunicationException, DeviceActionException, InterruptedException {
		// wait until the finished conveyor is not moving
		logger.info("Checking finished conveyor is not moving, if so, wait until stopped.");
		waitForStatusNot((ConveyorConstants.CONV_FINISHED_MOV));
		logger.info("Finished conveyor is not moving, wait for mode = auto.");
		waitForStatus(ConveyorConstants.MODE);
		logger.info("Mode = auto, so we can continue.");
		int command = 0;
		command = command | ConveyorConstants.RQST_INTERLOCK_FINISHED;
		int[] commandReg = {command};
		logger.info("Sending interlock finished conveyor command.");
		socketCommunication.writeRegisters(ConveyorConstants.COMMAND_REG, commandReg);
		logger.info("Waiting for confirmation finished conveyor interlock.");
		waitForStatus(ConveyorConstants.CONV_FINISHED_INTERLOCK);
		logger.info("Obtained interlock, prepare for put is ready");		
	}

	@Override public void prepareForIntervention(final DeviceInterventionSettings interventionSettings) throws AbstractCommunicationException, DeviceActionException, InterruptedException {
		// obtain interlock of correct work area, and wait until not moving
		if (interventionSettings.getWorkArea().equals(rawWorkArea)) {
			waitForStatusNot((ConveyorConstants.CONV_RAW_MOV));
			waitForStatus(ConveyorConstants.MODE);
			int command = 0;
			command = command | ConveyorConstants.RQST_INTERLOCK_RAW;
			int[] commandReg = {command};
			socketCommunication.writeRegisters(ConveyorConstants.COMMAND_REG, commandReg);
			waitForStatus(ConveyorConstants.CONV_RAW_INTERLOCK);
		} else if (interventionSettings.getWorkArea().equals(finishedWorkArea)) {
			waitForStatusNot((ConveyorConstants.CONV_FINISHED_MOV));
			waitForStatus(ConveyorConstants.MODE);
			int command = 0;
			command = command | ConveyorConstants.RQST_INTERLOCK_FINISHED;
			int[] commandReg = {command};
			socketCommunication.writeRegisters(ConveyorConstants.COMMAND_REG, commandReg);
			waitForStatus(ConveyorConstants.CONV_FINISHED_INTERLOCK);
		} else {
			throw new IllegalArgumentException("Illegal workarea");
		}
	}

	@Override
	public void pickFinished(final DevicePickSettings pickSettings) throws AbstractCommunicationException, DeviceActionException,
			InterruptedException {
		// release interlock
		int command = 0;
		command = command | ConveyorConstants.RELEASE_INTERLOCK_RAW;
		int[] commandReg = {command};
		socketCommunication.writeRegisters(ConveyorConstants.COMMAND_REG, commandReg);
		for (ConveyorListener listener : listeners) {
			listener.layoutChanged();
		}
	}

	@Override
	public void putFinished(final DevicePutSettings putSettings) throws AbstractCommunicationException, DeviceActionException,
			InterruptedException {
		// release interlock
		int command = 0;
		command = command | ConveyorConstants.RELEASE_INTERLOCK_FINISHED;
		int[] commandReg = {command};
		socketCommunication.writeRegisters(ConveyorConstants.COMMAND_REG, commandReg);
		// if last piece: do shift
		if (lastFinishedWorkPieceIndex == layout.getStackingPositionsFinishedWorkPieces().size() - 1) {
			waitForStatusNot(ConveyorConstants.CONV_FINISHED_INTERLOCK);
			// just to be sure, write the finished work piece shift length
			//writeFinishedWorkPieceLength();
			int command2 = 0;
			command2 = command2 | ConveyorConstants.SHIFT_FINISHED_WP;
			int[] commandReg2 = {command2};
			logger.info("$$$Writing shift command: " + command2);
			socketCommunication.writeRegisters(ConveyorConstants.COMMAND_REG, commandReg2);
			//FIXME for now we don't wait for a confirmation!
			lastFinishedWorkPieceIndex = 0;
			layout.shiftFinishedWorkPieces();
		} else {
			lastFinishedWorkPieceIndex++;
		}		
	}

	@Override
	public void interventionFinished(final DeviceInterventionSettings interventionSettings) throws AbstractCommunicationException, DeviceActionException, InterruptedException {
		// release interlock
		if (interventionSettings.getWorkArea().equals(rawWorkArea)) {
			int command = 0;
			command = command | ConveyorConstants.RELEASE_INTERLOCK_RAW;
			int[] commandReg = {command};
			socketCommunication.writeRegisters(ConveyorConstants.COMMAND_REG, commandReg);
		} else if (interventionSettings.getWorkArea().equals(finishedWorkArea)) {
			int command = 0;
			command = command | ConveyorConstants.RELEASE_INTERLOCK_FINISHED;
			int[] commandReg = {command};
			socketCommunication.writeRegisters(ConveyorConstants.COMMAND_REG, commandReg);
		} else {
			throw new IllegalStateException("Illegal workarea");
		}
	}

	@Override public void releasePiece(final DevicePickSettings pickSettings) throws AbstractCommunicationException, DeviceActionException, InterruptedException { }
	@Override public void grabPiece(final DevicePutSettings putSettings) throws AbstractCommunicationException, DeviceActionException, InterruptedException { 
		layout.setFinishedStackingPositionWorkPiece(lastFinishedWorkPieceIndex, true);
		notifyLayoutChanged();
	}

	@Override
	public void loadDeviceSettings(final DeviceSettings deviceSettings) {
		for (Entry<WorkArea, Clamping> entry : deviceSettings.getClampings().entrySet()) {
			entry.getKey().setActiveClamping(entry.getValue());
		}
		if (deviceSettings instanceof ConveyorSettings) {
			ConveyorSettings settings = (ConveyorSettings) deviceSettings;
			if (settings.getRawWorkPiece() != null) {
				this.rawWorkPiece = settings.getRawWorkPiece();
			}
			if (settings.getFinishedWorkPiece() != null) {
				this.finishedWorkPiece = settings.getFinishedWorkPiece();
			}
			layout.setOffsetSupport1(settings.getOffsetSupport1());
			layout.setOffsetOtherSupports(settings.getOffsetOtherSupports());
			this.amount = settings.getAmount();
			try {
				layout.configureRawWorkPieceStackingPositions();
			} catch (IncorrectWorkPieceDataException e) {
				e.printStackTrace();
			}
		}
	}

	protected boolean waitForStatusCondition(final Callable<Boolean> condition, final long timeout) throws InterruptedException, DeviceActionException {
		long waitedTime = 0;
		stopAction = false;
		// check status before we start
		try {
			if (condition.call()) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new DeviceActionException(this, EXCEPTION_WHILE_WAITING);
		}
		// also check connection status
		if (!isConnected()) {
			throw new DeviceActionException(this, EXCEPTION_DISCONNECTED_WHILE_WAITING);
		}
		while ((timeout == 0) || ((timeout > 0) && (waitedTime < timeout))) {
			// start waiting
			statusChanged = false;
			if ((timeout == 0) || ((timeout > 0) && (timeout > waitedTime))) {
				long timeBeforeWait = System.currentTimeMillis();
				synchronized (syncObject) {
					try {
						if (condition.call()) {
							return true;
						}
					} catch (Exception e) {
						e.printStackTrace();
						throw new DeviceActionException(this, EXCEPTION_WHILE_WAITING);
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
					throw new InterruptedException("Waiting for status got interrupted");
				}
				// just to be sure, check connection
				if (!isConnected()) {
					throw new DeviceActionException(this, EXCEPTION_DISCONNECTED_WHILE_WAITING);
				}
				// check if status has changed
				try {
					if ((statusChanged) && (condition.call())) {
						statusChanged = false;
						return true;
					}
				} catch (Exception e) {
					e.printStackTrace();
					throw new DeviceActionException(this, EXCEPTION_WHILE_WAITING);
				}
				// update waited time
				waitedTime += System.currentTimeMillis() - timeBeforeWait;
			} else {
				return false;
			}
		} 
		return false;
	}
	
	protected void waitForStatus(final int status) throws DeviceActionException, InterruptedException {
		waitForStatus(status, 0);
	}
	
	protected boolean waitForStatus(final int status, final long timeout) throws InterruptedException, DeviceActionException {
		return waitForStatusCondition(new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				return ((currentStatus & status) > 0);
			}
		}, timeout);
	}
	
	protected void waitForStatusNot(final int statusNot) throws DeviceActionException, InterruptedException {
		waitForStatusNot(statusNot, 0);
	}
	
	protected boolean waitForStatusNot(final int statusNot, final long timeout) throws InterruptedException, DeviceActionException {
		return waitForStatusCondition(new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				return ((currentStatus & statusNot) == 0);
			}
		}, timeout);
	}
	
	@Override
	public DeviceSettings getDeviceSettings() {
		return new ConveyorSettings(rawWorkPiece, finishedWorkPiece, amount, layout.getOffsetSupport1(), layout.getOffsetOtherSupports());
	}

	@Override
	public Coordinates getPickLocation(final WorkArea workArea, final ClampingManner clampType) throws DeviceActionException, InterruptedException {
		if (!workArea.equals(rawWorkArea)) {
			throw new IllegalStateException("Can only pick from raw conveyor");
		}
		// wait until workpiece in position
		logger.info("Waiting for raw work piece in position status.");
		waitForStatus((ConveyorConstants.RAW_WP_IN_POSITION));
		logger.info("Work piece in position.");
		// get lowest non-zero sensor value
		int sensorValue = Integer.MAX_VALUE;
		int sensorIndex = -1;
		int validIndex = 0;
		for (int i = 0; i < sensorValues.size(); i++) {
			if ((i == 0) || ((i > 0) && (layout.getRequestedSupportStatus()[i-1]))) {
				logger.info("OK: " + i);
				if ((sensorValues.get(i) < sensorValue) && (sensorValues.get(i) > 0)) {
					sensorIndex = validIndex;
					sensorValue = sensorValues.get(i);
				}
				validIndex++;
			}
		}
		if (sensorIndex == -1) {
			throw new IllegalStateException("Couldn't find a stacking position.");
		}
		StackingPosition stPos = layout.getStackingPositionsRawWorkPieces().get(sensorIndex);
		Coordinates c = new Coordinates(stPos.getPosition());
		c.setX((((float) sensorValue)/100) + stPos.getWorkPiece().getDimensions().getLength()/2);
		logger.info("Pick location at sensor: " + sensorIndex + " - coordinates: " + c);
		return c;
	}

	@Override
	public Coordinates getPutLocation(final WorkArea workArea, final WorkPieceDimensions workPieceDimensions, 
			final ClampingManner clampType) {
		return layout.getStackingPositionsFinishedWorkPieces().get(lastFinishedWorkPieceIndex).getPosition();
	}

	@Override
	public Coordinates getLocation(final WorkArea workArea, final Type type, final ClampingManner clampType) throws DeviceActionException, InterruptedException {
		if (type == Type.FINISHED) {
			return layout.getStackingPositionsFinishedWorkPieces().get(lastFinishedWorkPieceIndex).getPosition();
		} else if (type == Type.RAW) {
			return getPickLocation(workArea, clampType);
		}
		return null;
	}

	@Override
	public void interruptCurrentAction() {
		// no action possible
	}

	@Override
	public boolean isConnected() {
		return socketCommunication.isConnected();
	}
	
	@Override
	public DeviceType getType() {
		return DeviceType.CONVEYOR;
	}
	
	public void notifyLayoutChanged() {
		for (ConveyorListener listener : listeners) {
			listener.layoutChanged();
		}
	}
	
	public void notifyFinishedShifted() {
		for (ConveyorListener listener : listeners) {
			listener.finishedShifted(finishedWorkPiece.getDimensions().getLength() + workPieceShift);
		}
	}
	
	public void addListener(final ConveyorListener listener) {
		listeners.add(listener);
		logger.debug("Now listening to [" + toString() + "]: " + listener.toString());
	}
	
	public void removeListener(final ConveyorListener listener) {
		listeners.remove(listener);
		logger.debug("Stopped listening to [" + toString() + "]: " + listener.toString());
	}
	
	public void clearListeners() {
		listeners.clear();
	}

	public WorkPiece getRawWorkPiece() {
		return rawWorkPiece;
	}

	public void setRawWorkPiece(final WorkPiece rawWorkPiece) {
		this.rawWorkPiece = rawWorkPiece;
	}

	public WorkPiece getFinishedWorkPiece() {
		return finishedWorkPiece;
	}

	public void setFinishedWorkPiece(final WorkPiece finishedWorkPiece) {
		this.finishedWorkPiece = finishedWorkPiece;
	}
	
	public void setConveyorTimeout(final ConveyorAlarm conveyorTimeout) {
		this.conveyorTimeout = conveyorTimeout;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(final int amount) {
		this.amount = amount;
	}

	public void processConveyorEvent(final ConveyorEvent event) {
		switch(event.getId()) {
			case ConveyorEvent.ALARM_OCCURED:
				for (ConveyorListener listener : listeners) {
					listener.conveyorAlarmsOccured((ConveyorAlarmsOccuredEvent) event);
				}
				break;
			case ConveyorEvent.CONVEYOR_CONNECTED:
				for (ConveyorListener listener : listeners) {
					listener.conveyorConnected(event);
				}
				break;
			case ConveyorEvent.CONVEYOR_DISCONNECTED:
				statusChanged();
				for (ConveyorListener listener : listeners) {
					listener.conveyorDisconnected(event);
				}
				break;
			case ConveyorEvent.STATUS_CHANGED:
				statusChanged();
				for (ConveyorListener listener : listeners) {
					listener.conveyorStatusChanged(event);
				}
				break;
			case ConveyorEvent.SENSOR_VALUES_CHANGED:
				for (ConveyorListener listener : listeners) {
					listener.sensorValuesChanged((ConveyorSensorValuesChangedEvent) event);
				}
				break;
			default:
				throw new IllegalArgumentException("Unknown event type: " + event.getId());
		}
	}
	
	@Override
	public String toString() {
		return "Conveyor: " + getName();
	}
	
	/**
	 * This method will be called after processing a STATUS_CHANGED event, so if the waitForStatus method
	 * is waiting, it will be notified
	 */
	private void statusChanged() {
		synchronized (syncObject) {
			statusChanged = true;
			syncObject.notifyAll();
		}
	}

}
