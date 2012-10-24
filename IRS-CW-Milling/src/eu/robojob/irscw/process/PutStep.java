package eu.robojob.irscw.process;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import eu.robojob.irscw.external.AbstractServiceProvider;
import eu.robojob.irscw.external.communication.CommunicationException;
import eu.robojob.irscw.external.device.AbstractDevice;
import eu.robojob.irscw.external.device.AbstractDevice.AbstractDevicePutSettings;
import eu.robojob.irscw.external.device.DeviceActionException;
import eu.robojob.irscw.external.robot.AbstractRobot;
import eu.robojob.irscw.external.robot.AbstractRobot.AbstractRobotPutSettings;
import eu.robojob.irscw.external.robot.RobotActionException;
import eu.robojob.irscw.positioning.Coordinates;

public class PutStep extends AbstractTransportStep {

	private AbstractDevice.AbstractDevicePutSettings putSettings;
	private AbstractRobot.AbstractRobotPutSettings robotPutSettings;
	
	private static final Logger logger = Logger.getLogger(PutStep.class);
	
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
	public void executeStep() throws CommunicationException, RobotActionException, DeviceActionException {
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
				robot.initiatePut(robotPutSettings);
				device.grabPiece(putSettings);
				robot.finalizePut(robotPutSettings);
				robot.moveToHome();
				device.putFinished(putSettings);
			}
		}
	}

	@Override
	public void prepareForTeaching() throws CommunicationException, RobotActionException, DeviceActionException {
		if (!device.lock(processFlow)) {
			throw new IllegalStateException("Device " + device + " was already locked by: " + device.getLockingProcess());
		} else {
			if (!robot.lock(processFlow)) {
				throw new IllegalStateException("Robot " + robot + " was already locked by: " + robot.getLockingProcess());
			} else {
				device.prepareForPut(putSettings);
				Coordinates coordinates = device.getPutLocation(putSettings.getWorkArea(), robotPutSettings.getGripper().getWorkPiece().getDimensions());
				logger.info("put location: " + coordinates);
				robotPutSettings.setLocation(coordinates);
				robot.initiatePut(robotPutSettings);
			}
		}
	}

	@Override
	public void teachingFinished() throws CommunicationException, RobotActionException, DeviceActionException {
		if (!device.lock(processFlow)) {
			throw new IllegalStateException("Device " + device + " was already locked by: " + device.getLockingProcess());
		} else {
			if (!robot.lock(processFlow)) {
				throw new IllegalStateException("Robot " + robot + " was already locked by: " + robot.getLockingProcess());
			} else {
				// TODO: check!
				Coordinates coordinates = robot.getPosition();
				this.teachedOffset = coordinates.calculateOffset(device.getPickLocation(putSettings.getWorkArea()));
				logger.info("teached offset: " + teachedOffset);
				robotPutSettings.setLocation(device.getPutLocation(putSettings.getWorkArea(), robotPutSettings.getGripper().getWorkPiece().getDimensions()));
				device.grabPiece(putSettings);
				robot.finalizeTeachedPut(robotPutSettings);
			}
		}
	}

	@Override
	public String toString() {
		return "PutStep to " + device + " using " + robot;
	}

	@Override
	public void finalize() throws CommunicationException, DeviceActionException {
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
