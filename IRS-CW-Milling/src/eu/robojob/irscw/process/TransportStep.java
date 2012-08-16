package eu.robojob.irscw.process;

import eu.robojob.irscw.external.device.AbstractDevice;
import eu.robojob.irscw.external.device.WorkArea;
import eu.robojob.irscw.external.robot.AbstractRobot;
import eu.robojob.irscw.external.robot.Gripper;

public class TransportStep extends AbstractProcessStep {

	private AbstractRobot robot;
	private Gripper gripper;
	private AbstractDevice deviceFrom;
	private WorkArea workAreaFrom;
	private AbstractDevice.AbstractDevicePickSettings pickSettings;
	private AbstractDevice.AbstractDeviceClampingSettings fromClampingSettings;
	private AbstractDevice deviceTo;
	private AbstractDevice.AbstractDevicePutSettings putSettings;
	private AbstractDevice.AbstractDeviceClampingSettings toClampingSettings;
	private WorkArea workAreaTo;
	private AbstractRobot.AbstractRobotPickSettings robotPickSettings;
	private AbstractRobot.AbstractRobotPutSettings robotPutSettings;
	private AbstractRobot.AbstractRobotGriperSettings robotGriperSettings;
	
	public TransportStep (Process parentProcess, AbstractRobot robot, AbstractRobot.AbstractRobotPickSettings robotPickSettings,
			AbstractRobot.AbstractRobotPutSettings robotPutSettings, AbstractRobot.AbstractRobotGriperSettings robotGriperSettings,
			Gripper gripper, AbstractDevice deviceFrom, WorkArea workAreaFrom, 
			AbstractDevice.AbstractDevicePickSettings pickSettings, AbstractDevice.AbstractDeviceClampingSettings fromClampingSettings,
			AbstractDevice.AbstractDevicePutSettings putSettings, AbstractDevice.AbstractDeviceClampingSettings toClampingSettings,
			AbstractDevice deviceTo, WorkArea workAreaTo) {
		super(parentProcess);
		this.robot = robot;
		this.robotPickSettings = robotPickSettings;
		this.robotPutSettings = robotPutSettings;
		this.robotGriperSettings = robotGriperSettings;
		this.gripper = gripper;
		this.deviceFrom = deviceFrom;
		this.workAreaFrom = workAreaFrom;
		this.pickSettings = pickSettings;
		this.fromClampingSettings = fromClampingSettings;
		this.deviceTo = deviceTo;
		this.workAreaTo = workAreaTo;
		this.putSettings = putSettings;
		this.toClampingSettings = toClampingSettings;
	}
	
	//TODO add extra type of step to allow unloading and loading in one step
	@Override
	public void executeStep() {
		// check if the parent process has locked the devices to be used
		if (!deviceFrom.lock(parentProcess)) {
			throw new IllegalStateException("Device " + deviceFrom + " was already locked by: " + deviceFrom.getLockingProcess());
		} else {
			if (!deviceTo.lock(parentProcess)) {
				throw new IllegalStateException("Device " + deviceTo + " was already locked by: " + deviceTo.getLockingProcess());
			} else {
				if (!robot.lock(parentProcess)) {
					throw new IllegalStateException("Robot " + robot + " was already locked by: " + robot.getLockingProcess());
				} else {
					// all devices are properly locked, so we are allowed to continue
					deviceFrom.prepareForPick(workAreaFrom, pickSettings);
					robot.pick(workAreaFrom, robotPickSettings);
					robot.grabPiece(workAreaFrom, robotGriperSettings);
					deviceFrom.releasePiece(workAreaFrom, fromClampingSettings);
					deviceFrom.pickFinished(workAreaFrom, pickSettings);
					
					deviceTo.prepareForPut(workAreaTo, putSettings);
					robot.put(workAreaTo, robotPutSettings);
					deviceTo.grabPiece(workAreaTo, toClampingSettings);
					robot.releasePiece(workAreaTo, robotGriperSettings);
					deviceTo.putFinished(workAreaTo, putSettings);
				}
			}
		}
	}

	public AbstractRobot getRobot() {
		return robot;
	}

	public void setRobot(AbstractRobot robot) {
		this.robot = robot;
	}

	public AbstractRobot.AbstractRobotPickSettings getRobotPickSettings() {
		return robotPickSettings;
	}

	public void setRobotPickSettings(
			AbstractRobot.AbstractRobotPickSettings robotPickSettings) {
		this.robotPickSettings = robotPickSettings;
	}

	public AbstractRobot.AbstractRobotPutSettings getRobotPutSettings() {
		return robotPutSettings;
	}

	public void setRobotPutSettings(
			AbstractRobot.AbstractRobotPutSettings robotPutSettings) {
		this.robotPutSettings = robotPutSettings;
	}

	public Gripper getGripper() {
		return gripper;
	}

	public void setGripper(Gripper gripper) {
		this.gripper = gripper;
	}

	public AbstractDevice getDeviceFrom() {
		return deviceFrom;
	}

	public void setDeviceFrom(AbstractDevice deviceFrom) {
		this.deviceFrom = deviceFrom;
	}

	public WorkArea getWorkAreaFrom() {
		return workAreaFrom;
	}

	public void setWorkAreaFrom(WorkArea workAreaFrom) {
		this.workAreaFrom = workAreaFrom;
	}

	public AbstractDevice.AbstractDevicePickSettings getPickSettings() {
		return pickSettings;
	}

	public void setPickSettings(
			AbstractDevice.AbstractDevicePickSettings pickSettings) {
		this.pickSettings = pickSettings;
	}

	public AbstractDevice.AbstractDeviceClampingSettings getFromClampingSettings() {
		return fromClampingSettings;
	}

	public void setFromClampingSettings(
			AbstractDevice.AbstractDeviceClampingSettings fromClampingSettings) {
		this.fromClampingSettings = fromClampingSettings;
	}

	public AbstractDevice getDeviceTo() {
		return deviceTo;
	}

	public void setDeviceTo(AbstractDevice deviceTo) {
		this.deviceTo = deviceTo;
	}

	public WorkArea getWorkAreaTo() {
		return workAreaTo;
	}

	public void setWorkAreaTo(WorkArea workAreaTo) {
		this.workAreaTo = workAreaTo;
	}
	
	public AbstractDevice.AbstractDevicePutSettings getPutSettings() {
		return putSettings;
	}

	public void setPutSettings(AbstractDevice.AbstractDevicePutSettings putSettings) {
		this.putSettings = putSettings;
	}

	public AbstractDevice.AbstractDeviceClampingSettings getToClampingSettings() {
		return toClampingSettings;
	}

	public void setToClampingSettings(
			AbstractDevice.AbstractDeviceClampingSettings toClampingSettings) {
		this.toClampingSettings = toClampingSettings;
	}

	@Override
	public String toString() {
		return "TransportStep, " + "from: " + deviceFrom + "(" + workAreaFrom + "), to: " + deviceTo +
				"(" + workAreaTo + "), using: " + robot;
	}
	
}
