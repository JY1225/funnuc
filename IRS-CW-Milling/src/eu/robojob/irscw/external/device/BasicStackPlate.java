package eu.robojob.irscw.external.device;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import eu.robojob.irscw.external.device.StudPosition.StudType;
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
	private WorkPieceDimensions finishedWorkpieceDimensions;

	private List<StackingPosition> rawStackingPositions;
	private List<StackingPosition> finishedStackingPositions;
	
	private StudPosition[][] studPositions;
	
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
		this.rawStackingPositions = new ArrayList<StackingPosition>();
		this.finishedStackingPositions = new ArrayList<StackingPosition>();
		initializeStudPositions();
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

	public void configureRawWorkpieces(WorkPieceOrientation rawWorkPieceOrientation, WorkPieceDimensions rawWorkPieceDimensions, int rawWorkPiecePresentAmount) {
		//TODO check length is always larger than width
		switch(rawWorkPieceOrientation) {
			case HORIZONTAL:
				configureRawWorkPieceLocationsHorizontal(rawWorkPieceOrientation, rawWorkPieceDimensions, rawWorkPiecePresentAmount);
				break;
			case TILTED:
				configureRawWorkPieceLocationsTilted(rawWorkPieceOrientation, rawWorkPieceDimensions, rawWorkPiecePresentAmount);
				break;
			default:
				throw new IllegalArgumentException("Unknown work piece orientation");
		}
	}
	
	private void configureRawWorkPieceLocationsHorizontal(WorkPieceOrientation workPieceOrientation, WorkPieceDimensions workPieceDimensions, int rawWorkPiecePresentAmount) {
		
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
			throw new IllegalArgumentException("Amount of workpieces exceeds maximum");
		} else {
			initializeRawWorkPiecePositionsHorizontal(amountOfHorizontalStudsOnePiece, amountOfVerticalStudsOnePiece, amountHorizontal, amountVertical, workPieceDimensions, rawWorkPiecePresentAmount, remainingLength, remainingWidth);
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
				
				int leftK = (int) Math.floor(amountOfVerticalStudsOnePiece / 2);
				int rightK = amountOfHorizontalStudsOnePiece - 1;
				if (remainingLength <= 0) {
					rightK--;
				}
				if (rightK < 2) {
					throw new IllegalStateException("Illegal right k value");
				}
				studPositions[verticalStudIndex+leftK][horizontalStudIndex].setStudType(StudType.NORMAL);
				studPositions[verticalStudIndex][horizontalStudIndex + 1].setStudType(StudType.NORMAL);
				int horizontalPos2 = horizontalStudIndex + rightK;
				while(horizontalPos2 >= studPositions[0].length) {
					horizontalPos2--;
				}
				studPositions[verticalStudIndex][horizontalPos2].setStudType(StudType.NORMAL);
				
				StackingPosition position = new StackingPosition(new Coordinates(horizontalPos, verticalPos, 0, 0, 0, 0), true, WorkPieceOrientation.HORIZONTAL, workPieceDimensions);
				rawStackingPositions.add(position);
				totalPlaced++;
				if (totalPlaced >= amountOfRawWorkPieces) {
					return;
				}
				horizontalStudIndex += amountOfHorizontalStudsOnePiece;
			}
			verticalStudIndex += amountOfVerticalStudsOnePiece;
		}
	}
		
	
	private void configureRawWorkPieceLocationsTilted(WorkPieceOrientation workPieceOrientation, WorkPieceDimensions workPieceDimensions, int rawWorkPiecePresentAmount) {
		
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
		
		if (rawWorkPiecePresentAmount > amountHorizontal*amountVertical) {
			throw new IllegalArgumentException("Amount of workpieces exceeds maximum");
		}
		
	}
	
	private void initializeRawWorkPiecePostionsTilted(int amountOfHorizontalStudsOnePiece, int amountOfVerticalStudsOnePiece, 
			int amountHorizontal, int amountVertical, WorkPieceDimensions workPieceDimensions, int amountOfRawWorkPieces) {
		//TODO implement here
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

	public static class BasicStackPlatePickSettings extends AbstractStackingDevicePickSettings {
		public BasicStackPlatePickSettings(WorkArea workArea, Clamping clamping) {
			super(workArea, clamping);
		}
	}
	
	public static class BasicStackPlatePutSettings extends AbstractStackingDevicePutSettings {
		public BasicStackPlatePutSettings(WorkArea workArea, Clamping clamping) {
			super(workArea, clamping);
		}
	}
	
	@Override
	public DeviceType getType() {
		return DeviceType.BASIC_STACK_PLATE;
	}
}
