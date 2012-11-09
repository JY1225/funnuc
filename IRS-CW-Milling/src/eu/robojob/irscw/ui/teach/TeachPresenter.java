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
import eu.robojob.irscw.external.device.cnc.CNCMachineAlarmsOccuredException;
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
import eu.robojob.irscw.ui.MainPresenter;
import eu.robojob.irscw.ui.main.flow.FixedProcessFlowPresenter;
import eu.robojob.irscw.util.Translator;

public class TeachPresenter implements CNCMachineListener, FanucRobotListener, ProcessFlowListener, MainContentPresenter {

	private TeachView view;
	private FixedProcessFlowPresenter processFlowPresenter;
	private MainPresenter parent;
	
	private GeneralInfoView teachGeneralInfoView;
	private DisconnectedDevicesView teachDisconnectedDevicesView;
	private StatusView teachStatusView;
	
	private TeachThread teachThread;
	
	private boolean isTeached;
	private ProcessFlow processFlow;
	
	private Translator translator;
	
	private static Logger logger = Logger.getLogger(TeachPresenter.class);
	
	private Map<AbstractCNCMachine, Boolean> machines;
	private Map<FanucRobot, Boolean> robots;
	
	public TeachPresenter(TeachView view, FixedProcessFlowPresenter processFlowPresenter, ProcessFlow processFlow, DisconnectedDevicesView teachDisconnectedDevicesView,
			GeneralInfoView teachGeneralInfoView, StatusView teachStatusView) {
		this.view = view;
		this.processFlowPresenter = processFlowPresenter;
		view.setTop(processFlowPresenter.getView());
		this.processFlow = processFlow;
		this.teachThread = new TeachThread(processFlow);
		isTeached = false;
		this.teachDisconnectedDevicesView = teachDisconnectedDevicesView;
		this.teachGeneralInfoView = teachGeneralInfoView;
		teachGeneralInfoView.setPresenter(this);
		this.teachStatusView = teachStatusView;
		teachStatusView.setPresenter(this);
		machines = new HashMap<AbstractCNCMachine, Boolean>();
		robots = new HashMap<FanucRobot, Boolean>();
		this.translator = Translator.getInstance();
	}

	@Override
	public void setActive(boolean active) {
		logger.info("teach presenter set: " + active);
		if (active) {
			enable();
		} else {
			logger.info("hello");
			ThreadManager.getInstance().stopRunning(teachThread);
			for (AbstractCNCMachine machine : machines.keySet()) {
				machine.removeListener(this);
			}
			for (FanucRobot robot: robots.keySet()) {
				robot.removeListener(this);
			}
			machines.clear();
			robots.clear();
			processFlow.removeListener(this);
		}
	}
	
	private void enable() {
		view.setBottom(teachDisconnectedDevicesView);
		processFlow.addListener(this);
		for (AbstractCNCMachine machine : machines.keySet()) {
			machine.removeListener(this);
		}
		for (FanucRobot robot : robots.keySet()) {
			robot.removeListener(this);
		}
		machines.clear();
		robots.clear();
		//TODO for now we assume the devices/robots of the processflow will not change, so we register as a listener to all the devices/robots contained in the processflow
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
	
	public void showDisconnectedDevices(Set<String> deviceNames) {
		logger.info("about to show " + deviceNames.size() + " disconnected devices");
		view.setBottom(teachDisconnectedDevicesView);
		teachDisconnectedDevicesView.setDisconnectedDevices(deviceNames);
	}
	
	@Override
	public TeachView getView() {
		return view;
	}
	
	public void setParent(MainPresenter parent) {
		this.parent = parent;
	}
	
	public void loadProcessFlow(ProcessFlow processFlow) {
		processFlowPresenter.loadProcessFlow(processFlow);
	}
	
	public void showInfoMessage() {
		view.setBottom(teachGeneralInfoView);
	}
	
	public boolean isTeached() {
		return isTeached;
	}
	
	public void startFlow() {
		view.setBottom(teachStatusView);
		logger.info("starten proces!");
		setStatus("Starten proces...");
		processFlow.initialize();
		this.teachThread = new TeachThread(processFlow);
		ThreadManager.getInstance().submit(teachThread);
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
		setStatus("Er heeft zich een fout voorgedaan: " + e + "\n. Het proces dient opnieuw doorlopen te worden.");
		setTeachMode(false);
		ThreadManager.getInstance().stopRunning(teachThread);
	}
	
	private void setTeachMode(boolean enable) {
		parent.setChangeContentEnabled(!enable);
		if (enable) {
			teachStatusView.setProcessRunning();
		} else {
			teachStatusView.setProcessStopped();
		}
	}
	
	@Override
	public void modeChanged(final ModeChangedEvent e) {
		Platform.runLater(new Runnable() {
			@Override public void run() {
				switch (e.getMode()) {
					case TEACH :
						setTeachMode(true);
						break;
					default:
						setTeachMode(false);
						break;
				}
			}
		});
	}
	
	@Override
	public void cNCMachineConnected(final CNCMachineEvent event) {
		logger.info("CONNEECTEED");
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
					setAlarmStatus("De robot geeft aan dat zich een alarm heeft voorgedaan!");
				} else {
					setAlarmStatus("");
					if (teachThread.isRunning()) {
						try {
							event.getSource().continueProgram();
						} catch (CommunicationException e) {
							exceptionOccured(e);
						}
					}
				}
			}});
	}
	
	@Override
	public void cNCMachineStatusChanged(CNCMachineStatusChangedEvent event) {}
	@Override
	public void cNCMachineAlarmsOccured(final CNCMachineAlarmsOccuredEvent event) {
		if (event.getAlarms().size() > 0) {
			Platform.runLater(new Runnable() {
				@Override public void run() {
					exceptionOccured(new CNCMachineAlarmsOccuredException(event.getSource(), event.getAlarms()));
				}});
		} else {
			
		}
	}
	@Override
	public void dataChanged(ProcessFlowEvent e) {}
	@Override
	public void finishedAmountChanged(FinishedAmountChangedEvent e) {}
	
}
