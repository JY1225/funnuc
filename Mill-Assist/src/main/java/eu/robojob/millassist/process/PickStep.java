package eu.robojob.millassist.process;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.robojob.millassist.external.communication.AbstractCommunicationException;
import eu.robojob.millassist.external.device.AbstractDevice;
import eu.robojob.millassist.external.device.DeviceActionException;
import eu.robojob.millassist.external.device.DevicePickSettings;
import eu.robojob.millassist.external.device.processing.reversal.ReversalUnit;
import eu.robojob.millassist.external.robot.AbstractRobot;
import eu.robojob.millassist.external.robot.AbstractRobotActionSettings.ApproachType;
import eu.robojob.millassist.external.robot.RobotActionException;
import eu.robojob.millassist.external.robot.RobotPickSettings;
import eu.robojob.millassist.positioning.Coordinates;
import eu.robojob.millassist.positioning.TeachedCoordinatesCalculator;
import eu.robojob.millassist.process.event.StatusChangedEvent;
import eu.robojob.millassist.process.execution.ProcessExecutor;
import eu.robojob.millassist.process.execution.fixed.ProcessFlowExecutionThread;

public class PickStep extends AbstractTransportStep {

	private static Logger logger = LogManager.getLogger(PickStep.class.getName());
	
	private DevicePickSettings devicePickSettings;
	private RobotPickSettings robotPickSettings;
			
	public PickStep(final ProcessFlow processFlow, final DevicePickSettings devicePickSettings, final RobotPickSettings robotPickSettings) {
		super(processFlow);
		setDeviceSettings(devicePickSettings);
		setRobotSettings(robotPickSettings);
	}
	
	public PickStep(final DevicePickSettings devicePickSettings, final RobotPickSettings robotPickSettings) {
		this(null, devicePickSettings, robotPickSettings);
	}
	
	@Override
	public void executeStep(final int processId, final ProcessExecutor executor) throws AbstractCommunicationException, RobotActionException, DeviceActionException, InterruptedException {
		executeStep(false, processId, executor);
	}
	
