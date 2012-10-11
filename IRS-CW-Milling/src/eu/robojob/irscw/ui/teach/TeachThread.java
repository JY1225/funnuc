package eu.robojob.irscw.ui.teach;

import javafx.application.Platform;

import org.apache.log4j.Logger;

import eu.robojob.irscw.external.communication.CommunicationException;
import eu.robojob.irscw.external.device.DeviceActionException;
import eu.robojob.irscw.external.robot.RobotActionException;
import eu.robojob.irscw.process.AbstractProcessStep;
import eu.robojob.irscw.process.InterventionStep;
import eu.robojob.irscw.process.PickStep;
import eu.robojob.irscw.process.ProcessingStep;
import eu.robojob.irscw.process.PutStep;
import eu.robojob.irscw.process.TeachJob;
import eu.robojob.irscw.util.Translator;

public class TeachThread extends Thread {

	private TeachJob teachJob;
	private TeachPresenter teachPresenter;
	private Translator translator;
	
	private static Logger logger = Logger.getLogger(TeachThread.class);
	
	private boolean canContinue;
	private Object uiSyncObject;
	
	private final static String PREPARE_PICK = "prepare-pick";
	private final static String EXECUTE_PICK = "execute-pick";
	private final static String EXECUTED_PICK = "executed-pick";
	private final static String PREPARE_PUT = "prepare-put";
	private final static String EXECUTE_PUT = "execute-put";
	private final static String EXECUTED_PUT = "executed-put";
	
	private final static int TEACH_TIMEOUT = 1000 * 60 * 15;
	
	private PickStep lastPickStep;
	
	public TeachThread(TeachJob teachJob, TeachPresenter teachPresenter) {
		this.teachJob = teachJob;
		this.teachPresenter = teachPresenter;
		canContinue = false;
		this.uiSyncObject = new Object();
		this.translator = Translator.getInstance();
		this.lastPickStep = null;
	}
	
	@Override
	public void run() {
		canContinue = true;
		try {
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
			setStatus("process-finished");
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					teachPresenter.flowFinished();
				}
			});
		} catch(Exception e) {
			logger.error(e);
			e.printStackTrace();
			notifyException(e);
		}
	}
	
	private void handlePick(final PickStep pickStep) throws CommunicationException, RobotActionException {
		// notify presenter this pick step is in progress 
		notifyStepInProgress(pickStep);
		this.lastPickStep = pickStep;
		if (pickStep.needsTeaching()) {
			
			// set status-message to indicate we're preparing for the pick
			setStatus(PREPARE_PICK);
			pickStep.prepareForTeaching();
			
			canContinue = false;
			
			// teaching the exact position is needed at this point
			notifyTeachingNeeded();
			waitForTeaching();
			
			setStatus(EXECUTE_PICK);
			pickStep.teachingFinished();
			
			setStatus(EXECUTED_PICK);
		} else {
			// to teaching needed so we just prepare and execute pick
			setStatus(EXECUTE_PICK);
			pickStep.executeStep();
			setStatus(EXECUTED_PICK);
		}
		notifyStepFinished(pickStep);
	}
	
	private void handlePut(final PutStep putStep) throws CommunicationException, RobotActionException {
		// notify presenter this pick step is in progress 
		notifyStepInProgress(putStep);
		if (putStep.needsTeaching()) {
			
			// set status-message to indicate we're preparing for the pick
			setStatus(PREPARE_PUT);
			putStep.prepareForTeaching();
			
			if (lastPickStep != null) {
				lastPickStep.finalize();
				lastPickStep = null;
			} else {
				throw new IllegalStateException("Put without previous pick?");
			}
			
			canContinue = false;
			
			// teaching the exact position is needed at this point
			notifyTeachingNeeded();
			waitForTeaching();
			
			setStatus(EXECUTE_PUT);
			putStep.teachingFinished();
			
			//wait2Sec();
			setStatus(EXECUTED_PUT);
			
		} else {
			// to teaching needed so we just prepare and execute pick
			setStatus(EXECUTE_PUT);
			putStep.executeStep();
			setStatus(EXECUTED_PUT);
		}
		notifyStepFinished(putStep);
	}
	
	private void waitForTeaching() {
		// waiting for teaching to finish... 
		synchronized(uiSyncObject) {
			try {
				uiSyncObject.wait(TEACH_TIMEOUT);
			} catch (InterruptedException e) {
				if (!canContinue) {
					// the waiting was interrupted, but not because teaching was finished, so something went wrong!
					logger.error(e);
					return;
				} else {
					logger.info("can continue");
				}
			}
			if (!canContinue) {
				// timeout! 
				logger.error("You waited too long...");
			}
		}
	}
	
	private void handleProcessing(final ProcessingStep step) throws CommunicationException, DeviceActionException {
		// processingStep.executeStep();
		// executing process step
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				teachPresenter.processingInProgress(step);
			}
		});
		setStatus("prepare-process");
		step.executeStep();
		setStatus("executed-process");
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				teachPresenter.processingFinished(step);
			}
		});
	}
	
	private void notifyTeachingNeeded() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				teachPresenter.teachingNeeded();
			}
		});
	}
	
	private void notifyStepInProgress(final PickStep pickStep) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				teachPresenter.pickStepInProgress(pickStep);
			}
		});
	}
	
	private void notifyStepInProgress(final PutStep putStep) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				teachPresenter.putStepInProgress(putStep);
			}
		});
	}
	
	private void notifyStepFinished(final PickStep pickStep) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				teachPresenter.pickStepFinished(pickStep);
			}
		});
	}
	
	private void notifyStepFinished(final PutStep putStep) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				teachPresenter.putStepFinished(putStep);
			}
		});
	}

	public void teachingFinished() {
		synchronized (uiSyncObject) {
			canContinue = true;
			uiSyncObject.notify();
		}
	}
	
	private void setStatus(final String statusKey) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				teachPresenter.setStatus(translator.getTranslation(statusKey));
			}
		});
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
