package eu.robojob.millassist.external.device.stacking.conveyor;


public interface ConveyorListener {

	void layoutChanged();
	void finishedShifted(float distance);
	void unregister();
	
	void conveyorConnected(ConveyorEvent event);
	void conveyorDisconnected(ConveyorEvent event);
	void conveyorStatusChanged(ConveyorEvent event);
	void conveyorAlarmsOccured(ConveyorAlarmsOccuredEvent event);
	
}
