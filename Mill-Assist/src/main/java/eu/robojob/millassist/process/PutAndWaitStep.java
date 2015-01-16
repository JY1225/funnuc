package eu.robojob.millassist.process;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.robojob.millassist.external.communication.AbstractCommunicationException;
import eu.robojob.millassist.external.device.DeviceActionException;
import eu.robojob.millassist.external.device.DevicePutSettings;
import eu.robojob.millassist.external.robot.RobotActionException;
import eu.robojob.millassist.external.robot.RobotPutSettings;
import eu.robojob.millassist.positioning.Coordinates;
import eu.robojob.millassist.positioning.TeachedCoordinatesCalculator;
import eu.robojob.millassist.process.event.StatusChangedEvent;
import eu.robojob.millassist.process.execution.ProcessExecutor;

public class PutAndWaitStep extends PutStep {
	
	private static Logger logger = LogManager.getLogger(PutAndWaitStep.class.getName());

	public PutAndWaitStep(final ProcessFlow processFlow, final DevicePutSettings putSettings, final RobotPutSettings robotPutSettings) {
		super(processFlow, putSettings, robotPutSettings);
	}

	public PutAndWaitStep(final DevicePutSettings putSettings, final RobotPutSettings robotPutSettings) {
		super(putSettings, robotPutSettings);
	}
	
	@Override
	public void executeStep(final int workPieceId, final ProcessExecutor executor) throws AbstractCommunicationException, RobotActionException, DeviceActionException, InterruptedException {
		executeStep(false, workPieceId, executor);
	}
	
	@Override
	public void executeStepTeached(final int workPieceId, final ProcessExecutor executor) throws AbstractCommunicationException, RobotActionException, DeviceActionException, InterruptedException {
		executeStep(true, workPieceId, executor);
	}
	
