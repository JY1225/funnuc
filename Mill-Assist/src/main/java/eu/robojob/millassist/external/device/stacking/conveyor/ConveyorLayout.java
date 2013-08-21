package eu.robojob.millassist.external.device.stacking.conveyor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import eu.robojob.millassist.external.device.stacking.IncorrectWorkPieceDataException;
import eu.robojob.millassist.external.device.stacking.StackingPosition;
import eu.robojob.millassist.workpiece.WorkPiece;

public class ConveyorLayout {

	private Conveyor parent;
	
	private int rawTrackAmount;
	private float rawTrackWidth;
	private float spaceBetweenTracks;
	private float supportWidth;
	private float finishedConveyorWidth;
	private float interferenceDistance;
	private float maxWorkPieceLength;
	private float rawConveyorLength;
	private float finishedConveyorLength;
	private float maxOverlap;
	private float minDistRaw;
	private float minDistFinished;
	private Boolean[] currentSupportStatus;
	private Boolean[] requestedSupportStatus;
	
	private List<StackingPosition> stackingPositionsRawWorkPieces;
	private List<StackingPosition> stackingPositionsFinishedWorkPieces;
	
	private List<Boolean> finishedStackingPositionWorkPieces;
	
	public ConveyorLayout(final int rawTrackAmount, final float rawTrackWidth, final float spaceBetweenTracks, 
			final float supportWidth, final float finishedConveyorWidth, final float interferenceDistance, 
			final float maxWorkPieceLength, final float rawConveyorLength, final float finishedConveyorLength, 
			final float maxOverlap, final float minDistRaw, final float minDistFinished) {
		this.rawTrackAmount = rawTrackAmount;
		this.rawTrackWidth = rawTrackWidth;
		this.spaceBetweenTracks = spaceBetweenTracks;
		this.supportWidth = supportWidth;
		this.finishedConveyorWidth = finishedConveyorWidth;
		this.interferenceDistance = interferenceDistance;
		this.maxWorkPieceLength = maxWorkPieceLength;
		this.rawConveyorLength = rawConveyorLength;
		this.finishedConveyorLength = finishedConveyorLength;
		this.maxOverlap = maxOverlap;
		this.minDistRaw = minDistRaw;
		this.minDistFinished = minDistFinished;
		int supportAmount = rawTrackAmount - 1;
		currentSupportStatus = new Boolean[supportAmount];
		requestedSupportStatus = new Boolean[supportAmount];
		Arrays.fill(currentSupportStatus, Boolean.FALSE);
		Arrays.fill(requestedSupportStatus, Boolean.FALSE);
		this.stackingPositionsRawWorkPieces = new ArrayList<StackingPosition>();
		this.stackingPositionsFinishedWorkPieces = new ArrayList<StackingPosition>();
		this.finishedStackingPositionWorkPieces = new ArrayList<Boolean>();
	}
	
	public void setParent(final Conveyor parent) {
		this.parent = parent;
	}

	public Conveyor getParent() {
		return this.parent;
	}
	
	public int getRawTrackAmount() {
		return rawTrackAmount;
	}

	public List<Boolean> getFinishedStackingPositionWorkPieces() {
		return finishedStackingPositionWorkPieces;
	}

	public void setRawTrackAmount(final int rawTrackAmount) {
		this.rawTrackAmount = rawTrackAmount;
	}


	public float getRawTrackWidth() {
		return rawTrackWidth;
	}


	public void setRawTrackWidth(final float rawTrackWidth) {
		this.rawTrackWidth = rawTrackWidth;
	}

	public void setFinishedStackingPositionWorkPiece(final int index, final boolean value) {
		finishedStackingPositionWorkPieces.set(index, value);
		parent.notifyLayoutChanged();
	}

	public void setFinishedStackingPositionsNoWorkPiece() {
		Collections.fill(finishedStackingPositionWorkPieces, false);
		parent.notifyLayoutChanged();
	}
	
	public void shiftFinishedWorkPieces() {
		Collections.fill(finishedStackingPositionWorkPieces, false);
		parent.notifyFinishedShifted();
	}
	
	public float getSpaceBetweenTracks() {
		return spaceBetweenTracks;
	}

	public void setSpaceBetweenTracks(final float spaceBetweenTracks) {
		this.spaceBetweenTracks = spaceBetweenTracks;
	}

	public float getSupportWidth() {
		return supportWidth;
	}

