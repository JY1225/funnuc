package eu.robojob.irscw.process;

import eu.robojob.irscw.external.communication.AbstractCommunicationException;
import eu.robojob.irscw.external.device.AbstractDevice;
import eu.robojob.irscw.external.device.DeviceActionException;
import eu.robojob.irscw.external.robot.AbstractRobot;
import eu.robojob.irscw.external.robot.RobotActionException;
import eu.robojob.irscw.positioning.Coordinates;

public abstract class AbstractTransportStep extends AbstractProcessStep implements RobotStep, DeviceStep {

	private AbstractDevice device;
	private AbstractRobot robot;
	private Coordinates relativeTeachedOffset;
	
	public AbstractTransportStep(final ProcessFlow processFlow, final AbstractDevice device, final AbstractRobot robot) {
		super(processFlow);
		this.robot = robot;
		this.device = device;
		relativeTeachedOffset = null;
	}
	
	public AbstractTransportStep(final AbstractDevice device, final AbstractRobot robot) {
		this(null, device, robot);
	}
		
	public abstract boolean needsTeaching();
	public abstract void executeStepTeached(int workpieceId) throws AbstractCommunicationException, DeviceActionException, RobotActionException, InterruptedException;

	public abstract void finalizeStep() throws AbstractCommunicationException, RobotActionException, InterruptedException;
	
	public AbstractRobot getRobot() {
		return robot;
	}
	
	public AbstractDevice getDevice() {
		return device;
	}
	
	public void setRelativeTeachedOffset(final Coordinates teachedOffset) {
		this.relativeTeachedOffset = teachedOffset;
	}
	
	public Coordinates getRelativeTeachedOffset() {
		return relativeTeachedOffset;
	}
	
}
