package eu.robojob.irscw.external.device;

import eu.robojob.irscw.external.device.BasicStackPlate.WorkPieceOrientation;
import eu.robojob.irscw.positioning.Coordinates;
import eu.robojob.irscw.workpiece.WorkPieceDimensions;

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