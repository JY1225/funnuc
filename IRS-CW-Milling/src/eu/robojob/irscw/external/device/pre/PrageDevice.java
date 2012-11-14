package eu.robojob.irscw.external.device.pre;

import java.util.List;

import org.apache.log4j.Logger;

import eu.robojob.irscw.external.communication.CommunicationException;
import eu.robojob.irscw.external.device.AbstractProcessingDevice;
import eu.robojob.irscw.external.device.DeviceActionException;
import eu.robojob.irscw.external.device.DeviceType;
import eu.robojob.irscw.external.device.WorkArea;
import eu.robojob.irscw.external.device.Zone;
import eu.robojob.irscw.external.robot.AbstractRobot;
import eu.robojob.irscw.external.robot.FanucRobotConstants;
import eu.robojob.irscw.external.robot.RobotActionException;
import eu.robojob.irscw.positioning.Coordinates;
import eu.robojob.irscw.workpiece.WorkPieceDimensions;

public class PrageDevice extends AbstractProcessingDevice {
	
	private AbstractRobot robot;
	
	private static final Logger logger = Logger.getLogger(PrageDevice.class);
	
	public PrageDevice(String id, AbstractRobot robot) {
		super(id, false);
		this.robot = robot;
	}
	
	public PrageDevice (String id, List<Zone> zones, AbstractRobot robot) {
		super(id, zones, false);
		this.robot = robot;
	}

	@Override
	public void startCyclus(AbstractProcessingDeviceStartCyclusSettings startCylusSettings) throws CommunicationException, DeviceActionException, InterruptedException {
		logger.info("start cyclus.");
	}

	@Override
	public void prepareForStartCyclus(AbstractProcessingDeviceStartCyclusSettings startCylusSettings) throws CommunicationException, DeviceActionException {
	}

	@Override
	public boolean validateStartCyclusSettings(AbstractProcessingDeviceStartCyclusSettings startCyclusSettings) {
		for (Zone zone : zones) {
			if (zone.getWorkAreas().contains(startCyclusSettings.getWorkArea())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean canPick(AbstractDevicePickSettings pickSettings) throws CommunicationException, DeviceActionException {
		return true;
	}
	@Override
	public boolean canPut(AbstractDevicePutSettings putSettings) throws CommunicationException, DeviceActionException, InterruptedException {
		return true;
	}

	@Override
	public void prepareForPick(AbstractDevicePickSettings pickSettings) throws CommunicationException, DeviceActionException, InterruptedException {
	}

	@Override
	public void prepareForPut(AbstractDevicePutSettings putSettings) throws CommunicationException, DeviceActionException, InterruptedException {
		// make sure Präge clamps are open
		try {
			robot.writeRegister(FanucRobotConstants.REGISTER_IPC_TO_ROBOT, ""+FanucRobotConstants.REGISTER_IPC_TO_ROBOT_PRAGE_UNCLAMP);
		} catch (RobotActionException e) {
			throw new DeviceActionException(e.getMessage());
		}
	}

	@Override
	public void prepareForIntervention(AbstractDeviceInterventionSettings interventionSettings) throws CommunicationException, DeviceActionException {}
	@Override
	public void pickFinished(AbstractDevicePickSettings pickSettings) throws CommunicationException, DeviceActionException {}
	@Override
	public void putFinished(AbstractDevicePutSettings putSettings) throws CommunicationException, DeviceActionException {}
	@Override
	public void interventionFinished(AbstractDeviceInterventionSettings interventionSettings) throws CommunicationException, DeviceActionException {}
	@Override
	public void releasePiece(AbstractDevicePickSettings pickSettings) throws CommunicationException, DeviceActionException, InterruptedException {
	}
	@Override
	public void grabPiece(AbstractDevicePutSettings putSettings) throws CommunicationException, DeviceActionException, InterruptedException {
		try {
			robot.doPrage();
		} catch (RobotActionException e) {
			throw new DeviceActionException(e.getMessage());
		}
	}
	@Override
	public void loadDeviceSettings(AbstractDeviceSettings deviceSettings) {}
	@Override
	public AbstractDeviceSettings getDeviceSettings() {
		return null;
	}
	@Override
	public boolean validatePickSettings(AbstractDevicePickSettings pickSettings) {
		return true;
	}
	@Override
	public boolean validatePutSettings(AbstractDevicePutSettings putSettings) {
		return true;
	}
	@Override
	public boolean validateInterventionSettings(AbstractDeviceInterventionSettings interventionSettings) {
		return true;
	}
	@Override
	public AbstractDeviceInterventionSettings getInterventionSettings(AbstractDevicePickSettings pickSettings) {
		return null;
	}
	@Override
	public AbstractDeviceInterventionSettings getInterventionSettings(AbstractDevicePutSettings putSettings) {
		return null;
	}
	@Override
	public Coordinates getPickLocation(WorkArea workArea) {
		return workArea.getActiveClamping().getRelativePosition();
	}
	@Override
	public Coordinates getPutLocation(WorkArea workArea, WorkPieceDimensions workPieceDimensions) {
		return workArea.getActiveClamping().getRelativePosition();
	}
	
	@Override
	public void stopCurrentAction() {}
	@Override
	public boolean isConnected() {
		return false;
	}
	@Override
	public DeviceType getType() {
		return DeviceType.PRE_PROCESSING;
	}
	
	public static class PrageDevicePickSettings extends AbstractProcessingDevice.AbstractProcessingDevicePickSettings {
		public PrageDevicePickSettings(WorkArea workArea) {
			super(workArea);
		}
	}
	public static class PrageDevicePutSettings extends AbstractProcessingDevice.AbstractProcessingDevicePutSettings {
		public PrageDevicePutSettings(WorkArea workArea) {
			super(workArea);
		}
		@Override
		public boolean isPutPositionFixed() {
			return true;
		}
	}
	public static class PrageDeviceStartCyclusSettings extends AbstractProcessingDevice.AbstractProcessingDeviceStartCyclusSettings {
		public PrageDeviceStartCyclusSettings(WorkArea workArea) {
			super(workArea);
		}
	}
	public static class PrageDeviceInterventionSettings extends AbstractProcessingDevice.AbstractProcessingDeviceInterventionSettings {
		public PrageDeviceInterventionSettings(WorkArea workArea) {
			super(workArea);
		}
	}
}
