package eu.robojob.irscw.ui.main.model;

import java.util.ArrayList;
import java.util.List;

import eu.robojob.irscw.external.device.AbstractDevice;
import eu.robojob.irscw.external.device.AbstractDevice.AbstractDeviceSettings;
import eu.robojob.irscw.external.device.DeviceType;
import eu.robojob.irscw.process.AbstractProcessStep;
import eu.robojob.irscw.process.InterventionStep;
import eu.robojob.irscw.process.PickStep;
import eu.robojob.irscw.process.ProcessingStep;
import eu.robojob.irscw.process.PutStep;

public class DeviceInformation {

	private int index;
	private AbstractDevice device;
	private PutStep putStep;
	private InterventionStep interventionStepBeforePick;
	private ProcessingStep processingStep;
	private InterventionStep interventionStepAfterPut;
	private PickStep pickStep;
	private ProcessFlowAdapter flowAdapter;
	private AbstractDevice.AbstractDeviceSettings deviceSettings;

	public DeviceInformation(int index, ProcessFlowAdapter flowAdapter, AbstractDevice device, PutStep putStep, InterventionStep interventionStepBeforePick, ProcessingStep processingStep,
			InterventionStep interventionStepAfterPut, PickStep pickStep, AbstractDeviceSettings deviceSettings) {
		this.index = index;
		this.flowAdapter = flowAdapter;
		this.device = device;
		this.putStep = putStep;
		this.interventionStepBeforePick = interventionStepBeforePick;
		this.processingStep = processingStep;
		this.interventionStepAfterPut = interventionStepAfterPut;
		this.pickStep = pickStep;
		this.deviceSettings = deviceSettings;
	}
	
	public DeviceInformation(int index, ProcessFlowAdapter flowAdapter) {
		this(index, flowAdapter, null, null, null, null, null, null, null);
	}
	
	public DeviceType getType() {
		if (device != null) {
			return device.getType();
		} else {
			if ((index == 0)||(index == (flowAdapter.getDeviceStepCount()-1))) {
				return DeviceType.STACKING;
			} else {
				if (index < flowAdapter.getCNCMachineIndex()) {
					return DeviceType.PRE_PROCESSING;
				} else if (index == flowAdapter.getCNCMachineIndex()) {
					return DeviceType.CNC_MACHINE;
				} else {
					return DeviceType.POST_PROCESSING;
				}
			}
		}
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
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
		if (pickStep != null) {
			return true;
		} else {
			return false;
		}
	}

	public AbstractDevice.AbstractDeviceSettings getDeviceSettings() {
		return deviceSettings;
	}

	public void setDeviceSettings(
			AbstractDevice.AbstractDeviceSettings deviceSettings) {
		this.deviceSettings = deviceSettings;
	}

	public List<AbstractProcessStep> getSteps() {
		List<AbstractProcessStep> steps = new ArrayList<AbstractProcessStep>();
		if (hasPickStep())
			steps.add(pickStep);
		if (hasPutStep())
			steps.add(putStep);
		if (hasProcessingStep())
			steps.add(processingStep);
		if (hasInterventionStepAfterPut())
			steps.add(interventionStepAfterPut);
		if (hasInterventionStepBeforePick())
			steps.add(interventionStepBeforePick);
		return steps;
	}
	
}
