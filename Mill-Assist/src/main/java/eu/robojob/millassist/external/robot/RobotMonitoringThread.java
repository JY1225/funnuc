package eu.robojob.millassist.external.robot;

import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.robojob.millassist.external.communication.AbstractCommunicationException;
import eu.robojob.millassist.threading.MonitoringThread;

public class RobotMonitoringThread implements Runnable, MonitoringThread {

	private static final int REFRESH_TIME = 250;
	
	private AbstractRobot robot;
	private boolean alive;
	private double previousZRest;
	private int previousStatus;
	private Set<RobotAlarm> previousAlarms;
	private boolean wasConnected;
	
	private static Logger logger = LogManager.getLogger(RobotMonitoringThread.class.getName());
	
	public RobotMonitoringThread(final AbstractRobot robot) {
		this.robot = robot;
		this.alive = true;
		this.previousAlarms = new HashSet<RobotAlarm>();
		this.wasConnected = false;
	}
	
	@Override
	public void run() {
		try {
			while (alive) {
				if (robot.isConnected()) {
					try {
						if (!wasConnected) {
							robot.restartProgram();
							wasConnected = true;
						}
						robot.updateStatusZRestAndAlarms();
						int status = robot.getStatus();
						if (status != previousStatus) {
							robot.processRobotEvent(new RobotEvent(robot, RobotEvent.STATUS_CHANGED));
						}
						this.previousStatus = status;
						Set<RobotAlarm> alarms = robot.getAlarms();
						Set<RobotAlarm> prevAlarmsBuffer = new HashSet<RobotAlarm>(previousAlarms);
						this.previousAlarms = alarms;
						if ((!prevAlarmsBuffer.containsAll(alarms)) || (!alarms.containsAll(prevAlarmsBuffer))) {
							if (alarms.size() == 0) {
								for (RobotAlarm alarm : prevAlarmsBuffer) {
									if (alarm.getId() == RobotAlarm.FAULT_LED) {
										logger.debug("No more alarms (previously: " + prevAlarmsBuffer + "), so sending continue command!");
										robot.continueProgram();
										break;
									}
								}
							}
							robot.processRobotEvent(new RobotAlarmsOccuredEvent(robot, alarms));
						}
						double zrest = robot.getZRest();
						if (zrest != previousZRest) {
							robot.processRobotEvent(new RobotEvent(robot, RobotEvent.ZREST_CHANGED));
						}
					} catch (AbstractCommunicationException | InterruptedException e) {
						if (robot.isConnected()) {
							logger.error(e);
							e.printStackTrace();
							robot.disconnect();
						}
					}
				} else {
					if (wasConnected) {
						wasConnected = false;
					}
				}
				try {
					Thread.sleep(REFRESH_TIME);
				} catch (InterruptedException e) {
					interrupted();
				}
			}
		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
		} catch (Throwable t) {
			logger.error(t);
			t.printStackTrace();
		}
		logger.info(toString() + " ended...");
	}
	
	public void interrupted() {
		alive = false;
	}
	
	@Override
	public String toString() {
		return "RobotMonitoringThread: " + robot.toString();
	}

	@Override
	public void stopExecution() {
		alive = false;
	}
}
