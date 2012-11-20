package eu.robojob.irscw.process;

import org.apache.log4j.Logger;

import eu.robojob.irscw.external.communication.CommunicationException;
import eu.robojob.irscw.external.device.AbstractDevice;
import eu.robojob.irscw.external.device.AbstractDevice.AbstractDevicePickSettings;
import eu.robojob.irscw.external.device.DeviceActionException;
import eu.robojob.irscw.external.robot.AbstractRobot;
import eu.robojob.irscw.external.robot.AbstractRobot.AbstractRobotPickSettings;
import eu.robojob.irscw.external.robot.RobotActionException;
import eu.robojob.irscw.process.event.ActiveStepChangedEvent;

public class PickAfterWaitStep extends PickStep {

	private static final Logger logger = Logger.getLogger(PickAfterWaitStep.class);
			
	public PickAfterWaitStep(AbstractRobot robot, AbstractDevice deviceFrom, AbstractDevicePickSettings pickSettings, AbstractRobotPickSettings robotPickSettings) {
		super(robot, deviceFrom, pickSettings, robotPickSettings);
	}
	
	public PickAfterWaitStep(ProcessFlow processFlow, AbstractRobot robot, AbstractDevice deviceFrom, AbstractDevicePickSettings pickSettings, AbstractRobotPickSettings robotPickSettings) {
		super(processFlow, robot, deviceFrom, pickSettings, robotPickSettings);
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
				logger.debug("About to execute pick after wait in " + device.getId() + " using " + robot.getId());
				processFlow.processProcessFlowEvent(new ActiveStepChangedEvent(processFlow, this, ActiveStepChangedEvent.PICK_PREPARE_DEVICE));
				logger.debug("Preparing device...");
				device.prepareForPick(pickSettings);
				logger.debug("Device prepared.");
				processFlow.processProcessFlowEvent(new ActiveStepChangedEvent(processFlow, this, ActiveStepChangedEvent.PICK_EXECUTE_NORMAL));
				if (needsTeaching()) {
					throw new IllegalStateException("No teaching needed with this kind of step.");
				}
				logger.debug("About to ask device to release piece");
				device.releasePiece(pickSettings);
				logger.debug("Device released piece, about to move away");
				robot.moveAway();
				robotPickSettings.getGripperHead().getGripper().setWorkPiece(robotPickSettings.getWorkPiece());
				device.pickFinished(pickSettings);
				logger.debug("Pick finished");
			}
		}
	}
	
	@Override
	public void prepareForTeaching() throws CommunicationException, RobotActionException, DeviceActionException, InterruptedException {
		throw new IllegalStateException("No teaching needed with this kind of step.");

	}

	@Override
	public void teachingFinished() throws CommunicationException, RobotActionException, DeviceActionException, InterruptedException {
		throw new IllegalStateException("No teaching needed with this kind of step.");
	}
	
	@Override
	public boolean needsTeaching() {
		return false;
	}

}
