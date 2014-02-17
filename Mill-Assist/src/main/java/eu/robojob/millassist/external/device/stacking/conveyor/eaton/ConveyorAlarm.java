package eu.robojob.millassist.external.device.stacking.conveyor.eaton;

import java.util.HashSet;
import java.util.Set;

public class ConveyorAlarm extends eu.robojob.millassist.external.device.stacking.conveyor.ConveyorAlarm {

	public static final int ALR_EMERGENCY_STOP			=	1000;
	public static final int ALR_NO_PRESSURE				=	1001;
	public static final int ALR_ENGINE_A				= 	1002;
	public static final int ALR_ENGINE_B				= 	1003;
	public static final int ALR_CONV_A_EMPTY			= 	1004;
	public static final int ALR_SENSOR_A_1				= 	1006;
	public static final int ALR_SENSOR_A_2				= 	1007;
	public static final int ALR_CONV_B_EMPTY			= 	1008;
	public static final int ALR_CONV_B_FULL				= 	1009;
	public static final int ALR_SENSOR_B_1				= 	1010;
	public static final int ALR_SENSOR_B_2				= 	1011;
	public static final int ALR_MODE_MANUAL				= 	1100;
	
	public static final int WAIT_FOR_RAW_WORKPIECE_TIMEOUT = 1200;
		
	public ConveyorAlarm(final int id) {
		super(id);
	}
	
	public static Set<eu.robojob.millassist.external.device.stacking.conveyor.ConveyorAlarm> parseConveyorAlarms(final int alarmReg1, final int statusInt, final eu.robojob.millassist.external.device.stacking.conveyor.ConveyorAlarm timeout) {
		Set<eu.robojob.millassist.external.device.stacking.conveyor.ConveyorAlarm> alarms = new HashSet<eu.robojob.millassist.external.device.stacking.conveyor.ConveyorAlarm>();
		if ((alarmReg1 & ConveyorConstants.ALR_EMERGENCY_STOP) > 0) {
			alarms.add(new ConveyorAlarm(ConveyorAlarm.ALR_EMERGENCY_STOP));
		}
		if ((alarmReg1 & ConveyorConstants.ALR_NO_PRESSURE) > 0) {
			alarms.add(new ConveyorAlarm(ConveyorAlarm.ALR_NO_PRESSURE));
		}
		if ((alarmReg1 & ConveyorConstants.ALR_ENGINE_A) > 0) {
			alarms.add(new ConveyorAlarm(ConveyorAlarm.ALR_ENGINE_A));
		}
		if ((alarmReg1 & ConveyorConstants.ALR_ENGINE_B) > 0) {
			alarms.add(new ConveyorAlarm(ConveyorAlarm.ALR_ENGINE_B));
		}
		if ((alarmReg1 & ConveyorConstants.ALR_CONV_A_EMPTY) > 0) {
			alarms.add(new ConveyorAlarm(ConveyorAlarm.ALR_CONV_A_EMPTY));
		}
		if ((alarmReg1 & ConveyorConstants.ALR_SENSOR_A_1) > 0) {
			alarms.add(new ConveyorAlarm(ConveyorAlarm.ALR_SENSOR_A_1));
		}
		if ((alarmReg1 & ConveyorConstants.ALR_SENSOR_A_2) > 0) {
			alarms.add(new ConveyorAlarm(ConveyorAlarm.ALR_SENSOR_A_2));
		}
		if ((alarmReg1 & ConveyorConstants.ALR_CONV_B_EMPTY) > 0) {
			alarms.add(new ConveyorAlarm(ConveyorAlarm.ALR_CONV_B_EMPTY));
		}
		if ((alarmReg1 & ConveyorConstants.ALR_CONV_B_FULL) > 0) {
			alarms.add(new ConveyorAlarm(ConveyorAlarm.ALR_CONV_B_FULL));
		}
		if ((alarmReg1 & ConveyorConstants.ALR_SENSOR_B_1) > 0) {
			alarms.add(new ConveyorAlarm(ConveyorAlarm.ALR_SENSOR_B_1));
		}
		if ((alarmReg1 & ConveyorConstants.ALR_SENSOR_B_2) > 0) {
			alarms.add(new ConveyorAlarm(ConveyorAlarm.ALR_SENSOR_B_2));
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
