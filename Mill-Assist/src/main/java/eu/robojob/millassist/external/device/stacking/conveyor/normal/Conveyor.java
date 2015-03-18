package eu.robojob.millassist.external.device.stacking.conveyor.normal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.robojob.millassist.external.communication.AbstractCommunicationException;
import eu.robojob.millassist.external.communication.socket.SocketConnection;
import eu.robojob.millassist.external.communication.socket.SocketDisconnectedException;
import eu.robojob.millassist.external.communication.socket.SocketResponseTimedOutException;
import eu.robojob.millassist.external.communication.socket.SocketWrongResponseException;
import eu.robojob.millassist.external.device.Clamping;
import eu.robojob.millassist.external.device.ClampingManner;
import eu.robojob.millassist.external.device.DeviceActionException;
import eu.robojob.millassist.external.device.DeviceInterventionSettings;
import eu.robojob.millassist.external.device.DevicePickSettings;
import eu.robojob.millassist.external.device.DevicePutSettings;
import eu.robojob.millassist.external.device.DeviceSettings;
import eu.robojob.millassist.external.device.EDeviceGroup;
import eu.robojob.millassist.external.device.SimpleWorkArea;
import eu.robojob.millassist.external.device.WorkAreaManager;
import eu.robojob.millassist.external.device.Zone;
import eu.robojob.millassist.external.device.stacking.IncorrectWorkPieceDataException;
import eu.robojob.millassist.external.device.stacking.conveyor.ConveyorAlarmsOccuredEvent;
import eu.robojob.millassist.external.device.visitor.AbstractPiecePlacementVisitor;
import eu.robojob.millassist.external.robot.AbstractRobotActionSettings.ApproachType;
import eu.robojob.millassist.positioning.Coordinates;
import eu.robojob.millassist.process.ProcessFlow;
import eu.robojob.millassist.threading.ThreadManager;
import eu.robojob.millassist.util.PropertyManager;
import eu.robojob.millassist.util.PropertyManager.Setting;
import eu.robojob.millassist.workpiece.IWorkPieceDimensions;
import eu.robojob.millassist.workpiece.WorkPiece;
import eu.robojob.millassist.workpiece.WorkPiece.Dimensions;
import eu.robojob.millassist.workpiece.WorkPiece.Material;
import eu.robojob.millassist.workpiece.WorkPiece.Type;
import eu.robojob.millassist.workpiece.WorkPiece.WorkPieceShape;
import eu.robojob.millassist.workpiece.RectangularDimensions;

public class Conveyor extends eu.robojob.millassist.external.device.stacking.conveyor.AbstractConveyor {
	
	private WorkAreaManager rawWorkArea;
	private WorkAreaManager finishedWorkArea;
	private int lastFinishedWorkPieceIndex;
	private float nomSpeed1;
	private float nomSpeed2;
	private List<Integer> sensorValues;
	private int amount;
	private boolean isLeftSetup;
	
	private float workPieceShift;
	
	private List<ConveyorListener> listeners;
	
	private ConveyorLayout layout;
		
	private static Logger logger = LogManager.getLogger(Conveyor.class.getName());
	
	private static final long WAIT_FOR_READY_FOR_CMD_TIMEOUT = 2000;
	private static final long SUPPORTS_SELECT_TIMEOUT = 1000;
	private static final long SUPPORTS_UPDATE_TIMEOUT = 10000;	
	private static final int OPERATOR_RQST_BLUE_LAMP_VAL = 5;
	private static final int FINISH_BLUE_LAMP_VAL = 10;
	
	public enum SupportState {
		UP, DOWN, UNKNOWN;
	}
				
