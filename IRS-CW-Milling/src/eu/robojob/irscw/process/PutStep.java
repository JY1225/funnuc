package eu.robojob.irscw.process;

import eu.robojob.irscw.external.device.AbstractDevice;
import eu.robojob.irscw.external.robot.AbstractRobot;
import eu.robojob.irscw.external.robot.Gripper;

public class PutStep extends AbstractTransportStep {

	private AbstractRobot robot;
	private Gripper gripper;
	private AbstractDevice deviceTo;
	private AbstractDevice.AbstractDevicePutSettings putSettings;
	private AbstractRobot.AbstractRobotPutSettings robotPutSettings;
	
	public PutStep(Process parentProcess, AbstractRobot robot, Gripper gripper, AbstractDevice deviceTo,
			AbstractDevice.AbstractDevicePutSettings putSettings, AbstractRobot.AbstractRobotPutSettings robotPutSettings) {
		super(parentProcess);
		this.robot = robot;
		this.gripper = gripper;
		this.deviceTo = deviceTo;
		this.robotPutSettings = robotPutSettings;
	}

	@Override
	public void executeStep() {
		deviceTo.prepareForPut(putSettings);
		robot.put(robotPutSettings);
		deviceTo.grabPiece(putSettings);
		robot.releasePiece(robotPutSettings);
	}

	@Override
	public AbstractProcessStep clone(Process parentProcess) {
		return new PutStep(parentProcess, robot, gripper, deviceTo, putSettings, robotPutSettings);
	}

	@Override
	public String toString() {
		return "PutStep to " + deviceTo + " using " + robot;
	}

	@Override
	public void finalize() {
		deviceTo.putFinished(putSettings);
	}

	public AbstractRobot getRobot() {
		return robot;
	}

	public Gripper getGripper() {
		return gripper;
	}

	public AbstractDevice getDeviceTo() {
		return deviceTo;
	}

}
