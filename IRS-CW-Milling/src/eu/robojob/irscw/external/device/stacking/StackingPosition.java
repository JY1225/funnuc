package eu.robojob.irscw.external.device.stacking;

import java.util.ArrayList;
import java.util.List;

import eu.robojob.irscw.external.device.stacking.BasicStackPlate.WorkPieceOrientation;
import eu.robojob.irscw.positioning.Coordinates;
import eu.robojob.irscw.workpiece.WorkPiece;

public class StackingPosition {
	
	private Coordinates position;
	private WorkPiece workPiece;
	private WorkPieceOrientation orientation;
	
	private static final int HORIZONTAL_R = 90;
	private static final int TILTED_R = 135;
	
	private List<StudPosition> studs;
	
	public StackingPosition(final Coordinates position, final WorkPiece workPiece, final WorkPieceOrientation orientation, final List<StudPosition> studs) {
		this.position = position;
		this.workPiece = workPiece;
		this.studs = studs;
		this.orientation = orientation;
		if (orientation == WorkPieceOrientation.TILTED) {
			position.setR(TILTED_R);
		} else if (orientation == WorkPieceOrientation.HORIZONTAL) {
			position.setR(HORIZONTAL_R);
		} else {
			throw new IllegalArgumentException("Unkown orientation.");
		}
	}
	
	public StackingPosition(final Coordinates position, final WorkPiece workPiece, final WorkPieceOrientation orientation) {
		this(position, workPiece, orientation, new ArrayList<StudPosition>());
	}

	public StackingPosition(final float horizontalPosition, final float verticalPosition, final WorkPiece workPiece, final WorkPieceOrientation orientation) {
		this (new Coordinates(horizontalPosition, verticalPosition, 0, 0, 0, 0), workPiece, orientation);
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
	
	public List<StudPosition> getStuds() {
		return studs;
	}
	
	public WorkPieceOrientation getOrientation() {
		return orientation;
	}
	
	public void addstud(final StudPosition stud) {
		studs.add(stud);
	}

	public String toString() {
		return "StackingPosition: " + position + "  -  " + workPiece;
	}
}