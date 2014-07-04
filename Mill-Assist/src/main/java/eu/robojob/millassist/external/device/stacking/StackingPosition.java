package eu.robojob.millassist.external.device.stacking;

import eu.robojob.millassist.positioning.Coordinates;
import eu.robojob.millassist.workpiece.WorkPiece;

public class StackingPosition {

	private Coordinates position;
	private WorkPiece workPiece;
		
	public StackingPosition(final Coordinates position, final WorkPiece workPiece) {
		this.position = position;
		this.workPiece = workPiece;
	}

	public StackingPosition(final float horizontalPosition, final float verticalPosition, final float r, final WorkPiece workPiece) {
		this (new Coordinates(horizontalPosition, verticalPosition, 0, 0, 0, r), workPiece);
	}
	
	public Coordinates getPosition() {
		return position;
	}

	public WorkPiece getWorkPiece() {
		return workPiece;
	}

	public void setWorkPiece(final WorkPiece workPiece) {
		this.workPiece = workPiece;
	}
	
	public boolean hasWorkPiece() {
		return this.workPiece != null;
	}
	
	public String toString() {
		return "StackingPosition: " + position + "  -  " + workPiece;
	}
}
