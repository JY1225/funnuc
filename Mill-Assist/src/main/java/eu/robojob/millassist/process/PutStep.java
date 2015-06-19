package eu.robojob.millassist.process;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.robojob.millassist.external.communication.AbstractCommunicationException;
import eu.robojob.millassist.external.device.AbstractDevice;
import eu.robojob.millassist.external.device.DeviceActionException;
import eu.robojob.millassist.external.device.DeviceInterventionSettings;
import eu.robojob.millassist.external.device.DevicePutSettings;
import eu.robojob.millassist.external.device.processing.reversal.ReversalUnit;
import eu.robojob.millassist.external.robot.AbstractRobot;
import eu.robojob.millassist.external.robot.AbstractRobotActionSettings.ApproachType;
import eu.robojob.millassist.external.robot.RobotActionException;
import eu.robojob.millassist.external.robot.RobotPutSettings;
import eu.robojob.millassist.positioning.Coordinates;
import eu.robojob.millassist.positioning.TeachedCoordinatesCalculator;
import eu.robojob.millassist.process.event.StatusChangedEvent;
import eu.robojob.millassist.process.execution.ProcessExecutor;
import eu.robojob.millassist.process.execution.fixed.ProcessFlowExecutionThread;

public class PutStep extends AbstractTransportStep {

	private DevicePutSettings devicePutSettings;
	private RobotPutSettings robotPutSettings;
    private InterventionStep removeFinishedInterventionStep;
    private static final String INTERVENTION_REMOVE_FINISHED = "Status.removeFinished";
	
	private static Logger logger = LogManager.getLogger(PutStep.class.getName());
	
	public PutStep(final ProcessFlow processFlow, final DevicePutSettings devicePutSettings, final RobotPutSettings robotPutSettings) {
		super(processFlow);
		this.devicePutSettings = devicePutSettings;
		setDeviceSettings(devicePutSettings);
		setRobotSettings(robotPutSettings);
	}
	
	public PutStep(final DevicePutSettings devicePutSettings, final RobotPutSettings robotPutSettings) {
		this(null, devicePutSettings, robotPutSettings);
	}
	
	@Override
	public void executeStep(final int processId, final ProcessExecutor executor) throws AbstractCommunicationException, RobotActionException, DeviceActionException, InterruptedException {
		executeStep(false, processId, executor);
	}

