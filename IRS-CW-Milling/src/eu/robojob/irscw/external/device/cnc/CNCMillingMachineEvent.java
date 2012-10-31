package eu.robojob.irscw.external.device.cnc;

public class CNCMillingMachineEvent {

	private CNCMillingMachine source;
	private int id;
	private long when;
	
	public static final int CNC_MILLING_CONNECTED = 1;
	public static final int CNC_MILLING_DISCONNECTED = 2;

	public static final int ALARM_OCCURED = 3;
	public static final int STATUS_CHANGED = 4;
	
	public CNCMillingMachineEvent(CNCMillingMachine source, int id) {
		this.source = source;
		this.id = id;
		this.when = System.currentTimeMillis();
	}

	public CNCMillingMachine getSource() {
		return source;
	}

	public void setSource(CNCMillingMachine source) {
		this.source = source;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public long getWhen() {
		return when;
	}

	public void setWhen(long when) {
		this.when = when;
	}
}
