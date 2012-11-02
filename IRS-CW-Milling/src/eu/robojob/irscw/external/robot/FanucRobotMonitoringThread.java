package eu.robojob.irscw.external.robot;

import org.apache.log4j.Logger;

import eu.robojob.irscw.external.communication.CommunicationException;

public class FanucRobotMonitoringThread extends Thread {

	private static final int REFRESH_TIME = 500;
	private FanucRobot fanucRobot;
	private boolean alive;
	private FanucRobotStatus previousStatus;
	//TODO alarms!
	
	private static Logger logger = Logger.getLogger(FanucRobotMonitoringThread.class);
	
	public FanucRobotMonitoringThread(FanucRobot fanucRobot) {
		this.fanucRobot = fanucRobot;
	}
	
	@Override
	public void run() {
		while (alive) {
			if (fanucRobot.isConnected()) {
				try {
					FanucRobotStatus status = fanucRobot.getStatus();
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
}
