package eu.robojob.irscw.external.device.processing.prage;

import java.util.List;

import eu.robojob.irscw.external.communication.AbstractCommunicationException;
import eu.robojob.irscw.external.device.ClampingManner;
import eu.robojob.irscw.external.device.ClampingManner.Type;
import eu.robojob.irscw.external.device.DeviceActionException;
import eu.robojob.irscw.external.device.DeviceInterventionSettings;
import eu.robojob.irscw.external.device.DevicePickSettings;
import eu.robojob.irscw.external.device.DevicePutSettings;
import eu.robojob.irscw.external.device.DeviceSettings;
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
		
	private static final String EXCEPTION_PRAGE_TIMEOUT = "PrageDevice.prageTimeout";
	
	private static final float LENGTH_CLAMP_LOCATION_R = 90;
	private static final float WIDTH_CLAMP_LOCATION_R = 0;
	
	public PrageDevice(String id, AbstractRobot robot) {
		super(id, false);
		this.robot = robot;
	}
	
	public PrageDevice (String id, List<Zone> zones, AbstractRobot robot) {
		super(id, zones, false);
		this.robot = robot;
	}

	@Override public void startCyclus(ProcessingDeviceStartCyclusSettings startCylusSettings) throws AbstractCommunicationException, DeviceActionException, InterruptedException {}
	@Override public void prepareForStartCyclus(ProcessingDeviceStartCyclusSettings startCylusSettings) throws AbstractCommunicationException, DeviceActionException {}
	@Override public void prepareForPick(DevicePickSettings pickSettings) throws AbstractCommunicationException, DeviceActionException, InterruptedException {}
	@Override public void prepareForPut(DevicePutSettings putSettings) throws AbstractCommunicationException, DeviceActionException, InterruptedException {}
	@Override public void prepareForIntervention(DeviceInterventionSettings interventionSettings) throws AbstractCommunicationException, DeviceActionException {}
	@Override public void pickFinished(DevicePickSettings pickSettings) throws AbstractCommunicationException, DeviceActionException {}
	@Override public void putFinished(DevicePutSettings putSettings) throws AbstractCommunicationException, DeviceActionException {}
	@Override public void interventionFinished(DeviceInterventionSettings interventionSettings) throws AbstractCommunicationException, DeviceActionException {}
	@Override public void releasePiece(DevicePickSettings pickSettings) throws AbstractCommunicationException, DeviceActionException, InterruptedException {}
	@Override public void loadDeviceSettings(DeviceSettings deviceSettings) {}
	@Override public void interruptCurrentAction() {}
	@Override public void prepareForProcess(ProcessFlow process) throws AbstractCommunicationException, InterruptedException {}
	
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
		return true;
	}
	
	@Override
	public void grabPiece(DevicePutSettings putSettings) throws AbstractCommunicationException, DeviceActionException, InterruptedException {
		try {
			robot.doPrage();
		} catch (RobotActionException e) {
			throw new DeviceActionException(this, EXCEPTION_PRAGE_TIMEOUT);
		}
	}
	
	@Override
	public DeviceSettings getDeviceSettings() {
		return null;
	}
	
	@Override
	public boolean validateStartCyclusSettings(ProcessingDeviceStartCyclusSettings startCyclusSettings) {
		if (getWorkAreaIds().contains(startCyclusSettings.getWorkArea().getId())) {
			return true;
		}
		return false;
	}
	
	@Override
	public boolean validatePickSettings(DevicePickSettings pickSettings) {
		if (getWorkAreaIds().contains(pickSettings.getWorkArea().getId())) {
			return true;
		}
		return false;
	}
	
	@Override
	public boolean validatePutSettings(DevicePutSettings putSettings) {
		if (getWorkAreaIds().contains(putSettings.getWorkArea().getId())) {
			return true;
		}
		return false;
	}
	
	@Override
	public boolean validateInterventionSettings(DeviceInterventionSettings interventionSettings) {
		if (getWorkAreaIds().contains(interventionSettings.getWorkArea().getId())) {
			return true;
		}
		return false;
	}
	
	@Override
	public Coordinates getPickLocation(WorkArea workArea, ClampingManner clampType) {
		throw new IllegalStateException("This method should never be called");
	}
	
	@Override
	public Coordinates getPutLocation(WorkArea workArea, WorkPieceDimensions workPieceDimensions, ClampingManner clampType) {
		Coordinates c = new Coordinates(workArea.getActiveClamping().getRelativePosition());
		if (clampType.getType() == Type.LENGTH) {
			c.offset(new Coordinates(0, workPieceDimensions.getWidth()/2, 0, 0, 0, 0));
			c.setR(LENGTH_CLAMP_LOCATION_R);
		} else {
			c.offset(new Coordinates(0, workPieceDimensions.getLength()/2, 0, 0, 0, 0));
			c.setR(WIDTH_CLAMP_LOCATION_R);
		}
		return c;
	}
	
	@Override
	public boolean isConnected() {
		return false;
	}
	
	@Override
	public DeviceType getType() {
		return DeviceType.PRE_PROCESSING;
	}

}
