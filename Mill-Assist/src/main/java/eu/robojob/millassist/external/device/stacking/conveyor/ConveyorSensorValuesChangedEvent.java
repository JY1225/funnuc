package eu.robojob.millassist.external.device.stacking.conveyor;

import java.util.List;

public class ConveyorSensorValuesChangedEvent extends ConveyorEvent {

	private List<Integer> sensorValues;
	
	public ConveyorSensorValuesChangedEvent(final Conveyor conveyor, final List<Integer> sensorValues) {
		super(conveyor, ConveyorEvent.SENSOR_VALUES_CHANGED);
		this.sensorValues = sensorValues;
	}

	public List<Integer> getSensorValues() {
		return sensorValues;
	}

}
