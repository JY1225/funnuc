package eu.robojob.millassist.external.device.processing.prage;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import eu.robojob.millassist.external.communication.AbstractCommunicationException;
import eu.robojob.millassist.external.device.Clamping;
import eu.robojob.millassist.external.device.ClampingManner;
import eu.robojob.millassist.external.device.ClampingManner.Type;
import eu.robojob.millassist.external.device.DeviceActionException;
import eu.robojob.millassist.external.device.DeviceInterventionSettings;
import eu.robojob.millassist.external.device.DevicePickSettings;
import eu.robojob.millassist.external.device.DevicePutSettings;
import eu.robojob.millassist.external.device.DeviceSettings;
import eu.robojob.millassist.external.device.DeviceType;
import eu.robojob.millassist.external.device.WorkArea;
import eu.robojob.millassist.external.device.Zone;
import eu.robojob.millassist.external.device.processing.AbstractProcessingDevice;
import eu.robojob.millassist.external.device.processing.ProcessingDeviceStartCyclusSettings;
import eu.robojob.millassist.external.robot.RobotActionException;
import eu.robojob.millassist.positioning.Coordinates;
import eu.robojob.millassist.process.ProcessFlow;
import eu.robojob.millassist.workpiece.WorkPieceDimensions;

public class PrageDevice extends AbstractProcessingDevice {
		
	private static final String EXCEPTION_PRAGE_TIMEOUT = "PrageDevice.prageTimeout";
	
	private int clampingWidthDeltaR;
	
	public PrageDevice(final String name, final int clampingWidthDeltaR) {
		super(name, false);
		this.clampingWidthDeltaR = clampingWidthDeltaR;
	}
	
	public PrageDevice(final String name, final Set<Zone> zones, final int clampingWidthDeltaR) {
		super(name, zones, false);
		this.clampingWidthDeltaR = clampingWidthDeltaR;
	}

	public int getClampingWidthDeltaR() {
		return clampingWidthDeltaR;
	}

	public void setClampingWidthDeltaR(final int clampingWidthDeltaR) {
		this.clampingWidthDeltaR = clampingWidthDeltaR;
	}

	@Override public void startCyclus(final ProcessingDeviceStartCyclusSettings startCylusSettings) throws AbstractCommunicationException, DeviceActionException, InterruptedException { }
	@Override public void prepareForStartCyclus(final ProcessingDeviceStartCyclusSettings startCylusSettings) throws AbstractCommunicationException, DeviceActionException { }
	@Override public void prepareForPick(final DevicePickSettings pickSettings) throws AbstractCommunicationException, DeviceActionException, InterruptedException { }
	@Override public void prepareForPut(final DevicePutSettings putSettings) throws AbstractCommunicationException, DeviceActionException, InterruptedException { }
	@Override public void prepareForIntervention(final DeviceInterventionSettings interventionSettings) throws AbstractCommunicationException, DeviceActionException { }
	@Override public void pickFinished(final DevicePickSettings pickSettings) throws AbstractCommunicationException, DeviceActionException { }
	@Override public void putFinished(final DevicePutSettings putSettings) throws AbstractCommunicationException, DeviceActionException { }
	@Override public void interventionFinished(final DeviceInterventionSettings interventionSettings) throws AbstractCommunicationException, DeviceActionException { }
	@Override public void releasePiece(final DevicePickSettings pickSettings) throws AbstractCommunicationException, DeviceActionException, InterruptedException { }
	@Override public void loadDeviceSettings(final DeviceSettings deviceSettings) {
		for (Entry<WorkArea, Clamping> entry : deviceSettings.getClampings().entrySet()) {
			entry.getKey().setActiveClamping(entry.getValue());
		}
	}
	@Override public void interruptCurrentAction() { }
	@Override public void prepareForProcess(final ProcessFlow process) throws AbstractCommunicationException, InterruptedException { }
	@Override public void reset() throws AbstractCommunicationException, DeviceActionException, InterruptedException { }
	
	@Override
	public boolean canPick(final DevicePickSettings pickSettings) throws AbstractCommunicationException, DeviceActionException {
		return true;
	}
	@Override
	public boolean canPut(final DevicePutSettings putSettings) throws AbstractCommunicationException, DeviceActionException, InterruptedException {
		return true;
	}
	@Override
	public boolean canIntervention(final DeviceInterventionSettings interventionSettings) throws AbstractCommunicationException, DeviceActionException {
		return true;
	}
	
	@Override
	public void grabPiece(final DevicePutSettings putSettings) throws AbstractCommunicationException, DeviceActionException, InterruptedException {
		try {
			putSettings.getStep().getRobot().performIOAction();
		} catch (RobotActionException e) {
			throw new DeviceActionException(this, EXCEPTION_PRAGE_TIMEOUT);
		}
	}
	
	@Override
	public DeviceSettings getDeviceSettings() {
		Map<WorkArea, Clamping> clampings = new HashMap<WorkArea, Clamping>();
		for (WorkArea workArea : getWorkAreas()) {
			clampings.put(workArea, workArea.getActiveClamping());
		}
		return new DeviceSettings();
	}
	
	@Override
	public boolean validateStartCyclusSettings(final ProcessingDeviceStartCyclusSettings startCyclusSettings) {
		if (getWorkAreaNames().contains(startCyclusSettings.getWorkArea().getName())) {
			return true;
		}
		return false;
	}
	
	@Override
	public boolean validatePickSettings(final DevicePickSettings pickSettings) {
		if (getWorkAreaNames().contains(pickSettings.getWorkArea().getName())) {
			return true;
		}
		return false;
	}
	
	@Override
	public boolean validatePutSettings(final DevicePutSettings putSettings) {
		if (getWorkAreaNames().contains(putSettings.getWorkArea().getName())) {
			return true;
		}
		return false;
	}
	
	@Override
	public boolean validateInterventionSettings(final DeviceInterventionSettings interventionSettings) {
		if (getWorkAreaNames().contains(interventionSettings.getWorkArea().getName())) {
			return true;
		}
		return false;
	}
	
	@Override
	public Coordinates getPickLocation(final WorkArea workArea, final ClampingManner clampType) {
		throw new IllegalStateException("This method should never be called");
	}
	
	@Override
	public Coordinates getLocationOrientation(final WorkArea workArea) {
		Coordinates c = new Coordinates(workArea.getActiveClamping().getRelativePosition());
		return c;
	}
	
	@Override
	public Coordinates getPutLocation(final WorkArea workArea, final WorkPieceDimensions workPieceDimensions, final ClampingManner clampType) {
		Coordinates c = new Coordinates(workArea.getActiveClamping().getRelativePosition());
		if (clampType.getType() == Type.LENGTH) {
			c.offset(new Coordinates(0, workPieceDimensions.getWidth() / 2, 0, 0, 0, 0));
		} else {
			c.offset(new Coordinates(0, workPieceDimensions.getLength() / 2, 0, 0, 0, 0));
			c.setR(clampingWidthDeltaR);
		}
		return c;
	}
	
	@Override
	public boolean isConnected() {
		return true;
	}
	
	@Override
	public DeviceType getType() {
		return DeviceType.PRE_PROCESSING;
	}

}
