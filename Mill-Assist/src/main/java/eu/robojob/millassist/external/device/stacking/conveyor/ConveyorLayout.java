package eu.robojob.millassist.external.device.stacking.conveyor;

public class ConveyorLayout {

	private int rawTrackAmount;
	private float rawTrackWidth;
	private float spaceBetweenTracks;
	private float supportWidth;
	private float finishedConveyorWidth;
	private float interferenceDistance;
	private float maxWorkPieceLength;
	private float rawConveyorLength;
	private float finishedConveyorLength;
	
	public ConveyorLayout(final int rawTrackAmount, final float rawTrackWidth, final float spaceBetweenTracks, 
			final float supportWidth, final float finishedConveyorWidth, final float interferenceDistance, 
			final float maxWorkPieceLength, final float rawConveyorLength, final float finishedConveyorLength) {
		this.rawTrackAmount = rawTrackAmount;
		this.rawTrackWidth = rawTrackWidth;
		this.spaceBetweenTracks = spaceBetweenTracks;
		this.supportWidth = supportWidth;
		this.finishedConveyorWidth = finishedConveyorWidth;
		this.interferenceDistance = interferenceDistance;
		this.maxWorkPieceLength = maxWorkPieceLength;
		this.rawConveyorLength = rawConveyorLength;
		this.finishedConveyorLength = finishedConveyorLength;
	}

	public int getRawTrackAmount() {
		return rawTrackAmount;
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
	
}
