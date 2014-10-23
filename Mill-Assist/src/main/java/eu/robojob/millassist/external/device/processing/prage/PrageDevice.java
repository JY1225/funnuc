package eu.robojob.millassist.external.device.processing.prage;

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
import eu.robojob.millassist.external.device.EDeviceGroup;
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
			entry.getKey().setDefaultClamping(entry.getValue());
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
	public Coordinates getPickLocation(final WorkArea workArea, final WorkPieceDimensions workPieceDimensions, final ClampingManner clampType) {
		throw new IllegalStateException("This method should never be called");
	}
	
	@Override
	public Coordinates getLocationOrientation(final WorkArea workArea, final ClampingManner clampType) {
		Coordinates c = new Coordinates(workArea.getDefaultClamping().getRelativePosition());
		if (clampType.getType() == Type.LENGTH) {
			if (clampType.isChanged()) {
				c.setR(c.getR() + clampingWidthDeltaR);
			} else {
				c.setR(c.getR());
			}
		} else {
			if (clampType.isChanged()) {
				c.setR(c.getR());
			} else {
				c.setR(c.getR() + clampingWidthDeltaR);
			}
		}
		return c;
	}
	
	@Override
	public Coordinates getPutLocation(final WorkArea workArea, final WorkPieceDimensions workPieceDimensions, final ClampingManner clampType) {
		Coordinates c = new Coordinates(workArea.getDefaultClamping().getRelativePosition());
		if (clampType.getType() == Type.LENGTH) {
			if (clampType.isChanged()) {
				c.setR(c.getR() + clampingWidthDeltaR);
			} else {
				c.setR(c.getR());
			}
			switch (workArea.getDefaultClamping().getType()) {
				case CENTRUM:
					// no action needed
					break;
				case FIXED_XM:
					c.setX(c.getX() - workPieceDimensions.getWidth()/2);
					break;
				case FIXED_YM:
					c.setY(c.getY() - workPieceDimensions.getWidth()/2);
					break;
				case FIXED_XP:
					c.setX(c.getX() + workPieceDimensions.getWidth()/2);
					break;
				case FIXED_YP:
					c.setY(c.getY() + workPieceDimensions.getWidth()/2);
					break;
				case NONE:
					throw new IllegalArgumentException("Machine clamping type can't be NONE.");
				default:
					throw new IllegalArgumentException("Unknown clamping type: " + workArea.getDefaultClamping().getType());
			}
		} else {
			if (clampType.isChanged()) {
				c.setR(c.getR());
			} else {
				c.setR(c.getR() + clampingWidthDeltaR);
			}
			switch (workArea.getDefaultClamping().getType()) {
			case CENTRUM:
				// no action needed
				break;
			case FIXED_XM:
				c.setX(c.getX() - workPieceDimensions.getLength()/2);
				break;
			case FIXED_YM:
				c.setY(c.getY() - workPieceDimensions.getLength()/2);
				break;
			case FIXED_XP:
				c.setX(c.getX() + workPieceDimensions.getLength()/2);
				break;
			case FIXED_YP:
				c.setY(c.getY() + workPieceDimensions.getLength()/2);
				break;
			case NONE:
				throw new IllegalArgumentException("Machine clamping type can't be NONE.");
			default:
				throw new IllegalArgumentException("Unknown clamping type: " + workArea.getDefaultClamping().getType());
			}
		}
		return c;
	}
	
	@Override
	public boolean isConnected() {
		return true;
	}
	
	@Override
	public EDeviceGroup getType() {
		return EDeviceGroup.PRE_PROCESSING;
	}

}
