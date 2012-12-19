package eu.robojob.irscw.process;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.robojob.irscw.external.communication.AbstractCommunicationException;
import eu.robojob.irscw.external.device.AbstractDevice;
import eu.robojob.irscw.external.device.DeviceActionException;
import eu.robojob.irscw.external.device.DevicePickSettings;
import eu.robojob.irscw.external.robot.AbstractRobot;
import eu.robojob.irscw.external.robot.RobotActionException;
import eu.robojob.irscw.external.robot.RobotPickSettings;
import eu.robojob.irscw.positioning.Coordinates;
import eu.robojob.irscw.positioning.TeachedCoordinatesCalculator;
import eu.robojob.irscw.process.event.StatusChangedEvent;

public class PickStep extends AbstractTransportStep {

	private static Logger logger = LogManager.getLogger(PickStep.class.getName());
	
	private DevicePickSettings pickSettings;
	private RobotPickSettings robotPickSettings;
			
	public PickStep(final ProcessFlow processFlow, final AbstractRobot robot, final AbstractDevice deviceFrom, final DevicePickSettings pickSettings, final RobotPickSettings robotPickSettings) {
		super(processFlow, deviceFrom, robot);
		setDeviceSettings(pickSettings);
		setRobotSettings(robotPickSettings);
	}
	
	public PickStep(final AbstractRobot robot, final AbstractDevice deviceFrom, final DevicePickSettings pickSettings, final RobotPickSettings robotPickSettings) {
		this(null, robot, deviceFrom, pickSettings, robotPickSettings);
	}
	
	@Override
	public void executeStep(final int workPieceId) throws AbstractCommunicationException, RobotActionException, DeviceActionException, InterruptedException {
		executeStep(false, workPieceId);
	}
	
	@Override
	public void executeStepTeached(final int workPieceId) throws AbstractCommunicationException, RobotActionException, DeviceActionException, InterruptedException {
		executeStep(true, workPieceId);
	}

