package eu.robojob.irscw.external.device.stacking;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import eu.robojob.irscw.external.communication.CommunicationException;
import eu.robojob.irscw.external.device.ClampingType;
import eu.robojob.irscw.external.device.DeviceType;
import eu.robojob.irscw.external.device.WorkArea;
import eu.robojob.irscw.external.device.Zone;
import eu.robojob.irscw.positioning.Coordinates;
import eu.robojob.irscw.process.ProcessFlow;
import eu.robojob.irscw.workpiece.WorkPiece;
import eu.robojob.irscw.workpiece.WorkPiece.Type;
import eu.robojob.irscw.workpiece.WorkPieceDimensions;

// it is important this device can keep state, for other (processing) devices, no state is needed
// a workpiece is put, processed and picked, and the device-state is the same as before these actions
// for this device, state is needed to know the configuration (studs) and workpieces (finished, raw, ...) present

// for now, we asume only one layer can be placed, and the pick-locations will be re-used for putting finished workpieces
// also, this stack plate can only contain two types of workpieces, raw workpieces and finished workpieces
// also, the orientation of raw and finished workpieces is the same, and as mentioned earlier pick-locations equal put-locations
public class BasicStackPlate extends AbstractStackingDevice {

	public enum WorkPieceOrientation {
		HORIZONTAL, TILTED
	}
		
	private static Logger logger = Logger.getLogger(BasicStackPlate.class);
	private BasicStackPlateLayout layout;
		
	private WorkPiece rawWorkPiece;
	private WorkPiece finishedWorkPiece;
	
	private StackingPosition currentPickLocation;
	
	public BasicStackPlate(String id, List<Zone> zones, BasicStackPlateLayout layout) {
		super(id, zones);
		this.layout = layout;
		this.rawWorkPiece = null;
		this.finishedWorkPiece = null;
		currentPickLocation = null;
	}
	
	public BasicStackPlate(String id, BasicStackPlateLayout layout) {
		this(id, new ArrayList<Zone>(), layout);
	}
	
	public void setFinishedAmount(int finishedAmount) {
		for (int i = 0; i < layout.getStackingPositions().size(); i++) {
			if (i < finishedAmount) {
				WorkPiece finishedWorkPiece = new WorkPiece(Type.FINISHED, rawWorkPiece.getDimensions());
				layout.getStackingPositions().get(i).setWorkPiece(finishedWorkPiece);
			} else {
				layout.getStackingPositions().get(i).setWorkPiece(rawWorkPiece);
			}
		}
	}

