package eu.robojob.irscw.external.device.stacking;

import eu.robojob.irscw.external.device.DeviceSettings;
import eu.robojob.irscw.external.device.stacking.BasicStackPlate.WorkPieceOrientation;
import eu.robojob.irscw.workpiece.WorkPiece;
import eu.robojob.irscw.workpiece.WorkPiece.Type;
import eu.robojob.irscw.workpiece.WorkPieceDimensions;

public class BasicStackPlateSettings extends DeviceSettings {

	private WorkPiece rawWorkPiece;
	private WorkPiece finishedWorkPiece;
	//TODO maybe in the future, a list of intermediate half-finished workpieces could be added
	private WorkPieceOrientation orientation;
	private int amount;
	
	public BasicStackPlateSettings(final WorkPieceDimensions dimensions, final WorkPieceDimensions finishedDimensions,
			final WorkPieceOrientation orientation, final int amount) {
		this(new WorkPiece(WorkPiece.Type.RAW, dimensions), new WorkPiece(WorkPiece.Type.FINISHED, finishedDimensions), orientation, amount);
	}
	
	public BasicStackPlateSettings(final WorkPiece workPiece, final WorkPiece finishedWorkPiece, final WorkPieceOrientation orientation, final int amount) {
		this.amount = amount;
		this.orientation = orientation;
		this.rawWorkPiece = workPiece;
		this.finishedWorkPiece = finishedWorkPiece;
	}

	public int getAmount() {
		return amount;
	}

	public void setRawWorkPieceDimensions(final WorkPieceDimensions dimensions) {
		if (rawWorkPiece == null) {
			rawWorkPiece = new WorkPiece(Type.RAW, dimensions);
		} else {
			this.rawWorkPiece.setDimensions(dimensions);
		}
	}
	
	public void setFinishedWorkPieceDimensions(final WorkPieceDimensions dimensions) {
		if (finishedWorkPiece == null) {
			finishedWorkPiece = new WorkPiece(Type.FINISHED, dimensions);
		} else {
			this.finishedWorkPiece.setDimensions(dimensions);
		}
	}

	public void setOrientation(final WorkPieceOrientation orientation) {
		this.orientation = orientation;
	}

	public void setAmount(final int amount) {
		this.amount = amount;
	}
	
	public WorkPiece getRawWorkPiece() {
		return this.rawWorkPiece;
	}
	
	public WorkPiece getFinishedWorkPiece() {
		return this.finishedWorkPiece;
	}

	public WorkPieceOrientation getOrientation() {
		return orientation;
	}
}