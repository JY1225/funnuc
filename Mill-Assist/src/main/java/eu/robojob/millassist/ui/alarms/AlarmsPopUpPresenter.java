package eu.robojob.millassist.ui.alarms;

import java.util.HashSet;
import java.util.Set;

import javafx.application.Platform;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.robojob.millassist.external.communication.AbstractCommunicationException;
import eu.robojob.millassist.external.device.AbstractDevice;
import eu.robojob.millassist.external.device.DeviceActionException;
import eu.robojob.millassist.external.device.DeviceManager;
import eu.robojob.millassist.external.device.processing.cnc.AbstractCNCMachine;
import eu.robojob.millassist.external.device.processing.cnc.CNCMachineAlarm;
import eu.robojob.millassist.external.device.processing.cnc.CNCMachineAlarmsOccuredEvent;
import eu.robojob.millassist.external.device.processing.cnc.CNCMachineEvent;
import eu.robojob.millassist.external.device.processing.cnc.CNCMachineListener;
import eu.robojob.millassist.external.device.stacking.conveyor.ConveyorAlarmsOccuredEvent;
import eu.robojob.millassist.external.device.stacking.conveyor.ConveyorEvent;
import eu.robojob.millassist.external.device.stacking.conveyor.ConveyorListener;
import eu.robojob.millassist.external.device.stacking.conveyor.AbstractConveyor;
import eu.robojob.millassist.external.device.stacking.conveyor.ConveyorAlarm;
import eu.robojob.millassist.external.robot.AbstractRobot;
import eu.robojob.millassist.external.robot.RobotAlarm;
import eu.robojob.millassist.external.robot.RobotAlarmsOccuredEvent;
import eu.robojob.millassist.external.robot.RobotEvent;
import eu.robojob.millassist.external.robot.RobotListener;
import eu.robojob.millassist.external.robot.RobotManager;
import eu.robojob.millassist.process.ProcessFlow;
import eu.robojob.millassist.process.event.DataChangedEvent;
import eu.robojob.millassist.process.event.DimensionsChangedEvent;
import eu.robojob.millassist.process.event.ExceptionOccuredEvent;
import eu.robojob.millassist.process.event.FinishedAmountChangedEvent;
import eu.robojob.millassist.process.event.ModeChangedEvent;
import eu.robojob.millassist.process.event.ProcessChangedEvent;
import eu.robojob.millassist.process.event.ProcessFlowListener;
import eu.robojob.millassist.process.event.StatusChangedEvent;
import eu.robojob.millassist.ui.MainPresenter;
import eu.robojob.millassist.ui.general.AbstractPopUpPresenter;
import eu.robojob.millassist.util.Translator;

public class AlarmsPopUpPresenter extends AbstractPopUpPresenter<AlarmsPopUpView> implements CNCMachineListener, RobotListener, ConveyorListener, ProcessFlowListener {

	private ProcessFlow processFlow;
	private Set<AbstractCNCMachine> cncMachines;
	private Set<AbstractRobot> robots;
	private Set<AbstractConveyor> conveyors;
		
	private static final String NOT_CONNECTED_TO = "AlarmsPopUpPresenter.notConnectedTo";
	private static Logger logger = LogManager.getLogger(AlarmsPopUpPresenter.class.getName());
	
	public AlarmsPopUpPresenter(final AlarmsPopUpView view, final ProcessFlow processFlow, 
			final DeviceManager deviceMgr, final RobotManager robotMgr) {
		super(view);
		this.processFlow = processFlow;
		processFlow.addListener(this);
		cncMachines = new HashSet<AbstractCNCMachine>();
		robots = new HashSet<AbstractRobot>();
		conveyors = new HashSet<AbstractConveyor>();
		for (AbstractCNCMachine cncMachine: deviceMgr.getCNCMachines()) {
			cncMachine.addListener(this);
			cncMachines.add(cncMachine);
		}
		for (AbstractRobot robot: robotMgr.getRobots()) {
			robot.addListener(this);
			robots.add(robot);
		}
		for (AbstractConveyor conveyor : deviceMgr.getConveyors()) {
			conveyor.addListener(this);
			conveyors.add(conveyor);
		}
		updateAlarms();
		updateButtons();
	}
	
