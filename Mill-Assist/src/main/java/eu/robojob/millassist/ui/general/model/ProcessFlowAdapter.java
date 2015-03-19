package eu.robojob.millassist.ui.general.model;

import eu.robojob.millassist.external.device.AbstractDevice;
import eu.robojob.millassist.external.device.DeviceInterventionSettings;
import eu.robojob.millassist.external.device.DeviceSettings;
import eu.robojob.millassist.external.device.EDeviceGroup;
import eu.robojob.millassist.external.device.SimpleWorkArea;
import eu.robojob.millassist.external.device.stacking.stackplate.AbstractStackPlateDeviceSettings;
import eu.robojob.millassist.process.AbstractProcessStep;
import eu.robojob.millassist.process.InterventionStep;
import eu.robojob.millassist.process.PickStep;
import eu.robojob.millassist.process.ProcessFlow;
import eu.robojob.millassist.process.ProcessStepType;
import eu.robojob.millassist.process.ProcessingStep;
import eu.robojob.millassist.process.PutStep;
import eu.robojob.millassist.process.event.DataChangedEvent;
import eu.robojob.millassist.workpiece.WorkPiece;
import eu.robojob.millassist.workpiece.WorkPiece.Type;

/**
 * This class extends the functionalities of the ProcessFlow class and organizes its information to simplify interaction between UI classes and ProcessFlow
 * Should not contain state
 */
public class ProcessFlowAdapter {

	//TODO review the max amount - this can disappear
	//private static final int MAX_DEVICE_AMOUNT = 6;
	private static final int MAX_DEVICE_AMOUNT = 100;
	private boolean needsRevisitWP = false;
	
	private ProcessFlow processFlow;
	
	public ProcessFlowAdapter(final ProcessFlow processFlow) {
		this.processFlow = processFlow;
	}
	
	public int getDeviceStepCount() {
		int deviceSteps = 0;
		for (int i = 0; i < processFlow.getProcessSteps().size(); i++) {
			AbstractProcessStep step = processFlow.getStep(i);
			if (step.getType() == ProcessStepType.PICK_STEP) {
				deviceSteps++;
			}
		}
		// add one step for the last put-step
		deviceSteps++;
		return deviceSteps;
	}

	public int getMaxDevices() {
		return MAX_DEVICE_AMOUNT;
	}
	
	public int getTransportStepCount() {
		return getDeviceStepCount() - 1;
	}
	
	public DeviceInformation getDeviceInformation(final int index) {
		if ((index > getDeviceStepCount()) || (index < 0)) {
			throw new IllegalArgumentException("Incorrect index [" + index + "], limits are [0, " + getDeviceStepCount() + "].");
		}
		DeviceInformation deviceInformation = new DeviceInformation(index, this);
		int curDevIndex = 0;
		for (int i = 0; i < processFlow.getProcessSteps().size(); i++) {
			AbstractProcessStep step = processFlow.getStep(i);
			//First device does not have a PUT_STEP
			if (step.getType() == ProcessStepType.PUT_STEP) {
				curDevIndex++;
			}
			if (curDevIndex == index) {
				switch (step.getType()) {
					case PICK_STEP:
						deviceInformation.setPickStep((PickStep) step);
						break;
					case PUT_STEP:
						deviceInformation.setPutStep((PutStep) step);
						break;
					case INTERVENTION_STEP:
						// as the index is greater than zero, there always is a previous step!
						if ((i > 0) && (processFlow.getStep(i - 1).getType() == ProcessStepType.PUT_STEP)) {
							deviceInformation.setInterventionStepAfterPut((InterventionStep) step);
						} else {
							deviceInformation.setInterventionStepBeforePick((InterventionStep) step);
						}
						break;
					case PROCESSING_STEP:
						deviceInformation.setProcessingStep((ProcessingStep) step);
						break;
					default:
						throw new IllegalStateException("Unknown step type.");
				}
			}
		}
		deviceInformation.setDeviceSettings(processFlow.getDeviceSettings(deviceInformation.getDevice()));
		return deviceInformation;
	}
	
