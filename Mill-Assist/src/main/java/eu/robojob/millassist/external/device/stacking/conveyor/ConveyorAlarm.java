package eu.robojob.millassist.external.device.stacking.conveyor;

import java.util.HashSet;
import java.util.Set;

import eu.robojob.millassist.util.Translator;

public class ConveyorAlarm {

	public static final int ALR_EMERGENCY_STOP			=	0;
	public static final int ALR_NO_POWER				=	1;
	public static final int ALR_ENGINE_1				=	2;
	public static final int ALR_ENGINE_2				=	3;
	public static final int ALR_RAW_CONV_EMPTY			=	4;
	public static final int ALR_FINISHED_CONV_FULL		=	5;
	public static final int ALR_SUPPORT_1				=	6;
	public static final int ALR_SUPPORT_2				=	7;
	public static final int ALR_SUPPORT_3				=	8;
	public static final int ALR_SENSOR_1				=	11;
	public static final int ALR_SENSOR_2				=	12;
	public static final int ALR_SENSOR_3				=	13;
	public static final int ALR_SENSOR_4				=	14;
	public static final int ALR_MODE_MANUAL				= 	100;
	
	public static final int WAIT_FOR_RAW_WORKPIECE_TIMEOUT = 200;
	
	private static final int DEFAULT_PRIORITY = 5;

	private int id;
	
	public ConveyorAlarm(final int id) {
		this.id = id;
	}
	
	public String getLocalizedMessage() {
		return Translator.getTranslation("ConveyorAlarm." + id);
	}
	
	public String getMessage() {
		return "Conveyor alarm: id = " + id;
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
		if (o instanceof ConveyorAlarm) {
			if (((ConveyorAlarm) o).getId() == id) {
				return true;
			}
		}
		return false;
	}
	
	public String toString() {
		return "CNCMachineAlarm: " + id;
	}
	
	public static Set<ConveyorAlarm> parseConveyorAlarms(final int alarmReg1, final int statusInt, final ConveyorAlarm timeout) {
		Set<ConveyorAlarm> alarms = new HashSet<ConveyorAlarm>();
		if ((alarmReg1 & ConveyorConstants.ALR_EMERGENCY_STOP) > 0) {
			alarms.add(new ConveyorAlarm(ConveyorAlarm.ALR_EMERGENCY_STOP));
		}
		if ((alarmReg1 & ConveyorConstants.ALR_NO_PRESSURE) > 0) {
			alarms.add(new ConveyorAlarm(ConveyorAlarm.ALR_NO_POWER));
		}
		if ((alarmReg1 & ConveyorConstants.ALR_ENGINE_1) > 0) {
			alarms.add(new ConveyorAlarm(ConveyorAlarm.ALR_ENGINE_1));
		}
		if ((alarmReg1 & ConveyorConstants.ALR_ENGINE_2) > 0) {
			alarms.add(new ConveyorAlarm(ConveyorAlarm.ALR_ENGINE_2));
		}
		if ((alarmReg1 & ConveyorConstants.ALR_RAW_CONV_EMPTY) > 0) {
			alarms.add(new ConveyorAlarm(ConveyorAlarm.ALR_RAW_CONV_EMPTY));
		}
		if ((alarmReg1 & ConveyorConstants.ALR_FINISHED_CONV_FULL) > 0) {
			alarms.add(new ConveyorAlarm(ConveyorAlarm.ALR_FINISHED_CONV_FULL));
		}
		if ((alarmReg1 & ConveyorConstants.ALR_SUPPORT_1) > 0) {
			alarms.add(new ConveyorAlarm(ConveyorAlarm.ALR_SUPPORT_1));
		}
		if ((alarmReg1 & ConveyorConstants.ALR_SUPPORT_2) > 0) {
			alarms.add(new ConveyorAlarm(ConveyorAlarm.ALR_SUPPORT_2));
		}
		if ((alarmReg1 & ConveyorConstants.ALR_SUPPORT_3) > 0) {
			alarms.add(new ConveyorAlarm(ConveyorAlarm.ALR_SUPPORT_3));
		}
		if ((alarmReg1 & ConveyorConstants.ALR_SENSOR_1) > 0) {
			alarms.add(new ConveyorAlarm(ConveyorAlarm.ALR_SENSOR_1));
		}
		if ((alarmReg1 & ConveyorConstants.ALR_SENSOR_2) > 0) {
			alarms.add(new ConveyorAlarm(ConveyorAlarm.ALR_SENSOR_2));
		}
		if ((alarmReg1 & ConveyorConstants.ALR_SENSOR_3) > 0) {
			alarms.add(new ConveyorAlarm(ConveyorAlarm.ALR_SENSOR_3));
		}
		if ((alarmReg1 & ConveyorConstants.ALR_SENSOR_4) > 0) {
			alarms.add(new ConveyorAlarm(ConveyorAlarm.ALR_SENSOR_4));
		}
		if ((statusInt & ConveyorConstants.MODE) == 0) {
			alarms.add(new ConveyorAlarm(ConveyorAlarm.ALR_MODE_MANUAL));
		}
		if (timeout != null) {
			alarms.add(timeout);
		}
		return alarms;
	}
	
}
