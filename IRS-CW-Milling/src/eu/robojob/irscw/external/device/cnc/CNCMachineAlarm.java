package eu.robojob.irscw.external.device.cnc;

import eu.robojob.irscw.util.Translator;

public class CNCMachineAlarm {

	public static final int MACHINE = 0;
	public static final int FEED_HOLD = 1;
	public static final int MAIN_PRESSURE = 2;
	public static final int OIL_TEMP_HIGH = 3;
	public static final int OIL_LEVEL_LOW = 4;
	public static final int DOOR1_NOT_OPEN = 5;
	public static final int DOOR2_NOT_OPEN = 6;
	public static final int DOOR1_NOT_CLOSED = 7;
	public static final int DOOR2_NOT_CLOSED = 8;
	public static final int CLAMP1_NOT_OPEN = 9;
	public static final int CLAMP2_NOT_OPEN = 10;
	public static final int CLAMP1_NOT_CLOSED = 11;
	public static final int CLAMP2_NOT_CLOSED = 12;
	public static final int WA1_PUT = 13;
	public static final int WA2_PUT = 14;
	public static final int WA1_PICK = 15;
	public static final int WA2_PICK = 16;
	public static final int WA1_CYCLUS_START = 17;
	public static final int WA2_CYCLUS_START = 18;
	public static final int WA1_CLAMP = 19;
	public static final int WA2_CLAMP = 20;
	public static final int WA1_UNCLAMP = 21;
	public static final int WA2_UNCLAMP = 22;
	public static final int MULTIPLE_IPC_REQUESTS = 23;
	
	private int id;
	private Translator translator;
	
	public CNCMachineAlarm(int id) {
		this.id = id;
		this.translator = Translator.getInstance();
	}
	
	public String getMessage() {
		return translator.getTranslation("CNCMachineAlarm." + id);
	}
	
	public int getPriority() {
		return 0;
	}
	
	public int getId() {
		return id;
	}
	
	@Override
	public int hashCode() {
		return id;
	}
	
	public boolean equals(Object o) {
		if (o instanceof CNCMachineAlarm) {
			if (((CNCMachineAlarm) o).getId() == id) {
				return true;
			}
		}
		return false;
	}
	
	public String toString() {
		return id + "";
	}
}
