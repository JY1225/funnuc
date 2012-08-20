package eu.robojob.irscw.process;

import java.util.LinkedList;
import java.util.Set;

import org.apache.log4j.Logger;

import eu.robojob.irscw.external.AbstractServiceProvider;

public abstract class AbstractJob {
	
	private static Logger logger = Logger.getLogger(AbstractJob.class);

	private int finishedWorkpiecesAmount;
	
	private LinkedList<Process> activeProcesses;
	private Process mainProcess;
	private Process secondProcess;
	
	private Process process;
	private int mainProcessIndex;
	
	private Object canContinue;
	
	private boolean isActive;
	private boolean finished;
	
	public void pauzeExecution() {
		if (this.isActive = false) {
			throw new IllegalStateException("Process was already pauzed");
		}
		this.isActive = false;
	}
	
	public void continueExecution() {
		if (this.isActive = true) {
			throw new IllegalStateException("Job was already active");
		} else {
			this.isActive = true;
			canContinue.notify();
		}
	}
	
	public void startExecution() {
		this.isActive = true;
		while (hasNextStep()) {
			if (!isActive) {
				try {
					canContinue.wait();
				} catch (InterruptedException e) {
					if (isActive) {
						executeStep();
					} else {
						throw new IllegalStateException("Waiting for process re-activation was interrupted, but status was not changed to active");
					}
				}
			} else {
				executeStep();
			}
		}
	}
	
	public abstract boolean hasNextStep();
	public abstract boolean hasNextProcess();
	
	// optimization for two processes at the same time
	//TODO adapt logic so more processes can be executed at the same time
	public void executeStep() {
		
		// update active processes, main and second process
		updateActiveProcesses();
		
		if (mainProcess == null) {
			finished = true;
		}
		
		// main Process dictates the flow
		AbstractProcessStep step = mainProcess.getCurrentStep();
		ProcessStepExecution mainProcessExecution = new ProcessStepExecution(step);
		
		Thread mainProcessExecutionThread = new Thread(mainProcessExecution);
		mainProcessExecutionThread.start();
		
		while(!mainProcessExecution.hasFinished()) {
			if (secondProcess != null) {
				if (!secondProcess.isActive()) {
					Set<AbstractServiceProvider> mainServiceProviders = step.getServiceProviders();
					Set<AbstractServiceProvider> secondServiceProviders = secondProcess.getCurrentStep().getServiceProviders();
					mainServiceProviders.retainAll(secondServiceProviders);
					if (mainServiceProviders.size() == 0) {
						ProcessStepExecution secondProcessStepExecution = new ProcessStepExecution(secondProcess.getCurrentStep());
						Thread secondProcessExecutionThread = new Thread(secondProcessStepExecution);
						secondProcessExecutionThread.start();
					}
				}
			} else {
				try {
					mainProcessExecutionThread.join();
				} catch (InterruptedException e) {
					logger.error(e);
				}
			}
			
		}
	}
	
	private void updateActiveProcesses() {
		while (activeProcesses.getFirst().hasFinished()) {
			activeProcesses.removeFirst();
		}
		if (activeProcesses.size() > 0) {
			mainProcess = activeProcesses.getFirst();
		} else {
			mainProcess = null;
		}
		if ((activeProcesses.size() < 2) && (hasNextProcess())) {
			secondProcess = new Process(process);
			activeProcesses.add(secondProcess);
		} else if (hasNextProcess()) {
			secondProcess = activeProcesses.get(1);
		}
		if (secondProcess.hasFinished()) {
			secondProcess = null;
		}
	}
	
}