	public void setSupportWidth(final float supportWidth) {
		this.supportWidth = supportWidth;
	}

	public float getFinishedConveyorWidth() {
		return finishedConveyorWidth;
	}

	public void setFinishedConveyorWidth(final float finishedConveyorWidth) {
		this.finishedConveyorWidth = finishedConveyorWidth;
	}

	public float getInterferenceDistance() {
		return interferenceDistance;
	}

	public void setInterferenceDistance(final float interferenceDistance) {
		this.interferenceDistance = interferenceDistance;
	}

	public float getMaxWorkPieceLength() {
		return maxWorkPieceLength;
	}

	public void setMaxWorkPieceLength(final float maxWorkPieceLength) {
		this.maxWorkPieceLength = maxWorkPieceLength;
	}

	public float getRawConveyorLength() {
		return rawConveyorLength;
	}

	public void setRawConveyorLength(final float rawConveyorLength) {
		this.rawConveyorLength = rawConveyorLength;
	}

	public float getFinishedConveyorLength() {
		return finishedConveyorLength;
	}

	public void setFinishedConveyorLength(final float finishedConveyorLength) {
		this.finishedConveyorLength = finishedConveyorLength;
	}

	public Boolean[] getCurrentSupportStatus() {
		return currentSupportStatus;
	}

	public void setCurrentSupportStatus(final Boolean[] currentSupportStatus) {
		this.currentSupportStatus = currentSupportStatus;
	}

	public Boolean[] getRequestedSupportStatus() {
		return requestedSupportStatus;
	}

	public void setRequestedSupportStatus(final Boolean[] requestedSupportStatus) {
		this.requestedSupportStatus = requestedSupportStatus;
	}
	
	public float getMaxOverlap() {
		return maxOverlap;
	}

	public void setMaxOverlap(final float maxOverlap) {
		this.maxOverlap = maxOverlap;
	}
	
	public float getMinDistRaw() {
		return minDistRaw;
	}

	public void setMinDistRaw(final float minDistRaw) {
		this.minDistRaw = minDistRaw;
	}

	public float getMinDistFinished() {
		return minDistFinished;
	}

	public void setMinDistFinished(final float minDistFinished) {
		this.minDistFinished = minDistFinished;
	}

	public List<StackingPosition> getStackingPositionsRawWorkPieces() {
		return stackingPositionsRawWorkPieces;
	}

	public void setStackingPositionsRawWorkPieces(final List<StackingPosition> stackingPositions) {
		this.stackingPositionsRawWorkPieces = stackingPositions;
	}

	public List<StackingPosition> getStackingPositionsFinishedWorkPieces() {
		return stackingPositionsFinishedWorkPieces;
	}

	public void setStackingPositionsFinishedWorkPieces(final List<StackingPosition> stackingPositionsFinishedWorkPieces) {
		this.stackingPositionsFinishedWorkPieces = stackingPositionsFinishedWorkPieces;
	}

	//TODO rekening houden met overflow percentage!
	public void configureRawWorkPieceStackingPositions() throws IncorrectWorkPieceDataException {
		
		WorkPiece workPiece = parent.getRawWorkPiece();
		
		this.stackingPositionsRawWorkPieces.clear();
		Arrays.fill(requestedSupportStatus, false);
		// first calculate the amount of tracks required for one workPiece
		if (workPiece.getDimensions().getLength() < workPiece.getDimensions().getWidth()) {
			throw new IncorrectWorkPieceDataException(IncorrectWorkPieceDataException.LENGTH_SMALLER_WIDTH);
		}
		if (workPiece.getDimensions().getLength() > maxWorkPieceLength) {
			throw new IncorrectWorkPieceDataException(IncorrectWorkPieceDataException.TOO_LARGE);
		}
		int amount = 1;
		boolean finished = false;
		float distance = 0;
		while (!finished) {
			distance = amount * rawTrackWidth;
			// calculate extra space on top
			if (amount < rawTrackAmount) {
				distance += (spaceBetweenTracks - supportWidth) / 2;
			} else if (amount == rawTrackAmount) {
				distance += maxOverlap;
			} else {
				throw new IncorrectWorkPieceDataException(IncorrectWorkPieceDataException.TOO_LARGE);
			}
			// calculate space between tracks
			distance += (amount - 1) * spaceBetweenTracks;
			// calculate extra space on bottom
			distance += (spaceBetweenTracks -  supportWidth) / 2;
			if (distance - interferenceDistance >= workPiece.getDimensions().getWidth()) {
				finished = true;
			} else {
				amount++;
			}
		}
		for (int i = 0; i < Math.floor(rawTrackAmount / amount); i++) {
			float x = workPiece.getDimensions().getLength()/2 + minDistRaw;			
			float y = workPiece.getDimensions().getWidth()/2;
			// add the distance of the tracks used by previous work pieces
			// add the distance of the space between tracks used by previous work pieces
			if (i > 0) {
				y += (i * amount) * rawTrackWidth;
				y += (i * amount) * spaceBetweenTracks;
				//y += supportWidth;

			}
			// if not the first, add the distance of support and distance between support en 
			// track below
			// if not the first add the distance between the first support and the first track
			// together = space between tracks
			/*if (i > 0) {
				y += spaceBetweenTracks;
			} else {
				// else add the distance of the first support and gap
				y += supportWidth + (spaceBetweenTracks - supportWidth) / 2;
			}*/
			StackingPosition stackingPos = new StackingPosition(x, y, 0, workPiece);
			this.stackingPositionsRawWorkPieces.add(stackingPos);
			// set requested support below
			if (i > 0) {
				requestedSupportStatus[i*amount - 1] = true;
			}
		}
		
		configureFinishedWorkPieceStackingPositions();
	}
	
