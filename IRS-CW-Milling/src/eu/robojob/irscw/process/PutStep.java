package eu.robojob.irscw.process;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import eu.robojob.irscw.external.AbstractServiceProvider;
import eu.robojob.irscw.external.device.AbstractDevice;
import eu.robojob.irscw.external.device.AbstractDevice.AbstractDevicePutSettings;
import eu.robojob.irscw.external.robot.AbstractRobot;
import eu.robojob.irscw.external.robot.AbstractRobot.AbstractRobotPutSettings;
import eu.robojob.irscw.positioning.Coordinates;

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
		// check if the parent process has locked the devices to be used
		if (!device.lock(processFlow)) {
			throw new IllegalStateException("Device " + device + " was already locked by: " + device.getLockingProcess());
		} else {
			if (!robot.lock(processFlow)) {
				throw new IllegalStateException("Robot " + robot + " was already locked by: " + robot.getLockingProcess());
			} else {
				device.prepareForPut(putSettings);
				if (needsTeaching()) {
					if (teachedOffset == null) {
						throw new IllegalStateException("Unknown teached position");
					} else {
						Coordinates position = device.getPutLocation(putSettings.getWorkArea(), robotPutSettings.getGripper().getWorkPiece().getDimensions());
						position.offset(teachedOffset);
						robotPutSettings.setLocation(position);
					}
				} else {
					Coordinates position = device.getPutLocation(putSettings.getWorkArea(), robotPutSettings.getGripper().getWorkPiece().getDimensions());
					// no offset needed? sometimes there is! use offset of corresponding pick!
					// getting pick step:
					PickStep pickStep = (PickStep) processFlow.getStep(processFlow.getStepIndex(this) - 1);
					if (pickStep.needsTeaching()) {
						position.offset(pickStep.getTeachedOffset());
					}
					robotPutSettings.setLocation(position);
				}
				robot.put(robotPutSettings);
				device.grabPiece(putSettings);
				robot.releasePiece(robotPutSettings);
			}
		}
	}

	@Override
	public void prepareForTeaching() throws IOException {
		if (!device.lock(processFlow)) {
			throw new IllegalStateException("Device " + device + " was already locked by: " + device.getLockingProcess());
		} else {
			if (!robot.lock(processFlow)) {
				throw new IllegalStateException("Robot " + robot + " was already locked by: " + robot.getLockingProcess());
			} else {
				device.prepareForPut(putSettings);
				Coordinates coordinates = device.getPutLocation(putSettings.getWorkArea(), robotPutSettings.getGripper().getWorkPiece().getDimensions());
				robot.moveTo(putSettings.getWorkArea().getUserFrame(), coordinates);
				robot.setTeachModeEnabled(true);
			}
		}
	}

	@Override
	public void teachingFinished() throws IOException {
		if (!device.lock(processFlow)) {
			throw new IllegalStateException("Device " + device + " was already locked by: " + device.getLockingProcess());
		} else {
			if (!robot.lock(processFlow)) {
				throw new IllegalStateException("Robot " + robot + " was already locked by: " + robot.getLockingProcess());
			} else {
				robot.setTeachModeEnabled(false);
				Coordinates coordinates = robot.getPosition();
				teachedOffset = coordinates.calculateOffset(device.getPutLocation(putSettings.getWorkArea(), robotPutSettings.getGripper().getWorkPiece().getDimensions()));
				device.grabPiece(putSettings);
				robot.releasePiece(robotPutSettings);
			}
		}
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
