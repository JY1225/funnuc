package eu.robojob.irscw.ui.teach;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javafx.application.Platform;

import org.apache.log4j.Logger;

import eu.robojob.irscw.external.communication.CommunicationException;
import eu.robojob.irscw.external.device.AbstractDevice;
import eu.robojob.irscw.external.device.cnc.AbstractCNCMachine;
import eu.robojob.irscw.external.device.cnc.CNCMachineAlarmsOccuredEvent;
import eu.robojob.irscw.external.device.cnc.CNCMachineEvent;
import eu.robojob.irscw.external.device.cnc.CNCMachineListener;
import eu.robojob.irscw.external.device.cnc.CNCMachineStatusChangedEvent;
import eu.robojob.irscw.external.robot.AbstractRobot;
import eu.robojob.irscw.external.robot.FanucRobot;
import eu.robojob.irscw.external.robot.FanucRobotAlarmsOccuredEvent;
import eu.robojob.irscw.external.robot.FanucRobotEvent;
import eu.robojob.irscw.external.robot.FanucRobotListener;
import eu.robojob.irscw.external.robot.FanucRobotStatusChangedEvent;
import eu.robojob.irscw.process.PickStep;
import eu.robojob.irscw.process.ProcessFlow;
import eu.robojob.irscw.process.ProcessFlow.Mode;
import eu.robojob.irscw.process.PutStep;
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

public class TeachPresenter implements CNCMachineListener, FanucRobotListener, ProcessFlowListener, MainContentPresenter {

	private TeachView view;
	private FixedProcessFlowPresenter processFlowPresenter;
	
	private GeneralInfoView teachGeneralInfoView;
	private DisconnectedDevicesView teachDisconnectedDevicesView;
	private StatusView teachStatusView;
	
	private TeachThread teachThread;
	
	private ProcessFlow processFlow;
	
	private Translator translator;
	
	private static Logger logger = Logger.getLogger(TeachPresenter.class);
	
	private Map<AbstractCNCMachine, Boolean> machines;
	private Map<FanucRobot, Boolean> robots;
	
	private boolean alarms;
	
	public TeachPresenter(TeachView view, FixedProcessFlowPresenter processFlowPresenter, ProcessFlow processFlow, DisconnectedDevicesView teachDisconnectedDevicesView,
			GeneralInfoView teachGeneralInfoView, StatusView teachStatusView) {
		this.view = view;
		this.processFlowPresenter = processFlowPresenter;
		view.setTop(processFlowPresenter.getView());
		this.processFlow = processFlow;
		//this.teachThread = new TeachThread(processFlow);
		this.teachThread = new OptimizedTeachThread(processFlow);
		this.teachDisconnectedDevicesView = teachDisconnectedDevicesView;
		this.teachGeneralInfoView = teachGeneralInfoView;
		teachGeneralInfoView.setPresenter(this);
		this.teachStatusView = teachStatusView;
		teachStatusView.setPresenter(this);
		machines = new HashMap<AbstractCNCMachine, Boolean>();
		robots = new HashMap<FanucRobot, Boolean>();
		this.translator = Translator.getInstance();
		this.alarms = false;
	}
	
	/**
	 * This method is called when the teach-screen is opened (active) or closed (not active)
	 * @param active
	 */
	@Override
	public void setActive(boolean active) {
		if (active) {
			enable();
		} else {
			// disable this view, we don't want to listen anymore to the robots, devices, process 
			// and the teach thread can be stopped (if still running)
			if (teachThread.isRunning()) {
				ThreadManager.getInstance().stopRunning(teachThread);
			}
			stopListening();
		}
	}
	
