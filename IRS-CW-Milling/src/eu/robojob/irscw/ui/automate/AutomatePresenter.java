package eu.robojob.irscw.ui.automate;

import java.util.HashSet;
import java.util.Set;

import javafx.application.Platform;

import org.apache.log4j.Logger;

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
import eu.robojob.irscw.process.ProcessFlow;
import eu.robojob.irscw.process.event.ActiveStepChangedEvent;
import eu.robojob.irscw.process.event.ExceptionOccuredEvent;
import eu.robojob.irscw.process.event.FinishedAmountChangedEvent;
import eu.robojob.irscw.process.event.ModeChangedEvent;
import eu.robojob.irscw.process.event.ProcessFlowEvent;
import eu.robojob.irscw.process.event.ProcessFlowListener;
import eu.robojob.irscw.threading.ThreadManager;
import eu.robojob.irscw.ui.MainContentPresenter;
import eu.robojob.irscw.ui.MainPresenter;
import eu.robojob.irscw.ui.automate.AutomateView.Status;
import eu.robojob.irscw.ui.main.flow.FixedProcessFlowPresenter;
import eu.robojob.irscw.util.Translator;


public class AutomatePresenter implements MainContentPresenter, CNCMachineListener, FanucRobotListener, ProcessFlowListener {

	private AutomateView view;
	private FixedProcessFlowPresenter processFlowPresenter;
	private ProcessFlow processFlow;
	private MainPresenter parent;
	
	private Set<AbstractCNCMachine> machines;
	private Set<FanucRobot> robots;
	
	private AutomateThread automateThread;
	
	private Translator translator;
	
	private static final Logger logger = Logger.getLogger(AutomatePresenter.class);
	
	public AutomatePresenter(AutomateView view, FixedProcessFlowPresenter processFlowPresenter, ProcessFlow processFlow) {
		this.view = view;
		view.setPresenter(this);
		this.processFlowPresenter = processFlowPresenter;
		view.setTop(processFlowPresenter.getView());
		this.processFlow = processFlow;
		this.machines = new HashSet<AbstractCNCMachine>();
		this.robots = new HashSet<FanucRobot>();
		this.automateThread = new AutomateThread(processFlow);
		this.translator = Translator.getInstance();
	}
	
	public void setParent(MainPresenter parent) {
		this.parent = parent;
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
			for (AbstractCNCMachine machine : machines) {
				machine.removeListener(this);
			}
			for (FanucRobot robot: robots) {
				robot.removeListener(this);
			}
			machines.clear();
			robots.clear();
			processFlow.removeListener(this);
			view.setTotalAmount(processFlow.getTotalAmount());
			view.setFinishedAmount(processFlow.getFinishedAmount());
		}
	}
	
	// we assume all devices are connected, so no extra check is done
	private void enable() {
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
	}
	
	public void clickedStart() {
		ThreadManager.getInstance().submit(automateThread);
	}
	
	public void clickedRestart() {
		processFlow.initialize();
		ThreadManager.getInstance().submit(automateThread);
	}
	
	public void clickedPause() {
		
	}
	
	private void setAutoMode(boolean autoMode) {
		parent.setChangeContentEnabled(!autoMode);
		if (autoMode) {
			view.setProcessRunning();
			view.showPauseButton();
		} else {
			view.setProcessStopped();
			view.showStartButton();
		}
	}

	@Override
	public void modeChanged(final ModeChangedEvent e) {
		Platform.runLater(new Runnable() {
			@Override public void run() {
				switch (e.getMode()) {
					case AUTO :
						setAutoMode(true);
						break;
					case FINISHED :
						setAutoMode(false);
						view.showRestartButton();
						break;
					default:
						setAutoMode(false);
						break;
				}
			}
		});
	}
	
	public void exceptionOccured(Exception e){
		logger.error(e);
		processFlowPresenter.refresh();
		view.setStatus(Status.ERROR, "FOUT: " + e);
		setAutoMode(false);
	}

	@Override
	public void activeStepChanged(final ActiveStepChangedEvent e) {
		Platform.runLater(new Runnable() {
			@Override public void run() {
			switch (e.getStatusId()) {
				case ActiveStepChangedEvent.PICK_PREPARE_DEVICE:
					view.setStatus(Status.OK, translator.getTranslation("pick-prepare-device"));
					break;
				case ActiveStepChangedEvent.PICK_EXECUTE_TEACHED:
					view.setStatus(Status.OK, translator.getTranslation("pick-execute-teached"));
					break;
				case ActiveStepChangedEvent.PICK_EXECUTE_NORMAL:
					view.setStatus(Status.OK, translator.getTranslation("pick-execute-normal"));
					break;
				case ActiveStepChangedEvent.PICK_FINISHED:
					//setStatus(translator.getTranslation("pick-finished"));
					break;
				case ActiveStepChangedEvent.PUT_PREPARE_DEVICE:
					view.setStatus(Status.OK, translator.getTranslation("put-prepare-device"));
					break;
				case ActiveStepChangedEvent.PUT_EXECUTE_TEACHED:
					view.setStatus(Status.OK, translator.getTranslation("put-execute-teached"));
					break;
				case ActiveStepChangedEvent.PUT_EXECUTE_NORMAL:
					view.setStatus(Status.OK, translator.getTranslation("put-execute-normal"));
					break;
				case ActiveStepChangedEvent.PUT_FINISHED:
					if (processFlow.getProcessSteps().get(processFlow.getProcessSteps().size() - 1).equals(e.getActiveStep())) {
						view.setStatus(Status.WARNING, translator.getTranslation("auto-finished"));
					} else {
						view.setStatus(Status.OK, translator.getTranslation("put-finished"));
					}
					break;
				case ActiveStepChangedEvent.PROCESSING_PREPARE_DEVICE:
					view.setStatus(Status.OK, translator.getTranslation("processing-prepare-device"));
					break;
				case ActiveStepChangedEvent.PROCESSING_IN_PROGRESS:
					view.setProcessPaused();
					view.setStatus(Status.OK, translator.getTranslation("processing-in-progress"));
					break;
				case ActiveStepChangedEvent.PROCESSING_FINISHED:
					view.setProcessRunning();
					view.setStatus(Status.OK, translator.getTranslation("processing-finished"));
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
	public void robotConnected(FanucRobotEvent event) {}

	@Override
	public void robotDisconnected(FanucRobotEvent event) {}

	@Override
	public void robotStatusChanged(final FanucRobotStatusChangedEvent event) {
		Platform.runLater(new Runnable() {
			@Override public void run() {
				view.setZRest(event.getStatus().getZRest());
			}});
	}

	@Override
	public void robotAlarmsOccured(FanucRobotAlarmsOccuredEvent event) {}

	@Override
	public void cNCMachineConnected(CNCMachineEvent event) {}

	@Override
	public void cNCMachineDisconnected(CNCMachineEvent event) {}

	@Override
	public void cNCMachineStatusChanged(CNCMachineStatusChangedEvent event) {}

	@Override
	public void cNCMachineAlarmsOccured(CNCMachineAlarmsOccuredEvent event) {}
}
