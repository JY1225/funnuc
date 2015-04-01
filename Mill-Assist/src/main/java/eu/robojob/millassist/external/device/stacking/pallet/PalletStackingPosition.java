package eu.robojob.millassist.external.device.stacking.pallet;

import eu.robojob.millassist.external.device.stacking.StackingPosition;
import eu.robojob.millassist.positioning.Coordinates;
import eu.robojob.millassist.workpiece.WorkPiece;

public class PalletStackingPosition extends StackingPosition {

    /**
     * The current amount of work pieces on this stacking position.
     */
    private int amount;
    
    public PalletStackingPosition(Coordinates position, WorkPiece workPiece, final int amount) {
        super(position, workPiece);
        this.amount = amount;
    }
    
    public PalletStackingPosition(final float horizontalPosition, final float verticalPosition, final float r, final WorkPiece workPiece) {
        this (new Coordinates(horizontalPosition, verticalPosition, 0, 0, 0, r), workPiece, 0);
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
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
    
    /**
     * Determines the Z value of the put position and then returns the updated put position.
     * @return The put position
     */
    public Coordinates getPutPosition() {
        Coordinates position = new Coordinates(super.getPosition());
        if (amount > 0) {
            position.setZ(amount * getWorkPiece().getDimensions().getZSafe());
        }
        return position;
    }

}
