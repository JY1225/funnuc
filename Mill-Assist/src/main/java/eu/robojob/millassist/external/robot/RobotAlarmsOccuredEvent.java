package eu.robojob.millassist.external.robot;

import java.util.Set;

public class RobotAlarmsOccuredEvent extends RobotEvent {

	private Set<RobotAlarm> alarms;
	
	public RobotAlarmsOccuredEvent(final AbstractRobot source, final Set<RobotAlarm> alarms) {
		super(source, RobotEvent.ALARMS_OCCURED);
		this.alarms = alarms;
	}
	
	public Set<RobotAlarm> getAlarms() {
		return alarms;
	}

}
