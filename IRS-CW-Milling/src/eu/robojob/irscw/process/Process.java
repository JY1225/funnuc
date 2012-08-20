package eu.robojob.irscw.process;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import eu.robojob.irscw.external.device.AbstractStackingDevice;

public class Process {
	
	private List<AbstractProcessStep> processSteps;
	private int currentStepNumber;
	private boolean isActive;
	
	private AbstractStackingDevice source;
	private AbstractStackingDevice destination;
	
	private static Logger logger = Logger.getLogger(Process.class.getName());
	
	private Object canContinue;
	
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
		isActive = false;
		canContinue = new Object();
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
	
	public boolean isActive() {
		return isActive;
	}
	
	public void pauzeExecution() {
		if (this.isActive = false) {
			throw new IllegalStateException("Process was already pauzed");
		}
		this.isActive = false;
	}
	
	public void continueExecution() {
		if (this.isActive = true) {
			throw new IllegalStateException("Process was already active");
		} else {
			this.isActive = true;
			canContinue.notify();
		}
	}
	
	public void startExecution() {
		this.isActive = true;
		for (int i = 0; i < processSteps.size(); i++) {
			if (!isActive) {
				try {
					canContinue.wait();
				} catch (InterruptedException e) {
					if (isActive) {
						executeStep(i); 
					} else {
						throw new IllegalStateException("Waiting for process re-activation was interrupted, but status was not changed to active");
					}
				}
			} else {
				executeStep(i);
			}
		}
	}
	
	//TODO catch exceptions
	private void executeStep(int stepNumber) { 
		if ((stepNumber < 0) || (stepNumber > (processSteps.size() - 1))) {
			throw new IllegalArgumentException("Wrong stepNumber provided");
		}
		logger.info("Starting execution of step: " + stepNumber);
		currentStepNumber = stepNumber;
		processSteps.get(stepNumber).executeStep();
		logger.info("Finished execution of step: " + stepNumber);
		currentStepNumber++;
	}
	
	public void executeCurrentStep() {
		executeStep(currentStepNumber);
	}
	
	public boolean hasFinished() {
		if ((currentStepNumber == (processSteps.size() - 1)) && (isActive == false)) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean hasNextStep() {
		if (currentStepNumber == (processSteps.size() - 1)) {
			return false; 
		} else {
			return true;
		}
	}
}
