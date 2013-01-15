package eu.robojob.irscw.ui.general.status;

import java.util.Set;

import javafx.application.Platform;
import eu.robojob.irscw.external.robot.RobotEvent;
import eu.robojob.irscw.process.DeviceStep;
import eu.robojob.irscw.process.PickStep;
import eu.robojob.irscw.process.ProcessFlow.Mode;
import eu.robojob.irscw.process.PutStep;
import eu.robojob.irscw.process.event.ExceptionOccuredEvent;
import eu.robojob.irscw.process.event.FinishedAmountChangedEvent;
import eu.robojob.irscw.process.event.ModeChangedEvent;
import eu.robojob.irscw.process.event.ProcessFlowEvent;
import eu.robojob.irscw.process.event.ProcessFlowListener;
import eu.robojob.irscw.process.event.StatusChangedEvent;
import eu.robojob.irscw.ui.teach.TeachPresenter;
import eu.robojob.irscw.util.Translator;

public class StatusPresenter implements ProcessFlowListener {

	private StatusView view;
	private TeachPresenter parent;
	
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
	
	public StatusPresenter(final StatusView view) {
		this.view = view;
		view.setPresenter(this);
	}
	
	public void setParent(final TeachPresenter parent) {
		this.parent = parent;
	}
	
	public void stopTeaching() {
		parent.stopTeaching();
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
					case StatusChangedEvent.NONE_ACTIVE:
						view.setInfoMessage(Translator.getTranslation(NONE_ACTIVE));
						break;
					case StatusChangedEvent.STARTED:
						if (e.getActiveStep() instanceof PickStep) {
							view.setInfoMessage(Translator.getTranslation(EXECUTE_PICK) + ((DeviceStep) e.getActiveStep()).getDevice().getId() + ".");
						} else if (e.getActiveStep() instanceof PutStep) {
							view.setInfoMessage(Translator.getTranslation(EXECUTE_PUT) + ((DeviceStep) e.getActiveStep()).getDevice().getId() + ".");
						}
						break;
					case StatusChangedEvent.PREPARE_DEVICE:
						if (e.getActiveStep() instanceof PickStep) {
							view.setInfoMessage(((PickStep) e.getActiveStep()).getDevice().getId() + " " + Translator.getTranslation(PREPARE_PICK) + ".");
						} else if (e.getActiveStep() instanceof PutStep) {
							view.setInfoMessage(((PutStep) e.getActiveStep()).getDevice().getId() + " " + Translator.getTranslation(PREPARE_PUT) + ".");
						}
						break;
					case StatusChangedEvent.EXECUTE_TEACHED:
					case StatusChangedEvent.EXECUTE_NORMAL:
						if (e.getActiveStep() instanceof PickStep) {
							view.setInfoMessage(Translator.getTranslation(EXECUTE_PICK) + ((DeviceStep) e.getActiveStep()).getDevice().getId() + ".");
						} else if (e.getActiveStep() instanceof PutStep) {
							view.setInfoMessage(Translator.getTranslation(EXECUTE_PUT) + ((DeviceStep) e.getActiveStep()).getDevice().getId() + ".");
						}
						break;
					case StatusChangedEvent.INTERVENTION_READY:
						throw new IllegalStateException("Intervention not supported while teaching");
					case StatusChangedEvent.PROCESSING_STARTED:
						view.setInfoMessage(((DeviceStep) e.getActiveStep()).getDevice().getId() + " " + Translator.getTranslation(PROCESSING));
						break;
					case StatusChangedEvent.ENDED:
						break;
					case StatusChangedEvent.TEACHING_NEEDED:
						view.setInfoMessage(Translator.getTranslation(TEACHING_NEEDED));
						break;
					case StatusChangedEvent.TEACHING_FINISHED:
						view.setInfoMessage(Translator.getTranslation(TEACHING_FINISHED));
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
		
	public void robotZRestChanged(final RobotEvent event) { }

	@Override public void dataChanged(final ProcessFlowEvent e) { }
	@Override public void finishedAmountChanged(final FinishedAmountChangedEvent e) { }
	@Override public void exceptionOccured(final ExceptionOccuredEvent e) { 
		Platform.runLater(new Runnable() {
			@Override public void run() {
				view.setErrorMessage(e.getException().getLocalizedMessage());
			}
		});
	}
}
