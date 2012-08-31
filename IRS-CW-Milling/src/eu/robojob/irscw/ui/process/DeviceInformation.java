package eu.robojob.irscw.ui.process;

import eu.robojob.irscw.external.device.AbstractDevice;
import eu.robojob.irscw.process.InterventionStep;
import eu.robojob.irscw.process.PickStep;
import eu.robojob.irscw.process.ProcessingStep;
import eu.robojob.irscw.process.PutStep;

public class DeviceInformation {

	private AbstractDevice device;
	private PutStep putStep;
	private InterventionStep interventionStepBefore;
	private ProcessingStep processingStep;
	private InterventionStep interventionStepAfter;
	private PickStep pickStep;

	public DeviceInformation(AbstractDevice device, PutStep putStep, InterventionStep interventionStepBefore, ProcessingStep processingStep,
			InterventionStep interventionStepAfter, PickStep pickStep) {
		this.device = device;
		this.putStep = putStep;
		this.interventionStepBefore = interventionStepBefore;
		this.processingStep = processingStep;
		this.interventionStepAfter = interventionStepAfter;
		this.pickStep = pickStep;
	}

	public AbstractDevice getDevice() {
		return device;
	}

	public void setDevice(AbstractDevice device) {
		this.device = device;
	}

	public PutStep getPutStep() {
		return putStep;
	}

	public void setPutStep(PutStep putStep) {
		this.putStep = putStep;
	}

	public InterventionStep getInterventionStepBefore() {
		return interventionStepBefore;
	}

	public void setInterventionStepBefore(InterventionStep interventionStepBefore) {
		this.interventionStepBefore = interventionStepBefore;
	}

	public ProcessingStep getProcessingStep() {
		return processingStep;
	}

	public void setProcessingStep(ProcessingStep processingStep) {
		this.processingStep = processingStep;
	}

	public InterventionStep getInterventionStepAfter() {
		return interventionStepAfter;
	}

	public void setInterventionStepAfter(InterventionStep interventionStepAfter) {
		this.interventionStepAfter = interventionStepAfter;
	}

	public PickStep getPickStep() {
		return pickStep;
	}

	public void setPickStep(PickStep pickStep) {
		this.pickStep = pickStep;
	}
	
	public boolean hasPutStep() {
		if (putStep != null) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean hasInterventionStepBefore() {
		if (interventionStepBefore != null) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean hasProcessingStep() {
		if (processingStep != null) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean hasInterventionStepAfter() {
		if (interventionStepAfter != null) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean haasPickStep() {
		if (putStep != null) {
			return true;
		} else {
			return false;
		}
	}
	
}