	@Override
	protected void setViewPresenter() {
		getView().setPresenter(this);
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
			} else if (device instanceof AbstractConveyor) {
				Set<ConveyorAlarm> alarms = ((AbstractConveyor) device).getAlarms();
				for (ConveyorAlarm alarm : alarms) {
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
		if (alarmStrings.size() > 0) {
			// alarms, so indicate this!
			for (AbstractDevice device : processFlow.getDevices()) {
				if (device instanceof AbstractCNCMachine) {
					if (device.isConnected()) {
						try {
							((AbstractCNCMachine) device).indicateOperatorRequested(true);
						} catch (AbstractCommunicationException | InterruptedException e) {
							e.printStackTrace();
							logger.error(e);
						}
					}
				}
				if (device instanceof AbstractConveyor) {
					if (device.isConnected()) {
						try {
							((AbstractConveyor) device).indicateOperatorRequested(true);
						} catch (AbstractCommunicationException | InterruptedException e) {
							e.printStackTrace();
							logger.error(e);
						}
					}
				}
			}
			if (getParent() != null) {
				getParent().indicateAlarmsPresent(true);
			}
		} else {
			for (AbstractDevice device : processFlow.getDevices()) {
				if (device instanceof AbstractCNCMachine) {
					if (device.isConnected()) {
						try {
							((AbstractCNCMachine) device).indicateOperatorRequested(false);
						} catch (AbstractCommunicationException | InterruptedException e) {
							e.printStackTrace();
							logger.error(e);
						}
					}
				}
				if (device instanceof AbstractConveyor) {
					if (device.isConnected()) {
						try {
							((AbstractConveyor) device).indicateOperatorRequested(false);
						} catch (AbstractCommunicationException | InterruptedException e) {
							e.printStackTrace();
							logger.error(e);
						}
					}
				}
			}
			if (getParent() != null) {
				getParent().indicateAlarmsPresent(false);
			}
		}
		logger.debug("Updating alarms: " + alarmStrings);
		getView().updateAlarms(alarmStrings);
	}
	
	@Override 
	public void setParent(final MainPresenter parent) {
		super.setParent(parent);
		updateAlarms();
	}
	
	private void updateButtons() {
		Set<AbstractDevice> devices = new HashSet<AbstractDevice>();
		for (AbstractDevice device : processFlow.getDevices()) {
			if ((device instanceof AbstractCNCMachine) || (device instanceof AbstractConveyor)) {
				devices.add(device);
			}
		}
		getView().updateResetButtons(processFlow.getRobots(), devices);
	}

	@Override 
	public void robotConnected(final RobotEvent event) { 
		Platform.runLater(new Runnable() {
			@Override public void run() {
				updateAlarms();
				getView().setRobotConnected(event.getSource(), true);
			}
		});
	}

	@Override 
	public void robotDisconnected(final RobotEvent event) {
		Platform.runLater(new Runnable() {
			@Override public void run() {
				updateAlarms();
				getView().setRobotConnected(event.getSource(), false);
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
				getView().setDeviceConnected(event.getSource(), true);
			}
		});
	}

	@Override public void cNCMachineDisconnected(final CNCMachineEvent event) {
		Platform.runLater(new Runnable() {
			@Override public void run() {
				updateAlarms();
				getView().setDeviceConnected(event.getSource(), false);
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
		for (AbstractConveyor conveyor : conveyors) {
			conveyor.removeListener(this);
		}
		processFlow.removeListener(this);
		cncMachines.clear();
		robots.clear();
		conveyors.clear();
	}

	@Override
	public void layoutChanged() { }

	@Override
	public void conveyorConnected(final ConveyorEvent event) {
		Platform.runLater(new Runnable() {
			@Override public void run() {
				updateAlarms();
				getView().setDeviceConnected(event.getSource(), true);
			}
		});
	}

	@Override
	public void conveyorDisconnected(final ConveyorEvent event) {
		Platform.runLater(new Runnable() {
			@Override public void run() {
				updateAlarms();
				getView().setDeviceConnected(event.getSource(), false);
			}
		});
	}

	@Override
	public void conveyorStatusChanged(final ConveyorEvent event) { }

	@Override
	public void conveyorAlarmsOccured(final ConveyorAlarmsOccuredEvent event) {
		Platform.runLater(new Runnable() {
			@Override public void run() {
				updateAlarms();
			}
		});
	}

	public void resetDevice(final AbstractDevice device) {
		try {
			device.reset();
		} catch (AbstractCommunicationException | DeviceActionException | InterruptedException e) {
			e.printStackTrace();
			logger.error(e);
		}
	}
	
	public void resetRobot(final AbstractRobot robot) {
		try {
			robot.reset();
		} catch (AbstractCommunicationException | InterruptedException e) {
			e.printStackTrace();
			logger.error(e);
		}
	}

	@Override public void modeChanged(final ModeChangedEvent e) { }

	@Override public void statusChanged(final StatusChangedEvent e) { }

	@Override
	public void dataChanged(final DataChangedEvent e) {
		if (e instanceof ProcessChangedEvent) {
			updateAlarms();
			updateButtons();
		}
	}

	@Override public void finishedAmountChanged(final FinishedAmountChangedEvent e) { }

	@Override public void exceptionOccured(final ExceptionOccuredEvent e) { }
	
	@Override public void finishedShifted(final float distance) { }
	
	@Override public void dimensionChanged(DimensionsChangedEvent e) {	}
}
