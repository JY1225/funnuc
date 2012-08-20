package eu.robojob.irscw.process;

import java.util.HashSet;
import java.util.Set;

import eu.robojob.irscw.external.AbstractServiceProvider;
import eu.robojob.irscw.external.device.AbstractDevice;
import eu.robojob.irscw.external.robot.AbstractRobot;
import eu.robojob.irscw.external.robot.Gripper;

public class PutStep extends AbstractTransportStep {

	private AbstractRobot robot;
	private Gripper gripper;
	private AbstractDevice.AbstractDevicePutSettings putSettings;
	private AbstractRobot.AbstractRobotPutSettings robotPutSettings;
	
	public PutStep(Process parentProcess, AbstractRobot robot, Gripper gripper, AbstractDevice deviceTo,
			AbstractDevice.AbstractDevicePutSettings putSettings, AbstractRobot.AbstractRobotPutSettings robotPutSettings) {
		super(parentProcess, deviceTo);
		this.robot = robot;
		this.gripper = gripper;
		this.robotPutSettings = robotPutSettings;
	}

	@Override
	public void executeStep() {
		device.prepareForPut(putSettings);
		robot.put(robotPutSettings);
		device.grabPiece(putSettings);
		robot.releasePiece(robotPutSettings);
	}

	@Override
	public AbstractProcessStep clone(Process parentProcess) {
		return new PutStep(parentProcess, robot, gripper, device, putSettings, robotPutSettings);
	}

	@Override
	public String toString() {
		return "PutStep to " + device + " using " + robot;
	}

	@Override
	public void finalize() {
		device.putFinished(putSettings);
	}

	public AbstractRobot getRobot() {
		return robot;
	}

	public Gripper getGripper() {
		return gripper;
	}
	
	@Override
	public Set<AbstractServiceProvider> getServiceProviders() {
		Set<AbstractServiceProvider> providers = new HashSet<AbstractServiceProvider>();
		providers.add(device);
		providers.add(robot);
		return providers;
	}

}
