package eu.robojob.irscw.process;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import eu.robojob.irscw.external.AbstractServiceProvider;
import eu.robojob.irscw.external.device.AbstractDevice;
import eu.robojob.irscw.external.device.AbstractDevice.AbstractDeviceActionSettings;
import eu.robojob.irscw.external.robot.AbstractRobot;
import eu.robojob.irscw.external.robot.Gripper;

public class PutStep extends AbstractTransportStep {

	private Gripper gripper;
	private AbstractDevice.AbstractDevicePutSettings putSettings;
	private AbstractRobot.AbstractRobotPutSettings robotPutSettings;
	
	public PutStep(ProcessFlow processFlow, AbstractRobot robot, Gripper gripper, AbstractDevice deviceTo,
			AbstractDevice.AbstractDevicePutSettings putSettings, AbstractRobot.AbstractRobotPutSettings robotPutSettings) {
		super(processFlow, deviceTo, robot);
		this.gripper = gripper;
		this.putSettings = putSettings;
		this.robotPutSettings = robotPutSettings;
	}
	
	public PutStep(AbstractRobot robot, Gripper gripper, AbstractDevice deviceTo, AbstractDevice.AbstractDevicePutSettings putSettings,
			AbstractRobot.AbstractRobotPutSettings robotPutSettings) {
		this(null, robot, gripper, deviceTo, putSettings, robotPutSettings);
	}

	@Override
	public void executeStep() throws IOException {
		device.prepareForPut(putSettings);
		robot.put(robotPutSettings);
		device.grabPiece(putSettings);
		robot.releasePiece(robotPutSettings);
	}

	@Override
	public AbstractProcessStep clone(ProcessFlow parentProcess) {
		return new PutStep(parentProcess, robot, gripper, device, putSettings, robotPutSettings);
	}

	@Override
	public String toString() {
		return "PutStep to " + device + " using " + robot;
	}

	@Override
	public void finalize() throws IOException {
		if (!device.lock(processFlow)) {
			throw new IllegalStateException("Device " + device + " was already locked by: " + device.getLockingProcess());
		} else {
			if (!robot.lock(processFlow)) {
				throw new IllegalStateException("Robot " + robot + " was already locked by: " + robot.getLockingProcess());
			} else {
				device.putFinished(putSettings);
				device.release(processFlow);
				robot.release(processFlow);
			}
		}
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
		return putSettings;
	}

}
