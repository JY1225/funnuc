package eu.robojob.millassist.external.device.stacking.conveyor;

import java.util.HashSet;
import java.util.Set;
import java.util.Map.Entry;

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
import eu.robojob.millassist.external.device.stacking.stackplate.BasicStackPlateSettings;
import eu.robojob.millassist.positioning.Coordinates;
import eu.robojob.millassist.process.ProcessFlow;
import eu.robojob.millassist.workpiece.WorkPiece;
import eu.robojob.millassist.workpiece.WorkPiece.Type;
import eu.robojob.millassist.workpiece.WorkPieceDimensions;

public class Conveyor extends AbstractStackingDevice {
	
	private WorkPiece rawWorkPiece;
	private WorkPiece finishedWorkPiece;
	
	private WorkArea rawWorkArea;
	private WorkArea finishedWorkArea;
	
	private int lastFinishedWorkPieceIndex;
	
	private float nomSpeed1;
	private float nomSpeed2;
	
	private int amount;
	
	private ConveyorLayout layout;
	
	public Conveyor(final String name, final Set<Zone> zones, final WorkArea rawWorkArea, final WorkArea finishedWorkArea, 
			final ConveyorLayout layout, final float nomSpeed1, final float nomSpeed2) {
		super(name, zones);
		this.rawWorkArea = rawWorkArea;
		this.finishedWorkArea = finishedWorkArea;
		this.lastFinishedWorkPieceIndex = 0;
		this.layout = layout;
		this.nomSpeed1 = nomSpeed1;
		this.nomSpeed2 = nomSpeed2;
	}
	
	public Conveyor(final String name, final WorkArea rawWorkArea, final WorkArea finishedWorkArea, final ConveyorLayout layout, 
			final float nomSpeed1, final float nomSpeed2) {
		this(name, new HashSet<Zone>(), rawWorkArea, finishedWorkArea, layout, nomSpeed1, nomSpeed2);
	}	
	
	public ConveyorLayout getLayout() {
		return layout;
	}
	
	@Override
	public void prepareForProcess(final ProcessFlow process) throws AbstractCommunicationException, InterruptedException {
	}

	@Override
	public boolean canPick(final DevicePickSettings pickSettings) throws AbstractCommunicationException, DeviceActionException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canPut(final DevicePutSettings putSettings) throws AbstractCommunicationException, DeviceActionException,
			InterruptedException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canIntervention(final DeviceInterventionSettings interventionSettings) throws AbstractCommunicationException, DeviceActionException {
		return true;
	}

	@Override
	public void prepareForPick(final DevicePickSettings pickSettings) throws AbstractCommunicationException, DeviceActionException,
			InterruptedException {
		// obtain interlock when a workpiece is ready
	}

	@Override
	public void prepareForPut(final DevicePutSettings putSettings) throws AbstractCommunicationException, DeviceActionException, InterruptedException {
		// no action needed
	}

	@Override public void prepareForIntervention(final DeviceInterventionSettings interventionSettings) throws AbstractCommunicationException, DeviceActionException, InterruptedException {
		// obtain interlock
	}

	@Override
	public void pickFinished(final DevicePickSettings pickSettings) throws AbstractCommunicationException, DeviceActionException,
			InterruptedException {
		// release interlock
	}

	@Override
	public void putFinished(final DevicePutSettings putSettings) throws AbstractCommunicationException, DeviceActionException,
			InterruptedException {
		// if last piece: do shift
	}

	@Override
	public void interventionFinished(final DeviceInterventionSettings interventionSettings) throws AbstractCommunicationException, DeviceActionException {
		// release interlock
	}

	@Override public void releasePiece(final DevicePickSettings pickSettings) throws AbstractCommunicationException, DeviceActionException, InterruptedException { }
	@Override public void grabPiece(final DevicePutSettings putSettings) throws AbstractCommunicationException, DeviceActionException, InterruptedException { }

	@Override
	public void loadDeviceSettings(final DeviceSettings deviceSettings) {
		for (Entry<WorkArea, Clamping> entry : deviceSettings.getClampings().entrySet()) {
			entry.getKey().setActiveClamping(entry.getValue());
		}
		if (deviceSettings instanceof ConveyorSettings) {
			ConveyorSettings settings = (ConveyorSettings) deviceSettings;
			if (settings.getRawWorkPiece() != null) {
				this.rawWorkPiece = settings.getRawWorkPiece();
			}
			if (settings.getFinishedWorkPiece() != null) {
				this.finishedWorkPiece = settings.getFinishedWorkPiece();
			}
			this.amount = settings.getAmount();
		}
	}

	@Override
	public DeviceSettings getDeviceSettings() {
		return new ConveyorSettings(rawWorkPiece, finishedWorkPiece, amount);
	}

	@Override
	public Coordinates getPickLocation(final WorkArea workArea, final ClampingManner clampType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Coordinates getPutLocation(final WorkArea workArea, final WorkPieceDimensions workPieceDimensions, 
			final ClampingManner clampType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Coordinates getLocation(final WorkArea workArea, final Type type, final ClampingManner clampType) {
		//TODO implement
		return null;
	}

	@Override
	public void interruptCurrentAction() {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean isConnected() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public DeviceType getType() {
		return DeviceType.CONVEYOR;
	}
	
}
