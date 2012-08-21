package eu.robojob.irscw.process;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public abstract class AbstractJob {
	
	private static Logger logger = Logger.getLogger(AbstractJob.class);

	protected int finishedWorkpiecesAmount;
	
	// for now, we keep it simple with always just one active process
	private ProcessFlow activeProcess;
	
	private List<AbstractTransportStep> pendingTransportSteps;
	
	private ProcessFlow process;
	
	private Object canContinue;
	
	private boolean isActive;
	private boolean finished;
	
	public AbstractJob(ProcessFlow process) {
		this.process = process;
		this.canContinue = new Object();
		this.isActive = false;
		this.finished = false;
		activeProcess = new ProcessFlow(process);
		finishedWorkpiecesAmount = 0;
		this.pendingTransportSteps = new ArrayList<AbstractTransportStep>();
	}
	
	public void pauzeExecution() {
		if (this.isActive = false) {
			throw new IllegalStateException("Process was already pauzed");
		}
		logger.info("Execution pauzed");
		this.isActive = false;
	}
	
	public void continueExecution() {
		if (this.isActive = true) {
			throw new IllegalStateException("Job was already active");
		} else {
			logger.info("Execution resumed");
			this.isActive = true;
			canContinue.notify();
		}
	}
	
	public void startExecution() {
		this.isActive = true;
		while (activeProcess != null) {
			if (!isActive) {
				try {
					logger.info("Awaiting process-resumption");
					canContinue.wait();
				} catch (InterruptedException e) {
					if (isActive) {
						logger.info("Executing next step");
						executeStep();
					} else {
						throw new IllegalStateException("Waiting for process re-activation was interrupted, but status was not changed to active");
					}
				}
			} else {
				executeStep();
			}
		}
		finished = true;
		logger.info("Job finished");
	}
	
	public abstract boolean hasNextProcess();
	
	//TODO optimize processing (multiple processes at the same time)
	public void executeStep() {
		try {
			process.getCurrentStep().executeStep();
			process.nextStep();
			if (process.hasFinished()) {
				finishedWorkpiecesAmount++;
				if (hasNextProcess()) {
					activeProcess = new ProcessFlow(process);
				}
			}
		} catch (IOException e) {
			logger.error(e);
			isActive = false;
		}
	}

	public int getFinishedWorkpiecesAmount() {
		return finishedWorkpiecesAmount;
	}

	public void setFinishedWorkpiecesAmount(int finishedWorkpiecesAmount) {
		this.finishedWorkpiecesAmount = finishedWorkpiecesAmount;
	}

	public boolean hasFinished() {
		return finished;
	}
	
}