	@Override
	public void executeStepTeached(final int processId, final ProcessExecutor executor) throws AbstractCommunicationException, RobotActionException, DeviceActionException, InterruptedException {
		executeStep(true, processId, executor);
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
					while(!getDevice().canPick(devicePickSettings)) {
						logger.info("Waiting till can pick");
						Thread.sleep(500);
					}
					logger.info("Can pick - " + devicePickSettings.getWorkArea());
					checkProcessExecutorStatus(executor);
					getProcessFlow().processProcessFlowEvent(new StatusChangedEvent(getProcessFlow(), this, StatusChangedEvent.STARTED, processId));
					Coordinates originalPosition = new Coordinates(getDevice().getPickLocation(devicePickSettings.getWorkArea(), getRobotSettings().getWorkPiece().getDimensions(), getProcessFlow().getClampingType(), getRobotSettings().getApproachType()));
					if (needsTeaching()) {
						Coordinates position = new Coordinates(originalPosition);
						logger.debug("Original coordinates: " + position + ".");
						if (getRelativeTeachedOffset() == null) {
							float extraOffsetZ = 0;
							// (originalPosition.getZ() + workPiece.getHeight()) < (activeClamping.getZ() + activeClamping.getHeight()) 
							if (originalPosition.getZ() + getRobotSettings().getWorkPiece().getDimensions().getHeight() < 
									getDeviceSettings().getWorkArea().getWorkAreaManager().getActiveClamping(true, getDeviceSettings().getWorkArea().getSequenceNb()).getRelativePosition().getZ() 
									+ getDeviceSettings().getWorkArea().getWorkAreaManager().getActiveClamping(true, getDeviceSettings().getWorkArea().getSequenceNb()).getHeight()) {
								// extraOffsetZ = activeClamping.getZ() + activeClamping.getHeight() - originalPosition.getZ() - workPiece.getHeight()
								extraOffsetZ = (getDeviceSettings().getWorkArea().getWorkAreaManager().getActiveClamping(true, getDeviceSettings().getWorkArea().getSequenceNb()).getRelativePosition().getZ() 
										+ getDeviceSettings().getWorkArea().getWorkAreaManager().getActiveClamping(true, getDeviceSettings().getWorkArea().getSequenceNb()).getHeight()) 
										- (originalPosition.getZ() + getRobotSettings().getWorkPiece().getDimensions().getHeight());
								if(devicePickSettings.getDevice() instanceof ReversalUnit && (getRobotSettings().getApproachType().equals(ApproachType.BOTTOM))) {
									extraOffsetZ -= ((ReversalUnit) devicePickSettings.getDevice()).getStationHeight();
								}
							} else if(devicePickSettings.getDevice() instanceof ReversalUnit && (getRobotSettings().getApproachType().equals(ApproachType.BOTTOM))) {
								extraOffsetZ = ((ReversalUnit) devicePickSettings.getDevice()).getStationHeight() * -1;
							}
							setRelativeTeachedOffset(new Coordinates(0, 0, extraOffsetZ, 0, 0, 0));
						}
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
					checkProcessExecutorStatus(executor);
					getProcessFlow().processProcessFlowEvent(new StatusChangedEvent(getProcessFlow(), this, StatusChangedEvent.PREPARE_DEVICE, processId));
					logger.debug("Initiating robot: [" + getRobot() + "] pick action.");
					getRobotSettings().setTeachingNeeded(teached);
					checkProcessExecutorStatus(executor);
					if (executor instanceof ProcessFlowExecutionThread) {
						robotPickSettings.setIsTIMPick(((ProcessFlowExecutionThread) executor).isTIMPossible());
					} else {
						robotPickSettings.setIsTIMPick(false);
					}
					getRobot().initiatePick(robotPickSettings, getDeviceSettings().getWorkArea().getWorkAreaManager().getActiveClamping(true, getDeviceSettings().getWorkArea().getSequenceNb()));		// we send the robot to the (safe) IP point, at the same time, the device can start preparing
					logger.debug("Preparing [" + getDevice() + "] for pick using [" + getRobot() + "].");
					checkProcessExecutorStatus(executor);
					getDevice().prepareForPick(devicePickSettings, processId);
					logger.debug("Device [" + getDevice() + "] prepared for pick.");
					checkProcessExecutorStatus(executor);
					if (teached && needsTeaching()) {
						getProcessFlow().processProcessFlowEvent(new StatusChangedEvent(getProcessFlow(), this, StatusChangedEvent.EXECUTE_TEACHED, processId));
						checkProcessExecutorStatus(executor);
						getRobot().continuePickTillAtLocation();
						checkProcessExecutorStatus(executor);
						getProcessFlow().processProcessFlowEvent(new StatusChangedEvent(getProcessFlow(), this, StatusChangedEvent.TEACHING_NEEDED, processId));
						checkProcessExecutorStatus(executor);
						getRobot().continuePickTillUnclampAck();
						checkProcessExecutorStatus(executor);
						getProcessFlow().processProcessFlowEvent(new StatusChangedEvent(getProcessFlow(), this, StatusChangedEvent.TEACHING_FINISHED, processId));
						Coordinates robotPosition = getRobot().getPosition();
						Coordinates relTeachedOffset = TeachedCoordinatesCalculator.calculateRelativeTeachedOffset(originalPosition, robotPosition.calculateOffset(originalPosition));
						logger.info("The relative teached offset: [" + relTeachedOffset + "].");
						setRelativeTeachedOffset(relTeachedOffset);
					} else {
						getProcessFlow().processProcessFlowEvent(new StatusChangedEvent(getProcessFlow(), this, StatusChangedEvent.EXECUTE_NORMAL, processId));
						checkProcessExecutorStatus(executor);
						getRobot().continuePickTillAtLocation();
						checkProcessExecutorStatus(executor);
						getRobot().continuePickTillUnclampAck();
					}
					logger.debug("Robot pick action succeeded, about to ask device [" + getDevice() +  "] to release piece.");
					checkProcessExecutorStatus(executor);
					getDevice().releasePiece(devicePickSettings);
					logger.debug("Device [" + getDevice() + "] released piece, about to finalize pick.");
					robotPickSettings.getGripperHead().getGripper().setWorkPiece(robotPickSettings.getWorkPiece());
					checkProcessExecutorStatus(executor);
					getRobot().continuePickTillIPPoint();
					checkProcessExecutorStatus(executor);
					getDevice().pickFinished(devicePickSettings, processId);
					getProcessFlow().processProcessFlowEvent(new StatusChangedEvent(getProcessFlow(), this, StatusChangedEvent.ENDED, processId));
					logger.debug("Pick ready (but not finalized).");
				} catch (AbstractCommunicationException | RobotActionException | DeviceActionException | InterruptedException e) {
					throw e;
				} finally {
					getDevice().release();
					getRobot().release();
				}
			}
		}
	}
	
	@Override
	public void finalizeStep(final ProcessExecutor executor) throws AbstractCommunicationException, RobotActionException, InterruptedException {
		if (!getRobot().lock(getProcessFlow())) {
			throw new IllegalStateException("Robot [" + getRobot() + "] was already locked by " + getRobot().getLockingProcess());
		} else {
			try {
				checkProcessExecutorStatus(executor);
				getRobot().finalizePick();
				logger.debug("Finalized pick");
			} catch(AbstractCommunicationException | RobotActionException | InterruptedException e) {
				throw e;
			} finally {
				getRobot().release();
			}
		}
	}
	
	@Override
	public String toString() {
		return "PickStep from [" + getDevice() + "] with [" + getRobot() + "].";
	}

	@Override
	public DevicePickSettings getDeviceSettings() {
		return devicePickSettings;
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
		this.devicePickSettings = settings;
		if (devicePickSettings != null) {
			devicePickSettings.setStep(this);
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

	@Override
	public AbstractRobot getRobot() {
		return robotPickSettings.getRobot();
	}

	@Override
	public AbstractDevice getDevice() {
		return devicePickSettings.getDevice();
	}

}