	public Conveyor(final String name, final Set<Zone> zones, final WorkAreaManager rawWorkArea, final WorkAreaManager finishedWorkArea, 
			final ConveyorLayout layout, final SocketConnection socketConnection, final float nomSpeed1, final float nomSpeed2) {
		super(name, zones, socketConnection);
		this.rawWorkArea = rawWorkArea;
		this.finishedWorkArea = finishedWorkArea;
		this.lastFinishedWorkPieceIndex = 0;
		this.layout = layout;
		layout.setParent(this);
		this.nomSpeed1 = nomSpeed1;
		this.nomSpeed2 = nomSpeed2;
		this.listeners = new ArrayList<ConveyorListener>();
		this.sensorValues = new ArrayList<Integer>();
		this.workPieceShift = 20;
		checkSetup();
		ConveyorMonitoringThread monitoringThread = new ConveyorMonitoringThread(this);
		ThreadManager.submit(monitoringThread);
	}
	
	public Conveyor(final String name, final WorkAreaManager rawWorkArea, final WorkAreaManager finishedWorkArea, final ConveyorLayout layout, 
			final SocketConnection socketConnection, final float nomSpeed1, final float nomSpeed2) {
		this(name, new HashSet<Zone>(), rawWorkArea, finishedWorkArea, layout, socketConnection, nomSpeed1, nomSpeed2);
	}	
	
	public ConveyorLayout getLayout() {
		return layout;
	}
	
	public void updateStatusAndAlarms() throws AbstractCommunicationException, InterruptedException {
		int statusInt = (getSocketCommunication().readRegisters(ConveyorConstants.STATUS_REG, 1)).get(0);
		setStatus(statusInt);
		List<Integer> alarmInts = getSocketCommunication().readRegisters(ConveyorConstants.ALARMS_REG, 1);
		int alarmReg1 = alarmInts.get(0);
		setAlarms(ConveyorAlarm.parseConveyorAlarms(alarmReg1, statusInt, getConveyorTimeout()));
		this.sensorValues = getSocketCommunication().readRegisters(ConveyorConstants.SENSOR_0_REG, 4);
		SupportState[] currentSupportStatus = new SupportState[4];
		if ((statusInt & ConveyorConstants.USE_SUPPORT_SENSORS) > 0) {
			if ((statusInt & ConveyorConstants.SUPPORT_0_IS_DOWN) > 0) {
				currentSupportStatus[0] = SupportState.DOWN;
			} else {
				currentSupportStatus[0] = SupportState.UP;
			}
			if ((statusInt & ConveyorConstants.SUPPORT_1_IS_DOWN) > 0) {
				currentSupportStatus[1] = SupportState.DOWN;
			} else {
				currentSupportStatus[1] = SupportState.UP;
			}
			if ((statusInt & ConveyorConstants.SUPPORT_2_IS_DOWN) > 0) {
				currentSupportStatus[2] = SupportState.DOWN;
			} else {
				currentSupportStatus[2] = SupportState.UP;
			}
			if ((statusInt & ConveyorConstants.SUPPORT_3_IS_DOWN) > 0) {
				currentSupportStatus[3] = SupportState.DOWN;
			} else {
				currentSupportStatus[3] = SupportState.UP;
			}
		} else {
			currentSupportStatus[0] = SupportState.UNKNOWN;
			currentSupportStatus[1] = SupportState.UNKNOWN;
			currentSupportStatus[2] = SupportState.UNKNOWN;
			currentSupportStatus[3] = SupportState.UNKNOWN;
		}
		layout.setCurrentSupportStatus(currentSupportStatus);
		Boolean[] supportSelectionStatus = new Boolean[4];
		int supportsSelectionInt = (getSocketCommunication().readRegisters(ConveyorConstants.SUPPORT_SELECTION, 1)).get(0);
		supportSelectionStatus[0] = ((supportsSelectionInt & ConveyorConstants.SUPPORT_0_SELECTED) > 0);
		supportSelectionStatus[1] = ((supportsSelectionInt & ConveyorConstants.SUPPORT_1_SELECTED) > 0);
		supportSelectionStatus[2] = ((supportsSelectionInt & ConveyorConstants.SUPPORT_2_SELECTED) > 0);
		supportSelectionStatus[3] = ((supportsSelectionInt & ConveyorConstants.SUPPORT_3_SELECTED) > 0);
		layout.setSupportSelectionStatus(supportSelectionStatus);
	}
	
