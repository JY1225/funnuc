package eu.robojob.irscw.process;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import eu.robojob.irscw.external.AbstractServiceProvider;
import eu.robojob.irscw.external.device.AbstractDevice;
import eu.robojob.irscw.external.device.AbstractDevice.AbstractDevicePutSettings;
import eu.robojob.irscw.external.robot.AbstractRobot;
import eu.robojob.irscw.external.robot.AbstractRobot.AbstractRobotPutSettings;

public class PutStep extends AbstractTransportStep {

	private AbstractDevice.AbstractDevicePutSettings putSettings;
	private AbstractRobot.AbstractRobotPutSettings robotPutSettings;
	
	public PutStep(ProcessFlow processFlow, AbstractRobot robot, AbstractDevice deviceTo,
			AbstractDevice.AbstractDevicePutSettings putSettings, AbstractRobot.AbstractRobotPutSettings robotPutSettings) {
		super(processFlow, deviceTo, robot);
		this.putSettings = putSettings;
		if (putSettings != null) {
			putSettings.setStep(this);
		}
		this.robotPutSettings = robotPutSettings;
	}
	
	public PutStep(AbstractRobot robot, AbstractDevice deviceTo, AbstractDevice.AbstractDevicePutSettings putSettings,
			AbstractRobot.AbstractRobotPutSettings robotPutSettings) {
		this(null, robot, deviceTo, putSettings, robotPutSettings);
	}

	@Override
	public void executeStep() throws IOException {
		device.prepareForPut(putSettings);
		robot.put(robotPutSettings);
		device.grabPiece(putSettings);
		robot.releasePiece(robotPutSettings);
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
	
	@Override
	public Set<AbstractServiceProvider> getServiceProviders() {
		Set<AbstractServiceProvider> providers = new HashSet<AbstractServiceProvider>();
		providers.add(device);
		providers.add(robot);
		return providers;
	}

	@Override
	public AbstractDevicePutSettings getDeviceSettings() {
		return putSettings;
	}

	@Override
	public ProcessStepType getType() {
		return ProcessStepType.PUT_STEP;
	}

	@Override
	public AbstractRobotPutSettings getRobotSettings() {
		return robotPutSettings;
	}
	
	public void setRobotSettings(AbstractRobotPutSettings settings) {
		this.robotPutSettings = settings;
	}

	@Override
	public boolean needsTeaching() {
		// since we already know the work piece's dimensions (ground pane) and griper height from picking it up
		if (putSettings.isPutPositionFixed()) {
			return false;
		} else {
			return true;
		}
	}

}
