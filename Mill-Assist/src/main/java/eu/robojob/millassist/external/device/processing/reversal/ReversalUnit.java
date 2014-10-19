package eu.robojob.millassist.external.device.processing.reversal;

import java.util.Map.Entry;
import java.util.Set;

import eu.robojob.millassist.external.communication.AbstractCommunicationException;
import eu.robojob.millassist.external.device.Clamping;
import eu.robojob.millassist.external.device.ClampingManner;
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
import eu.robojob.millassist.external.robot.AbstractRobotActionSettings.ApproachType;
import eu.robojob.millassist.positioning.Coordinates;
import eu.robojob.millassist.process.ProcessFlow;
import eu.robojob.millassist.workpiece.WorkPieceDimensions;

public class ReversalUnit extends AbstractProcessingDevice {
	
	private float stationHeight;
	
	public ReversalUnit(final String name, final float stationHeight) {
		super(name, false);
		this.stationHeight = stationHeight;
	}
	
	public ReversalUnit(final String name, final Set<Zone> zones, final float stationHeight) {
		super(name, zones, false);
		this.stationHeight = stationHeight;
	}

	public float getStationHeight() {
		return stationHeight;
	}

	public void setStationHeight(final float stationHeight) {
		this.stationHeight = stationHeight;
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
	public void grabPiece(final DevicePutSettings putSettings) throws AbstractCommunicationException, DeviceActionException, InterruptedException {	}
	
	@Override
	public ReversalUnitSettings getDeviceSettings() {
		return new ReversalUnitSettings();
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
	public Coordinates getPickLocation(final WorkArea workArea, final WorkPieceDimensions workPieceDimensions, final ClampingManner clampType) {
		Coordinates c = new Coordinates(workArea.getActiveClamping().getRelativePosition());
		c.setX(c.getX() + workPieceDimensions.getLength()/2);
		c.setY(c.getY() + workPieceDimensions.getWidth()/2);
		return c;
	}
	
	@Override
	public Coordinates getLocationOrientation(final WorkArea workArea, final ClampingManner clampType) {	
		return new Coordinates(workArea.getActiveClamping().getRelativePosition());
	}
	
	@Override
	public Coordinates getPutLocation(final WorkArea workArea, final WorkPieceDimensions workPieceDimensions, final ClampingManner clampType) {
		Coordinates c = new Coordinates(workArea.getActiveClamping().getRelativePosition());
		c.setX(c.getX() + workPieceDimensions.getLength()/2);
		c.setY(c.getY() + workPieceDimensions.getWidth()/2);
		return c;
	}
	
	@Override
	public boolean isConnected() {
		return true;
	}
	
	@Override
	public float getZSafePlane(final WorkPieceDimensions dimensions, final WorkArea workArea, final ApproachType approachType) throws IllegalArgumentException {
		if (approachType.equals(ApproachType.BOTTOM)) {
			float zSafePlane = workArea.getActiveClamping().getRelativePosition().getZ(); 
			System.out.println(zSafePlane);
			zSafePlane += ((ReversalUnit) workArea.getZone().getDevice()).getStationHeight();
			System.out.println(zSafePlane);
			return (zSafePlane * -1);
		} else {
			return super.getZSafePlane(dimensions, workArea, approachType);
		}
	}
	
	@Override
	public DeviceType getType() {
		return DeviceType.POST_PROCESSING;
	}

}
