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
	private boolean needsTeaching;
	
	
	//TODO refactor constructors so there is one constructor, called by the others
	public ProcessFlow() {
		this.processSteps = new ArrayList<AbstractProcessStep>();
		this.finished = false;
		needsTeaching = true;
	}
			
	public ProcessFlow(List<AbstractProcessStep>processSteps) {
		this.finished = false;
		needsTeaching = true;
		this.currentStepNumber = 0;
		setUpProcess(processSteps);
	}
	
	public ProcessFlow(ProcessFlow aProcess) {
		this.finished = false;
		needsTeaching = true;
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
	
	public boolean needsTeaching() {
		return needsTeaching;
	}

	public void setNeedsTeaching(boolean needsTeaching) {
		this.needsTeaching = needsTeaching;
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
	
	public void addStep(int index, AbstractProcessStep newStep) {
		processSteps.add(index, newStep);
		newStep.setProcessFlow(this);
	}
	
	public void addStep(AbstractProcessStep newStep) {
		processSteps.add(newStep);
		newStep.setProcessFlow(this);
	}
	
	public int getStepIndex (AbstractProcessStep step) {
		return processSteps.indexOf(step);
	}
	
	public AbstractProcessStep getStep(int index) {
		return processSteps.get(index);
	}
	
	public boolean occursMultipleTimes(AbstractProcessStep step) {
		if ((processSteps.indexOf(step) != -1) && (processSteps.indexOf(step) != processSteps.lastIndexOf(step))) {
			return true;
		} else {
			return false;
		}
	}
	
}
