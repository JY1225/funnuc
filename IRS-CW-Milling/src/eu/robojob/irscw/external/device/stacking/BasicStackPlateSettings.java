package eu.robojob.irscw.external.device.stacking;

import eu.robojob.irscw.external.device.DeviceSettings;
import eu.robojob.irscw.external.device.stacking.BasicStackPlate.WorkPieceOrientation;
import eu.robojob.irscw.workpiece.WorkPiece;
import eu.robojob.irscw.workpiece.WorkPiece.Type;
import eu.robojob.irscw.workpiece.WorkPieceDimensions;

public class BasicStackPlateSettings extends DeviceSettings {

	private WorkPiece workPiece;
	private WorkPieceOrientation orientation;
	private Integer amount;
	
	public BasicStackPlateSettings(WorkPieceDimensions dimensions, WorkPieceOrientation orientation, Integer amount) {
		this(new WorkPiece(WorkPiece.Type.RAW, dimensions), orientation, amount);
	}
	
	public BasicStackPlateSettings(WorkPiece workPiece, WorkPieceOrientation orientation, Integer amount) {
		this.amount = amount;
		this.orientation = orientation;
		this.workPiece = workPiece;
	}

	public Integer getAmount() {
		return amount;
	}

	public void setDimensions(WorkPieceDimensions dimensions) {
		if (workPiece == null) {
			workPiece = new WorkPiece(Type.RAW, dimensions);
		} else {
			this.workPiece.setDimensions(dimensions);
		}
	}

	public void setOrientation(WorkPieceOrientation orientation) {
		this.orientation = orientation;
	}

	public void setAmount(Integer amount) {
		this.amount = amount;
	}
	
	public WorkPiece getWorkPiece() {
		return this.workPiece;
	}

	public WorkPieceOrientation getOrientation() {
		return orientation;
	}
}