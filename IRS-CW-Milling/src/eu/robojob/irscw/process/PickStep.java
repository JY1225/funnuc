package eu.robojob.irscw.process;

import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.robojob.irscw.external.AbstractServiceProvider;
import eu.robojob.irscw.external.communication.AbstractCommunicationException;
import eu.robojob.irscw.external.device.AbstractDevice;
import eu.robojob.irscw.external.device.DeviceActionException;
import eu.robojob.irscw.external.device.DevicePickSettings;
import eu.robojob.irscw.external.robot.AbstractRobot;
import eu.robojob.irscw.external.robot.AbstractRobot.AbstractRobotPickSettings;
import eu.robojob.irscw.external.robot.RobotActionException;
import eu.robojob.irscw.positioning.Coordinates;
import eu.robojob.irscw.process.event.ActiveStepChangedEvent;

public class PickStep extends AbstractTransportStep {

	private static final Logger logger = LogManager.getLogger(PickStep.class.getName());
	
	protected DevicePickSettings pickSettings;
	protected AbstractRobot.AbstractRobotPickSettings robotPickSettings;
			
	public PickStep(ProcessFlow processFlow, AbstractRobot robot, AbstractDevice deviceFrom, DevicePickSettings pickSettings,
			AbstractRobot.AbstractRobotPickSettings robotPickSettings) {
		super(processFlow, deviceFrom, robot);
		this.pickSettings = pickSettings;
		if (pickSettings != null) {
			pickSettings.setStep(this);
		}
		setRobotSettings(robotPickSettings);
	}
	
	public PickStep(AbstractRobot robot, AbstractDevice deviceFrom, DevicePickSettings pickSettings,
			AbstractRobot.AbstractRobotPickSettings robotPickSettings) {
		this(null, robot, deviceFrom, pickSettings, robotPickSettings);
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
				logger.debug("About to execute pick in " + device.getId() + " using " + robot.getId());
				processFlow.processProcessFlowEvent(new ActiveStepChangedEvent(processFlow, this, ActiveStepChangedEvent.PICK_PREPARE_DEVICE));
				logger.debug("Preparing device...");
				device.prepareForPick(pickSettings);
				logger.debug("Device prepared.");
				processFlow.processProcessFlowEvent(new ActiveStepChangedEvent(processFlow, this, ActiveStepChangedEvent.PICK_EXECUTE_NORMAL));
				if (needsTeaching()) {
					logger.debug("The exact pick location should have been teached: " + relativeTeachedOffset);
					if (relativeTeachedOffset == null) {
						throw new IllegalStateException("Unknown teached position");
					} else {
						Coordinates position = new Coordinates(device.getPickLocation(pickSettings.getWorkArea(), processFlow.getClampingType()));
						logger.debug("Normal coordinates: " + position);
						Coordinates absoluteOffset = calculator.calculateAbsoluteOffset(position, relativeTeachedOffset);
						position.offset(absoluteOffset);
						logger.debug("Coordinates after adding teached offset: " + position);
						robotPickSettings.setLocation(position);
					}
				} else {
					Coordinates position = new Coordinates(device.getPickLocation(pickSettings.getWorkArea(), processFlow.getClampingType()));
					logger.debug("The location of this pick was calculated (no teaching): " + position);
					robotPickSettings.setLocation(position);
				}
				logger.debug("Robot initiating pick action");
				robot.initiatePick(robotPickSettings);
				logger.debug("Robot action succeeded, about to ask device to release piece");
				device.releasePiece(pickSettings);
				logger.debug("Device released piece, about to finalize pick");
				robot.finalizePick(robotPickSettings);
				robotPickSettings.getGripperHead().getGripper().setWorkPiece(robotPickSettings.getWorkPiece());
				//robot.moveToHome();
				device.pickFinished(pickSettings);
				logger.debug("Pick finished");
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
				logger.debug("About to execute pick using teaching in " + device.getId() + " using " + robot.getId());
				processFlow.processProcessFlowEvent(new ActiveStepChangedEvent(processFlow, this, ActiveStepChangedEvent.PICK_PREPARE_DEVICE));
				logger.debug("Preparing device...");
				device.prepareForPick(pickSettings);
				logger.debug("Device prepared...");
				Coordinates coordinates = new Coordinates(device.getPickLocation(pickSettings.getWorkArea(), processFlow.getClampingType()));
				logger.info("Coordinates before teaching: " + coordinates);
				if (relativeTeachedOffset != null) {
					Coordinates c = calculator.calculateAbsoluteOffset(coordinates, relativeTeachedOffset);
					coordinates.offset(c);
					logger.info("Coordinates before teaching (added teached offset): " + coordinates);
				}
				robotPickSettings.setLocation(coordinates);
				processFlow.processProcessFlowEvent(new ActiveStepChangedEvent(processFlow, this, ActiveStepChangedEvent.PICK_EXECUTE_TEACHED));
				logger.debug("Robot initiating pick action");
				robot.initiateTeachedPick(robotPickSettings);
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
				Coordinates oldCoordinates = new Coordinates(device.getPickLocation(pickSettings.getWorkArea(), processFlow.getClampingType()));
				this.relativeTeachedOffset = coordinates.calculateOffset(oldCoordinates);
				this.relativeTeachedOffset = calculator.calculateRelativeTeachedOffset(oldCoordinates, relativeTeachedOffset);
				logger.debug("The teached offset is: " + relativeTeachedOffset);
				robotPickSettings.setLocation(device.getPickLocation(pickSettings.getWorkArea(), processFlow.getClampingType()));
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
	public void finalize() throws AbstractCommunicationException, DeviceActionException {
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
	public DevicePickSettings getDeviceSettings() {
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

	public Coordinates getRelativeTeachedOffset() {
		return relativeTeachedOffset;
	}
}
