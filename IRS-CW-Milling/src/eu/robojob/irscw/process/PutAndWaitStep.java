package eu.robojob.irscw.process;

import org.apache.log4j.Logger;

import eu.robojob.irscw.external.communication.CommunicationException;
import eu.robojob.irscw.external.device.AbstractDevice;
import eu.robojob.irscw.external.device.AbstractDevice.AbstractDevicePutSettings;
import eu.robojob.irscw.external.device.DeviceActionException;
import eu.robojob.irscw.external.robot.AbstractRobot;
import eu.robojob.irscw.external.robot.AbstractRobot.AbstractRobotPutSettings;
import eu.robojob.irscw.external.robot.RobotActionException;
import eu.robojob.irscw.positioning.Coordinates;
import eu.robojob.irscw.process.event.ActiveStepChangedEvent;

public class PutAndWaitStep extends PutStep {
	
	private static final Logger logger = Logger.getLogger(PutAndWaitStep.class);

	public PutAndWaitStep(ProcessFlow processFlow, AbstractRobot robot, AbstractDevice deviceTo, AbstractDevicePutSettings putSettings, AbstractRobotPutSettings robotPutSettings) {
		super(processFlow, robot, deviceTo, putSettings, robotPutSettings);
	}

	public PutAndWaitStep(AbstractRobot robot, AbstractDevice deviceTo, AbstractDevicePutSettings putSettings, AbstractRobotPutSettings robotPutSettings) {
		super(robot, deviceTo, putSettings, robotPutSettings);
	}

	@Override
	public void executeStep() throws CommunicationException, RobotActionException, DeviceActionException, InterruptedException {
		// check if the parent process has locked the devices to be used
		if (!device.lock(processFlow)) {
			throw new IllegalStateException("Device " + device + " was already locked by: " + device.getLockingProcess());
		} else {
			if (!robot.lock(processFlow)) {
				throw new IllegalStateException("Robot " + robot + " was already locked by: " + robot.getLockingProcess());
			} else {
				logger.debug("About to execute put and wait in " + device.getId() + " using " + robot.getId());
				processFlow.processProcessFlowEvent(new ActiveStepChangedEvent(processFlow, this, ActiveStepChangedEvent.PUT_PREPARE_DEVICE));
				logger.debug("Preparing device...");
				device.prepareForPut(putSettings);
				logger.debug("Device prepared.");
				processFlow.processProcessFlowEvent(new ActiveStepChangedEvent(processFlow, this, ActiveStepChangedEvent.PUT_EXECUTE_NORMAL));
				if (needsTeaching()) {
					logger.debug("The exact put and wait location should have been teached: " + teachedOffset);
					if (teachedOffset == null) {
						throw new IllegalStateException("Unknown teached position");
					} else {
						Coordinates position = new Coordinates(device.getPutLocation(putSettings.getWorkArea(), robotPutSettings.getGripperHead().getGripper().getWorkPiece().getDimensions()));
						logger.debug("Normal coordinates: " + position);
						position.offset(teachedOffset);
						logger.debug("Coordinates after adding teached offset: " + position);
						robotPutSettings.setLocation(position);
					}
				} else {
					Coordinates position = new Coordinates(device.getPutLocation(putSettings.getWorkArea(), robotPutSettings.getGripperHead().getGripper().getWorkPiece().getDimensions()));
					/*// no offset needed? sometimes there is! use offset of corresponding pick!
					// getting pick step:
					PickStep pickStep = (PickStep) processFlow.getStep(processFlow.getStepIndex(this) - 1);
					if (pickStep.needsTeaching()) {
						position.offset(pickStep.getTeachedOffset());
					}*/
					logger.debug("The location of this put and wait was calculated (no teaching): " + position);
					robotPutSettings.setLocation(position);
				}
				logger.debug("Robot initiating put and wait action");
				robot.moveToAndWait(robotPutSettings);
				logger.debug("Robot action succeeded, about to ask device to grab piece");
				device.grabPiece(putSettings);
				processFlow.processProcessFlowEvent(new ActiveStepChangedEvent(processFlow, this, ActiveStepChangedEvent.PUT_FINISHED));
				logger.debug("Put and wait finished");
			}
		}
	}
	
	@Override
	public void prepareForTeaching() throws CommunicationException, RobotActionException, DeviceActionException, InterruptedException {
		if (!device.lock(processFlow)) {
			throw new IllegalStateException("Device " + device + " was already locked by: " + device.getLockingProcess());
		} else {
			if (!robot.lock(processFlow)) {
				throw new IllegalStateException("Robot " + robot + " was already locked by: " + robot.getLockingProcess());
			} else {
				logger.debug("About to execute put and wait using teaching in " + device.getId() + " using " + robot.getId());
				processFlow.processProcessFlowEvent(new ActiveStepChangedEvent(processFlow, this, ActiveStepChangedEvent.PUT_PREPARE_DEVICE));
				logger.debug("Preparing device...");
				device.prepareForPut(putSettings);
				logger.debug("Device prepared...");
				Coordinates coordinates = new Coordinates(device.getPutLocation(putSettings.getWorkArea(), robotPutSettings.getGripperHead().getGripper().getWorkPiece().getDimensions()));
				logger.info("Coordinates before teaching: " + coordinates);
				robotPutSettings.setLocation(coordinates);
				processFlow.processProcessFlowEvent(new ActiveStepChangedEvent(processFlow, this, ActiveStepChangedEvent.PUT_EXECUTE_TEACHED));
				logger.debug("Robot initiating put and wait action");
				robot.teachedMoveToAndWait(robotPutSettings);
				logger.debug("Robot action succeeded");
			}
		}
	}

	@Override
	public void teachingFinished() throws CommunicationException, RobotActionException, DeviceActionException, InterruptedException {
		if (!device.lock(processFlow)) {
			throw new IllegalStateException("Device " + device + " was already locked by: " + device.getLockingProcess());
		} else {
			if (!robot.lock(processFlow)) {
				throw new IllegalStateException("Robot " + robot + " was already locked by: " + robot.getLockingProcess());
			} else {
				logger.debug("Teaching finished");
				Coordinates coordinates = new Coordinates(robot.getPosition());
				Coordinates oldCoordinates = new Coordinates(device.getPutLocation(putSettings.getWorkArea(), robotPutSettings.getGripperHead().getGripper().getWorkPiece().getDimensions()));
				this.teachedOffset = coordinates.calculateOffset(oldCoordinates);
				logger.debug("The teached offset is: " + teachedOffset);
				robotPutSettings.setLocation(device.getPutLocation(putSettings.getWorkArea(), robotPutSettings.getGripperHead().getGripper().getWorkPiece().getDimensions()));
				logger.debug("About to ask device to grab piece");
				device.grabPiece(putSettings);
				logger.debug("Device grabbed piece, about to finalize put");
				processFlow.processProcessFlowEvent(new ActiveStepChangedEvent(processFlow, this, ActiveStepChangedEvent.PUT_FINISHED));
				device.putFinished(putSettings);
				logger.debug("Put and wait finished");
			}
		}
	}
}