	@Override
	public void executeStepTeached(final int workPieceId, final ProcessExecutor executor) throws AbstractCommunicationException, DeviceActionException, RobotActionException, InterruptedException {
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
	                
					@SuppressWarnings("unchecked")
					Coordinates originalPosition = new Coordinates(getDevice().getPutLocation(
                            getProcessFlow().getPiecePlacementVisitor(getRobotSettings().getGripperHead().getGripper().getWorkPiece().getShape()),
                            getDeviceSettings().getWorkArea(), 
                            getRobotSettings().getGripperHead().getGripper().getWorkPiece().getDimensions(), 
                            getProcessFlow().getClampingType(), 
                            getRobotSettings().getApproachType()));
					if (needsTeaching()) {
						Coordinates position = new Coordinates(originalPosition);
						logger.debug("Original coordinates: " + position + ".");
						if (getRelativeTeachedOffset() == null) {
							initSafeTeachedOffset(originalPosition);
						}
						logger.debug("The teached offset that will be used: [" + getRelativeTeachedOffset() + "].");
						Coordinates absoluteOffset = TeachedCoordinatesCalculator.calculateAbsoluteOffset(position, getRelativeTeachedOffset());
						logger.debug("The absolute offset that will be used: [" + absoluteOffset + "].");
						position.offset(absoluteOffset);
						logger.debug("Exact put location: [" + position + "].");
						getRobotSettings().setLocation(position);
					} else {
						Coordinates position = new Coordinates(originalPosition);
						logger.debug("Exact put location (calculated without teaching): [" + position + "].");
						getRobotSettings().setLocation(position);
					}
					checkProcessExecutorStatus(executor);
					getProcessFlow().processProcessFlowEvent(new StatusChangedEvent(getProcessFlow(), this, StatusChangedEvent.PREPARE_DEVICE, processId));
					logger.debug("Initiating robot: [" + getRobot() + "] put action.");
					getRobotSettings().setTeachingNeeded(teached);
					checkProcessExecutorStatus(executor);
					if (executor instanceof ProcessFlowExecutionThread) {
						robotPutSettings.setIsTIMPut(((ProcessFlowExecutionThread) executor).isTIMPossible());
					} else {
						robotPutSettings.setIsTIMPut(false);
					}
					getRobot().initiatePut(getRobotSettings(), getDeviceSettings().getWorkArea().getWorkAreaManager().getActiveClamping(false, getDeviceSettings().getWorkArea().getSequenceNb()));		// we send the robot to the (safe) IP point, at the same time, the device can start preparing
					logger.debug("Preparing [" + getDevice() + "] for put using [" + getRobot() + "].");
					checkProcessExecutorStatus(executor);
					getDevice().prepareForPut(getDeviceSettings(), processId);
					logger.debug("Device [" + getDevice() + "] prepared for put.");
					checkProcessExecutorStatus(executor);
					if (teached && needsTeaching()) {
						getProcessFlow().processProcessFlowEvent(new StatusChangedEvent(getProcessFlow(), this, StatusChangedEvent.EXECUTE_TEACHED, processId));
						checkProcessExecutorStatus(executor);
						getRobot().continuePutTillAtLocation();
						getProcessFlow().processProcessFlowEvent(new StatusChangedEvent(getProcessFlow(), this, StatusChangedEvent.TEACHING_NEEDED, processId));
						checkProcessExecutorStatus(executor);
						getRobot().continuePutTillClampAck();
						checkProcessExecutorStatus(executor);
						getProcessFlow().processProcessFlowEvent(new StatusChangedEvent(getProcessFlow(), this, StatusChangedEvent.TEACHING_FINISHED, processId));
						Coordinates robotPosition = getRobot().getPosition();
						Coordinates relTeachedOffset = TeachedCoordinatesCalculator.calculateRelativeTeachedOffset(originalPosition, robotPosition.calculateOffset(originalPosition));
						logger.info("The relative teached offset: [" + relTeachedOffset + "].");
						setRelativeTeachedOffset(relTeachedOffset);
					} else {
						getProcessFlow().processProcessFlowEvent(new StatusChangedEvent(getProcessFlow(), this, StatusChangedEvent.EXECUTE_NORMAL, processId));
						checkProcessExecutorStatus(executor);
						getRobot().continuePutTillAtLocation();
						checkProcessExecutorStatus(executor);
						getRobot().continuePutTillClampAck();
					}
					logger.debug("Robot put action succeeded, about to ask device [" + getDevice() +  "] to grab piece.");
					checkProcessExecutorStatus(executor);
					getDevice().grabPiece(getDeviceSettings());
					checkProcessExecutorStatus(executor);
					logger.debug("Device [" + getDevice() + "] grabbed piece, about to finalize put.");
					robotPutSettings.getGripperHead().getGripper().setWorkPiece(null);
					checkProcessExecutorStatus(executor);
					getRobot().continuePutTillIPPoint();
					checkProcessExecutorStatus(executor);
					getDevice().putFinished(getDeviceSettings());
					getProcessFlow().processProcessFlowEvent(new StatusChangedEvent(getProcessFlow(), this, StatusChangedEvent.ENDED, processId));
					logger.debug("Put ready (but not finalized).");
				} catch (AbstractCommunicationException | RobotActionException | DeviceActionException | InterruptedException e) {
					throw e;
				} finally {
					getDevice().release();
					getRobot().release();
				}
			}
		}
	}
	
	/**
	 * Ajust teached offset, so robot is in a safe position before teaching
	 * @param devicePutSettings
	 * @param originalPosition
	 */
	private void initSafeTeachedOffset(final Coordinates originalPosition) {
		float extraOffsetX = 0;
		float extraOffsetY = 0;
		float extraOffsetZ = 0;
		if ((getDevice() instanceof ReversalUnit) && !(getRobotSettings().getApproachType().equals(ApproachType.TOP))) {
			if (getRobotSettings().getApproachType().equals(ApproachType.BOTTOM)) {
				extraOffsetZ = - ((ReversalUnit) getDevice()).getStationHeight();
			} else if (getRobotSettings().getApproachType().equals(ApproachType.FRONT)) {
				extraOffsetX = ((ReversalUnit) getDevice()).getStationLength() - originalPosition.getX();
			}  else if (getRobotSettings().getApproachType().equals(ApproachType.FRONT)) {
				extraOffsetY = - ((ReversalUnit) getDevice()).getStationFixtureWidth();
			}
		} else {
			if (originalPosition.getZ() < getDeviceSettings().getWorkArea().getWorkAreaManager().getActiveClamping(false, getDeviceSettings().getWorkArea().getSequenceNb()).getRelativePosition().getZ() + 
					getDeviceSettings().getWorkArea().getDefaultClamping().getHeight()) {
				extraOffsetZ = (getDeviceSettings().getWorkArea().getWorkAreaManager().getActiveClamping(false, getDeviceSettings().getWorkArea().getSequenceNb()).getRelativePosition().getZ() 
						+ getDeviceSettings().getWorkArea().getWorkAreaManager().getActiveClamping(false, getDeviceSettings().getWorkArea().getSequenceNb()).getHeight()) 
						- originalPosition.getZ();
			}
		}
		setRelativeTeachedOffset(TeachedCoordinatesCalculator.calculateRelativeTeachedOffset(originalPosition, new Coordinates(extraOffsetX, extraOffsetY, extraOffsetZ, 0, 0, 0)));
	}
	
	@Override
	public void finalizeStep(final ProcessExecutor executor) throws AbstractCommunicationException, RobotActionException, InterruptedException {
		if (!getRobot().lock(getProcessFlow())) {
			throw new IllegalStateException("Robot [" + getRobot() + "] was already locked by " + getRobot().getLockingProcess());
		} else {
			getRobot().finalizePut();
			checkProcessExecutorStatus(executor);
			getRobot().release();
			logger.debug("Finalized put.");
		}
	}
	
	public InterventionStep getInterventionStep() {
	    if(removeFinishedInterventionStep == null) {
	        removeFinishedInterventionStep = new InterventionStep(getProcessFlow(), new DeviceInterventionSettings(getDevice(), getDeviceSettings().getWorkArea()), 1, INTERVENTION_REMOVE_FINISHED);
	    }
	    return removeFinishedInterventionStep;
	}
	
	public boolean mustExecuteInterventionStep() {
	    @SuppressWarnings("unchecked")
	    Coordinates putLocation = getDevice().getPutLocation(
                getProcessFlow().getPiecePlacementVisitor(getRobotSettings().getGripperHead().getGripper().getWorkPiece().getShape()),
                getDeviceSettings().getWorkArea(), 
                getRobotSettings().getGripperHead().getGripper().getWorkPiece().getDimensions(), 
                getProcessFlow().getClampingType(), 
                getRobotSettings().getApproachType());
        return putLocation == null;
	}

	@Override
	public String toString() {
		return "PutStep to [" + getDevice() + "] using [" + getRobot() + "].";
	}

	@Override
	public DevicePutSettings getDeviceSettings() {
		return devicePutSettings;
	}

	@Override
	public ProcessStepType getType() {
		return ProcessStepType.PUT_STEP;
	}

	@Override
	public RobotPutSettings getRobotSettings() {
		return robotPutSettings;
	}
	
	public void setRobotSettings(final RobotPutSettings settings) {
		this.robotPutSettings = settings;
		if (robotPutSettings != null) {
			robotPutSettings.setStep(this);
		}
	}
	
	public void setDeviceSettings(final DevicePutSettings settings) {
		this.devicePutSettings = settings;
		if (devicePutSettings != null) {
			devicePutSettings.setStep(this);
		}
	}
	
	@Override
	public boolean needsTeaching() {
		//TODO implement
		return true;
	}

	@Override
	public AbstractRobot getRobot() {
		return robotPutSettings.getRobot();
	}

	@Override
	public AbstractDevice getDevice() {
		return devicePutSettings.getDevice();
	}

}
