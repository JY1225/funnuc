package eu.robojob.millassist.external.device.stacking.conveyor.normal;

public class ConveyorEvent extends eu.robojob.millassist.external.device.stacking.conveyor.ConveyorEvent {

	public static final int SENSOR_VALUES_CHANGED = 5;

	public ConveyorEvent(final Conveyor source, final int id) {
		super(source, id);
	}

}
