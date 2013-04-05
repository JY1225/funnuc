package eu.robojob.irscw.external.device.processing.cnc;

import java.util.HashSet;
import java.util.Set;

import eu.robojob.irscw.util.Translator;

public class CNCMachineAlarm {

	public static final int MACHINE = 0;
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
	
	public static final int CYCLE_NOT_STARTED_TIMEOUT = 101;
	public static final int CYCLE_END_TIMEOUT = 102;
	public static final int PREPARE_PICK_TIMEOUT = 103;
	public static final int PREPARE_PUT_TIMEOUT = 104;
	public static final int UNCLAMP_TIMEOUT = 105;
	public static final int CLAMP_TIMEOUT = 106;
			
	
	private static final int DEFAULT_PRIORITY = 5;
		
	private int id;
	
	public CNCMachineAlarm(final int id) {
		this.id = id;
	}
	
	public String getLocalizedMessage() {
		return Translator.getTranslation("CNCMachineAlarm." + id);
	}
	
	public String getMessage() {
		return "CNC Machine alarm: id = " + id;
	}
	
	//TODO implement priorities, for now: all the same
	public int getPriority() {
		return DEFAULT_PRIORITY;
	}
	
	public int getId() {
		return id;
	}
	
	@Override
	public int hashCode() {
		return id;
	}
	
	@Override
	public boolean equals(final Object o) {
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
	
	//TODO perhaps this methods should be placed in the CNCMillingMachine-class or an helper class
	public static Set<CNCMachineAlarm> parseCNCAlarms(final int alarmReg1, final int alarmReg2, 
			final CNCMachineAlarm timeout) {
		Set<CNCMachineAlarm> alarms = new HashSet<CNCMachineAlarm>();
		if ((alarmReg1 & CNCMachineConstants.ALR_MACHINE) > 0) {
			alarms.add(new CNCMachineAlarm(CNCMachineAlarm.MACHINE));
		}
		if ((alarmReg1 & CNCMachineConstants.ALR_DOOR1_NOT_OPEN) > 0) {
			alarms.add(new CNCMachineAlarm(CNCMachineAlarm.DOOR1_NOT_OPEN));
		}
		if ((alarmReg1 & CNCMachineConstants.ALR_DOOR2_NOT_OPEN) > 0) {
			alarms.add(new CNCMachineAlarm(CNCMachineAlarm.DOOR2_NOT_OPEN));
		}
		if ((alarmReg1 & CNCMachineConstants.ALR_DOOR1_NOT_CLOSE) > 0) {
			alarms.add(new CNCMachineAlarm(CNCMachineAlarm.DOOR1_NOT_CLOSED));
		}
		if ((alarmReg1 & CNCMachineConstants.ALR_DOOR2_NOT_CLOSE) > 0) {
			alarms.add(new CNCMachineAlarm(CNCMachineAlarm.DOOR2_NOT_CLOSED));
		}
		if ((alarmReg1 & CNCMachineConstants.ALR_CLAMP1_NOT_OPEN) > 0) {
			alarms.add(new CNCMachineAlarm(CNCMachineAlarm.CLAMP1_NOT_OPEN));
		}
		if ((alarmReg1 & CNCMachineConstants.ALR_CLAMP2_NOT_OPEN) > 0) {
			alarms.add(new CNCMachineAlarm(CNCMachineAlarm.CLAMP2_NOT_OPEN));
		}
		if ((alarmReg1 & CNCMachineConstants.ALR_CLAMP1_NOT_CLOSE) > 0) {
			alarms.add(new CNCMachineAlarm(CNCMachineAlarm.CLAMP1_NOT_CLOSED));
		}
		if ((alarmReg1 & CNCMachineConstants.ALR_CLAMP2_NOT_CLOSE) > 0) {
			alarms.add(new CNCMachineAlarm(CNCMachineAlarm.CLAMP2_NOT_CLOSED));
		}
		if ((alarmReg2 & CNCMachineConstants.ALR_WA1_PUT) > 0) {
			alarms.add(new CNCMachineAlarm(CNCMachineAlarm.WA1_PUT));
		}
		if ((alarmReg2 & CNCMachineConstants.ALR_WA2_PUT) > 0) {
			alarms.add(new CNCMachineAlarm(CNCMachineAlarm.WA2_PUT));
		}
		if ((alarmReg2 & CNCMachineConstants.ALR_WA1_PICK) > 0) {
			alarms.add(new CNCMachineAlarm(CNCMachineAlarm.WA1_PICK));
		}
		if ((alarmReg2 & CNCMachineConstants.ALR_WA2_PICK) > 0) {
			alarms.add(new CNCMachineAlarm(CNCMachineAlarm.WA2_PICK));
		}
		if ((alarmReg2 & CNCMachineConstants.ALR_WA1_CYST) > 0) {
			alarms.add(new CNCMachineAlarm(CNCMachineAlarm.WA1_CYCLUS_START));
		}
		if ((alarmReg2 & CNCMachineConstants.ALR_WA2_CYST) > 0) {
			alarms.add(new CNCMachineAlarm(CNCMachineAlarm.WA2_CYCLUS_START));
		}
		if ((alarmReg2 & CNCMachineConstants.ALR_WA1_CLAMP) > 0) {
			alarms.add(new CNCMachineAlarm(CNCMachineAlarm.WA1_CLAMP));
		}
		if ((alarmReg2 & CNCMachineConstants.ALR_WA2_CLAMP) > 0) {
			alarms.add(new CNCMachineAlarm(CNCMachineAlarm.WA2_CLAMP));
		}
		if ((alarmReg2 & CNCMachineConstants.ALR_WA1_UNCLAMP) > 0) {
			alarms.add(new CNCMachineAlarm(CNCMachineAlarm.WA1_UNCLAMP));
		}
		if ((alarmReg2 & CNCMachineConstants.ALR_WA2_UNCLAMP) > 0) {
			alarms.add(new CNCMachineAlarm(CNCMachineAlarm.WA2_UNCLAMP));
		}
		if ((alarmReg2 & CNCMachineConstants.ALR_MULTIPLE_IPC_RQST) > 0) {
			alarms.add(new CNCMachineAlarm(CNCMachineAlarm.MULTIPLE_IPC_REQUESTS));
		}
		if (timeout != null) {
			alarms.add(timeout);
		}
		return alarms;
	}
}
