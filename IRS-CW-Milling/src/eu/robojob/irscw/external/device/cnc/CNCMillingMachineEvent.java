package eu.robojob.irscw.external.device.cnc;

public class CNCMillingMachineEvent {

	private CNCMillingMachine source;
	private int id;
	private long when;
	
	public static final int CNC_MILLING_CONNECTED = 1;
	public static final int CNC_MILLING_DISCONNECTED = 2;

	public static final int ALARM_OCCURED = 3;
	
}
