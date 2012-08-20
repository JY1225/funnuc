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
		// TODO Auto-generated method stub
		
	}

	@Override
	public AbstractProcessStep clone(Process parentProcess) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void finalize() {
		// TODO Auto-generated method stub
		
	}

}
