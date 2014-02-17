package eu.robojob.millassist.external.device.stacking.conveyor.eaton;

import eu.robojob.millassist.external.device.DeviceSettings;
import eu.robojob.millassist.workpiece.WorkPiece;

public class ConveyorSettings extends DeviceSettings {

	private WorkPiece rawWorkPiece;
	private WorkPiece finishedWorkPiece;
	private int amount;
	
	public ConveyorSettings(final WorkPiece rawWorkPiece, final WorkPiece finishedWorkPiece, final int amount) {
		this.rawWorkPiece = rawWorkPiece;
		this.finishedWorkPiece = finishedWorkPiece;
		this.amount = amount;
	}

	public WorkPiece getRawWorkPiece() {
		return rawWorkPiece;
	}

	public void setRawWorkPiece(final WorkPiece rawWorkPiece) {
		this.rawWorkPiece = rawWorkPiece;
	}

	public WorkPiece getFinishedWorkPiece() {
		return finishedWorkPiece;
	}

	public void setFinishedWorkPiece(final WorkPiece finishedWorkPiece) {
		this.finishedWorkPiece = finishedWorkPiece;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(final int amount) {
		this.amount = amount;
	}

}
