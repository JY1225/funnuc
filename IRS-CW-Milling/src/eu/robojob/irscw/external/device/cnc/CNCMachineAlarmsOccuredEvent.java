package eu.robojob.irscw.external.device.cnc;

import java.util.Set;

public class CNCMachineAlarmsOccuredEvent extends CNCMachineEvent {

	private Set<CNCMachineAlarm> alarms;

	public CNCMachineAlarmsOccuredEvent(AbstractCNCMachine source, Set<CNCMachineAlarm> alarms) {
		super(source, CNCMachineEvent.ALARM_OCCURED);
		this.alarms = alarms;
	}	
	
	public Set<CNCMachineAlarm> getAlarms() {
		return alarms;
	}
}
