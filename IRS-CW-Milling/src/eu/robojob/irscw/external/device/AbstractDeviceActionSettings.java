package eu.robojob.irscw.external.device;

import eu.robojob.irscw.process.AbstractProcessStep;

public abstract class AbstractDeviceActionSettings<T extends AbstractProcessStep> {
	protected WorkArea workArea;
	protected T step;
	
	public AbstractDeviceActionSettings(WorkArea workArea) {
		setWorkArea(workArea);
	}
	
	public void setStep(T step) {
		this.step = step;
	}

	public WorkArea getWorkArea() {
		return workArea;
	}
	
	public void setWorkArea(WorkArea workArea) {
		this.workArea = workArea;
	}
}