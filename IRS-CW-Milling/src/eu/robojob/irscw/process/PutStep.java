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
import eu.robojob.irscw.process.event.ActiveStepChangedEvent;

public class PutStep extends AbstractTransportStep {

	protected DevicePutSettings putSettings;
	protected RobotPutSettings robotPutSettings;
	
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
		if (!device.lock(processFlow)) {
			throw new IllegalStateException("Device " + device + " was already locked by: " + device.getLockingProcess());
		} else {
			if (!robot.lock(processFlow)) {
				throw new IllegalStateException("Robot " + robot + " was already locked by: " + robot.getLockingProcess());
			} else {
				logger.debug("About to execute put in " + device.getId() + " using " + robot.getId());
				processFlow.processProcessFlowEvent(new ActiveStepChangedEvent(processFlow, this, ActiveStepChangedEvent.PUT_PREPARE_DEVICE));
				logger.debug("Preparing device...");
				device.prepareForPut(putSettings);
				logger.debug("Device prepared.");
				processFlow.processProcessFlowEvent(new ActiveStepChangedEvent(processFlow, this, ActiveStepChangedEvent.PUT_EXECUTE_NORMAL));
				if (needsTeaching()) {
					logger.debug("The exact put location should have been teached: " + relativeTeachedOffset);
					if (relativeTeachedOffset == null) {
						throw new IllegalStateException("Unknown teached position");
					} else {
						Coordinates position = new Coordinates(device.getPutLocation(putSettings.getWorkArea(), robotPutSettings.getGripperHead().getGripper().getWorkPiece().getDimensions(), processFlow.getClampingType()));
						logger.debug("Normal coordinates: " + position);
						Coordinates absoluteOffset = calculator.calculateAbsoluteOffset(position, relativeTeachedOffset);
						position.offset(absoluteOffset);
						logger.debug("Coordinates after adding teached offset: " + position);
						robotPutSettings.setLocation(position);
					}
				} else {
					Coordinates position = new Coordinates(device.getPutLocation(putSettings.getWorkArea(), robotPutSettings.getGripperHead().getGripper().getWorkPiece().getDimensions(), processFlow.getClampingType()));
					logger.debug("The location of this put was calculated (no teaching): " + position);
					robotPutSettings.setLocation(position);
				}
				logger.debug("Robot initiating put action");
				robot.initiatePut(robotPutSettings);
				logger.debug("Robot action succeeded, about to ask device to grab piece");
				device.grabPiece(putSettings);
				logger.debug("Device grabbed piece, about to finalize put");
				robot.finalizePut(robotPutSettings);
				device.putFinished(putSettings);
				processFlow.processProcessFlowEvent(new ActiveStepChangedEvent(processFlow, this, ActiveStepChangedEvent.PUT_FINISHED));
				logger.debug("Put finished");
			}
		}
	}

	@Override
	public void prepareForTeaching() throws AbstractCommunicationException, RobotActionException, DeviceActionException, InterruptedException {
		if (!device.lock(processFlow)) {
			throw new IllegalStateException("Device " + device + " was already locked by: " + device.getLockingProcess());
		} else {
			if (!robot.lock(processFlow)) {
				throw new IllegalStateException("Robot " + robot + " was already locked by: " + robot.getLockingProcess());
			} else {
				logger.debug("About to execute put using teaching in " + device.getId() + " using " + robot.getId());
				processFlow.processProcessFlowEvent(new ActiveStepChangedEvent(processFlow, this, ActiveStepChangedEvent.PUT_PREPARE_DEVICE));
				logger.debug("Preparing device...");
				device.prepareForPut(putSettings);
				logger.debug("Device prepared...");
				Coordinates coordinates = new Coordinates(device.getPutLocation(putSettings.getWorkArea(), robotPutSettings.getGripperHead().getGripper().getWorkPiece().getDimensions(), processFlow.getClampingType()));
				logger.info("Coordinates before teaching: " + coordinates);
				if (relativeTeachedOffset != null) {
					Coordinates c = calculator.calculateAbsoluteOffset(coordinates, relativeTeachedOffset);
					coordinates.offset(c);
					logger.info("Coordinates before teaching (added teached offset): " + coordinates);
				}
				robotPutSettings.setLocation(coordinates);
				processFlow.processProcessFlowEvent(new ActiveStepChangedEvent(processFlow, this, ActiveStepChangedEvent.PUT_EXECUTE_TEACHED));
				logger.debug("Robot initiating put action");
				robot.initiateTeachedPut(robotPutSettings);
				logger.debug("Robot action succeeded");
			}
		}
	}

	@Override
	public void teachingFinished() throws AbstractCommunicationException, RobotActionException, DeviceActionException, InterruptedException {
		if (!device.lock(processFlow)) {
			throw new IllegalStateException("Device " + device + " was already locked by: " + device.getLockingProcess());
		} else {
			if (!robot.lock(processFlow)) {
				throw new IllegalStateException("Robot " + robot + " was already locked by: " + robot.getLockingProcess());
			} else {
				logger.debug("Teaching finished");
				Coordinates coordinates = new Coordinates(robot.getPosition());
				Coordinates oldCoordinates = new Coordinates(device.getPutLocation(putSettings.getWorkArea(), robotPutSettings.getGripperHead().getGripper().getWorkPiece().getDimensions(), processFlow.getClampingType()));
				this.relativeTeachedOffset = coordinates.calculateOffset(oldCoordinates);
				this.relativeTeachedOffset = calculator.calculateRelativeTeachedOffset(oldCoordinates, relativeTeachedOffset);
				logger.debug("The teached offset is: " + relativeTeachedOffset);
				robotPutSettings.setLocation(device.getPutLocation(putSettings.getWorkArea(), robotPutSettings.getGripperHead().getGripper().getWorkPiece().getDimensions(), processFlow.getClampingType()));
				logger.debug("About to ask device to grab piece");
				device.grabPiece(putSettings);
				logger.debug("Device grabbed piece, about to finalize put");
				robot.finalizeTeachedPut(robotPutSettings);
				processFlow.processProcessFlowEvent(new ActiveStepChangedEvent(processFlow, this, ActiveStepChangedEvent.PUT_FINISHED));
				device.putFinished(putSettings);
				logger.debug("Put finished");
			}
		}
	}

	@Override
	public String toString() {
		return "PutStep to " + device + " using " + robot;
	}

	@Override
	public void finalize() throws AbstractCommunicationException, DeviceActionException {
		if (!device.lock(processFlow)) {
			throw new IllegalStateException("Device " + device + " was already locked by: " + device.getLockingProcess());
		} else {
			if (!robot.lock(processFlow)) {
				throw new IllegalStateException("Robot " + robot + " was already locked by: " + robot.getLockingProcess());
			} else {
				device.putFinished(putSettings);
				device.release(processFlow);
				robot.release(processFlow);
				processFlow.processProcessFlowEvent(new ActiveStepChangedEvent(processFlow, this, ActiveStepChangedEvent.PUT_FINISHED));
			}
		}
	}
	
	@Override
	public Set<AbstractServiceProvider> getServiceProviders() {
		Set<AbstractServiceProvider> providers = new HashSet<AbstractServiceProvider>();
		providers.add(device);
		providers.add(robot);
		return providers;
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
