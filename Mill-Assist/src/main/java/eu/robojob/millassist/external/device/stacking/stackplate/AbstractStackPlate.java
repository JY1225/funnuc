package eu.robojob.millassist.external.device.stacking.stackplate;

import java.util.Set;
import java.util.Map.Entry;

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
import eu.robojob.millassist.external.device.WorkArea;
import eu.robojob.millassist.external.device.Zone;
import eu.robojob.millassist.external.device.stacking.AbstractStackingDevice;
import eu.robojob.millassist.external.device.stacking.IncorrectWorkPieceDataException;
import eu.robojob.millassist.external.device.stacking.stackplate.gridplate.GridPlateLayout;
import eu.robojob.millassist.positioning.Coordinates;
import eu.robojob.millassist.process.ProcessFlow;
import eu.robojob.millassist.workpiece.WorkPiece.Material;
import eu.robojob.millassist.workpiece.WorkPiece.Type;
import eu.robojob.millassist.workpiece.WorkPiece;
import eu.robojob.millassist.workpiece.WorkPieceDimensions;

public abstract class AbstractStackPlate extends AbstractStackingDevice {

	public enum WorkPieceOrientation {
		
		HORIZONTAL {
			@Override
			public int getDegrees() {
				return 0;
			}
		}, TILTED {

			@Override
			public int getDegrees() {
				return 45;
			}
		}, DEG90 {

			@Override
			public int getDegrees() {
				return 90;
			}
		};
		
		public abstract int getDegrees();
	}
	
	private AbstractStackPlateLayout layout;
	private StackPlateStackingPosition currentPickLocation;
	private StackPlateStackingPosition currentPutLocation;
	
	private static Logger logger = LogManager.getLogger(AbstractStackPlate.class.getName());
	
	public AbstractStackPlate(String name, Set<Zone> zones) {
		super(name, zones);
		this.currentPickLocation = null;
		this.currentPutLocation = null;
	}

	public AbstractStackPlateLayout getLayout() {
		return this.layout;
	}
	
	public void setLayout(AbstractStackPlateLayout layout) {
		this.layout = layout;
		layout.setStackPlate(this);
	}
	
	@Override 
	public synchronized Coordinates getLocation(final WorkArea workArea, final Type type, final ClampingManner clampType) {
		for (StackPlateStackingPosition stackingPos : getLayout().getStackingPositions()) {
			if ((stackingPos.getWorkPiece() != null) && (stackingPos.getWorkPiece().getType() == type) && 
					(stackingPos.getAmount() > 0)) {
				Coordinates c = new Coordinates(stackingPos.getPickPosition());
				c.offset(workArea.getActiveClamping().getRelativePosition());
				return c;
			}
		}
		return null;
	}

	@Override
	public void clearDeviceSettings() {
		setRawWorkPiece(new WorkPiece(WorkPiece.Type.RAW, new WorkPieceDimensions(), Material.OTHER, 0.0f));
		setFinishedWorkPiece(new WorkPiece(WorkPiece.Type.FINISHED, new WorkPieceDimensions(), Material.OTHER, 0.0f));
		getWorkAreas().get(0).getActiveClamping().resetHeightToDefault();
		this.currentPickLocation = null;
		this.currentPutLocation = null;
		try {
			this.getLayout().configureStackingPositions(null, getLayout().getOrientation(), 1);
		} catch (IncorrectWorkPieceDataException e) {
			logger.error(e);
		}
		notifyLayoutChanged();
	}

