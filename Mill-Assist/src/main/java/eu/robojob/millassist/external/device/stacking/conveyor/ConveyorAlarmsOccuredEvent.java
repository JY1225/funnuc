package eu.robojob.millassist.external.device.stacking.conveyor;

import java.util.Set;

public class ConveyorAlarmsOccuredEvent extends ConveyorEvent {

	private Set<ConveyorAlarm> alarms;
	
	public ConveyorAlarmsOccuredEvent(final AbstractConveyor source, final Set<ConveyorAlarm> alarms) {
		super(source, ConveyorEvent.ALARM_OCCURED);
		this.alarms = alarms;
	}

	public Set<ConveyorAlarm> getAlarms() {
		return alarms;
	}
	
}
