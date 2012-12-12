package eu.robojob.irscw.external.robot.fanuc;

import java.util.Set;

public class FanucRobotAlarmsOccuredEvent extends FanucRobotEvent {

	private Set<FanucRobotAlarm> alarms;
	
	public FanucRobotAlarmsOccuredEvent(FanucRobot source, Set<FanucRobotAlarm> alarms) {
		super(source, FanucRobotEvent.ALARMS_OCCURED);
		this.alarms = alarms;
	}
	
	public Set<FanucRobotAlarm> getAlarms() {
		return alarms;
	}

}
