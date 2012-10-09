package eu.robojob.irscw.ui.teach;

import javafx.application.Platform;

import org.apache.log4j.Logger;

import eu.robojob.irscw.process.AbstractProcessStep;
import eu.robojob.irscw.process.AbstractTransportStep;
import eu.robojob.irscw.process.InterventionStep;
import eu.robojob.irscw.process.PickStep;
import eu.robojob.irscw.process.ProcessingStep;
import eu.robojob.irscw.process.PutStep;
import eu.robojob.irscw.process.TeachJob;
import eu.robojob.irscw.util.Translator;

public class TeachRunnable implements Runnable {

	private TeachJob teachJob;
	private TeachPresenter teachPresenter;
	private Translator translator;
	
	private static Logger logger = Logger.getLogger(TeachRunnable.class);
	
	private boolean canContinue;
	private Object uiSyncObject;
	
	private final static String PREPARE_PICK = "prepare-pick";
	private final static String EXECUTE_PICK = "execute-pick";
	private final static String EXECUTED_PICK = "executed-pick";
	private final static String PREPARE_PUT = "prepare-put";
	private final static String EXECUTE_PUT = "execute-put";
	private final static String EXECUTED_PUT = "executed-put";
	
	private final static int TEACH_TIMEOUT = 1000 * 60 * 15;
	
	public TeachRunnable(TeachJob teachJob, TeachPresenter teachPresenter) {
		this.teachJob = teachJob;
		this.teachPresenter = teachPresenter;
		canContinue = false;
		this.uiSyncObject = new Object();
		this.translator = Translator.getInstance();
	}
	
	@Override
	public void run() {
		canContinue = true;
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
	}
	
	private void handlePick(final PickStep pickStep) {
		// notify presenter this pick step is in progress 
		notifyStepInProgress(pickStep);
		if (pickStep.needsTeaching()) {
			
			// set status-message to indicate we're preparing for the pick
			setStatus(PREPARE_PICK);
			wait2Sec();
			
			canContinue = false;
			
			// teaching the exact position is needed at this point
			notifyTeachingNeeded();
			waitForTeaching();
						
			// now we know the teaching position, we execute the pick
			setStatus(EXECUTE_PICK);
			
			wait2Sec();
			setStatus(EXECUTED_PICK);
			
		} else {
			// to teaching needed so we just prepare and execute pick
			setStatus(PREPARE_PICK);
			wait2Sec();
			setStatus(EXECUTE_PICK);
			wait2Sec();
			setStatus(EXECUTED_PICK);
		}
		notifyStepFinished(pickStep);
	}
	
	//TODO refactor: duplicate code
	private void handlePut(final PutStep putStep) {
		// notify presenter this put step is in progress 
		notifyStepInProgress(putStep);
		if (putStep.needsTeaching()) {
			
			// set status-message to indicate we're preparing for the put
			setStatus(PREPARE_PUT);
			wait2Sec();
			
			canContinue = false;
			// teaching the exact position is needed at this point
			notifyTeachingNeeded();
			waitForTeaching();
			
			// now we know the teaching position, we execute the pick
			setStatus(EXECUTE_PUT);
			
			wait2Sec();
			setStatus(EXECUTED_PUT);
			
		} else {
			// to teaching needed so we just prepare and execute pick
			setStatus(PREPARE_PUT);
			wait2Sec();
			setStatus(EXECUTE_PUT);
			wait2Sec();
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
	
	private void handleProcessing(final ProcessingStep step) {
		// processingStep.executeStep();
		// executing process step
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				teachPresenter.processingInProgress(step);
			}
		});
		setStatus("prepare-process");
		wait2Sec();
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
	
	private void notifyStepInProgress(final AbstractTransportStep step) {
		if (step instanceof PickStep) {
			notifyStepInProgress((PickStep) step);
		} else if (step instanceof PutStep) {
			notifyStepInProgress((PutStep) step);
		}
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
	
	private void wait2Sec() {
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
				teachPresenter.setInfo(translator.getTranslation(statusKey));
			}
		});
	}
}
