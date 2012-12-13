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
import eu.robojob.irscw.process.event.ActiveStepChangedEvent;

public class PickStep extends AbstractTransportStep {

	private static Logger logger = LogManager.getLogger(PickStep.class.getName());
	
	private DevicePickSettings pickSettings;
	private RobotPickSettings robotPickSettings;
			
	public PickStep(final ProcessFlow processFlow, final AbstractRobot robot, final AbstractDevice deviceFrom, final DevicePickSettings pickSettings, final RobotPickSettings robotPickSettings) {
		super(processFlow, deviceFrom, robot);
		this.pickSettings = pickSettings;
		if (pickSettings != null) {
			pickSettings.setStep(this);
		}
		if (robotPickSettings != null) {
			robotPickSettings.setStep(this);
		}
		setRobotSettings(robotPickSettings);
	}
	
	public PickStep(final AbstractRobot robot, final AbstractDevice deviceFrom, final DevicePickSettings pickSettings, final RobotPickSettings robotPickSettings) {
		this(null, robot, deviceFrom, pickSettings, robotPickSettings);
	}

	@Override
	public void executeStep() throws AbstractCommunicationException, RobotActionException, DeviceActionException, InterruptedException {
		// check if the parent process has locked the devices to be used
		if (!getDevice().lock(getProcessFlow())) {
			throw new IllegalStateException("Device " + getDevice() + " was already locked by: " + getDevice().getLockingProcess());
		} else {
			if (!getRobot().lock(getProcessFlow())) {
				throw new IllegalStateException("Robot " + getRobot() + " was already locked by: " + getRobot().getLockingProcess());
			} else {
				logger.debug("About to execute pick in " + getDevice().getId() + " using " + getRobot().getId());
				getProcessFlow().processProcessFlowEvent(new ActiveStepChangedEvent(getProcessFlow(), this, ActiveStepChangedEvent.PICK_PREPARE_DEVICE));
				logger.debug("Preparing device...");
				getDevice().prepareForPick(pickSettings);
				logger.debug("Device prepared.");
				getProcessFlow().processProcessFlowEvent(new ActiveStepChangedEvent(getProcessFlow(), this, ActiveStepChangedEvent.PICK_EXECUTE_NORMAL));
				if (needsTeaching()) {
					logger.debug("The exact pick location should have been teached: " + getRelativeTeachedOffset());
					if (getRelativeTeachedOffset() == null) {
						throw new IllegalStateException("Unknown teached position");
					} else {
						Coordinates position = new Coordinates(getDevice().getPickLocation(pickSettings.getWorkArea(), getProcessFlow().getClampingType()));
						logger.debug("Normal coordinates: " + position);
						Coordinates absoluteOffset = TeachedCoordinatesCalculator.calculateAbsoluteOffset(position, getRelativeTeachedOffset());
						position.offset(absoluteOffset);
						logger.debug("Coordinates after adding teached offset: " + position);
						robotPickSettings.setLocation(position);
					}
				} else {
					Coordinates position = new Coordinates(getDevice().getPickLocation(pickSettings.getWorkArea(), getProcessFlow().getClampingType()));
					logger.debug("The location of this pick was calculated (no teaching): " + position);
					robotPickSettings.setLocation(position);
				}
				logger.debug("Robot initiating pick action");
				getRobot().initiatePick(robotPickSettings);
				logger.debug("Robot action succeeded, about to ask device to release piece");
				getDevice().releasePiece(pickSettings);
				logger.debug("Device released piece, about to finalize pick");
				getRobot().finalizePick(robotPickSettings);
				robotPickSettings.getGripperHead().getGripper().setWorkPiece(robotPickSettings.getWorkPiece());
				//robot.moveToHome();
				getDevice().pickFinished(pickSettings);
				logger.debug("Pick finished");
			}
		}
	}

