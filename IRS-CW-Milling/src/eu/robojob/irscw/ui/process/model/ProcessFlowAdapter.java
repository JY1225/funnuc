package eu.robojob.irscw.ui.process.model;

import eu.robojob.irscw.process.AbstractProcessStep;
import eu.robojob.irscw.process.InterventionStep;
import eu.robojob.irscw.process.PickStep;
import eu.robojob.irscw.process.ProcessFlow;
import eu.robojob.irscw.process.ProcessStepType;
import eu.robojob.irscw.process.ProcessingStep;
import eu.robojob.irscw.process.PutStep;

public class ProcessFlowAdapter {

	private ProcessFlow processFlow;
	
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
	
	public int getTransportStepCount() {
		return getDeviceStepCount()-1;
	}
	
	public DeviceInformation getDeviceInformation(int index) {
		if ((index > getDeviceStepCount()) || (index < 0)) {
			throw new IllegalArgumentException("Incorrect index");
		}
		
		DeviceInformation deviceInformation = new DeviceInformation();
		
		int curIndex = 0;
		
		// if not the first, include pick step and possible intervention and processing steps
		int curDevIndex = 0;
		if (index != 0) {
			for (int i = 0; i < processFlow.getProcessSteps().size(); i++) {
				AbstractProcessStep step = processFlow.getStep(i);
				if (step.getType() == ProcessStepType.PUT_STEP) {
					curDevIndex++;
				}
				if (curDevIndex == index) {
					curIndex = i;
					switch(step.getType()) {
						case PUT_STEP:
							deviceInformation.setPutStep((PutStep) step);
							curIndex++;
							break;
						case INTERVENTION_STEP:
							// as the index is greater than zero, there always is a previous step!
							if (processFlow.getStep(i-1).getType() == ProcessStepType.PUT_STEP) {
								deviceInformation.setInterventionStepAfterPut((InterventionStep) step);
								curIndex++;
							} 
							break;
						case PROCESSING_STEP:
							deviceInformation.setProcessingStep((ProcessingStep) step);
							curIndex++;
							break;
					}
				}
			}
		}
		deviceInformation.setDevice(processFlow.getStep(curIndex).getDevice());
		if (processFlow.getStep(curIndex).getType() == ProcessStepType.INTERVENTION_STEP) {
			deviceInformation.setInterventionStepBeforePick((InterventionStep) processFlow.getStep(curIndex));
			curIndex++;
		}
		if (processFlow.getStep(curIndex).getType() == ProcessStepType.PICK_STEP) {
			deviceInformation.setPickStep((PickStep) processFlow.getStep(curIndex));
		}
		
		return deviceInformation;
		
	}
	
	public TransportInformation getTransportInformation(int index) {
		if ((index > getTransportStepCount()) || (index < 0)) {
			throw new IllegalArgumentException("Incorrect index");
		}
		
		TransportInformation transportInformation = new TransportInformation();
		
		int curTranspIndex = 0;
		for (int i = 0; i < processFlow.getProcessSteps().size(); i++) {
			AbstractProcessStep step = processFlow.getStep(i);
			if (curTranspIndex == index) {
				if (step.getType() == ProcessStepType.PICK_STEP) {
					transportInformation.setPickStep((PickStep) step);
					if ((i>1)&&(processFlow.getProcessSteps().get(i-1).getType() == ProcessStepType.INTERVENTION_STEP)) {
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
		
		return transportInformation;
	}
	
}
