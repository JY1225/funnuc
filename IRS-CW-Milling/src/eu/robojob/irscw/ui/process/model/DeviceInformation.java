package eu.robojob.irscw.ui.process.model;

import eu.robojob.irscw.external.device.AbstractDevice;
import eu.robojob.irscw.process.InterventionStep;
import eu.robojob.irscw.process.PickStep;
import eu.robojob.irscw.process.ProcessingStep;
import eu.robojob.irscw.process.PutStep;

public class DeviceInformation {

	private AbstractDevice device;
	private PutStep putStep;
	private InterventionStep interventionStepBeforePick;
	private ProcessingStep processingStep;
	private InterventionStep interventionStepAfterPut;
	private PickStep pickStep;

	public DeviceInformation(AbstractDevice device, PutStep putStep, InterventionStep interventionStepBeforePick, ProcessingStep processingStep,
			InterventionStep interventionStepAfterPut, PickStep pickStep) {
		this.device = device;
		this.putStep = putStep;
		this.interventionStepBeforePick = interventionStepBeforePick;
		this.processingStep = processingStep;
		this.interventionStepAfterPut = interventionStepAfterPut;
		this.pickStep = pickStep;
	}
	
	public DeviceInformation() {
		this(null, null, null, null, null, null);
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
		setDevice(putStep.getDevice());
	}


	public InterventionStep getInterventionStepBeforePick() {
		return interventionStepBeforePick;
	}

	public void setInterventionStepBeforePick(
			InterventionStep interventionStepBeforePick) {
		this.interventionStepBeforePick = interventionStepBeforePick;
		setDevice(interventionStepBeforePick.getDevice());
	}

	public ProcessingStep getProcessingStep() {
		return processingStep;
	}

	public void setProcessingStep(ProcessingStep processingStep) {
		this.processingStep = processingStep;
		setDevice(processingStep.getDevice());
	}

	public InterventionStep getInterventionStepAfterPut() {
		return interventionStepAfterPut;
	}

	public void setInterventionStepAfterPut(
			InterventionStep interventionStepAfterPut) {
		this.interventionStepAfterPut = interventionStepAfterPut;
		setDevice(interventionStepAfterPut.getDevice());
	}

	public PickStep getPickStep() {
		return pickStep;
	}

	public void setPickStep(PickStep pickStep) {
		this.pickStep = pickStep;
		setDevice(pickStep.getDevice());
	}
	
	public boolean hasPutStep() {
		if (putStep != null) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean hasInterventionStepBeforePick() {
		if (interventionStepBeforePick != null) {
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
	
	public boolean hasInterventionStepAfterPut() {
		if (interventionStepAfterPut != null) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean hasPickStep() {
		if (putStep != null) {
			return true;
		} else {
			return false;
		}
	}
	
}
