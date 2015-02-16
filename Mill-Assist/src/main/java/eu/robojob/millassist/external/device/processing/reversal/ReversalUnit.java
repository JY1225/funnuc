package eu.robojob.millassist.external.device.processing.reversal;

import java.util.HashMap;
import java.util.Map;
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
import eu.robojob.millassist.external.device.EDeviceGroup;
import eu.robojob.millassist.external.device.SimpleWorkArea;
import eu.robojob.millassist.external.device.Zone;
import eu.robojob.millassist.external.device.processing.AbstractProcessingDevice;
import eu.robojob.millassist.external.device.processing.ProcessingDeviceStartCyclusSettings;
import eu.robojob.millassist.external.robot.AbstractRobotActionSettings.ApproachType;
import eu.robojob.millassist.positioning.Coordinates;
import eu.robojob.millassist.process.ProcessFlow;
import eu.robojob.millassist.util.PropertyManager;
import eu.robojob.millassist.util.PropertyManager.Setting;
import eu.robojob.millassist.workpiece.WorkPieceDimensions;

public class ReversalUnit extends AbstractProcessingDevice {
	
	private float stationHeight;
	private float stationLength;
	private float stationFixtureWidth;
	private boolean isWidthReversal = false;
	private float addedXValue;
	private boolean isShiftedOrigin;
	private Map<ApproachType, Boolean> allowedApproachTypes = new HashMap<ApproachType, Boolean>();
	
	public ReversalUnit(final String name, final float stationLength, final float stationFixtureWidth, final float stationHeight, final float addedXValue) {
		super(name, false);
		this.stationLength = stationLength;
		this.stationFixtureWidth = stationFixtureWidth;
		this.stationHeight = stationHeight;
		this.addedXValue = addedXValue;
		setWidthReversal();
	}
	
	public ReversalUnit(final String name, final Set<Zone> zones, final float stationLength, final float stationFixtureWidth, final float stationHeight, final float addedXValue) {
		super(name, zones, false);
		this.stationLength = stationLength;
		this.stationFixtureWidth = stationFixtureWidth;
		this.stationHeight = stationHeight;
		this.addedXValue = addedXValue;
		setWidthReversal();
	}

	public float getStationHeight() {
		return stationHeight;
	}

	public void setStationHeight(final float stationHeight) {
		this.stationHeight = stationHeight;
	}
	
	public float getStationLength() {
		return stationLength;
	}

	public void setStationLength(float stationLength) {
		this.stationLength = stationLength;
	}

	public float getStationFixtureWidth() {
		return stationFixtureWidth;
	}

	public void setStationFixtureWidth(float stationFixtureWidth) {
		this.stationFixtureWidth = stationFixtureWidth;
	}

