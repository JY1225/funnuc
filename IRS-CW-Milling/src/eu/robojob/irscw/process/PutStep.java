package eu.robojob.irscw.process;

import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.robojob.irscw.external.AbstractServiceProvider;
import eu.robojob.irscw.external.communication.AbstractCommunicationException;
import eu.robojob.irscw.external.device.AbstractDevice;
import eu.robojob.irscw.external.device.DeviceActionException;
import eu.robojob.irscw.external.device.DevicePutSettings;
import eu.robojob.irscw.external.robot.AbstractRobot;
import eu.robojob.irscw.external.robot.RobotActionException;
import eu.robojob.irscw.external.robot.RobotPutSettings;
import eu.robojob.irscw.positioning.Coordinates;
import eu.robojob.irscw.positioning.TeachedCoordinatesCalculator;
import eu.robojob.irscw.process.event.ActiveStepChangedEvent;

public class PutStep extends AbstractTransportStep {

	private DevicePutSettings putSettings;
	private RobotPutSettings robotPutSettings;
	
	private static final Logger logger = LogManager.getLogger(PutStep.class.getName());
	
	public PutStep(ProcessFlow processFlow, AbstractRobot robot, AbstractDevice deviceTo,
			DevicePutSettings putSettings, RobotPutSettings robotPutSettings) {
		super(processFlow, deviceTo, robot);
		this.putSettings = putSettings;
		if (putSettings != null) {
			putSettings.setStep(this);
		}
		if (robotPutSettings != null) {
			robotPutSettings.setStep(this);
		}
		setRobotSettings(robotPutSettings);
	}
	
