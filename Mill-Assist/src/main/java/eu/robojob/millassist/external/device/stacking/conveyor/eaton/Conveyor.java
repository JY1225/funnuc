package eu.robojob.millassist.external.device.stacking.conveyor.eaton;

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
import eu.robojob.millassist.external.device.stacking.IncorrectWorkPieceDataException;
import eu.robojob.millassist.external.device.stacking.StackingPosition;
import eu.robojob.millassist.external.device.stacking.conveyor.AbstractConveyor;
import eu.robojob.millassist.external.device.stacking.conveyor.ConveyorListener;
import eu.robojob.millassist.positioning.Coordinates;
import eu.robojob.millassist.process.ProcessFlow;
import eu.robojob.millassist.threading.ThreadManager;
import eu.robojob.millassist.workpiece.WorkPiece;
import eu.robojob.millassist.workpiece.WorkPiece.Material;
import eu.robojob.millassist.workpiece.WorkPiece.Type;
import eu.robojob.millassist.workpiece.WorkPieceDimensions;

public class Conveyor extends AbstractConveyor {

	private ConveyorLayout layout;
	private WorkArea workAreaA;
	private WorkArea workAreaB;
	private float nomSpeedA;
	private float nomSpeedASlow;
	private float nomSpeedB;
	private float nomSpeedBSlow;
	private float workPieceShift;
	private boolean lastTrackPickedA;
	private int amount;
	
	private static Logger logger = LogManager.getLogger(Conveyor.class.getName());

	public Conveyor(final String name, final Set<Zone> zones, final WorkArea workAreaA, final WorkArea workAreaB,
			final ConveyorLayout layout, final SocketConnection socketConnection, final float nomSpeedA, final float nomSpeedASlow,
			final float nomSpeedB, final float nomSpeedBSlow) {
		super(name, zones, socketConnection);
		this.workAreaA = workAreaA;
		this.workAreaB = workAreaB;
		this.layout = layout;
		layout.setParent(this);
		this.nomSpeedA = nomSpeedA;
		this.nomSpeedB = nomSpeedB;
		this.nomSpeedASlow = nomSpeedASlow;
		this.nomSpeedBSlow = nomSpeedBSlow;
		this.workPieceShift = 20;
		this.lastTrackPickedA = false;
		this.amount = -1;
		ConveyorMonitoringThread monitoringThread = new ConveyorMonitoringThread(this);
		ThreadManager.submit(monitoringThread);
	}

	public Conveyor(final String name, final WorkArea workAreaA, final WorkArea workAreaB,
			final ConveyorLayout layout, final SocketConnection socketConnection, final float nomSpeedA, final float nomSpeedASlow, 
			final float nomSpeedB, final float nomSpeedBSlow) {
		this(name, new HashSet<Zone>(), workAreaA, workAreaB, layout, socketConnection, nomSpeedA, nomSpeedASlow, nomSpeedB, nomSpeedBSlow);
	}
	
	public ConveyorLayout getLayout() {
		return layout;
	}
	
	public void upateStatusAndAlarms() throws AbstractCommunicationException, InterruptedException {
		int statusInt = (getSocketCommunication().readRegisters(ConveyorConstants.STATUS_REG, 1)).get(0);
		setStatus(statusInt);
		List<Integer> alarmInts = getSocketCommunication().readRegisters(ConveyorConstants.ALARMS_REG, 1);
		int alarmReg1 = alarmInts.get(0);
		setAlarms(ConveyorAlarm.parseConveyorAlarms(alarmReg1, statusInt, getConveyorTimeout()));
	}
	
