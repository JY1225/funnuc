package eu.robojob.millassist.external.device.stacking.pallet.strategy;

import eu.robojob.millassist.external.device.stacking.pallet.PalletLayout;
import eu.robojob.millassist.workpiece.IWorkPieceDimensions;

public abstract class AbstractPalletUnloadStrategy<T extends IWorkPieceDimensions> {
    
    protected PalletLayout layout;
    public AbstractPalletUnloadStrategy(PalletLayout layout) {
        this.layout = layout;
    }

    public abstract void configureFinishedPieces(T dimensions);

}
