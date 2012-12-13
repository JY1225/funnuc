package eu.robojob.irscw.process;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.robojob.irscw.external.communication.AbstractCommunicationException;
import eu.robojob.irscw.external.device.AbstractDevice;
import eu.robojob.irscw.external.device.DeviceActionException;
import eu.robojob.irscw.external.device.DevicePickSettings;
import eu.robojob.irscw.external.robot.AbstractRobot;
import eu.robojob.irscw.external.robot.RobotActionException;
import eu.robojob.irscw.external.robot.RobotPickSettings;
import eu.robojob.irscw.process.event.ActiveStepChangedEvent;

public class PickAfterWaitStep extends PickStep {

	private static Logger logger = LogManager.getLogger(PickAfterWaitStep.class.getName());
			
	public PickAfterWaitStep(final AbstractRobot robot, final AbstractDevice deviceFrom, final DevicePickSettings pickSettings, final RobotPickSettings robotPickSettings) {
		super(robot, deviceFrom, pickSettings, robotPickSettings);
	}
	
	public PickAfterWaitStep(final ProcessFlow processFlow, final AbstractRobot robot, final AbstractDevice deviceFrom, final DevicePickSettings pickSettings, final RobotPickSettings robotPickSettings) {
		super(processFlow, robot, deviceFrom, pickSettings, robotPickSettings);
	}

	@Override
	public void executeStep() throws AbstractCommunicationException, RobotActionException, DeviceActionException, InterruptedException {
		// check if the parent process has locked the devices to be used
		if (!getDevice().lock(getProcessFlow())) {
			throw new IllegalStateException("Device " + getDevice() + " was already locked by: " + getDevice().getLockingProcess());
		} else {
			if (!getRobot().lock(getProcessFlow())) {
				throw new IllegalStateException("Robot " + getRobot() + " was already locked by: " + getRobot().getLockingProcess());
			} else {
				logger.debug("About to execute pick after wait in " + getDevice().getId() + " using " + getRobot().getId());
				getProcessFlow().processProcessFlowEvent(new ActiveStepChangedEvent(getProcessFlow(), this, ActiveStepChangedEvent.PICK_PREPARE_DEVICE));
				logger.debug("Preparing device...");
				getDevice().prepareForPick(getDeviceSettings());
				logger.debug("Device prepared.");
				getProcessFlow().processProcessFlowEvent(new ActiveStepChangedEvent(getProcessFlow(), this, ActiveStepChangedEvent.PICK_EXECUTE_NORMAL));
				if (needsTeaching()) {
					throw new IllegalStateException("No teaching needed with this kind of step.");
				}
				logger.debug("About to ask device to release piece");
				getDevice().releasePiece(getDeviceSettings());
				logger.debug("Device released piece, about to move away");
				getRobot().moveAway();
				getRobotSettings().getGripperHead().getGripper().setWorkPiece(getRobotSettings().getWorkPiece());
				getDevice().pickFinished(getDeviceSettings());
				logger.debug("Pick finished");
			}
		}
	}
	
	@Override
	public void prepareForTeaching() throws AbstractCommunicationException, RobotActionException, DeviceActionException, InterruptedException {
		throw new IllegalStateException("No teaching needed with this kind of step.");

	}

	@Override
	public void teachingFinished() throws AbstractCommunicationException, RobotActionException, DeviceActionException, InterruptedException {
		throw new IllegalStateException("No teaching needed with this kind of step.");
	}
	
	@Override
	public boolean needsTeaching() {
		return false;
	}

}