	@Override public void startCyclus(final ProcessingDeviceStartCyclusSettings startCylusSettings, final int processId) throws AbstractCommunicationException, DeviceActionException, InterruptedException { }
	@Override public void prepareForStartCyclus(final ProcessingDeviceStartCyclusSettings startCylusSettings) throws AbstractCommunicationException, DeviceActionException { }
	@Override public void prepareForPick(final DevicePickSettings pickSettings, final int processId) throws AbstractCommunicationException, DeviceActionException, InterruptedException { }
	@Override public void prepareForPut(final DevicePutSettings putSettings, final int processId) throws AbstractCommunicationException, DeviceActionException, InterruptedException { }
	@Override public void prepareForIntervention(final DeviceInterventionSettings interventionSettings) throws AbstractCommunicationException, DeviceActionException { }
	@Override public void pickFinished(final DevicePickSettings pickSettings, final int processId) throws AbstractCommunicationException, DeviceActionException { }
	@Override public void putFinished(final DevicePutSettings putSettings) throws AbstractCommunicationException, DeviceActionException { }
	@Override public void interventionFinished(final DeviceInterventionSettings interventionSettings) throws AbstractCommunicationException, DeviceActionException { }
	@Override public void releasePiece(final DevicePickSettings pickSettings) throws AbstractCommunicationException, DeviceActionException, InterruptedException { }
	@Override public void loadDeviceSettings(final DeviceSettings deviceSettings) {
		for (Entry<SimpleWorkArea, Clamping> entry : deviceSettings.getClampings().entrySet()) {
			entry.getKey().setDefaultClamping(entry.getValue());
		}
		isShiftedOrigin = ((ReversalUnitSettings) deviceSettings).isShiftedOrigin();
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
	public Coordinates getPickLocation(final SimpleWorkArea workArea, final WorkPieceDimensions workPieceDimensions, final ClampingManner clampType, final ApproachType approachType) {
		Coordinates c = new Coordinates(workArea.getDefaultClamping().getRelativePosition());
		WorkPieceDimensions dimensions = workPieceDimensions;
		if (isWidthReversal) {
			dimensions = new WorkPieceDimensions(workPieceDimensions.getWidth(), workPieceDimensions.getLength(), workPieceDimensions.getHeight());
		}	
		c.setX(c.getX() + getXCoord(dimensions, approachType));
		c.setY(c.getY() + getYCoord(dimensions, approachType));
		c.setZ(c.getZ() + getZCoord(dimensions, approachType));
		if (isShiftedOrigin) {
			c.setX(c.getX() + addedXValue);
		}
		return c;
	}
	
	@Override
	public Coordinates getLocationOrientation(final SimpleWorkArea workArea, final ClampingManner clampType) {	
		return new Coordinates(workArea.getDefaultClamping().getRelativePosition());
	}
	
	@Override
	public Coordinates getPutLocation(final SimpleWorkArea workArea, final WorkPieceDimensions workPieceDimensions, final ClampingManner clampType, final ApproachType approachType) {
		Coordinates c = new Coordinates(workArea.getDefaultClamping().getRelativePosition());
		WorkPieceDimensions dimensions = workPieceDimensions;
		if (isWidthReversal) {
			dimensions = new WorkPieceDimensions(workPieceDimensions.getWidth(), workPieceDimensions.getLength(), workPieceDimensions.getHeight());
		}
		c.setX(c.getX() + getXCoord(dimensions, approachType));
		c.setY(c.getY() + getYCoord(dimensions, approachType));
		c.setZ(c.getZ() + getZCoord(dimensions, approachType));
		if (isShiftedOrigin) {
			c.setX(c.getX() + addedXValue);
		}
		return c;
	}
	
	private static float getXCoord(final WorkPieceDimensions workPieceDimensions, final ApproachType approachType) {
		switch (approachType) {
		case BOTTOM:
		case TOP:
		case LEFT:
			return workPieceDimensions.getWidth()/2;
		case FRONT:
			return workPieceDimensions.getHeight();
		default: 
			return 0;
		}
	}

	private static float getYCoord(final WorkPieceDimensions workPieceDimensions, final ApproachType approachType) {
		switch (approachType) {
		case BOTTOM:
		case TOP:
		case FRONT:
			return workPieceDimensions.getLength()/2;
		case LEFT:
			return 0;
		default:
			return 0;
		}
	}
	
	private static float getZCoord(final WorkPieceDimensions workPieceDimensions, final ApproachType approachType) {
		switch (approachType) {
		case BOTTOM:
		case TOP:	
			return 0;
		case FRONT:
			return workPieceDimensions.getWidth()/2;
		case LEFT:
			return workPieceDimensions.getLength()/2;
		default:
			return 0;
		}
	}
	
	@Override
	public boolean isConnected() {
		return true;
	}
	
	private void setWidthReversal() {
		this.isWidthReversal = PropertyManager.hasSettingValue(Setting.REVERSAL_WIDTH, "true");
	}
	
	@Override
	public float getZSafePlane(final WorkPieceDimensions dimensions, final SimpleWorkArea workArea, final ApproachType approachType) throws IllegalArgumentException {
		//X naar voren, Y naar rechts, Z omhoog
		switch (approachType) {
		case BOTTOM:
			float zSafePlane = workArea.getDefaultClamping().getRelativePosition().getZ(); 
			zSafePlane += ((ReversalUnit) workArea.getWorkAreaManager().getZone().getDevice()).getStationHeight();
			return (zSafePlane * -1);
		case FRONT:
		case LEFT:
			return 0;
		default:
			return super.getZSafePlane(dimensions, workArea, approachType);
		}
	}
	
	@Override
	public EDeviceGroup getType() {
		return EDeviceGroup.POST_PROCESSING;
	}

	public void addApproachType(final ApproachType approachType, final boolean isAllowed) {
		allowedApproachTypes.put(approachType, isAllowed);
	}
	
	public Map<ApproachType, Boolean> getAllowedApproachTypes() {
		return allowedApproachTypes;
	}
	
	public void setAllowedApproachTypes(Map<ApproachType, Boolean> allowedApproaches) {
		this.allowedApproachTypes = allowedApproaches;
	}
	
	public float getAddedX() {
		return addedXValue;
	}

	public void setAddedX(final float addedX) {
		this.addedXValue = addedX;
	}
	
	public float getAddedXOrigin() {
		if (isShiftedOrigin) {
			return addedXValue;
		} 
		return 0;
	}
	
	public ApproachType getFirstAllowedApproachType() {
		for (ApproachType type: allowedApproachTypes.keySet()) {
			//method is only used for pick, where TOP is not allowed since it is the default for the put
			if (allowedApproachTypes.get(type) && !type.equals(ApproachType.TOP)) {
				return type;
			}
		}
		return null;
	}
}
