package eu.robojob.irscw.ui.main.model;

import eu.robojob.irscw.external.device.DeviceType;
import eu.robojob.irscw.process.AbstractProcessStep;
import eu.robojob.irscw.process.InterventionStep;
import eu.robojob.irscw.process.PickStep;
import eu.robojob.irscw.process.ProcessFlow;
import eu.robojob.irscw.process.ProcessStepType;
import eu.robojob.irscw.process.ProcessingStep;
import eu.robojob.irscw.process.PutStep;

public class ProcessFlowAdapter {

	private ProcessFlow processFlow;
	private static final int maxDeviceCount = 4;
	
	public ProcessFlowAdapter(ProcessFlow processFlow) {
		this.processFlow = processFlow;
	}
	
	public int getDeviceStepCount() {
		int deviceSteps = 0;
		
		for (int i = 0; i < processFlow.getProcessSteps().size(); i++) {
			AbstractProcessStep step = processFlow.getStep(i);
			if(step.getType() == ProcessStepType.PICK_STEP) {
				deviceSteps++;
			}
		}
		
		// add one step for the last put-step
		deviceSteps++;
		
		return deviceSteps;
	}

	public int getMaxDevices() {
		return maxDeviceCount;
	}
	
	public int getTransportStepCount() {
		return getDeviceStepCount()-1;
	}
	
	public DeviceInformation getDeviceInformation(int index) {
		if ((index > getDeviceStepCount()) || (index < 0)) {
			throw new IllegalArgumentException("Incorrect index");
		}
		
		DeviceInformation deviceInformation = new DeviceInformation(index, this);
				
		int curDevIndex = 0;
		for (int i = 0; i < processFlow.getProcessSteps().size(); i++) {
			AbstractProcessStep step = processFlow.getStep(i);
			if (step.getType() == ProcessStepType.PUT_STEP) {
				curDevIndex++;
			}
			if (curDevIndex == index) {
				switch(step.getType()) {
					case PICK_STEP:
						deviceInformation.setPickStep((PickStep) step);
						break;
					case PUT_STEP:
						deviceInformation.setPutStep((PutStep) step);
						break;
					case INTERVENTION_STEP:
						// as the index is greater than zero, there always is a previous step!
						if ((i > 0) && (processFlow.getStep(i-1).getType() == ProcessStepType.PUT_STEP)) {
							deviceInformation.setInterventionStepAfterPut((InterventionStep) step);
						} else {
							deviceInformation.setInterventionStepBeforePick((InterventionStep) step);
						}
						break;
					case PROCESSING_STEP:
						deviceInformation.setProcessingStep((ProcessingStep) step);
						break;
				}
			}
		}
		
		deviceInformation.setDeviceSettings(processFlow.getDeviceSettings(deviceInformation.getDevice()));
		
		return deviceInformation;
	}
	