	public void configureSupports() {
		try {
			configureSupports(layout.getRequestedSupportStatus());
		} catch (AbstractCommunicationException	| InterruptedException | DeviceActionException e) {
			e.printStackTrace();
			logger.error(e);
		}
	}
	
	public boolean isRawConveyorEmpty() {
		for (eu.robojob.millassist.external.device.stacking.conveyor.ConveyorAlarm alarm : getAlarms()) {
			if (alarm.getId() == ConveyorAlarm.ALR_RAW_CONV_EMPTY) {
				return true;
			}
		}
		return false;
	}
	
	public void allSupportsDown() {
		Boolean[] supportSelection = layout.getSupportSelectionStatus();
		Boolean[] allDown = new Boolean[4];
		Arrays.fill(allDown, false);
		try {
			configureSupports(allDown);
			selectSupports(supportSelection);
		} catch (AbstractCommunicationException	| InterruptedException | DeviceActionException e) {
			e.printStackTrace();
			logger.error(e);
		}
	}
	
	private void configureSupports(final Boolean[] requestedSupportState) throws SocketResponseTimedOutException, SocketDisconnectedException, InterruptedException, SocketWrongResponseException, DeviceActionException {
		int command = 0;
		int[] values = new int[1];
		command = command | ConveyorConstants.PREPARE_FOR_CMD;
		values[0] = command;
		getSocketCommunication().writeRegisters(ConveyorConstants.COMMAND_REG, values);
		boolean ready = waitForStatus(ConveyorConstants.READY_FOR_CMD, WAIT_FOR_READY_FOR_CMD_TIMEOUT);
		if (!ready) {
			logger.error("Timeout while preparing for update cmd");
			return;
		}
		command = 0;
		command = command | ConveyorConstants.SUPPORTS_UPDATE;
		if (requestedSupportState[0]) {
			command = command | ConveyorConstants.SUPPORT_0;
		}
		if (requestedSupportState[1]) {
			command = command | ConveyorConstants.SUPPORT_1;
		}
		if (requestedSupportState[2]) {
			command = command | ConveyorConstants.SUPPORT_2;
		}
		if (requestedSupportState[3]) {
			command = command | ConveyorConstants.SUPPORT_3;
		}
		values[0] = command;
		getSocketCommunication().writeRegisters(ConveyorConstants.COMMAND_REG, values);
		boolean updated = waitForStatus(ConveyorConstants.SUPPORTS_UPDATED, SUPPORTS_UPDATE_TIMEOUT);
		if (!updated) {
			logger.error("Timeout while updating supports");
			return;
		}
	}
	
	public void changeSupportsSelection(final Boolean[] supportSelection) throws SocketResponseTimedOutException, SocketDisconnectedException, SocketWrongResponseException, InterruptedException, DeviceActionException {
		selectSupports(supportSelection);
	}
	
	private void selectSupports(final Boolean[] supportSelection) throws SocketResponseTimedOutException, SocketDisconnectedException, InterruptedException, SocketWrongResponseException, DeviceActionException {
		int command = 0;
		int[] values = new int[1];
		// First reset status
		command = command | ConveyorConstants.PREPARE_FOR_CMD;
		values[0] = command;
		getSocketCommunication().writeRegisters(ConveyorConstants.COMMAND_REG, values);
		boolean ready = waitForStatus(ConveyorConstants.READY_FOR_CMD, WAIT_FOR_READY_FOR_CMD_TIMEOUT);
		if (!ready) {
			logger.error("Timeout while preparing for select cmd");
			return;
		}
		command = 0;
		command = command | ConveyorConstants.SUPPORTS_SELECT;
		if (supportSelection[0]) {
			command = command | ConveyorConstants.SUPPORT_0;
		}
		if (supportSelection[1]) {
			command = command | ConveyorConstants.SUPPORT_1;
		}
		if (supportSelection[2]) {
			command = command | ConveyorConstants.SUPPORT_2;
		}
		if (supportSelection[3]) {
			command = command | ConveyorConstants.SUPPORT_3;
		}
		values[0] = command;
		getSocketCommunication().writeRegisters(ConveyorConstants.COMMAND_REG, values);
		boolean selected = waitForStatus(ConveyorConstants.SUPPORTS_SELECTED, SUPPORTS_SELECT_TIMEOUT);
		if (!selected) {
			logger.error("Timeout while selecting supports");
			return;
		}
	}
	
