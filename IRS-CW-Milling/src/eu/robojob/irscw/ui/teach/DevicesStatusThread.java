package eu.robojob.irscw.ui.teach;

import java.util.HashSet;
import java.util.Set;

import javafx.application.Platform;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.robojob.irscw.external.device.AbstractDevice;
import eu.robojob.irscw.external.device.processing.cnc.CNCMachineAlarmsOccuredEvent;
import eu.robojob.irscw.external.device.processing.cnc.CNCMachineEvent;
import eu.robojob.irscw.external.device.processing.cnc.CNCMachineListener;
import eu.robojob.irscw.external.robot.AbstractRobot;
import eu.robojob.irscw.external.robot.RobotAlarmsOccuredEvent;
import eu.robojob.irscw.external.robot.RobotEvent;
import eu.robojob.irscw.external.robot.RobotListener;
import eu.robojob.irscw.process.ProcessFlow;

public class DevicesStatusThread extends Thread implements RobotListener, CNCMachineListener {

	private ProcessFlow processFlow;
	private TeachPresenter teachPresenter;
	
	private boolean alive;
	
	private static final int SLEEP_TIME = 1000;
	
	private static final Logger logger = LogManager.getLogger(DevicesStatusThread.class.getName());
	
	public DevicesStatusThread(TeachPresenter teachPresenter, ProcessFlow processFlow) {
		this.teachPresenter = teachPresenter;
		this.processFlow = processFlow;
		alive = true;
	}
	
	@Override
	public void run() {
		while (alive) {
			try {
				if (processFlow == null) {
					throw new IllegalStateException("processlfow is null");
				} else {
					logger.info("About to check status of devices");
					Set<String> disconnectedDevices = new HashSet<String>();
					for (AbstractDevice device : processFlow.getDevices()) {
						if (!device.isConnected()) {
							disconnectedDevices.add(device.getId());
						}
					}
					for (AbstractRobot robot : processFlow.getRobots()) {
						if (!robot.isConnected()) {
							disconnectedDevices.add(robot.getId());
						} 
					}
					if (disconnectedDevices.size() > 0) {
						logger.info("found " + disconnectedDevices.size() + " disconnected devices");
						showDisconnectedDevices(disconnectedDevices);
						try {
							logger.info("about to sleep");
							Thread.sleep(SLEEP_TIME);
						} catch (InterruptedException e) {
							logger.error("woken up from sleep");
							alive = false;
						}
					} else {
						logger.info("no disconnected devices found");
						allDevicesConnected();
						alive = false;
					}
				}
			} catch (Exception e) {
				logger.error(e);
			}
		}
	}
	
	private void showDisconnectedDevices(final Set<String> disconnectedDevices) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				teachPresenter.showDisconnectedDevices(disconnectedDevices);
			}
		});
	}
	
	private void allDevicesConnected() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				teachPresenter.showInfoMessage();
			}
		});
	}
	
	@Override
	public void interrupt() {
		alive = false;
	}

	@Override
	public void cNCMachineConnected(CNCMachineEvent event) {
		
	}

	@Override
	public void cNCMachineDisconnected(CNCMachineEvent event) {
		
	}

	@Override
	public void cNCMachineStatusChanged(CNCMachineEvent event) {
	}

	@Override
	public void cNCMachineAlarmsOccured(CNCMachineAlarmsOccuredEvent event) {
	}

	@Override
	public void robotConnected(RobotEvent event) {
		
	}

	@Override
	public void robotDisconnected(RobotEvent event) {
		
	}

	@Override
	public void robotStatusChanged(RobotEvent event) {
	}

	@Override
	public void robotAlarmsOccured(RobotAlarmsOccuredEvent event) {
	}

	@Override
	public void robotZRestChanged(RobotEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void robotSpeedChanged(RobotEvent event) {
		// TODO Auto-generated method stub
		
	}
	
}
