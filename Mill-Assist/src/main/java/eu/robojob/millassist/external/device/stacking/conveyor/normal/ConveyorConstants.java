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
	public static final int RAW_CONV_MOV				=	BIT1;
	public static final int RAW_CONV_INTERLOCK			=	BIT2;
	public static final int FIN_CONV_MOV				= 	BIT3;
	public static final int FIN_CONV_INTERLOCK			=	BIT4;
	public static final int SHIFT_FINISHED_WP_OK		=	BIT5;
	public static final int RAW_WP_IN_POSITION			=	BIT6;
	public static final int SUPPORTS_UPDATED			=	BIT8;
	public static final int SUPPORTS_SELECTED			=	BIT9;
	// BIT 10 currently not used
	public static final int USE_SUPPORT_SENSORS			=	BIT11;
	public static final int SUPPORT_0_IS_DOWN			=	BIT12;
	public static final int SUPPORT_1_IS_DOWN			=	BIT13;
	public static final int SUPPORT_2_IS_DOWN			=	BIT14;
	public static final int SUPPORT_3_IS_DOWN			=	BIT15;
	
	// Alarms register
	public static final int ALARMS_REG = 2;
	public static final int ALR_EMERGENCY_STOP			=	BIT0;
	public static final int ALR_NO_PRESSURE				=	BIT1;
	public static final int ALR_ENGINE_1				=	BIT2;
	public static final int ALR_ENGINE_2				=	BIT3;
	public static final int ALR_RAW_CONV_EMPTY			=	BIT4;
	public static final int ALR_FINISHED_CONV_FULL		=	BIT5;
	public static final int ALR_SUPPORT_0				=	BIT6;
	public static final int ALR_SUPPORT_1				=	BIT7;
	public static final int ALR_SUPPORT_2				=	BIT8;
	public static final int ALR_SUPPORT_3				=	BIT9;
	// BITS 9-10 currently not used
	public static final int ALR_SENSOR_0				=	BIT11;
	public static final int ALR_SENSOR_1				=	BIT12;
	public static final int ALR_SENSOR_2				=	BIT13;
	public static final int ALR_SENSOR_3				=	BIT14;
	
	// Sensor value registers
	public static final int SENSOR_0_REG = 3;
	public static final int SENSOR_1_REG = 4;
	public static final int SENSOR_2_REG = 5;
	public static final int SENSOR_3_REG = 6;
	
	public static final int SUPPORT_SELECTION = 8;
	public static final int SUPPORT_0_SELECTED			=	BIT0;
	public static final int SUPPORT_1_SELECTED			=	BIT1;
	public static final int SUPPORT_2_SELECTED			=	BIT2;
	public static final int SUPPORT_3_SELECTED			=	BIT3;
	
	// Command register
	public static final int COMMAND_REG = 10;
	public static final int RQST_INTERLOCK_RAW			=	BIT0;
	public static final int RELEASE_INTERLOCK_RAW		=	BIT1;
	public static final int RQST_INTERLOCK_FINISHED		=	BIT2;
	public static final int RELEASE_INTERLOCK_FINISHED	=	BIT3;
	public static final int RESET_ALARMS				=	BIT4;
	public static final int SHIFT_FINISHED_WP			=	BIT5;
	public static final int SUPPORTS_SELECT				=	BIT6;
	public static final int SUPPORTS_UPDATE				=	BIT7;
	public static final int SUPPORT_0					=	BIT8;
	public static final int SUPPORT_1					=	BIT9;
	public static final int SUPPORT_2					=	BIT10;
	public static final int SUPPORT_3					=	BIT11;
	public static final int PREPARE_FOR_CMD				=	BIT12;
	// BITS 12-15 currently not used
	
	public static final int SPEED_RAW_CONVEYOR = 11;
	public static final int SPEED_FINISHED_CONVEYOR = 12;
	public static final int OFFSET_SENSOR_0 = 13;
	public static final int SPAN_SENSOR_0 = 14;
	public static final int THRESHOLD_SENSOR_0 = 15;
	public static final int OFFSET_SENSOR_1 = 16;
	public static final int SPAN_SENSOR_1 = 17;
	public static final int THRESHOLD_SENSOR_1 = 18;
	public static final int OFFSET_SENSOR_2 = 19;
	public static final int SPAN_SENSOR_2 = 20;
	public static final int THRESHOLD_SENSOR_2 = 21;
	public static final int OFFSET_SENSOR_3 = 22;
	public static final int SPAN_SENSOR_3 = 23;
	public static final int THRESHOLD_SENSOR_3 = 24;
	public static final int LENGTH_CONV_RAW = 25;
	public static final int LENGTH_CONV_FINISHED = 26;
	public static final int LENGTH_WP_RAW_SHIFT = 27;
	public static final int LENGTH_WP_FINISHED_SHIFT = 28;
	public static final int BLUE_LAMP = 29;
}
