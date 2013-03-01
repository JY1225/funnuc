package eu.robojob.irscw.external.device.stacking;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.robojob.irscw.external.communication.AbstractCommunicationException;
import eu.robojob.irscw.external.device.Clamping;
import eu.robojob.irscw.external.device.ClampingManner;
import eu.robojob.irscw.external.device.DeviceActionException;
import eu.robojob.irscw.external.device.DeviceInterventionSettings;
import eu.robojob.irscw.external.device.DevicePickSettings;
import eu.robojob.irscw.external.device.DevicePutSettings;
import eu.robojob.irscw.external.device.DeviceSettings;
import eu.robojob.irscw.external.device.DeviceType;
import eu.robojob.irscw.external.device.WorkArea;
import eu.robojob.irscw.external.device.Zone;
import eu.robojob.irscw.positioning.Coordinates;
import eu.robojob.irscw.process.ProcessFlow;
import eu.robojob.irscw.workpiece.WorkPiece;
import eu.robojob.irscw.workpiece.WorkPiece.Type;
import eu.robojob.irscw.workpiece.WorkPieceDimensions;

/**
 * This device needs to maintain state (position contents change during process-execution). 
 * Basic Stack Plate: one layer, can contain Raw of Finished workPieces with same orientation and dimensions
 */
public class BasicStackPlate extends AbstractStackingDevice {

	public static final float STUD_HEIGHT = 30;
	
	public enum WorkPieceOrientation {
		HORIZONTAL, TILTED
	}
		
	private static Logger logger = LogManager.getLogger(BasicStackPlate.class.getName());
	private BasicStackPlateLayout layout;	
	private WorkPiece rawWorkPiece;
	private WorkPiece finishedWorkPiece;
	
	private List<StackingPosition> currentPickLocations;
	
	public BasicStackPlate(final String name, final Set<Zone> zones, final BasicStackPlateLayout layout) {
		super(name, zones);
		this.layout = layout;
		this.rawWorkPiece = new WorkPiece(Type.RAW, new WorkPieceDimensions());
		this.currentPickLocations = new ArrayList<StackingPosition>();
	}
	
	public BasicStackPlate(final String name, final BasicStackPlateLayout layout) {
		this(name, new HashSet<Zone>(), layout);
	}

