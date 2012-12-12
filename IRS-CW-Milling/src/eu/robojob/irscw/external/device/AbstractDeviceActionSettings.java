package eu.robojob.irscw.external.device;

import eu.robojob.irscw.process.AbstractProcessStep;

public abstract class AbstractDeviceActionSettings<T extends AbstractProcessStep> {
	
	private WorkArea workArea;
	private T step;
	
	public AbstractDeviceActionSettings(final WorkArea workArea) {
		setWorkArea(workArea);
	}
	
	public void setStep(final T step) {
		this.step = step;
	}

	public T getStep() {
		return step;
	}
	
	public WorkArea getWorkArea() {
		return workArea;
	}
	
	public void setWorkArea(final WorkArea workArea) {
		this.workArea = workArea;
	}
}