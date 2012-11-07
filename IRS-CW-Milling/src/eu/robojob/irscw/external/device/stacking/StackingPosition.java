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
	
	private List<StudPosition> studs;
	
	public StackingPosition(Coordinates position, WorkPiece workPiece, WorkPieceOrientation orientation, List<StudPosition> studs) {
		this.position = position;
		this.workPiece = workPiece;
		this.studs = studs;
		this.orientation = orientation;
		if (orientation == WorkPieceOrientation.TILTED) {
			position.setR(45);
		} else if (orientation == WorkPieceOrientation.HORIZONTAL) {
			position.setR(0);
		} else {
			throw new IllegalArgumentException("Unkown orientation");
		}
	}
	
	public StackingPosition(Coordinates position,WorkPiece workPiece, WorkPieceOrientation orientation) {
		this(position, workPiece, orientation, new ArrayList<StudPosition>());
	}

	public StackingPosition(float horizontalPosition, float verticalPosition, WorkPiece workPiece, WorkPieceOrientation orientation) {
		this (new Coordinates(horizontalPosition, verticalPosition, 0, 0, 0, 0), workPiece, orientation);
	}
	
	public Coordinates getPosition() {
		return position;
	}

	public WorkPiece getWorkPiece() {
		return workPiece;
	}

	public void setWorkPiece(WorkPiece workPiece) {
		this.workPiece = workPiece;
	}
	
	public List<StudPosition> getStuds() {
		return studs;
	}
	
	public WorkPieceOrientation getOrientation() {
		return orientation;
	}
	
	public void addstud(StudPosition stud) {
		studs.add(stud);
	}

	public String toString() {
		return "StackingPosition: " + position + "  -  " + workPiece;
	}
}