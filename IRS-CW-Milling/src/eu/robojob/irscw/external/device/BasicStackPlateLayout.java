package eu.robojob.irscw.external.device;

import java.util.ArrayList;
import java.util.List;

import eu.robojob.irscw.external.device.BasicStackPlate.WorkPieceOrientation;
import eu.robojob.irscw.external.device.StudPosition.StudType;
import eu.robojob.irscw.external.device.exception.IncorrectWorkPieceDataException;
import eu.robojob.irscw.workpiece.WorkPiece;
import eu.robojob.irscw.workpiece.WorkPieceDimensions;
import eu.robojob.irscw.workpiece.WorkPiece.Type;

public class BasicStackPlateLayout {
	
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
	
	private StudPosition[][] studPositions;
	
	private WorkPieceOrientation orientation;
	
	private List<StackingPosition> stackingPositions;
	
	private static final float MIN_OVERLAP_DISTANCE = 10;
		
	public BasicStackPlateLayout(int horizontalHoleAmount, int verticalHoleAmount, float holeDiameter, float studDiameter, float horizontalPadding,
			float verticalPadding, float horizontalHoleDistance, float interferenceDistance, float overflowPercentage) {
		this.horizontalHoleAmount = horizontalHoleAmount;
		this.verticalHoleAmount = verticalHoleAmount;
		this.holeDiameter = holeDiameter;
		this.studDiameter = studDiameter;
		this.horizontalPadding = horizontalPadding;
		this.verticalPadding = verticalPadding;
		this.horizontalHoleDistance = horizontalHoleDistance;
		this.verticalHoleDistance = 2*horizontalHoleDistance;
		this.interferenceDistance = interferenceDistance;
		this.overFlowPercentage = overflowPercentage;
		// initialize stud positions
		this.studPositions = new StudPosition[verticalHoleAmount][horizontalHoleAmount];
		for (int i = 0; i < verticalHoleAmount; i++) {
			for (int j = 0; j < horizontalHoleAmount; j++) {
				float x = j * horizontalHoleDistance + horizontalPadding;
				float y = getWidth() - (i * verticalHoleDistance + verticalPadding);
				studPositions[i][j] = new StudPosition(j, i, x, y, StudType.NONE);
			}
		}
		this.stackingPositions = new ArrayList<StackingPosition>();
		this.orientation = WorkPieceOrientation.HORIZONTAL;
	}
	
	public float getWidth() {
		return verticalPadding*2 + (verticalHoleAmount - 1) * verticalHoleDistance;
	}
	
	public float getLength() {
		return horizontalPadding*2 + (horizontalHoleAmount - 1) * horizontalHoleDistance;
	}
	
	private void clearStuds() {
		for (StudPosition[] vertPos : studPositions) {
			for (StudPosition pos : vertPos) {
				pos.setStudType(StudType.NONE);
			}
		}
	}

	public float getHorizontalStudLength() {
		return (float) (1.3 * horizontalHoleDistance);
	}
	
	public float getHorizontalStudWidth() {
		return (float) (0.5 * verticalHoleDistance);
	}
	
