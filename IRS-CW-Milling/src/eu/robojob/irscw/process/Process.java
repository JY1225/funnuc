package eu.robojob.irscw.process;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import eu.robojob.irscw.external.device.AbstractStackingDevice;

public class Process {
	
	private List<AbstractProcessStep> processSteps;
	private int currentStepNumber;
	
	private AbstractStackingDevice source;
	private AbstractStackingDevice destination;
	
	private static Logger logger = Logger.getLogger(Process.class.getName());
		
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
			source = (AbstractStackingDevice) ((PickStep) processSteps.get(0)).getDeviceFrom();
			destination = (AbstractStackingDevice) ((PutStep) processSteps.get(processSteps.size() - 1)).getDeviceTo();
		}
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
	
	public void nextStep() {
		currentStepNumber++;
		if (currentStepNumber >= processSteps.size()) {
			throw new IllegalStateException("The current step number is larger than the amount of steps");
		}
	}
	
	public boolean hasFinished() {
		return hasNextStep();
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
}
