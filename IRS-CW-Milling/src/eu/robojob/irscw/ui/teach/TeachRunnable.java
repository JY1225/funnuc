package eu.robojob.irscw.ui.teach;

import javafx.application.Platform;

import org.apache.log4j.Logger;

import eu.robojob.irscw.process.AbstractProcessStep;
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
					final PickStep pickStep = (PickStep) step;
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							teachPresenter.pickStepInProgress(pickStep);
						}
					});
					if (pickStep.needsTeaching()) {
						setStatus("prepare-pick");
						wait2Sec();
						canContinue = false;
						Platform.runLater(new Runnable() {
							@Override
							public void run() {
								teachPresenter.teachingNeeded();
							}
						});
						while (!canContinue) {
							synchronized(uiSyncObject) {
								try {
									uiSyncObject.wait(1000);
								} catch (InterruptedException e) {
									if (!canContinue) {
										logger.error(e);
										return;
									} else {
										logger.info("can continue");
									}
								}
							}
						}
						setStatus("execute-pick");
						wait2Sec();
						setStatus("executed-pick");
					} else {
						// pickStep.executeStep();
						setStatus("prepare-pick");
						wait2Sec();
						setStatus("execute-pick");
						wait2Sec();
						setStatus("executed-pick");
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
						setStatus("prepare-put");
						wait2Sec();
						canContinue = false;
						Platform.runLater(new Runnable() {
							@Override
							public void run() {
								teachPresenter.teachingNeeded();
							}
						});
						while (!canContinue) {
							synchronized(uiSyncObject) {
								try {
									uiSyncObject.wait(1000);
								} catch (InterruptedException e) {
									if (!canContinue) {
										logger.error(e);
										return;
									} else {
										logger.info("can continue");
									}
								}
							}
						}
						setStatus("execute-put");
						wait2Sec();
						setStatus("executed-pick");
						
					} else {
						setStatus("prepare-put");
						wait2Sec();
						setStatus("execute-put");
						wait2Sec();
						setStatus("executed-put");
						
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
					setStatus("prepare-process");
					wait2Sec();
					setStatus("executed-process");
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
		setStatus("process-finished");
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				teachPresenter.flowFinished();
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
