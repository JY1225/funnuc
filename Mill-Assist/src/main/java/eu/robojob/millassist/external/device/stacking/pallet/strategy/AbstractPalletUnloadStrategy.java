package eu.robojob.millassist.external.device.stacking.pallet.strategy;

import eu.robojob.millassist.external.device.stacking.pallet.PalletLayout;
import eu.robojob.millassist.workpiece.IWorkPieceDimensions;

public abstract class AbstractPalletUnloadStrategy<T extends IWorkPieceDimensions> {
    /**
     * The layout of the pallet.
     */
    protected PalletLayout layout;
    
    public AbstractPalletUnloadStrategy(PalletLayout layout) {
        this.layout = layout;
    }

    /**
     * Determine the stacking positions of the layout for the given workpiece dimensions.
     * @param dimensions The dimensions of the work piece that will be stacked on the pallet
     */
    public abstract void configureFinishedPieces(T dimensions);

}
