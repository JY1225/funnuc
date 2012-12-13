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
	public static final int X_AXE1						=	BIT4;
	public static final int X_AXE2						=	BIT5;
	public static final int X_AXE3						=	BIT6;
	public static final int X_AXE4						=	BIT7;
	public static final int X_AXE5						=	BIT8;
	public static final int X_AXE6						=	BIT9;
	public static final int X_Z1_FREE					=	BIT10;
	public static final int X_Z2_FREE					=	BIT11;

	public static final int INPUT_SLOT_2 = 2;
	public static final int X_CLAMP1_LMT_OPEN			= 	BIT0;
	public static final int X_CLAMP1_LMT_CLOSE			= 	BIT1;
	public static final int X_CLAMP1_PRESSURE_OPEN		= 	BIT2;
	public static final int X_CLAMP1_PRESSURE_CLOSE		= 	BIT3;
	public static final int X_CLAMP1_WPP				= 	BIT4;
	public static final int X_CLAMP2_LMT_OPEN			= 	BIT5;
	public static final int X_CLAMP2_LMT_CLOSE			= 	BIT6;
	public static final int X_CLAMP2_PRESSURE_OPEN		= 	BIT7;
	public static final int X_CLAMP2_PRESSURE_CLOSE		= 	BIT8;
	public static final int X_CLAMP2_WPP				= 	BIT9;
	public static final int X_CLAMP1_ORIENT				= 	BIT10;
	public static final int X_CLAMP2_ORIENT				= 	BIT11;
	
	public static final int INPUT_SLOT_3 = 3;
	public static final int X_DOOR1_OPEN_PERMISSION		= 	BIT0;
	public static final int X_DOOR1_CLOSE				= 	BIT1;
	public static final int X_DOOR1_OPEN				= 	BIT2;
	public static final int X_DOOR2_OPEN_PERMISSION		= 	BIT3;
	public static final int X_DOOR2_CLOSE				= 	BIT4;
	public static final int X_DOOR2_OPEN				= 	BIT5;
	public static final int X_LIGHT_CURTAIN_FREE		= 	BIT6;
	public static final int X_MAIN_PRESSURE				= 	BIT7;
	public static final int X_OIL_TEMP_ALARM			= 	BIT8;
	public static final int X_OIL_LEVEL_LOW				= 	BIT9;
	
	
	// Y: OUTPUTS
	
	public static final int OUTPUT_SLOT_4 = 4;
	public static final int Y_DOOR_OPEN_REQUEST			= 	BIT0;
	public static final int Y_DOOR2_CLOSE				= 	BIT1;
	public static final int Y_DOOR2_OPEN				= 	BIT2;
	public static final int Y_DOOR1_CLOSE				= 	BIT3;
	public static final int Y_DOOR1_OPEN				= 	BIT4;
	public static final int Y_CLAMP1_UNCLAMP			= 	BIT5;
	public static final int Y_CLAMP1_CLAMP				= 	BIT6;
	public static final int Y_CLAMP2_UNCLAMP			= 	BIT7;
	public static final int Y_CLAMP2_CLAMP				= 	BIT8;
	public static final int Y_LAMP_RED					= 	BIT9;
	public static final int Y_LAMP_GREEN				= 	BIT10;
	public static final int Y_LAMP_ORANGE				= 	BIT11;
	
	public static final int OUTPUT_SLOT_5 = 5;
	public static final int Y_INTERLOCK					= 	BIT0;
	public static final int Y_FEED_HOLD					= 	BIT1;
	public static final int Y_CYCLUS_START				= 	BIT2;
	public static final int Y_Z1_RELEASE				= 	BIT3;
	public static final int Y_Z2_RELEASE				= 	BIT4;
	public static final int Y_CLAMP1_ORIENT				= 	BIT5;
	public static final int Y_CLAMP2_ORIENT				= 	BIT6;
	
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
	public static final int R_ROBOT_SERVREQ				= 	BIT14;
	
	
	public static final int CONFIGURATION = 7;
	public static final int DOOR1_PRESENT				= 	BIT0;
	public static final int DOOR2_PRESENT 				= 	BIT1;
	public static final int AXE1_PRESENT				= 	BIT2;
	public static final int AXE2_PRESENT				= 	BIT3;
	public static final int AXE3_PRESENT				= 	BIT4;
	public static final int AXE4_PRESENT				= 	BIT5;
	public static final int AXE5_PRESENT				= 	BIT6;
	public static final int AXE6_PRESENT				= 	BIT7;
	public static final int WA_HAS_CLAMP				= 	BIT8;
	public static final int CLAMP_GROUP_PRESENT			= 	BIT9;
	public static final int ZONE2_PRESENT				= 	BIT10;
	public static final int WORK_AREA2_PRESENT			= 	BIT11;
	
	
	// ALR: ALARMS
	
	public static final int ALARMS_REG1 = 8;
	public static final int ALR_MACHINE					= 	BIT0;
	public static final int ALR_FEED_HOLD 				= 	BIT1;
	public static final int ALR_MAIN_PRESSURE			= 	BIT2;
	public static final int ALR_OIL_TEMP_HIGH			= 	BIT3;
	public static final int ALR_OIL_LEVEL_LOW			= 	BIT4;
	public static final int ALR_DOOR1_NOT_OPEN			= 	BIT5;
	public static final int ALR_DOOR2_NOT_OPEN			= 	BIT6;
	public static final int ALR_DOOR1_NOT_CLOSE			= 	BIT7;
	public static final int ALR_DOOR2_NOT_CLOSE			= 	BIT8;
	public static final int ALR_CLAMP1_NOT_OPEN			= 	BIT9;
	public static final int ALR_CLAMP2_NOT_OPEN			= 	BIT10;
	public static final int ALR_CLAMP1_NOT_CLOSE		= 	BIT11;
	public static final int ALR_CLAMP2_NOT_CLOSE		= 	BIT12;
	
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
	public static final int ALR_MULTIPLE_IPC_RQST		= 	BIT15;
	
	
	// COMMAND REGISTERS
	
	public static final int COMMAND = 8;
	public static final int ALL_CLAMPS_OPEN_CLOSE		= 	BIT0;
	public static final int CLAMP_Z1_OPEN_CLOSE			= 	BIT1;
	public static final int CLAMP_Z2_OPEN_CLOSE			= 	BIT2;
	public static final int DOOR_Z1_OPEN_CLOSE			= 	BIT9;
	public static final int DOOR_Z2_OPEN_CLOSE			= 	BIT10;
	public static final int CYCLE_START					= 	BIT11;
	public static final int CYCLE_STOP					= 	BIT12;	
	public static final int CYCLE_Z1_PERMIT				= 	BIT13;
	public static final int CYCLE_Z2_PERMIT				= 	BIT14;	
	
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
	public static final int IPC_SYNC_OTHER_WA			= 	BIT15;	
	
	public static final int CNC_TASK = 13;
	public static final int WA1_CNC_PROCESS				= 	1;
	public static final int WA2_CNC_PROCESS				= 	2;
	public static final int WA1_WA2_CNC_PROCESS			= 	3;
	public static final int WA2_WA1_CNC_PROCESS			= 	4;
	
	public static final int OTHER = 14;
	public static final int RESET_REQUEST				=	BIT0;
	public static final int OPERATOR_REQUESTED			=	BIT1;
	public static final int ALL_WP_PROCESSED			=	BIT2;
	public static final int NC_RESET					=	BIT3;
	// used with M-codes and service requests
	public static final int SERV_REQ_FINISH				=	BIT4;
	public static final int POWER_OFF					=	BIT5;
	
}
