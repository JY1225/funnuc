package eu.robojob.irscw.process;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import eu.robojob.irscw.external.AbstractServiceProvider;
import eu.robojob.irscw.external.device.AbstractDevice;
import eu.robojob.irscw.external.device.AbstractDevice.AbstractDeviceActionSettings;
import eu.robojob.irscw.external.robot.AbstractRobot;
import eu.robojob.irscw.external.robot.Gripper;

public class PickStep extends AbstractTransportStep {

	private Gripper gripper;
	private AbstractDevice.AbstractDevicePickSettings pickSettings;
	private AbstractRobot.AbstractRobotPickSettings robotPickSettings;
	
	public PickStep(ProcessFlow processFlow, AbstractRobot robot, Gripper gripper, AbstractDevice deviceFrom, AbstractDevice.AbstractDevicePickSettings pickSettings,
			AbstractRobot.AbstractRobotPickSettings robotPickSettings) {
		super(processFlow, deviceFrom, robot);
		this.gripper = gripper;
		this.pickSettings = pickSettings;
		this.robotPickSettings = robotPickSettings;
	}
	
	public PickStep(AbstractRobot robot, Gripper gripper, AbstractDevice deviceFrom, AbstractDevice.AbstractDevicePickSettings pickSettings,
			AbstractRobot.AbstractRobotPickSettings robotPickSettings) {
		this(null, robot, gripper, deviceFrom, pickSettings, robotPickSettings);
	}
	
	@Override
	public PickStep clone(ProcessFlow parentProcess) {
		return new PickStep(parentProcess, robot, gripper, device, pickSettings, robotPickSettings);
	}

	@Override
	public void executeStep() throws IOException {
		// check if the parent process has locked the devices to be used
		if (!device.lock(processFlow)) {
			throw new IllegalStateException("Device " + device + " was already locked by: " + device.getLockingProcess());
		} else {
			if (!robot.lock(processFlow)) {
				throw new IllegalStateException("Robot " + robot + " was already locked by: " + robot.getLockingProcess());
			} else {
				device.prepareForPick(pickSettings);
				robot.pick(robotPickSettings);
				robot.grabPiece(robotPickSettings);
				device.releasePiece(pickSettings);
			}
		}
	}
	
	@Override
	public void finalize() throws IOException {
		if (!device.lock(processFlow)) {
			throw new IllegalStateException("Device " + device + " was already locked by: " + device.getLockingProcess());
		} else {
			if (!robot.lock(processFlow)) {
				throw new IllegalStateException("Robot " + robot + " was already locked by: " + robot.getLockingProcess());
			} else {
				device.pickFinished(pickSettings);
				device.release(processFlow);
				robot.release(processFlow);
			}
		}
	}

	@Override
	public String toString() {
		return "PickStep from " + device + " with: " + robot;
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

	@Override
	public AbstractDeviceActionSettings getDeviceSettings() {
		return pickSettings;
	}

}
