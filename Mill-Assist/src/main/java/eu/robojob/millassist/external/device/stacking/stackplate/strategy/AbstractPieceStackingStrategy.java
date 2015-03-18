package eu.robojob.millassist.external.device.stacking.stackplate.strategy;

import eu.robojob.millassist.external.device.stacking.IncorrectWorkPieceDataException;
import eu.robojob.millassist.external.device.stacking.stackplate.AbstractStackPlateLayout;
import eu.robojob.millassist.workpiece.IWorkPieceDimensions;

public abstract class AbstractPieceStackingStrategy<T extends IWorkPieceDimensions, S extends AbstractStackPlateLayout> {
	
	private S deviceContext;
	
	protected AbstractPieceStackingStrategy(S context) {
		this.deviceContext = context;
	}
	
	protected S getContext() {
		return this.deviceContext;
	}
	
	public abstract void configureStackingPositions(T rawPiece, T finishedPiece)  throws IncorrectWorkPieceDataException;
	public abstract void configureOnlyRawStackingPos(T rawPiece)  throws IncorrectWorkPieceDataException;
	public abstract void configureSameDimensionPositions(T rawPiece)  throws IncorrectWorkPieceDataException;

	protected abstract void isValidWorkPiece(final T workPiece) throws IncorrectWorkPieceDataException;
	
}
