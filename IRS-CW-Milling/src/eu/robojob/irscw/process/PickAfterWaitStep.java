package eu.robojob.irscw.process;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.robojob.irscw.external.communication.AbstractCommunicationException;
import eu.robojob.irscw.external.device.DeviceActionException;
import eu.robojob.irscw.external.device.DevicePickSettings;
import eu.robojob.irscw.external.robot.RobotActionException;
import eu.robojob.irscw.external.robot.RobotPickSettings;
import eu.robojob.irscw.process.event.StatusChangedEvent;

public class PickAfterWaitStep extends PickStep {

	private static Logger logger = LogManager.getLogger(PickAfterWaitStep.class.getName());
			
	public PickAfterWaitStep(final DevicePickSettings pickSettings, final RobotPickSettings robotPickSettings) {
		super(pickSettings, robotPickSettings);
	}
	
	public PickAfterWaitStep(final ProcessFlow processFlow, final DevicePickSettings pickSettings, final RobotPickSettings robotPickSettings) {
		super(processFlow, pickSettings, robotPickSettings);
	}

	@Override
	public void executeStep(final int workPieceId) throws AbstractCommunicationException, RobotActionException, DeviceActionException, InterruptedException {
		// check if the parent process has locked the devices to be used
		if (!getDevice().lock(getProcessFlow())) {
			throw new IllegalStateException("Device [" + getDevice() + "] was already locked by [" + getDevice().getLockingProcess() + "].");
		} else {
			if (!getRobot().lock(getProcessFlow())) {
				throw new IllegalStateException("Robot [" + getRobot() + "] was already locked by [" + getRobot().getLockingProcess() + "].");
			} else {
				getProcessFlow().processProcessFlowEvent(new StatusChangedEvent(getProcessFlow(), this, StatusChangedEvent.STARTED, workPieceId));
				getProcessFlow().processProcessFlowEvent(new StatusChangedEvent(getProcessFlow(), this, StatusChangedEvent.PREPARE_DEVICE, workPieceId));
				logger.debug("Preparing device [" + getDevice() + "] for pick-after-wait, using [" + getRobot() + "].");
				getDevice().prepareForPick(getDeviceSettings());
				logger.debug("Device [" + getDevice() + "] prepared for pick-after-wait.");
				getProcessFlow().processProcessFlowEvent(new StatusChangedEvent(getProcessFlow(), this, StatusChangedEvent.EXECUTE_NORMAL, workPieceId));
				if (needsTeaching()) {
					throw new IllegalStateException("No teaching needed with this kind of step.");
				}
				logger.debug("About to ask device [" + getDevice() + "] to release piece.");
				getDevice().releasePiece(getDeviceSettings());
				logger.debug("Device [" + getDevice() + "] released piece, about to move away.");
				getRobot().continueMoveWithPieceTillIPPoint();
				getDevice().pickFinished(getDeviceSettings());
				getProcessFlow().processProcessFlowEvent(new StatusChangedEvent(getProcessFlow(), this, StatusChangedEvent.ENDED, workPieceId));
				logger.debug("Pick-after-wait ready (but not finalized).");
			}
		}
	}
	
	@Override
	public void executeStepTeached(final int workPieceId) throws AbstractCommunicationException, RobotActionException, DeviceActionException, InterruptedException {
		executeStep(workPieceId);
	}
	
	@Override
	public void finalizeStep() throws AbstractCommunicationException, RobotActionException, InterruptedException {
		if (!getRobot().lock(getProcessFlow())) {
			throw new IllegalStateException("Robot [" + getRobot() + "] was already locked by " + getRobot().getLockingProcess());
		} else {
			getRobot().finalizeMoveWithPiece();
			getRobot().release(getProcessFlow());
			logger.debug("Finalized pick-after-wait.");
		}
	}
	
	@Override
	public boolean needsTeaching() {
		return false;		// this step never needs teaching
	}

}
