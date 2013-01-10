package eu.robojob.irscw.ui.teach;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
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
import eu.robojob.irscw.positioning.Coordinates;
import eu.robojob.irscw.process.ProcessFlow;
import eu.robojob.irscw.process.ProcessFlow.Mode;
import eu.robojob.irscw.process.event.FinishedAmountChangedEvent;
import eu.robojob.irscw.process.event.ModeChangedEvent;
import eu.robojob.irscw.process.event.ProcessFlowEvent;
import eu.robojob.irscw.process.event.ProcessFlowListener;
import eu.robojob.irscw.process.event.StatusChangedEvent;
import eu.robojob.irscw.process.execution.TeachOptimizedThread;
import eu.robojob.irscw.process.execution.TeachThread;
import eu.robojob.irscw.threading.ThreadManager;
import eu.robojob.irscw.ui.MainContentPresenter;
import eu.robojob.irscw.ui.MainPresenter;
import eu.robojob.irscw.ui.general.flow.FixedProcessFlowPresenter;
import eu.robojob.irscw.util.Translator;

public class TeachPresenter implements CNCMachineListener, RobotListener, ProcessFlowListener, MainContentPresenter {

	private TeachView view;
	private FixedProcessFlowPresenter processFlowPresenter;
	private DisconnectedDevicesView teachDisconnectedDevicesView;
	private GeneralInfoPresenter generalInfoPresenter;
	private StatusView teachStatusView;
	private TeachThread teachThread;
	private ProcessFlow processFlow;
	private Map<AbstractCNCMachine, Boolean> machines;
	private Map<AbstractRobot, Boolean> robots;
	private boolean alarms;

	private static Logger logger = LogManager.getLogger(TeachPresenter.class.getName());

	private static final String STARTING_PROCESS = "TeachPresenter.startingProcess";
	private static final String ALARM_OCCURED = "TeachPresenter.alarmOccured";
	private static final String TEACHING_FINISHED = "TeachPresenter.teachingFinished";
	
	public TeachPresenter(final TeachView view, final FixedProcessFlowPresenter processFlowPresenter, final ProcessFlow processFlow, final DisconnectedDevicesView teachDisconnectedDevicesView,
			final GeneralInfoPresenter generalInfoPresenter, final StatusView teachStatusView) {
		this.view = view;
		this.processFlowPresenter = processFlowPresenter;
		view.setTop(processFlowPresenter.getView());
		this.processFlow = processFlow;
		this.teachDisconnectedDevicesView = teachDisconnectedDevicesView;
		this.generalInfoPresenter = generalInfoPresenter;
		generalInfoPresenter.setParent(this);
		this.teachStatusView = teachStatusView;
		teachStatusView.setPresenter(this);
		machines = new HashMap<AbstractCNCMachine, Boolean>();
		robots = new HashMap<AbstractRobot, Boolean>();
		this.alarms = false;
	}

	@Override
	public void setActive(final boolean active) {
		if (active) {
			enable();
		} else {
			if ((teachThread != null) && (teachThread.isRunning())) {
				ThreadManager.stopRunning(teachThread);
			}
			stopListening();
		}
	}
	
	private void stopListening() {
		for (AbstractCNCMachine machine : machines.keySet()) {
			machine.removeListener(this);
		}
		for (AbstractRobot robot : robots.keySet()) {
			robot.removeListener(this);
		}
		processFlowPresenter.stopListening();
		machines.clear();
		robots.clear();
		processFlow.removeListener(this);
	}
	
	private void enable() {
		processFlow.addListener(this);
		processFlowPresenter.refresh();
		for (AbstractDevice device : processFlow.getDevices()) {
			if (device instanceof AbstractCNCMachine) {
				AbstractCNCMachine machine = (AbstractCNCMachine) device;
				machine.addListener(this);
				if (machine.isConnected()) {
					machines.put(machine, true);
				} else {
					machines.put(machine, false);
				}
			}
		}
		for (AbstractRobot robot : processFlow.getRobots()) {
			if (robot instanceof FanucRobot) {
				FanucRobot fRobot = (FanucRobot) robot;
				fRobot.addListener(this);
				if (fRobot.isConnected()) {
					robots.put(fRobot, true);
				} else {
					robots.put(fRobot, false);
				}
			}
		}
		processFlowPresenter.startListening();
		checkAllConnected();
	}
	
	public void checkAllConnected() {
		boolean allConnected = true;
		Set<String> disconnectedDevices = new HashSet<String>();
		for (Entry<AbstractCNCMachine, Boolean> entry : machines.entrySet()) {
			if (!entry.getValue()) {
				allConnected = false;
				disconnectedDevices.add(entry.getKey().getId());
			}
		}
		for (Entry<AbstractRobot, Boolean> entry : robots.entrySet()) {
			if (!entry.getValue()) {
				allConnected = false;
				disconnectedDevices.add(entry.getKey().getId());
			}
		}
		if (!allConnected) {
			showDisconnectedDevices(disconnectedDevices);
		} else {
			showInfoMessage();
		}
	}
	
	public void showDisconnectedDevices(final Set<String> deviceNames) {
		teachDisconnectedDevicesView.setDisconnectedDevices(deviceNames);
		view.setBottom(teachDisconnectedDevicesView);
	}
	