	/**
	 * Configures the list of Stacking-positions and updates the 2D-array of studPositions 
	 * @param dimensions
	 * @param orientation
	 * @throws IncorrectWorkPieceDataException 
	 */
	public void configureStackingPositions(WorkPiece rawWorkPiece, WorkPieceOrientation orientation) throws IncorrectWorkPieceDataException {
		stackingPositions.clear();
		clearStuds();
		
		// Todo: add upper limits
		WorkPieceDimensions dimensions = rawWorkPiece.getDimensions();
		if (!((dimensions != null) && (dimensions.getWidth() > 0) && (dimensions.getLength() > 0) && (dimensions.getHeight() > 0))) {
			throw new IncorrectWorkPieceDataException();
		}
		
		switch(orientation) {
			case HORIZONTAL:
				configureHorizontalStackingPositions(dimensions);
				break;
			case TILTED:
				configureTiltedStackingPositions(dimensions);
				break;
			default:
				throw new IllegalArgumentException("Unknown work piece orientation");
		}
		
		this.orientation = orientation;

	}
	
	
	private void configureHorizontalStackingPositions(WorkPieceDimensions dimensions) {
		
		float remainingLength = dimensions.getLength();
		// initially two studs are used
		int amountOfHorizontalStudsOnePiece = 2;
		
		// the piece is moved studDiameter/2 from the center of its left-most stud (since it's aligned against it)
		// the distance between the left-most side and the center of the second stud is removed from the remaining length
		remainingLength -= (horizontalHoleDistance - studDiameter/2);
		
		// for real small work-pieces
		if (remainingLength < 0) {
			remainingLength = 0;
		}
				
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
		// note if this distance is long enough, the next stud does not need to be added to the horizontal studs of this piece, as it will be used for the next workpiece
		
		// how many times will this fit
		int amountHorizontal = (int) Math.floor(horizontalHoleAmount / amountOfHorizontalStudsOnePiece);
		
		// special condition for the last piece
		// we calculate the amount of space there is left:
		float remainingDistanceBetweenHoles = (horizontalHoleAmount % amountOfHorizontalStudsOnePiece - 1)*horizontalHoleDistance;
		if (remainingDistanceBetweenHoles < 0)  {
			remainingDistanceBetweenHoles = 0;
		}
		float spaceLeft = remainingDistanceBetweenHoles + horizontalPadding + overFlowPercentage*dimensions.getLength() - studDiameter/2;
		// if enough space if left (taking into account the overflowPercentage), an extra piece can be placed
		if (spaceLeft >= dimensions.getLength()) {
			amountHorizontal++;
		} else if ((spaceLeft < remainingLength) && (remainingDistanceBetweenHoles == 0)) {
			amountHorizontal--;
		}
				
		int amountOfVerticalStudsOnePiece = 1;
		float remainingWidth = dimensions.getWidth();
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
		spaceLeft = remainingDistanceBetweenHoles + verticalPadding + overFlowPercentage*dimensions.getWidth() - studDiameter/2;
		if (spaceLeft >= dimensions.getWidth()) {
			amountVertical++;
		} else if ((spaceLeft < remainingWidth) && (remainingDistanceBetweenHoles == 0)) {
			amountVertical--;
		}
		if ((amountOfVerticalStudsOnePiece == 1)&&(amountVertical > verticalHoleAmount)) {
			amountVertical--;
		}
				
		initializeRawWorkPiecePositionsHorizontal(amountOfHorizontalStudsOnePiece, amountOfVerticalStudsOnePiece, amountHorizontal, amountVertical, dimensions, remainingLength, remainingWidth);
	}
	
