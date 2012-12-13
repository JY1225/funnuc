package eu.robojob.irscw.process;

import eu.robojob.irscw.external.communication.AbstractCommunicationException;
import eu.robojob.irscw.external.device.AbstractDevice;
import eu.robojob.irscw.external.device.AbstractDeviceActionSettings;
import eu.robojob.irscw.external.device.DeviceActionException;
import eu.robojob.irscw.external.robot.AbstractRobot;
import eu.robojob.irscw.external.robot.AbstractRobotActionSettings;
import eu.robojob.irscw.external.robot.RobotActionException;
import eu.robojob.irscw.positioning.Coordinates;

public abstract class AbstractTransportStep extends AbstractProcessStep {

	private AbstractRobot robot;
	private Coordinates relativeTeachedOffset;
	
	public AbstractTransportStep(final ProcessFlow processFlow, final AbstractDevice device, final AbstractRobot robot) {
		super(processFlow, device);
		this.robot = robot;
		relativeTeachedOffset = null;
	}
	
	public AbstractTransportStep(final AbstractDevice device, final AbstractRobot robot) {
		this(null, device, robot);
	}
	
	public abstract void finalize() throws AbstractCommunicationException, RobotActionException, DeviceActionException;
	
	public abstract AbstractDeviceActionSettings<?> getDeviceSettings();
	public abstract AbstractRobotActionSettings<?> getRobotSettings();
	public abstract boolean needsTeaching();
	public abstract void prepareForTeaching() throws AbstractCommunicationException, RobotActionException, DeviceActionException, InterruptedException;
	public abstract void teachingFinished() throws AbstractCommunicationException, RobotActionException, DeviceActionException, InterruptedException;

	public AbstractRobot getRobot() {
		return robot;
	}
	
	public void setRelativeTeachedOffset(final Coordinates teachedOffset) {
		this.relativeTeachedOffset = teachedOffset;
	}
	
	public Coordinates getRelativeTeachedOffset() {
		return relativeTeachedOffset;
	}
	
}