	public PutStep(AbstractRobot robot, AbstractDevice deviceTo, DevicePutSettings putSettings, RobotPutSettings robotPutSettings) {
		this(null, robot, deviceTo, putSettings, robotPutSettings);
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
				logger.debug("About to execute put in " + getDevice().getId() + " using " + getRobot().getId());
				getProcessFlow().processProcessFlowEvent(new ActiveStepChangedEvent(getProcessFlow(), this, ActiveStepChangedEvent.PUT_PREPARE_DEVICE));
				logger.debug("Preparing device...");
				getDevice().prepareForPut(putSettings);
				logger.debug("Device prepared.");
				getProcessFlow().processProcessFlowEvent(new ActiveStepChangedEvent(getProcessFlow(), this, ActiveStepChangedEvent.PUT_EXECUTE_NORMAL));
				if (needsTeaching()) {
					logger.debug("The exact put location should have been teached: " + getRelativeTeachedOffset());
					if (getRelativeTeachedOffset() == null) {
						throw new IllegalStateException("Unknown teached position");
					} else {
						Coordinates position = new Coordinates(getDevice().getPutLocation(putSettings.getWorkArea(), robotPutSettings.getGripperHead().getGripper().getWorkPiece().getDimensions(), getProcessFlow().getClampingType()));
						logger.debug("Normal coordinates: " + position);
						Coordinates absoluteOffset = TeachedCoordinatesCalculator.calculateAbsoluteOffset(position, getRelativeTeachedOffset());
						position.offset(absoluteOffset);
						logger.debug("Coordinates after adding teached offset: " + position);
						robotPutSettings.setLocation(position);
					}
				} else {
					Coordinates position = new Coordinates(getDevice().getPutLocation(putSettings.getWorkArea(), robotPutSettings.getGripperHead().getGripper().getWorkPiece().getDimensions(), getProcessFlow().getClampingType()));
					logger.debug("The location of this put was calculated (no teaching): " + position);
					robotPutSettings.setLocation(position);
				}
				logger.debug("Robot initiating put action");
				getRobot().initiatePut(robotPutSettings);
				logger.debug("Robot action succeeded, about to ask device to grab piece");
				getDevice().grabPiece(putSettings);
				logger.debug("Device grabbed piece, about to finalize put");
				getRobot().finalizePut(robotPutSettings);
				getDevice().putFinished(putSettings);
				getProcessFlow().processProcessFlowEvent(new ActiveStepChangedEvent(getProcessFlow(), this, ActiveStepChangedEvent.PUT_FINISHED));
				logger.debug("Put finished");
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
				logger.debug("About to execute put using teaching in " + getDevice().getId() + " using " + getRobot().getId());
				getProcessFlow().processProcessFlowEvent(new ActiveStepChangedEvent(getProcessFlow(), this, ActiveStepChangedEvent.PUT_PREPARE_DEVICE));
				logger.debug("Preparing device...");
				getDevice().prepareForPut(putSettings);
				logger.debug("Device prepared...");
				Coordinates coordinates = new Coordinates(getDevice().getPutLocation(putSettings.getWorkArea(), robotPutSettings.getGripperHead().getGripper().getWorkPiece().getDimensions(), getProcessFlow().getClampingType()));
				logger.info("Coordinates before teaching: " + coordinates);
				if (getRelativeTeachedOffset() != null) {
					Coordinates c = TeachedCoordinatesCalculator.calculateAbsoluteOffset(coordinates, getRelativeTeachedOffset());
					coordinates.offset(c);
					logger.info("Coordinates before teaching (added teached offset): " + coordinates);
				}
				robotPutSettings.setLocation(coordinates);
				getProcessFlow().processProcessFlowEvent(new ActiveStepChangedEvent(getProcessFlow(), this, ActiveStepChangedEvent.PUT_EXECUTE_TEACHED));
				logger.debug("Robot initiating put action");
				getRobot().initiateTeachedPut(robotPutSettings);
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
				Coordinates oldCoordinates = new Coordinates(getDevice().getPutLocation(putSettings.getWorkArea(), robotPutSettings.getGripperHead().getGripper().getWorkPiece().getDimensions(), getProcessFlow().getClampingType()));
				setRelativeTeachedOffset(coordinates.calculateOffset(oldCoordinates));
				setRelativeTeachedOffset(TeachedCoordinatesCalculator.calculateRelativeTeachedOffset(oldCoordinates, getRelativeTeachedOffset()));
				logger.debug("The teached offset is: " + getRelativeTeachedOffset());
				robotPutSettings.setLocation(getDevice().getPutLocation(putSettings.getWorkArea(), robotPutSettings.getGripperHead().getGripper().getWorkPiece().getDimensions(), getProcessFlow().getClampingType()));
				logger.debug("About to ask device to grab piece");
				getDevice().grabPiece(putSettings);
				logger.debug("Device grabbed piece, about to finalize put");
				getRobot().finalizeTeachedPut(robotPutSettings);
				getProcessFlow().processProcessFlowEvent(new ActiveStepChangedEvent(getProcessFlow(), this, ActiveStepChangedEvent.PUT_FINISHED));
				getDevice().putFinished(putSettings);
				logger.debug("Put finished");
			}
		}
	}

	@Override
	public String toString() {
		return "PutStep to " + getDevice() + " using " + getRobot();
	}

	@Override
	public void finalize() throws AbstractCommunicationException, DeviceActionException {
		if (!getDevice().lock(getProcessFlow())) {
			throw new IllegalStateException("Device " + getDevice() + " was already locked by: " + getDevice().getLockingProcess());
		} else {
			if (!getRobot().lock(getProcessFlow())) {
				throw new IllegalStateException("Robot " + getRobot() + " was already locked by: " + getRobot().getLockingProcess());
			} else {
				getDevice().putFinished(putSettings);
				getDevice().release(getProcessFlow());
				getRobot().release(getProcessFlow());
				getProcessFlow().processProcessFlowEvent(new ActiveStepChangedEvent(getProcessFlow(), this, ActiveStepChangedEvent.PUT_FINISHED));
			}
		}
	}

	@Override
	public DevicePutSettings getDeviceSettings() {
		return putSettings;
	}

	@Override
	public ProcessStepType getType() {
		return ProcessStepType.PUT_STEP;
	}

	@Override
	public RobotPutSettings getRobotSettings() {
		return robotPutSettings;
	}
	
	public void setRobotSettings(RobotPutSettings settings) {
		this.robotPutSettings = settings;
		if (robotPutSettings != null) {
			robotPutSettings.setStep(this);
		}
	}

	@Override
	public boolean needsTeaching() {
		//TODO implement
		return true;
	}
}
