package eu.robojob.irscw.process;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import eu.robojob.irscw.external.AbstractServiceProvider;
import eu.robojob.irscw.external.communication.CommunicationException;
import eu.robojob.irscw.external.device.AbstractDevice;
import eu.robojob.irscw.external.device.AbstractDevice.AbstractDevicePutSettings;
import eu.robojob.irscw.external.device.DeviceActionException;
import eu.robojob.irscw.external.robot.AbstractRobot;
import eu.robojob.irscw.external.robot.AbstractRobot.AbstractRobotPutSettings;
import eu.robojob.irscw.external.robot.RobotActionException;
import eu.robojob.irscw.positioning.Coordinates;
import eu.robojob.irscw.process.event.ActiveStepChangedEvent;

public class PutStep extends AbstractTransportStep {

	private AbstractDevice.AbstractDevicePutSettings putSettings;
	private AbstractRobot.AbstractRobotPutSettings robotPutSettings;
	
	private static final Logger logger = Logger.getLogger(PutStep.class);
	
	public PutStep(ProcessFlow processFlow, AbstractRobot robot, AbstractDevice deviceTo,
			AbstractDevice.AbstractDevicePutSettings putSettings, AbstractRobot.AbstractRobotPutSettings robotPutSettings) {
		super(processFlow, deviceTo, robot);
		this.putSettings = putSettings;
		if (putSettings != null) {
			putSettings.setStep(this);
		}
		setRobotSettings(robotPutSettings);
	}
	
	public PutStep(AbstractRobot robot, AbstractDevice deviceTo, AbstractDevice.AbstractDevicePutSettings putSettings,
			AbstractRobot.AbstractRobotPutSettings robotPutSettings) {
		this(null, robot, deviceTo, putSettings, robotPutSettings);
	}

	@Override
	public void executeStep() throws CommunicationException, RobotActionException, DeviceActionException {
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
					logger.debug("The exact pick location should have been teached: " + teachedOffset);
					if (teachedOffset == null) {
						throw new IllegalStateException("Unknown teached position");
					} else {
						Coordinates position = new Coordinates(device.getPutLocation(putSettings.getWorkArea(), robotPutSettings.getGripper().getWorkPiece().getDimensions()));
						logger.debug("Normal coordinates: " + position);
						position.offset(teachedOffset);
						logger.debug("Coordinates after adding teached offset: " + position);
						robotPutSettings.setLocation(position);
					}
				} else {
					Coordinates position = new Coordinates(device.getPutLocation(putSettings.getWorkArea(), robotPutSettings.getGripper().getWorkPiece().getDimensions()));
					/*// no offset needed? sometimes there is! use offset of corresponding pick!
					// getting pick step:
					PickStep pickStep = (PickStep) processFlow.getStep(processFlow.getStepIndex(this) - 1);
					if (pickStep.needsTeaching()) {
						position.offset(pickStep.getTeachedOffset());
					}*/
					logger.debug("The location of this put was calculated (no teaching): " + position);
					robotPutSettings.setLocation(position);
				}
				logger.debug("Robot initiating put action");
				robot.initiatePut(robotPutSettings);
				logger.debug("Robot action succeeded, about to ask device to grab piece");
				device.grabPiece(putSettings);
				logger.debug("Device grabbed piece, about to finalize put");
				robot.finalizePut(robotPutSettings);
				robot.moveToHome();
				device.putFinished(putSettings);
				processFlow.processProcessFlowEvent(new ActiveStepChangedEvent(processFlow, this, ActiveStepChangedEvent.PUT_FINISHED));
				logger.debug("Put finished");
			}
		}
	}

	@Override
	public void prepareForTeaching() throws CommunicationException, RobotActionException, DeviceActionException {
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
				Coordinates coordinates = new Coordinates(device.getPutLocation(putSettings.getWorkArea(), robotPutSettings.getGripper().getWorkPiece().getDimensions()));
				logger.info("Coordinates before teaching: " + coordinates);
				robotPutSettings.setLocation(coordinates);
				processFlow.processProcessFlowEvent(new ActiveStepChangedEvent(processFlow, this, ActiveStepChangedEvent.PUT_EXECUTE_TEACHED));
				logger.debug("Robot initiating pick action");
				robot.initiateTeachedPut(robotPutSettings);
				logger.debug("Robot action succeeded");
			}
		}
	}

	@Override
	public void teachingFinished() throws CommunicationException, RobotActionException, DeviceActionException {
		if (!device.lock(processFlow)) {
			throw new IllegalStateException("Device " + device + " was already locked by: " + device.getLockingProcess());
		} else {
			if (!robot.lock(processFlow)) {
				throw new IllegalStateException("Robot " + robot + " was already locked by: " + robot.getLockingProcess());
			} else {
				logger.debug("Teaching finished");
				Coordinates coordinates = new Coordinates(robot.getPosition());
				Coordinates oldCoordinates = new Coordinates(device.getPutLocation(putSettings.getWorkArea(), robotPutSettings.getGripper().getWorkPiece().getDimensions()));
				this.teachedOffset = coordinates.calculateOffset(oldCoordinates);
				logger.debug("The teached offset is: " + teachedOffset);
				robotPutSettings.setLocation(device.getPutLocation(putSettings.getWorkArea(), robotPutSettings.getGripper().getWorkPiece().getDimensions()));
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
	public void finalize() throws CommunicationException, DeviceActionException {
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
	public AbstractDevicePutSettings getDeviceSettings() {
		return putSettings;
	}

	@Override
	public ProcessStepType getType() {
		return ProcessStepType.PUT_STEP;
	}

	@Override
	public AbstractRobotPutSettings getRobotSettings() {
		return robotPutSettings;
	}
	
	public void setRobotSettings(AbstractRobotPutSettings settings) {
		this.robotPutSettings = settings;
		if (robotPutSettings != null) {
			robotPutSettings.setPutStep(this);
		}
	}

	@Override
	public boolean needsTeaching() {
		// since we already know the work piece's dimensions (ground pane) and griper height from picking it up
		/*if (putSettings.isPutPositionFixed()) {
			return false;
		} else {
			return true;
		}*/
		return true;
	}
}