	//TODO rekening houden met overflow percentage!
	public void configureFinishedWorkPieceStackingPositions() throws IncorrectWorkPieceDataException {
		WorkPiece workPiece = parent.getFinishedWorkPiece();
		this.stackingPositionsFinishedWorkPieces.clear();
		this.finishedStackingPositionWorkPieces.clear();
		// calculate the amount of workpieces that can fit in one row
		float rowHeight = getWidthFinishedWorkPieceConveyorWithOverlap();
		boolean finished = false;
		int amountOneRow = 0;
		float totalHeight = 0;
		while (!finished) {
			if (totalHeight > rowHeight) {
				finished = true;
				amountOneRow--;
			} else {			
				amountOneRow++;
				totalHeight += workPiece.getDimensions().getWidth();
				if (amountOneRow > 1) {
					totalHeight += interferenceDistance;
				}
			}
		}
		// calculate the amount of space left between pieces without the overlap 
		float extraSpaceWithoutOverlap = getWidthFinishedWorkPieceConveyor() - 
				amountOneRow * workPiece.getDimensions().getWidth() - (amountOneRow - 1) * interferenceDistance;
		float spaceBetween = Math.abs(extraSpaceWithoutOverlap)/(amountOneRow - 1) + interferenceDistance;
		// place the workPieces
		for (int i = 0; i < amountOneRow; i++) {
			float xFirst = minDistFinished + workPiece.getDimensions().getLength()/2;
			float yFirst = workPiece.getDimensions().getWidth()/2;
			if (i == 0) {
				StackingPosition stPos = new StackingPosition(xFirst, yFirst, 0, workPiece);
				stackingPositionsFinishedWorkPieces.add(stPos);
			} else {
				float x = xFirst;
				float y = yFirst + i * (workPiece.getDimensions().getWidth() + spaceBetween);
				StackingPosition stPos = new StackingPosition(x, y, 0, workPiece);
				stackingPositionsFinishedWorkPieces.add(stPos);
			}
			finishedStackingPositionWorkPieces.add(false);
		}
		if (amountOneRow <= 0) {
			throw new IncorrectWorkPieceDataException(IncorrectWorkPieceDataException.TOO_LARGE);
		}
	}
	
	public float getWidthRawWorkPieceConveyor() {
		float width = rawTrackAmount * rawTrackWidth + (rawTrackAmount - 1) * spaceBetweenTracks + 
				spaceBetweenTracks - (spaceBetweenTracks - supportWidth) / 2;
		return width;
	}
	
	public float getWidthRawWorkPieceConveyorWithOverlap() {
		float width = rawTrackAmount * rawTrackWidth + maxOverlap + (rawTrackAmount - 1) * spaceBetweenTracks + 
				spaceBetweenTracks - (spaceBetweenTracks - supportWidth) / 2;
		return width;
	}
	
	public float getWidthFinishedWorkPieceConveyor() {
		return finishedConveyorWidth;
	}
	
	public float getWidthFinishedWorkPieceConveyorWithOverlap() {
		return finishedConveyorWidth + maxOverlap;
	}
	
}
