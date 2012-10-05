package eu.robojob.irscw.process;

import org.apache.log4j.Logger;

public abstract class AbstractJob {
	
	private static Logger logger = Logger.getLogger(AbstractJob.class);
	
	private int currentStepIndex;
	
	private ProcessFlow processFlow;
	
	public AbstractJob(ProcessFlow processFlow) {
		this.processFlow = processFlow;
		initialize();
	}
	
	public void initialize() {
		currentStepIndex = 0;
	}
	
	public boolean hasNextStep() {
		if (processFlow.getProcessSteps().size() > currentStepIndex + 1) {
			return true;
		} else {
			return false;
		}
	}
	
	public void nextStep() {
		if (hasNextStep()) {
			currentStepIndex++;
		} else {
			throw new IllegalStateException("Last step was reached");
		}
	}
	
	public AbstractProcessStep getCurrentStep() {
		return processFlow.getStep(currentStepIndex);
	}
	
	public int getCurrentStepIndex() {
		return currentStepIndex;
	}
}