	/**
	 * Helper method, executed when the teach-screen is opened
	 */
	private void enable() {
		// always check if all devices are connected before continuing
		view.setBottom(teachDisconnectedDevicesView);
		// listen to the process and refresh the process flow view
		processFlow.addListener(this);
		processFlowPresenter.refresh();
		// check status of all devices and start listening to them
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
		for (Entry<FanucRobot, Boolean> entry : robots.entrySet()) {
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
	
	/**
	 * Enable the DisconnectedDevicesView, showing all devices that are not yet connected
	 * @param deviceNames
	 */
	public void showDisconnectedDevices(Set<String> deviceNames) {
		logger.info("about to show " + deviceNames.size() + " disconnected devices");
		teachDisconnectedDevicesView.setDisconnectedDevices(deviceNames);
		view.setBottom(teachDisconnectedDevicesView);
	}
	
	/**
	 * Show the general info message when everything is set so the teaching can begin
	 */
	public void showInfoMessage() {
		view.setBottom(teachGeneralInfoView);
	}
	
	private void stopListening() {
		for (AbstractCNCMachine machine : machines.keySet()) {
			machine.removeListener(this);
		}
		for (FanucRobot robot : robots.keySet()) {
			robot.removeListener(this);
		}
		machines.clear();
		robots.clear();
		processFlow.removeListener(this);
	}

	/**
	 * When start-button is clicked on GeneralInfoView
	 */
	public void startFlow() {
		view.setBottom(teachStatusView);
		if (!alarms) {
			teachStatusView.hideAlarmMessage();
		}
		logger.info("starten proces!");
		setStatus("Starten proces...");
		processFlow.initialize();
		if (this.teachThread.isRunning()) {
			throw new IllegalStateException("Shouldn't be possible!");
		}
		//this.teachThread = new TeachThread(processFlow);
		this.teachThread = new OptimizedTeachThread(processFlow);
		ThreadManager.getInstance().submit(teachThread);
	}
	
	public void startFlowTeachAll() {
		view.setBottom(teachStatusView);
		if (!alarms) {
			teachStatusView.hideAlarmMessage();
		}
		logger.info("starten proces!");
		setStatus("Starten proces...");
		processFlow.initialize();
		if (this.teachThread.isRunning()) {
			throw new IllegalStateException("Shouldn't be possible!");
		}
		this.teachThread = new TeachThread(processFlow);
		ThreadManager.getInstance().submit(teachThread);
	}
	
	public void stopTeaching() {
		if (teachThread.isRunning()) {
			ThreadManager.getInstance().stopRunning(teachThread);
		}
		stopListening();
		enable();
	}
	
	public void setStatus(String status) {
		teachStatusView.setMessage(status);
	}
	
	public void setAlarmStatus(String alarmStatus) {
		teachStatusView.setAlarmMessage(alarmStatus);
	}
	
	public void exceptionOccured(Exception e){
		logger.error(e);
		e.printStackTrace();
		processFlowPresenter.refresh();
		setAlarmStatus("Fout opgetreden: " + e.getMessage() + "\n. Het proces dient opnieuw doorlopen te worden.");
		ThreadManager.getInstance().stopRunning(teachThread);
	}
	
	@Override
	public void modeChanged(final ModeChangedEvent e) {
		Platform.runLater(new Runnable() {
			@Override public void run() {
				logger.info("mode changed: " + e.getMode());
				switch (e.getMode()) {
					case TEACH :
						teachStatusView.setProcessRunning();
						break;
					case READY :
						teachStatusView.setProcessStopped();
						setStatus(translator.getTranslation("teach-finished"));
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
	public void robotConnected(final FanucRobotEvent event) {
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
	public void robotDisconnected(FanucRobotEvent event) {
		if (processFlow.getMode() != Mode.TEACH) {
			Platform.runLater(new Runnable() {
				@Override public void run() {
					checkAllConnected();
				}
			});
		}
	}
	
	@Override
	public void activeStepChanged(final ActiveStepChangedEvent e) {
		Platform.runLater(new Runnable() {
			@Override public void run() {
			switch (e.getStatusId()) {
				case ActiveStepChangedEvent.NONE_ACTIVE:
					setStatus(translator.getTranslation("none-active"));
					break;
				case ActiveStepChangedEvent.PICK_PREPARE_DEVICE:
					setStatus(translator.getTranslation("pick-prepare-device"));
					break;
				case ActiveStepChangedEvent.PICK_EXECUTE_TEACHED:
					setStatus(translator.getTranslation("pick-execute-teached"));
					break;
				case ActiveStepChangedEvent.PICK_EXECUTE_NORMAL:
					setStatus(translator.getTranslation("pick-execute-normal"));
					break;
				case ActiveStepChangedEvent.PICK_FINISHED:
					//setStatus(translator.getTranslation("pick-finished"));
					break;
				case ActiveStepChangedEvent.PUT_PREPARE_DEVICE:
					setStatus(translator.getTranslation("put-prepare-device"));
					break;
				case ActiveStepChangedEvent.PUT_EXECUTE_TEACHED:
					setStatus(translator.getTranslation("put-execute-teached"));
					break;
				case ActiveStepChangedEvent.PUT_EXECUTE_NORMAL:
					setStatus(translator.getTranslation("put-execute-normal"));
					break;
				case ActiveStepChangedEvent.PUT_FINISHED:
					if (processFlow.getProcessSteps().get(processFlow.getProcessSteps().size() - 1).equals(e.getActiveStep())) {
						setStatus(translator.getTranslation("teach-finished"));
					} else {
						setStatus(translator.getTranslation("put-finished"));
					}
					break;
				case ActiveStepChangedEvent.PROCESSING_PREPARE_DEVICE:
					setStatus(translator.getTranslation("processing-prepare-device"));
					break;
				case ActiveStepChangedEvent.PROCESSING_IN_PROGRESS:
					teachStatusView.setProcessPaused();
					setStatus(translator.getTranslation("processing-in-progress"));
					break;
				case ActiveStepChangedEvent.PROCESSING_FINISHED:
					teachStatusView.setProcessRunning();
					setStatus(translator.getTranslation("processing-finished"));
					break;
				case ActiveStepChangedEvent.TEACHING_NEEDED:
					setStatus(translator.getTranslation("teaching-needed"));
					teachStatusView.setProcessPaused();
					break;
				case ActiveStepChangedEvent.TEACHING_FINISHED:
					teachStatusView.setProcessRunning();
					if (e.getActiveStep() instanceof PickStep) {
						setStatus(translator.getTranslation("pick-execute-normal"));
					} else if (e.getActiveStep() instanceof PutStep) {
						setStatus(translator.getTranslation("put-execute-normal"));
					} else {
						throw new IllegalStateException("Teaching finished, but no pick or put step");
					}
					break;
				default:
					throw new IllegalStateException("Unkown process state changed event");
			}
		}});
	}

	@Override
	public void exceptionOccured(final ExceptionOccuredEvent e) {
		if (teachThread.isRunning()) {
			Platform.runLater(new Runnable() {
				@Override public void run() {
					exceptionOccured(e.getE());
				}});
		}
	}

	@Override
	public void robotStatusChanged(final FanucRobotStatusChangedEvent event) {
		Platform.runLater(new Runnable() {
			@Override public void run() {
				teachStatusView.setZRest(event.getStatus().getZRest());
			}});
	}
	
	@Override
	public void robotAlarmsOccured(final FanucRobotAlarmsOccuredEvent event) {
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
						teachStatusView.hideAlarmMessage();
					} catch (CommunicationException e) {
						exceptionOccured(e);
					}
				}
			}});
	}
	
	@Override
	public void cNCMachineStatusChanged(CNCMachineStatusChangedEvent event) {}
	
	@Override
	public void cNCMachineAlarmsOccured(final CNCMachineAlarmsOccuredEvent event) {
		Platform.runLater(new Runnable() {
			@Override public void run() {
				if (event.getAlarms().size() > 0) {
					alarms = true;
					setAlarmStatus("De cnc-machine dat zich een of meerdere alarmen hebben voorgedaan: " + event.getAlarms());
				} else {
					alarms = false;
					teachStatusView.hideAlarmMessage();
				}
			}});
	}
	
	@Override
	public void dataChanged(ProcessFlowEvent e) {}
	@Override
	public void finishedAmountChanged(FinishedAmountChangedEvent e) {}
	
	@Override
	public TeachView getView() {
		return view;
	}
	
	public void loadProcessFlow(ProcessFlow processFlow) {
		processFlowPresenter.loadProcessFlow(processFlow);
	}
	
}
