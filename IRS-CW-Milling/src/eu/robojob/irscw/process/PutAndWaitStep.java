package eu.robojob.irscw.process;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

public class PutAndWaitStep extends PutStep {
	
	private static final Logger logger = LogManager.getLogger(PutAndWaitStep.class.getName());

	public PutAndWaitStep(ProcessFlow processFlow, AbstractRobot robot, AbstractDevice deviceTo, DevicePutSettings putSettings, RobotPutSettings robotPutSettings) {
		super(processFlow, robot, deviceTo, putSettings, robotPutSettings);
	}

	public PutAndWaitStep(AbstractRobot robot, AbstractDevice deviceTo, DevicePutSettings putSettings, RobotPutSettings robotPutSettings) {
		super(robot, deviceTo, putSettings, robotPutSettings);
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
				logger.debug("About to execute put and wait in " + getDevice().getId() + " using " + getRobot().getId());
				getProcessFlow().processProcessFlowEvent(new ActiveStepChangedEvent(getProcessFlow(), this, ActiveStepChangedEvent.PUT_PREPARE_DEVICE));
				logger.debug("Preparing device...");
				getDevice().prepareForPut(getDeviceSettings());
				logger.debug("Device prepared.");
				getProcessFlow().processProcessFlowEvent(new ActiveStepChangedEvent(getProcessFlow(), this, ActiveStepChangedEvent.PUT_EXECUTE_NORMAL));
				if (needsTeaching()) {
					logger.debug("The exact put and wait location should have been teached: " + getRelativeTeachedOffset());
					if (getRelativeTeachedOffset() == null) {
						throw new IllegalStateException("Unknown teached position");
					} else {
						Coordinates position = new Coordinates(getDevice().getPutLocation(getDeviceSettings().getWorkArea(), getRobotSettings().getGripperHead().getGripper().getWorkPiece().getDimensions(), getProcessFlow().getClampingType()));
						logger.debug("Normal coordinates: " + position);
						Coordinates absoluteOffset = TeachedCoordinatesCalculator.calculateAbsoluteOffset(position, getRelativeTeachedOffset());
						position.offset(absoluteOffset);
						logger.debug("Coordinates after adding teached offset: " + position);
						getRobotSettings().setLocation(position);
					}
				} else {
					Coordinates position = new Coordinates(getDevice().getPutLocation(getDeviceSettings().getWorkArea(), getRobotSettings().getGripperHead().getGripper().getWorkPiece().getDimensions(), getProcessFlow().getClampingType()));
					logger.debug("The location of this put and wait was calculated (no teaching): " + position);
					getRobotSettings().setLocation(position);
				}
				logger.debug("Robot initiating put and wait action");
				getRobot().initiateMoveWithPiece(getRobotSettings());
				getRobot().continueMoveWithPieceTillAtLocation();
				getRobot().continueMoveWithPieceTillWait();
				logger.debug("Robot action succeeded, about to ask device to grab piece");
				getDevice().grabPiece(getDeviceSettings());
				getProcessFlow().processProcessFlowEvent(new ActiveStepChangedEvent(getProcessFlow(), this, ActiveStepChangedEvent.PUT_FINISHED));
				logger.debug("Put and wait finished");
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
				logger.debug("About to execute put and wait using teaching in " + getDevice().getId() + " using " + getRobot().getId());
				getProcessFlow().processProcessFlowEvent(new ActiveStepChangedEvent(getProcessFlow(), this, ActiveStepChangedEvent.PUT_PREPARE_DEVICE));
				logger.debug("Preparing device...");
				getDevice().prepareForPut(getDeviceSettings());
				logger.debug("Device prepared...");
				Coordinates coordinates = new Coordinates(getDevice().getPutLocation(getDeviceSettings().getWorkArea(), getRobotSettings().getGripperHead().getGripper().getWorkPiece().getDimensions(), getProcessFlow().getClampingType()));
				logger.info("Coordinates before teaching: " + coordinates);
				if (getRelativeTeachedOffset() != null) {
					Coordinates c = TeachedCoordinatesCalculator.calculateAbsoluteOffset(coordinates, getRelativeTeachedOffset());
					coordinates.offset(c);
					logger.info("Coordinates before teaching (added teached offset): " + coordinates);
				}
				getRobotSettings().setLocation(coordinates);
				getProcessFlow().processProcessFlowEvent(new ActiveStepChangedEvent(getProcessFlow(), this, ActiveStepChangedEvent.PUT_EXECUTE_TEACHED));
				logger.debug("Robot initiating put and wait action");
				getRobot().initiateMoveWithPiece(getRobotSettings());
				getRobot().continueMoveWithPieceTillAtLocation();
				getRobot().continueMoveWithPieceTillAtLocation();
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
				Coordinates oldCoordinates = new Coordinates(getDevice().getPutLocation(getDeviceSettings().getWorkArea(), getRobotSettings().getGripperHead().getGripper().getWorkPiece().getDimensions(), getProcessFlow().getClampingType()));
				setRelativeTeachedOffset(coordinates.calculateOffset(oldCoordinates));
				logger.debug("original coordinates: " + oldCoordinates);
				logger.debug("the new position is: " + coordinates);
				logger.debug("The teached offset is: " + getRelativeTeachedOffset());
				setRelativeTeachedOffset(TeachedCoordinatesCalculator.calculateRelativeTeachedOffset(oldCoordinates, getRelativeTeachedOffset()));
				logger.debug("The teached offset is (absolute): " + getRelativeTeachedOffset());
				getRobotSettings().setLocation(getDevice().getPutLocation(getDeviceSettings().getWorkArea(), getRobotSettings().getGripperHead().getGripper().getWorkPiece().getDimensions(), getProcessFlow().getClampingType()));
				logger.debug("About to ask device to grab piece");
				getDevice().grabPiece(getDeviceSettings());
				logger.debug("Device grabbed piece, about to finalize put");
				getProcessFlow().processProcessFlowEvent(new ActiveStepChangedEvent(getProcessFlow(), this, ActiveStepChangedEvent.PUT_FINISHED));
				getDevice().putFinished(getDeviceSettings());
				logger.debug("Put and wait finished");
			}
		}
	}
}
