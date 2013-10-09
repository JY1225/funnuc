package eu.robojob.millassist.external.device.stacking.stackplate;

import eu.robojob.millassist.external.device.DeviceSettings;
import eu.robojob.millassist.external.device.stacking.stackplate.BasicStackPlate.WorkPieceOrientation;
import eu.robojob.millassist.workpiece.WorkPiece;

public class BasicStackPlateSettings extends DeviceSettings {

	private WorkPiece rawWorkPiece;
	private WorkPiece finishedWorkPiece;
	//TODO maybe in the future, a list of intermediate half-finished workpieces could be added
	private WorkPieceOrientation orientation;
	private int layers;
	private int amount;
	
	public BasicStackPlateSettings(final WorkPiece workPiece, final WorkPiece finishedWorkPiece, final WorkPieceOrientation orientation, final int layers, final int amount) {
		this.layers = layers;
		this.amount = amount;
		this.orientation = orientation;
		this.rawWorkPiece = workPiece;
		this.finishedWorkPiece = finishedWorkPiece;
	}

	public int getAmount() {
		return amount;
	}

	public void setRawWorkPiece(final WorkPiece rawWorkPiece) {
		this.rawWorkPiece = rawWorkPiece;
	}
	
	public void setFinishedWorkPiece(final WorkPiece finishedWorkPiece) {
		this.finishedWorkPiece = finishedWorkPiece;
	}

	public void setOrientation(final WorkPieceOrientation orientation) {
		this.orientation = orientation;
	}

	public void setAmount(final int amount) {
		this.amount = amount;
	}
	
	public int getLayers() {
		return layers;
	}

	public void setLayers(final int layers) {
		this.layers = layers;
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