	@Override
	public void prepareForProcess(ProcessFlow process)
			throws AbstractCommunicationException, InterruptedException {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean canPick(DevicePickSettings pickSettings) throws AbstractCommunicationException, DeviceActionException {
		for (StackPlateStackingPosition stackingPos : getLayout().getStackingPositions()) {
			if ((stackingPos.getWorkPiece() != null) && (stackingPos.getWorkPiece().getType() == Type.RAW)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean canPut(DevicePutSettings putSettings) throws AbstractCommunicationException, DeviceActionException, InterruptedException {
		for (StackPlateStackingPosition stackingPos : getLayout().getStackingPositions()) {
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
	public void prepareForPick(DevicePickSettings pickSettings)
			throws AbstractCommunicationException, DeviceActionException,
			InterruptedException {
		// TODO Auto-generated method stub

	}

	@Override
	public void prepareForPut(DevicePutSettings putSettings)
			throws AbstractCommunicationException, DeviceActionException,
			InterruptedException {
		// TODO Auto-generated method stub

	}

	@Override
	public void prepareForIntervention(
			DeviceInterventionSettings interventionSettings)
			throws AbstractCommunicationException, DeviceActionException,
			InterruptedException {
		// TODO Auto-generated method stub

	}

	@Override
	public synchronized void pickFinished(final DevicePickSettings pickSettings) {
		currentPickLocation.setAmount(currentPickLocation.getAmount() - 1);
		if (currentPickLocation.getAmount() == 0) {
			currentPickLocation.setWorkPiece(null);
		}
		currentPickLocation = null;
		notifyLayoutChanged();
		logger.info("pick finished!!");
	}

	@Override
	public synchronized void putFinished(final DevicePutSettings putSettings) {
		currentPutLocation.setWorkPiece(getFinishedWorkPiece());
		currentPutLocation.setAmount(currentPutLocation.getAmount() + 1);
		currentPutLocation = null;
		notifyLayoutChanged();
		logger.info("put finished!");
	}

	@Override
	public void interventionFinished(
			DeviceInterventionSettings interventionSettings)
			throws AbstractCommunicationException, DeviceActionException,
			InterruptedException {
		// TODO Auto-generated method stub

	}

	@Override
	public void releasePiece(DevicePickSettings pickSettings)
			throws AbstractCommunicationException, DeviceActionException,
			InterruptedException {
		// TODO Auto-generated method stub

	}

	@Override
	public void grabPiece(DevicePutSettings putSettings)
			throws AbstractCommunicationException, DeviceActionException,
			InterruptedException {
		// TODO Auto-generated method stub

	}

	@Override
	public void reset() throws AbstractCommunicationException,
			DeviceActionException, InterruptedException {
		// TODO Auto-generated method stub

	}

	@Override
	public synchronized void loadDeviceSettings(final DeviceSettings deviceSettings) {
		for (Entry<WorkArea, Clamping> entry : deviceSettings.getClampings().entrySet()) {
			entry.getKey().setActiveClamping(entry.getValue());
		}
		if (deviceSettings instanceof AbstractStackPlateDeviceSettings) {
			AbstractStackPlateDeviceSettings settings = (AbstractStackPlateDeviceSettings) deviceSettings;
			resetCurrentPickLocation();
			resetCurrentPutLocation();
			getWorkAreas().get(0).getActiveClamping().setHeight(settings.getStudHeight());
			try {
				if (settings.getRawWorkPiece() != null) {
					setRawWorkPiece(settings.getRawWorkPiece());
					setFinishedWorkPiece(settings.getFinishedWorkPiece());
					getLayout().configureStackingPositions(settings.getRawWorkPiece(), settings.getOrientation(), settings.getLayers());
					getLayout().initRawWorkPieces(getRawWorkPiece(), settings.getAmount());
				} else {
					logger.info("Raw workpiece was null!");
					setRawWorkPiece(settings.getRawWorkPiece());
					setFinishedWorkPiece(settings.getFinishedWorkPiece());
					getLayout().configureStackingPositions(null, settings.getOrientation(), settings.getLayers());
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
	public AbstractStackPlateDeviceSettings getDeviceSettings() {
		int gridId = 0;
		if(layout instanceof GridPlateLayout) {
			gridId = ((GridPlateLayout) layout).getId();
		}
		return new AbstractStackPlateDeviceSettings(getRawWorkPiece(), getFinishedWorkPiece(), getLayout().getOrientation(), getLayout().getLayers(), getLayout().getWorkPieceAmount(WorkPiece.Type.RAW), getWorkAreas().get(0).getActiveClamping().getHeight(), gridId);
	}

	@Override
	public synchronized Coordinates getPickLocation(final WorkArea workArea, final WorkPieceDimensions workPieceDimensions, final ClampingManner clampType) {
		for (StackPlateStackingPosition stackingPos : getLayout().getStackingPositions()) {
			if ((stackingPos.getWorkPiece() != null) && (stackingPos.getWorkPiece().getType() == Type.RAW) && 
					(stackingPos.getAmount() > 0)) {
				currentPickLocation = stackingPos;
				Coordinates c = new Coordinates(stackingPos.getPickPosition());
				c.offset(workArea.getActiveClamping().getRelativePosition());
				return c;
			}
		}
		return null;
	}
	
	@Override
	public synchronized Coordinates getPutLocation(final WorkArea workArea, final WorkPieceDimensions workPieceDimensions, final ClampingManner clampType) {
		for (StackPlateStackingPosition stackingPos : getLayout().getStackingPositions()) {
			if ((stackingPos.getWorkPiece() == null) || ((stackingPos.getWorkPiece() != null) && 
					(stackingPos.getWorkPiece().getType() == Type.FINISHED) && (stackingPos.getAmount() < getLayout().getLayers()))) {
				currentPutLocation = stackingPos;
				Coordinates c = new Coordinates(stackingPos.getPutPosition());
				c.offset(workArea.getActiveClamping().getRelativePosition());
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
	
	protected synchronized void resetCurrentPickLocation() {
		this.currentPickLocation = null;
	}

	protected synchronized void resetCurrentPutLocation() {
		this.currentPutLocation = null;
	}

	@Override
	public Coordinates getLocationOrientation(final WorkArea workArea, final ClampingManner clampType) {
		Coordinates c = new Coordinates(getLayout().getStackingPositions().get(0).getPosition());
		c.offset(workArea.getActiveClamping().getRelativePosition());
		c.setX(0);
		c.setY(0);
		c.setZ(0);
		return c;
	}

	@Override
	public void interruptCurrentAction() {
		// TODO Auto-generated method stub

	}
	
	@Override
	public boolean validatePickSettings(final DevicePickSettings pickSettings) {
		// note we assume the corresponding device settings are loaded!
		if ((pickSettings != null) && (pickSettings.getWorkArea() != null) && (getWorkAreaNames().contains(pickSettings.getWorkArea().getName())) 
				&& (getLayout().getStackingPositions() != null) && (getLayout().getStackingPositions().size() > 0)) {
			return true;
		}
		return false;
	}

	@Override
	public boolean validatePutSettings(final DevicePutSettings putSettings) {
		// note we assume the corresponding device settings are loaded!
		if ((putSettings != null) && (putSettings.getWorkArea() != null) && (getWorkAreaNames().contains(putSettings.getWorkArea().getName())) 
				&& (getLayout().getStackingPositions() != null) && (getLayout().getStackingPositions().size() > 0)) {
			return true;
		}
		return false;
	}

	@Override
	public boolean validateInterventionSettings(final DeviceInterventionSettings interventionSettings) {
		// note we assume the corresponding device settings are loaded!
		if ((interventionSettings != null) && (interventionSettings.getWorkArea() != null) && (getWorkAreaNames().contains(interventionSettings.getWorkArea().getName())) 
				&& (getLayout().getStackingPositions() != null) && (getLayout().getStackingPositions().size() > 0)) {
			return true;
		}
		return false;
	}

	@Override
	public boolean isConnected() {
		// TODO Auto-generated method stub
		return false;
	}
	
	public abstract void notifyLayoutChanged();
	
	public void placeFinishedWorkPieces(final int finishedAmount) {
		int placedAmount = 0;
		int nbLayers = getLayout().getLayers();
		int position = 0;
		int replacedAmount = 0;
		while(placedAmount < finishedAmount) {
			StackPlateStackingPosition stPos = getLayout().getStackingPositions().get(position);
			if(!stPos.hasWorkPiece()) {
				WorkPiece finishedWorkPiece = new WorkPiece(Type.FINISHED, getRawWorkPiece().getDimensions(), null, Float.NaN);
				stPos.setWorkPiece(finishedWorkPiece);
				while(stPos.getAmount() < nbLayers && placedAmount < finishedAmount) {
					stPos.incrementAmount();
					placedAmount++;
				}
			} else if (stPos.getWorkPiece().getType().equals(WorkPiece.Type.RAW)) {
				WorkPiece finishedWorkPiece = new WorkPiece(Type.FINISHED, getRawWorkPiece().getDimensions(), null, Float.NaN);
				stPos.setWorkPiece(finishedWorkPiece);
				replacedAmount += stPos.getAmount();
				stPos.setAmount(0);
				while(stPos.getAmount() < nbLayers && placedAmount < finishedAmount) {
					stPos.incrementAmount();
					placedAmount++;
				} 
			} else if (stPos.getWorkPiece().getType().equals(WorkPiece.Type.FINISHED)) {
				while(stPos.getAmount() < nbLayers && placedAmount < finishedAmount) {
					stPos.incrementAmount();
					placedAmount++;
				}
			}
			position++;
		}
		decreaseAmountOfFirstRawPieces(placedAmount - replacedAmount);
		notifyLayoutChanged();
	}
	
	private void decreaseAmountOfFirstRawPieces(final int amount) {
		int position = getLayout().getFirstRawPosition();
		getLayout().getStackingPositions().get(position).decrementAmountBy(amount);
	}
	
	public abstract float getR(WorkPieceOrientation orientation);

}
