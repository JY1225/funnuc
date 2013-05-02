package eu.robojob.millassist.process;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.robojob.millassist.external.communication.AbstractCommunicationException;
import eu.robojob.millassist.external.device.DeviceActionException;
import eu.robojob.millassist.external.device.DevicePickSettings;
import eu.robojob.millassist.external.robot.RobotActionException;
import eu.robojob.millassist.external.robot.RobotPickSettings;
import eu.robojob.millassist.process.event.StatusChangedEvent;
import eu.robojob.millassist.process.execution.ProcessExecutor;

public class PickAfterWaitStep extends PickStep {

	private static Logger logger = LogManager.getLogger(PickAfterWaitStep.class.getName());
			
	public PickAfterWaitStep(final DevicePickSettings pickSettings, final RobotPickSettings robotPickSettings) {
		super(pickSettings, robotPickSettings);
	}
	
	public PickAfterWaitStep(final ProcessFlow processFlow, final DevicePickSettings pickSettings, final RobotPickSettings robotPickSettings) {
		super(processFlow, pickSettings, robotPickSettings);
	}

	@Override
	public void executeStep(final int workPieceId, final ProcessExecutor executor) throws AbstractCommunicationException, RobotActionException, DeviceActionException, InterruptedException {
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
					getProcessFlow().processProcessFlowEvent(new StatusChangedEvent(getProcessFlow(), this, StatusChangedEvent.STARTED, workPieceId));
					getProcessFlow().processProcessFlowEvent(new StatusChangedEvent(getProcessFlow(), this, StatusChangedEvent.PREPARE_DEVICE, workPieceId));
					checkProcessExecutorStatus(executor);
					logger.debug("Preparing device [" + getDevice() + "] for pick-after-wait, using [" + getRobot() + "].");
					checkProcessExecutorStatus(executor);
					getDevice().prepareForPick(getDeviceSettings());
					logger.debug("Device [" + getDevice() + "] prepared for pick-after-wait.");
					checkProcessExecutorStatus(executor);
					getProcessFlow().processProcessFlowEvent(new StatusChangedEvent(getProcessFlow(), this, StatusChangedEvent.EXECUTE_NORMAL, workPieceId));
					if (needsTeaching()) {
						throw new IllegalStateException("No teaching needed with this kind of step.");
					}
					logger.debug("About to ask device [" + getDevice() + "] to release piece.");
					checkProcessExecutorStatus(executor);
					getDevice().releasePiece(getDeviceSettings());
					logger.debug("Device [" + getDevice() + "] released piece, about to move away.");
					checkProcessExecutorStatus(executor);
					getRobot().continueMoveWithPieceTillIPPoint();
					checkProcessExecutorStatus(executor);
					getDevice().pickFinished(getDeviceSettings());
					checkProcessExecutorStatus(executor);
					getProcessFlow().processProcessFlowEvent(new StatusChangedEvent(getProcessFlow(), this, StatusChangedEvent.ENDED, workPieceId));
					logger.debug("Pick-after-wait ready (but not finalized).");
				} catch (AbstractCommunicationException | RobotActionException | DeviceActionException | InterruptedException e) {
					throw e;
				} finally {
					getDevice().release();
					getRobot().release();
				}
			}
		}
	}
	
	@Override
	public void executeStepTeached(final int workPieceId, final ProcessExecutor executor) throws AbstractCommunicationException, RobotActionException, DeviceActionException, InterruptedException {
		executeStep(workPieceId, executor);
	}
	
	@Override
	public void finalizeStep(final ProcessExecutor executor) throws AbstractCommunicationException, RobotActionException, InterruptedException {
		if (!getRobot().lock(getProcessFlow())) {
			throw new IllegalStateException("Robot [" + getRobot() + "] was already locked by " + getRobot().getLockingProcess());
		} else {
			try {
				checkProcessExecutorStatus(executor);
				getRobot().finalizeMovePiece();
				logger.debug("Finalized pick-after-wait.");
			} catch(AbstractCommunicationException | RobotActionException | InterruptedException e) {
				throw e;
			} finally {
				getRobot().release();
			}
		}
	}
	
	@Override
	public boolean needsTeaching() {
		return false;		// this step never needs teaching
	}

}