	public TransportInformation getTransportInformation(final int index) {
		if ((index > getTransportStepCount()) || (index < 0)) {
			throw new IllegalArgumentException("Incorrect index [" + index + "], limits are [0, " + getTransportStepCount() + "].");
		}
		TransportInformation transportInformation = new TransportInformation();
		transportInformation.setIndex(index);
		int curTranspIndex = 0;
		for (int i = 0; i < processFlow.getProcessSteps().size(); i++) {
			AbstractProcessStep step = processFlow.getStep(i);
			if (curTranspIndex == index) {
				if (step.getType() == ProcessStepType.PICK_STEP) {
					transportInformation.setPickStep((PickStep) step);
					if ((i >= 1) && (processFlow.getProcessSteps().get(i - 1).getType() == ProcessStepType.INTERVENTION_STEP)) {
						transportInformation.setInterventionBeforePick((InterventionStep) processFlow.getProcessSteps().get(i - 1));
					}
				} else if (step.getType() == ProcessStepType.PUT_STEP) {
					transportInformation.setPutStep((PutStep) step);
					if ((i < processFlow.getProcessSteps().size() - 1) && (processFlow.getStep(i + 1).getType() == ProcessStepType.INTERVENTION_STEP)) {
						transportInformation.setInterventionAfterPut((InterventionStep) processFlow.getStep(i + 1));
					}
				}
			}
			if (step.getType() == ProcessStepType.PUT_STEP) {
				curTranspIndex++;
			}
		}
		transportInformation.setRobotSettings(processFlow.getRobotSettings(transportInformation.getRobot()));
		return transportInformation;
	}
	
	public void addInterventionStepAfterPut(final int transportIndex) {
		addInterventionStepAfterPut(getTransportInformation(transportIndex));
	}
	
	public void addInterventionStepAfterPut(final TransportInformation transportInfo) {
		AbstractDevice device = transportInfo.getPutStep().getDevice();
		InterventionStep intervention = new InterventionStep(new DeviceInterventionSettings(device, transportInfo.getPutStep().getDeviceSettings().getWorkArea()), 0);
		processFlow.addStepAfter(transportInfo.getPutStep(), intervention);
	}
	
	public void addInterventionStepBeforePick(final int transportIndex) {
		addInterventionStepBeforePick(getTransportInformation(transportIndex));
	}
	
	public void addInterventionStepBeforePick(final TransportInformation transportInfo) {
		AbstractDevice device = transportInfo.getPickStep().getDevice();
		InterventionStep intervention = new InterventionStep(new DeviceInterventionSettings(device, transportInfo.getPickStep().getDeviceSettings().getWorkArea()), 0);
		processFlow.addStepBefore(transportInfo.getPickStep(), intervention);
	}
	
	public void addDeviceSteps(final int transportIndex, final DeviceInformation deviceInfo) {
		if (canAddDevice()/* && (transportIndex < getCNCMachineIndex())*/) {			
			DeviceInformation prevDeviceInfo = getDeviceInformation(transportIndex);
			processFlow.addStepAfter(prevDeviceInfo.getPickStep(), deviceInfo.getPutStep());
			if (deviceInfo.hasProcessingStep()) {
				processFlow.addStepAfter(deviceInfo.getPutStep(), deviceInfo.getProcessingStep());
				processFlow.addStepAfter(deviceInfo.getProcessingStep(), deviceInfo.getPickStep());
			} else {
				processFlow.addStepAfter(prevDeviceInfo.getPutStep(), deviceInfo.getPickStep());
			}
			processFlow.processProcessFlowEvent(new DataChangedEvent(processFlow, deviceInfo.getPickStep(), true));
		} else {
			throw new IllegalStateException("Amount of device-steps " + (getDeviceStepCount() + 1) + " would be greater than maximum: " + MAX_DEVICE_AMOUNT);
		}
	}
	
	public void removeDeviceSteps(final int deviceIndex) {
		DeviceInformation deviceInfo = getDeviceInformation(deviceIndex);
		processFlow.removeSteps(deviceInfo.getSteps());
	}
	
	//TODO could be optimized - review if both first and lastCNC methods are needed
	public int getCNCMachineIndex() {
		for (int i = 0; i < getDeviceStepCount(); i++) {
			AbstractDevice device = getDeviceInformation(i).getDevice();
			if ((device != null) && (device.getType() == EDeviceGroup.CNC_MACHINE)) {
				return i;
			}
		}
		return -1;
	}
	
	//TODO could be optimized - opslaan als variabele en aanpassen bij toevoegen/verwijderen machine? - efficienter
	public int getLastCNCMachineIndex() {
		int indexLastCNC = -1;
		for (int i = 0; i < getDeviceStepCount(); i++) {
			AbstractDevice device = getDeviceInformation(i).getDevice();
			if ((device != null) && (device.getType() == EDeviceGroup.CNC_MACHINE)) {
				indexLastCNC = i;
			}
		}
		return indexLastCNC;
	}
	
