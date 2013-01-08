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
import eu.robojob.irscw.process.event.FinishedAmountChangedEvent;
import eu.robojob.irscw.process.event.ModeChangedEvent;
import eu.robojob.irscw.process.event.ProcessFlowEvent;
import eu.robojob.irscw.process.event.ProcessFlowListener;
import eu.robojob.irscw.process.event.StatusChangedEvent;
import eu.robojob.irscw.process.execution.AutomateOptimizedThread;
import eu.robojob.irscw.threading.ThreadManager;
import eu.robojob.irscw.ui.MainContentPresenter;
import eu.robojob.irscw.ui.MainPresenter;
import eu.robojob.irscw.ui.general.flow.FixedProcessFlowPresenter;
import eu.robojob.irscw.util.Translator;

public class AutomatePresenter implements MainContentPresenter, CNCMachineListener, RobotListener, ProcessFlowListener {

	private AutomateView view;
	private FixedProcessFlowPresenter processFlowPresenter;
	private ProcessFlow processFlow;
	
	private Set<AbstractCNCMachine> machines;
	private Set<FanucRobot> robots;
	
	private AutomateOptimizedThread automateThread;
	
	private ProcessFlowTimer processFlowTimer;
	private AutomateTimingThread automateTimingThread;
	
	private boolean alarms;
	
	private static final String PROCESS_FINISHED = "AutomatePresenter.processFinished";
	
	private static Logger logger = LogManager.getLogger(AutomatePresenter.class.getName());
	
	public AutomatePresenter(final AutomateView view, final FixedProcessFlowPresenter processFlowPresenter, final ProcessFlow processFlow, final ProcessFlowTimer processFlowTimer) {
		this.view = view;
		view.setPresenter(this);
		this.processFlowPresenter = processFlowPresenter;
		view.setTop(processFlowPresenter.getView());
		this.processFlow = processFlow;
		this.processFlowTimer = processFlowTimer;
		this.machines = new HashSet<AbstractCNCMachine>();
		this.robots = new HashSet<FanucRobot>();
		this.automateThread = new AutomateOptimizedThread(processFlow);
		this.automateTimingThread = new AutomateTimingThread(this, processFlowTimer);
		this.alarms = false;
	}
	
	@Override
	public AutomateView getView() {
		return view;
	}
	
	public void loadProcessFlow(final ProcessFlow processFlow) {
		processFlowPresenter.loadProcessFlow(processFlow);
		view.setTotalAmount(processFlow.getTotalAmount());
		view.setFinishedAmount(processFlow.getFinishedAmount());
	}
	
	@Override
	public void setActive(final boolean active) {
		if (active) {
			enable();
		} else {
			ThreadManager.stopRunning(automateThread);
			ThreadManager.stopRunning(automateTimingThread);
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
		processFlowPresenter.stopListening();
		machines.clear();
		robots.clear();
		processFlow.removeListener(this);
	}
	
	public int getMainProcessFlowId() {
		return automateThread.getMainProcessFlowId();
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
		processFlowPresenter.startListening();
		processFlow.addListener(this);
		view.setTotalAmount(processFlow.getTotalAmount());
		view.setFinishedAmount(processFlow.getFinishedAmount());
		automateTimingThread = new AutomateTimingThread(this, processFlowTimer);
		ThreadManager.submit(automateTimingThread);
	}
	
	public void clickedStart() {
		if (automateThread != null) {
			ThreadManager.stopRunning(automateThread);
		}
		logger.info("clicked start thread");
		if (!alarms) {
			view.hideAlarmMessage();
		}
		automateThread = new AutomateOptimizedThread(processFlow);
		ThreadManager.submit(automateThread);
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
	
	public void setAlarmStatus(final String alarmStatus) {
		view.setAlarmStatus(alarmStatus);
	}
	
	public void clickedStop() {
		ThreadManager.stopRunning(automateThread);
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
						view.setStatus(Translator.getTranslation(PROCESS_FINISHED));
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
				} catch (InterruptedException e) {
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
				} catch (InterruptedException e) {
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
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void exceptionOccured(final Exception e) {
		logger.error(e);
		e.printStackTrace();
		processFlowPresenter.refresh();
		ThreadManager.stopRunning(automateThread);
		setAlarmStatus("Fout opgetreden: " + e.getMessage() + ". Het proces dient opnieuw doorlopen te worden.");
	}

	//FIXME REVIEW
	@Override
	public void statusChanged(final StatusChangedEvent e) {
	}

	@Override
	public void dataChanged(final ProcessFlowEvent e) {
	}

	@Override
	public void finishedAmountChanged(final FinishedAmountChangedEvent e) {
		Platform.runLater(new Runnable() {
			@Override public void run() {
				view.setTotalAmount(e.getTotalAmount());
				view.setFinishedAmount(e.getFinishedAmount());
			} });
	}

	@Override
	public void robotConnected(final RobotEvent event) {
	}

	@Override
	public void robotDisconnected(final RobotEvent event) {
	}

	@Override
	public void robotStatusChanged(final RobotEvent event) {
		Platform.runLater(new Runnable() {
			@Override public void run() {
				view.setZRest(event.getSource().getZRest());
			} });
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
			} });
	}

	@Override
	public void cNCMachineConnected(final CNCMachineEvent event) {
	}

	@Override
	public void cNCMachineDisconnected(final CNCMachineEvent event) {
	}

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
			} });
	}

	@Override
	public void robotZRestChanged(final RobotEvent event) {
		// TODO Auto-generated method stub
	}

	@Override
	public void robotSpeedChanged(final RobotEvent event) {
		// TODO Auto-generated method stub	
	}

	@Override
	public void cNCMachineStatusChanged(final CNCMachineEvent event) {
		// TODO Auto-generated method stub
	}

	@Override
	public void setParent(final MainPresenter mainPresenter) {
		// TODO Auto-generated method stub
	}
}
