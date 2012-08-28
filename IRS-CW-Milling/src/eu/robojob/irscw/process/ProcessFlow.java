package eu.robojob.irscw.process;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import eu.robojob.irscw.external.device.AbstractDevice;

public class ProcessFlow {
	
	private static Logger logger = Logger.getLogger(ProcessFlow.class);
	
	private List<AbstractProcessStep> processSteps;
	private int currentStepNumber;
	
	private boolean finished;
	
	public ProcessFlow() {
		this.processSteps = new ArrayList<AbstractProcessStep>();
		this.finished = false;
	}
			
	public ProcessFlow(List<AbstractProcessStep>processSteps) {
		this.finished = false;
		this.currentStepNumber = 0;
		setUpProcess(processSteps);
	}
	
	public ProcessFlow(ProcessFlow aProcess) {
		this.finished = false;
		this.currentStepNumber = 0;
		List<AbstractProcessStep> processStepsCopy = new ArrayList<AbstractProcessStep>();
		for (AbstractProcessStep processStep : aProcess.getProcessSteps()) {
			AbstractProcessStep newStep = processStep.clone(this);
			processStepsCopy.add(newStep);
		}
		setUpProcess(processStepsCopy);
	}
	
	public List<AbstractProcessStep> getProcessSteps() {
		return processSteps;
	}
	
	private void setUpProcess(List<AbstractProcessStep> processSteps) {
		this.processSteps = processSteps;
		if (processSteps.size() < 2) {
			throw new IllegalArgumentException("A process should have a minimum of 2 step (Pick & Put)");
		} 
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
	
	public void addStep(AbstractProcessStep newStep) {
		processSteps.add(newStep);
		logger.debug("Added new step: " + newStep);
		newStep.setProcessFlow(this);
	}
	
	public int getStepIndex (AbstractProcessStep step) {
		return processSteps.indexOf(step);
	}
	
}
