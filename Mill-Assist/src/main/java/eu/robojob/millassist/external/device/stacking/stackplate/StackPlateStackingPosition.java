package eu.robojob.millassist.external.device.stacking.stackplate;

import java.util.ArrayList;
import java.util.List;

import eu.robojob.millassist.external.device.stacking.StackingPosition;
import eu.robojob.millassist.external.device.stacking.stackplate.BasicStackPlate.WorkPieceOrientation;
import eu.robojob.millassist.positioning.Coordinates;
import eu.robojob.millassist.workpiece.WorkPiece;

public class StackPlateStackingPosition extends StackingPosition {
	
	private WorkPieceOrientation orientation;
	
	private List<StudPosition> studs;
	
	public StackPlateStackingPosition(final Coordinates position, final WorkPiece workPiece, final WorkPieceOrientation orientation, final List<StudPosition> studs) {
		super(position, workPiece);
		this.studs = studs;
		this.orientation = orientation;
	}
	
	public StackPlateStackingPosition(final Coordinates position, final WorkPiece workPiece, final WorkPieceOrientation orientation) {
		this(position, workPiece, orientation, new ArrayList<StudPosition>());
	}

	public StackPlateStackingPosition(final float horizontalPosition, final float verticalPosition, final float r, final WorkPiece workPiece, final WorkPieceOrientation orientation) {
		this (new Coordinates(horizontalPosition, verticalPosition, 0, 0, 0, r), workPiece, orientation);
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

}