	private void initializeRawWorkPiecePositionsHorizontal(int amountOfHorizontalStudsOnePiece, int amountOfVerticalStudsOnePiece, 
			int amountHorizontal, int amountVertical, WorkPieceDimensions workPieceDimensions, float remainingLength, float remainingWidth) {
		int verticalStudIndex = 0;
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
				
				StackingPosition stackingPosition = new StackingPosition(horizontalPos, verticalPos, null, WorkPieceOrientation.HORIZONTAL);
				
				// condition one: only two vertical studs and not enough remaining width (only one leftVerticalExtraIndex)
				// condition two: only two horizontal studs, or: only three horizontal studs and not enough remaining length (only two rightHorizontalExtraIndex)
				if ((rightHorizontalExtraIndex <=2) || (leftVerticalExtraIndex == 0)) {
					StudPosition studPosition = new StudPosition(horizontalStudIndex, verticalStudIndex, studPositions[verticalStudIndex][horizontalStudIndex].getCenterPosition(), StudType.HORIZONTAL_CORNER);
					stackingPosition.addstud(studPosition);
					corner = true;
				} else {
					StudPosition studPosition1 = new StudPosition(horizontalStudIndex, verticalStudIndex+leftVerticalExtraIndex, studPositions[verticalStudIndex+leftVerticalExtraIndex][horizontalStudIndex].getCenterPosition(), StudType.NORMAL);
					StudPosition studPosition2 = new StudPosition(horizontalStudIndex + 1, verticalStudIndex, studPositions[verticalStudIndex][horizontalStudIndex + 1].getCenterPosition(), StudType.NORMAL);
					stackingPosition.addstud(studPosition1);
					stackingPosition.addstud(studPosition2);
				}
				
				int horizontalPos2 = horizontalStudIndex + rightHorizontalExtraIndex;
				while(horizontalPos2 >= studPositions[0].length) {
					horizontalPos2--;
				}
				if (horizontalPos2 > horizontalStudIndex) {
					if ((!corner) || (corner && (horizontalPos2 > horizontalStudIndex + 1))) {
						StudPosition studPos2 = new StudPosition(horizontalPos2, verticalStudIndex, studPositions[verticalStudIndex][horizontalPos2].getCenterPosition(), StudType.NORMAL);
						stackingPosition.addstud(studPos2);
					}
				}
				
				stackingPositions.add(stackingPosition);
				
				horizontalStudIndex += amountOfHorizontalStudsOnePiece;
			}
			verticalStudIndex += amountOfVerticalStudsOnePiece;
		}
	}
	
	private void configureTiltedStackingPositions(WorkPieceDimensions dimensions) throws IncorrectWorkPieceDataException {
		
		float a = (float) (horizontalHoleDistance/2 - Math.sqrt(2)*(studDiameter/2));
		float b = (float) (studDiameter/(2*Math.sqrt(2)));
		//float c = (float) (dimensions.getLength() / (Math.sqrt(2)));
		float d = (float) ((dimensions.getWidth() / (Math.sqrt(2)))  - horizontalHoleDistance/2);
		//float e = (float) (horizontalHoleDistance/2 - studDiameter / (2 * Math.sqrt(2)));
		float dright = (float) ((dimensions.getLength() / (Math.sqrt(2))) - horizontalHoleDistance/2);
		
		if (dimensions.getWidth() - MIN_OVERLAP_DISTANCE < Math.sqrt(2) * (a+b)) {
			// stud left to bottom-corner can't be reached
			throw new IncorrectWorkPieceDataException("Workpiece-width is too small");
		}
		
		double temp = (2*horizontalHoleDistance)*(2 * horizontalHoleDistance) + (verticalHoleDistance*verticalHoleDistance);
		if (dimensions.getLength() - MIN_OVERLAP_DISTANCE - (a+b)/(Math.sqrt(2)) < Math.sqrt(temp)) {
			// no upper-stud can be reached
			throw new IncorrectWorkPieceDataException("Workpiece-length is too small");
		}
		
		int amountOfHorizontalStudsOnePieceLeft = 1;
		int amountOfHorizontalStudsOnePieceRight = 1;
		float remainingD = d;
		while (remainingD > horizontalHoleDistance) {
			// for each time the remaining left-distance is bigger than the horizontal hole distance, an extra stud is needed on both sides
			amountOfHorizontalStudsOnePieceLeft++;
			remainingD -= horizontalHoleDistance;
		}
		
		float remainingDr = dright;
		while (remainingDr > horizontalHoleDistance) {
			amountOfHorizontalStudsOnePieceRight++;
			remainingDr -= horizontalHoleDistance;
		}
		
		// distance to next stud
		float f = horizontalHoleDistance - remainingD;
		float fr = horizontalHoleDistance - remainingDr;
		// total workpiece width (and height)
		//float totalHorizontalSize = (float) ((dimensions.getHeight() + dimensions.getLength())/Math.sqrt(2));
		
		float remainingLeft = remainingD;
		float remainingRight = remainingDr;
		
		int amountOfHorizontalStudsToTheRight = amountOfHorizontalStudsOnePieceLeft;
		
		// collision between two pieces would occur
		if (remainingLeft > horizontalHoleDistance/2 - interferenceDistance/2) {
			// so we add one to the right
			amountOfHorizontalStudsToTheRight++;
		} 
		
		// collision between a workpiece and the studs of its left-neighbour would occur
		while ( (amountOfHorizontalStudsToTheRight +1) * horizontalHoleDistance - studDiameter - interferenceDistance*(Math.sqrt(2)) - horizontalHoleDistance/2  < dimensions.getWidth() * Math.sqrt(2))  {
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
		float g = (float) (dimensions.getLength()/Math.sqrt(2) + dimensions.getWidth()/Math.sqrt(2) - a);
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
		
		initializeRawWorkPiecePostionsTilted(amountOfHorizontalStudsOnePieceLeft, amountOfHorizontalStudsToTheRight, amountOfHorizontalStudsOnePieceRight, amountOfVerticalStudsOnePiece, dimensions, amountHorizontal, amountVertical, a);
	}
	
	private void initializeRawWorkPiecePostionsTilted(int amountOfHorizontalStudsOnePieceLeft, int amountOfHorizontalStudsToTheRight, int amountOfHorizontalStudsOnePieceRight, int amountOfVerticalStudsOnePiece, 
			WorkPieceDimensions dimensions, int amountHorizontal, int amountVertical, float a) {
		
		float h = (float) (horizontalHoleDistance/2 + ( dimensions.getLength()/(Math.sqrt(2)) - dimensions.getWidth()/(Math.sqrt(2)) )/2);
		float v = (float) ( (dimensions.getLength()/(Math.sqrt(2)) + dimensions.getWidth()/(Math.sqrt(2)))/2 - a);
				
		int verticalIndex = 0;
		for (int i = 0; i < amountVertical; i++) {
			
			int horizontalIndex = -1;
			for (int j = 0; j < amountHorizontal; j++) {
				horizontalIndex += amountOfHorizontalStudsOnePieceLeft;
				
				float x = horizontalPadding + (horizontalIndex * horizontalHoleDistance) + h;
				float y = getWidth() - (verticalPadding + (verticalIndex * verticalHoleDistance) + v);
				
				StackingPosition position = new StackingPosition(x, y, null, WorkPieceOrientation.TILTED);
								
				StudPosition studPosition1 = new StudPosition(horizontalIndex, verticalIndex, studPositions[verticalIndex][horizontalIndex].getCenterPosition(), StudType.NORMAL);
				StudPosition studPosition2 = new StudPosition(horizontalIndex + 1, verticalIndex, studPositions[verticalIndex][horizontalIndex+1].getCenterPosition(), StudType.NORMAL);
				
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
					StudPosition studPosition3 = new StudPosition(horizontalIndex + 1 +extraRight, verticalIndex + extraTop, studPositions[verticalIndex + extraTop][horizontalIndex + 1 +extraRight].getCenterPosition(), StudType.NORMAL);
					position.addstud(studPosition1);
					position.addstud(studPosition2);
					position.addstud(studPosition3);
					stackingPositions.add(position);
				}
				horizontalIndex += amountOfHorizontalStudsToTheRight;
			}
			verticalIndex += amountOfVerticalStudsOnePiece;
		}
	}

	public void placeRawWorkPieces(WorkPiece rawWorkPiece, int amount) throws IncorrectWorkPieceDataException {
		if (amount <= getMaxRawWorkPiecesAmount()) {
			for (int i = 0; i < amount; i++) {
				StackingPosition stackingPos = stackingPositions.get(i);
				stackingPos.setWorkPiece(rawWorkPiece);
				for (StudPosition studPos : stackingPos.getStuds()) {
					studPositions[studPos.getRowIndex()][studPos.getColumnIndex()] = studPos;
				}
			}
		} else {
			throw new IncorrectWorkPieceDataException("Provided amount-argument is too high");
		}
	}
	
	public int getMaxRawWorkPiecesAmount() {
		return stackingPositions.size();
	}
	
	public int getHorizontalHoleAmount() {
		return horizontalHoleAmount;
	}

	public void setHorizontalHoleAmount(int horizontalHoleAmount) {
		this.horizontalHoleAmount = horizontalHoleAmount;
	}

	public int getVerticalHoleAmount() {
		return verticalHoleAmount;
	}

	public void setVerticalHoleAmount(int verticalHoleAmount) {
		this.verticalHoleAmount = verticalHoleAmount;
	}

	public float getHoleDiameter() {
		return holeDiameter;
	}

	public void setHoleDiameter(float holeDiameter) {
		this.holeDiameter = holeDiameter;
	}

	public float getStudDiameter() {
		return studDiameter;
	}

	public void setStudDiameter(float studDiameter) {
		this.studDiameter = studDiameter;
	}

	public float getHorizontalPadding() {
		return horizontalPadding;
	}

	public void setHorizontalPadding(float horizontalPadding) {
		this.horizontalPadding = horizontalPadding;
	}

	public float getVerticalPadding() {
		return verticalPadding;
	}

	public void setVerticalPadding(float verticalPadding) {
		this.verticalPadding = verticalPadding;
	}

	public float getHorizontalHoleDistance() {
		return horizontalHoleDistance;
	}

	public void setHorizontalHoleDistance(float horizontalHoleDistance) {
		this.horizontalHoleDistance = horizontalHoleDistance;
	}

	public float getVerticalHoleDistance() {
		return verticalHoleDistance;
	}

	public void setVerticalHoleDistance(float verticalHoleDistance) {
		this.verticalHoleDistance = verticalHoleDistance;
	}

	public StudPosition[][] getStudPositions() {
		return studPositions;
	}

	public void setStudPositions(StudPosition[][] studPositions) {
		this.studPositions = studPositions;
	}

	public List<StackingPosition> getStackingPositions() {
		return stackingPositions;
	}

	public void setStackingPositions(List<StackingPosition> stackingPositions) {
		this.stackingPositions = stackingPositions;
	}
	
	public WorkPieceOrientation getOrientation() {
		return orientation;
	}
	
	public int getRawWorkPieceAmount() {
		int amount = 0;
		for (StackingPosition position : stackingPositions) {
			if ((position.getWorkPiece() != null) && (position.getWorkPiece().getType() == Type.RAW)) {
				amount++;
			}
		}
		return amount;
	}
}
