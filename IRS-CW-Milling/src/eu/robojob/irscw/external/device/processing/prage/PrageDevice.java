package eu.robojob.irscw.external.device.processing.prage;

import java.util.List;

import org.apache.log4j.Logger;

import eu.robojob.irscw.external.communication.AbstractCommunicationException;
import eu.robojob.irscw.external.device.DeviceInterventionSettings;
import eu.robojob.irscw.external.device.DevicePickSettings;
import eu.robojob.irscw.external.device.DevicePutSettings;
import eu.robojob.irscw.external.device.DeviceSettings;
import eu.robojob.irscw.external.device.ClampingManner;
import eu.robojob.irscw.external.device.ClampingManner.Type;
import eu.robojob.irscw.external.device.DeviceActionException;
import eu.robojob.irscw.external.device.DeviceType;
import eu.robojob.irscw.external.device.WorkArea;
import eu.robojob.irscw.external.device.Zone;
import eu.robojob.irscw.external.device.processing.AbstractProcessingDevice;
import eu.robojob.irscw.external.device.processing.ProcessingDeviceStartCyclusSettings;
import eu.robojob.irscw.external.robot.AbstractRobot;
import eu.robojob.irscw.external.robot.RobotActionException;
import eu.robojob.irscw.positioning.Coordinates;
import eu.robojob.irscw.process.ProcessFlow;
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
	public void startCyclus(ProcessingDeviceStartCyclusSettings startCylusSettings) throws AbstractCommunicationException, DeviceActionException, InterruptedException {
		logger.info("start cyclus.");
	}

	@Override
	public void prepareForStartCyclus(ProcessingDeviceStartCyclusSettings startCylusSettings) throws AbstractCommunicationException, DeviceActionException {
	}

	@Override
	public boolean validateStartCyclusSettings(ProcessingDeviceStartCyclusSettings startCyclusSettings) {
		for (Zone zone : zones) {
			if (zone.getWorkAreas().contains(startCyclusSettings.getWorkArea())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean canPick(DevicePickSettings pickSettings) throws AbstractCommunicationException, DeviceActionException {
		return true;
	}
	@Override
	public boolean canPut(DevicePutSettings putSettings) throws AbstractCommunicationException, DeviceActionException, InterruptedException {
		return true;
	}
	@Override
	public boolean canIntervention(DeviceInterventionSettings interventionSettings) throws AbstractCommunicationException, DeviceActionException {
		return false;
	}

	@Override
	public void prepareForPick(DevicePickSettings pickSettings) throws AbstractCommunicationException, DeviceActionException, InterruptedException {
	}

	@Override
	public void prepareForPut(DevicePutSettings putSettings) throws AbstractCommunicationException, DeviceActionException, InterruptedException {
	}

	@Override
	public void prepareForIntervention(DeviceInterventionSettings interventionSettings) throws AbstractCommunicationException, DeviceActionException {}
	@Override
	public void pickFinished(DevicePickSettings pickSettings) throws AbstractCommunicationException, DeviceActionException {}
	@Override
	public void putFinished(DevicePutSettings putSettings) throws AbstractCommunicationException, DeviceActionException {}
	@Override
	public void interventionFinished(DeviceInterventionSettings interventionSettings) throws AbstractCommunicationException, DeviceActionException {}
	@Override
	public void releasePiece(DevicePickSettings pickSettings) throws AbstractCommunicationException, DeviceActionException, InterruptedException {
	}
	@Override
	public void grabPiece(DevicePutSettings putSettings) throws AbstractCommunicationException, DeviceActionException, InterruptedException {
		try {
			robot.doPrage();
		} catch (RobotActionException e) {
			throw new DeviceActionException(e.getMessage());
		}
	}
	@Override
	public void loadDeviceSettings(DeviceSettings deviceSettings) {}
	@Override
	public DeviceSettings getDeviceSettings() {
		return null;
	}
	@Override
	public boolean validatePickSettings(DevicePickSettings pickSettings) {
		return true;
	}
	@Override
	public boolean validatePutSettings(DevicePutSettings putSettings) {
		return true;
	}
	@Override
	public boolean validateInterventionSettings(DeviceInterventionSettings interventionSettings) {
		return true;
	}
	@Override
	public Coordinates getPickLocation(WorkArea workArea, ClampingManner clampType) {
		Coordinates c = new Coordinates(workArea.getActiveClamping().getRelativePosition());
		if (clampType.getType() == Type.LENGTH) {
			c.setR(90);
		} else {
			c.setR(0);
		}
		return c;
	}
	@Override
	public Coordinates getPutLocation(WorkArea workArea, WorkPieceDimensions workPieceDimensions, ClampingManner clampType) {
		Coordinates c = new Coordinates(workArea.getActiveClamping().getRelativePosition());
		if (clampType.getType() == Type.LENGTH) {
			c.offset(new Coordinates(0, workPieceDimensions.getWidth()/2, 0, 0, 0, 0));
			c.setR(90);
		} else {
			c.offset(new Coordinates(0, workPieceDimensions.getLength()/2, 0, 0, 0, 0));
			c.setR(0);
		}
		return c;
	}
	
	@Override
	public void interruptCurrentAction() {}
	@Override
	public boolean isConnected() {
		return false;
	}
	@Override
	public DeviceType getType() {
		return DeviceType.PRE_PROCESSING;
	}
	
	@Override
	public void prepareForProcess(ProcessFlow process) throws AbstractCommunicationException, InterruptedException {		
	}

}
