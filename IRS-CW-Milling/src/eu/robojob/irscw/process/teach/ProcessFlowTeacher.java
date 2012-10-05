package eu.robojob.irscw.process.teach;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import eu.robojob.irscw.process.AbstractProcessStep;
import eu.robojob.irscw.process.InterventionStep;
import eu.robojob.irscw.process.PickStep;
import eu.robojob.irscw.process.ProcessFlow;
import eu.robojob.irscw.process.ProcessingStep;
import eu.robojob.irscw.process.PutStep;
import eu.robojob.irscw.process.TeachJob;

public class ProcessFlowTeacher {

	private TeachJob teachJob;
	private Set<TeachObserver> observers;
	
	private static Logger logger = Logger.getLogger(ProcessFlowTeacher.class);
	
	public ProcessFlowTeacher(ProcessFlow processFlow) {
		this.teachJob = new TeachJob(processFlow);
		this.observers = new HashSet<TeachObserver>();
	}
	
	public void addObserver(TeachObserver observer) {
		observers.add(observer);
	}
	
	public void startTeachingFlow() throws IOException {
		teachJob.initialize();
		while (teachJob.hasNextStep()) {
			AbstractProcessStep step = teachJob.getCurrentStep();
			// intervention steps can be skipped
			if (!(step instanceof InterventionStep)) {
				for (TeachObserver observer : observers) {
					observer.stepInProgress(step);
				}
				if (step instanceof PickStep) {
					PickStep pickStep = (PickStep) step;
					if (pickStep.needsTeaching()) {
						
					} else {
						pickStep.executeStep();
					}
					
				} else if (step instanceof PutStep) {
					PutStep putStep = (PutStep) step;
					if (putStep.needsTeaching()) {
						
					} else {
						putStep.executeStep();
					}
					
				} else if (step instanceof ProcessingStep) {
					ProcessingStep processingStep = (ProcessingStep) step;
					processingStep.executeStep();
				}
			}
		}
		
	}
	
	public void teachingFinished() {
		
	}
	
	public void continueExecution() {
		
	}
	
	public int getCurrentStepIndex() {
		return teachJob.getCurrentStepIndex();
	}
	
	public AbstractProcessStep getCurrentStep() {
		return teachJob.getCurrentStep();
	}
	
	public boolean currentStepNeedsTeaching() {
		AbstractProcessStep step = getCurrentStep();
		if (step instanceof PickStep) {
			PickStep pickStep = (PickStep) step;
			return pickStep.needsTeaching();
		} else if (step instanceof PutStep) {
			PutStep putStep = (PutStep) step;
			return putStep.needsTeaching();
		} else {
			return false;
		}
	}
	
	public void prepareForTeaching() {
		AbstractProcessStep step = getCurrentStep();
		if (step instanceof PickStep) {
			PickStep pickStep = (PickStep) step;
			
		} else if (step instanceof PutStep) {
			PutStep putStep = (PutStep) step;
			
		}
	}
}