	@Override
	public void prepareForTeaching() throws AbstractCommunicationException, RobotActionException, DeviceActionException, InterruptedException {
		if (!getDevice().lock(getProcessFlow())) {
			throw new IllegalStateException("Device " + getDevice() + " was already locked by: " + getDevice().getLockingProcess());
		} else {
			if (!getRobot().lock(getProcessFlow())) {
				throw new IllegalStateException("Robot " + getRobot() + " was already locked by: " + getRobot().getLockingProcess());
			} else {
				logger.debug("About to execute pick using teaching in " + getDevice().getId() + " using " + getRobot().getId());
				getProcessFlow().processProcessFlowEvent(new ActiveStepChangedEvent(getProcessFlow(), this, ActiveStepChangedEvent.PICK_PREPARE_DEVICE));
				logger.debug("Preparing device...");
				getDevice().prepareForPick(pickSettings);
				logger.debug("Device prepared...");
				Coordinates coordinates = new Coordinates(getDevice().getPickLocation(pickSettings.getWorkArea(), getProcessFlow().getClampingType()));
				logger.info("Coordinates before teaching: " + coordinates);
				if (getRelativeTeachedOffset() != null) {
					Coordinates c = TeachedCoordinatesCalculator.calculateAbsoluteOffset(coordinates, getRelativeTeachedOffset());
					coordinates.offset(c);
					logger.info("Coordinates before teaching (added teached offset): " + coordinates);
				}
				robotPickSettings.setLocation(coordinates);
				getProcessFlow().processProcessFlowEvent(new ActiveStepChangedEvent(getProcessFlow(), this, ActiveStepChangedEvent.PICK_EXECUTE_TEACHED));
				logger.debug("Robot initiating pick action");
				getRobot().initiateTeachedPick(robotPickSettings);
				logger.debug("Robot action succeeded");
			}
		}
	}

	@Override
	public void teachingFinished() throws AbstractCommunicationException, RobotActionException, DeviceActionException, InterruptedException {
		if (!getDevice().lock(getProcessFlow())) {
			throw new IllegalStateException("Device " + getDevice() + " was already locked by: " + getDevice().getLockingProcess());
		} else {
			if (!getRobot().lock(getProcessFlow())) {
				throw new IllegalStateException("Robot " + getRobot() + " was already locked by: " + getRobot().getLockingProcess());
			} else {
				logger.debug("Teaching finished");
				Coordinates coordinates = new Coordinates(getRobot().getPosition());
				Coordinates oldCoordinates = new Coordinates(getDevice().getPickLocation(pickSettings.getWorkArea(), getProcessFlow().getClampingType()));
				setRelativeTeachedOffset(coordinates.calculateOffset(oldCoordinates));
				setRelativeTeachedOffset(TeachedCoordinatesCalculator.calculateRelativeTeachedOffset(oldCoordinates, getRelativeTeachedOffset()));
				logger.debug("The teached offset is: " + getRelativeTeachedOffset());
				robotPickSettings.setLocation(getDevice().getPickLocation(pickSettings.getWorkArea(), getProcessFlow().getClampingType()));
				logger.debug("About to ask device to release piece");
				getDevice().releasePiece(pickSettings);
				logger.debug("Device released piece, about to finalize pick");
				getRobot().finalizeTeachedPick(robotPickSettings);
				getDevice().pickFinished(pickSettings);
				getProcessFlow().processProcessFlowEvent(new ActiveStepChangedEvent(getProcessFlow(), this, ActiveStepChangedEvent.PICK_FINISHED));
				logger.debug("Pick finished");
			}
		}
	}
	
	@Override
	//TODO review!!
	public void finalize() throws AbstractCommunicationException, DeviceActionException {
		if (!getDevice().lock(getProcessFlow())) {
			throw new IllegalStateException("Device " + getDevice() + " was already locked by: " + getDevice().getLockingProcess());
		} else {
			if (!getRobot().lock(getProcessFlow())) {
				throw new IllegalStateException("Robot " + getRobot() + " was already locked by: " + getRobot().getLockingProcess());
			} else {
				getDevice().pickFinished(pickSettings);
				getDevice().release(getProcessFlow());
				getRobot().release(getProcessFlow());
				getProcessFlow().processProcessFlowEvent(new ActiveStepChangedEvent(getProcessFlow(), this, ActiveStepChangedEvent.PICK_FINISHED));
			}
		}
	}

	@Override
	public String toString() {
		return "PickStep from " + getDevice() + " with: " + getRobot();
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
