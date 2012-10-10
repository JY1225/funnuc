package eu.robojob.irscw.external.device;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import eu.robojob.irscw.external.device.exception.IncorrectWorkPieceDataException;
import eu.robojob.irscw.positioning.Coordinates;
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
	
	public BasicStackPlate(String id, List<Zone> zones, BasicStackPlateLayout layout) {
		super(id, zones);
		this.layout = layout;
		this.rawWorkPiece = null;
	}
	
	public BasicStackPlate(String id, BasicStackPlateLayout layout) {
		this(id, new ArrayList<Zone>(), layout);
	}

	@Override
	public boolean canPickWorkpiece() {
		logger.debug("basic stack plate can pick workpiece called");
		return false;
	}

	@Override
	public boolean canPutWorkpiece() {
		logger.debug("basic stack plate can put workpiece called");
		return false;
	}
	
	@Override
	public Coordinates getPickLocation(WorkArea workArea) {
		logger.debug("basic stack plate get pick location called");
		return null;
	}

	@Override
	public Coordinates getPutLocation(WorkArea workArea, WorkPieceDimensions workPieceDimensions) {
		logger.debug("basic stack plate get put location called");
		return null;
	}

	@Override
	public void prepareForPick(AbstractDevicePickSettings pickSettings) throws IOException {
		logger.debug("basic stack plate prepare for pick called");
	}

	@Override
	public void prepareForPut(AbstractDevicePutSettings putSettings) throws IOException {
		logger.debug("basic stack plate prepare for put called");
	}

	@Override
	public void prepareForIntervention(AbstractDeviceInterventionSettings interventionSettings) throws IOException {
		logger.debug("basic stack plate prepare for intervention called");
	}

	@Override
	public void pickFinished(AbstractDevicePickSettings pickSettings) throws IOException {
		logger.debug("basic stack plate pick finished called");
	}

	@Override
	public void putFinished(AbstractDevicePutSettings putSettings) throws IOException {
		logger.debug("basic stack plate put finished called");
	}

	@Override
	public void interventionFinished(AbstractDeviceInterventionSettings interventionSettings) throws IOException {
		logger.debug("basic stack plate intervention finished called");
	}

	@Override
	public void releasePiece(AbstractDevicePickSettings pickSettings) throws IOException {
		logger.debug("basic stack plate release piece called");
	}

	@Override
	public void grabPiece(AbstractDevicePutSettings putSettings) throws IOException {
		logger.debug("grab piece");
	}

	@Override
	public String getStatus() throws IOException {
		logger.debug("basic stack plate get status called");
		return null;
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
		private int amount;
		
		public BasicStackPlateSettings(WorkPieceDimensions dimensions, WorkPieceOrientation orientation, int amount) {
			this(new WorkPiece(WorkPiece.Type.RAW, dimensions), orientation, amount);
		}
		
		public BasicStackPlateSettings(WorkPiece workPiece, WorkPieceOrientation orientation, int amount) {
			this.amount = amount;
			this.orientation = orientation;
			this.workPiece = workPiece;
		}

		public int getAmount() {
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

		public void setAmount(int amount) {
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
	public void loadDeviceSettings(AbstractDeviceSettings deviceSettings) {
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
	
}
