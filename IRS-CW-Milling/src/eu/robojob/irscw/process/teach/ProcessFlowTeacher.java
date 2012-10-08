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
						//TODO : let pick step prepare itself for teaching
						// TEMP: preparing for pick
						try {
							Thread.sleep(2000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						for (TeachObserver observer : observers) {
							observer.preparedForTeaching(pickStep);
						}
						// TEMP: finishing of step
						try {
							Thread.sleep(2000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						for (TeachObserver observer : observers) {
							observer.finishedStep(pickStep);
						}
					} else {
						// pickStep.executeStep();
						try {
							Thread.sleep(2000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					
				} else if (step instanceof PutStep) {
					PutStep putStep = (PutStep) step;
					if (putStep.needsTeaching()) {
						//TODO : let pick step prepare itself for teaching
						// TEMP: preparing for pick
						try {
							Thread.sleep(2000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						for (TeachObserver observer : observers) {
							observer.preparedForTeaching(putStep);
						}
						// TEMP: finishing of step
						try {
							Thread.sleep(2000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						for (TeachObserver observer : observers) {
							observer.finishedStep(putStep);
						}
					} else {
						//putStep.executeStep();
						try {
							Thread.sleep(2000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					
				} else if (step instanceof ProcessingStep) {
					ProcessingStep processingStep = (ProcessingStep) step;
					// processingStep.executeStep();
					// executing process step
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				for (TeachObserver observer : observers) {
					observer.finishedStep(step);
				}
			}
		}
		
	}
	
	public int getCurrentStepIndex() {
		return teachJob.getCurrentStepIndex();
	}
	
	public AbstractProcessStep getCurrentStep() {
		return teachJob.getCurrentStep();
	}
	
}
