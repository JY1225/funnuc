package eu.robojob.irscw.process;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import eu.robojob.irscw.external.AbstractServiceProvider;
import eu.robojob.irscw.external.device.AbstractDevice;
import eu.robojob.irscw.external.device.AbstractDevice.AbstractDevicePickSettings;
import eu.robojob.irscw.external.robot.AbstractRobot;
import eu.robojob.irscw.external.robot.AbstractRobot.AbstractRobotPickSettings;
import eu.robojob.irscw.positioning.Coordinates;
import eu.robojob.irscw.workpiece.WorkPiece;
import eu.robojob.irscw.workpiece.WorkPiece.Type;

public class PickStep extends AbstractTransportStep {

	private static final Logger logger = Logger.getLogger(PickStep.class);
	
	private AbstractDevice.AbstractDevicePickSettings pickSettings;
	private AbstractRobot.AbstractRobotPickSettings robotPickSettings;
		
	public PickStep(ProcessFlow processFlow, AbstractRobot robot, AbstractDevice deviceFrom, AbstractDevice.AbstractDevicePickSettings pickSettings,
			AbstractRobot.AbstractRobotPickSettings robotPickSettings) {
		super(processFlow, deviceFrom, robot);
		this.pickSettings = pickSettings;
		if (pickSettings != null) {
			pickSettings.setStep(this);
		}
		this.robotPickSettings = robotPickSettings;
	}
	
	public PickStep(AbstractRobot robot, AbstractDevice deviceFrom, AbstractDevice.AbstractDevicePickSettings pickSettings,
			AbstractRobot.AbstractRobotPickSettings robotPickSettings) {
		this(null, robot, deviceFrom, pickSettings, robotPickSettings);
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
				if (needsTeaching()) {
					if (teachedOffset == null) {
						throw new IllegalStateException("Unknown teached position");
					} else {
						Coordinates position = device.getPickLocation(pickSettings.getWorkArea());
						position.offset(teachedOffset);
						robotPickSettings.setLocation(position);
					}
				} else {
					Coordinates position = device.getPickLocation(pickSettings.getWorkArea());
					robotPickSettings.setLocation(position);
				}
				robot.pick(robotPickSettings);
				robot.grabPiece(robotPickSettings);
				robotPickSettings.getGripper().setWorkPiece(robotPickSettings.getWorkPiece());
				device.releasePiece(pickSettings);
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
				device.prepareForPick(pickSettings);
				Coordinates coordinates = device.getPickLocation(pickSettings.getWorkArea());
				logger.info("pick location: " + coordinates);
				robot.moveTo(pickSettings.getWorkArea().getUserFrame(), coordinates);
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
				this.teachedOffset = coordinates.calculateOffset( device.getPickLocation(pickSettings.getWorkArea()));
				logger.info("teached offset: " + teachedOffset);
				robot.grabPiece(robotPickSettings);
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

	@Override
	public Set<AbstractServiceProvider> getServiceProviders() {
		Set<AbstractServiceProvider> providers = new HashSet<AbstractServiceProvider>();
		providers.add(device);
		providers.add(robot);
		return providers;
	}

	@Override
	public AbstractDevicePickSettings getDeviceSettings() {
		return pickSettings;
	}

	@Override
	public ProcessStepType getType() {
		return ProcessStepType.PICK_STEP;
	}

	@Override
	public AbstractRobotPickSettings getRobotSettings() {
		return robotPickSettings;
	}

	public void setRobotSettings(AbstractRobotPickSettings settings) {
		this.robotPickSettings = settings;
	}

	@Override
	public boolean needsTeaching() {
		// pick location is always fixed!
		if ((robotPickSettings.getWorkPiece().getDimensions().isKnownShape()) && (robotPickSettings.getGripper().isFixedHeight())) {
			return false;
		} else {
			return true;
		}
	}

	public Coordinates getTeachedOffset() {
		return teachedOffset;
	}
}
