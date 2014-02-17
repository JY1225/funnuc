package eu.robojob.millassist.external.device.stacking.conveyor.eaton;


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
	// BIT 1 currently not used
	public static final int CONV_A_MOV 					=	BIT2;
	public static final int CONV_A_SLOW					=	BIT3;
	public static final int CONV_A_INTERLOCK			=	BIT4;
	public static final int CONV_A_WP_DETECTED			=	BIT5;
	public static final int CONV_A_WP_IN_POSITION		=	BIT6;
	public static final int CONV_B_MODE					=	BIT7;
	public static final int CONV_B_MOV					=	BIT8;
	public static final int CONV_B_SLOW					= 	BIT9;
	public static final int CONV_B_INTERLOCK			=	BIT10;
	public static final int CONV_B_WP_DETECTED			=	BIT11;
	public static final int CONV_B_WP_IN_POSITION		=	BIT12;
	public static final int SHIFT_FINISHED_WP_OK		=	BIT13;
	// BIT 12 - 15 not used
	
	// Alarms register
	public static final int ALARMS_REG = 2;
	public static final int ALR_EMERGENCY_STOP			=	BIT0;
	public static final int ALR_NO_PRESSURE				=	BIT1;
	public static final int ALR_ENGINE_A				=	BIT2;
	public static final int ALR_ENGINE_B				=	BIT3;
	public static final int ALR_CONV_A_EMPTY			=	BIT4;
	// BIT 5 currently not used
	public static final int ALR_SENSOR_A_1				=	BIT6;
	public static final int ALR_SENSOR_A_2				=	BIT7;
	public static final int ALR_CONV_B_EMPTY			=	BIT8;
	public static final int ALR_CONV_B_FULL				=	BIT9;
	public static final int ALR_SENSOR_B_1				=	BIT10;
	public static final int ALR_SENSOR_B_2				=	BIT11;
	// BITS 12 - 15 not used
	
	// Command register
	public static final int COMMAND_REG = 10;
	public static final int RQST_INTERLOCK_A			=	BIT0;
	public static final int RELEASE_INTERLOCK_A			=	BIT1;
	public static final int RQST_INTERLOCK_B			=	BIT2;
	public static final int RELEASE_INTERLOCK_B			=	BIT3;
	public static final int RESET_ALARMS				=	BIT4;
	public static final int UPDATE_CONV_B_MODE			=	BIT5;
	public static final int NEW_CONV_B_MODE				=	BIT6;
	public static final int SHIFT_FINISHED_WP			=	BIT7;
	public static final int OPERATOR_REQUESTED			=	BIT8;
	public static final int ALL_WP_FINISHED				=	BIT9;
	// BITS 10-15 currently not used
	
	public static final int SPEED_CONV_A = 11;
	public static final int SPEED_CONV_A_SLOW = 12;
	public static final int LENGTH_CONV_A = 13;
	public static final int SENSOR_DIST_A = 14;
	public static final int SPEED_CONV_B = 15;
	public static final int SPEED_CONV_B_SLOW = 16;
	public static final int LENGTH_CONV_B = 17;
	public static final int SENSOR_DIST_B = 18;
	public static final int LENGTH_WP_FIN_SHIFT = 19;
	
}
