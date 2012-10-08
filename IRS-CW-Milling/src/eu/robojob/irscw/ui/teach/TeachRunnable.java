package eu.robojob.irscw.ui.teach;

import javafx.application.Platform;

import org.apache.log4j.Logger;

import eu.robojob.irscw.process.AbstractProcessStep;
import eu.robojob.irscw.process.InterventionStep;
import eu.robojob.irscw.process.PickStep;
import eu.robojob.irscw.process.ProcessingStep;
import eu.robojob.irscw.process.PutStep;
import eu.robojob.irscw.process.TeachJob;

public class TeachRunnable implements Runnable {

	private TeachJob teachJob;
	private TeachPresenter teachPresenter;
	
	private static Logger logger = Logger.getLogger(TeachRunnable.class);
	
	public TeachRunnable(TeachJob teachJob, TeachPresenter teachPresenter) {
		this.teachJob = teachJob;
		this.teachPresenter = teachPresenter;
	}
	
	@Override
	public void run() {
		while (teachJob.hasStep()) {
			AbstractProcessStep step = teachJob.getCurrentStep();
			// intervention steps can be skipped
			if (!(step instanceof InterventionStep)) {
				if (step instanceof PickStep) {
					final PickStep pickStep = (PickStep) step;
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							teachPresenter.pickStepInProgress(pickStep);
						}
					});
					if (pickStep.needsTeaching()) {
						wait2Sec();
					
						// TEMP: finishing of step
						wait2Sec();
					
					} else {
						// pickStep.executeStep();
						wait2Sec();
					}
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							teachPresenter.pickStepFinished(pickStep);
						}
					});
				} else if (step instanceof PutStep) {
					final PutStep putStep = (PutStep) step;
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							teachPresenter.putStepInProgress(putStep);
						}
					});
					if (putStep.needsTeaching()) {
						//TODO : let pick step prepare itself for teaching
						// TEMP: preparing for pick
						wait2Sec();
						
						// TEMP: finishing of step
						wait2Sec();
						
					} else {
						//putStep.executeStep();
						wait2Sec();
						
					}
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							teachPresenter.putStepFinished(putStep);
						}
					});
				} else if (step instanceof ProcessingStep) {
					final ProcessingStep processingStep = (ProcessingStep) step;
					// processingStep.executeStep();
					// executing process step
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							teachPresenter.processingInProgress(processingStep);
						}
					});
					wait2Sec();
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							teachPresenter.processingFinished(processingStep);
						}
					});
				}
			}
			teachJob.nextStep();
		}
	}
	
	private void wait2Sec() {
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
