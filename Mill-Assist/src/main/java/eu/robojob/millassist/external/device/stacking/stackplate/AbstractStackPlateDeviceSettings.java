package eu.robojob.millassist.external.device.stacking.stackplate;

import eu.robojob.millassist.external.device.DeviceSettings;
import eu.robojob.millassist.external.device.stacking.stackplate.AbstractStackPlate.WorkPieceOrientation;
import eu.robojob.millassist.workpiece.WorkPiece;

public class AbstractStackPlateDeviceSettings extends DeviceSettings {

	private WorkPiece rawWorkPiece;
	private WorkPiece finishedWorkPiece;
	//TODO maybe in the future, a list of intermediate half-finished workpieces could be added
	private WorkPieceOrientation orientation;
	private int layers;
	private int amount;
	private float studHeight;
	private int gridId;
	
	protected AbstractStackPlateDeviceSettings(final WorkPiece workPiece, final WorkPiece finishedWorkPiece, final WorkPieceOrientation orientation, final int layers, final int amount) {
		this.layers = layers;
		this.amount = amount;
		this.orientation = orientation;
		this.rawWorkPiece = workPiece;
		this.finishedWorkPiece = finishedWorkPiece;
		this.gridId = 0;
	}
	
	public AbstractStackPlateDeviceSettings(final WorkPiece workPiece, final WorkPiece finishedWorkPiece, final WorkPieceOrientation orientation, final int layers, final int amount, final float studHeight) {
		this(workPiece, finishedWorkPiece, orientation, layers, amount);
		this.studHeight = studHeight;
	}
	
	public AbstractStackPlateDeviceSettings(final WorkPiece workPiece, final WorkPiece finishedWorkPiece, final WorkPieceOrientation orientation, final int layers, final int amount, final float studHeight, int gridId) {
		this(workPiece, finishedWorkPiece, orientation, layers, amount, studHeight);
		this.gridId = gridId;
	}
	
	public int getGridId() {
		return this.gridId;
	}
	
	public void setGridId(int gridId) {
		this.gridId = gridId;
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
	
	public float getStudHeight() {
		return studHeight;
	}

	public void setStudHeight(float studHeight) {
		this.studHeight = studHeight;
	}

}
