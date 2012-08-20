package eu.robojob.irscw.process;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class Process {
	
	private List<AbstractProcessStep> processSteps;
	private int currentStepNumber;
	private boolean isActive;
	
	private static Logger logger = Logger.getLogger(Process.class.getName());
	
	private Object canContinue;
	
	public Process(List<AbstractProcessStep>processSteps) {
		this.processSteps = processSteps;
		if (processSteps.size() > 0) {
			currentStepNumber = 0;
		} else {
			currentStepNumber = -1;
		}
		isActive = false;
		canContinue = new Object();
	}
	
	public Process() {
		this(new ArrayList<AbstractProcessStep>());
	}
	
	public Process(Process aProcess) {
		this();
		for (AbstractProcessStep processStep : processSteps) {
			AbstractProcessStep newStep = processStep.clone(this);
			addProcessStep(newStep);
		}
	}
	
	public void addProcessStep(AbstractProcessStep step) {
		processSteps.add(step);
		if (processSteps.size() == 1) {
			currentStepNumber = 0;
		}
	}
	
	public void removeProcessStep(AbstractProcessStep step) {
		if ((currentStepNumber != -1) && (processSteps.get(currentStepNumber).equals(step))) {
			currentStepNumber = -1;
		}
		processSteps.remove(step);
	}
	
	public void removeAllProcessSteps() {
		currentStepNumber = -1;
		processSteps.clear();
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