	private void executeStep(final boolean teached, final int workPieceId) throws AbstractCommunicationException, RobotActionException, DeviceActionException, InterruptedException {
		// check if the parent process has locked the devices to be used
		if (!getDevice().lock(getProcessFlow())) {
			throw new IllegalStateException("Device [" + getDevice() + "] was already locked by [" + getDevice().getLockingProcess() + "].");
		} else {
			if (!getRobot().lock(getProcessFlow())) {
				throw new IllegalStateException("Robot [" + getRobot() + "] was already locked by [" + getRobot().getLockingProcess() + "].");
			} else {
				getProcessFlow().processProcessFlowEvent(new StatusChangedEvent(getProcessFlow(), this, StatusChangedEvent.STARTED, workPieceId));
				Coordinates originalPosition = new Coordinates(getDevice().getPickLocation(pickSettings.getWorkArea(), getProcessFlow().getClampingType()));
				if (needsTeaching()) {
					Coordinates position = new Coordinates(originalPosition);
					logger.debug("Original coordinates: " + position + ".");
					if (getRelativeTeachedOffset() == null) {
						if (!teached) {
							throw new IllegalStateException("Teaching was needed, but no relative offset value available and 'teach mode' is not active!");
						}
					} else {
						logger.debug("The teached offset that will be used: [" + getRelativeTeachedOffset() + "].");
						Coordinates absoluteOffset = TeachedCoordinatesCalculator.calculateAbsoluteOffset(position, getRelativeTeachedOffset());
						logger.debug("The absolute offset that will be used: [" + absoluteOffset + "].");
						position.offset(absoluteOffset);
						logger.debug("Exact pick location: [" + position + "].");
					}
					robotPickSettings.setLocation(position);
				} else {
					Coordinates position = new Coordinates(originalPosition);
					logger.debug("Exact pick location (calculated without teaching): [" + position + "].");
					robotPickSettings.setLocation(position);
				}
				getProcessFlow().processProcessFlowEvent(new StatusChangedEvent(getProcessFlow(), this, StatusChangedEvent.PREPARE_DEVICE, workPieceId));
				logger.debug("Initiating robot: [" + getRobot() + "] pick action.");
				getRobot().initiatePick(robotPickSettings);		// we send the robot to the (safe) IP point, at the same time, the device can start preparing
				logger.debug("Preparing [" + getDevice() + "] for pick using [" + getRobot() + "].");
				getDevice().prepareForPick(pickSettings);
				logger.debug("Device [" + getDevice() + "] prepared for pick.");
				if (teached && needsTeaching()) {
					getProcessFlow().processProcessFlowEvent(new StatusChangedEvent(getProcessFlow(), this, StatusChangedEvent.EXECUTE_TEACHED, workPieceId));
					getRobot().continuePickTillAtLocation();
					getProcessFlow().processProcessFlowEvent(new StatusChangedEvent(getProcessFlow(), this, StatusChangedEvent.TEACHING_NEEDED, workPieceId));
					getRobot().continuePickTillUnclampAck();
					getProcessFlow().processProcessFlowEvent(new StatusChangedEvent(getProcessFlow(), this, StatusChangedEvent.TEACHING_FINISHED, workPieceId));
					Coordinates robotPosition = getRobot().getPosition();
					Coordinates relTeachedOffset = TeachedCoordinatesCalculator.calculateRelativeTeachedOffset(originalPosition, robotPosition.calculateOffset(originalPosition));
					logger.info("The relative teached offset: [" + relTeachedOffset + "].");
					setRelativeTeachedOffset(relTeachedOffset);
				} else {
					getProcessFlow().processProcessFlowEvent(new StatusChangedEvent(getProcessFlow(), this, StatusChangedEvent.EXECUTE_NORMAL, workPieceId));
					getRobot().continuePickTillAtLocation();
					getRobot().continuePickTillUnclampAck();
				}
				logger.debug("Robot pick action succeeded, about to ask device [" + getDevice() +  "] to release piece.");
				getDevice().releasePiece(pickSettings);
				logger.debug("Device [" + getDevice() + "] released piece, about to finalize pick.");
				robotPickSettings.getGripperHead().getGripper().setWorkPiece(robotPickSettings.getWorkPiece());
				getRobot().continuePickTillIPPoint();
				getDevice().pickFinished(pickSettings);
				getDevice().release(getProcessFlow());
				getRobot().release(getProcessFlow());
				getProcessFlow().processProcessFlowEvent(new StatusChangedEvent(getProcessFlow(), this, StatusChangedEvent.ENDED, workPieceId));
				logger.debug("Pick ready (but not finalized).");
			}
		}
	}
	
	@Override
	public void finalizeStep() throws AbstractCommunicationException, RobotActionException, InterruptedException {
		if (!getRobot().lock(getProcessFlow())) {
			throw new IllegalStateException("Robot [" + getRobot() + "] was already locked by " + getRobot().getLockingProcess());
		} else {
			getRobot().finalizePick();
			getRobot().release(getProcessFlow());
			logger.debug("Finalized pick");
		}
	}
	
	@Override
	public String toString() {
		return "PickStep from [" + getDevice() + "] with [" + getRobot() + "].";
	}

	@Override
	public DevicePickSettings getDeviceSettings() {
		return pickSettings;
	}

	@Override
	public ProcessStepType getType() {
		return ProcessStepType.PICK_STEP;
	}

	@Override
	public RobotPickSettings getRobotSettings() {
		return robotPickSettings;
	}
	
	public void setDeviceSettings(final DevicePickSettings settings) {
		this.pickSettings = settings;
		if (pickSettings != null) {
			pickSettings.setStep(this);
		}
	}

	public void setRobotSettings(final RobotPickSettings settings) {
		this.robotPickSettings = settings;
		if (robotPickSettings != null) {
			robotPickSettings.setStep(this);
		}
	}

	@Override
	public boolean needsTeaching() {
		//TODO implement
		return true;
	}

}
