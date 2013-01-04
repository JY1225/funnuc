package eu.robojob.irscw.process.execution;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.robojob.irscw.process.AbstractProcessStep;
import eu.robojob.irscw.process.AbstractTransportStep;

public class ProcessStepExecutionThread extends Thread {

	private AbstractProcessStep step;
	private int workpieceId;
	private ProcessExecutor parent;
	private static Logger logger = LogManager.getLogger(ProcessStepExecutionThread.class.getName());
	
	public ProcessStepExecutionThread(final AbstractProcessStep step, final int workpieceId, final ProcessExecutor parent) {
		this.step = step;
		this.workpieceId = workpieceId;
		this.parent = parent;
	}
	
	@Override
	public void run() {
		logger.debug("Started step execution [" + step + "], workpiece id [" + workpieceId + "].");
		try {
			step.executeStep(workpieceId);
			if (step instanceof AbstractTransportStep) {
				((AbstractTransportStep) step).finalizeStep();
			}
			parent.stepExecutionFinished(workpieceId);
			logger.debug("Finished step execution [" + step + "], workpiece id [" + workpieceId + "].");
		} catch (Exception e) {
			parent.notifyException(e);
		} catch (Throwable t) {
			parent.notifyThrowable(t);
		}
		logger.info(toString() + " ended...");
	}
	
	@Override
	public String toString() {
		return "ProcessStepExecutionThread: " + step.toString();
	}
}
