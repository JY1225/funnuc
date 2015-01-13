package eu.robojob.millassist.external.device;

import eu.robojob.millassist.process.AbstractProcessStep;

public abstract class AbstractDeviceActionSettings<T extends AbstractProcessStep> {
	
	private AbstractDevice device;
	private SimpleWorkArea workArea;
	private T step;
	private int id;
	
	public AbstractDeviceActionSettings(final AbstractDevice device, final SimpleWorkArea workArea) {
		setDevice(device);
		setWorkArea(workArea);
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

	public SimpleWorkArea getWorkArea() {
		return workArea;
	}
	
	public void setWorkArea(final SimpleWorkArea workArea) {
		this.workArea = workArea;
	}
}