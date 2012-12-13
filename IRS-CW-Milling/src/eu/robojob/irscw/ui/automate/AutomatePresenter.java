package eu.robojob.irscw.ui.automate;

import java.util.HashSet;
import java.util.Set;

import javafx.application.Platform;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.robojob.irscw.external.communication.AbstractCommunicationException;
import eu.robojob.irscw.external.device.AbstractDevice;
import eu.robojob.irscw.external.device.processing.cnc.AbstractCNCMachine;
import eu.robojob.irscw.external.device.processing.cnc.CNCMachineAlarmsOccuredEvent;
import eu.robojob.irscw.external.device.processing.cnc.CNCMachineEvent;
import eu.robojob.irscw.external.device.processing.cnc.CNCMachineListener;
import eu.robojob.irscw.external.robot.AbstractRobot;
import eu.robojob.irscw.external.robot.RobotAlarmsOccuredEvent;
import eu.robojob.irscw.external.robot.RobotEvent;
import eu.robojob.irscw.external.robot.RobotListener;
import eu.robojob.irscw.external.robot.fanuc.FanucRobot;
import eu.robojob.irscw.process.ProcessFlow;
import eu.robojob.irscw.process.ProcessFlowTimer;
import eu.robojob.irscw.process.event.ActiveStepChangedEvent;
import eu.robojob.irscw.process.event.ExceptionOccuredEvent;
import eu.robojob.irscw.process.event.FinishedAmountChangedEvent;
import eu.robojob.irscw.process.event.ModeChangedEvent;
import eu.robojob.irscw.process.event.ProcessFlowEvent;
import eu.robojob.irscw.process.event.ProcessFlowListener;
import eu.robojob.irscw.threading.ThreadManager;
import eu.robojob.irscw.ui.MainContentPresenter;
import eu.robojob.irscw.ui.main.flow.FixedProcessFlowPresenter;
import eu.robojob.irscw.util.Translator;

public class AutomatePresenter implements MainContentPresenter, CNCMachineListener, RobotListener, ProcessFlowListener {

	private AutomateView view;
	private FixedProcessFlowPresenter processFlowPresenter;
	private ProcessFlow processFlow;
	
	private Set<AbstractCNCMachine> machines;
	private Set<FanucRobot> robots;
	
	private AutomateThread automateThread;
	
	private Translator translator;
	private ProcessFlowTimer processFlowTimer;
	private AutomateTimingThread automateTimingThread;
	
	private boolean alarms;
	
	private static final Logger logger = LogManager.getLogger(AutomatePresenter.class.getName());
	
	public AutomatePresenter(AutomateView view, FixedProcessFlowPresenter processFlowPresenter, ProcessFlow processFlow, ProcessFlowTimer processFlowTimer) {
		this.view = view;
		view.setPresenter(this);
		this.processFlowPresenter = processFlowPresenter;
		view.setTop(processFlowPresenter.getView());
		this.processFlow = processFlow;
		this.processFlowTimer = processFlowTimer;
		this.machines = new HashSet<AbstractCNCMachine>();
		this.robots = new HashSet<FanucRobot>();
		this.automateThread = new AutomateThread(processFlow);
		this.translator = Translator.getInstance();
		this.automateTimingThread = new AutomateTimingThread(this, processFlowTimer);
		this.alarms = false;
	}
	
	@Override
	public AutomateView getView() {
		return view;
	}
	
	public void loadProcessFlow(ProcessFlow processFlow) {
		processFlowPresenter.loadProcessFlow(processFlow);
		view.setTotalAmount(processFlow.getTotalAmount());
		view.setFinishedAmount(processFlow.getFinishedAmount());
	}
	
	@Override
	public void setActive(boolean active) {
		if (active) {
			enable();
		} else {
			ThreadManager.getInstance().stopRunning(automateThread);
			ThreadManager.getInstance().stopRunning(automateTimingThread);
			stopListening();
			view.setTotalAmount(processFlow.getTotalAmount());
			view.setFinishedAmount(processFlow.getFinishedAmount());
			stopIndications();
		}
	}
	
	private void stopListening() {
		for (AbstractCNCMachine machine : machines) {
			machine.removeListener(this);
		}
		for (FanucRobot robot : robots) {
			robot.removeListener(this);
		}
		machines.clear();
		robots.clear();
		processFlow.removeListener(this);
	}
	
	public void setTimers(final String cycleTime, final String cycleTimePassed, final String timeTillPause, final String timeTillFinished) {
		Platform.runLater(new Runnable() {
			@Override public void run() {
				view.setCycleTime(cycleTime);
			}
		});
		Platform.runLater(new Runnable() {
			@Override public void run() {
				view.setCycleTimePassed(cycleTimePassed);
			}
		});
		Platform.runLater(new Runnable() {
			@Override public void run() {
				view.setTimeTillPause(timeTillPause);
			}
		});
		Platform.runLater(new Runnable() {
			@Override public void run() {
				view.setTimeTillFinished(timeTillFinished);
			}
		});
	}
	
