package eu.robojob.millassist.ui.general.model;

import java.util.ArrayList;
import java.util.List;

import eu.robojob.millassist.external.device.AbstractDevice;
import eu.robojob.millassist.external.device.DeviceSettings;
import eu.robojob.millassist.external.device.EDeviceGroup;
import eu.robojob.millassist.process.AbstractProcessStep;
import eu.robojob.millassist.process.InterventionStep;
import eu.robojob.millassist.process.PickStep;
import eu.robojob.millassist.process.ProcessingStep;
import eu.robojob.millassist.process.PutStep;

/**
 * This class represents the usage of a device object in the processflow. It allows us to use multiple the same device more than once in the same flow. 
 * This can easily be seen, because the DeviceInformation object holds an unique index (step index of the flow) with all the deviceSettings, 
 * deviceActionSettings (pick/put/processing), etc attached. These settings are, like the index, unique for every step. So if an extra step is needed,
 * a new DeviceInformation object needs to be created with the necessary information.
 */
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
	public EDeviceGroup getType() {
		if (device != null) {
			return device.getType();
		} else {
			if (index < getIndexOfFirstCNCMachine()) {
				return EDeviceGroup.PRE_PROCESSING;
			} else if (index == getIndexOfFirstCNCMachine()) {
				return EDeviceGroup.CNC_MACHINE;
			} else {
				return EDeviceGroup.POST_PROCESSING;
			}
		}
	}
	
	public int getIndexOfFirstCNCMachine() {
		return flowAdapter.getCNCMachineIndex();
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
