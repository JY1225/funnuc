package eu.robojob.irscw.external.device.processing.cnc;

public class CNCMachineEvent {

	private AbstractCNCMachine source;
	private int id;
	private long when;
	
	public static final int CNC_MACHINE_CONNECTED = 1;
	public static final int CNC_MACHINE_DISCONNECTED = 2;

	public static final int ALARM_OCCURED = 3;
	public static final int STATUS_CHANGED = 4;
	
	public CNCMachineEvent(AbstractCNCMachine source, int id) {
		this.source = source;
		this.id = id;
		this.when = System.currentTimeMillis();
	}

	public AbstractCNCMachine getSource() {
		return source;
	}

	public int getId() {
		return id;
	}

	public long getWhen() {
		return when;
	}

}
