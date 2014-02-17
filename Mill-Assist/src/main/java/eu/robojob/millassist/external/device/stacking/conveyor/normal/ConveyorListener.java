package eu.robojob.millassist.external.device.stacking.conveyor.normal;

public interface ConveyorListener extends eu.robojob.millassist.external.device.stacking.conveyor.ConveyorListener {

	void sensorValuesChanged(ConveyorSensorValuesChangedEvent event);
}
