package eu.robojob.irscw.external.device.cnc;

import java.util.Set;

public class CNCMachineAlarmsOccuredException extends Exception {
	
	private static final long serialVersionUID = 1L;

	private AbstractCNCMachine source;
	private Set<CNCMachineAlarm> alarms;
	
	public CNCMachineAlarmsOccuredException(AbstractCNCMachine source, Set<CNCMachineAlarm> alarms) {
		this.source = source;
		this.alarms = alarms;
	}
	
	public Set<CNCMachineAlarm> getAlarms() {
		return alarms;
	}
	
	public AbstractCNCMachine getSource() {
		return source;
	}
	
	public String getMessage() {
		return "FanucRobot alarm occured: " + alarms;
	}
}
