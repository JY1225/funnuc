package eu.robojob.irscw.ui.teach;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
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
import eu.robojob.irscw.threading.ThreadManager;
import eu.robojob.irscw.ui.MainPresenter;
import eu.robojob.irscw.ui.main.flow.FixedProcessFlowPresenter;

public class TeachPresenter implements CNCMachineListener, FanucRobotListener{

	private TeachView view;
	private FixedProcessFlowPresenter processFlowPresenter;
	private MainPresenter parent;
	
	private GeneralInfoView teachGeneralInfoView;
	private DisconnectedDevicesView teachDisconnectedDevicesView;
	private StatusView teachStatusView;
	
	private TeachThread teachThread;
	
	private boolean isTeached;
	private ProcessFlow processFlow;
	
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
		machines = new HashMap<AbstractCNCMachine, Boolean>();
		robots = new HashMap<FanucRobot, Boolean>();
	}

	public void setEnabled(boolean enabled) {
		if (enabled) {
			enable();
		} else {
			ThreadManager.getInstance().stopRunning(teachThread);
			for (AbstractCNCMachine machine : machines.keySet()) {
				machine.removeListener(this);
			}
			for (FanucRobot robot: robots.keySet()) {
				robot.removeListener(this);
			}
		}
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
	
	private void enable() {
		view.setBottom(teachDisconnectedDevicesView);
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
	
	public void showDisconnectedDevices(Set<String> deviceNames) {
		logger.info("about to show " + deviceNames.size() + " disconnected devices");
		view.setBottom(teachDisconnectedDevicesView);
		teachDisconnectedDevicesView.setDisconnectedDevices(deviceNames);
	}
	
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
		setTeachMode(true);
		view.setBottom(teachStatusView);
		logger.info("starten proces!");
		setStatus("Starten proces...");
		setProcessRunning(true);
		processFlow.initialize();
		ThreadManager.getInstance().submit(teachThread);
	}
	
	public void setStatus(String status) {
		teachStatusView.setMessage(status);
	}
	
	public void setProcessRunning(boolean running) {
		teachStatusView.setProcessPaused(!running);
	}
	
	public void exceptionOccured(Exception e){
		logger.error(e);
		processFlowPresenter.refresh();
		setStatus("FOUT: " + e);
		setProcessRunning(false);
		setTeachMode(false);
	}
	
	private void setTeachMode(boolean enable) {
		parent.setMenuBarEnabled(!enable);
	}
	
	@Override
	public void cNCMachineConnected(final CNCMachineEvent event) {
		Platform.runLater(new Runnable() {
			@Override public void run() {
				machines.put(event.getSource(), true);
				checkAllConnected();
			}
		});
	}

	@Override
	public void cNCMachineDisconnected(final CNCMachineEvent event) {
		Platform.runLater(new Runnable() {
			@Override public void run() {
				machines.put(event.getSource(), false);
				checkAllConnected();
			}
		});
	}

	@Override
	public void robotConnected(final FanucRobotEvent event) {
		Platform.runLater(new Runnable() {
			@Override public void run() {
				robots.put(event.getSource(), true);
				checkAllConnected();
			}
		});
	}

	@Override
	public void robotDisconnected(FanucRobotEvent event) {
		Platform.runLater(new Runnable() {
			@Override public void run() {
				checkAllConnected();
			}
		});
	}

	@Override
	public void robotStatusChanged(FanucRobotStatusChangedEvent event) {}
	@Override
	public void robotAlarmsOccured(FanucRobotAlarmsOccuredEvent event) {}
	@Override
	public void cNCMachineStatusChanged(CNCMachineStatusChangedEvent event) {}
	@Override
	public void cNCMachineAlarmsOccured(CNCMachineAlarmsOccuredEvent event) {}
}
