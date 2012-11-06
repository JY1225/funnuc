package eu.robojob.irscw.process;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import eu.robojob.irscw.external.AbstractServiceProvider;
import eu.robojob.irscw.external.communication.CommunicationException;
import eu.robojob.irscw.external.device.AbstractDevice;
import eu.robojob.irscw.external.device.AbstractDevice.AbstractDevicePickSettings;
import eu.robojob.irscw.external.device.DeviceActionException;
import eu.robojob.irscw.external.robot.AbstractRobot;
import eu.robojob.irscw.external.robot.AbstractRobot.AbstractRobotPickSettings;
import eu.robojob.irscw.external.robot.RobotActionException;
import eu.robojob.irscw.positioning.Coordinates;
import eu.robojob.irscw.process.event.ActiveStepChangedEvent;

public class PickStep extends AbstractTransportStep {

	private static final Logger logger = Logger.getLogger(PickStep.class);
	
	private AbstractDevice.AbstractDevicePickSettings pickSettings;
	private AbstractRobot.AbstractRobotPickSettings robotPickSettings;
		
	public PickStep(ProcessFlow processFlow, AbstractRobot robot, AbstractDevice deviceFrom, AbstractDevice.AbstractDevicePickSettings pickSettings,
			AbstractRobot.AbstractRobotPickSettings robotPickSettings) {
		super(processFlow, deviceFrom, robot);
		this.pickSettings = pickSettings;
		if (pickSettings != null) {
			pickSettings.setStep(this);
		}
		setRobotSettings(robotPickSettings);
	}
	
	public PickStep(AbstractRobot robot, AbstractDevice deviceFrom, AbstractDevice.AbstractDevicePickSettings pickSettings,
			AbstractRobot.AbstractRobotPickSettings robotPickSettings) {
		this(null, robot, deviceFrom, pickSettings, robotPickSettings);
	}

	@Override
	public void executeStep() throws CommunicationException, RobotActionException, DeviceActionException {
		// check if the parent process has locked the devices to be used
		//TODO IMPLEMENT!!
		if (!device.lock(processFlow)) {
			throw new IllegalStateException("Device " + device + " was already locked by: " + device.getLockingProcess());
		} else {
			if (!robot.lock(processFlow)) {
				throw new IllegalStateException("Robot " + robot + " was already locked by: " + robot.getLockingProcess());
			} else {
				logger.debug("About to execute pick in " + device.getId() + " using " + robot.getId());
				processFlow.processProcessFlowEvent(new ActiveStepChangedEvent(processFlow, this, ActiveStepChangedEvent.PICK_PREPARE_DEVICE));
				logger.debug("Preparing device...");
				device.prepareForPick(pickSettings);
				logger.debug("Device prepared.");
				processFlow.processProcessFlowEvent(new ActiveStepChangedEvent(processFlow, this, ActiveStepChangedEvent.PICK_EXECUTE_NORMAL));
				if (needsTeaching()) {
					logger.debug("The exact pick location should have been teached: " + teachedOffset);
					if (teachedOffset == null) {
						throw new IllegalStateException("Unknown teached position");
					} else {
						Coordinates position = new Coordinates(device.getPickLocation(pickSettings.getWorkArea()));
						logger.debug("Normal coordinates: " + position);
						position.offset(teachedOffset);
						logger.debug("Coordinates after adding teached offset: " + position);
						robotPickSettings.setLocation(position);
					}
				} else {
					Coordinates position = new Coordinates(device.getPickLocation(pickSettings.getWorkArea()));
					logger.debug("The location of this pick was calculated (no teaching): " + position);
					robotPickSettings.setLocation(position);
				}
				logger.debug("Robot initiating pick action");
				robot.initiatePick(robotPickSettings);
				logger.debug("Robot action succeeded, about to ask device to release piece");
				device.releasePiece(pickSettings);
				logger.debug("Device released piece, about to finalize pick");
				robot.finalizePick(robotPickSettings);
				robotPickSettings.getGripper().setWorkPiece(robotPickSettings.getWorkPiece());
				robot.moveToHome();
				device.pickFinished(pickSettings);
				logger.debug("Pick finished");
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
				logger.debug("About to execute pick using teaching in " + device.getId() + " using " + robot.getId());
				processFlow.processProcessFlowEvent(new ActiveStepChangedEvent(processFlow, this, ActiveStepChangedEvent.PICK_PREPARE_DEVICE));
				logger.debug("Preparing device...");
				device.prepareForPick(pickSettings);
				logger.debug("Device prepared...");
				Coordinates coordinates = new Coordinates(device.getPickLocation(pickSettings.getWorkArea()));
				logger.info("Coordinates before teaching: " + coordinates);
				robotPickSettings.setLocation(coordinates);
				processFlow.processProcessFlowEvent(new ActiveStepChangedEvent(processFlow, this, ActiveStepChangedEvent.PICK_EXECUTE_TEACHED));
				logger.debug("Robot initiating pick action");
				robot.initiateTeachedPick(robotPickSettings);
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
				this.teachedOffset = coordinates.calculateOffset(device.getPickLocation(pickSettings.getWorkArea()));
				logger.debug("The teached offset is: " + teachedOffset);
				robotPickSettings.setLocation(device.getPickLocation(pickSettings.getWorkArea()));
				logger.debug("About to ask device to release piece");
				device.releasePiece(pickSettings);
				logger.debug("Device released piece, about to finalize pick");
				robot.finalizeTeachedPick(robotPickSettings);
				device.pickFinished(pickSettings);
				processFlow.processProcessFlowEvent(new ActiveStepChangedEvent(processFlow, this, ActiveStepChangedEvent.PICK_FINISHED));
				logger.debug("Pick finished");
			}
		}
	}
	
	@Override
	//TODO review!!
	public void finalize() throws CommunicationException, DeviceActionException {
		if (!device.lock(processFlow)) {
			throw new IllegalStateException("Device " + device + " was already locked by: " + device.getLockingProcess());
		} else {
			if (!robot.lock(processFlow)) {
				throw new IllegalStateException("Robot " + robot + " was already locked by: " + robot.getLockingProcess());
			} else {
				device.pickFinished(pickSettings);
				device.release(processFlow);
				robot.release(processFlow);
				processFlow.processProcessFlowEvent(new ActiveStepChangedEvent(processFlow, this, ActiveStepChangedEvent.PICK_FINISHED));
			}
		}
	}

	@Override
	public String toString() {
		return "PickStep from " + device + " with: " + robot;
	}

	@Override
	public Set<AbstractServiceProvider> getServiceProviders() {
		Set<AbstractServiceProvider> providers = new HashSet<AbstractServiceProvider>();
		providers.add(device);
		providers.add(robot);
		return providers;
	}

	@Override
	public AbstractDevicePickSettings getDeviceSettings() {
		return pickSettings;
	}

	@Override
	public ProcessStepType getType() {
		return ProcessStepType.PICK_STEP;
	}

	@Override
	public AbstractRobotPickSettings getRobotSettings() {
		return robotPickSettings;
	}

	public void setRobotSettings(AbstractRobotPickSettings settings) {
		this.robotPickSettings = settings;
		if (robotPickSettings != null) {
			robotPickSettings.setPickStep(this);
		}
	}

	@Override
	public boolean needsTeaching() {
		// pick location is always fixed!
		/*if ((robotPickSettings.getWorkPiece().getDimensions().isKnownShape()) && (robotPickSettings.getGripper().isFixedHeight())) {
			return false;
		} else {
			return true;
		}*/
		return true;
	}

	public Coordinates getTeachedOffset() {
		return teachedOffset;
	}
}
