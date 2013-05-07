package eu.robojob.millassist.ui.general.status;

import java.util.Set;

import javafx.application.Platform;
import eu.robojob.millassist.process.DeviceStep;
import eu.robojob.millassist.process.PickStep;
import eu.robojob.millassist.process.ProcessFlow.Mode;
import eu.robojob.millassist.process.PutStep;
import eu.robojob.millassist.process.event.DataChangedEvent;
import eu.robojob.millassist.process.event.ExceptionOccuredEvent;
import eu.robojob.millassist.process.event.FinishedAmountChangedEvent;
import eu.robojob.millassist.process.event.ModeChangedEvent;
import eu.robojob.millassist.process.event.ProcessFlowListener;
import eu.robojob.millassist.process.event.StatusChangedEvent;
import eu.robojob.millassist.util.Translator;

public class StatusPresenter implements ProcessFlowListener {

	private StatusView view;
	
	private static final String INITIALIZING_PROCESS = "Status.initializingProcess";
	private static final String PROCESS_TEACH_STARTED = "Status.processTeachStarted";
	private static final String PROCESS_TEACH_FINISHED = "Status.processTeachFinished";
	private static final String PREPARE_PICK = "Status.prepareDevicePick";
	private static final String PREPARE_PUT = "Status.prepareDevicePut";
	private static final String EXECUTE_PICK = "Status.executePick";
	private static final String EXECUTE_PUT = "Status.executePut";
	private static final String PROCESSING = "Status.processing";
	private static final String NONE_ACTIVE = "Status.noneActive";
	private static final String TEACHING_NEEDED = "Status.teachingNeeded";
	private static final String TEACHING_FINISHED = "Status.teachingFinished";
	private static final String ENDED_PICK = "Status.endedPick";
	private static final String ENDED_PUT = "Status.endedPut";
	private static final String INTERVENTION_READY = "Status.interventionReady";
	
	public StatusPresenter(final StatusView view) {
		this.view = view;
	}
	
	public StatusView getView() {
		return view;
	}
	
	public void initializeView() {
		view.setInfoMessage(Translator.getTranslation(NONE_ACTIVE));
		view.setErrorMessage(null);
	}

	@Override public void statusChanged(final StatusChangedEvent e) {
		Platform.runLater(new Runnable() {
			@Override public void run() {
				switch (e.getStatusId()) {
					case StatusChangedEvent.INACTIVE:
						view.setInfoMessage(Translator.getTranslation(NONE_ACTIVE));
						break;
					case StatusChangedEvent.STARTED:
						if (e.getActiveStep() instanceof PickStep) {
							view.setInfoMessage(Translator.getTranslation(EXECUTE_PICK) + ((DeviceStep) e.getActiveStep()).getDevice().getName() + ".");
						} else if (e.getActiveStep() instanceof PutStep) {
							view.setInfoMessage(Translator.getTranslation(EXECUTE_PUT) + ((DeviceStep) e.getActiveStep()).getDevice().getName() + ".");
						}
						break;
					case StatusChangedEvent.PREPARE_DEVICE:
						if (e.getActiveStep() instanceof PickStep) {
							view.setInfoMessage(((PickStep) e.getActiveStep()).getDevice().getName() + " " + Translator.getTranslation(PREPARE_PICK) + ".");
						} else if (e.getActiveStep() instanceof PutStep) {
							view.setInfoMessage(((PutStep) e.getActiveStep()).getDevice().getName() + " " + Translator.getTranslation(PREPARE_PUT) + ".");
						}
						break;
					case StatusChangedEvent.EXECUTE_TEACHED:
					case StatusChangedEvent.EXECUTE_NORMAL:
						if (e.getActiveStep() instanceof PickStep) {
							view.setInfoMessage(Translator.getTranslation(EXECUTE_PICK) + ((DeviceStep) e.getActiveStep()).getDevice().getName() + ".");
						} else if (e.getActiveStep() instanceof PutStep) {
							view.setInfoMessage(Translator.getTranslation(EXECUTE_PUT) + ((DeviceStep) e.getActiveStep()).getDevice().getName() + ".");
						}
						break;
					case StatusChangedEvent.INTERVENTION_READY:
						view.setInfoMessage(Translator.getTranslation(INTERVENTION_READY));
						break;
					case StatusChangedEvent.PROCESSING_STARTED:
						view.setInfoMessage(((DeviceStep) e.getActiveStep()).getDevice().getName() + " " + Translator.getTranslation(PROCESSING));
						break;
					case StatusChangedEvent.ENDED:
						if (e.getActiveStep() instanceof PickStep) {
							view.setInfoMessage(Translator.getTranslation(ENDED_PICK) + ((DeviceStep) e.getActiveStep()).getDevice().getName() + ".");
						} else if (e.getActiveStep() instanceof PutStep) {
							view.setInfoMessage(Translator.getTranslation(ENDED_PUT) + ((DeviceStep) e.getActiveStep()).getDevice().getName() + ".");
						}
						break;
					case StatusChangedEvent.TEACHING_NEEDED:
						view.setInfoMessage(Translator.getTranslation(TEACHING_NEEDED));
						break;
					case StatusChangedEvent.TEACHING_FINISHED:
						view.setInfoMessage(Translator.getTranslation(TEACHING_FINISHED));
						break;
					case StatusChangedEvent.PREPARE:
						view.setInfoMessage(Translator.getTranslation(INITIALIZING_PROCESS));
						break;
					default:
						throw new IllegalArgumentException("Unknown status id: " + e.getStatusId());
				}
			}
		});
	}
	
	@Override public void modeChanged(final ModeChangedEvent e) {
		Platform.runLater(new Runnable() {
			@Override public void run() {
				if (e.getMode() == Mode.TEACH) {
					view.setInfoMessage(Translator.getTranslation(PROCESS_TEACH_STARTED));
				} else if (e.getMode() == Mode.READY) {
					view.setInfoMessage(Translator.getTranslation(PROCESS_TEACH_FINISHED));
				}
			}
		});
	}
	
	public void updateAlarms(final Set<String> alarms) { 
		if (alarms.size() == 0) {
			view.setAlarmMessage(null);
		} else {
			String alarmString = "";
			int i = 0;
			for (String alarm : alarms) {
				if (i != 0) {
					alarmString = alarmString + " - ";
				}
				alarmString = alarmString + alarm;
				i++;
			}
			view.setAlarmMessage(alarmString);
		}
	}
		
	public void setZRest(final double zRest) {
		view.setZRest(zRest);
	}

	@Override public void dataChanged(final DataChangedEvent e) { }
	@Override public void finishedAmountChanged(final FinishedAmountChangedEvent e) { }
	@Override public void exceptionOccured(final ExceptionOccuredEvent e) { 
		Platform.runLater(new Runnable() {
			@Override public void run() {
				view.setErrorMessage(e.getException().getLocalizedMessage());
			}
		});
	}

	@Override
	public void unregister() {
		//TODO implement!
	}
}