	// we assume all devices are connected, so no extra check is done
	private void enable() {
		processFlowPresenter.refresh();
		for (AbstractDevice device : processFlow.getDevices()) {
			if (device instanceof AbstractCNCMachine) {
				AbstractCNCMachine machine = (AbstractCNCMachine) device;
				machine.addListener(this);
				machines.add(machine);
			}
		}
		for (AbstractRobot robot : processFlow.getRobots()) {
			if (robot instanceof FanucRobot) {
				FanucRobot fRobot = (FanucRobot) robot;
				fRobot.addListener(this);
				robots.add(fRobot);
			}
		}
		processFlow.addListener(this);
		view.setTotalAmount(processFlow.getTotalAmount());
		view.setFinishedAmount(processFlow.getFinishedAmount());
		automateTimingThread = new AutomateTimingThread(this, processFlowTimer);
		ThreadManager.getInstance().submit(automateTimingThread);
	}
	
	public void clickedStart() {
		if (automateThread != null) {
			ThreadManager.getInstance().stopRunning(automateThread);
		}
		logger.info("clicked start thread");
		if (!alarms) {
			view.hideAlarmMessage();
		}
		automateThread = new AutomateThread(processFlow);
		ThreadManager.getInstance().submit(automateThread);
	}
	
	public void clickedReset() {
		//ThreadManager.getInstance().stopRunning(automateThread);
		if (!alarms) {
			view.hideAlarmMessage();
		}
		processFlow.initialize();
		for (AbstractDevice device : processFlow.getDevices()) {
			if (device instanceof AbstractCNCMachine) {
				try {
					((AbstractCNCMachine) device).reset();
				} catch (AbstractCommunicationException | InterruptedException e) {
					logger.error(e);
					e.printStackTrace();
				}
			}
		}
		processFlowPresenter.refresh();
	}
	
	public void clickedPause() {
		view.setStatus("Het proces wordt gepauzeerd na afloop van de huidige actie.");
		automateThread.stopRunning();
	}
	
	public void setAlarmStatus(String alarmStatus) {
		view.setAlarmStatus(alarmStatus);
	}
	
	public void clickedStop() {
		ThreadManager.getInstance().stopRunning(automateThread);
		stopListening();
		enable();		
	}

	@Override
	public void modeChanged(final ModeChangedEvent e) {
		Platform.runLater(new Runnable() {
			@Override public void run() {
				logger.info("mode changed: " + e.getMode());
				switch (e.getMode()) {
					case AUTO :
						view.setProcessRunning();
						view.setRunningButtons();
						stopIndications();
						break;
					case FINISHED :
						view.setProcessStopped();
						view.setNotRunningButtons();
						view.setStatus(translator.getTranslation("process-finished"));
						indicateFinished();
						break;
					case PAUSED :
						indicatePaused();
						view.setProcessStopped();
						view.setNotRunningButtons();
						break;
					default:
						stopIndications();
						view.setProcessStopped();
						view.setNotRunningButtons();
						break;
				}
			}
		});
	}
	
