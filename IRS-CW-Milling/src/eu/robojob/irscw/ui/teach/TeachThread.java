package eu.robojob.irscw.ui.teach;

import javafx.application.Platform;

import org.apache.log4j.Logger;

import eu.robojob.irscw.external.communication.CommunicationException;
import eu.robojob.irscw.external.device.DeviceActionException;
import eu.robojob.irscw.external.robot.AbstractRobot;
import eu.robojob.irscw.external.robot.RobotActionException;
import eu.robojob.irscw.process.AbstractProcessStep;
import eu.robojob.irscw.process.InterventionStep;
import eu.robojob.irscw.process.PickStep;
import eu.robojob.irscw.process.ProcessFlow.Mode;
import eu.robojob.irscw.process.ProcessingStep;
import eu.robojob.irscw.process.PutStep;
import eu.robojob.irscw.process.TeachJob;
import eu.robojob.irscw.util.Translator;

public class TeachThread extends Thread {

	private TeachJob teachJob;
	private TeachPresenter teachPresenter;
	
	private static Logger logger = Logger.getLogger(TeachThread.class);
	
	private PickStep lastPickStep;
	
	public TeachThread(TeachJob teachJob, TeachPresenter teachPresenter) {
		this.teachJob = teachJob;
		this.teachPresenter = teachPresenter;
		this.lastPickStep = null;
	}
	
	@Override
	public void run() {
		teachJob.getProcessFlow().setMode(Mode.TEACH);
		try {
			for (AbstractRobot robot : teachJob.getProcessFlow().getRobots()) {
				robot.restartProgram();
			}
			while (teachJob.hasStep()) {
				AbstractProcessStep step = teachJob.getCurrentStep();
				// intervention steps can be skipped
				if (!(step instanceof InterventionStep)) {
					if (step instanceof PickStep) {
						handlePick((PickStep) step);
					} else if (step instanceof PutStep) {
						handlePut((PutStep) step);
					} else if (step instanceof ProcessingStep) {
						handleProcessing((ProcessingStep) step);
					}
				}
				teachJob.nextStep();
			}
			teachJob.getProcessFlow().setMode(Mode.READY);
		} catch(Exception e) {
			//TODO better handling of these exceptions (communication, robot, device, ...)
			logger.error(e);
			e.printStackTrace();
			notifyException(e);
		}
	}
	
	private void handlePick(final PickStep pickStep) throws CommunicationException, RobotActionException, DeviceActionException {
		// notify presenter this pick step is in progress 
		this.lastPickStep = pickStep;
		if (pickStep.needsTeaching()) {
			
			// set status-message to indicate we're preparing for the pick
			pickStep.prepareForTeaching();
						
			// teaching the exact position is needed at this point			
			pickStep.teachingFinished();
			
		} else {
			// to teaching needed so we just prepare and execute pick
			pickStep.executeStep();
		}
	}
	
	private void handlePut(final PutStep putStep) throws CommunicationException, RobotActionException, DeviceActionException {
		// notify presenter this pick step is in progress 
		if (putStep.needsTeaching()) {
			
			// set status-message to indicate we're preparing for the pick
			putStep.prepareForTeaching();
			
			if (lastPickStep != null) {
				lastPickStep.finalize();
				lastPickStep = null;
			} else {
				throw new IllegalStateException("Put without previous pick?");
			}
					
			// teaching the exact position is needed at this point			
			putStep.teachingFinished();
			
		} else {
			// to teaching needed so we just prepare and execute pick
			putStep.executeStep();
		}
	}

	private void handleProcessing(final ProcessingStep step) throws CommunicationException, DeviceActionException {
		// processingStep.executeStep();
		// executing process step
		step.executeStep();
	}
	
	private void notifyException(final Exception e) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				teachPresenter.exceptionOccured(e);
			}
		});
	}
}
