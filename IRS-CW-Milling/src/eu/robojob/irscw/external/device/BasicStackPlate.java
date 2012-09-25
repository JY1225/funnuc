package eu.robojob.irscw.external.device;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

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
	private float overFlowPercentage;
	
	// specific configuration settings
	private WorkPieceOrientation workPieceOrientation;
	
	private WorkPieceDimensions rawWorkPieceDimensions;
	private int rawWorkPiecePresentAmount;
	
	private WorkPieceDimensions finishedWorkpieceDimensions;
	private int finishedWorkPiecePresentAmount;
	
	private static Logger logger = Logger.getLogger(BasicStackPlate.class);
	
	public BasicStackPlate(String id, List<Zone> zones, int horizontalHoleAmount, int verticalHoleAmount, float holeDiameter, float studDiameter,
			float horizontalPadding, float verticalPadding, float horizontalHoleDistance, float interferenceDistance, float overflowPercentage) {
		super(id, zones);
		this.horizontalHoleAmount = horizontalHoleAmount;
		this.verticalHoleAmount = verticalHoleAmount;
		this.holeDiameter = holeDiameter;
		this.studDiameter = studDiameter;
		this.horizontalPadding = horizontalPadding;
		this.verticalPadding = verticalPadding;
		this.horizontalHoleDistance = horizontalHoleDistance;
		// the calculations in this class expect the vertical distance to be twice the horizontal distance
		this.verticalHoleDistance = 2*horizontalHoleDistance;
		this.interferenceDistance = interferenceDistance;
		this.overFlowPercentage = overflowPercentage;
		if (overflowPercentage < 0 || overflowPercentage > 1) {
			throw new IllegalArgumentException("Wrong percentage value");
		}
	}
	
	public BasicStackPlate(String id, int horizontalHoleAmount, int verticalHoleAmount, float holeDiameter, float studDiameter,
			float horizontalPadding, float verticalPadding, float horizontalHoleDistance, float interferenceDistance, float overflowPercentage) {
		this(id, new ArrayList<Zone>(), horizontalHoleAmount, verticalHoleAmount, holeDiameter, studDiameter, horizontalPadding, verticalPadding, 
				horizontalHoleDistance, interferenceDistance, overflowPercentage);
	}

	public void configureRawWorkpieces(WorkPieceOrientation rawWorkPieceOrientation, WorkPieceDimensions rawWorkPieceDimensions, int rawWorkPiecePresentAmount) {
		//TODO check length is always larger than width
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
		
		if (workPieceDimensions.getLength() < (horizontalHoleDistance - studDiameter/2)) {
			throw new IllegalStateException("Workpiece-length is too small!");
		}
		
		// calculate amount of holes one piece takes (horizontally):
		float remainingLength = workPieceDimensions.getLength();
		// initially two studs are used
		int amountOfHorizontalStudsOnePiece = 2;
		// the piece is moved studDiameter/2 from the center of its left-most stud
		// the distance between the left-most side and the center of the second stud is removed from the remaining length
		remainingLength -= (horizontalHoleDistance - studDiameter/2);
		// for each time the horizontal hole distance fits in the remaining length the amount of horizontal studs is incremented
		while (remainingLength > horizontalHoleDistance) {
			remainingLength -= horizontalHoleDistance;
			amountOfHorizontalStudsOnePiece++;
		}
		// the remaining distance is the space between the next stud and the end of the piece
		float remainingDistance = horizontalHoleDistance - remainingLength;
		if (remainingDistance - studDiameter/2 < interferenceDistance) {
			remainingLength = 0;
			amountOfHorizontalStudsOnePiece++;
		}
		
		// how many times will this fit
		int amountHorizontal = (int) Math.floor(horizontalHoleAmount / amountOfHorizontalStudsOnePiece);
		// special condition for the last piece
		if (remainingLength - horizontalPadding - (horizontalHoleAmount / amountOfHorizontalStudsOnePiece - 1)*horizontalHoleDistance > 0) {
			amountHorizontal--;
		}
		
		int amountOfVerticalStudsOnePiece = 1;
		float remainingWidth = workPieceDimensions.getWidth();
		while (remainingWidth > verticalHoleDistance) {
			remainingWidth -= verticalHoleDistance;
			amountOfVerticalStudsOnePiece ++;
		}
		remainingDistance = verticalHoleDistance - remainingWidth;
		// note: whole studDiameter here, because we measure from top of studs (see documentation)
		if (remainingDistance - studDiameter < interferenceDistance) {
			amountOfVerticalStudsOnePiece++;
			remainingWidth = 0;
		}
		
		// how many times will this fit
		int amountVertical = (int) Math.floor(verticalHoleAmount / amountOfVerticalStudsOnePiece);
		// special condition for the last piece
		if (remainingWidth - verticalPadding  - (verticalHoleAmount / amountOfVerticalStudsOnePiece - 1)*verticalHoleDistance  > 0) {
			amountVertical--;
		}
				
		return amountHorizontal*amountVertical;
	}
	
	private int calculateMaxWorkPieceAmountTilted(WorkPieceOrientation workPieceOrientation, WorkPieceDimensions workPieceDimensions) {
		
		float hvdr = 0.5f;
		float a = (float) (horizontalHoleDistance/2 - Math.sqrt(2)*(studDiameter/2));
		float b = (float) (studDiameter/(2*Math.sqrt(2)));
		float c = (float) (workPieceDimensions.getLength() / (Math.sqrt(2)));
		float d = (float) ((workPieceDimensions.getWidth() / (Math.sqrt(2)))  - horizontalHoleDistance/2);
		float e = (float) (horizontalHoleDistance/2 - studDiameter / (2 * Math.sqrt(2)));
		
		if (workPieceDimensions.getWidth() < Math.sqrt(2) * (a+b)) {
			throw new IllegalArgumentException("Workpiece-width is too small");
		}
		float tempVar = (verticalHoleDistance * verticalHoleDistance) + (((1/hvdr)*(horizontalHoleDistance))*((1/hvdr)*(horizontalHoleDistance)));
		if (workPieceDimensions.getLength() < Math.sqrt(2) * (a+b) + Math.sqrt(tempVar)) {
			throw new IllegalArgumentException("Workpiece-height is too small");
		}
		
		int amountOfHorizontalStudsOnePieceLeft = 1;
		int amountOfHorizontalStudsOnePieceRight = 1;
		float remainingD = d;
		while (remainingD > horizontalHoleDistance) {
			amountOfHorizontalStudsOnePieceLeft++;
			amountOfHorizontalStudsOnePieceRight++;
			remainingD -= horizontalHoleDistance;
		}
		
		float f = horizontalHoleDistance - remainingD;
		float totalHorizontalSize = (float) ((workPieceDimensions.getHeight() + workPieceDimensions.getLength()) + Math.sqrt(2));
		if ((remainingD - studDiameter/2 - horizontalPadding)/totalHorizontalSize > overFlowPercentage) {
			amountOfHorizontalStudsOnePieceLeft++;
		} else {
			if (remainingD < horizontalHoleDistance/2) {
				amountOfHorizontalStudsOnePieceRight++;
			} else {
				if (remainingD > horizontalHoleDistance/2) {
					amountOfHorizontalStudsOnePieceRight++;
				}
			}
		}
		if ((remainingD > horizontalHoleDistance/2) && (2 * Math.sqrt(2)*f < interferenceDistance)) {
			amountOfHorizontalStudsOnePieceRight++;
		}
		
		int amountHorizontal = (int) Math.floor(horizontalHoleAmount/(amountOfHorizontalStudsOnePieceLeft + amountOfHorizontalStudsOnePieceRight));
		float lengthRight = (float) (workPieceDimensions.getLength() / (Math.sqrt(2)) - horizontalHoleDistance/2);
		lengthRight -= horizontalHoleDistance * (amountOfHorizontalStudsOnePieceRight - 1);
		if ((lengthRight - horizontalPadding - (horizontalHoleAmount % (amountOfHorizontalStudsOnePieceLeft + amountOfHorizontalStudsOnePieceRight) - 1)*horizontalHoleDistance) > 0) {
			amountHorizontal--;
		}
		
		float g = (float) (workPieceDimensions.getLength()/Math.sqrt(2) + workPieceDimensions.getWidth()/Math.sqrt(2));
		int amountOfVerticalStudsOnePiece = 1;
		float remainingG = g;
		while (remainingG > verticalHoleDistance) {
			remainingG -= verticalHoleDistance;
			amountOfVerticalStudsOnePiece++;
		}
		float y = (float) (horizontalHoleDistance/(2 * Math.sqrt(2)) - Math.sqrt(2) * studDiameter/2 - studDiameter/2);
		if (y < 0) {
			y = 0;
		}
		if (remainingG > (verticalHoleDistance - studDiameter - y)) {
			amountOfVerticalStudsOnePiece++;
		}
		
		int amountVertical = (int) Math.floor(verticalHoleAmount / amountOfVerticalStudsOnePiece);
		if (remainingG  - (verticalHoleAmount % amountOfVerticalStudsOnePiece)* verticalHoleDistance > verticalPadding) {
			amountVertical--;
		}
		return amountVertical * amountHorizontal;
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
