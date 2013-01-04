package eu.robojob.irscw.process;

import eu.robojob.irscw.external.communication.AbstractCommunicationException;
import eu.robojob.irscw.external.device.DeviceActionException;
import eu.robojob.irscw.external.device.processing.AbstractProcessingDevice;
import eu.robojob.irscw.external.device.processing.ProcessingDeviceStartCyclusSettings;
import eu.robojob.irscw.external.robot.AbstractRobot;
import eu.robojob.irscw.external.robot.AbstractRobotActionSettings;
import eu.robojob.irscw.external.robot.RobotProcessingWhileWaitingSettings;

public class ProcessingWhileWaitingStep extends ProcessingStep implements RobotStep {

	private RobotProcessingWhileWaitingSettings robotSettings;
	private AbstractRobot robot;
	
	public ProcessingWhileWaitingStep(final ProcessFlow processFlow, final AbstractProcessingDevice processingDevice, 
			final ProcessingDeviceStartCyclusSettings startCyclusSettings, final AbstractRobot robot, final RobotProcessingWhileWaitingSettings robotSettings) {
		super(processFlow, processingDevice, startCyclusSettings);
		this.robotSettings = robotSettings;
		this.robot = robot;
	}
	
	public ProcessingWhileWaitingStep(final AbstractProcessingDevice processingDevice, final ProcessingDeviceStartCyclusSettings startCyclusSettings, 
			final AbstractRobot robot, final RobotProcessingWhileWaitingSettings robotSettings) {
		this(null, processingDevice, startCyclusSettings, robot, robotSettings);
	}
	
	@Override
	public AbstractRobotActionSettings<?> getRobotSettings() {
		return robotSettings;
	}
	
	@Override
	public AbstractRobot getRobot() {
		return robot;
	}

	@Override
	public void executeStep(final int workPieceId) throws AbstractCommunicationException, DeviceActionException, InterruptedException {
		if (!getRobot().lock(getProcessFlow())) {
			throw new IllegalStateException("Robot [" + getRobot() + "] was already locked by [" + getRobot().getLockingProcess() + "].");
		} else {
			super.executeStep(workPieceId);
		}
	}
}
