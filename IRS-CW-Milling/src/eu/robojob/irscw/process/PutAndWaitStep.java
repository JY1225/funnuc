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
import eu.robojob.irscw.process.event.StatusChangedEvent;

public class PutAndWaitStep extends PutStep {
	
	private static Logger logger = LogManager.getLogger(PutAndWaitStep.class.getName());

	public PutAndWaitStep(final ProcessFlow processFlow, final AbstractRobot robot, final AbstractDevice deviceTo, final DevicePutSettings putSettings, final RobotPutSettings robotPutSettings) {
		super(processFlow, robot, deviceTo, putSettings, robotPutSettings);
	}

	public PutAndWaitStep(final AbstractRobot robot, final AbstractDevice deviceTo, final DevicePutSettings putSettings, final RobotPutSettings robotPutSettings) {
		super(robot, deviceTo, putSettings, robotPutSettings);
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
				Coordinates originalPosition = new Coordinates(getDevice().getPutLocation(getDeviceSettings().getWorkArea(), getRobotSettings().getGripperHead().getGripper().getWorkPiece().getDimensions(), getProcessFlow().getClampingType()));
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
						position.offset(absoluteOffset);
						logger.debug("Exact put location: [" + position + "].");
					}
					getRobotSettings().setLocation(position);
				} else {
					Coordinates position = new Coordinates(originalPosition);
					logger.debug("Exact put location (calculated without teaching): [" + position + "].");
					getRobotSettings().setLocation(position);
				}
				getProcessFlow().processProcessFlowEvent(new StatusChangedEvent(getProcessFlow(), this, StatusChangedEvent.PREPARE_DEVICE, workPieceId));
				logger.debug("Initiating robot: [" + getRobot() + "] move-and-wait action.");
				getRobot().initiateMoveWithPiece(getRobotSettings());		// we send the robot to the (safe) IP point, at the same time, the device can start preparing
				logger.debug("Preparing [" + getDevice() + "] for move-and-wait using [" + getRobot() + "].");
				getDevice().prepareForPut(getDeviceSettings());
				logger.debug("Device [" + getDevice() + "] prepared for move-and-wait.");
				if (teached && needsTeaching()) {
					getProcessFlow().processProcessFlowEvent(new StatusChangedEvent(getProcessFlow(), this, StatusChangedEvent.EXECUTE_TEACHED, workPieceId));
					getRobot().continueMoveWithPieceTillAtLocation();
					getProcessFlow().processProcessFlowEvent(new StatusChangedEvent(getProcessFlow(), this, StatusChangedEvent.TEACHING_NEEDED, workPieceId));
					Coordinates robotPosition = getRobot().getPosition();
					setRelativeTeachedOffset(TeachedCoordinatesCalculator.calculateRelativeTeachedOffset(originalPosition, robotPosition.calculateOffset(originalPosition)));
					getRobot().continueMoveWithPieceTillWait();
					getProcessFlow().processProcessFlowEvent(new StatusChangedEvent(getProcessFlow(), this, StatusChangedEvent.TEACHING_FINISHED, workPieceId));
				} else {
					getProcessFlow().processProcessFlowEvent(new StatusChangedEvent(getProcessFlow(), this, StatusChangedEvent.EXECUTE_NORMAL, workPieceId));
					getRobot().continueMoveWithPieceTillAtLocation();
					getRobot().continueMoveWithPieceTillWait();
				}
				logger.debug("Robot move-and-wait action succeeded, about to ask device [" + getDevice() +  "] to grab piece.");
				getDevice().grabPiece(getDeviceSettings());
				logger.debug("Device [" + getDevice() + "] grabbed piece, about to finalize put.");				
				getDevice().grabPiece(getDeviceSettings());
				getProcessFlow().processProcessFlowEvent(new StatusChangedEvent(getProcessFlow(), this, StatusChangedEvent.ENDED, workPieceId));
			}
		}
	}
	
}
