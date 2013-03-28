package eu.robojob.irscw.process;

import eu.robojob.irscw.external.communication.AbstractCommunicationException;
import eu.robojob.irscw.external.device.DeviceActionException;
import eu.robojob.irscw.external.robot.RobotActionException;
import eu.robojob.irscw.positioning.Coordinates;
import eu.robojob.irscw.process.execution.ProcessExecutor;

public abstract class AbstractTransportStep extends AbstractProcessStep implements RobotStep, DeviceStep {

	//TODO review if these are necessary (also contained in settings)
	private Coordinates relativeTeachedOffset;
	
	public AbstractTransportStep(final ProcessFlow processFlow) {
		super(processFlow);
		relativeTeachedOffset = null;
	}
	
	public AbstractTransportStep() {
		this(null);
	}
		
	public abstract boolean needsTeaching();
	public abstract void executeStepTeached(int workpieceId, ProcessExecutor executor) throws AbstractCommunicationException, DeviceActionException, RobotActionException, InterruptedException;

	public abstract void finalizeStep(ProcessExecutor executor) throws AbstractCommunicationException, RobotActionException, InterruptedException;
	
	public void setRelativeTeachedOffset(final Coordinates teachedOffset) {
		this.relativeTeachedOffset = teachedOffset;
	}
	
	public Coordinates getRelativeTeachedOffset() {
		return relativeTeachedOffset;
	}
	
}