	@Override
	public void clearDeviceSettings() {
		setRawWorkPiece(new WorkPiece(WorkPiece.Type.RAW, new RectangularDimensions(), Material.OTHER, WorkPieceShape.CUBIC, 0.0f));
		setFinishedWorkPiece(new WorkPiece(WorkPiece.Type.FINISHED, new RectangularDimensions(), Material.OTHER, WorkPieceShape.CUBIC, 0.0f));
		layout.clearSettings();
		notifyLayoutChanged();
	}
	
	public float getNomSpeedRawConveyor() {
		return nomSpeed1;
	}
	
	public float getNomSpeedFinishedConveyor() {
		return nomSpeed2;
	}
	
	public List<Integer> getSensorValues() {
		return sensorValues;
	}
	
	public boolean isModeAuto() {
		return (getStatus() & ConveyorConstants.MODE) > 0;
	}

	public WorkAreaManager getRawWorkArea() {
		return rawWorkArea;
	}

	public WorkAreaManager getFinishedWorkArea() {
		return finishedWorkArea;
	}

	public boolean isInterlockRaw() {
		return (getStatus() & ConveyorConstants.RAW_CONV_INTERLOCK) > 0;
	}
	
	public boolean isInterlockFinished() {
		return (getStatus() & ConveyorConstants.FIN_CONV_INTERLOCK) > 0;
	}
	
	public boolean isMovingRaw() {
		return (getStatus() & ConveyorConstants.RAW_CONV_MOV) > 0;
	}

	public boolean isMovingFinished() {
		return (getStatus() & ConveyorConstants.FIN_CONV_MOV) > 0;
	}
	
	@Override public void reset() throws AbstractCommunicationException, DeviceActionException, InterruptedException { 
		int command = 0;
		command = command | ConveyorConstants.RESET_ALARMS;
		int[] values = {command};
		getSocketCommunication().writeRegisters(ConveyorConstants.COMMAND_REG, values);
	}
	
	@Override
	public void prepareForProcess(final ProcessFlow process) throws AbstractCommunicationException, InterruptedException {
		clearIndications();
		writeRawWorkPieceLength();
		writeFinishedWorkPieceLength();
	}
	
	public void writeRawWorkPieceLength() throws SocketResponseTimedOutException, SocketDisconnectedException, InterruptedException, SocketWrongResponseException {
		int workPieceLength = (int) Math.round(getRawWorkPiece().getDimensions().getDimension(Dimensions.LENGTH));
		workPieceLength +=  workPieceShift;
		int[] length = {workPieceLength};
		getSocketCommunication().writeRegisters(ConveyorConstants.LENGTH_WP_RAW_SHIFT, length);
	}
	
	public void writeFinishedWorkPieceLength() throws SocketResponseTimedOutException, SocketDisconnectedException, InterruptedException, SocketWrongResponseException {
		int workPieceLength = (int) Math.round(getFinishedWorkPiece().getDimensions().getDimension(Dimensions.LENGTH));
		workPieceLength +=  workPieceShift;
		int[] length = {workPieceLength};
		getSocketCommunication().writeRegisters(ConveyorConstants.LENGTH_WP_FINISHED_SHIFT, length);
	}
	
	public float getWorkPieceShift() {
		return workPieceShift;
	}

	@Override
	public boolean canPick(final DevicePickSettings pickSettings) throws AbstractCommunicationException, DeviceActionException {
		// only if mode = auto, work piece in position and interlock
		return (((getStatus() & ConveyorConstants.RAW_WP_IN_POSITION) > 0) && isModeAuto());
	}

