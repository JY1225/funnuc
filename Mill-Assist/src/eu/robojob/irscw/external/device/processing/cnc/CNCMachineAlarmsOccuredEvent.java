package eu.robojob.irscw.external.device.processing.cnc;

import java.util.Set;

public class CNCMachineAlarmsOccuredEvent extends CNCMachineEvent {

	private Set<CNCMachineAlarm> alarms;

	public CNCMachineAlarmsOccuredEvent(final AbstractCNCMachine source, final Set<CNCMachineAlarm> alarms) {
		super(source, CNCMachineEvent.ALARM_OCCURED);
		this.alarms = alarms;
	}	
	
	public Set<CNCMachineAlarm> getAlarms() {
		return alarms;
	}
}
