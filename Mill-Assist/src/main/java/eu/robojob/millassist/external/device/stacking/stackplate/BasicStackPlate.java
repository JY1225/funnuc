package eu.robojob.millassist.external.device.stacking.stackplate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
import eu.robojob.millassist.external.device.stacking.IncorrectWorkPieceDataException;
import eu.robojob.millassist.positioning.Coordinates;
import eu.robojob.millassist.process.ProcessFlow;
import eu.robojob.millassist.workpiece.WorkPiece;
import eu.robojob.millassist.workpiece.WorkPiece.Material;
import eu.robojob.millassist.workpiece.WorkPiece.Type;
import eu.robojob.millassist.workpiece.WorkPieceDimensions;

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
	private List<BasicStackPlateListener> listeners;
	
	private StackPlateStackingPosition currentPickLocation;
	private StackPlateStackingPosition currentPutLocation;
	
	public BasicStackPlate(final String name, final Set<Zone> zones, final BasicStackPlateLayout layout) {
		super(name, zones);
		this.layout = layout;
		this.rawWorkPiece = new WorkPiece(Type.RAW, new WorkPieceDimensions(), Material.OTHER, 0.0f);
		this.currentPickLocation = null;
		this.currentPutLocation = null;
		this.listeners = new ArrayList<BasicStackPlateListener>();
	}
	
	public BasicStackPlate(final String name, final BasicStackPlateLayout layout) {
		this(name, new HashSet<Zone>(), layout);
	}

	@Override
	public synchronized boolean canPick(final DevicePickSettings pickSettings) throws AbstractCommunicationException {
		for (StackPlateStackingPosition stackingPos : layout.getStackingPositions()) {
			if ((stackingPos.getWorkPiece() != null) && (stackingPos.getWorkPiece().getType() == Type.RAW)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public synchronized boolean canPut(final DevicePutSettings putSettings) throws AbstractCommunicationException {
		for (StackPlateStackingPosition stackingPos : layout.getStackingPositions()) {
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
	public Coordinates getLocationOrientation(final WorkArea workArea) {
		Coordinates c = new Coordinates(layout.getStackingPositions().get(0).getPosition());
		c.setX(0);
		c.setY(0);
		c.setZ(0);
		return c;
	}
	
	@Override
	public synchronized Coordinates getPickLocation(final WorkArea workArea, final ClampingManner clampType) {
		for (StackPlateStackingPosition stackingPos : layout.getStackingPositions()) {
			if ((stackingPos.getWorkPiece() != null) && (stackingPos.getWorkPiece().getType() == Type.RAW) && 
					(stackingPos.getAmount() > 0)) {
				currentPickLocation = stackingPos;
				Coordinates c = new Coordinates(stackingPos.getPickPosition());
				return c;
			}
		}
		return null;
	}
	
	@Override 
	public synchronized Coordinates getLocation(final WorkArea workArea, final Type type, final ClampingManner clampType) {
		for (StackPlateStackingPosition stackingPos : layout.getStackingPositions()) {
			if ((stackingPos.getWorkPiece() != null) && (stackingPos.getWorkPiece().getType() == type) && 
					(stackingPos.getAmount() > 0)) {
				Coordinates c = new Coordinates(stackingPos.getPickPosition());
				return c;
			}
		}
		return null;
	}

	@Override
	public synchronized Coordinates getPutLocation(final WorkArea workArea, final WorkPieceDimensions workPieceDimensions, final ClampingManner clampType) {
		for (StackPlateStackingPosition stackingPos : layout.getStackingPositions()) {
			if ((stackingPos.getWorkPiece() == null) || ((stackingPos.getWorkPiece() != null) && 
					(stackingPos.getWorkPiece().getType() == Type.FINISHED) && (stackingPos.getAmount() < layout.getLayers()))) {
				currentPutLocation = stackingPos;
				Coordinates c = new Coordinates(stackingPos.getPutPosition());
				return c;
			}
		}
		return null;
	}
	
	public synchronized StackPlateStackingPosition getCurrentPickLocation() {
		return currentPickLocation;
	}

	public synchronized StackPlateStackingPosition getCurrentPutLocation() {
		return currentPutLocation;
	}
	
	@Override public void prepareForPick(final DevicePickSettings pickSettings) { }
	@Override public void prepareForPut(final DevicePutSettings putSettings) { }
	@Override public void prepareForIntervention(final DeviceInterventionSettings interventionSettings) { }
	@Override public void interventionFinished(final DeviceInterventionSettings interventionSettings) { }
	@Override public void releasePiece(final DevicePickSettings pickSettings) { }
	@Override public void grabPiece(final DevicePutSettings putSettings) { }
	@Override public void interruptCurrentAction() { }
	@Override public void reset() throws AbstractCommunicationException, DeviceActionException, InterruptedException { }

	@Override
	public synchronized void pickFinished(final DevicePickSettings pickSettings) {
		currentPickLocation.setAmount(currentPickLocation.getAmount() - 1);
		if (currentPickLocation.getAmount() == 0) {
			currentPickLocation.setWorkPiece(null);
		}
		currentPickLocation = null;
		for (BasicStackPlateListener listener : listeners) {
			listener.layoutChanged();
		}
		logger.info("pick finished!!");
	}

	@Override
	public synchronized void putFinished(final DevicePutSettings putSettings) {
		currentPutLocation.setWorkPiece(finishedWorkPiece);
		currentPutLocation.setAmount(currentPutLocation.getAmount() + 1);
		currentPutLocation = null;
		for (BasicStackPlateListener listener : listeners) {
			listener.layoutChanged();
		}
		logger.info("put finished!");
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
			this.currentPickLocation = null;
			this.currentPutLocation = null;
			try {
				if (settings.getRawWorkPiece() != null) {
					this.rawWorkPiece = settings.getRawWorkPiece();
					this.finishedWorkPiece = settings.getFinishedWorkPiece();
					layout.configureStackingPositions(settings.getRawWorkPiece(), settings.getOrientation(), settings.getLayers());
					layout.placeRawWorkPieces(rawWorkPiece, settings.getAmount());
				} else {
					logger.info("Raw workpiece was null!");
					this.rawWorkPiece = settings.getRawWorkPiece();
					this.finishedWorkPiece = settings.getFinishedWorkPiece();
					layout.configureStackingPositions(null, settings.getOrientation(), settings.getLayers());
				}
			} catch (IncorrectWorkPieceDataException e) {
				logger.error(e);
			}
		} else {
			throw new IllegalArgumentException("Unknown device settings");
		}
		notifyLayoutChanged();
	}

	@Override
	public DeviceSettings getDeviceSettings() {
		return new BasicStackPlateSettings(rawWorkPiece, finishedWorkPiece, layout.getOrientation(), layout.getLayers(), layout.getRawWorkPieceAmount());
	}
	
	@Override
	public void clearDeviceSettings() {
		this.rawWorkPiece = new WorkPiece(WorkPiece.Type.RAW, new WorkPieceDimensions(), Material.OTHER, 0.0f);
		this.finishedWorkPiece = new WorkPiece(WorkPiece.Type.FINISHED, new WorkPieceDimensions(), Material.OTHER, 0.0f);
		this.currentPickLocation = null;
		this.currentPutLocation = null;
		try {
			this.layout.configureStackingPositions(null, layout.getOrientation(), 1);
		} catch (IncorrectWorkPieceDataException e) {
			logger.error(e);
		}
		notifyLayoutChanged();
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
	
	public WorkPiece getFinishedWorkPiece() {
		return finishedWorkPiece;
	}

	public void placeFinishedWorkPieces(final int finishedAmount) {
		for (int i = 0; i < layout.getStackingPositions().size(); i++) {
			if (i < finishedAmount) {
				//TODO improve, dimensions finished workpiece are not always the same as raw workpiece dimensions
				WorkPiece finishedWorkPiece = new WorkPiece(Type.FINISHED, rawWorkPiece.getDimensions(), null, Float.NaN);
				layout.getStackingPositions().get(i).setWorkPiece(finishedWorkPiece);
			} 
		}
		notifyLayoutChanged();
	}
	
	public void notifyLayoutChanged() {
		for (BasicStackPlateListener listener : listeners) {
			listener.layoutChanged();
		}
	}
	
	public void addListener(final BasicStackPlateListener listener) {
		this.listeners.add(listener);
	}
	
	public void removeListener(final BasicStackPlateListener listener) {
		this.listeners.remove(listener);
	}
	
	public void clearListeners() {
		this.listeners.clear();
	}
	
	public int getFinishedWorkPiecesToRefillAmount() {
		int amount = 0;
		for (StackPlateStackingPosition location : layout.getStackingPositions()) {
			if ((location.getWorkPiece() != null) && (location.getWorkPiece().getType().equals(WorkPiece.Type.FINISHED))) {
				amount = amount + location.getAmount();
			}
		}
		return amount;
	}
	
	public synchronized void replaceFinishedWorkPieces(final int amount) throws IncorrectWorkPieceDataException {
		if (amount > getFinishedWorkPiecesToRefillAmount()) {
			throw new IncorrectWorkPieceDataException(IncorrectWorkPieceDataException.INCORRECT_AMOUNT);
		} else {
			int readyAmount = 0;
			for (int i = 0; i < layout.getStackingPositions().size(); i++) {
				StackPlateStackingPosition location = layout.getStackingPositions().get(i);
				if ((i == 0) && (layout.getLayers() > 1)) {
					location.setWorkPiece(null);
					location.setAmount(0);
				} else if ((location.getWorkPiece() == null) || ((location.getWorkPiece() != null) && (location.getWorkPiece().getType().equals(WorkPiece.Type.FINISHED)))) {
					location.setWorkPiece(new WorkPiece(Type.RAW, rawWorkPiece.getDimensions(), null, Float.NaN));
					int addedAmount = layout.getLayers();
					if (readyAmount + addedAmount > amount) {
						location.setAmount(amount - readyAmount);
					} else {
						location.setAmount(addedAmount);
					}
					readyAmount = readyAmount + addedAmount;
				} else if ((location.getWorkPiece() != null) && (location.getWorkPiece().getType().equals(WorkPiece.Type.RAW))) {
					int addedAmount = layout.getLayers() - location.getAmount();
					if (readyAmount + addedAmount > amount) {
						location.setAmount(location.getAmount() + amount - readyAmount);
					} else {
						location.setAmount(location.getAmount() + addedAmount);
					}
				} 
				if (readyAmount >= amount) {
					break;
				}
			}
		}
		notifyLayoutChanged();
	}

}
