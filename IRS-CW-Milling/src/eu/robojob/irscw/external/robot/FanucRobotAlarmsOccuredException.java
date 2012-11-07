package eu.robojob.irscw.external.robot;

import java.util.Set;

public class FanucRobotAlarmsOccuredException extends RobotActionException {

	private static final long serialVersionUID = 1L;

	private FanucRobot source;
	private Set<FanucRobotAlarm> alarms;
	
	public FanucRobotAlarmsOccuredException(FanucRobot source, Set<FanucRobotAlarm> alarms) {
		this.source = source;
		this.alarms = alarms;
	}
	
	public Set<FanucRobotAlarm> getAlarms() {
		return alarms;
	}
	
	public FanucRobot getSource() {
		return source;
	}
	
	public String getMessage() {
		return "FanucRobot alarm occured: " + alarms;
	}
}