	public int getNbCNCMachinesInFlow() {
		int count = 0;
		for (int i = 0; i < getDeviceStepCount(); i++) {
			AbstractDevice device = getDeviceInformation(i).getDevice();
			if ((device != null) && (device.getType() == EDeviceGroup.CNC_MACHINE)) {
				count++;
			}
		}
		return count;
	}
	
	public int getCNCNbInFlow(int indexCNCMachine) {
		int count = 0;
		for (int i = 0; i < getDeviceStepCount(); i++) {
			AbstractDevice device = getDeviceInformation(i).getDevice();
			if ((device != null) && (device.getType() == EDeviceGroup.CNC_MACHINE)) {
				count++;
			}
			if (i == indexCNCMachine) {
				return count;
			}
		}
		return count;
	}
	
	public boolean canAddDevice() {
		if (getDeviceStepCount() < MAX_DEVICE_AMOUNT) {
			return true;
		}
		return false;
	}
	
	public boolean canRemoveDevice() {
		for (int i = 0; i < getDeviceStepCount(); i++) {
			DeviceInformation info = getDeviceInformation(i);
			if ((info.getType() == EDeviceGroup.POST_PROCESSING) || (info.getType() == EDeviceGroup.PRE_PROCESSING)) {
				return true;
			}
		}
		return false;
	}
	
	public ProcessFlow getProcessFlow() {
		return processFlow;
	}
	
	public int getDeviceIndex(final ProcessingStep processingStep) {
		for (int i = 0; i < getDeviceStepCount(); i++) {
			DeviceInformation deviceInfo = getDeviceInformation(i);
			if ((deviceInfo.hasProcessingStep()) && (deviceInfo.getProcessingStep().equals(processingStep))) {
				return i;
			}
		}
		return -1;
	}
	
	public int getTransportIndex(final PickStep pickStep) {
		for (int i = 0; i < getTransportStepCount(); i++) {
			TransportInformation transportInfo = getTransportInformation(i);
			if ((transportInfo.getPickStep() != null) && (transportInfo.getPickStep().equals(pickStep))) {
				return i;
			}
		}
		return -1;
	}
	
	public int getTransportIndex(final PutStep putStep) {
		for (int i = 0; i < getTransportStepCount(); i++) {
			TransportInformation transportInfo = getTransportInformation(i);
			if ((transportInfo.getPutStep() != null) && (transportInfo.getPutStep().equals(putStep))) {
				return i;
			}
		}
		return -1;
	}
	
	//Only use is when CNC machine is removed from the flow
	public void updateCNCMachineWorkArea() {
		SimpleWorkArea workArea = getDeviceInformation(getLastCNCMachineIndex()).getPutStep().getDeviceSettings().getWorkArea();
		workArea.setInUse(false);
	}
	
	public void updateFinalWorkPieceFlow() {
		DeviceSettings deviceSettingsLastWP = getDeviceInformation(getDeviceStepCount() - 1).getDeviceSettings();
		WorkPiece finishedWorkPiece = getDeviceInformation(getLastCNCMachineIndex()).getPickStep().getRobotSettings().getWorkPiece();
		finishedWorkPiece.setType(Type.FINISHED);
		if (deviceSettingsLastWP instanceof AbstractStackPlateDeviceSettings) {
			((AbstractStackPlateDeviceSettings) deviceSettingsLastWP).setFinishedWorkPiece(finishedWorkPiece);
		} else if (deviceSettingsLastWP instanceof eu.robojob.millassist.external.device.stacking.conveyor.normal.ConveyorSettings) {
			((eu.robojob.millassist.external.device.stacking.conveyor.normal.ConveyorSettings) deviceSettingsLastWP).setFinishedWorkPiece(finishedWorkPiece);
		} else if (deviceSettingsLastWP instanceof eu.robojob.millassist.external.device.stacking.conveyor.eaton.ConveyorSettings) {
			((eu.robojob.millassist.external.device.stacking.conveyor.eaton.ConveyorSettings) deviceSettingsLastWP).setFinishedWorkPiece(finishedWorkPiece);
		}
	}
	
	public void setNeedsToRevisitWorkPieces(final boolean flag) {
		this.needsRevisitWP = flag;
	}
	
	public void revisitWorkPieces() {
		if (needsRevisitWP) {
			processFlow.revisitProcessFlowWorkPieces();
			needsRevisitWP = false;
		}
	}
}
