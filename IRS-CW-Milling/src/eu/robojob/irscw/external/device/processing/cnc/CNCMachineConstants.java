package eu.robojob.irscw.external.device.processing.cnc;

public final class CNCMachineConstants {

	private CNCMachineConstants() {
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
	
	
	// X: INPUTS
	
	public static final int INPUT_SLOT_1 = 1;
	public static final int X_MACHINE_ALARM 			= 	BIT0;
	public static final int X_MACHINE_IN_CYCLE			=	BIT1;
	public static final int X_MACHINE_CYCLE_STOP		=	BIT2;
	public static final int X_MACHINE_CYCLE_FINISHED	=	BIT3;
	public static final int X_CNC_CONDITION1			=	BIT4;
	public static final int X_CNC_CONDITION2			=	BIT5;
	public static final int X_CNC_CONDITION3			=	BIT6;
	public static final int X_ROBOT_SERVICE4			=	BIT7;
	public static final int X_ROBOT_SERVICE5			=	BIT8;
	// BIT 9 UNUSED
	public static final int X_Z1_FREE					=	BIT10;
	// BIT 11-15 UNUSED
	
	public static final int INPUT_SLOT_2 = 2;
	public static final int X1_CLAMP1_LMT_OPEN			= 	BIT0;
	public static final int X2_MAIN_SPINDLE_SELECTED 	=	BIT0;
	public static final int X1_CLAMP1_LMT_CLOSE			= 	BIT1;
	public static final int X2_SUB_SPINDLE_SELECTED		=	BIT1;
	public static final int X_CLAMP1_PRESSURE_OPEN		= 	BIT2;
	public static final int X_CLAMP1_PRESSURE_CLOSE		= 	BIT3;
	public static final int X1_CLAMP1_WPP				= 	BIT4;
	public static final int X2_CLAMP1_CHUCK_CLAMP_END	= 	BIT4;
	public static final int X1_CLAMP2_LMT_OPEN			= 	BIT5;
	public static final int X2_DOOR_LEFT_CLOSE_SWITCH	= 	BIT5;
	public static final int X1_CLAMP2_LMT_CLOSE			= 	BIT6;
	public static final int X2_DOOR_LEFT_OPEN_SWITCH	= 	BIT6;
	public static final int X1_CLAMP2_PRESSURE_OPEN		= 	BIT7;
	public static final int X2_DOOR_RIGHT_CLOSE_SWITCH	= 	BIT7;
	public static final int X1_CLAMP2_PRESSURE_CLOSE	= 	BIT8;
	public static final int X2_DOOR_RIGHT_OPEN_SWITCH	= 	BIT8;
	public static final int X1_CLAMP2_WPP				= 	BIT9;
	public static final int X2_CHUCK_UNCLAMP_END		= 	BIT9;
	public static final int X1_CLAMP1_ORIENT			= 	BIT10;
	public static final int X2_SPINDLE_ORIENTED_END		= 	BIT10;
	public static final int X_CLAMP2_ORIENT				= 	BIT11;
	// BIT 12-15 UNUSED
	
	public static final int INPUT_SLOT_3 = 3;
	public static final int X_DOOR2_OPEN_PERMISSION		= 	BIT0;
	public static final int X_DOOR2_CLOSE				= 	BIT1;
	public static final int X_DOOR2_OPEN				= 	BIT2;
	public static final int X_DOOR1_OPEN_PERMISSION		= 	BIT3;
	public static final int X_DOOR1_CLOSE				= 	BIT4;
	public static final int X_DOOR1_OPEN				= 	BIT5;
	public static final int X_SAFETY_RELAY_STATUS		= 	BIT6;
	public static final int X_MAIN_PRESSURE				= 	BIT7;
	public static final int X_ROBOT_SERVICE3			= 	BIT8;
	public static final int X_ROBOT_SERVICE2			= 	BIT9;
	public static final int X_ROBOT_SERVICE1			= 	BIT10;
	public static final int X_MACHINE_AUTO_MODE			= 	BIT11;
	// BITS 12-15 UNUSED
	
	// Y: OUTPUTS
	
	public static final int OUTPUT_SLOT_4 = 4;
	public static final int Y_DOOR_OPEN_REQUEST			= 	BIT0;
	public static final int Y_DOOR2_CLOSE				= 	BIT1;
	public static final int Y_DOOR2_OPEN				= 	BIT2;
	public static final int Y_DOOR1_CLOSE				= 	BIT3;
	public static final int Y_DOOR1_OPEN				= 	BIT4;
	public static final int Y1_CLAMP1_UNCLAMP			= 	BIT5;
	public static final int Y2_CHUCK_UNCLAMP			= 	BIT5;
	public static final int Y1_CLAMP1_CLAMP				= 	BIT6;
	public static final int Y2_CHUCK_CLAMP				= 	BIT6;
	public static final int Y1_CLAMP2_UNCLAMP			= 	BIT7;
	public static final int Y2_MAIN_SPINDLE_SELECT		= 	BIT7;
	public static final int Y1_CLAMP2_CLAMP				= 	BIT8;
	public static final int Y2_SUB_SPINDLE_SELECT		= 	BIT8;
	// BITS 9-15 UNUSED
	
	public static final int OUTPUT_SLOT_5 = 5;
	public static final int Y_NC_RESET					= 	BIT0;
	public static final int Y_ROBOT_IN_SERVICE			= 	BIT1;
	public static final int Y_DOOR1_LED					= 	BIT2;
	//BIT 3 UNUSED
	public static final int Y_CYCLUS_START				= 	BIT4;
	public static final int Y1_CLAMP1_ORIENT			= 	BIT5;
	public static final int Y2_SPINDLE_ORIENT			= 	BIT5;
	public static final int Y1_CLAMP2_ORIENT			= 	BIT6;
	public static final int Y2_DOOR1_OPEN_LED_ORIENT	= 	BIT6;
	public static final int Y1_NC_POWEROFF				=	BIT7;
	public static final int Y2_DOOR1_CLOSE_LED			=	BIT7;
	public static final int Y1_CNC_BREAKERTRIP			=	BIT8;
	public static final int Y2_DOOR2_CLOSE_LED			=	BIT8;
	public static final int Y1_CLAMP1_AIRBLOW			=	BIT9;
	public static final int Y2_SPINDLE_AIR_BLOW			=	BIT9;
	public static final int Y1_CLAMP2_AIRBLOW			=	BIT10;
	public static final int Y2_DOOR2_OPEN_LED			=	BIT10;
	public static final int Y_M_CODE_FINISH				=	BIT11;
	// BITS 12-15 UNUSED
	
	
	// R: RESULTS
	
	public static final int STATUS = 6;
	public static final int R_PUT_WA1_ALLOWED			= 	BIT0;
	public static final int R_PUT_WA2_ALLOWED			= 	BIT1;
	public static final int R_PUT_WA1_READY				= 	BIT2;
	public static final int R_PUT_WA2_READY				= 	BIT3;
	public static final int R_CLAMP_WA1_READY			= 	BIT4;
	public static final int R_CLAMP_WA2_READY			= 	BIT5;
	public static final int R_CYCLE_STARTED_WA1			= 	BIT6;
	public static final int R_CYCLE_STARTED_WA2			= 	BIT7;
	public static final int R_PICK_WA1_REQUESTED		= 	BIT8;
	public static final int R_PICK_WA2_REQUESTED		= 	BIT9;
	public static final int R_PICK_WA1_READY			= 	BIT10;
	public static final int R_PICK_WA2_READY			= 	BIT11;
	public static final int R_UNCLAMP_WA1_READY			= 	BIT12;
	public static final int R_UNCLAMP_WA2_READY			= 	BIT13;
	// BITS 14_15 UNUSED
	
	
	public static final int CONFIGURATION = 7;
	public static final int DOOR1_PRESENT				= 	BIT0;
	public static final int DOOR2_PRESENT 				= 	BIT1;
	// BITS 2-6 UNUSED	
	public static final int WA_HAS_CLAMP				= 	BIT8;
	public static final int CLAMP_GROUP_PRESENT			= 	BIT9;
	public static final int ZONE2_PRESENT				= 	BIT10;
	public static final int WORK_AREA2_PRESENT			= 	BIT11;
	
	
	// ALR: ALARMS
	
	public static final int ALARMS_REG1 = 8;
	public static final int ALR_MACHINE					= 	BIT0;
	// BITS 1-4 UNUSED
	public static final int ALR_DOOR1_NOT_OPEN			= 	BIT5;
	public static final int ALR_DOOR2_NOT_OPEN			= 	BIT6;
	public static final int ALR_DOOR1_NOT_CLOSE			= 	BIT7;
	public static final int ALR_DOOR2_NOT_CLOSE			= 	BIT8;
	public static final int ALR_CLAMP1_NOT_OPEN			= 	BIT9;
	public static final int ALR_CLAMP2_NOT_OPEN			= 	BIT10;
	public static final int ALR_CLAMP1_NOT_CLOSE		= 	BIT11;
	public static final int ALR_CLAMP2_NOT_CLOSE		= 	BIT12;
	// BITS 13-15 UNUSED
	
	public static final int ALARMS_REG2 = 9;
	public static final int ALR_WA1_PUT					= 	BIT0;
	public static final int ALR_WA2_PUT 				= 	BIT1;
	public static final int ALR_WA1_PICK				= 	BIT2;
	public static final int ALR_WA2_PICK				= 	BIT3;
	public static final int ALR_WA1_CYST				= 	BIT4;
	public static final int ALR_WA2_CYST				= 	BIT5;
	public static final int ALR_WA1_CLAMP				= 	BIT6;
	public static final int ALR_WA2_CLAMP				= 	BIT7;
	public static final int ALR_WA1_UNCLAMP				= 	BIT8;
	public static final int ALR_WA2_UNCLAMP				= 	BIT9;
	// BITS 10-14 UNUSED
	public static final int ALR_MULTIPLE_IPC_RQST		= 	BIT15;
	
	
	// COMMAND REGISTERS
	
	public static final int IPC_REQUEST = 12;
	public static final int IPC_PUT_WA1_REQUEST			= 	BIT0;
	public static final int IPC_PUT_WA2_REQUEST			= 	BIT1;
	public static final int IPC_CLAMP_WA1_REQUEST		= 	BIT2;
	public static final int IPC_CLAMP_WA2_REQUEST		= 	BIT3;
	public static final int IPC_CYCLESTART_WA1_REQUEST	= 	BIT4;
	public static final int IPC_CYCLESTART_WA2_REQUEST	= 	BIT5;
	public static final int IPC_PICK_WA1_RQST			= 	BIT8;	
	public static final int IPC_PICK_WA2_RQST			= 	BIT9;
	public static final int IPC_UNCLAMP_WA1_RQST		= 	BIT10;	
	public static final int IPC_UNCLAMP_WA2_RQST		= 	BIT11;	
	public static final int IPC_CYCLEEND_WA1_RQST		= 	BIT12;	
	public static final int IPC_CYCLEEND_WA2_RQST		= 	BIT13;	
	
	public static final int IPC_READ_REQUEST_2 = 14;
	public static final int IPC_RESET_REQUEST			=	BIT0;
	public static final int IPC_OPERATOR_REQUESTED		=	BIT1;
	public static final int IPC_ALL_WP_PROCESSED		=	BIT2;
	public static final int IPC_NC_RESET				=	BIT3;
	public static final int IPC_POWER_OFF				=	BIT4;
	public static final int IPC_SERV_REQ_FINISH			=	BIT5;
	public static final int IPC_DOORS_SERV_REQ_FINISH	=	BIT6;
	// BIT7 UNUSED
	public static final int IPC_TAILSLEEVE_IN_RQST		=	BIT8;
	public static final int IPC_TAILSLEEVE_OUT_RQST		=	BIT9;
	public static final int IPC_STEADY_REST_OPEN_RQST	=	BIT10;
	public static final int IPC_STEADY_REST_CLOSE_RQST	=	BIT11;
	public static final int IPC_DOOR1_OPEN_RQST			=	BIT12;
	public static final int IPC_DOOR1_CLOSE_RQST		=	BIT13;
	public static final int IPC_DOOR2_OPEN_RQST			=	BIT14;
	public static final int IPC_DOOR2_CLOSE_RQST		=	BIT15;
	
	public static final int IPC_READ_REQUEST_3 = 15;
	public static final int IPC_X_ROBOT_SERVICE1		=	BIT0;
	public static final int IPC_X_ROBOT_SERVICE2		=	BIT1;
	public static final int IPC_X_ROBOT_SERVICE3		=	BIT2;
	public static final int IPC_X_ROBOT_SERVICE4		=	BIT3;
	public static final int IPC_X_ROBOT_SERVICE5		=	BIT4;
	
	
	// CONFIGURATION
	
	public static final int CONFIG_1 = 20;
	public static final int CRT_WA1_PICK_DOOR1_OPEN		= 	BIT0;
	public static final int CRT_WA1_PICK_DOOR1_CLOSE	= 	BIT1;
	public static final int CRT_WA1_PICK_DOOR2_OPEN		= 	BIT2;
	public static final int CRT_WA1_PICK_DOOR2_CLOSE	= 	BIT3;
	public static final int CRT_WA1_PICK_CLAMP_UNCLAMP 	= 	BIT4;
	public static final int CRT_WA1_PICK_CLAMP_CLAMPING	= 	BIT5;
	public static final int CRT_WA1_PICK_CONDITION1		= 	BIT6;	
	public static final int CRT_WA1_PICK_CONDITION2		= 	BIT7;
	public static final int CRT_WA1_PICK_CONDITION3		= 	BIT8;	
	// BITS 9-11 UNUSED
	public static final int CRT_WA1_PICK_AIRBLOW		= 	BIT12;	
	public static final int CRT_WA1_PICK_ORIENT1		= 	BIT13;	
	public static final int CRT_WA1_PICK_ORIENT2		= 	BIT14;	
	
	public static final int CONFIG_2 = 21;
	public static final int CRT_WA2_PICK_DOOR1_OPEN		= 	BIT0;
	public static final int CRT_WA2_PICK_DOOR1_CLOSE	= 	BIT1;
	public static final int CRT_WA2_PICK_DOOR2_OPEN		= 	BIT2;
	public static final int CRT_WA2_PICK_DOOR2_CLOSE	= 	BIT3;
	public static final int CRT_WA2_PICK_CLAMP_UNCLAMP 	= 	BIT4;
	public static final int CRT_WA2_PICK_CLAMP_CLAMPING	= 	BIT5;
	public static final int CRT_WA2_PICK_CONDITION1		= 	BIT6;	
	public static final int CRT_WA2_PICK_CONDITION2		= 	BIT7;
	public static final int CRT_WA2_PICK_CONDITION3		= 	BIT8;	
	// BITS 9-11 UNUSED
	public static final int CRT_WA2_PICK_AIRBLOW		= 	BIT12;	
	public static final int CRT_WA2_PICK_ORIENT1		= 	BIT13;	
	public static final int CRT_WA2_PICK_ORIENT2		= 	BIT14;	
	
	public static final int CONFIG_3 = 22;
	public static final int CRT_WA1_PUT_DOOR1_OPEN		= 	BIT0;
	public static final int CRT_WA1_PUT_DOOR1_CLOSE		= 	BIT1;
	public static final int CRT_WA1_PUT_DOOR2_OPEN		= 	BIT2;
	public static final int CRT_WA1_PUT_DOOR2_CLOSE		= 	BIT3;
	public static final int CRT_WA1_PUT_CLAMP_UNCLAMP 	= 	BIT4;
	public static final int CRT_WA1_PUT_CLAMP_CLAMPING	= 	BIT5;
	public static final int CRT_WA1_PUT_CONDITION1		= 	BIT6;	
	public static final int CRT_WA1_PUT_CONDITION2		= 	BIT7;
	public static final int CRT_WA1_PUT_CONDITION3		= 	BIT8;	
	// BITS 9-11 UNUSED
	public static final int CRT_WA1_PUT_AIRBLOW			= 	BIT12;	
	public static final int CRT_WA1_PUT_ORIENT1			= 	BIT13;	
	public static final int CRT_WA1_PUT_ORIENT2			= 	BIT14;	
	
	public static final int CONFIG_4 = 23;
	public static final int CRT_WA2_PUT_DOOR1_OPEN		= 	BIT0;
	public static final int CRT_WA2_PUT_DOOR1_CLOSE		= 	BIT1;
	public static final int CRT_WA2_PUT_DOOR2_OPEN		= 	BIT2;
	public static final int CRT_WA2_PUT_DOOR2_CLOSE		= 	BIT3;
	public static final int CRT_WA2_PUT_CLAMP_UNCLAMP 	= 	BIT4;
	public static final int CRT_WA2_PUT_CLAMP_CLAMPING	= 	BIT5;
	public static final int CRT_WA2_PUT_CONDITION1		= 	BIT6;	
	public static final int CRT_WA2_PUT_CONDITION2		= 	BIT7;
	public static final int CRT_WA2_PUT_CONDITION3		= 	BIT8;	
	// BITS 9-11 UNUSED
	public static final int CRT_WA2_PUT_AIRBLOW			= 	BIT12;	
	public static final int CRT_WA2_PUT_ORIENT1			= 	BIT13;	
	public static final int CRT_WA2_PUT_ORIENT2			= 	BIT14;	
	
	public static final int CONFIG_5 = 24;
	public static final int CRT_WA1_CYST_DOOR1_OPEN		= 	BIT0;
	public static final int CRT_WA1_CYST_DOOR1_CLOSE	= 	BIT1;
	public static final int CRT_WA1_CYST_DOOR2_OPEN		= 	BIT2;
	public static final int CRT_WA1_CYST_DOOR2_CLOSE	= 	BIT3;
	public static final int CRT_WA1_CYST_CLAMP_UNCLAMP 	= 	BIT4;
	public static final int CRT_WA1_CYST_CLAMP_CLAMPING	= 	BIT5;
	public static final int CRT_WA1_CYST_CONDITION1		= 	BIT6;	
	public static final int CRT_WA1_CYST_CONDITION2		= 	BIT7;
	public static final int CRT_WA1_CYST_CONDITION3		= 	BIT8;	
	// BITS 9-11 UNUSED
	public static final int CRT_WA1_PICK_ORIENT_TIMER	= 	BIT12;	
	public static final int CRT_WA1_PUT_ORIENT_TIMER	= 	BIT13;	
	
	public static final int CONFIG_6 = 25;
	public static final int CRT_WA2_CYST_DOOR1_OPEN		= 	BIT0;
	public static final int CRT_WA2_CYST_DOOR1_CLOSE	= 	BIT1;
	public static final int CRT_WA2_CYST_DOOR2_OPEN		= 	BIT2;
	public static final int CRT_WA2_CYST_DOOR2_CLOSE	= 	BIT3;
	public static final int CRT_WA2_CYST_CLAMP_UNCLAMP 	= 	BIT4;
	public static final int CRT_WA2_CYST_CLAMP_CLAMPING	= 	BIT5;
	public static final int CRT_WA2_CYST_CONDITION1		= 	BIT6;	
	public static final int CRT_WA2_CYST_CONDITION2		= 	BIT7;
	public static final int CRT_WA2_CYST_CONDITION3		= 	BIT8;	
	// BITS 9-11 UNUSED
	public static final int CRT_WA2_PICK_ORIENT_TIMER	= 	BIT12;	
	public static final int CRT_WA2_PUT_ORIENT_TIMER	= 	BIT13;	
	
	public static final int CONFIG_7 = 26;
	public static final int USE_WA1_PUT_CONDITION1		=	BIT0;
	public static final int USE_WA1_PUT_CONDITION2		=	BIT1;
	public static final int USE_WA1_PUT_CONDITION3		=	BIT2;
	// BITS 3-5 UNUSED
	public static final int USE_WA2_PUT_CONDITION1		=	BIT6;
	public static final int USE_WA2_PUT_CONDITION2		=	BIT7;
	public static final int USE_WA2_PUT_CONDITION3		=	BIT8;
	
	public static final int CONFIG_8 = 27;
	public static final int USE_WA1_PICK_CONDITION1		=	BIT0;
	public static final int USE_WA1_PICK_CONDITION2		=	BIT1;
	public static final int USE_WA1_PICK_CONDITION3		=	BIT2;
	// BITS 3-5 UNUSED
	public static final int USE_WA2_PICK_CONDITION1		=	BIT6;
	public static final int USE_WA2_PICK_CONDITION2		=	BIT7;
	public static final int USE_WA2_PICK_CONDITION3		=	BIT8;
	
	public static final int CONFIG_9 = 28;
	public static final int USE_WA1_CYST_CONDITION1		=	BIT0;
	public static final int USE_WA1_CYST_CONDITION2		=	BIT1;
	public static final int USE_WA1_CYST_CONDITION3		=	BIT2;
	// BITS 3-5 UNUSED
	public static final int USE_WA2_CYST_CONDITION1		=	BIT6;
	public static final int USE_WA2_CYST_CONDITION2		=	BIT7;
	public static final int USE_WA2_CYST_CONDITION3		=	BIT8;
	
	public static final int CONFIG_10 = 29;
	public static final int CON_CLAMP1_UNCLAMP_LMTOPEN	= 	BIT0;
	public static final int CON_CLAMP1_UNCLAMP_LMTCLOSE	= 	BIT1;
	public static final int CON_CLAMP1_UNCLAMP_PRESOPEN	= 	BIT2;
	public static final int CON_CLAMP1_UNCLAMP_PRESCLOSE= 	BIT3;
	public static final int CON_CLAMP1_UNCLAMP_WPP	 	= 	BIT4;
	public static final int CON_CLAMP1_CLAMP_LMTOPEN	= 	BIT5;
	public static final int CON_CLAMP1_CLAMP_LMTCLOSE	= 	BIT6;	
	public static final int CON_CLAMP1_CLAMP_PRESOPEN	= 	BIT7;
	public static final int CON_CLAMP1_CLAMP_PRESCLOSE	= 	BIT8;
	public static final int CON_CLAMP1_CLAMP_WPP	 	= 	BIT9;

	public static final int CONFIG_11 = 30;
	public static final int CON_CLAMP2_UNCLAMP_LMTOPEN	= 	BIT0;
	public static final int CON_CLAMP2_UNCLAMP_LMTCLOSE	= 	BIT1;
	public static final int CON_CLAMP2_UNCLAMP_PRESOPEN	= 	BIT2;
	public static final int CON_CLAMP2_UNCLAMP_PRESCLOSE= 	BIT3;
	public static final int CON_CLAMP2_UNCLAMP_WPP	 	= 	BIT4;
	public static final int CON_CLAMP2_CLAMP_LMTOPEN	= 	BIT5;
	public static final int CON_CLAMP2_CLAMP_LMTCLOSE	= 	BIT6;	
	public static final int CON_CLAMP2_CLAMP_PRESOPEN	= 	BIT7;
	public static final int CON_CLAMP2_CLAMP_PRESCLOSE	= 	BIT8;
	public static final int CON_CLAMP2_CLAMP_WPP	 	= 	BIT9;
}
