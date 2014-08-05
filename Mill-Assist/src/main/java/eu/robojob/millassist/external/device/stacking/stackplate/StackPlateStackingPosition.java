package eu.robojob.millassist.external.device.stacking.stackplate;

import java.util.ArrayList;
import java.util.List;

import eu.robojob.millassist.external.device.stacking.StackingPosition;
import eu.robojob.millassist.external.device.stacking.stackplate.BasicStackPlate.WorkPieceOrientation;
import eu.robojob.millassist.positioning.Coordinates;
import eu.robojob.millassist.workpiece.WorkPiece;

public class StackPlateStackingPosition extends StackingPosition {
	
	private WorkPieceOrientation orientation;
	private int amount;	
	private List<StudPosition> studs;
	
	public StackPlateStackingPosition(final Coordinates position, final WorkPiece workPiece, final int amount, final WorkPieceOrientation orientation, final List<StudPosition> studs) {
		super(position, workPiece);
		this.studs = studs;
		this.orientation = orientation;
		this.amount = amount;
	}
	
	public StackPlateStackingPosition(final Coordinates position, final WorkPiece workPiece, final int amount, final WorkPieceOrientation orientation) {
		this(position, workPiece, amount, orientation, new ArrayList<StudPosition>());
	}

	public StackPlateStackingPosition(final float horizontalPosition, final float verticalPosition, final float r, final WorkPiece workPiece, final int amount, final WorkPieceOrientation orientation) {
		this (new Coordinates(horizontalPosition, verticalPosition, 0, 0, 0, r), workPiece, amount, orientation);
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
	
	public Coordinates getPickPosition() {
		// for pick the top workpiece shouldn't be used!
		Coordinates position = new Coordinates(super.getPosition());
		if (amount > 0) {
			position.setZ((amount - 1) * getWorkPiece().getDimensions().getHeight());
		} 
		return position;
	}
	
	public Coordinates getPutPosition() {
		// for pick the top workpiece shouldn't be used!
		Coordinates position = new Coordinates(super.getPosition());
		if (amount > 0) {
			position.setZ(amount * getWorkPiece().getDimensions().getHeight());
		}
		return position;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(final int amount) {
		this.amount = amount;
	}
	
	public void incrementAmount() {
		this.amount++;
	}
	
	public void decrementAmount() {
		this.amount--;
	}
	
	public void incrementAmountBy(int count) {
		this.amount += count;
	}
	
	public void decrementAmountBy(int count) {
		this.amount -= count;
		if(this.amount <= 0) {
			this.setWorkPiece(null);
		}
	}

}