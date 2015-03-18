package eu.robojob.millassist.external.device.stacking.stackplate.strategy.gridPlate;

import eu.robojob.millassist.external.device.stacking.stackplate.gridplate.GridPlateLayout;
import eu.robojob.millassist.external.device.stacking.stackplate.strategy.AbstractPieceStackingStrategy;
import eu.robojob.millassist.workpiece.IWorkPieceDimensions;

public abstract class AGridPlateStrategy<T extends IWorkPieceDimensions> extends AbstractPieceStackingStrategy<T, GridPlateLayout> {

	protected AGridPlateStrategy(GridPlateLayout context) {
		super(context);
	}
	
}
