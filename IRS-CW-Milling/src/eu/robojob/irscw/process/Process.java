package eu.robojob.irscw.process;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class Process {
	
	private List<AbstractProcessStep> processSteps;
	private AbstractProcessStep currentStep;
	private boolean isActive;
	
	private static Logger logger = Logger.getLogger(Process.class.getName());
	
	private Object canContinue;
	
	public Process(List<AbstractProcessStep>processSteps) {
		this.processSteps = processSteps;
		if (processSteps.size() > 0) {
			currentStep = processSteps.get(0);
		}
		isActive = false;
		canContinue = new Object();
	}
	
	public Process() {
		this(new ArrayList<AbstractProcessStep>());
	}
	
	public void addProcessStep(AbstractProcessStep step) {
		processSteps.add(step);
		if (processSteps.size() == 1) {
			currentStep = processSteps.get(0);
		}
	}
	
	public void removeProcessStep(AbstractProcessStep step) {
		if (currentStep.equals(step)) {
			currentStep = null;
		}
		processSteps.remove(step);
	}
	
	public void removeAllProcessSteps() {
		currentStep = null;
		processSteps.clear();
	}
	
	public AbstractProcessStep getCurrentStep() {
		return currentStep;
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
		for (AbstractProcessStep step : processSteps) {
			if (!isActive) {
				try {
					canContinue.wait();
				} catch (InterruptedException e) {
					if (isActive) {
						executeStep(step); 
					} else {
						throw new IllegalStateException("Waiting for process re-activation was interrupted, but status was not changed to active");
					}
				}
			} else {
				executeStep(step);
			}
		}
	}
	
	//TODO catch exceptions
	private void executeStep(AbstractProcessStep step) {
		logger.info("Starting execution of step: " + step);
		this.currentStep = step;
		step.executeStep();
		logger.info("Finished execution of step: " + step);
	}
}
