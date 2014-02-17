package eu.robojob.millassist.ui.general.model;

import java.util.ArrayList;
import java.util.List;

import eu.robojob.millassist.external.device.AbstractDevice;
import eu.robojob.millassist.external.device.DeviceSettings;
import eu.robojob.millassist.external.device.DeviceType;
import eu.robojob.millassist.external.device.stacking.bin.OutputBin;
import eu.robojob.millassist.external.device.stacking.conveyor.normal.Conveyor;
import eu.robojob.millassist.external.device.stacking.stackplate.BasicStackPlate;
import eu.robojob.millassist.process.AbstractProcessStep;
import eu.robojob.millassist.process.InterventionStep;
import eu.robojob.millassist.process.PickStep;
import eu.robojob.millassist.process.ProcessingStep;
import eu.robojob.millassist.process.PutStep;

public class DeviceInformation {

	private int index;
	private AbstractDevice device;
	private PutStep putStep;
	private InterventionStep interventionStepBeforePick;
	private ProcessingStep processingStep;
	private InterventionStep interventionStepAfterPut;
	private PickStep pickStep;
	private ProcessFlowAdapter flowAdapter;
	private DeviceSettings deviceSettings;

	public DeviceInformation(final int index, final ProcessFlowAdapter flowAdapter, final AbstractDevice device, final PutStep putStep, final InterventionStep interventionStepBeforePick, 
			final ProcessingStep processingStep, final InterventionStep interventionStepAfterPut, final PickStep pickStep, final DeviceSettings deviceSettings) {
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
	
	public DeviceInformation(final int index, final ProcessFlowAdapter flowAdapter) {
		this(index, flowAdapter, null, null, null, null, null, null, null);
	}
	
	//TODO review
	public DeviceType getType() {
		if (device != null) {
			return device.getType();
		} else {
			if ((index == 0) || (index == (flowAdapter.getDeviceStepCount() - 1))) {	// first or last device
				if (device instanceof BasicStackPlate) {
					return DeviceType.STACKING;
				} else if (device instanceof Conveyor) {
					return DeviceType.CONVEYOR;
				} else if (device instanceof OutputBin) {
					return DeviceType.OUTPUT_BIN;
				} else if (device instanceof eu.robojob.millassist.external.device.stacking.conveyor.eaton.Conveyor) {
					return DeviceType.CONVEYOR_EATON;
				} else {
					throw new IllegalStateException("Unkown device type: " + device);
				}
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

	public void setIndex(final int index) {
		this.index = index;
	}

	public AbstractDevice getDevice() {
		return device;
	}

	public void setDevice(final AbstractDevice device) {
		this.device = device;
	}

	public PutStep getPutStep() {
		return putStep;
	}

	public void setPutStep(final PutStep putStep) {
		this.putStep = putStep;
		setDevice(putStep.getDevice());
	}


	public InterventionStep getInterventionStepBeforePick() {
		return interventionStepBeforePick;
	}

	public void setInterventionStepBeforePick(final InterventionStep interventionStepBeforePick) {
		this.interventionStepBeforePick = interventionStepBeforePick;
		setDevice(interventionStepBeforePick.getDevice());
	}

	public ProcessingStep getProcessingStep() {
		return processingStep;
	}

	public void setProcessingStep(final ProcessingStep processingStep) {
		this.processingStep = processingStep;
		setDevice(processingStep.getDevice());
	}

	public InterventionStep getInterventionStepAfterPut() {
		return interventionStepAfterPut;
	}

	public void setInterventionStepAfterPut(final InterventionStep interventionStepAfterPut) {
		this.interventionStepAfterPut = interventionStepAfterPut;
		setDevice(interventionStepAfterPut.getDevice());
	}

	public PickStep getPickStep() {
		return pickStep;
	}

	public void setPickStep(final PickStep pickStep) {
		this.pickStep = pickStep;
		setDevice(pickStep.getDevice());
	}
	
	public boolean hasPutStep() {
		if (putStep != null) {
			return true;
		}
		return false;
	}
	
	public boolean hasInterventionStepBeforePick() {
		if (interventionStepBeforePick != null) {
			return true;
		}
		return false;
	}
	
	public boolean hasProcessingStep() {
		if (processingStep != null) {
			return true;
		}
		return false;
	}
	
	public boolean hasInterventionStepAfterPut() {
		if (interventionStepAfterPut != null) {
			return true;
		} 
		return false;
	}
	
	public boolean hasPickStep() {
		if (pickStep != null) {
			return true;
		}
		return false;
	}

	public DeviceSettings getDeviceSettings() {
		return deviceSettings;
	}

	public void setDeviceSettings(final DeviceSettings deviceSettings) {
		this.deviceSettings = deviceSettings;
	}

	public List<AbstractProcessStep> getSteps() {
		List<AbstractProcessStep> steps = new ArrayList<AbstractProcessStep>();
		if (hasPickStep()) {
			steps.add(pickStep);
		}
		if (hasPutStep()) {
			steps.add(putStep);
		}
		if (hasProcessingStep()) {
			steps.add(processingStep);
		}
		if (hasInterventionStepAfterPut()) {
			steps.add(interventionStepAfterPut);
		}
		if (hasInterventionStepBeforePick()) {
			steps.add(interventionStepBeforePick);
		}
		return steps;
	}
	
}
