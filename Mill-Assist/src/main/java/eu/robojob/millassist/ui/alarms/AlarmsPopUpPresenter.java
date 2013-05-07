package eu.robojob.millassist.ui.alarms;

import java.util.HashSet;
import java.util.Set;

import javafx.application.Platform;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.robojob.millassist.external.device.AbstractDevice;
import eu.robojob.millassist.external.device.DeviceManager;
import eu.robojob.millassist.external.device.processing.cnc.AbstractCNCMachine;
import eu.robojob.millassist.external.device.processing.cnc.CNCMachineAlarm;
import eu.robojob.millassist.external.device.processing.cnc.CNCMachineAlarmsOccuredEvent;
import eu.robojob.millassist.external.device.processing.cnc.CNCMachineEvent;
import eu.robojob.millassist.external.device.processing.cnc.CNCMachineListener;
import eu.robojob.millassist.external.robot.AbstractRobot;
import eu.robojob.millassist.external.robot.RobotAlarm;
import eu.robojob.millassist.external.robot.RobotAlarmsOccuredEvent;
import eu.robojob.millassist.external.robot.RobotEvent;
import eu.robojob.millassist.external.robot.RobotListener;
import eu.robojob.millassist.external.robot.RobotManager;
import eu.robojob.millassist.process.ProcessFlow;
import eu.robojob.millassist.ui.general.AbstractPopUpPresenter;
import eu.robojob.millassist.util.Translator;

public class AlarmsPopUpPresenter extends AbstractPopUpPresenter<AlarmsPopUpView> implements CNCMachineListener, RobotListener {

	private ProcessFlow processFlow;
	private Set<AbstractCNCMachine> cncMachines;
	private Set<AbstractRobot> robots;
	
	private static final String NOT_CONNECTED_TO = "AlarmsPopUpPresenter.notConnectedTo";
	private static Logger logger = LogManager.getLogger(AlarmsPopUpPresenter.class.getName());
	
	public AlarmsPopUpPresenter(final AlarmsPopUpView view, final ProcessFlow processFlow, 
			final DeviceManager deviceMgr, final RobotManager robotMgr) {
		super(view);
		this.processFlow = processFlow;
		cncMachines = new HashSet<AbstractCNCMachine>();
		robots = new HashSet<AbstractRobot>();
		for (AbstractCNCMachine cncMachine: deviceMgr.getCNCMachines()) {
			cncMachine.addListener(this);
			cncMachines.add(cncMachine);
		}
		for (AbstractRobot robot: robotMgr.getRobots()) {
			robot.addListener(this);
			robots.add(robot);
		}
		updateAlarms();
	}

	@Override
	protected void setViewPresenter() {
		getView().setPresenter(this);
	}
	
	protected void updateAlarms() {
		logger.debug("Updating alarms!");
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
		getView().updateAlarms(alarmStrings);
	}

	@Override 
	public void robotConnected(final RobotEvent event) { 
		Platform.runLater(new Runnable() {
			@Override public void run() {
				updateAlarms();
			}
		});
	}

	@Override 
	public void robotDisconnected(final RobotEvent event) {
		Platform.runLater(new Runnable() {
			@Override public void run() {
				updateAlarms();
			}
		});
	}

	@Override public void robotStatusChanged(final RobotEvent event) { }

	@Override public void robotZRestChanged(final RobotEvent event) { }

	@Override public void robotAlarmsOccured(final RobotAlarmsOccuredEvent event) {
		Platform.runLater(new Runnable() {
			@Override public void run() {
				updateAlarms();
			}
		});
	}

	@Override public void robotSpeedChanged(final RobotEvent event) { }

	@Override public void cNCMachineConnected(final CNCMachineEvent event) {
		Platform.runLater(new Runnable() {
			@Override public void run() {
				updateAlarms();
			}
		});
	}

	@Override public void cNCMachineDisconnected(final CNCMachineEvent event) {
		Platform.runLater(new Runnable() {
			@Override public void run() {
				updateAlarms();
			}
		});
	}

	@Override public void cNCMachineStatusChanged(final CNCMachineEvent event) { }

	@Override public void cNCMachineAlarmsOccured(final CNCMachineAlarmsOccuredEvent event) {
		Platform.runLater(new Runnable() {
			@Override public void run() {
				updateAlarms();
			}
		});
	}

	@Override
	public void unregister() {
		for (AbstractCNCMachine cncMachine : cncMachines) {
			cncMachine.removeListener(this);
		}
		for (AbstractRobot robot : robots) {
			robot.removeListener(this);
		}
		cncMachines.clear();
		robots.clear();
	}

}