	private void executeStep(final boolean teached, final int processId, final ProcessExecutor executor) throws AbstractCommunicationException, RobotActionException, DeviceActionException, InterruptedException {
		// check if the parent process has locked the devices to be used
		if (!getDevice().lock(getProcessFlow())) {
			throw new IllegalStateException("Device [" + getDevice() + "] was already locked by [" + getDevice().getLockingProcess() + "].");
		} else {
			if (!getRobot().lock(getProcessFlow())) {
				getDevice().release();
				throw new IllegalStateException("Robot [" + getRobot() + "] was already locked by [" + getRobot().getLockingProcess() + "].");
			} else {
				try {
					checkProcessExecutorStatus(executor);
					getProcessFlow().processProcessFlowEvent(new StatusChangedEvent(getProcessFlow(), this, StatusChangedEvent.STARTED, processId));
					Coordinates originalPosition = new Coordinates(getDevice().getPutLocation(getDeviceSettings().getWorkArea(), getRobotSettings().getGripperHead().getGripper().getWorkPiece().getDimensions(), getProcessFlow().getClampingType(), getRobotSettings().getApproachType()));
					if (needsTeaching()) {
						Coordinates position = new Coordinates(originalPosition);
						logger.debug("Original coordinates: " + position + ".");
						if (getRelativeTeachedOffset() == null) {
							if (originalPosition.getZ() < getDeviceSettings().getWorkArea().getDefaultClamping().getRelativePosition().getZ() + getDeviceSettings().getWorkArea().getDefaultClamping().getHeight()) {
								//float extraOffset = (getDeviceSettings().getWorkArea().getActiveClamping().getRelativePosition().getZ() + getDeviceSettings().getWorkArea().getActiveClamping().getHeight()) - (originalPosition.getZ() + getRobotSettings().getGripperHead().getGripper().getWorkPiece().getDimensions().getHeight());
								float extraOffset = (getDeviceSettings().getWorkArea().getDefaultClamping().getRelativePosition().getZ() + getDeviceSettings().getWorkArea().getDefaultClamping().getHeight()) - originalPosition.getZ();
								setRelativeTeachedOffset(new Coordinates(0, 0, extraOffset, 0, 0, 0));
							}
						}
						if (getRelativeTeachedOffset() == null) {
							if (!teached) {
								throw new IllegalStateException("Teaching was needed, but no relative offset value available and 'teach mode' is not active!");
							}
						} else {
							logger.debug("The relative teached offset that will be used: [" + getRelativeTeachedOffset() + "].");
							Coordinates absoluteOffset = TeachedCoordinatesCalculator.calculateAbsoluteOffset(position, getRelativeTeachedOffset());
							logger.debug("The absolute offset that will be used: [" + absoluteOffset + "].");
							position.offset(absoluteOffset);
							logger.debug("Exact put location: [" + position + "].");
						}
						getRobotSettings().setLocation(position);
					} else {
						Coordinates position = new Coordinates(originalPosition);
						logger.debug("Exact put location (calculated without teaching): [" + position + "].");
						getRobotSettings().setLocation(position);
					}
					checkProcessExecutorStatus(executor);
					getProcessFlow().processProcessFlowEvent(new StatusChangedEvent(getProcessFlow(), this, StatusChangedEvent.PREPARE_DEVICE, processId));
					logger.debug("Initiating robot: [" + getRobot() + "] move-and-wait action.");
					getRobotSettings().setTeachingNeeded(teached);
					checkProcessExecutorStatus(executor);
					getRobot().initiateMoveWithPiece(getRobotSettings());		// we send the robot to the (safe) IP point, at the same time, the device can start preparing
					logger.debug("Preparing [" + getDevice() + "] for move-and-wait using [" + getRobot() + "].");
					checkProcessExecutorStatus(executor);
					getDevice().prepareForPut(getDeviceSettings(), processId);
					logger.debug("Device [" + getDevice() + "] prepared for move-and-wait.");
					checkProcessExecutorStatus(executor);
					if (teached && needsTeaching()) {
						getProcessFlow().processProcessFlowEvent(new StatusChangedEvent(getProcessFlow(), this, StatusChangedEvent.EXECUTE_TEACHED, processId));
						checkProcessExecutorStatus(executor);
						getRobot().continueMoveTillAtLocation();
						getProcessFlow().processProcessFlowEvent(new StatusChangedEvent(getProcessFlow(), this, StatusChangedEvent.TEACHING_NEEDED, processId));
						checkProcessExecutorStatus(executor);
						getRobot().continueMoveTillWait();
						checkProcessExecutorStatus(executor);
						getProcessFlow().processProcessFlowEvent(new StatusChangedEvent(getProcessFlow(), this, StatusChangedEvent.TEACHING_FINISHED, processId));
						Coordinates robotPosition = getRobot().getPosition();
						Coordinates relTeachedOffset = TeachedCoordinatesCalculator.calculateRelativeTeachedOffset(originalPosition, robotPosition.calculateOffset(originalPosition));
						logger.info("The relative teached offset: [" + relTeachedOffset + "].");
						setRelativeTeachedOffset(relTeachedOffset);
					} else {
						getProcessFlow().processProcessFlowEvent(new StatusChangedEvent(getProcessFlow(), this, StatusChangedEvent.EXECUTE_NORMAL, processId));
						checkProcessExecutorStatus(executor);
						getRobot().continueMoveTillAtLocation();
						checkProcessExecutorStatus(executor);
						getRobot().continueMoveTillWait();
					}
					logger.debug("Robot move-and-wait action succeeded, about to ask device [" + getDevice() +  "] to grab piece.");
					checkProcessExecutorStatus(executor);
					getDevice().grabPiece(getDeviceSettings());
					logger.debug("Device [" + getDevice() + "] grabbed piece, about to finalize put.");				
					getProcessFlow().processProcessFlowEvent(new StatusChangedEvent(getProcessFlow(), this, StatusChangedEvent.ENDED, processId));
				} catch (AbstractCommunicationException | RobotActionException | DeviceActionException | InterruptedException e) {
					throw e;
				} finally {
					getRobot().release();
					getDevice().release();
				}
			}
		}
	}
	
	@Override
	public void finalizeStep(final ProcessExecutor executor) throws AbstractCommunicationException, RobotActionException, InterruptedException {
		if (!getRobot().lock(getProcessFlow())) {
			throw new IllegalStateException("Robot [" + getRobot() + "] was already locked by " + getRobot().getLockingProcess());
		} else {
			// no finalize action here, the finalization is to be done by the pick after wait step
			getRobot().release();
			logger.debug("Finalized put-and-wait.");
		}
	}
	
}