	public TransportInformation getTransportInformation(int index) {
		if ((index > getTransportStepCount()) || (index < 0)) {
			throw new IllegalArgumentException("Incorrect index");
		}
		
		TransportInformation transportInformation = new TransportInformation();
		transportInformation.setIndex(index);
		
		int curTranspIndex = 0;
		for (int i = 0; i < processFlow.getProcessSteps().size(); i++) {
			AbstractProcessStep step = processFlow.getStep(i);
			if (curTranspIndex == index) {
				if (step.getType() == ProcessStepType.PICK_STEP) {
					transportInformation.setPickStep((PickStep) step);
					if ((i>=1)&&(processFlow.getProcessSteps().get(i-1).getType() == ProcessStepType.INTERVENTION_STEP)) {
						transportInformation.setInterventionBeforePick((InterventionStep) processFlow.getProcessSteps().get(i-1));
					}
				} else if (step.getType() == ProcessStepType.PUT_STEP) {
					transportInformation.setPutStep((PutStep) step);
					if ((i < processFlow.getProcessSteps().size() -1) && (processFlow.getStep(i+1).getType() == ProcessStepType.INTERVENTION_STEP)) {
						transportInformation.setInterventionAfterPut((InterventionStep) processFlow.getStep(i+1));
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
	
	public void addInterventionStepAfterPut(int transportIndex) {
		addInterventionStepAfterPut(getTransportInformation(transportIndex));
	}
	
	public void addInterventionStepAfterPut(TransportInformation transportInfo) {
		InterventionStep intervention = new InterventionStep(transportInfo.getPutStep().getDevice(), null, 0);
		processFlow.addStepAfter(transportInfo.getPutStep(), intervention);
	}
	
	public void addInterventionStepBeforePick(int transportIndex) {
		addInterventionStepBeforePick(getTransportInformation(transportIndex));
	}
	
	public void addInterventionStepBeforePick(TransportInformation transportInfo) {
		InterventionStep intervention = new InterventionStep(transportInfo.getPickStep().getDevice(), null, 0);
		processFlow.addStepBefore(transportInfo.getPickStep(), intervention);
	}
	
	public void removeInterventionStepBeforePick(int transportIndex) {
		removeInterventionStepBeforePick(getTransportInformation(transportIndex));
	}
	
	public void removeInterventionStepAfterPut(int transportIndex) {
		removeInterventionStepBeforePick(getTransportInformation(transportIndex));
	}
	
	public void removeInterventionStepBeforePick(TransportInformation transportInfo) {
		processFlow.removeStep(transportInfo.getInterventionBeforePick());
	}
	
	public void removeInterventionStepAfterPut(TransportInformation transportInfo) {
		processFlow.removeStep(transportInfo.getInterventionAfterPut());
	}
	
	public void addDeviceSteps(int transportIndex) {
		if (getDeviceStepCount() < maxDeviceCount) {
			DeviceInformation deviceInfo = getDeviceInformation(transportIndex);
			TransportInformation transportInfo = getTransportInformation(transportIndex);
			PutStep putStep = new PutStep(processFlow, deviceInfo.getPickStep().getRobot(), null, null, transportInfo.getPutStep().getRobotSettings());
			ProcessingStep processingStep = new ProcessingStep(null, null);
			PickStep pickStep = new PickStep(processFlow, deviceInfo.getPickStep().getRobot(), null, null, deviceInfo.getPickStep().getRobot().getDefaultPickSettings());
			//transportInfo.getPutStep().setRobotSettings(putStep.getRobot().getDefaultPutSettings());
			processFlow.addStepAfter(deviceInfo.getPickStep(), putStep);
			processFlow.addStepAfter(putStep, processingStep);
			processFlow.addStepAfter(processingStep, pickStep);
		} else {
			throw new IllegalStateException("Amount of device-steps would be greater than maximum.");
		}
	}
	
	public void removeDeviceSteps(int deviceIndex) {
		DeviceInformation deviceInfo = getDeviceInformation(deviceIndex);
		TransportInformation transportBefore = getTransportInformation(deviceIndex-1);
		TransportInformation transportAfter = getTransportInformation(deviceIndex);
		transportAfter.getPutStep().setRobotSettings(transportBefore.getPutStep().getRobotSettings());
		processFlow.removeSteps(deviceInfo.getSteps());
	}
	
	public int getCNCMachineIndex() {
		for (int i = 0; i < getDeviceStepCount(); i++) {
			//TODO refactor this, kind of a hack...
			if ((getDeviceInformation(i).getDevice() != null) && (getDeviceInformation(i).getDevice().getType() == DeviceType.CNC_MACHINE)) {
				return i;
			}
		}
		return -1;
	}
	
	public boolean canAddDevice() {
		if (getDeviceStepCount() < maxDeviceCount) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean canRemoveDevice() {
		for (int i = 0; i < getDeviceStepCount(); i++) {
			DeviceInformation info = getDeviceInformation(i);
			if ((info.getType() == DeviceType.POST_PROCESSING) || (info.getType() == DeviceType.PRE_PROCESSING)) {
				return true;
			}
		}
		return false;
	}
	
	public ProcessFlow getProcessFlow() {
		return processFlow;
	}
}
