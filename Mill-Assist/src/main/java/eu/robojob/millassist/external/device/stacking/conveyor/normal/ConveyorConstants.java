package eu.robojob.millassist.external.device.stacking.conveyor.normal;

public final class ConveyorConstants {

	private ConveyorConstants() {
	}
	
	public static final int BIT0						=	0b0000000000000001;
	public static final int BIT1						=	0b0000000000000010;
	public static final int BIT2						=	0b0000000000000100;
	public static final int BIT3						=	0b0000000000001000;
	public static final int BIT4						=	0b0000000000010000;
	public static final int BIT5						=	0b0000000000100000;
	public static final int BIT6						=	0b0000000001000000;
	public static final int BIT7						=	0b0000000010000000;
	public static final int BIT8						=	0b0000000100000000;
	public static final int BIT9						=	0b0000001000000000;
	public static final int BIT10						=	0b0000010000000000;
	public static final int BIT11						=	0b0000100000000000;
	public static final int BIT12						=	0b0001000000000000;
	public static final int BIT13						=	0b0010000000000000;
	public static final int BIT14						=	0b0100000000000000;
	public static final int BIT15						=	0b1000000000000000;
	
	// Status register
	public static final int STATUS_REG = 1;
	public static final int MODE 						=	BIT0;
	public static final int CONV_RAW_MOV				=	BIT1;
	public static final int CONV_RAW_INTERLOCK			=	BIT2;
	public static final int CONV_FINISHED_MOV			= 	BIT3;
	public static final int CONV_FINISHED_INTERLOCK		=	BIT4;
	public static final int SHIFT_FINISHED_WP_OK		=	BIT5;
	public static final int RAW_WP_IN_POSITION			=	BIT6;
	// BIT 7 currently not used
	public static final int SUPPORT_1_STATUS			=	BIT8;
	public static final int SUPPORT_2_STATUS			=	BIT9;
	public static final int SUPPORT_3_STATUS			=	BIT10;
	// BITS 11-15 currently not used
	
	// Alarms register
	public static final int ALARMS_REG = 2;
	public static final int ALR_EMERGENCY_STOP			=	BIT0;
	public static final int ALR_NO_PRESSURE				=	BIT1;
	public static final int ALR_ENGINE_1				=	BIT2;
	public static final int ALR_ENGINE_2				=	BIT3;
	public static final int ALR_RAW_CONV_EMPTY			=	BIT4;
	public static final int ALR_FINISHED_CONV_FULL		=	BIT5;
	public static final int ALR_SUPPORT_1				=	BIT6;
	public static final int ALR_SUPPORT_2				=	BIT7;
	public static final int ALR_SUPPORT_3				=	BIT8;
	// BITS 9-10 currently not used
	public static final int ALR_SENSOR_1				=	BIT11;
	public static final int ALR_SENSOR_2				=	BIT12;
	public static final int ALR_SENSOR_3				=	BIT13;
	public static final int ALR_SENSOR_4				=	BIT14;
	
	// Sensor value registers
	public static final int SENSOR_1_REG = 3;
	public static final int SENSOR_2_REG = 4;
	public static final int SENSOR_3_REG = 5;
	public static final int SENSOR_4_REG = 6;
	
	// Command register
	public static final int COMMAND_REG = 10;
	public static final int RQST_INTERLOCK_RAW			=	BIT0;
	public static final int RELEASE_INTERLOCK_RAW		=	BIT1;
	public static final int RQST_INTERLOCK_FINISHED		=	BIT2;
	public static final int RELEASE_INTERLOCK_FINISHED	=	BIT3;
	public static final int RESET_ALARMS				=	BIT4;
	public static final int SHIFT_FINISHED_WP			=	BIT5;
	public static final int OPERATOR_REQUESTED			=	BIT6;
	public static final int ALL_WP_FINISHED				=	BIT7;
	public static final int UPDATE_SUPPORTS				=	BIT8;
	public static final int SUPPORT_1_REQ_VAL			=	BIT9;
	public static final int SUPPORT_2_REQ_VAL			=	BIT10;
	public static final int SUPPORT_3_REQ_VAL			=	BIT11;
	// BITS 12-15 currently not used
	
	public static final int SPEED_RAW_CONVEYOR = 11;
	public static final int SPEED_FINISHED_CONVEYOR = 12;
	public static final int ZERO_SENSOR_1 = 13;
	public static final int SPAN_SENSOR_1 = 14;
	public static final int THRESHOLD_SENSOR_1 = 15;
	public static final int ZERO_SENSOR_2 = 16;
	public static final int SPAN_SENSOR_2 = 17;
	public static final int THRESHOLD_SENSOR_2 = 18;
	public static final int ZERO_SENSOR_3 = 19;
	public static final int SPAN_SENSOR_3 = 20;
	public static final int THRESHOLD_SENSOR_3 = 21;
	public static final int ZERO_SENSOR_4 = 22;
	public static final int SPAN_SENSOR_4 = 23;
	public static final int THRESHOLD_SENSOR_4 = 24;
	public static final int LENGTH_CONV_RAW = 25;
	public static final int LENGTH_CONV_FINISHED = 26;
	public static final int LENGTH_WP_RAW = 27;
	public static final int LENGTH_WP_FINISHED_SHIFT = 28;
}
