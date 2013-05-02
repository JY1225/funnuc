package eu.robojob.irscw.ui.general;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javafx.application.Platform;
import javafx.scene.Node;
import eu.robojob.irscw.external.device.AbstractDevice;
import eu.robojob.irscw.external.device.processing.cnc.AbstractCNCMachine;
import eu.robojob.irscw.external.device.processing.cnc.CNCMachineAlarm;
import eu.robojob.irscw.external.device.processing.cnc.CNCMachineAlarmsOccuredEvent;
import eu.robojob.irscw.external.device.processing.cnc.CNCMachineEvent;
import eu.robojob.irscw.external.device.processing.cnc.CNCMachineListener;
import eu.robojob.irscw.external.robot.AbstractRobot;
import eu.robojob.irscw.external.robot.RobotAlarm;
import eu.robojob.irscw.external.robot.RobotAlarmsOccuredEvent;
import eu.robojob.irscw.external.robot.RobotEvent;
import eu.robojob.irscw.external.robot.RobotListener;
import eu.robojob.irscw.external.robot.fanuc.FanucRobot;
import eu.robojob.irscw.process.ProcessFlow;
import eu.robojob.irscw.ui.MainPresenter;
import eu.robojob.irscw.ui.general.flow.FixedProcessFlowPresenter;
import eu.robojob.irscw.ui.general.status.StatusPresenter;
import eu.robojob.irscw.util.Translator;

public abstract class ExecutionPresenter implements CNCMachineListener, RobotListener, MainContentPresenter, ContentPresenter {

	private FixedProcessFlowPresenter processFlowPresenter;
	private StatusPresenter statusPresenter;
	private ProcessFlow processFlow;
	private Map<AbstractCNCMachine, Boolean> machines;
	private Map<AbstractRobot, Boolean> robots;	
	
	private static final String NOT_CONNECTED_TO = "TeachPresenter.notConnectedTo";
	
	public ExecutionPresenter(final FixedProcessFlowPresenter processFlowPresenter, final ProcessFlow processFlow, final StatusPresenter statusPresenter) {
		this.processFlowPresenter = processFlowPresenter;
		this.processFlow = processFlow;
		this.statusPresenter = statusPresenter;
		this.machines = new HashMap<AbstractCNCMachine, Boolean>();
		this.robots = new HashMap<AbstractRobot, Boolean>();
		updateAlarms();
	}
	
	@Override
	public void setActive(final boolean active) {
		if (active) {
			enable();
		} else {
			if (isRunning()) {
				stopRunning();
			}
			stopListening();
		}
	}
	
	public void loadProcessFlow(final ProcessFlow processFlow) {
		processFlowPresenter.loadProcessFlow(processFlow);
	}
	
	public ProcessFlow getProcessFlow() {
		return processFlow;
	}
	
	//TODO check if this is still ok if the devices contained in a processflow can change!
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
		startListening(processFlow);
		checkAllConnected();
	}
	
	public void checkAllConnected() {
		boolean allConnected = true;
		Set<String> disconnectedDevices = new HashSet<String>();
		for (Entry<AbstractCNCMachine, Boolean> entry : machines.entrySet()) {
			if (!entry.getValue()) {
				allConnected = false;
				disconnectedDevices.add(entry.getKey().getName());
			}
		}
		for (Entry<AbstractRobot, Boolean> entry : robots.entrySet()) {
			if (!entry.getValue()) {
				allConnected = false;
				disconnectedDevices.add(entry.getKey().getName());
			}
		}
		if (!allConnected) {
			disconnectedDevices(disconnectedDevices);
		} else {
			allConnected();
		}
	}
	
	public abstract void startListening(ProcessFlow processFlow);
	public abstract void stopListening(ProcessFlow processFlow);
	public abstract void stopRunning();
	public abstract void allConnected();
	public abstract void disconnectedDevices(Set<String> disconnectedDevices);
	public abstract boolean isRunning();
	
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
		stopListening(processFlow);
	}

	@Override
	public void setParent(final MainPresenter mainPresenter) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Node getView() {
		// TODO Auto-generated method stub
		return null;
	}
	
	protected void updateAlarms() {
		Set<String> alarmStrings = new HashSet<String>();
		for (AbstractDevice device : processFlow.getDevices()) {
			if (!device.isConnected()) {
				alarmStrings.add(Translator.getTranslation(NOT_CONNECTED_TO) + " " + device.getName() + ".");
			} else if (device instanceof AbstractCNCMachine) {
				Set<CNCMachineAlarm> alarms = ((AbstractCNCMachine) device).getAlarms();
				for (CNCMachineAlarm alarm : alarms) {
					alarmStrings.add(alarm.getLocalizedMessage());
				}
			}
			
		}
		for (AbstractRobot robot : processFlow.getRobots()) {
			if (!robot.isConnected()) {
				alarmStrings.add(Translator.getTranslation(NOT_CONNECTED_TO) + " " + robot.getName() + ".");
			} else {
				for (RobotAlarm alarm : robot.getAlarms()) {
					alarmStrings.add(alarm.getLocalizedMessage());
				}
			}
		}
		statusPresenter.updateAlarms(alarmStrings);
	}
	
	@Override
	public void cNCMachineConnected(final CNCMachineEvent event) {
		Platform.runLater(new Runnable() {
			@Override public void run() {
				machines.put(event.getSource(), true);
				if (isRunning()) {
					updateAlarms();
				} else {
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
				if (isRunning()) {
					updateAlarms();
				} else {
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
				if (isRunning()) {
					updateAlarms();
				} else {
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
				if (isRunning()) {
					updateAlarms();
				} else {
					checkAllConnected();
				}
			}
		});
	}

	@Override public void robotStatusChanged(final RobotEvent event) { }
	@Override public void robotZRestChanged(final RobotEvent event) { 
		Platform.runLater(new Runnable() {
			@Override public void run() {
				if (isRunning()) {
					statusPresenter.setZRest(event.getSource().getZRest());
				} else {
					statusPresenter.setZRest(-1);
				}
			}
		});
	}
	@Override public void robotSpeedChanged(final RobotEvent event) { }
	@Override public void cNCMachineStatusChanged(final CNCMachineEvent event) { }
	@Override public void robotAlarmsOccured(final RobotAlarmsOccuredEvent event) {
		Platform.runLater(new Runnable() {
			@Override public void run() {
				updateAlarms();
			}
		});
	}
	@Override public void cNCMachineAlarmsOccured(final CNCMachineAlarmsOccuredEvent event) { 
		Platform.runLater(new Runnable() {
			@Override public void run() {
				updateAlarms();
			}
		});
	}
}
