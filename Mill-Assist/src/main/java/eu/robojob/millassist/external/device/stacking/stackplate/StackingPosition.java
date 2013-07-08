package eu.robojob.millassist.external.device.stacking.stackplate;

import java.util.ArrayList;
import java.util.List;

import eu.robojob.millassist.external.device.stacking.stackplate.BasicStackPlate.WorkPieceOrientation;
import eu.robojob.millassist.positioning.Coordinates;
import eu.robojob.millassist.workpiece.WorkPiece;

public class StackingPosition {
	
	private Coordinates position;
	private WorkPiece workPiece;
	private WorkPieceOrientation orientation;
	
	private List<StudPosition> studs;
	
	public StackingPosition(final Coordinates position, final WorkPiece workPiece, final WorkPieceOrientation orientation, final List<StudPosition> studs) {
		this.position = position;
		this.workPiece = workPiece;
		this.studs = studs;
		this.orientation = orientation;
	}
	
	public StackingPosition(final Coordinates position, final WorkPiece workPiece, final WorkPieceOrientation orientation) {
		this(position, workPiece, orientation, new ArrayList<StudPosition>());
	}

	public StackingPosition(final float horizontalPosition, final float verticalPosition, final float r, final WorkPiece workPiece, final WorkPieceOrientation orientation) {
		this (new Coordinates(horizontalPosition, verticalPosition, 0, 0, 0, r), workPiece, orientation);
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