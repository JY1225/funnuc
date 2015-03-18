package eu.robojob.millassist.external.device.stacking.conveyor.normal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.robojob.millassist.external.device.stacking.IncorrectWorkPieceDataException;
import eu.robojob.millassist.external.device.stacking.StackingPosition;
import eu.robojob.millassist.external.device.stacking.conveyor.normal.Conveyor.SupportState;
import eu.robojob.millassist.workpiece.WorkPiece;
import eu.robojob.millassist.workpiece.WorkPiece.Dimensions;

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
	private Conveyor.SupportState[] currentSupportStatus;
	private Boolean[] requestedSupportStatus;
	private Boolean[] supportSelectionStatus;
	private float offsetSupport1;
	private float offsetOtherSupports;
	
	private List<StackingPosition> stackingPositionsRawWorkPieces;
	private List<StackingPosition> stackingPositionsFinishedWorkPieces;
	
	private List<Boolean> finishedStackingPositionWorkPieces;
	
	private static Logger logger = LogManager.getLogger(ConveyorLayout.class.getName());
		
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
		int supportAmount = rawTrackAmount;
		currentSupportStatus = new Conveyor.SupportState[supportAmount];
		requestedSupportStatus = new Boolean[supportAmount];
		supportSelectionStatus = new Boolean[supportAmount];
		Arrays.fill(currentSupportStatus, SupportState.UNKNOWN);
		Arrays.fill(requestedSupportStatus, Boolean.FALSE);
		Arrays.fill(supportSelectionStatus, Boolean.FALSE);
		this.stackingPositionsRawWorkPieces = new ArrayList<StackingPosition>();
		this.stackingPositionsFinishedWorkPieces = new ArrayList<StackingPosition>();
		this.finishedStackingPositionWorkPieces = new ArrayList<Boolean>();
		this.offsetSupport1 = 0.0f;
		this.offsetOtherSupports = 0.0f;
	}
	
	public void clearSettings() {
		Arrays.fill(requestedSupportStatus, Boolean.FALSE);
		this.offsetSupport1 = 0.0f;
		this.offsetOtherSupports = 0.0f;
		stackingPositionsRawWorkPieces.clear();
		stackingPositionsFinishedWorkPieces.clear();
		finishedStackingPositionWorkPieces.clear();
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

	public float getOffsetSupport1() {
		return offsetSupport1;
	}

	public void setOffsetSupport1(final float offsetSupport1) {
		logger.info("setting offset 1: " + offsetSupport1);
		this.offsetSupport1 = offsetSupport1;
	}

	public float getOffsetOtherSupports() {
		return offsetOtherSupports;
	}

	public void setOffsetOtherSupports(final float offsetOtherSupports) {
		logger.info("setting offset other: " + offsetOtherSupports);
		this.offsetOtherSupports = offsetOtherSupports;
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

	public Conveyor.SupportState[] getCurrentSupportStatus() {
		return currentSupportStatus;
	}

	public void setCurrentSupportStatus(final Conveyor.SupportState[] currentSupportStatus) {
		this.currentSupportStatus = currentSupportStatus;
	}

	public Boolean[] getRequestedSupportStatus() {
		return requestedSupportStatus;
	}

	public void setRequestedSupportStatus(final Boolean[] requestedSupportStatus) {
		this.requestedSupportStatus = requestedSupportStatus;
	}
	
	public Boolean[] getSupportSelectionStatus() {
		return supportSelectionStatus;
	}

	public void setSupportSelectionStatus(Boolean[] supportSelectionStatus) {
		this.supportSelectionStatus = supportSelectionStatus;
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
		if (workPiece.getDimensions().getDimension(Dimensions.LENGTH) < workPiece.getDimensions().getDimension(Dimensions.WIDTH)) {
			throw new IncorrectWorkPieceDataException(IncorrectWorkPieceDataException.LENGTH_SMALLER_WIDTH);
		}
		if (workPiece.getDimensions().getDimension(Dimensions.LENGTH) > maxWorkPieceLength) {
			throw new IncorrectWorkPieceDataException(IncorrectWorkPieceDataException.TOO_LARGE);
		}
		int amount = 1;
		boolean finished = false;
		float distance = 0;
		while (!finished) {
			distance = amount * rawTrackWidth;
			// calculate extra space on top
			if (amount < rawTrackAmount) {
				// no extra offset
			} else if (amount == rawTrackAmount) {
				distance += maxOverlap;
			} else {
				throw new IncorrectWorkPieceDataException(IncorrectWorkPieceDataException.TOO_LARGE);
			}
			// calculate space between tracks
			distance += (amount - 1) * spaceBetweenTracks;
			//FIXME replace 5 by new parameter: interference distance raw
			if (distance - 5 >= workPiece.getDimensions().getDimension(Dimensions.WIDTH)) {
				finished = true;
			} else {
				amount++;
			}
		}
		for (int i = 0; i < Math.floor(rawTrackAmount / amount); i++) {
			float x = workPiece.getDimensions().getDimension(Dimensions.LENGTH)/2 + minDistRaw;			
			float y = workPiece.getDimensions().getDimension(Dimensions.WIDTH)/2;
			//FIXME review
			int currentSupportNr = i * amount;
			if (currentSupportNr == 0) {
				logger.info("Against 0: " + offsetSupport1);
				y = y + offsetSupport1;
			} else {
				logger.info("Against other: " + offsetOtherSupports);
				y = y + offsetOtherSupports;
			}
			// add the distance of the tracks used by previous work pieces
			// add the distance of the space between tracks used by previous work pieces
			y += (i * amount) * rawTrackWidth;
			y += (i * amount) * spaceBetweenTracks;
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
			StackingPosition stackingPos;
			if (parent.isLeftSetup()) {
				stackingPos = new StackingPosition(x, y, 0, workPiece);
			} else {
				stackingPos = new StackingPosition(y, x, 0, workPiece);
			}
			this.stackingPositionsRawWorkPieces.add(stackingPos);
			// set requested support below
			requestedSupportStatus[i*amount] = true;
		}
		logger.info("Requested support status: " + requestedSupportStatus[0] + " - "  + requestedSupportStatus[1] +
				 " - "  + requestedSupportStatus[2] + " - "  + requestedSupportStatus[3]);
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
				totalHeight += workPiece.getDimensions().getDimension(Dimensions.WIDTH);
				if (amountOneRow > 1) {
					totalHeight += interferenceDistance;
				}
			}
		}
		// calculate the amount of space left between pieces without the overlap 
		float extraSpaceWithoutOverlap = getWidthFinishedWorkPieceConveyor() - 10 - 
				amountOneRow * workPiece.getDimensions().getDimension(Dimensions.WIDTH) - (amountOneRow - 1) * interferenceDistance;
		float spaceBetween = Math.abs(extraSpaceWithoutOverlap)/(amountOneRow - 1) + interferenceDistance;
		// place the workPieces
		for (int i = 0; i < amountOneRow; i++) {
			float xFirst = minDistFinished + workPiece.getDimensions().getDimension(Dimensions.LENGTH)/2;
			float yFirst = workPiece.getDimensions().getDimension(Dimensions.WIDTH)/2;
			if (i == 0) {
				StackingPosition stPos;
				if (parent.isLeftSetup()) {
					stPos = new StackingPosition(xFirst, yFirst, 0, workPiece);
				} else {
					stPos = new StackingPosition(yFirst, xFirst, 0, workPiece);
				}
				stackingPositionsFinishedWorkPieces.add(stPos);
			} else {
				float x = xFirst;
				float y = yFirst + i * (workPiece.getDimensions().getDimension(Dimensions.WIDTH) + spaceBetween);
				if (parent.isLeftSetup()) {
					StackingPosition stPos = new StackingPosition(x, y, 0, workPiece);
					stackingPositionsFinishedWorkPieces.add(stPos);
				} else {
					StackingPosition stPos = new StackingPosition(y, x, 0, workPiece);
					stackingPositionsFinishedWorkPieces.add(stPos);
				}
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
	
	public boolean isLeftSetup() {
		return parent.isLeftSetup();
	}
	
}
