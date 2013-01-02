package eu.robojob.irscw.external.robot;

import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.robojob.irscw.external.communication.AbstractCommunicationException;
import eu.robojob.irscw.threading.MonitoringThread;

public class RobotMonitoringThread extends Thread implements MonitoringThread {

	private static final int REFRESH_TIME = 150;
	
	private AbstractRobot robot;
	private boolean alive;
	private double previousZRest;
	private int previousStatus;
	private Set<RobotAlarm> previousAlarms;
	
	private static Logger logger = LogManager.getLogger(RobotMonitoringThread.class.getName());
	
	public RobotMonitoringThread(final AbstractRobot robot) {
		this.robot = robot;
		this.alive = true;
		this.previousAlarms = new HashSet<RobotAlarm>();
	}
	
	@Override
	public void run() {
		try {
			while (alive) {
				if (robot.isConnected()) {
					try {
						robot.updateStatusZRestAndAlarms();
						int status = robot.getStatus();
						if (status != previousStatus) {
							robot.processRobotEvent(new RobotEvent(robot, RobotEvent.STATUS_CHANGED));
						}
						this.previousStatus = status;
						Set<RobotAlarm> alarms = robot.getAlarms();
						if ((!previousAlarms.containsAll(alarms)) || (!alarms.containsAll(previousAlarms))) {
							robot.processRobotEvent(new RobotEvent(robot, RobotEvent.ALARMS_OCCURED));
						}
						this.previousAlarms = alarms;
						double zrest = robot.getZRest();
						if (zrest != previousZRest) {
							robot.processRobotEvent(new RobotEvent(robot, RobotEvent.ZREST_CHANGED));
						}
					} catch (AbstractCommunicationException | InterruptedException e) {
						if (robot.isConnected()) {
							logger.error(e);
							robot.disconnect();
						}
					}
				}
				try {
					Thread.sleep(REFRESH_TIME);
				} catch (InterruptedException e) {
					// interrupted, so let's just stop, the external communication thread takes care of disconnecting if needed at this point
					alive = false;
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
	
	@Override
	public void interrupt() {
		alive = false;
		super.interrupt();
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