	@Override
	public synchronized boolean canPick(AbstractDevicePickSettings pickSettings) throws CommunicationException {
		for (StackingPosition stackingPos : layout.getStackingPositions()) {
			if ((stackingPos.getWorkPiece() != null) && (stackingPos.getWorkPiece().getType() == Type.RAW)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public synchronized boolean canPut(AbstractDevicePutSettings putSettings) throws CommunicationException {
		for (StackingPosition stackingPos : layout.getStackingPositions()) {
			if (stackingPos.getWorkPiece() == null) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public synchronized Coordinates getPickLocation(WorkArea workArea, ClampingType clampType) {
		for (StackingPosition stackingPos : layout.getStackingPositions()) {
			if ((stackingPos.getWorkPiece() != null)&&(stackingPos.getWorkPiece().getType() != Type.FINISHED)) {
				currentPickLocation = stackingPos;
				Coordinates c = new Coordinates(stackingPos.getPosition());
				return c;
			}
		}
		return null;
	}
	
	@Override 
	public synchronized Coordinates getLocation(WorkArea workArea, Type type, ClampingType clampType) {
		for (StackingPosition stackingPos : layout.getStackingPositions()) {
			if ((stackingPos.getWorkPiece() != null)&&(stackingPos.getWorkPiece().getType()==type)) {
				Coordinates c = new Coordinates(stackingPos.getPosition());
				return c;
			}
		}
		return null;
	}

	@Override
	public synchronized Coordinates getPutLocation(WorkArea workArea, WorkPieceDimensions workPieceDimensions, ClampingType clampType) {
		logger.info("getting put location: " + workPieceDimensions);
		finishedWorkPiece = new WorkPiece(WorkPiece.Type.FINISHED, workPieceDimensions);
		Coordinates c = new Coordinates(currentPickLocation.getPosition());
		return c;
	}

	@Override
	public void prepareForPick(AbstractDevicePickSettings pickSettings) {
	}

	@Override
	public void prepareForPut(AbstractDevicePutSettings putSettings) {
	}

	@Override
	public void prepareForIntervention(AbstractDeviceInterventionSettings interventionSettings) {
	}

	@Override
	public synchronized void pickFinished(AbstractDevicePickSettings pickSettings) {
		currentPickLocation.setWorkPiece(null);
	}

	@Override
	public synchronized void putFinished(AbstractDevicePutSettings putSettings) {
		//BasicStackPlatePutSettings spPutSettings = (BasicStackPlatePutSettings) putSettings;
		currentPickLocation.setWorkPiece(finishedWorkPiece);
		currentPickLocation = null;
	}

	@Override
	public void interventionFinished(AbstractDeviceInterventionSettings interventionSettings) {
	}

	@Override
	public void releasePiece(AbstractDevicePickSettings pickSettings) {
	}

	@Override
	public void grabPiece(AbstractDevicePutSettings putSettings) {
	}
	
	public BasicStackPlateLayout getLayout() {
		return layout;
	}
	
	public WorkPiece getRawWorkPiece() {
		return rawWorkPiece;
	}
	
	public static class BasicStackPlatePickSettings extends AbstractStackingDevicePickSettings {
		public BasicStackPlatePickSettings(WorkArea workArea) {
			super(workArea);
		}
	}
	
	public static class BasicStackPlatePutSettings extends AbstractStackingDevicePutSettings {
		public BasicStackPlatePutSettings(WorkArea workArea) {
			super(workArea);
		}

		@Override
		public boolean isPutPositionFixed() {
			return true;
		}
	}
	
	public class BasicStackPlateInterventionSettings extends AbstractStackingDeviceInterventionSettings {
		
		public BasicStackPlateInterventionSettings(WorkArea workArea) {
			super(workArea);
		}
		
	}
	
	public class BasicStackPlateSettings extends AbstractStackingDeviceSettings {

		private WorkPiece workPiece;
		private WorkPieceOrientation orientation;
		private Integer amount;
		
		public BasicStackPlateSettings(WorkPieceDimensions dimensions, WorkPieceOrientation orientation, Integer amount) {
			this(new WorkPiece(WorkPiece.Type.RAW, dimensions), orientation, amount);
		}
		
		public BasicStackPlateSettings(WorkPiece workPiece, WorkPieceOrientation orientation, Integer amount) {
			this.amount = amount;
			this.orientation = orientation;
			this.workPiece = workPiece;
		}

		public Integer getAmount() {
			return amount;
		}

		public void setDimensions(WorkPieceDimensions dimensions) {
			if (workPiece == null) {
				workPiece = new WorkPiece(Type.RAW, dimensions);
			} else {
				this.workPiece.setDimensions(dimensions);
			}
		}

		public void setOrientation(WorkPieceOrientation orientation) {
			this.orientation = orientation;
		}

		public void setAmount(Integer amount) {
			this.amount = amount;
		}
		
		public WorkPiece getWorkPiece() {
			return this.workPiece;
		}

		public WorkPieceOrientation getOrientation() {
			return orientation;
		}
	}
	
	@Override
	public DeviceType getType() {
		return DeviceType.BASIC_STACK_PLATE;
	}

	@Override
	public synchronized void loadDeviceSettings(AbstractDeviceSettings deviceSettings) {
		if (deviceSettings instanceof BasicStackPlateSettings) {
			BasicStackPlateSettings settings = (BasicStackPlateSettings) deviceSettings;
			try {
				layout.configureStackingPositions(settings.getWorkPiece(), settings.getOrientation());
				this.rawWorkPiece = settings.getWorkPiece();
				layout.placeRawWorkPieces(rawWorkPiece, settings.getAmount());
			} catch (IncorrectWorkPieceDataException e) {
				logger.error(e);
			}
		} else {
			throw new IllegalArgumentException("Unknown device settings");
		}
	}

	@Override
	public AbstractDeviceSettings getDeviceSettings() {
		return new BasicStackPlateSettings(rawWorkPiece, layout.getOrientation(), layout.getRawWorkPieceAmount());
	}

	@Override
	public boolean validatePickSettings(AbstractDevicePickSettings pickSettings) {
		// note we assume the corresponding device settings are loaded!
		BasicStackPlatePickSettings stackPlatePickSettings = (BasicStackPlatePickSettings) pickSettings;
		// the used workarea should be the one workarea configured for this device
		if ((stackPlatePickSettings != null) && (stackPlatePickSettings.getWorkArea() != null) && (stackPlatePickSettings.getWorkArea().equals(getWorkAreas().get(0))) && 
				(layout.getStackingPositions() != null) && (layout.getStackingPositions().size() > 0)) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean validatePutSettings(AbstractDevicePutSettings putSettings) {
		// note we assume the corresponding device settings are loaded!
		BasicStackPlatePutSettings stackPlatePutSettings = (BasicStackPlatePutSettings) putSettings;
		// the used workarea should be the one workarea configured for this device
		if ((stackPlatePutSettings != null) && (stackPlatePutSettings.getWorkArea() != null) && (stackPlatePutSettings.getWorkArea().equals(getWorkAreas().get(0))) && 
				(layout.getStackingPositions() != null) && (layout.getStackingPositions().size() > 0)) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean validateInterventionSettings(
			AbstractDeviceInterventionSettings interventionSettings) {
		// note we assume the corresponding device settings are loaded!
		BasicStackPlateInterventionSettings stackPlateInterventionSettings = (BasicStackPlateInterventionSettings) interventionSettings;
		// the used workarea should be the one workarea configured for this device
		if ((stackPlateInterventionSettings != null) && (stackPlateInterventionSettings.getWorkArea() != null) && (stackPlateInterventionSettings.getWorkArea().equals(getWorkAreas().get(0))) && 
				(layout.getStackingPositions() != null) && (layout.getStackingPositions().size() > 0)) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public AbstractDeviceInterventionSettings getInterventionSettings(AbstractDevicePickSettings pickSettings) {
		return new BasicStackPlateInterventionSettings(pickSettings.getWorkArea());
	}

	@Override
	public AbstractDeviceInterventionSettings getInterventionSettings(AbstractDevicePutSettings putSettings) {
		return new BasicStackPlateInterventionSettings(putSettings.getWorkArea());
	}

	@Override
	public boolean isConnected() {
		return true;
	}

	@Override
	public void stopCurrentAction() {
	}

	@Override
	public void prepareForProcess(ProcessFlow process) throws CommunicationException, InterruptedException {
		/*try {
			layout.placeRawWorkPieces(rawWorkPiece, process.getTotalAmount());
		} catch (IncorrectWorkPieceDataException e) {
			e.printStackTrace();
		}*/
	}

}