	public boolean isConveyorAEmpty() {
		for (eu.robojob.millassist.external.device.stacking.conveyor.ConveyorAlarm alarm : getAlarms()) {
			if (alarm.getId() == ConveyorAlarm.ALR_CONV_A_EMPTY) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isConveyorBEmpty() {
		for (eu.robojob.millassist.external.device.stacking.conveyor.ConveyorAlarm alarm : getAlarms()) {
			if (alarm.getId() == ConveyorAlarm.ALR_CONV_B_EMPTY) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public void clearDeviceSettings() {
		setRawWorkPiece(new WorkPiece(WorkPiece.Type.RAW, new WorkPieceDimensions(), Material.OTHER, 0.0f));
		setFinishedWorkPiece(new WorkPiece(WorkPiece.Type.FINISHED, new WorkPieceDimensions(), Material.OTHER, 0.0f));
		layout.clearSettings();
		notifyLayoutChanged();
	}
	
	public float getNomSpeedConveyorA() {
		return nomSpeedA;
	}
	
	public float getNomSpeedConveyorASlow() {
		return nomSpeedASlow;
	}
	
	public float getNomSpeedConveyorB() {
		return nomSpeedB;
	}
	
	public float getNomSpeedConveyorBSlow() {
		return nomSpeedBSlow;
	}
	
	public boolean isModeAuto() {
		return (getStatus() & ConveyorConstants.MODE) > 0;
	}
	
	public boolean isTrackBModeLoad() {
		return (getStatus() & ConveyorConstants.CONV_B_MODE) <= 0;
	}
	
	public WorkArea getWorkAreaA() {
		return workAreaA;
	}

	public WorkArea getWorkAreaB() {
		return workAreaB;
	}
	
	public boolean isTrackAInterlock() {
		return (getStatus() & ConveyorConstants.CONV_A_INTERLOCK) > 0;
	}
	
	public boolean isTrackBInterlock() {
		return (getStatus() & ConveyorConstants.CONV_B_INTERLOCK) > 0;
	}
	
	public boolean isTrackAMoving() {
		return (getStatus() & ConveyorConstants.CONV_A_MOV) > 0;
	}
	
	public boolean isTrackASlow() {
		return (getStatus() & ConveyorConstants.CONV_A_SLOW) > 0;
	}
	
	public boolean isTrackBMoving() {
		return (getStatus() & ConveyorConstants.CONV_B_MOV) > 0;
	}
	
	public boolean isTrackBSlow() {
		return (getStatus() & ConveyorConstants.CONV_B_SLOW) > 0;
	}
	
	public boolean isTrackASensor1() {
		return (getStatus() & ConveyorConstants.CONV_A_WP_IN_POSITION) > 0;
	}
	
	public boolean isTrackBSensor1() {
		return (getStatus() & ConveyorConstants.CONV_B_WP_IN_POSITION) > 0;
	}
	
	@Override 
	public void reset() throws AbstractCommunicationException, DeviceActionException, InterruptedException { 
		int command = 0;
		command = command | ConveyorConstants.RESET_ALARMS;
		int[] values = {command};
		getSocketCommunication().writeRegisters(ConveyorConstants.COMMAND_REG, values);
	}

	@Override
	public Coordinates getLocation(final WorkArea workArea, final Type type, final ClampingManner clampType) throws DeviceActionException, InterruptedException {
		if (type == Type.FINISHED) {
			return getPutLocation(workArea, getFinishedWorkPiece().getDimensions(), clampType);
		} else if (type == Type.RAW) {
			return getPickLocation(workArea, getRawWorkPiece().getDimensions(), clampType);
		}
		return null;
	}

	@Override
	public void prepareForProcess(final ProcessFlow process) throws AbstractCommunicationException, InterruptedException {
		writeFinishedWorkPieceLength();
	}
	
	public void writeFinishedWorkPieceLength() throws SocketResponseTimedOutException, SocketDisconnectedException, InterruptedException {
		int workPieceLength = (int) Math.round(getFinishedWorkPiece().getDimensions().getLength());
		workPieceLength +=  workPieceShift;
		int[] length = {workPieceLength};
		getSocketCommunication().writeRegisters(ConveyorConstants.LENGTH_WP_FIN_SHIFT, length);
	}

	public float getWorkPieceShift() {
		return workPieceShift;
	}
	
	@Override
	public boolean canPick(final DevicePickSettings pickSettings) throws AbstractCommunicationException, DeviceActionException {
		if (!isTrackBModeLoad()) {
			pickSettings.setWorkArea(workAreaA);
			pickSettings.getStep().getRobotSettings().setWorkArea(workAreaA);
		}
		if (lastTrackPickedA) {
			// first check track B
			if (isTrackBModeLoad() && 
					isModeAuto() && ((getStatus() & ConveyorConstants.CONV_B_WP_IN_POSITION) > 0) && isModeAuto()) {
				pickSettings.setWorkArea(workAreaB);
				pickSettings.getStep().getRobotSettings().setWorkArea(workAreaB);
				return true;
			}
		}
		if (isModeAuto() && ((getStatus() & ConveyorConstants.CONV_A_WP_IN_POSITION) > 0)  && isModeAuto()) {
			pickSettings.setWorkArea(workAreaA);
			pickSettings.getStep().getRobotSettings().setWorkArea(workAreaA);
			return true;
		} else if (isTrackBModeLoad() && 
				isModeAuto() && ((getStatus() & ConveyorConstants.CONV_B_WP_IN_POSITION) > 0) && isModeAuto()) {
			pickSettings.setWorkArea(workAreaB);
			pickSettings.getStep().getRobotSettings().setWorkArea(workAreaB);
			return true;
		}
		return false;
	}

	@Override
	public boolean canPut(final DevicePutSettings putSettings) throws AbstractCommunicationException, DeviceActionException, InterruptedException {
		boolean noSpace = false;
		for (eu.robojob.millassist.external.device.stacking.conveyor.ConveyorAlarm alarm : getAlarms()) {
			if (alarm.getId() == ConveyorAlarm.ALR_CONV_B_FULL) {
				noSpace = true;
			}
		}
		return (isModeAuto() && (!isTrackBModeLoad()) && isTrackBInterlock() && !noSpace);
	}

	@Override
	public boolean canIntervention(final DeviceInterventionSettings interventionSettings) throws AbstractCommunicationException, DeviceActionException {
		if (interventionSettings.getWorkArea().equals(workAreaA)) {
			return isTrackAInterlock();
		} else if (interventionSettings.getWorkArea().equals(workAreaB)) {
			return isTrackBInterlock();
		} else {
			throw new IllegalArgumentException("Illegal workarea: " + interventionSettings.getWorkArea());
		}
	}

	@Override
	public void prepareForPick(final DevicePickSettings pickSettings) throws AbstractCommunicationException, DeviceActionException, InterruptedException {
		// wait until work piece in position, obtain interlock
		if (pickSettings.getWorkArea().equals(workAreaA)) {
			waitForStatus((ConveyorConstants.CONV_A_WP_IN_POSITION | ConveyorConstants.MODE));
			int command = 0;
			command = command | ConveyorConstants.RQST_INTERLOCK_A;
			int[] commandReg = {command};
			logger.debug("Sending interlock track A command.");
			getSocketCommunication().writeRegisters(ConveyorConstants.COMMAND_REG, commandReg);
			logger.debug("Waiting for confirmation track A interlock.");
			waitForStatus(ConveyorConstants.CONV_A_INTERLOCK);
			logger.debug("Obtained interlock, prepare for pick is ready");
		} else if (pickSettings.getWorkArea().equals(workAreaB)) {
			if (!isTrackBModeLoad()) {
				throw new IllegalArgumentException("Track B is not in load mode!");
			}
			waitForStatus((ConveyorConstants.CONV_B_WP_IN_POSITION | ConveyorConstants.MODE));
			int command = 0;
			command = command | ConveyorConstants.RQST_INTERLOCK_B;
			int[] commandReg = {command};
			logger.debug("Sending interlock track B command.");
			getSocketCommunication().writeRegisters(ConveyorConstants.COMMAND_REG, commandReg);
			logger.debug("Waiting for confirmation track B interlock.");
			waitForStatus(ConveyorConstants.CONV_B_INTERLOCK);
			logger.debug("Obtained interlock, prepare for pick is ready");
		} else {
			throw new IllegalArgumentException("Illegal workarea: " + pickSettings.getWorkArea());
		}
	}

	@Override
	public void prepareForPut(final DevicePutSettings putSettings) throws AbstractCommunicationException, DeviceActionException, InterruptedException {
		if (putSettings.getWorkArea().equals(workAreaB)) {
			if (!isTrackBModeLoad()) {
				logger.debug("Checking finished conveyor is not moving, if so, wait until stopped.");
				waitForStatusNot(ConveyorConstants.CONV_B_MOV);
				logger.debug("Finished conveyor is not moving, wait for mode = auto.");
				waitForStatus(ConveyorConstants.MODE);
				int command = 0;
				command = command | ConveyorConstants.RQST_INTERLOCK_B;
				int[] commandReg = {command};
				logger.debug("Sending interlock track B command.");
				getSocketCommunication().writeRegisters(ConveyorConstants.COMMAND_REG, commandReg);
				logger.debug("Waiting for confirmation track B interlock.");
				waitForStatus(ConveyorConstants.CONV_B_INTERLOCK);
				logger.debug("Obtained interlock prepare for put is ready.");
			} else {
				throw new IllegalStateException("Put on track B, but it's not in unload mode.");
			}
		} else {
			throw new IllegalArgumentException("Illegal workarea: " + putSettings.getWorkArea());
		}
	}

	@Override
	public void prepareForIntervention(final DeviceInterventionSettings interventionSettings) throws AbstractCommunicationException, DeviceActionException, InterruptedException {
		if (interventionSettings.getWorkArea().equals(workAreaA)) {
			int command = 0;
			command = command | ConveyorConstants.RQST_INTERLOCK_A;
			int[] commandReg = {command};
			logger.debug("Sending interlock track A command.");
			getSocketCommunication().writeRegisters(ConveyorConstants.COMMAND_REG, commandReg);
			logger.debug("Waiting for confirmation track A interlock.");
			waitForStatus(ConveyorConstants.CONV_A_INTERLOCK);
			logger.debug("Obtained interlock, prepare for intervention is ready");
		} else if (interventionSettings.getWorkArea().equals(workAreaB)) {
			if (!isTrackBModeLoad()) {
				throw new IllegalArgumentException("Track B is not in load mode!");
			}
			int command = 0;
			command = command | ConveyorConstants.RQST_INTERLOCK_B;
			int[] commandReg = {command};
			logger.debug("Sending interlock track B command.");
			getSocketCommunication().writeRegisters(ConveyorConstants.COMMAND_REG, commandReg);
			logger.debug("Waiting for confirmation track B interlock.");
			waitForStatus(ConveyorConstants.CONV_B_INTERLOCK);
			logger.debug("Obtained interlock, prepare for interventionSettings is ready");
		} else {
			throw new IllegalArgumentException("Illegal workarea: " + interventionSettings.getWorkArea());
		}
	}

	@Override
	public void pickFinished(final DevicePickSettings pickSettings) throws AbstractCommunicationException, DeviceActionException, InterruptedException {
		// release interlock and update lastTrackPickedA variable
		if (pickSettings.getWorkArea().equals(workAreaA)) {
			int command = 0;
			command = command | ConveyorConstants.RELEASE_INTERLOCK_A;
			int[] commandReg = {command};
			logger.debug("Sending release interlock A.");
			getSocketCommunication().writeRegisters(ConveyorConstants.COMMAND_REG, commandReg);
			logger.debug("Waiting for confirmation interlock A released.");
			waitForStatusNot(ConveyorConstants.CONV_A_INTERLOCK);
			lastTrackPickedA = true;
		} else if (pickSettings.getWorkArea().equals(workAreaB)) {
			int command = 0;
			command = command | ConveyorConstants.RELEASE_INTERLOCK_B;
			int[] commandReg = {command};
			logger.debug("Sending release interlock B.");
			getSocketCommunication().writeRegisters(ConveyorConstants.COMMAND_REG, commandReg);
			logger.debug("Waiting for confirmation interlock B released.");
			waitForStatusNot(ConveyorConstants.CONV_B_INTERLOCK);
			lastTrackPickedA = false;
		} else {
			throw new IllegalArgumentException("Illegal workarea: " + pickSettings.getWorkArea());
		}
	}

	@Override
	public void putFinished(final DevicePutSettings putSettings) throws AbstractCommunicationException, DeviceActionException, InterruptedException {
		// release interlock
		if (putSettings.getWorkArea().equals(workAreaB)) {
			int command = 0;
			command = command | ConveyorConstants.RELEASE_INTERLOCK_B;
			int[] commandReg = {command};
			logger.debug("Sending release interlock B.");
			getSocketCommunication().writeRegisters(ConveyorConstants.COMMAND_REG, commandReg);
			logger.debug("Waiting for confirmation interlock B released.");
			waitForStatusNot(ConveyorConstants.CONV_B_INTERLOCK);
			logger.info("No more interlock: " + getStatus());
			// shift track B
			int command2 = 0;
			command2 = command2 | ConveyorConstants.SHIFT_FINISHED_WP;
			int[] commandReg2 = {command2};
			logger.debug("Writing shift command");
			getSocketCommunication().writeRegisters(ConveyorConstants.COMMAND_REG, commandReg2);
			layout.shiftFinishedWorkPieces();		
			waitForStatus(ConveyorConstants.SHIFT_FINISHED_WP_OK);
		} else {
			throw new IllegalArgumentException("Illegal workarea: " + putSettings.getWorkArea());
		}
	}

	@Override
	public void interventionFinished(final DeviceInterventionSettings interventionSettings) throws AbstractCommunicationException, DeviceActionException, InterruptedException {
		// release interlock
		if (interventionSettings.getWorkArea().equals(workAreaA)) {
			int command = 0;
			command = command | ConveyorConstants.RELEASE_INTERLOCK_A;
			int[] commandReg = {command};
			getSocketCommunication().writeRegisters(ConveyorConstants.COMMAND_REG, commandReg);
			waitForStatusNot(ConveyorConstants.CONV_A_INTERLOCK);
		} else if (interventionSettings.getWorkArea().equals(workAreaB)) {
			int command = 0;
			command = command | ConveyorConstants.RELEASE_INTERLOCK_B;
			int[] commandReg = {command};
			getSocketCommunication().writeRegisters(ConveyorConstants.COMMAND_REG, commandReg);
			waitForStatusNot(ConveyorConstants.CONV_B_INTERLOCK);
		} else {
			throw new IllegalArgumentException("Illegal workarea: " + interventionSettings.getWorkArea());
		}
	}

	@Override 
	public void releasePiece(final DevicePickSettings pickSettings) throws AbstractCommunicationException, DeviceActionException, InterruptedException {
		for (ConveyorListener listener : getListeners()) {
			listener.layoutChanged();
		}
	}

	@Override
	public void grabPiece(final DevicePutSettings putSettings) throws AbstractCommunicationException, DeviceActionException, InterruptedException {
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
				setRawWorkPiece(settings.getRawWorkPiece());
			} else {
				setRawWorkPiece(new WorkPiece(WorkPiece.Type.RAW, new WorkPieceDimensions(), Material.OTHER, 0.0f));
			}
			if (settings.getFinishedWorkPiece() != null) {
				setFinishedWorkPiece(settings.getFinishedWorkPiece());
			} else {
				setFinishedWorkPiece(new WorkPiece(WorkPiece.Type.FINISHED, new WorkPieceDimensions(), Material.OTHER, 0.0f));
			}
			this.amount = settings.getAmount();
			try {
				layout.configureRawWorkPieceStackingPositions();
				layout.configureFinishedWorkPieceStackingPositions();
			} catch (IncorrectWorkPieceDataException e) {
				e.printStackTrace();
			}
		} else {
			throw new IllegalArgumentException("Expected conveyor settings, got " + deviceSettings + ".");
		}
	}

	@Override
	public DeviceSettings getDeviceSettings() {
		return new ConveyorSettings(getRawWorkPiece(), getFinishedWorkPiece(), amount);
	}

	@Override
	public Coordinates getPickLocation(final WorkArea workArea, final WorkPieceDimensions workPieceDimensions, final ClampingManner clampType) {
		if (workArea.equals(workAreaA)) {
			StackingPosition stPos = layout.getStackingPositionTrackA();
			return stPos.getPosition();
		} else if (workArea.equals(workAreaB)) {
			if (isTrackBModeLoad()) {
				StackingPosition stPos = layout.getStackingPositionTrackB();
				return stPos.getPosition();
			} else {
				throw new IllegalArgumentException("Track B mode is unload...");
			}
		} else {
			throw new IllegalArgumentException("Illegal workarea: " + workArea);
		}
	}

	@Override
	public Coordinates getPutLocation(final WorkArea workArea, final WorkPieceDimensions workPieceDimensions, final ClampingManner clampType) {
		if (workArea.equals(workAreaB)) {
			if (!isTrackBModeLoad()) {
				StackingPosition stPos = layout.getStackingPositionTrackB();
				return stPos.getPosition();
			} else {
				throw new IllegalArgumentException("Track B mode is load...");
			}
		} else {
			throw new IllegalArgumentException("Illegal workarea: " + workArea);
		}
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(final int amount) {
		this.amount = amount;
	}

	@Override
	public Coordinates getLocationOrientation(final WorkArea workArea, final ClampingManner clampType) {
		if (workArea.equals(workAreaA)) {
			return layout.getStackingPositionTrackA().getPosition();
		} else if (workArea.equals(workAreaB)) {
			return layout.getStackingPositionTrackB().getPosition();
		} else {
			throw new IllegalArgumentException("Illegal workarea: " + workArea);
		}
	}
	
	public void notifyFinishedShifted() {
		for (ConveyorListener listener : getListeners()) {
			listener.finishedShifted(getFinishedWorkPiece().getDimensions().getLength() + workPieceShift);
		}
	}

	@Override
	public void interruptCurrentAction() {
		// no action needed
	}
	
	@Override
	public DeviceType getType() {
		return DeviceType.CONVEYOR_EATON;
	}
	
	@Override
	public DevicePickSettings getDefaultPickSettings() {
		return new DevicePickSettings(this, workAreaA);
	}
	
	@Override
	public DevicePutSettings getDefaultPutSettings() {
		return new DevicePutSettings(this, workAreaB);
	}
	
}
