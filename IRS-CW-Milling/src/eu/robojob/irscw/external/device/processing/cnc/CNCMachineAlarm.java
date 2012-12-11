package eu.robojob.irscw.external.device.processing.cnc;

import java.util.HashSet;
import java.util.Set;

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
	
	private Translator translator;
	
	private int id;
	
	public CNCMachineAlarm(int id) {
		this.id = id;
		this.translator = Translator.getInstance();
	}
	
	public String getLocalizedMessage() {
		return translator.getTranslation("CNCMachineAlarm." + id);
	}
	
	public String getMessage() {
		return "CNC Machine alarm: id = " + id;
	}
	
	//TODO implement priorities, for now: all the same
	public int getPriority() {
		return 5;
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
		return "CNCMachineAlarm: " + id;
	}
	
	public static Set<CNCMachineAlarm> parseAlarms(int alarmReg1, int alarmReg2) {
		Set<CNCMachineAlarm> alarms = new HashSet<CNCMachineAlarm>();
		if ((alarmReg1 & CNCMachineConstants.ALR_MACHINE)>0) {
			alarms.add(new CNCMachineAlarm(CNCMachineAlarm.MACHINE));
		}
		if ((alarmReg1 & CNCMachineConstants.ALR_FEED_HOLD)>0) {
			alarms.add(new CNCMachineAlarm(CNCMachineAlarm.FEED_HOLD));
		}
		if ((alarmReg1 & CNCMachineConstants.ALR_MAIN_PRESSURE)>0) {
			alarms.add(new CNCMachineAlarm(CNCMachineAlarm.MAIN_PRESSURE));
		}
		if ((alarmReg1 & CNCMachineConstants.ALR_OIL_TEMP_HIGH)>0) {
			alarms.add(new CNCMachineAlarm(CNCMachineAlarm.OIL_TEMP_HIGH));
		}
		if ((alarmReg1 & CNCMachineConstants.ALR_OIL_LEVEL_LOW)>0) {
			alarms.add(new CNCMachineAlarm(CNCMachineAlarm.OIL_LEVEL_LOW));
		}
		if ((alarmReg1 & CNCMachineConstants.ALR_DOOR1_NOT_OPEN)>0) {
			alarms.add(new CNCMachineAlarm(CNCMachineAlarm.DOOR1_NOT_OPEN));
		}
		if ((alarmReg1 & CNCMachineConstants.ALR_DOOR2_NOT_OPEN)>0) {
			alarms.add(new CNCMachineAlarm(CNCMachineAlarm.DOOR2_NOT_OPEN));
		}
		if ((alarmReg1 & CNCMachineConstants.ALR_DOOR1_NOT_CLOSE)>0) {
			alarms.add(new CNCMachineAlarm(CNCMachineAlarm.DOOR1_NOT_CLOSED));
		}
		if ((alarmReg1 & CNCMachineConstants.ALR_DOOR2_NOT_CLOSE)>0) {
			alarms.add(new CNCMachineAlarm(CNCMachineAlarm.DOOR2_NOT_CLOSED));
		}
		if ((alarmReg1 & CNCMachineConstants.ALR_CLAMP1_NOT_OPEN)>0) {
			alarms.add(new CNCMachineAlarm(CNCMachineAlarm.CLAMP1_NOT_OPEN));
		}
		if ((alarmReg1 & CNCMachineConstants.ALR_CLAMP2_NOT_OPEN)>0) {
			alarms.add(new CNCMachineAlarm(CNCMachineAlarm.CLAMP2_NOT_OPEN));
		}
		if ((alarmReg1 & CNCMachineConstants.ALR_CLAMP1_NOT_CLOSE)>0) {
			alarms.add(new CNCMachineAlarm(CNCMachineAlarm.CLAMP1_NOT_CLOSED));
		}
		if ((alarmReg1 & CNCMachineConstants.ALR_CLAMP2_NOT_CLOSE)>0) {
			alarms.add(new CNCMachineAlarm(CNCMachineAlarm.CLAMP2_NOT_CLOSED));
		}
		if ((alarmReg2 & CNCMachineConstants.ALR_WA1_PUT)>0) {
			alarms.add(new CNCMachineAlarm(CNCMachineAlarm.WA1_PUT));
		}
		if ((alarmReg2 & CNCMachineConstants.ALR_WA2_PUT)>0) {
			alarms.add(new CNCMachineAlarm(CNCMachineAlarm.WA2_PUT));
		}
		if ((alarmReg2 & CNCMachineConstants.ALR_WA1_PICK)>0) {
			alarms.add(new CNCMachineAlarm(CNCMachineAlarm.WA1_PICK));
		}
		if ((alarmReg2 & CNCMachineConstants.ALR_WA2_PICK)>0) {
			alarms.add(new CNCMachineAlarm(CNCMachineAlarm.WA2_PICK));
		}
		if ((alarmReg2 & CNCMachineConstants.ALR_WA1_CYST)>0) {
			alarms.add(new CNCMachineAlarm(CNCMachineAlarm.WA1_CYCLUS_START));
		}
		if ((alarmReg2 & CNCMachineConstants.ALR_WA2_CYST)>0) {
			alarms.add(new CNCMachineAlarm(CNCMachineAlarm.WA2_CYCLUS_START));
		}
		if ((alarmReg2 & CNCMachineConstants.ALR_WA1_CLAMP)>0) {
			alarms.add(new CNCMachineAlarm(CNCMachineAlarm.WA1_CLAMP));
		}
		if ((alarmReg2 & CNCMachineConstants.ALR_WA2_CLAMP)>0) {
			alarms.add(new CNCMachineAlarm(CNCMachineAlarm.WA2_CLAMP));
		}
		if ((alarmReg2 & CNCMachineConstants.ALR_WA1_UNCLAMP)>0) {
			alarms.add(new CNCMachineAlarm(CNCMachineAlarm.WA1_UNCLAMP));
		}
		if ((alarmReg2 & CNCMachineConstants.ALR_WA2_UNCLAMP)>0) {
			alarms.add(new CNCMachineAlarm(CNCMachineAlarm.WA2_UNCLAMP));
		}
		if ((alarmReg2 & CNCMachineConstants.ALR_MULTIPLE_IPC_RQST)>0) {
			alarms.add(new CNCMachineAlarm(CNCMachineAlarm.MULTIPLE_IPC_REQUESTS));
		}
		return alarms;
	}
}
