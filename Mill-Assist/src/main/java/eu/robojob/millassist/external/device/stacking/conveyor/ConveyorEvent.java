package eu.robojob.millassist.external.device.stacking.conveyor;

public class ConveyorEvent {

	private Conveyor source;
	private int id;
	private long timestamp;
	
	public static final int CONVEYOR_CONNECTED = 1;
	public static final int CONVEYOR_DISCONNECTED = 2;
	public static final int ALARM_OCCURED = 3;
	public static final int STATUS_CHANGED = 4;
	public static final int SENSOR_VALUES_CHANGED = 5;
	
	public ConveyorEvent(final Conveyor source, final int id) {
		this.source = source;
		this.id = id;
		this.timestamp = System.currentTimeMillis();
	}

	public Conveyor getSource() {
		return source;
	}

	public int getId() {
		return id;
	}

	public long getTimestamp() {
		return timestamp;
	}

}
