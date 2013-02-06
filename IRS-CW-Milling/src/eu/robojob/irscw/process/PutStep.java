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

public class PutStep extends AbstractTransportStep {

	private DevicePutSettings devicePutSettings;
	private RobotPutSettings robotPutSettings;
	
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
	public void executeStep(final int workPieceId) throws AbstractCommunicationException, RobotActionException, DeviceActionException, InterruptedException {
		executeStep(false, workPieceId);
	}

	@Override
	public void executeStepTeached(final int workPieceId) throws AbstractCommunicationException, DeviceActionException, RobotActionException, InterruptedException {
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
				getProcessFlow().processProcessFlowEvent(new StatusChangedEvent(getProcessFlow(), this, StatusChangedEvent.PREPARE_DEVICE, workPieceId));
				logger.debug("Initiating robot: [" + getRobot() + "] put action.");
				getRobotSettings().setTeachingNeeded(teached);
				getRobot().initiatePut(getRobotSettings());		// we send the robot to the (safe) IP point, at the same time, the device can start preparing
				logger.debug("Preparing [" + getDevice() + "] for put using [" + getRobot() + "].");
				getDevice().prepareForPut(getDeviceSettings());
				logger.debug("Device [" + getDevice() + "] prepared for put.");
				if (teached && needsTeaching()) {
					getProcessFlow().processProcessFlowEvent(new StatusChangedEvent(getProcessFlow(), this, StatusChangedEvent.EXECUTE_TEACHED, workPieceId));
					getRobot().continuePutTillAtLocation();
					getProcessFlow().processProcessFlowEvent(new StatusChangedEvent(getProcessFlow(), this, StatusChangedEvent.TEACHING_NEEDED, workPieceId));
					getRobot().continuePutTillClampAck();
					getProcessFlow().processProcessFlowEvent(new StatusChangedEvent(getProcessFlow(), this, StatusChangedEvent.TEACHING_FINISHED, workPieceId));
					Coordinates robotPosition = getRobot().getPosition();
					Coordinates relTeachedOffset = TeachedCoordinatesCalculator.calculateRelativeTeachedOffset(originalPosition, robotPosition.calculateOffset(originalPosition));
					logger.info("The relative teached offset: [" + relTeachedOffset + "].");
					setRelativeTeachedOffset(relTeachedOffset);
				} else {
					getProcessFlow().processProcessFlowEvent(new StatusChangedEvent(getProcessFlow(), this, StatusChangedEvent.EXECUTE_NORMAL, workPieceId));
					getRobot().continuePutTillAtLocation();
					getRobot().continuePutTillClampAck();
				}
				logger.debug("Robot put action succeeded, about to ask device [" + getDevice() +  "] to grab piece.");
				getDevice().grabPiece(getDeviceSettings());
				logger.debug("Device [" + getDevice() + "] grabbed piece, about to finalize put.");
				robotPutSettings.getGripperHead().getGripper().setWorkPiece(null);
				getRobot().continuePutTillIPPoint();
				getDevice().putFinished(getDeviceSettings());
				getDevice().release(getProcessFlow());
				getRobot().release(getProcessFlow());
				getProcessFlow().processProcessFlowEvent(new StatusChangedEvent(getProcessFlow(), this, StatusChangedEvent.ENDED, workPieceId));
				logger.debug("Put ready (but not finalized).");
			}
		}
	}
	
	@Override
	public void finalizeStep() throws AbstractCommunicationException, RobotActionException, InterruptedException {
		if (!getRobot().lock(getProcessFlow())) {
			throw new IllegalStateException("Robot [" + getRobot() + "] was already locked by " + getRobot().getLockingProcess());
		} else {
			getRobot().finalizePut();
			getRobot().release(getProcessFlow());
			logger.debug("Finalized put.");
		}
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
