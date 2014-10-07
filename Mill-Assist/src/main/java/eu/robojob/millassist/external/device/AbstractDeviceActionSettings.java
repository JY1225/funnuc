package eu.robojob.millassist.external.device;

import eu.robojob.millassist.process.AbstractProcessStep;
import eu.robojob.millassist.workpiece.WorkPiece;

public abstract class AbstractDeviceActionSettings<T extends AbstractProcessStep> {
	
	private AbstractDevice device;
	private WorkArea workArea;
	private WorkPiece workPiece;
	private T step;
	private int id;
	
	public AbstractDeviceActionSettings(final AbstractDevice device, final WorkArea workArea, final WorkPiece workPiece) {
		setDevice(device);
		setWorkArea(workArea);
		setWorkPiece(workPiece);
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
	
	public WorkPiece getWorkPiece() {
		return workPiece;
	}
	
	public void setWorkPiece(final WorkPiece workPiece) {
		this.workPiece = workPiece;
	}
}