package eu.robojob.millassist.external.device.stacking.conveyor;

import eu.robojob.millassist.external.device.DeviceSettings;
import eu.robojob.millassist.workpiece.WorkPiece;

public class ConveyorSettings extends DeviceSettings {

	private WorkPiece rawWorkPiece;
	private WorkPiece finishedWorkPiece;
	private int amount;
	private float offsetSupport1;
	private float offsetOtherSupports;
	
	public ConveyorSettings(final WorkPiece rawWorkPiece, final WorkPiece finishedWorkPiece, final int amount, 
			final float offsetSupport1, final float offsetOtherSupports) {
		this.rawWorkPiece = rawWorkPiece;
		this.finishedWorkPiece = finishedWorkPiece;
		this.amount = amount;
		this.offsetSupport1 = offsetSupport1;
		this.offsetOtherSupports = offsetOtherSupports;
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

	public float getOffsetSupport1() {
		return offsetSupport1;
	}

	public void setOffsetSupport1(final float offsetSupport1) {
		this.offsetSupport1 = offsetSupport1;
	}

	public float getOffsetOtherSupports() {
		return offsetOtherSupports;
	}

	public void setOffsetOtherSupports(final float offsetOtherSupports) {
		this.offsetOtherSupports = offsetOtherSupports;
	}

}