	public void showInfoMessage() {
		view.setBottom(generalInfoPresenter.getView());
	}
	
	
	public void startTeachOptimal() {
		//TODO implement
	}

	public void startFlow(final Coordinates extraFinishedOffset) {
		view.setBottom(teachStatusView);
		if (!alarms) {
			teachStatusView.hideAlarmMessage();
		}
		setStatus(Translator.getTranslation(STARTING_PROCESS));
		processFlow.initialize();
		if (this.teachThread.isRunning()) {
			throw new IllegalStateException("Shouldn't be possible!");
		}
		this.teachThread = new TeachOptimizedThread(processFlow, extraFinishedOffset);
		ThreadManager.submit(teachThread);
	}
	
	public void startTeachAll() {
		view.setBottom(teachStatusView);
		if (!alarms) {
			teachStatusView.hideAlarmMessage();
		}
		setStatus(Translator.getTranslation(STARTING_PROCESS));
		processFlow.initialize();
		if (this.teachThread.isRunning()) {
			throw new IllegalStateException("Shouldn't be possible!");
		}
		this.teachThread = new TeachThread(processFlow);
		ThreadManager.submit(teachThread);
	}
	
	public void stopTeaching() {
		if (teachThread.isRunning()) {
			ThreadManager.stopRunning(teachThread);
		}
		stopListening();
		enable();
	}
	
	public void setStatus(final String status) {
		teachStatusView.setMessage(status);
	}
	
	public void setAlarmStatus(final String alarmStatus) {
		teachStatusView.setAlarmMessage(alarmStatus);
	}
	
	public void exceptionOccured(final Exception e) {
		logger.error(e);
		e.printStackTrace();
		processFlowPresenter.refresh();
		ThreadManager.stopRunning(teachThread);
		setAlarmStatus(Translator.getTranslation(ALARM_OCCURED) + e.getMessage());
	}
	
	@Override
	public void modeChanged(final ModeChangedEvent e) {
		Platform.runLater(new Runnable() {
			@Override public void run() {
				switch (e.getMode()) {
					case TEACH :
						teachStatusView.setProcessRunning();
						break;
					case READY :
						teachStatusView.setProcessStopped();
						setStatus(Translator.getTranslation(TEACHING_FINISHED));
						break;
					default:
						teachStatusView.setProcessStopped();
						break;
				}
			}
		});
	}
	
	@Override
	public void cNCMachineConnected(final CNCMachineEvent event) {
		if (processFlow.getMode() != Mode.TEACH) {
			Platform.runLater(new Runnable() {
				@Override public void run() {
					machines.put(event.getSource(), true);
					checkAllConnected();
				}
			});
		}
	}

	@Override
	public void cNCMachineDisconnected(final CNCMachineEvent event) {
		if (processFlow.getMode() != Mode.TEACH) {
			Platform.runLater(new Runnable() {
				@Override public void run() {
					machines.put(event.getSource(), false);
					checkAllConnected();
				}
			});
		}
	}

	@Override
	public void robotConnected(final RobotEvent event) {
		if (processFlow.getMode() != Mode.TEACH) {
			Platform.runLater(new Runnable() {
				@Override public void run() {
					robots.put(event.getSource(), true);
					checkAllConnected();
				}
			});
		}
	}

	@Override
	public void robotDisconnected(final RobotEvent event) {
		if (processFlow.getMode() != Mode.TEACH) {
			Platform.runLater(new Runnable() {
				@Override public void run() {
					checkAllConnected();
				}
			});
		}
	}
	
	@Override
	public void robotAlarmsOccured(final RobotAlarmsOccuredEvent event) {
		Platform.runLater(new Runnable() {
			@Override public void run() {
				if (event.getAlarms().size() > 0) {
					setAlarmStatus(ALARM_OCCURED);
					alarms = true;
				} else {
					alarms = false;
					try {
						event.getSource().continueProgram();
						teachStatusView.hideAlarmMessage();
					} catch (AbstractCommunicationException | InterruptedException e) {
						exceptionOccured(e);
					}
				}
			} });
	}
	
	@Override
	public void cNCMachineAlarmsOccured(final CNCMachineAlarmsOccuredEvent event) {
		Platform.runLater(new Runnable() {
			@Override public void run() {
				if (event.getAlarms().size() > 0) {
					alarms = true;
					setAlarmStatus(ALARM_OCCURED);
				} else {
					alarms = false;
					teachStatusView.hideAlarmMessage();
				}
			} });
	}
	
	@Override
	public TeachView getView() {
		return view;
	}
	
	public void loadProcessFlow(final ProcessFlow processFlow) {
		processFlowPresenter.loadProcessFlow(processFlow);
	}

	@Override
	public void statusChanged(final StatusChangedEvent e) {
		//FIXME needs to be implemented
	}
	
	@Override public void robotStatusChanged(final RobotEvent event) { }
	@Override public void dataChanged(final ProcessFlowEvent e) { }
	@Override public void finishedAmountChanged(final FinishedAmountChangedEvent e) { }
	@Override public void robotZRestChanged(final RobotEvent event) { }
	@Override public void robotSpeedChanged(final RobotEvent event) { }
	@Override public void setParent(final MainPresenter mainPresenter) { }
	@Override public void cNCMachineStatusChanged(final CNCMachineEvent event) { }
}
