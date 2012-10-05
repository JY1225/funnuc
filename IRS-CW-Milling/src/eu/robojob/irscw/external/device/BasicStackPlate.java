package eu.robojob.irscw.external.device;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import eu.robojob.irscw.external.device.StudPosition.StudType;
import eu.robojob.irscw.external.device.exception.IncorrectWorkPieceDataException;
import eu.robojob.irscw.positioning.Coordinates;
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
	
	private int rawWorkPieceAmount;
	
	// specific configuration settings
	private WorkPieceOrientation workPieceOrientation;
	private WorkPieceDimensions rawWorkPieceDimensions;
	private WorkPieceDimensions finishedWorkpieceDimensions;

	private List<StackingPosition> rawStackingPositions;
	private List<StackingPosition> finishedStackingPositions;
	
	private StudPosition[][] studPositions;
	
	private static Logger logger = Logger.getLogger(BasicStackPlate.class);
	
	private static final float MIN_OVERLAP_DISTANCE = 10;
	
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
		this.rawWorkPieceAmount = 0;
		this.rawWorkPieceDimensions = new WorkPieceDimensions();
		this.finishedWorkpieceDimensions = new WorkPieceDimensions();
		
		this.rawStackingPositions = new ArrayList<StackingPosition>();
		this.finishedStackingPositions = new ArrayList<StackingPosition>();
		
		setWorkPieceOrientation(WorkPieceOrientation.HORIZONTAL);
		initializeStudPositions();
	}
	
	public float getHorizontalStudLength() {
		return (float) (1.3 * horizontalHoleDistance);
	}
	
	public float getHorizontalStudWidth() {
		return (float) (0.5 * verticalHoleDistance);
	}
	
	private void initializeStudPositions() {
		this.studPositions = new StudPosition[verticalHoleAmount][horizontalHoleAmount];
		for (int i = 0; i < verticalHoleAmount; i++) {
			for (int j = 0; j < horizontalHoleAmount; j++) {
				float x = j * horizontalHoleDistance + horizontalPadding;
				float y = getWidth() - (i * verticalHoleDistance + verticalPadding);
				studPositions[i][j] = new StudPosition(x, y, StudType.NONE);
			}
		}
	}
	
	public BasicStackPlate(String id, int horizontalHoleAmount, int verticalHoleAmount, float holeDiameter, float studDiameter,
			float horizontalPadding, float verticalPadding, float horizontalHoleDistance, float interferenceDistance, float overflowPercentage) {
		this(id, new ArrayList<Zone>(), horizontalHoleAmount, verticalHoleAmount, holeDiameter, studDiameter, horizontalPadding, verticalPadding, 
				horizontalHoleDistance, interferenceDistance, overflowPercentage);
	}

	public void configureRawWorkpieces() throws IncorrectWorkPieceDataException {
		//TODO check length is always larger than width
		rawStackingPositions.clear();
		clearStudPositions();
		
		if (!((rawWorkPieceDimensions != null) && (rawWorkPieceDimensions.getWidth() > 0) && (rawWorkPieceDimensions.getLength() > 0))) {
			throw new IncorrectWorkPieceDataException();
		}
		
		switch(workPieceOrientation) {
			case HORIZONTAL:
				configureRawWorkPieceLocationsHorizontal(workPieceOrientation, rawWorkPieceDimensions, rawWorkPieceAmount);
				break;
			case TILTED:
				configureRawWorkPieceLocationsTilted(workPieceOrientation, rawWorkPieceDimensions, rawWorkPieceAmount);
				break;
			default:
				throw new IllegalArgumentException("Unknown work piece orientation");
		}
	}
	
	private void configureRawWorkPieceLocationsHorizontal(WorkPieceOrientation workPieceOrientation, WorkPieceDimensions workPieceDimensions, int rawWorkPiecePresentAmount) throws IncorrectWorkPieceDataException {
		
		// ---HORIZONTALLY---
		
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
		
		// for real small work-pieces
		if (remainingLength < 0) {
			remainingLength = 0;
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
		// we calculate the amount of space there is left:
		float remainingDistanceBetweenHoles = (horizontalHoleAmount % amountOfHorizontalStudsOnePiece - 1)*horizontalHoleDistance;
		if (remainingDistanceBetweenHoles < 0)  {
			remainingDistanceBetweenHoles = 0;
		}
		float spaceLeft = remainingDistanceBetweenHoles + horizontalPadding + overFlowPercentage*workPieceDimensions.getLength() - studDiameter/2;
		// if enough space if left (taking into account the overflowPercentage), an extra piece can be placed
		if (spaceLeft >= workPieceDimensions.getLength()) {
			amountHorizontal++;
		} else if ((spaceLeft < remainingLength) && (remainingDistanceBetweenHoles == 0)) {
			amountHorizontal--;
		}
		
		// ---VERTICALLY---
		
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
		// we calculate the amount of space there is left: 
		remainingDistanceBetweenHoles = (verticalHoleAmount % amountOfVerticalStudsOnePiece - 1)*verticalHoleDistance;
		if (remainingDistanceBetweenHoles < 0) {
			remainingDistanceBetweenHoles = 0;
		}
		spaceLeft = remainingDistanceBetweenHoles + verticalPadding + overFlowPercentage*workPieceDimensions.getWidth() - studDiameter/2;
		if (spaceLeft >= workPieceDimensions.getWidth()) {
			amountVertical++;
		} else if ((spaceLeft < remainingWidth) && (remainingDistanceBetweenHoles == 0)) {
			amountVertical--;
		}
				
		if (rawWorkPiecePresentAmount > amountHorizontal*amountVertical) {
			throw new IncorrectWorkPieceDataException();
		} else {
			initializeRawWorkPiecePositionsHorizontal(amountOfHorizontalStudsOnePiece, amountOfVerticalStudsOnePiece, amountHorizontal, amountVertical, workPieceDimensions, rawWorkPiecePresentAmount, remainingLength, remainingWidth);
			rawWorkPieceAmount = rawWorkPiecePresentAmount;
		}
	}
	

	private void initializeRawWorkPiecePositionsHorizontal(int amountOfHorizontalStudsOnePiece, int amountOfVerticalStudsOnePiece, 
			int amountHorizontal, int amountVertical, WorkPieceDimensions workPieceDimensions, int amountOfRawWorkPieces, float remainingLength, float remainingWidth) {
		rawStackingPositions.clear();
		int verticalStudIndex = 0;
		int totalPlaced = 0;
		for (int i = 0; i < amountVertical; i++) {
			// calculate vertical position
			// position is calculated using width, because of the orientation of x: right, y: down
			float verticalPos = getWidth() - (verticalStudIndex * verticalHoleDistance + studDiameter/2 + workPieceDimensions.getWidth()/2 + verticalPadding);
			int horizontalStudIndex = 0;
			for (int j = 0; j < amountHorizontal; j++) {
				float horizontalPos = horizontalStudIndex * horizontalHoleDistance + studDiameter/2 + workPieceDimensions.getLength()/2 + horizontalPadding;
				
				int leftVerticalExtraIndex = (int) Math.floor(amountOfVerticalStudsOnePiece / 2);
				int rightHorizontalExtraIndex = amountOfHorizontalStudsOnePiece - 1;
				
				if (remainingLength <= MIN_OVERLAP_DISTANCE) {
					rightHorizontalExtraIndex--;
				}
				if (remainingWidth <= MIN_OVERLAP_DISTANCE) {
					leftVerticalExtraIndex--;
				}
				
				boolean corner = false;
				
				// condition one: only two vertical studs and not enough remaining width (only one leftVerticalExtraIndex)
				// condition two: only two horizontal studs, or: only three horizontal studs and not enough remaining length (only two rightHorizontalExtraIndex)
				
				if ((rightHorizontalExtraIndex <=2) || (leftVerticalExtraIndex == 0)) {
					studPositions[verticalStudIndex][horizontalStudIndex].setStudType(StudType.HORIZONTAL_CORNER);
					corner = true;
				} else {
					studPositions[verticalStudIndex+leftVerticalExtraIndex][horizontalStudIndex].setStudType(StudType.NORMAL);
					studPositions[verticalStudIndex][horizontalStudIndex + 1].setStudType(StudType.NORMAL);
				}
				
				int horizontalPos2 = horizontalStudIndex + rightHorizontalExtraIndex;
				while(horizontalPos2 >= studPositions[0].length) {
					horizontalPos2--;
				}
				if (horizontalPos2 > horizontalStudIndex) {
					if ((!corner) || (corner && (horizontalPos2 > horizontalStudIndex + 1))) {
						studPositions[verticalStudIndex][horizontalPos2].setStudType(StudType.NORMAL);
					}
				}
				
				StackingPosition position = new StackingPosition(new Coordinates(horizontalPos, verticalPos, 0, 0, 0, 0), true, WorkPieceOrientation.HORIZONTAL, workPieceDimensions);
				rawStackingPositions.add(position);
				totalPlaced++;
				if (totalPlaced >= amountOfRawWorkPieces) {
					return;
				}
				horizontalStudIndex += amountOfHorizontalStudsOnePiece;
			}
			verticalStudIndex += amountOfVerticalStudsOnePiece;
			//TODO initialize amount of raw work pieces
		}
	}
		
	private void clearStudPositions() {
		for (StudPosition[] vertPos : studPositions) {
			for (StudPosition pos : vertPos) {
				pos.setStudType(StudType.NONE);
			}
		}
	}
	
	private void configureRawWorkPieceLocationsTilted(WorkPieceOrientation workPieceOrientation, WorkPieceDimensions workPieceDimensions, int rawWorkPiecePresentAmount) throws IncorrectWorkPieceDataException {
		
		float a = (float) (horizontalHoleDistance/2 - Math.sqrt(2)*(studDiameter/2));
		float b = (float) (studDiameter/(2*Math.sqrt(2)));
		float c = (float) (workPieceDimensions.getLength() / (Math.sqrt(2)));
		float d = (float) ((workPieceDimensions.getWidth() / (Math.sqrt(2)))  - horizontalHoleDistance/2);
		float e = (float) (horizontalHoleDistance/2 - studDiameter / (2 * Math.sqrt(2)));
		float dr = (float) ((workPieceDimensions.getLength() / (Math.sqrt(2))) - horizontalHoleDistance/2);
		
		if (workPieceDimensions.getWidth() - MIN_OVERLAP_DISTANCE < Math.sqrt(2) * (a+b)) {
			// stud left to bottom-corner can't be reached
			throw new IncorrectWorkPieceDataException("Workpiece-width is too small");
		}
		
		double temp = (2*horizontalHoleDistance)*(2 * horizontalHoleDistance) + (verticalHoleDistance*verticalHoleDistance);
		if (workPieceDimensions.getLength() - MIN_OVERLAP_DISTANCE - (a+b)/(Math.sqrt(2)) < Math.sqrt(temp)) {
			// no upper-stud can be reached
			throw new IncorrectWorkPieceDataException("Workpiece-height is too small");
		}
		
		int amountOfHorizontalStudsOnePieceLeft = 1;
		int amountOfHorizontalStudsOnePieceRight = 1;
		float remainingD = d;
		while (remainingD > horizontalHoleDistance) {
			// for each time the remaining left-distance is bigger than the horizontal hole distance, an extra stud is needed on both sides
			amountOfHorizontalStudsOnePieceLeft++;
			remainingD -= horizontalHoleDistance;
		}
		
		float remainingDr = dr;
		while (remainingDr > horizontalHoleDistance) {
			amountOfHorizontalStudsOnePieceRight++;
			remainingDr -= horizontalHoleDistance;
		}
		
		// distance to next stud
		float f = horizontalHoleDistance - remainingD;
		float fr = horizontalHoleDistance - remainingDr;
		// total workpiece width (and height)
		float totalHorizontalSize = (float) ((workPieceDimensions.getHeight() + workPieceDimensions.getLength())/Math.sqrt(2));
		
		float remainingLeft = remainingD;
		float remainingRight = remainingDr;
		
		int amountOfHorizontalStudsToTheRight = amountOfHorizontalStudsOnePieceLeft;
		
		// collision between two pieces would occur
		if (remainingLeft > horizontalHoleDistance/2 - interferenceDistance/2) {
			// so we add one to the right
			amountOfHorizontalStudsToTheRight++;
		} 
		
		// collision between a workpiece and the studs of its left-neighbour would occur
		while ( (amountOfHorizontalStudsToTheRight +1) * horizontalHoleDistance - studDiameter - interferenceDistance*(Math.sqrt(2)) - horizontalHoleDistance/2  < workPieceDimensions.getWidth() * Math.sqrt(2))  {
			amountOfHorizontalStudsToTheRight++;
		}
		
		// collision could occur between left corner and stud to the left
		//TODO: take into account the height
		if ((remainingLeft > horizontalHoleDistance/2) && (f - studDiameter/2 < interferenceDistance)) {
			amountOfHorizontalStudsOnePieceLeft++;
			remainingLeft = 0;
		}
		
		// same for right
		if ((remainingRight > horizontalHoleDistance/2) && (fr - studDiameter/2 < interferenceDistance)) {
			amountOfHorizontalStudsOnePieceRight++;
			remainingRight = 0;
		}
		
		// we calculate the amount of studs we can place
		int amountHorizontal = (int) Math.floor(horizontalHoleAmount/(amountOfHorizontalStudsOnePieceLeft + amountOfHorizontalStudsToTheRight));
		
		// check if the most right workpiece can be placed, if not, decrease amount of horizontal pieces and try again
		// TODO: for now we don't take into account overlap (assume it's always ok) (also for left, this could be applied)
		int extraStudsNeededForMostRight = amountOfHorizontalStudsOnePieceRight - amountOfHorizontalStudsToTheRight;
		if (extraStudsNeededForMostRight > 0) {
			while (amountHorizontal * (amountOfHorizontalStudsOnePieceLeft + amountOfHorizontalStudsToTheRight) + extraStudsNeededForMostRight > horizontalHoleAmount) {
				amountHorizontal--;
			}
		}
		
		// --- VERTICAL ---
		float g = (float) (workPieceDimensions.getLength()/Math.sqrt(2) + workPieceDimensions.getWidth()/Math.sqrt(2) - a);
		int amountOfVerticalStudsOnePiece = 1;
		float remainingG = g;
		while (remainingG > verticalHoleDistance) {
			remainingG -= verticalHoleDistance;
			amountOfVerticalStudsOnePiece++;
		}
		if (remainingG > verticalHoleDistance - studDiameter/2 - interferenceDistance) {
			remainingG = 0;
			amountOfVerticalStudsOnePiece++;
		}
		
		int amountVertical = (int) Math.floor(verticalHoleAmount / amountOfVerticalStudsOnePiece);
		if (remainingG > verticalPadding) {
			amountVertical--;
		}
		
		if (rawWorkPiecePresentAmount > amountHorizontal*amountVertical) {
			throw new IncorrectWorkPieceDataException("Amount of workpieces exceeds maximum");
		} else {
			initializeRawWorkPiecePostionsTilted(amountOfHorizontalStudsOnePieceLeft, amountOfHorizontalStudsToTheRight, amountOfHorizontalStudsOnePieceRight, amountOfVerticalStudsOnePiece, amountHorizontal, amountVertical, a);
		}
		
	}
	
	private void initializeRawWorkPiecePostionsTilted(int amountOfHorizontalStudsOnePieceLeft, int amountOfHorizontalStudsToTheRight, int amountOfHorizontalStudsOnePieceRight, int amountOfVerticalStudsOnePiece, 
			int amountHorizontal, int amountVertical, float a) {
		rawStackingPositions.clear();
		
		float h = (float) (horizontalHoleDistance/2 + ( rawWorkPieceDimensions.getLength()/(Math.sqrt(2)) - rawWorkPieceDimensions.getWidth()/(Math.sqrt(2)) )/2);
		float v = (float) ( (rawWorkPieceDimensions.getLength()/(Math.sqrt(2)) + rawWorkPieceDimensions.getWidth()/(Math.sqrt(2)))/2 - a);
		
		int amountOfWorkPiecesPlaced = 0;
		
		int verticalIndex = 0;
		for (int i = 0; i < amountVertical; i++) {
			
			int horizontalIndex = -1;
			for (int j = 0; j < amountHorizontal; j++) {
				horizontalIndex += amountOfHorizontalStudsOnePieceLeft;
				
				float x = horizontalPadding + (horizontalIndex * horizontalHoleDistance) + h;
				float y = getWidth() - (verticalPadding + (verticalIndex * verticalHoleDistance) + v);
				
				rawStackingPositions.add(new StackingPosition(new Coordinates(x, y, 0, 0, 0, 0), true, WorkPieceOrientation.TILTED, rawWorkPieceDimensions));
				amountOfWorkPiecesPlaced++;
				
				studPositions[verticalIndex][horizontalIndex].setStudType(StudType.NORMAL);
				studPositions[verticalIndex][horizontalIndex+1].setStudType(StudType.NORMAL);
				
				int extraRight = amountOfHorizontalStudsOnePieceRight - 1;
				while (extraRight + horizontalIndex > horizontalHoleAmount) {
					extraRight--;
				}
				
				if (extraRight % 2 != 0) {
					extraRight--;
				}
				
				int extraTop = extraRight / 2;
				
				if (extraRight < 2) {
					throw new IllegalStateException("This can't be possible! Wrong calculations: " + extraRight);
				} else {
					studPositions[verticalIndex + extraTop][horizontalIndex + 1 +extraRight].setStudType(StudType.NORMAL);
				}
				
				if (amountOfWorkPiecesPlaced >= rawWorkPieceAmount) {
					return;
				}
				
				horizontalIndex += amountOfHorizontalStudsToTheRight;
			}
			
			verticalIndex += amountOfVerticalStudsOnePiece;
		}
		
	}

	public StudPosition[][] getStudPositions() {
		return studPositions;
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

	public int getHorizontalHoleAmount() {
		return horizontalHoleAmount;
	}

	public int getVerticalHoleAmount() {
		return verticalHoleAmount;
	}

	public float getHoleDiameter() {
		return holeDiameter;
	}

	public float getStudDiameter() {
		return studDiameter;
	}

	public float getHorizontalPadding() {
		return horizontalPadding;
	}

	public float getVerticalPadding() {
		return verticalPadding;
	}

	public float getHorizontalHoleDistance() {
		return horizontalHoleDistance;
	}

	public float getVerticalHoleDistance() {
		return verticalHoleDistance;
	}

	public WorkPieceDimensions getRawWorkPieceDimensions() {
		return rawWorkPieceDimensions;
	}

	public void setRawWorkPieceDimensions(WorkPieceDimensions rawWorkPieceDimensions) {
		this.rawWorkPieceDimensions = rawWorkPieceDimensions;
	}

	public WorkPieceDimensions getFinishedWorkpieceDimensions() {
		return finishedWorkpieceDimensions;
	}

	public void setFinishedWorkpieceDimensions(
			WorkPieceDimensions finishedWorkpieceDimensions) {
		this.finishedWorkpieceDimensions = finishedWorkpieceDimensions;
	}

	public List<StackingPosition> getRawStackingPositions() {
		return rawStackingPositions;
	}

	public List<StackingPosition> getFinishedStackingPositions() {
		return finishedStackingPositions;
	}

	public float getWidth() {
		return verticalPadding*2 + (verticalHoleAmount - 1) * verticalHoleDistance;
	}
	
	public float getLength() {
		return horizontalPadding*2 + (horizontalHoleAmount - 1) * horizontalHoleDistance;
	}

	public int getRawWorkPieceAmount() {
		return rawWorkPieceAmount;
	}

	public void setRawWorkPieceAmount(int rawWorkPieceAmount) {
		this.rawWorkPieceAmount = rawWorkPieceAmount;
	}

	public WorkPieceOrientation getWorkPieceOrientation() {
		return workPieceOrientation;
	}

	public void setWorkPieceOrientation(WorkPieceOrientation workPieceOrientation) {
		this.workPieceOrientation = workPieceOrientation;
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

		private WorkPieceDimensions dimensions;
		private WorkPieceOrientation orientation;
		private int amount;
		
		public BasicStackPlateSettings(WorkPieceDimensions dimensions, WorkPieceOrientation orientation, int amount) {
			this.dimensions = dimensions;
			this.orientation = orientation;
			this.amount = amount;
		}
		
		public WorkPieceDimensions getDimensions() {
			return dimensions;
		}

		public WorkPieceOrientation getOrientation() {
			return orientation;
		}

		public int getAmount() {
			return amount;
		}

		public void setDimensions(WorkPieceDimensions dimensions) {
			this.dimensions = dimensions;
		}

		public void setOrientation(WorkPieceOrientation orientation) {
			this.orientation = orientation;
		}

		public void setAmount(int amount) {
			this.amount = amount;
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
			this.rawWorkPieceDimensions = settings.getDimensions();
			this.rawWorkPieceAmount = settings.getAmount();
			this.workPieceOrientation = settings.getOrientation();
			try {
				configureRawWorkpieces();
			} catch (IncorrectWorkPieceDataException e) {
			}
		} else {
			throw new IllegalArgumentException("Unknown device settings");
		}
	}

	@Override
	public AbstractDeviceSettings getDeviceSettings() {
		return new BasicStackPlateSettings(rawWorkPieceDimensions, workPieceOrientation, rawWorkPieceAmount);
	}

	@Override
	public boolean validatePickSettings(AbstractDevicePickSettings pickSettings) {
		// note we assume the corresponding device settings are loaded!
		BasicStackPlatePickSettings stackPlatePickSettings = (BasicStackPlatePickSettings) pickSettings;
		// the used workarea should be the one workarea configured for this device
		if ((stackPlatePickSettings != null) && (stackPlatePickSettings.getWorkArea() != null) && (stackPlatePickSettings.getWorkArea().equals(getWorkAreas().get(0))) && 
				(getRawStackingPositions() != null) && (getRawStackingPositions().size() > 0)) {
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
				(getRawStackingPositions() != null) && (getRawStackingPositions().size() > 0)) {
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
				(getRawStackingPositions() != null) && (getRawStackingPositions().size() > 0)) {
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
	
}
