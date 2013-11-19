package eu.robojob.millassist.external.device.stacking.bin;

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
import eu.robojob.millassist.external.device.DeviceType;
import eu.robojob.millassist.external.device.WorkArea;
import eu.robojob.millassist.external.device.Zone;
import eu.robojob.millassist.external.device.stacking.AbstractStackingDevice;
import eu.robojob.millassist.positioning.Coordinates;
import eu.robojob.millassist.process.ProcessFlow;
import eu.robojob.millassist.workpiece.WorkPiece.Type;
import eu.robojob.millassist.workpiece.WorkPieceDimensions;

public class OutputBin extends AbstractStackingDevice {
	
	public OutputBin(final String name) {
		super(name);
		// set first clamping as active one!
		for (WorkArea wa : getWorkAreas()) {
			wa.setActiveClamping(wa.getClampings().iterator().next());
		}
	}

	public OutputBin(final String name, final Set<Zone> zones) {
		super(name, zones);
		// set first clamping as active one!
		for (WorkArea wa : getWorkAreas()) {
			wa.setActiveClamping(wa.getClampings().iterator().next());
		}
	}

	@Override
	public Coordinates getLocation(final WorkArea workArea, final Type type, final ClampingManner clampType) throws DeviceActionException, InterruptedException {
		return workArea.getActiveClamping().getRelativePosition();
	}
	
	@Override
	public void clearDeviceSettings() {		
	}

	@Override
	public void prepareForProcess(final ProcessFlow process) throws AbstractCommunicationException, InterruptedException {
		// no action needed
	}

	@Override
	public boolean canPick(final DevicePickSettings pickSettings) throws AbstractCommunicationException, DeviceActionException {
		// pick is not possible
		return false;
	}

	@Override
	public boolean canPut(final DevicePutSettings putSettings) throws AbstractCommunicationException, DeviceActionException, InterruptedException {
		// put is always possible
		return true;
	}

	@Override
	public boolean canIntervention(final DeviceInterventionSettings interventionSettings) throws AbstractCommunicationException, DeviceActionException {
		// intervention is always possible
		return true;
	}

	@Override
	public void prepareForPick(final DevicePickSettings pickSettings) throws AbstractCommunicationException, DeviceActionException, InterruptedException {		
		// pick is not possible
		throw new IllegalStateException("Pick is not possible");
	}

	@Override
	public void prepareForPut(final DevicePutSettings putSettings) throws AbstractCommunicationException, DeviceActionException, InterruptedException {
		// put is always possible
	}

	@Override
	public void prepareForIntervention(final DeviceInterventionSettings interventionSettings) throws AbstractCommunicationException, DeviceActionException,
			InterruptedException {
		// intervention is always possible
	}

	@Override
	public void pickFinished(final DevicePickSettings pickSettings) throws AbstractCommunicationException, DeviceActionException, InterruptedException {
		// no action needed
	}

	@Override
	public void putFinished(final DevicePutSettings putSettings) throws AbstractCommunicationException, DeviceActionException, InterruptedException {		
		// no action needed
	}

	@Override
	public void interventionFinished(final DeviceInterventionSettings interventionSettings) throws AbstractCommunicationException, DeviceActionException, InterruptedException {		
		// no action needed
	}

	@Override
	public void releasePiece(final DevicePickSettings pickSettings) throws AbstractCommunicationException, DeviceActionException, InterruptedException {		
		// no action needed
	}

	@Override
	public void grabPiece(final DevicePutSettings putSettings) throws AbstractCommunicationException, DeviceActionException, InterruptedException {		
		// no action needed
	}

	@Override
	public void reset() throws AbstractCommunicationException, DeviceActionException, InterruptedException {		
		// no action needed
	}

	@Override
	public void loadDeviceSettings(final DeviceSettings deviceSettings) {		
		for (Entry<WorkArea, Clamping> entry : deviceSettings.getClampings().entrySet()) {
			entry.getKey().setActiveClamping(entry.getValue());
		}
	}

	@Override
	public DeviceSettings getDeviceSettings() {
		Map<WorkArea, Clamping> clampings = new HashMap<WorkArea, Clamping>();
		for (WorkArea wa : getWorkAreas()) {
			clampings.put(wa, wa.getActiveClamping());
		}
		return new DeviceSettings(clampings);
	}

	@Override
	public Coordinates getPickLocation(final WorkArea workArea, final ClampingManner clampType) {
		throw new IllegalStateException("Pick from this device is not possible.");
	}

	@Override
	public Coordinates getPutLocation(final WorkArea workArea, final WorkPieceDimensions workPieceDimensions, final ClampingManner clampType) {
		return workArea.getActiveClamping().getRelativePosition();
	}

	@Override
	public Coordinates getLocationOrientation(final WorkArea workArea) {
		return workArea.getActiveClamping().getRelativePosition();
	}

	@Override
	public void interruptCurrentAction() {		
		// no action needed
	}

	@Override
	public boolean isConnected() {
		return true;
	}
	
	@Override
	public DeviceType getType() {
		return DeviceType.OUTPUT_BIN;
	}

}
