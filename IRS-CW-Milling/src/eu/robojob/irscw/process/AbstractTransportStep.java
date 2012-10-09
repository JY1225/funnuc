package eu.robojob.irscw.process;

import java.io.IOException;

import eu.robojob.irscw.external.device.AbstractDevice;
import eu.robojob.irscw.external.robot.AbstractRobot;
import eu.robojob.irscw.positioning.Coordinates;

public abstract class AbstractTransportStep extends AbstractProcessStep {

	protected AbstractRobot robot;
	protected Coordinates teachedOffset;
	
	public AbstractTransportStep(ProcessFlow processFlow, AbstractDevice device, AbstractRobot robot) {
		super(processFlow, device);
		this.robot = robot;
		teachedOffset = null;
	}
	
	public AbstractTransportStep(AbstractDevice device, AbstractRobot robot) {
		this(null, device, robot);
	}
	
	public abstract void finalize() throws IOException;
	
	public abstract AbstractDevice.AbstractDeviceActionSettings<?> getDeviceSettings();
	public abstract AbstractRobot.AbstractRobotActionSettings getRobotSettings();

	public AbstractRobot getRobot() {
		return robot;
	}

	public abstract boolean needsTeaching();
	public abstract void prepareForTeaching() throws IOException;
	public abstract void teachingFinished() throws IOException;
}
