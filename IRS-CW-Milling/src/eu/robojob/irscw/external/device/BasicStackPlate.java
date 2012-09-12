package eu.robojob.irscw.external.device;

import java.io.IOException;
import java.util.List;

import eu.robojob.irscw.positioning.Coordinates;
import eu.robojob.irscw.workpiece.WorkPieceDimensions;

// it is important this device can keep state, for other (processing) devices, no state is needed
// a workpiece is put, processed and picked, and the device-state is the same as before these actions
// for this device, state is needed to know the configuration (studs) and workpieces (finished, raw, ...) present

// for now, we asume only one layer can be placed, and the pick-locations will be re-used for putting finished workpieces
// also, this stack plate can only contain two types of workpieces, raw workpieces and finished workpieces
// also, the orientation of raw and finished workpieces is the same, and as mentioned earlier pick-locations equal put-locations
public class BasicStackPlate extends AbstractStackingDevice {

	enum WorkPieceOrientation {
		HORIZONTAL, TILTED
	}
	
	// general settings
	private int horizontalHoleAmount;
	private int verticalHoleAmount;
	private float holeDiameter;
	private float studDiameter;
	private float horizontalPadding;
	private float verticalPadding;
	private float horizontalHoleDistance;
	private float verticalHoleDistance;
	private float interferenceDistance;
	
	// specific configuration settings
	private WorkPieceOrientation workPieceOrientation;
	
	private WorkPieceDimensions rawWorkPieceDimensions;
	private int rawWorkPiecePresentAmount;
	
	private WorkPieceDimensions finishedWorkpieceDimensions;
	private int finishedWorkPiecePresentAmount;
	
	public BasicStackPlate(String id, List<Zone> zones, int horizontalHoleAmount, int verticalHoleAmount, float holeDiameter, float studDiameter,
			float horizontalPadding, float verticalPadding, float horizontalHoleDistance, float verticalHoleDistance, float interferenceDistance) {
		super(id, zones);
		this.horizontalHoleAmount = horizontalHoleAmount;
		this.verticalHoleAmount = verticalHoleAmount;
		this.holeDiameter = holeDiameter;
		this.studDiameter = studDiameter;
		this.horizontalPadding = horizontalPadding;
		this.verticalPadding = verticalPadding;
		this.horizontalHoleDistance = horizontalHoleDistance;
		this.verticalHoleDistance = verticalHoleDistance;
		this.interferenceDistance = interferenceDistance;
	}

	public void configureRawWorkpieces(WorkPieceOrientation rawWorkPieceOrientation, WorkPieceDimensions rawWorkPieceDimensions, int rawWorkPiecePresentAmount) {
		if (calculateMaxWorkPieceAmount(rawWorkPieceOrientation, rawWorkPieceDimensions) > rawWorkPiecePresentAmount) {
			throw new IllegalArgumentException("Amount of workpieces exceeds maximum!");
		}
	}
	
	public int calculateMaxWorkPieceAmount(WorkPieceOrientation workPieceOrientation, WorkPieceDimensions workPieceDimensions) {
		switch (workPieceOrientation) {
			case HORIZONTAL:
				return calculateMaxWorkPieceAmountHorizontal(workPieceOrientation, workPieceDimensions);
			case TILTED:
				return calculateMaxWorkPieceAmountTilted(workPieceOrientation, workPieceDimensions);
			default:
				throw new IllegalArgumentException("Unknown orientation");
		}
	}
	
	private int calculateMaxWorkPieceAmountHorizontal(WorkPieceOrientation workPieceOrientation, WorkPieceDimensions workPieceDimensions) {
		int amount = 0;
		
		// calculate amount of holes one piece takes (horizontally):
		int amountOfHorizontalHolesOnePiece = (int) Math.floor(workPieceDimensions.getLength()/horizontalHoleDistance);
		float horizontalRemainder = workPieceDimensions.getLength() % horizontalHoleDistance;
		if (horizontalRemainder < interferenceDistance) {
			amountOfHorizontalHolesOnePiece++;
			horizontalRemainder = horizontalHoleDistance;
		}
		int amountHorizontal = (int) Math.floor(horizontalHoleAmount / amountOfHorizontalHolesOnePiece);
		if ((horizontalHoleDistance - horizontalRemainder) > horizontalPadding) {
			amountHorizontal--;
		}
		
		// calculate amount of holes one piece takes (vertically);
		int amountOfVerticalHolesOnePiece = (int) Math.floor(workPieceDimensions.getWidth()/verticalHoleDistance);
		float verticalRemainder = workPieceDimensions.getWidth() % verticalHoleDistance;
		if (verticalRemainder < interferenceDistance) {
			amountOfVerticalHolesOnePiece++;
			verticalRemainder = verticalHoleDistance;
		}
		int amountVertical = (int) Math.floor(verticalHoleAmount / amountOfVerticalHolesOnePiece);
		if ((verticalHoleDistance - verticalRemainder) > verticalPadding) {
			amountVertical--;
		}
		
		return amountHorizontal*amountVertical;
	}
	
	private int calculateMaxWorkPieceAmountTilted(WorkPieceOrientation workPieceOrientation, WorkPieceDimensions workPieceDimensions) {
		int amount = 0;
		
		return amount;
	}

	@Override
	public boolean canPickWorkpiece() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canPutWorkpiece() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Coordinates getPickLocation(WorkArea workArea) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Coordinates getPutLocation(WorkArea workArea) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void prepareForPick(AbstractDevicePickSettings pickSettings)
			throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void prepareForPut(AbstractDevicePutSettings putSettings)
			throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void prepareForIntervention(
			AbstractDeviceInterventionSettings interventionSettings)
			throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pickFinished(AbstractDevicePickSettings pickSettings)
			throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void putFinished(AbstractDevicePutSettings putSettings)
			throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void interventionFinished(
			AbstractDeviceInterventionSettings interventionSettings)
			throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void releasePiece(AbstractDevicePickSettings pickSettings)
			throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void grabPiece(AbstractDevicePutSettings putSettings)
			throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getStatus() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}
	
	public class StackingPosition {
		private Coordinates position;
		private boolean containsWorkPiece;
		private WorkPieceOrientation orientation;
		private WorkPieceDimensions dimensions;
		
		public StackingPosition(Coordinates position, boolean containsWorkPiece, WorkPieceOrientation orientation, WorkPieceDimensions dimensions) {
			this.position = position;
			this.containsWorkPiece = containsWorkPiece;
			this.orientation = orientation;
			this.dimensions = dimensions;
		}

		public Coordinates getPosition() {
			return position;
		}

		public void setPosition(Coordinates position) {
			this.position = position;
		}

		public boolean isContainsWorkPiece() {
			return containsWorkPiece;
		}

		public void setContainsWorkPiece(boolean containsWorkPiece) {
			this.containsWorkPiece = containsWorkPiece;
		}

		public WorkPieceOrientation getOrientation() {
			return orientation;
		}

		public void setOrientation(WorkPieceOrientation orientation) {
			this.orientation = orientation;
		}

		public WorkPieceDimensions getDimensions() {
			return dimensions;
		}

		public void setDimensions(WorkPieceDimensions dimensions) {
			this.dimensions = dimensions;
		}
	
	}

}