	@Override
	public boolean canPut(final DevicePutSettings putSettings) throws AbstractCommunicationException, DeviceActionException,
			InterruptedException {
		boolean noSpace = false;
		for (eu.robojob.millassist.external.device.stacking.conveyor.ConveyorAlarm alarm : getAlarms()) {
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
		if (interventionSettings.getWorkArea().getWorkAreaManager().equals(rawWorkArea)) {
			return isInterlockRaw();
		} else if (interventionSettings.getWorkArea().getWorkAreaManager().equals(finishedWorkArea)) {
			return isInterlockFinished();
		} else {
			throw new IllegalArgumentException("Illegal workarea: " + interventionSettings.getWorkArea());
		}
	}

	@Override
	public void prepareForPick(final DevicePickSettings pickSettings, final int processId) throws AbstractCommunicationException, DeviceActionException,
			InterruptedException {
		// wait until workpiece in position, obtain interlock
		logger.info("Waiting for raw work piece in position status and mode = auto.");
		waitForStatus((ConveyorConstants.RAW_WP_IN_POSITION | ConveyorConstants.MODE));
		logger.info("Work piece in position and mode = auto.");
		int command = 0;
		command = command | ConveyorConstants.RQST_INTERLOCK_RAW;
		int[] commandReg = {command};
		logger.info("Sending interlock raw conveyor command.");
		getSocketCommunication().writeRegisters(ConveyorConstants.COMMAND_REG, commandReg);
		logger.info("Waiting for confirmation raw conveyor interlock.");
		waitForStatus(ConveyorConstants.RAW_CONV_INTERLOCK);
		logger.info("Obtained interlock, prepare for pick is ready");		
	}

	@Override
	public void prepareForPut(final DevicePutSettings putSettings, final int processId) throws AbstractCommunicationException, DeviceActionException, InterruptedException {
		// wait until the finished conveyor is not moving
		logger.info("Checking finished conveyor is not moving, if so, wait until stopped.");
		waitForStatusNot((ConveyorConstants.FIN_CONV_MOV));
		logger.info("Finished conveyor is not moving, wait for mode = auto.");
		waitForStatus(ConveyorConstants.MODE);
		logger.info("Mode = auto, so we can continue.");
		int command = 0;
		command = command | ConveyorConstants.RQST_INTERLOCK_FINISHED;
		int[] commandReg = {command};
		logger.info("Sending interlock finished conveyor command.");
		getSocketCommunication().writeRegisters(ConveyorConstants.COMMAND_REG, commandReg);
		logger.info("Waiting for confirmation finished conveyor interlock.");
		waitForStatus(ConveyorConstants.FIN_CONV_INTERLOCK);
		logger.info("Obtained interlock, prepare for put is ready");		
	}

	@Override public void prepareForIntervention(final DeviceInterventionSettings interventionSettings) throws AbstractCommunicationException, DeviceActionException, InterruptedException {
		// obtain interlock of correct work area, and wait until not moving
		if (interventionSettings.getWorkArea().getWorkAreaManager().equals(rawWorkArea)) {
			waitForStatusNot((ConveyorConstants.RAW_CONV_MOV));
			waitForStatus(ConveyorConstants.MODE);
			int command = 0;
			command = command | ConveyorConstants.RQST_INTERLOCK_RAW;
			int[] commandReg = {command};
			getSocketCommunication().writeRegisters(ConveyorConstants.COMMAND_REG, commandReg);
			waitForStatus(ConveyorConstants.RAW_CONV_INTERLOCK);
		} else if (interventionSettings.getWorkArea().getWorkAreaManager().equals(finishedWorkArea)) {
			waitForStatusNot((ConveyorConstants.FIN_CONV_MOV));
			waitForStatus(ConveyorConstants.MODE);
			int command = 0;
			command = command | ConveyorConstants.RQST_INTERLOCK_FINISHED;
			int[] commandReg = {command};
			getSocketCommunication().writeRegisters(ConveyorConstants.COMMAND_REG, commandReg);
			waitForStatus(ConveyorConstants.FIN_CONV_INTERLOCK);
		} else {
			throw new IllegalArgumentException("Illegal workarea");
		}
	}

	@Override
	public void pickFinished(final DevicePickSettings pickSettings, final int processId) throws AbstractCommunicationException, DeviceActionException,
			InterruptedException {
		// release interlock
		int command = 0;
		command = command | ConveyorConstants.RELEASE_INTERLOCK_RAW;
		int[] commandReg = {command};
		getSocketCommunication().writeRegisters(ConveyorConstants.COMMAND_REG, commandReg);
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
		getSocketCommunication().writeRegisters(ConveyorConstants.COMMAND_REG, commandReg);
		// if last piece: do shift
		if (lastFinishedWorkPieceIndex == layout.getStackingPositionsFinishedWorkPieces().size() - 1) {
			waitForStatusNot(ConveyorConstants.FIN_CONV_INTERLOCK);
			// just to be sure, write the finished work piece shift length
			//writeFinishedWorkPieceLength();
			int command2 = 0;
			command2 = command2 | ConveyorConstants.SHIFT_FINISHED_WP;
			int[] commandReg2 = {command2};
			logger.info("Writing shift command: " + command2);
			getSocketCommunication().writeRegisters(ConveyorConstants.COMMAND_REG, commandReg2);
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
		if (interventionSettings.getWorkArea().getWorkAreaManager().equals(rawWorkArea)) {
			int command = 0;
			command = command | ConveyorConstants.RELEASE_INTERLOCK_RAW;
			int[] commandReg = {command};
			getSocketCommunication().writeRegisters(ConveyorConstants.COMMAND_REG, commandReg);
		} else if (interventionSettings.getWorkArea().getWorkAreaManager().equals(finishedWorkArea)) {
			int command = 0;
			command = command | ConveyorConstants.RELEASE_INTERLOCK_FINISHED;
			int[] commandReg = {command};
			getSocketCommunication().writeRegisters(ConveyorConstants.COMMAND_REG, commandReg);
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
		for (Entry<SimpleWorkArea, Clamping> entry : deviceSettings.getClampings().entrySet()) {
			entry.getKey().setDefaultClamping(entry.getValue());
		}
		if (deviceSettings instanceof ConveyorSettings) {
			ConveyorSettings settings = (ConveyorSettings) deviceSettings;
			if (settings.getRawWorkPiece() != null) {
				setRawWorkPiece(settings.getRawWorkPiece());
			} else {
				setRawWorkPiece(new WorkPiece(WorkPiece.Type.RAW, new RectangularDimensions(), Material.OTHER, WorkPieceShape.CUBIC, 0.0f));
			}
			if (settings.getFinishedWorkPiece() != null) {
				setFinishedWorkPiece(settings.getFinishedWorkPiece());
			} else {
				setFinishedWorkPiece(new WorkPiece(WorkPiece.Type.FINISHED, new RectangularDimensions(), Material.OTHER, WorkPieceShape.CUBIC, 0.0f));
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

	
	@Override
	public DeviceSettings getDeviceSettings() {
		return new ConveyorSettings(getRawWorkPiece(), getFinishedWorkPiece(), amount, layout.getOffsetSupport1(), layout.getOffsetOtherSupports());
	}
	
	@Override
	public Coordinates getLocationOrientation(final SimpleWorkArea workArea, final ClampingManner clampType) {
		if (workArea.getWorkAreaManager().equals(rawWorkArea)) {
			Coordinates c = new Coordinates(layout.getStackingPositionsRawWorkPieces().get(0).getPosition());
			c.setX(0);
			c.setY(0);
			c.setZ(0);
			return c;
		} else if (workArea.getWorkAreaManager().equals(finishedWorkArea)) {
			Coordinates c = new Coordinates(layout.getStackingPositionsFinishedWorkPieces().get(0).getPosition());
			c.setX(0);
			c.setY(0);
			c.setZ(0);
			return c;
		} else {
			throw new IllegalArgumentException("Unknown workarea: " + workArea);
		}
	}

	@Override
	public void interruptCurrentAction() {
		// no action possible
	}

	@Override
	public EDeviceGroup getType() {
		return EDeviceGroup.CONVEYOR;
	}
	
	public void notifyFinishedShifted() {
		for (ConveyorListener listener : listeners) {
			listener.finishedShifted(getFinishedWorkPiece().getDimensions().getDimension(Dimensions.LENGTH) + workPieceShift);
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
	
	public int getAmount() {
		return amount;
	}

	public void setAmount(final int amount) {
		this.amount = amount;
	}

	public void processConveyorEvent(final ConveyorEvent event) {
		switch(event.getId()) {
			case ConveyorEvent.SENSOR_VALUES_CHANGED:
				for (ConveyorListener listener : listeners) {
					listener.sensorValuesChanged((ConveyorSensorValuesChangedEvent) event);
				}
				break;
			case ConveyorEvent.ALARM_OCCURED:
				for (ConveyorListener listener : listeners) {
					listener.conveyorAlarmsOccured(((ConveyorAlarmsOccuredEvent) ((eu.robojob.millassist.external.device.stacking.conveyor.ConveyorEvent) event)));
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
			default:
				throw new IllegalArgumentException("Unknown event type: " + event.getId());
		}
	}
	
	public DevicePickSettings getDefaultPickSettings(final int sequenceNb) {
		return new DevicePickSettings(this, rawWorkArea.getWorkAreaWithSequence(sequenceNb));
	}
	
	public DevicePutSettings getDefaultPutSettings(final int sequenceNb) {
		return new DevicePutSettings(this, finishedWorkArea.getWorkAreaWithSequence(sequenceNb));
	}
	
	@Override
	public String toString() {
		return "Conveyor: " + getName();
	}
	
	private void checkSetup() {
		this.isLeftSetup = !PropertyManager.hasSettingValue(Setting.CONVEYOR_SETUP, "right");
	}
	
	public boolean isLeftSetup() {
		return this.isLeftSetup;
	}

	@Override
	public void indicateAllProcessed() throws AbstractCommunicationException,
			InterruptedException, DeviceActionException {
		int[] registers = {FINISH_BLUE_LAMP_VAL};
		getSocketCommunication().writeRegisters(ConveyorConstants.BLUE_LAMP, registers);
	}

	@Override
	public void indicateOperatorRequested(boolean requested) throws AbstractCommunicationException, InterruptedException {
		int command = 0;
		if (requested) {
			command = OPERATOR_RQST_BLUE_LAMP_VAL;
		}
		int[] registers = {command};
		getSocketCommunication().writeRegisters(ConveyorConstants.BLUE_LAMP, registers);
	}

	@Override
	public void clearIndications() throws AbstractCommunicationException, InterruptedException {
		// reset blue lamp
		indicateOperatorRequested(false);
	}
	
	public int getLastFinishedWorkPieceIndex() {
		return this.lastFinishedWorkPieceIndex;
	}

	@Override
	public <T extends IWorkPieceDimensions> Coordinates getLocation(
			AbstractPiecePlacementVisitor<T> visitor, SimpleWorkArea workArea,
			Type type, ClampingManner clampType) {
		return visitor.getLocation(this, workArea, type, clampType);
	}

	@Override
	public <T extends IWorkPieceDimensions> Coordinates getPutLocation(
			AbstractPiecePlacementVisitor<T> visitor, SimpleWorkArea workArea,
			T dimensions, ClampingManner clampType, ApproachType approachType) {
		return visitor.getPutLocation(this, workArea, dimensions, clampType, approachType);
	}

	@Override
	public <T extends IWorkPieceDimensions> Coordinates getPickLocation(
			AbstractPiecePlacementVisitor<T> visitor, SimpleWorkArea workArea,
			T dimensions, ClampingManner clampType, ApproachType approachType) {
		return visitor.getPickLocation(this, workArea, dimensions, clampType, approachType);
	}

}
