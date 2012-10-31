package eu.robojob.irscw.external.device.cnc;

import eu.robojob.irscw.util.Translator;

public class CNCMillingMachineAlarm {

	public static final int MACHINE = 0;
	public static final int FEED_HOLD = 1;
	public static final int MAIN_PRESSURE = 2;
	public static final int AIR_OIL_TEMP_HIGH = 3;
	public static final int DOOR1_NOT_OPEN = 4;
	public static final int DOOR2_NOT_OPEN = 5;
	public static final int DOOR1_NOT_CLOSED = 6;
	public static final int DOOR2_NOT_CLOSED = 7;
	public static final int CLAMP1_NOT_OPEN = 8;
	public static final int CLAMP2_NOT_OPEN = 9;
	public static final int CLAMP1_NOT_CLOSED = 10;
	public static final int CLAMP2_NOT_CLOSED = 11;
	public static final int WA1_PUT = 12;
	public static final int WA2_PUT = 13;
	public static final int WA1_PICK = 14;
	public static final int WA2_PICK = 15;
	public static final int WA1_CYCLUS_START = 16;
	public static final int WA2_CYCLUS_START = 17;
	public static final int WA1_CLAMP = 18;
	public static final int WA2_CLAMP = 19;
	public static final int WA1_UNCLAMP = 20;
	public static final int WA2_UNCLAMP = 21;
	public static final int MULTIPLE_IPC_REQUESTS = 22;
	
	private int id;
	private Translator translator;
	
	public CNCMillingMachineAlarm(int id) {
		this.id = id;
		this.translator = Translator.getInstance();
	}
	
	public String getMessage() {
		return translator.getTranslation("CNCMachineAlarm." + id);
	}
	
	public int getPriority() {
		return 0;
	}
}
