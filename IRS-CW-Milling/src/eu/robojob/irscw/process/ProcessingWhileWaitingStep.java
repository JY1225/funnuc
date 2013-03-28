package eu.robojob.irscw.process;

import eu.robojob.irscw.external.communication.AbstractCommunicationException;
import eu.robojob.irscw.external.device.DeviceActionException;
import eu.robojob.irscw.external.device.processing.ProcessingDeviceStartCyclusSettings;
import eu.robojob.irscw.external.robot.AbstractRobot;
import eu.robojob.irscw.external.robot.AbstractRobotActionSettings;
import eu.robojob.irscw.external.robot.RobotProcessingWhileWaitingSettings;
import eu.robojob.irscw.process.execution.ProcessExecutor;

public class ProcessingWhileWaitingStep extends ProcessingStep implements RobotStep {

	private RobotProcessingWhileWaitingSettings robotSettings;
	
	public ProcessingWhileWaitingStep(final ProcessFlow processFlow, final ProcessingDeviceStartCyclusSettings startCyclusSettings, 
			final RobotProcessingWhileWaitingSettings robotSettings) {
		super(processFlow, startCyclusSettings);
		this.robotSettings = robotSettings;
	}
	
	public ProcessingWhileWaitingStep(final ProcessingDeviceStartCyclusSettings startCyclusSettings, final RobotProcessingWhileWaitingSettings robotSettings) {
		this(null, startCyclusSettings, robotSettings);
	}
	
	@Override
	public AbstractRobotActionSettings<?> getRobotSettings() {
		return robotSettings;
	}
	
	@Override
	public AbstractRobot getRobot() {
		return robotSettings.getRobot();
	}

	@Override
	public void executeStep(final int workPieceId, final ProcessExecutor executor) throws AbstractCommunicationException, DeviceActionException, InterruptedException {
		if (!getRobot().lock(getProcessFlow())) {
			throw new IllegalStateException("Robot [" + getRobot() + "] was already locked by [" + getRobot().getLockingProcess() + "].");
		} else {
			try {
				super.executeStep(workPieceId, executor);
			} catch (AbstractCommunicationException | DeviceActionException | InterruptedException e) {
				throw e;
			} finally {
				getRobot().release();
			}
		}
	}
}