	public void indicateFinished() {
		for (AbstractDevice device : processFlow.getDevices()) {
			if (device instanceof AbstractCNCMachine) {
				try {
					((AbstractCNCMachine) device).indicateAllProcessed();
				} catch (AbstractCommunicationException e) {
					e.printStackTrace();
					exceptionOccured(e);
				} catch(InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void stopIndications() {
		for (AbstractDevice device : processFlow.getDevices()) {
			if (device instanceof AbstractCNCMachine) {
				try {
					((AbstractCNCMachine) device).clearIndications();
				} catch (AbstractCommunicationException e) {
					e.printStackTrace();
					exceptionOccured(e);
				} catch(InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void indicatePaused() {
		for (AbstractDevice device : processFlow.getDevices()) {
			if (device instanceof AbstractCNCMachine) {
				try {
					((AbstractCNCMachine) device).indicateOperatorRequested(true);
				} catch (AbstractCommunicationException e) {
					e.printStackTrace();
					exceptionOccured(e);
				} catch(InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void exceptionOccured(Exception e){
		logger.error(e);
		e.printStackTrace();
		processFlowPresenter.refresh();
		ThreadManager.getInstance().stopRunning(automateThread);
		setAlarmStatus("Fout opgetreden: " + e.getMessage() + ". Het proces dient opnieuw doorlopen te worden.");
	}

	@Override
	public void activeStepChanged(final ActiveStepChangedEvent e) {
		Platform.runLater(new Runnable() {
			@Override public void run() {
			switch (e.getStatusId()) {
				case ActiveStepChangedEvent.NONE_ACTIVE:
					view.setStatus(translator.getTranslation("none-active"));
					break;
				case ActiveStepChangedEvent.PICK_PREPARE_DEVICE:
					view.setStatus(translator.getTranslation("pick-prepare-device"));
					break;
				case ActiveStepChangedEvent.PICK_EXECUTE_TEACHED:
					view.setStatus(translator.getTranslation("pick-execute-teached"));
					break;
				case ActiveStepChangedEvent.PICK_EXECUTE_NORMAL:
					view.setStatus(translator.getTranslation("pick-execute-normal"));
					break;
				case ActiveStepChangedEvent.PICK_FINISHED:
					//setStatus(translator.getTranslation("pick-finished"));
					break;
				case ActiveStepChangedEvent.PUT_PREPARE_DEVICE:
					view.setStatus(translator.getTranslation("put-prepare-device"));
					break;
				case ActiveStepChangedEvent.PUT_EXECUTE_TEACHED:
					view.setStatus(translator.getTranslation("put-execute-teached"));
					break;
				case ActiveStepChangedEvent.PUT_EXECUTE_NORMAL:
					view.setStatus(translator.getTranslation("put-execute-normal"));
					break;
				case ActiveStepChangedEvent.PUT_FINISHED:
					if (processFlow.getProcessSteps().get(processFlow.getProcessSteps().size() - 1).equals(e.getActiveStep())) {
						view.setStatus(translator.getTranslation("auto-finished"));
					} else {
						view.setStatus(translator.getTranslation("put-finished"));
					}
					break;
				case ActiveStepChangedEvent.PROCESSING_PREPARE_DEVICE:
					view.setStatus(translator.getTranslation("processing-prepare-device"));
					break;
				case ActiveStepChangedEvent.PROCESSING_IN_PROGRESS:
					view.setProcessPaused();
					view.setStatus(translator.getTranslation("processing-in-progress"));
					break;
				case ActiveStepChangedEvent.PROCESSING_FINISHED:
					view.setProcessRunning();
					view.setStatus(translator.getTranslation("processing-finished"));
					break;
				case ActiveStepChangedEvent.INTERVENTION_PREPARE_DEVICE:
					view.setStatus(translator.getTranslation("intervention-prepare-device"));
					break;
				case ActiveStepChangedEvent.INTERVENTION_READY:
					view.setStatus(translator.getTranslation("intervention-ready"));
					break;
				case ActiveStepChangedEvent.INTERVENTION_ROBOT_TO_HOME:
					view.setStatus(translator.getTranslation("intervention-robot-home"));
					break;
				case ActiveStepChangedEvent.TEACHING_NEEDED:
					throw new IllegalStateException("Teaching not possible when in auto-mode");
				case ActiveStepChangedEvent.TEACHING_FINISHED:
					throw new IllegalStateException("Teaching not possible when in auto-mode");
				default:
					throw new IllegalStateException("Unkown process state changed event");
			}
		}});
	}

	@Override
	public void exceptionOccured(final ExceptionOccuredEvent e) {
		Platform.runLater(new Runnable() {
			@Override public void run() {
				exceptionOccured(e.getE());
			}});
	}

	@Override
	public void dataChanged(ProcessFlowEvent e) {}

	@Override
	public void finishedAmountChanged(final FinishedAmountChangedEvent e) {
		Platform.runLater(new Runnable() {
			@Override public void run() {
				view.setTotalAmount(e.getTotalAmount());
				view.setFinishedAmount(e.getFinishedAmount());
			}});
	}

	@Override
	public void robotConnected(RobotEvent event) {}

	@Override
	public void robotDisconnected(RobotEvent event) {}

	@Override
	public void robotStatusChanged(final RobotEvent event) {
		Platform.runLater(new Runnable() {
			@Override public void run() {
				view.setZRest(event.getSource().getZRest());
			}});
	}

	@Override
	public void robotAlarmsOccured(final RobotAlarmsOccuredEvent event) {
		Platform.runLater(new Runnable() {
			@Override public void run() {
				if (event.getAlarms().size() > 0) {
					logger.info("Alarm!!");
					setAlarmStatus("De robot geeft aan dat zich een alarm heeft voorgedaan!");
					alarms = true;
				} else {
					alarms = false;
					try {
						event.getSource().continueProgram();
						view.hideAlarmMessage();
					} catch (AbstractCommunicationException | InterruptedException e) {
						exceptionOccured(e);
					}
				}
			}});
	}

	@Override
	public void cNCMachineConnected(CNCMachineEvent event) {}

	@Override
	public void cNCMachineDisconnected(CNCMachineEvent event) {}

	@Override
	public void cNCMachineAlarmsOccured(final CNCMachineAlarmsOccuredEvent event) {
		Platform.runLater(new Runnable() {
			@Override public void run() {
				if (event.getAlarms().size() > 0) {
					alarms = true;
					setAlarmStatus("De cnc-machine dat zich een of meerdere alarmen hebben voorgedaan: " + event.getAlarms());
				} else {
					alarms = false;
					view.hideAlarmMessage();
				}
			}});
	}

	@Override
	public void robotZRestChanged(RobotEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void robotSpeedChanged(RobotEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void cNCMachineStatusChanged(CNCMachineEvent event) {
		// TODO Auto-generated method stub
		
	}
}
