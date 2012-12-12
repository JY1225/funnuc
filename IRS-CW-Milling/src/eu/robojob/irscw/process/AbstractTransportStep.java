package eu.robojob.irscw.process;

import eu.robojob.irscw.external.communication.AbstractCommunicationException;
import eu.robojob.irscw.external.device.AbstractDevice;
import eu.robojob.irscw.external.device.AbstractDeviceActionSettings;
import eu.robojob.irscw.external.device.DeviceActionException;
import eu.robojob.irscw.external.robot.AbstractRobot;
import eu.robojob.irscw.external.robot.AbstractRobotActionSettings;
import eu.robojob.irscw.external.robot.RobotActionException;
import eu.robojob.irscw.positioning.Coordinates;
import eu.robojob.irscw.positioning.TeachedCoordinatesCalculator;

public abstract class AbstractTransportStep extends AbstractProcessStep {

	protected AbstractRobot robot;
	protected Coordinates relativeTeachedOffset;
	protected TeachedCoordinatesCalculator calculator;
	
	public AbstractTransportStep(ProcessFlow processFlow, AbstractDevice device, AbstractRobot robot) {
		super(processFlow, device);
		this.robot = robot;
		relativeTeachedOffset = null;
		this.calculator = new TeachedCoordinatesCalculator();
	}
	
	public AbstractTransportStep(AbstractDevice device, AbstractRobot robot) {
		this(null, device, robot);
	}
	
	public abstract void finalize() throws AbstractCommunicationException, RobotActionException, DeviceActionException;
	
	public abstract AbstractDeviceActionSettings<?> getDeviceSettings();
	public abstract AbstractRobotActionSettings getRobotSettings();

	public AbstractRobot getRobot() {
		return robot;
	}
	
	public void setRelativeTeachedOffset(Coordinates teachedOffset) {
		this.relativeTeachedOffset = teachedOffset;
	}
	
	public Coordinates getRelativeTeachedOffset() {
		return relativeTeachedOffset;
	}

	public abstract boolean needsTeaching();
	public abstract void prepareForTeaching() throws AbstractCommunicationException, RobotActionException, DeviceActionException, InterruptedException;
	public abstract void teachingFinished() throws AbstractCommunicationException, RobotActionException, DeviceActionException, InterruptedException;
}
