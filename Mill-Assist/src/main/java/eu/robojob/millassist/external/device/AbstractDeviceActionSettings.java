package eu.robojob.millassist.external.device;

import eu.robojob.millassist.process.AbstractProcessStep;
import eu.robojob.millassist.workpiece.WorkPiece;

public abstract class AbstractDeviceActionSettings<T extends AbstractProcessStep> {
	
	private AbstractDevice device;
	private WorkArea workArea;
	private WorkPiece.Type workPieceType;
	private T step;
	private int id;
	
	public AbstractDeviceActionSettings(final AbstractDevice device, final WorkArea workArea, final WorkPiece.Type workPieceType) {
		setDevice(device);
		setWorkArea(workArea);
		setWorkPieceType(workPieceType);
	}
	
	public int getId() {
		return id;
	}

	public void setId(final int id) {
		this.id = id;
	}

	public void setStep(final T step) {
		this.step = step;
	}

	public T getStep() {
		return step;
	}
	
	public AbstractDevice getDevice() {
		return device;
	}

	public void setDevice(final AbstractDevice device) {
		this.device = device;
	}

	public WorkArea getWorkArea() {
		return workArea;
	}
	
	public void setWorkArea(final WorkArea workArea) {
		this.workArea = workArea;
	}
	
	public WorkPiece.Type getWorkPieceType() {
		return this.workPieceType;
	}
	
	public void setWorkPieceType(WorkPiece.Type workPieceType) {
		this.workPieceType = workPieceType;
	}
	
	public void updateWorkPieceType() {
		if (workPieceType.equals(WorkPiece.Type.HALF_FINISHED)) {
			setWorkPieceType(WorkPiece.Type.FINISHED);
		}
	}
}