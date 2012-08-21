package eu.robojob.irscw.process;

import java.util.ArrayList;
import java.util.List;

import eu.robojob.irscw.external.device.AbstractDevice;
import eu.robojob.irscw.external.device.AbstractStackingDevice;

public class Process {
	
	private List<AbstractProcessStep> processSteps;
	private int currentStepNumber;
	
	private AbstractStackingDevice source;
	private AbstractStackingDevice destination;
	
	private boolean finished;
			
	public Process(List<AbstractProcessStep>processSteps) {
		setUpProcess(processSteps);
	}
	
	public Process(Process aProcess) {
		List<AbstractProcessStep> processStepsCopy = new ArrayList<AbstractProcessStep>();
		for (AbstractProcessStep processStep : processSteps) {
			AbstractProcessStep newStep = processStep.clone(this);
			processStepsCopy.add(newStep);
		}
		setUpProcess(processStepsCopy);
	}
	
	private void setUpProcess(List<AbstractProcessStep> processSteps) {
		this.processSteps = processSteps;
		if (processSteps.size() < 2) {
			throw new IllegalArgumentException("A process should have a minimum of 2 step (Pick & Put)");
		} else {
			// Process should start with a PickStep and end with a PutStep, both with a StackingDevice as device
			// If this is not the case, a ClassCastException will be thrown!
			source = (AbstractStackingDevice) ((PickStep) processSteps.get(0)).getDevice();
			destination = (AbstractStackingDevice) ((PutStep) processSteps.get(processSteps.size() - 1)).getDevice();
		}
		this.finished = false;
	}
	
	public AbstractStackingDevice getSource() {
		return source;
	}

	public AbstractStackingDevice getDestination() {
		return destination;
	}
	
	public AbstractProcessStep getCurrentStep() {
		if (currentStepNumber != -1) {
			return processSteps.get(currentStepNumber);
		} else {
			return null;
		}
	}
	
	public AbstractProcessStep getNextStep() {
		if ((currentStepNumber != -1) && (currentStepNumber < (processSteps.size() - 1))) {
			return processSteps.get(currentStepNumber + 1);
		} else {
			return null;
		}
	}
	
	public void nextStep() {
		currentStepNumber++;
		if (currentStepNumber >= processSteps.size()) {
			finished = true;
		}
	}
	
	public boolean hasFinished() {
		return finished;
	}
	
	public boolean hasNextStep() {
		if (currentStepNumber == (processSteps.size() - 1)) {
			return false; 
		} else {
			return true;
		}
	}
	
	public boolean isActive() {
		return getCurrentStep().isInProcess();
	}
	

	public boolean willNeedDevice(AbstractDevice device) {
		for (int i = currentStepNumber; i < processSteps.size(); i++) {
			if (processSteps.get(i).getDevice().equals(device)) {
				return true;
			}
		}
		return false;
	}
}
