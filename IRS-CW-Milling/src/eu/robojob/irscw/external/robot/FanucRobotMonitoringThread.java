package eu.robojob.irscw.external.robot;

import org.apache.log4j.Logger;

import eu.robojob.irscw.external.communication.CommunicationException;
import eu.robojob.irscw.threading.MonitoringThread;

public class FanucRobotMonitoringThread extends Thread implements MonitoringThread{

	private static final int REFRESH_TIME = 50;
	private FanucRobot fanucRobot;
	private boolean alive;
	private FanucRobotStatus previousStatus;
	//TODO alarms!
	
	private static Logger logger = Logger.getLogger(FanucRobotMonitoringThread.class);
	
	public FanucRobotMonitoringThread(FanucRobot fanucRobot) {
		this.alive = true;
		this.fanucRobot = fanucRobot;
	}
	
	@Override
	public void run() {
		while (alive) {
			if (fanucRobot.isConnected()) {
				try {
					fanucRobot.updateStatus();
					FanucRobotStatus status = fanucRobot.getStatus();
					if ((previousStatus == null) || (status.getControllerString() != previousStatus.getControllerString()) || (status.getZRest() != previousStatus.getZRest())) {
						fanucRobot.processFanucRobotEvent(new FanucRobotStatusChangedEvent(fanucRobot, status));
					}
					if ((previousStatus != null) && ((previousStatus.getErrorId() != status.getErrorId()) || (previousStatus.getControllerValue() != status.getControllerValue()))) {
						fanucRobot.processFanucRobotEvent(new FanucRobotAlarmsOccuredEvent(fanucRobot, status.getAlarms()));
					}
					this.previousStatus = status;
				} catch (CommunicationException e) {
					logger.error(e);
				}
			}
			try {
				Thread.sleep(REFRESH_TIME);
			} catch (InterruptedException e) {
				// interrupted, so let's just stop
				alive = false;
			}
		}
		logger.info(toString() + " ended...");
	}
	
	@Override
	public void interrupt() {
		alive = false;
	}
	
	@Override
	public String toString() {
		return "FanucRobotMonitoringThread: " + fanucRobot.toString();
	}

	@Override
	public void stopExecution() {
		alive = false;
	}
}