	@Override
	public synchronized boolean canPick(final DevicePickSettings pickSettings) throws AbstractCommunicationException {
		for (StackingPosition stackingPos : layout.getStackingPositions()) {
			if ((stackingPos.getWorkPiece() != null) && (stackingPos.getWorkPiece().getType() == Type.RAW)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public synchronized boolean canPut(final DevicePutSettings putSettings) throws AbstractCommunicationException {
		for (StackingPosition stackingPos : layout.getStackingPositions()) {
			if (stackingPos.getWorkPiece() == null) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean canIntervention(final DeviceInterventionSettings interventionSettings) throws AbstractCommunicationException, DeviceActionException {
		return true;
	}
	
	@Override
	public synchronized Coordinates getPickLocation(final WorkArea workArea, final ClampingManner clampType) {
		for (StackingPosition stackingPos : layout.getStackingPositions()) {
			if ((stackingPos.getWorkPiece() != null) && (stackingPos.getWorkPiece().getType() == Type.RAW)) {
				currentPickLocations.add(stackingPos);
				Coordinates c = new Coordinates(stackingPos.getPosition());
				return c;
			}
		}
		return null;
	}
	
	@Override 
	public synchronized Coordinates getLocation(final WorkArea workArea, final Type type, final ClampingManner clampType) {
		for (StackingPosition stackingPos : layout.getStackingPositions()) {
			if ((stackingPos.getWorkPiece() != null) && (stackingPos.getWorkPiece().getType() == type)) {
				Coordinates c = new Coordinates(stackingPos.getPosition());
				return c;
			}
		}
		return null;
	}

	@Override
	public synchronized Coordinates getPutLocation(final WorkArea workArea, final WorkPieceDimensions workPieceDimensions, final ClampingManner clampType) {
		finishedWorkPiece = new WorkPiece(WorkPiece.Type.FINISHED, workPieceDimensions);
		Coordinates c = new Coordinates(currentPickLocations.get(0).getPosition());
		return c;
	}

	@Override public void prepareForPick(final DevicePickSettings pickSettings) { }
	@Override public void prepareForPut(final DevicePutSettings putSettings) { }
	@Override public void prepareForIntervention(final DeviceInterventionSettings interventionSettings) { }
	@Override public void interventionFinished(final DeviceInterventionSettings interventionSettings) { }
	@Override public void releasePiece(final DevicePickSettings pickSettings) { }
	@Override public void grabPiece(final DevicePutSettings putSettings) { }
	@Override public void interruptCurrentAction() { }

	@Override
	public synchronized void pickFinished(final DevicePickSettings pickSettings) {
		currentPickLocations.get(currentPickLocations.size() - 1).setWorkPiece(null);
	}

	@Override
	public synchronized void putFinished(final DevicePutSettings putSettings) {
		currentPickLocations.remove(0).setWorkPiece(finishedWorkPiece);
	}
	
	@Override
	public DeviceType getType() {
		return DeviceType.BASIC_STACK_PLATE;
	}

	@Override
	public synchronized void loadDeviceSettings(final DeviceSettings deviceSettings) {
		for (Entry<WorkArea, Clamping> entry : deviceSettings.getClampings().entrySet()) {
			entry.getKey().setActiveClamping(entry.getValue());
		}
		if (deviceSettings instanceof BasicStackPlateSettings) {
			BasicStackPlateSettings settings = (BasicStackPlateSettings) deviceSettings;
			try {
				if (settings.getRawWorkPiece() != null) {
					layout.configureStackingPositions(settings.getRawWorkPiece(), settings.getOrientation());
					this.rawWorkPiece = settings.getRawWorkPiece();
					this.finishedWorkPiece = settings.getFinishedWorkPiece();
					layout.placeRawWorkPieces(rawWorkPiece, settings.getAmount());
				} else {
					layout.configureStackingPositions(null, settings.getOrientation());
					this.rawWorkPiece = settings.getRawWorkPiece();
					this.finishedWorkPiece = settings.getFinishedWorkPiece();
				}
			} catch (IncorrectWorkPieceDataException e) {
				logger.error(e);
			}
		} else {
			throw new IllegalArgumentException("Unknown device settings");
		}
	}

	@Override
	public DeviceSettings getDeviceSettings() {
		return new BasicStackPlateSettings(rawWorkPiece, finishedWorkPiece, layout.getOrientation(), layout.getRawWorkPieceAmount());
	}
	
	public void clearDeviceSettings() {
		this.rawWorkPiece = new WorkPiece(WorkPiece.Type.RAW, new WorkPieceDimensions());
		this.finishedWorkPiece = new WorkPiece(WorkPiece.Type.FINISHED, new WorkPieceDimensions());;
		try {
			this.layout.configureStackingPositions(null, layout.getOrientation());
		} catch (IncorrectWorkPieceDataException e) {
			logger.error(e);
		}
	}
	
	@Override
	public boolean validatePickSettings(final DevicePickSettings pickSettings) {
		// note we assume the corresponding device settings are loaded!
		if ((pickSettings != null) && (pickSettings.getWorkArea() != null) && (getWorkAreaNames().contains(pickSettings.getWorkArea().getName())) 
				&& (layout.getStackingPositions() != null) && (layout.getStackingPositions().size() > 0)) {
			return true;
		}
		return false;
	}

	@Override
	public boolean validatePutSettings(final DevicePutSettings putSettings) {
		// note we assume the corresponding device settings are loaded!
		if ((putSettings != null) && (putSettings.getWorkArea() != null) && (getWorkAreaNames().contains(putSettings.getWorkArea().getName())) 
				&& (layout.getStackingPositions() != null) && (layout.getStackingPositions().size() > 0)) {
			return true;
		}
		return false;
	}

	@Override
	public boolean validateInterventionSettings(final DeviceInterventionSettings interventionSettings) {
		// note we assume the corresponding device settings are loaded!
		if ((interventionSettings != null) && (interventionSettings.getWorkArea() != null) && (getWorkAreaNames().contains(interventionSettings.getWorkArea().getName())) 
				&& (layout.getStackingPositions() != null) && (layout.getStackingPositions().size() > 0)) {
			return true;
		}
		return false;
	}

	@Override
	public boolean isConnected() {
		return true;
	}

	@Override
	public void prepareForProcess(final ProcessFlow process) throws AbstractCommunicationException, InterruptedException { }
	
	public BasicStackPlateLayout getLayout() {
		return layout;
	}
	
	public WorkPiece getRawWorkPiece() {
		return rawWorkPiece;
	}

	public void placeFinishedWorkPieces(final int finishedAmount) {
		for (int i = 0; i < layout.getStackingPositions().size(); i++) {
			if (i < finishedAmount) {
				//TODO improve, dimensions finished workpiece are not always the same as raw workpiece dimensions
				WorkPiece finishedWorkPiece = new WorkPiece(Type.FINISHED, rawWorkPiece.getDimensions());
				layout.getStackingPositions().get(i).setWorkPiece(finishedWorkPiece);
			} 
		}
	}
}
