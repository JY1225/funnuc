package eu.robojob.irscw.process.execution;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.robojob.irscw.external.communication.AbstractCommunicationException;
import eu.robojob.irscw.external.device.DeviceActionException;
import eu.robojob.irscw.external.robot.RobotActionException;
import eu.robojob.irscw.process.AbstractProcessStep;
import eu.robojob.irscw.process.AbstractTransportStep;
import eu.robojob.irscw.process.InterventionStep;
import eu.robojob.irscw.util.Translator;

public class ProcessStepExecutionThread extends Thread {

	private AbstractProcessStep step;
	private int workpieceId;
	private ProcessExecutor parent;
	private static Logger logger = LogManager.getLogger(ProcessStepExecutionThread.class.getName());
	
	protected static final String OTHER_EXCEPTION = "Exception.otherException";
	
	
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
			} else if (step instanceof InterventionStep) {
				InterventionStep iStep = (InterventionStep) step;
				if (iStep.isInterventionNeeded(step.getProcessFlow().getFinishedAmount())) {
					parent.pause();
				}
			}
			parent.stepExecutionFinished(workpieceId);
			logger.debug("Finished step execution [" + step + "], workpiece id [" + workpieceId + "].");
		} catch (AbstractCommunicationException | RobotActionException | DeviceActionException e) {
			parent.notifyException(e);
		} catch (InterruptedException e) {
			parent.notifyInterruptedException(e);
		} catch (Exception e) {
			parent.notifyException(new Exception(Translator.getTranslation(OTHER_EXCEPTION)));
			e.printStackTrace();
			logger.error(e);
		} catch (Throwable t) {
			logger.error(t);
			t.printStackTrace();
		}
		logger.info(toString() + " ended...");
	}
	
	@Override
	public String toString() {
		return "ProcessStepExecutionThread: " + step.toString();
	}
}
