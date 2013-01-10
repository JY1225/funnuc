package eu.robojob.irscw.ui.teach;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javafx.application.Platform;
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
import eu.robojob.irscw.process.ProcessFlow.Mode;
import eu.robojob.irscw.process.execution.TeachOptimizedThread;
import eu.robojob.irscw.process.execution.TeachThread;
import eu.robojob.irscw.threading.ThreadManager;
import eu.robojob.irscw.ui.MainContentPresenter;
import eu.robojob.irscw.ui.MainPresenter;
import eu.robojob.irscw.ui.general.flow.FixedProcessFlowPresenter;

public class TeachPresenter implements CNCMachineListener, RobotListener, MainContentPresenter {

	private TeachView view;
	private FixedProcessFlowPresenter processFlowPresenter;
	private DisconnectedDevicesView teachDisconnectedDevicesView;
	private GeneralInfoPresenter generalInfoPresenter;
	private StatusPresenter statusPresenter;
	private TeachThread teachThread;
	private ProcessFlow processFlow;
	private Map<AbstractCNCMachine, Boolean> machines;
	private Map<AbstractRobot, Boolean> robots;
	
	public TeachPresenter(final TeachView view, final FixedProcessFlowPresenter processFlowPresenter, final ProcessFlow processFlow, final DisconnectedDevicesView teachDisconnectedDevicesView,
			final GeneralInfoPresenter generalInfoPresenter, final StatusPresenter statusPresenter) {
		this.view = view;
		this.processFlowPresenter = processFlowPresenter;
		view.setTop(processFlowPresenter.getView());
		this.processFlow = processFlow;
		this.teachDisconnectedDevicesView = teachDisconnectedDevicesView;
		this.generalInfoPresenter = generalInfoPresenter;
		generalInfoPresenter.setParent(this);
		this.statusPresenter = statusPresenter;
		statusPresenter.setParent(this);
		machines = new HashMap<AbstractCNCMachine, Boolean>();
		robots = new HashMap<AbstractRobot, Boolean>();
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
		processFlow.removeListener(statusPresenter);
	}
	
	private void enable() {
		processFlow.addListener(statusPresenter);
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
		startTeaching(new TeachOptimizedThread(processFlow));
	}
	
	public void startTeachAll() {
		startTeaching(new TeachThread(processFlow));
	}
	
	private void startTeaching(final TeachThread teachThread) {
		view.setBottom(statusPresenter.getView());
		processFlow.initialize();
		if ((this.teachThread != null) && (this.teachThread.isRunning())) {
			throw new IllegalStateException("Teach thread was already running: " + teachThread);
		}
		this.teachThread = new TeachThread(processFlow);
		//ThreadManager.submit(teachThread);
	}
	
	public void stopTeaching() {
		if (teachThread.isRunning()) {
			ThreadManager.stopRunning(teachThread);
		}
		checkAllConnected();
	}
	
	@Override
	public void cNCMachineConnected(final CNCMachineEvent event) {
		Platform.runLater(new Runnable() {
			@Override public void run() {
				machines.put(event.getSource(), true);
				if (processFlow.getMode() != Mode.TEACH) {
					checkAllConnected();
				}
			}
		});
	}

	@Override
	public void cNCMachineDisconnected(final CNCMachineEvent event) {
		Platform.runLater(new Runnable() {
			@Override public void run() {
				machines.put(event.getSource(), false);
				if (processFlow.getMode() != Mode.TEACH) {
					checkAllConnected();
				}
			}
		});
	}

	@Override
	public void robotConnected(final RobotEvent event) {
		Platform.runLater(new Runnable() {
			@Override public void run() {
				robots.put(event.getSource(), true);
				if (processFlow.getMode() != Mode.TEACH) {
					checkAllConnected();
				}
			}
		});
	}

	@Override
	public void robotDisconnected(final RobotEvent event) {
		Platform.runLater(new Runnable() {
			@Override public void run() {
				robots.put(event.getSource(), false);
				if (processFlow.getMode() != Mode.TEACH) {
					checkAllConnected();
				}
			}
		});
	}
	
	@Override
	public TeachView getView() {
		return view;
	}
	
	public void loadProcessFlow(final ProcessFlow processFlow) {
		processFlowPresenter.loadProcessFlow(processFlow);
	}
	
	@Override public void robotStatusChanged(final RobotEvent event) { }
	@Override public void robotZRestChanged(final RobotEvent event) { }
	@Override public void robotSpeedChanged(final RobotEvent event) { }
	@Override public void setParent(final MainPresenter mainPresenter) { }
	@Override public void cNCMachineStatusChanged(final CNCMachineEvent event) { }
	@Override public void robotAlarmsOccured(final RobotAlarmsOccuredEvent event) { }
	@Override public void cNCMachineAlarmsOccured(final CNCMachineAlarmsOccuredEvent event) { }
	
}
