package eu.robojob.irscw.process;

import eu.robojob.irscw.external.device.AbstractDevice;
import eu.robojob.irscw.external.robot.AbstractRobot;
import eu.robojob.irscw.external.robot.Gripper;

public class PickStep extends AbstractTransportStep {

	private AbstractRobot robot;
	private Gripper gripper;
	private AbstractDevice deviceFrom;
	private AbstractDevice.AbstractDevicePickSettings pickSettings;
	private AbstractRobot.AbstractRobotPickSettings robotPickSettings;
	
	public PickStep(Process parentProcess, AbstractRobot robot, Gripper gripper, AbstractDevice deviceFrom, AbstractDevice.AbstractDevicePickSettings pickSettings,
			AbstractRobot.AbstractRobotPickSettings robotPickSettings) {
		super(parentProcess);
		this.robot = robot;
		this.gripper = gripper;
		this.deviceFrom = deviceFrom;
		this.pickSettings = pickSettings;
		this.robotPickSettings = robotPickSettings;
	}
	
	@Override
	public PickStep clone(Process parentProcess) {
		return new PickStep(parentProcess, robot, gripper, deviceFrom, pickSettings, robotPickSettings);
	}

	@Override
	public void executeStep() {
		// check if the parent process has locked the devices to be used
		if (!deviceFrom.lock(parentProcess)) {
			throw new IllegalStateException("Device " + deviceFrom + " was already locked by: " + deviceFrom.getLockingProcess());
		} else {
			if (!robot.lock(parentProcess)) {
				throw new IllegalStateException("Robot " + robot + " was already locked by: " + robot.getLockingProcess());
			} else {
				deviceFrom.prepareForPick(pickSettings);
				robot.pick(robotPickSettings);
				robot.grabPiece(robotPickSettings);
				deviceFrom.releasePiece(pickSettings);
			}
		}
	}
	
	@Override
	public void finalize() {
		if (!deviceFrom.lock(parentProcess)) {
			throw new IllegalStateException("Device " + deviceFrom + " was already locked by: " + deviceFrom.getLockingProcess());
		} else {
			if (!robot.lock(parentProcess)) {
				throw new IllegalStateException("Robot " + robot + " was already locked by: " + robot.getLockingProcess());
			} else {
				deviceFrom.pickFinished(pickSettings);
			}
		}
	}

	@Override
	public String toString() {
		return "PickStep from " + deviceFrom + " with: " + robot;
	}

	public AbstractRobot getRobot() {
		return robot;
	}

	public Gripper getGripper() {
		return gripper;
	}

	public AbstractDevice getDeviceFrom() {
		return deviceFrom;
	}

}
