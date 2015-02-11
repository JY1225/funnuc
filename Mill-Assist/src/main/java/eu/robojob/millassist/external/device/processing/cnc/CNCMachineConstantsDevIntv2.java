package eu.robojob.millassist.external.device.processing.cnc;

public final class CNCMachineConstantsDevIntv2 {

	private CNCMachineConstantsDevIntv2() {
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
	public static final int IN_MACHINE_AUTO_MODE		= 	BIT0;
	public static final int IN_MACHINE_IN_CYCLUS		=	BIT1;
	public static final int IN_MACHINE_CYCLE_FINISHED	=	BIT2;
	public static final int IN_MACHINE_MACHINE_ALARM	=	BIT3;
	public static final int IN_MACHINE_PRESSURE_OK		=	BIT4;
	public static final int IN_MACHINE_ZONE1_FREE		=	BIT5;
	public static final int IN_MACHINE_ZONE2_FREE		=	BIT6;
	public static final int IN_MACHINE_FEEDHOLD_ACTIVE	=	BIT7;
	public static final int IN_MACHINE_CONDITION_PUT	=	BIT8;
	public static final int IN_MACHINE_CONDITION_PICK	=	BIT9;
	public static final int IN_MACHINE_M_CODE_1			=	BIT10;
	public static final int IN_MACHINE_M_CODE_2			= 	BIT11;
	public static final int MODULE_1_OK					= 	BIT12;
	// BIT 13-15 UNUSED
	
	public static final int INPUT_SLOT_2 = 2;
	public static final int IN_DOOR1_UNLOCKED			= 	BIT0;
	public static final int IN_DOOR1_IS_OPEN 			=	BIT1;
	public static final int IN_DOOR1_IS_CLOSED			= 	BIT2;
	public static final int IN_DOOR1_SWITCH_OPEN		=	BIT3;
	public static final int IN_DOOR1_SWITCH_CLOSE		= 	BIT4;
	public static final int IN_DOOR2_UNLOCKED			= 	BIT5;
	public static final int IN_DOOR2_IS_OPEN 			=	BIT6;
	public static final int IN_DOOR2_IS_CLOSED			= 	BIT7;
	public static final int IN_DOOR2_SWITCH_OPEN		=	BIT8;
	public static final int IN_DOOR2_SWITCH_CLOSE		= 	BIT9;
	public static final int IN_MACHINE_M_CODE_3			=	BIT10;
	public static final int IN_MACHINE_M_CODE_4			= 	BIT11;
	public static final int MODULE_2_OK					= 	BIT12;
	// BIT 13-15 UNUSED
	
	public static final int INPUT_SLOT_3 = 3;
	public static final int IN_FIX1_SWITCH_SELECTED		= 	BIT0;
	public static final int IN_FIX1_IS_OPEN				= 	BIT1;
	public static final int IN_FIX1_IS_CLOSED			= 	BIT2;
	public static final int IN_FIX1_IS_CLAMPED			= 	BIT3;
	// BIT 4 UNUSED
	public static final int IN_FIX2_SWITCH_SELECTED		= 	BIT5;
	public static final int IN_FIX2_IS_OPEN				= 	BIT6;
	public static final int IN_FIX2_IS_CLOSED			= 	BIT7;
	public static final int IN_FIX2_IS_CLAMPED			= 	BIT8;
	// BIT 9 UNUSED
	public static final int IN_SWITCH_FIXTURE_OPEN		=	BIT10;
	public static final int IN_SWITCH_FIXTURE_CLOSED	=	BIT11;
	public static final int MODULE_3_OK					= 	BIT12;
	// BITS 13-15 UNUSED
	
	// Y: OUTPUTS
	
	public static final int OUTPUT_SLOT_1 = 4;
	public static final int OUT_MACHINE_OPERATOR_RQSTD			= 	BIT0;
	public static final int OUT_MACHINE_NC_RESET				= 	BIT1;
	public static final int OUT_MACHINE_START_CYCLE 			= 	BIT2;
	public static final int OUT_MACHINE_POWER_OFF				= 	BIT3;
	public static final int OUT_MACHINE_ROBOT_OUT_OF_MACHINE	= 	BIT4;
	public static final int OUT_MACHINE_FEEDHOLD				= 	BIT5;
	public static final int OUT_MACHINE_RELEASE_ZONE_1			= 	BIT6;
	public static final int OUT_MACHINE_RELEASE_ZONE_2			= 	BIT7;
	public static final int OUT_MACHINE_M_CODE_FINISHED			=	BIT8;
	// BITS 9-11 UNUSED
	public static final int MODULE_4_OK							= 	BIT12;
	// BITS 13-15 UNUSED
	
	
	public static final int OUTPUT_SLOT_2 = 5;
	public static final int OUT_DOOR1_UNLOCK			= 	BIT0;
	public static final int OUT_DOOR1_OPEN	 			=	BIT1;
	public static final int OUT_DOOR1_CLOSE				= 	BIT2;
	public static final int OUT_DOOR1_LED				=	BIT3;
	// BIT 4 UNUSED
	public static final int OUT_DOOR2_UNLOCK			= 	BIT5;
	public static final int OUT_DOOR2_OPEN 				=	BIT6;
	public static final int OUT_DOOR2_CLOSED			= 	BIT7;
	public static final int OUT_DOOR2_LED				=	BIT8;
	public static final int OUT_MACHINE_GREEN_LAMP		= 	BIT9;
	public static final int OUT_MACHINE_BLUE_LAMP		= 	BIT10;
	public static final int OUT_MACHINE_ORANGE_LAMP		= 	BIT11;
	// BIT 11 UNUSED 
	public static final int MODULE_5_OK					= 	BIT12;
	// BITS 13-15 UNUSED
	
	
	public static final int OUTPUT_SLOT_3 = 6;
	public static final int OUT_FIX1_OPEN				= 	BIT0;
	public static final int OUT_FIX1_CLOSE	 			=	BIT1;
	public static final int OUT_FIX1_LED				= 	BIT2;
	public static final int OUT_FIX1_AIRBLOW			=	BIT3;
	// BIT 4 UNUSED
	public static final int OUT_FIX2_OPEN				= 	BIT5;
	public static final int OUT_FIX2_CLOSE	 			=	BIT6;
	public static final int OUT_FIX2_LED				= 	BIT7;
	public static final int OUT_FIX2_AIRBLOW			=	BIT8;
	// BIT 9-11 UNUSED 
	public static final int MODULE_6_OK					= 	BIT12;
	// BITS 13-15 UNUSED
	
	
	public static final int INPUT_SLOT_4 = 7;
	public static final int IN_FIX3_SWITCH_SELECTED		= 	BIT0;
	public static final int IN_FIX3_IS_OPEN				= 	BIT1;
	public static final int IN_FIX3_IS_CLOSED			= 	BIT2;
	public static final int IN_FIX3_IS_CLAMPED			= 	BIT3;
	// BIT 4 UNUSED
	public static final int IN_FIX4_SWITCH_SELECTED		= 	BIT5;
	public static final int IN_FIX4_IS_OPEN				= 	BIT6;
	public static final int IN_FIX4_IS_CLOSED			= 	BIT7;
	public static final int IN_FIX4_IS_CLAMPED			= 	BIT8;
	// BIT 9-11 UNUSED
	public static final int MODULE_7_OK					= 	BIT12;
	// BITS 13-15 UNUSED
		
	public static final int OUTPUT_SLOT_4 = 8;
	public static final int OUT_FIX3_OPEN				= 	BIT0;
	public static final int OUT_FIX3_CLOSE	 			=	BIT1;
	public static final int OUT_FIX3_LED				= 	BIT2;
	public static final int OUT_FIX3_AIRBLOW			=	BIT3;
	// BIT 4 UNUSED
	public static final int OUT_FIX4_OPEN				= 	BIT5;
	public static final int OUT_FIX4_CLOSE	 			=	BIT6;
	public static final int OUT_FIX4_LED				= 	BIT7;
	public static final int OUT_FIX4_AIRBLOW			=	BIT8;
	// BIT 9-11 UNUSED 
	public static final int MODULE_8_OK					= 	BIT12;
	// BITS 13-15 UNUSED
	
	
	
	// R: RESULTS
	
	public static final int STATUS_SLOT_1 = 15;
	public static final int R_MACHINE_ERROR				= 	BIT0;
	public static final int R_MACHINE_READY_FOR_START	=	BIT1;
	public static final int R_MACHINE_IPC_CONNECTION_OK	=	BIT2;
	// BITS 3-4 UNUSED
	public static final int R_MACHINE_ZONE1_PROCESSING	=	BIT5;
	public static final int R_MACHINE_ZONE2_PROCESSING	=	BIT6;
	public static final int R_DOOR_1_OPEN				= 	BIT7;
	public static final int R_DOOR_1_CLOSED				= 	BIT8;
	public static final int R_DOOR_2_OPEN				=	BIT9;
	public static final int R_DOOR_2_CLOSED				= 	BIT10;
	// BIT 11 UNUSED
	public static final int R_MACHINE_M_CODE_1			= 	BIT12;
	public static final int R_MACHINE_M_CODE_2			=	BIT13;
	public static final int R_MACHINE_M_CODE_3			=	BIT14;
	public static final int R_MACHINE_M_CODE_4			=	BIT15;

		
	public static final int STATUS_SLOT_2 = 16;
	public static final int R_FIX1_OPEN				= 	BIT0;
	public static final int R_FIX1_CLOSE 			=	BIT1;
	public static final int R_FIX1_CLAMPED			=	BIT2;
	// BIT 3 UNUSED
	public static final int R_FIX2_OPEN				= 	BIT4;
	public static final int R_FIX2_CLOSE 			=	BIT5;
	public static final int R_FIX2_CLAMPED			=	BIT6;
	// BIT 7 UNUSED
	public static final int R_FIX3_OPEN				= 	BIT8;
	public static final int R_FIX3_CLOSE 			=	BIT9;
	public static final int R_FIX3_CLAMPED			=	BIT10;
	// BIT 11 UNUSED
	public static final int R_FIX4_OPEN				= 	BIT12;
	public static final int R_FIX4_CLOSE 			=	BIT13;
	public static final int R_FIX4_CLAMPED			=	BIT14;
	// BIT 15 UNUSED
	
	
	// SELECTION REGISTER (ZONE/WORKAREA/FIXTURE)
	
	public static final int ZONE_WA_FIX_SELECT = 18;
	public static final int ZONE1_SELECT			= 	BIT0;
	public static final int ZONE2_SELECT			=	BIT1;
	public static final int WA1_SELECT				=	BIT2;
	public static final int WA2_SELECT				=	BIT3;
	// BIT 4 UNUSED
	public static final int FIX_SELECT_1			=	BIT5;
	public static final int FIX_SELECT_2			=	BIT6;
	public static final int FIX_SELECT_3			= 	BIT7;
	public static final int FIX_SELECT_4			= 	BIT8;
	// BITS 9-15 UNUSED

	
	// COMMAND REGISTERS
	
	public static final int IPC_COMMAND = 19;
	public static final int IPC_PREPARE_FOR_PUT_CMD			= 	BIT0;
	public static final int IPC_PREPARE_FOR_PICK_CMD		= 	BIT1;
	public static final int IPC_CLAMP_CMD					= 	BIT2;
	public static final int IPC_UNCLAMP_CMD					= 	BIT3;
	public static final int IPC_START_CNC_CMD				= 	BIT4;
	public static final int IPC_NC_RESET_CMD				= 	BIT5;
	public static final int IPC_MC_FINISH_CMD				= 	BIT6;
	// BITS 7-9 UNUSED	
	public static final int IPC_RESET_CMD					= 	BIT10;
	public static final int IPC_ABORT_CMD					= 	BIT11;	
	public static final int IPC_ROBOT_OUT_MACHINE_CMD		= 	BIT12;	
	public static final int IPC_ROBOT_OUT_MACHINE_VALUE_CMD	= 	BIT13;	
	// BITS 14-15 UNUSED
	
	
	public static final int IPC_BUSY = 20;
	public static final int IPC_PREPARE_FOR_PUT_BUSY	= 	BIT0;
	public static final int IPC_PREPARE_FOR_PICK_BUSY	= 	BIT1;
	public static final int IPC_CLAMP_BUSY				= 	BIT2;
	public static final int IPC_UNCLAMP_BUSY			= 	BIT3;
	public static final int IPC_START_CNC_BUSY			= 	BIT4;
	public static final int IPC_NC_RESET_BUSY			= 	BIT5;
	public static final int IPC_MC_FINISH_BUSY			= 	BIT6;
	// BITS 7-15 UNUSED
	
	public static final int IPC_ERROR = 21;
	public static final int IPC_PREPARE_FOR_PUT_ERR		= 	BIT0;
	public static final int IPC_PREPARE_FOR_PICK_ERR	= 	BIT1;
	public static final int IPC_CLAMP_ERR				= 	BIT2;
	public static final int IPC_UNCLAMP_ERR				= 	BIT3;
	public static final int IPC_START_CNC_ERR			= 	BIT4;
	public static final int IPC_NC_RESET_ERR			= 	BIT5;
	public static final int IPC_MC_FINISH_ERR			= 	BIT6;
	// BITS 7-15 UNUSED
	
	public static final int IPC_OK = 22;
	public static final int IPC_PREPARE_FOR_PUT_OK		= 	BIT0;
	public static final int IPC_PREPARE_FOR_PICK_OK		= 	BIT1;
	public static final int IPC_CLAMP_OK				= 	BIT2;
	public static final int IPC_UNCLAMP_OK				= 	BIT3;
	public static final int IPC_START_CNC_OK			= 	BIT4;
	public static final int IPC_NC_RESET_OK				= 	BIT5;
	public static final int IPC_MC_FINISH_OK			= 	BIT6;
	// BITS 7-15 UNUSED
	
	
	// CNC ERRORS & ALARMS
	
	public static final int ERROR_REG_1 = 25;
	public static final int ERR_GENERAL 						=	BIT0;
	public static final int ERR_NO_COMM_WITH_IPC 				=	BIT1;
	public static final int ERR_MACHINE_ALR						= 	BIT2;
	public static final int ERR_NO_AIR_DETECTED					=	BIT3;
	public static final int ERR_MACHINE_NOT_RDY_FOR_START		=	BIT4;
	public static final int ERR_CONFIG_FIXTURES 				=	BIT5;
	public static final int ERR_FIXTURE_CLAMP_RELEASED			= 	BIT6;
	public static final int ERR_CONFIG_DOORS					=	BIT7;
	public static final int ERR_OPEN_DOOR_ZONE_NOT_FREE			=	BIT8;
	public static final int ERR_OPEN_DOOR_CYCLE_NOT_FINISHED	= 	BIT9;
	public static final int ERR_PREP_PUT_MACHINE_NOT_READY		=	BIT10;
	public static final int ERR_PREP_PUT_OTHER_CMD_BUSY			=	BIT11;
	public static final int ERR_PREP_PUT_TIMEOUT				= 	BIT12;
	public static final int ERR_PREP_PUT_WRONG_ZONE_WA			=	BIT13;
	public static final int ERR_PREP_PUT_WRONG_FIXTURE			=   BIT14;
	public static final int ERR_PREP_PUT_ZONE_NOT_FREE			=	BIT15;
	
	public static final int[] ERROR_REG_1_ARRAY = new int[]{
		ERR_GENERAL,
		ERR_NO_COMM_WITH_IPC,
		ERR_MACHINE_ALR,
		ERR_NO_AIR_DETECTED,
		ERR_MACHINE_NOT_RDY_FOR_START,
		ERR_CONFIG_FIXTURES,
		ERR_FIXTURE_CLAMP_RELEASED,
		ERR_CONFIG_DOORS,
		ERR_OPEN_DOOR_ZONE_NOT_FREE,
		ERR_OPEN_DOOR_CYCLE_NOT_FINISHED,
		ERR_PREP_PUT_MACHINE_NOT_READY,
		ERR_PREP_PUT_OTHER_CMD_BUSY,
		ERR_PREP_PUT_TIMEOUT,
		ERR_PREP_PUT_WRONG_ZONE_WA,
		ERR_PREP_PUT_WRONG_FIXTURE,
		ERR_PREP_PUT_ZONE_NOT_FREE};
	
	public static final int ERROR_REG_2 = 26;
	// BITS 0-3 UNUSED
	public static final int ERR_PREP_PUT_CYCLE_NOT_FINISHED		=	BIT0;
	public static final int ERR_PREP_PUT_TIMEOUT_FXTR_OPEN		= 	BIT1;
	public static final int ERR_PREP_PUT_TIMEOUT_DOOR_CLOSED	=	BIT2;
	public static final int ERR_PREP_PUT_TIMEOUT_DOOR_OPEN		=	BIT3;
	public static final int ERR_PREP_PUT_ERROR_OCCURED			=   BIT4;
	public static final int ERR_PREP_PICK_MACHINE_NOT_READY		=	BIT9;
	public static final int ERR_PREP_PICK_OTHER_CMD_BUSY		= 	BIT10;
	public static final int ERR_PREP_PICK_TIMEOUT				=	BIT11;
	public static final int ERR_PREP_PICK_WRONG_ZONE_WA			=	BIT11;
	public static final int ERR_PREP_PICK_WORNG_FIXTURE			= 	BIT13;
	public static final int ERR_PREP_PICK_ZONE_NOT_FREE			= 	BIT14;
	public static final int ERR_PREP_PICK_CYCLE_NOT_FINISHED	=	BIT15;
	
	public static final int[] ERROR_REG_2_ARRAY = new int[]{
		ERR_PREP_PUT_CYCLE_NOT_FINISHED,
		ERR_PREP_PUT_TIMEOUT_FXTR_OPEN,
		ERR_PREP_PUT_TIMEOUT_DOOR_CLOSED,
		ERR_PREP_PUT_TIMEOUT_DOOR_OPEN,
		ERR_PREP_PUT_ERROR_OCCURED,
		ERR_PREP_PICK_MACHINE_NOT_READY,
		ERR_PREP_PICK_OTHER_CMD_BUSY,
		ERR_PREP_PICK_TIMEOUT,
		ERR_PREP_PICK_WRONG_ZONE_WA,
		ERR_PREP_PICK_WORNG_FIXTURE,
		ERR_PREP_PICK_ZONE_NOT_FREE,
		ERR_PREP_PICK_CYCLE_NOT_FINISHED};
	
	public static final int ERROR_REG_3 = 27;

	public static final int ERR_PREP_PICK_TIMEOUT_DOOR_CLOSED	=	BIT0;
	public static final int ERR_PREP_PICK_TIMEOUT_DOOR_OPEN		=	BIT1;
	public static final int ERR_PREP_PICK_ERROR_OCCURED			=	BIT2;
	// BITS 7 UNUSED
	public static final int ERR_CLAMP_MACHINE_NOT_READY			=	BIT8;
	public static final int ERR_CLAMP_OTHER_CMD_BUSY			=	BIT9;
	public static final int ERR_CLAMP_TIMEOUT_FXTR_CLOSED		= 	BIT10;
	public static final int ERR_CLAMP_WRONG_ZONE_WA				=	BIT11;
	public static final int ERR_CLAMP_WRONG_FIXTURES_SELECT		=	BIT12;
	public static final int ERR_CLAMP_CLOSED_NOT_CLAMPED		=	BIT13;
	public static final int ERR_CLAMP_ERROR_OCCURED				= 	BIT14;
	// BIT 15 UNUSED
	
	public static final int[] ERROR_REG_3_ARRAY = new int[]{
		ERR_PREP_PICK_TIMEOUT_DOOR_CLOSED,
		ERR_PREP_PICK_TIMEOUT_DOOR_OPEN,
		ERR_PREP_PICK_ERROR_OCCURED,
		ERR_CLAMP_MACHINE_NOT_READY,
		ERR_CLAMP_OTHER_CMD_BUSY,
		ERR_CLAMP_TIMEOUT_FXTR_CLOSED,
		ERR_CLAMP_WRONG_ZONE_WA,
		ERR_CLAMP_WRONG_FIXTURES_SELECT,
		ERR_CLAMP_CLOSED_NOT_CLAMPED,
		ERR_CLAMP_ERROR_OCCURED};
	
	public static final int ERROR_REG_4 = 28;
	// BITS 0-1 UNUSED
	public static final int ERR_UNCLAMP_MACHINE_NOT_READY		=	BIT2;
	public static final int ERR_UNCLAMP_OTHER_CMD_BUSY			=	BIT3;
	public static final int ERR_UNCLAMP_TIMEOUT_FXTR_OPEN		= 	BIT4;
	public static final int ERR_UNCLAMP_WRONG_ZONE_WA			=	BIT5;
	public static final int ERR_UNCLAMP_ZONE_NOT_FREE			=	BIT6;
	public static final int ERR_UNCLAMP_CYCLE_NOT_FINISHED		=	BIT7;
	public static final int ERR_UNCLAMP_WRONG_FIXTURES_SELECT 	=	BIT8;
	public static final int ERR_UNCLAMP_ERROR_OCCURED			= 	BIT9;
	// BITS 10-11 UNUSED
	public static final int ERR_START_CNC_MACHINE_NOT_READY		=	BIT12;
	public static final int ERR_START_CNC_OTHER_CMD_BUSY		=	BIT13;
	public static final int ERR_START_CNC_TIMEOUT				= 	BIT14;
	public static final int ERR_START_CNC_NOT_IN_AUTO_MODE		=	BIT15;
	
	public static final int[] ERROR_REG_4_ARRAY = new int[]{
		ERR_UNCLAMP_MACHINE_NOT_READY,
		ERR_UNCLAMP_OTHER_CMD_BUSY,
		ERR_UNCLAMP_TIMEOUT_FXTR_OPEN,
		ERR_UNCLAMP_WRONG_ZONE_WA,
		ERR_UNCLAMP_ZONE_NOT_FREE,
		ERR_UNCLAMP_CYCLE_NOT_FINISHED,
		ERR_UNCLAMP_WRONG_FIXTURES_SELECT,
		ERR_UNCLAMP_ERROR_OCCURED,
		ERR_START_CNC_MACHINE_NOT_READY,
		ERR_START_CNC_OTHER_CMD_BUSY,
		ERR_START_CNC_TIMEOUT,
		ERR_START_CNC_NOT_IN_AUTO_MODE};
	
	public static final int ERROR_REG_5 = 29;
	public static final int ERR_START_ERROR_OCCURED				= 	BIT0;
	// BITS 1-5 UNUSED
	public static final int ERR_NC_RESET_MACHINE_NOT_READY		=	BIT6;
	public static final int ERR_NC_RESET_OTHER_CMD_BUSY			=	BIT7;
	public static final int ERR_NC_RESET_TIMEOUT			 	=	BIT8;
	public static final int ERR_NC_RESET_ERROR_OCCURED		 	=	BIT9;
	// BITS 10-15 UNUSED
	
	public static final int[] ERROR_REG_5_ARRAY = new int[]{
		ERR_START_ERROR_OCCURED,
		ERR_NC_RESET_MACHINE_NOT_READY,
		ERR_NC_RESET_OTHER_CMD_BUSY,
		ERR_NC_RESET_TIMEOUT,
		ERR_NC_RESET_ERROR_OCCURED};
	
	public static final int ERROR_REG_6 = 30;
	public static final int ERR_MC_FINISH_MACHINE_NOT_READY		=	BIT0;
	public static final int ERR_MC_FINISH_OTHER_CMD_BUSY		=	BIT1;
	public static final int ERR_MC_FINISH_TIMEOUT			 	=	BIT2;
	public static final int ERR_MC_FINISH_NOT_IN_MODE_MC		=	BIT3;
	public static final int ERR_MC_FINISH_TIMEOUT_DOOR_CLOSED	=	BIT4;
	public static final int ERR_MC_FINISH_ERROR_OCCURED			=	BIT5;
	// BITS 6-10 UNUSED
	public static final int ERR_PROBU_COMM_ERROR				=	BIT11;
	public static final int ERR_PROBU_ALARM						=	BIT12;
	public static final int ERR_PROBU_ANYBU_ALARM				=	BIT13;
	
	public static final int[] ERROR_REG_6_ARRAY = new int[]{
		ERR_MC_FINISH_MACHINE_NOT_READY,
		ERR_MC_FINISH_OTHER_CMD_BUSY,
		ERR_MC_FINISH_TIMEOUT,
		ERR_MC_FINISH_NOT_IN_MODE_MC,
		ERR_MC_FINISH_TIMEOUT_DOOR_CLOSED,
		ERR_MC_FINISH_ERROR_OCCURED,
		ERR_PROBU_COMM_ERROR,
		ERR_PROBU_ALARM,
		ERR_PROBU_ANYBU_ALARM};
	
	public static final int[][] ERROR_REGS_ARRAY = new int[][]{
		ERROR_REG_1_ARRAY,
		ERROR_REG_2_ARRAY,
		ERROR_REG_3_ARRAY,
		ERROR_REG_4_ARRAY,
		ERROR_REG_5_ARRAY,
		ERROR_REG_6_ARRAY,
	};
	
	// CONFIGURATION
	
	public static final int CONFIG_1 = 35;
	public static final int CFG_MACHINE_ZONE_1_PRESENT			= 	BIT0;
	public static final int CFG_MACHINE_ZONE_2_PRESENT			= 	BIT1;
	public static final int CFG_MACHINE_ZONE_1_DOORS_1			= 	BIT2;
	public static final int CFG_MACHINE_ZONE_1_DOORS_2			= 	BIT3;
	public static final int CFG_MACHINE_ZONE_2_DOORS_2 			= 	BIT4;
	public static final int CFG_MACHINE_MODE_M_CODE				= 	BIT5;
	public static final int CFG_MACHINE_MC_FINISH_DOOR_CLOSED	= 	BIT6;	
	public static final int CFG_MACHINE_USE_ZONE_FREE			= 	BIT7;
	public static final int CFG_MACHINE_USE_CYCLE_FINISHED		= 	BIT8;	
	public static final int CFG_MACHINE_NEGATIVE_MACHINE_ALARM	= 	BIT9;
	public static final int CFG_MACHINE_SIMULATED				= 	BIT10;
	// BITS 11 UNUSED
	public static final int CFG_DOORS_1_USE_TIMERS				= 	BIT12;	
	public static final int CFG_DOORS_1_USE_UNLOCK				= 	BIT13;	
	public static final int CFG_DOORS_2_USE_TIMERS				= 	BIT14;	
	public static final int CFG_DOORS_2_USE_UNLOCK				= 	BIT15;	

	public static final int CONFIG_2 = 36;
	public static final int CFG_MACHINE_ZONE_1_WA_1_FIX_1		= 	BIT0;
	public static final int CFG_MACHINE_ZONE_1_WA_1_FIX_2		= 	BIT1;
	public static final int CFG_MACHINE_ZONE_1_WA_1_FIX_3		= 	BIT2;
	public static final int CFG_MACHINE_ZONE_1_WA_1_FIX_4		= 	BIT3;
	public static final int CFG_MACHINE_ZONE_1_WA_2_FIX_1 		= 	BIT4;
	public static final int CFG_MACHINE_ZONE_1_WA_2_FIX_2		= 	BIT5;
	public static final int CFG_MACHINE_ZONE_1_WA_2_FIX_3		= 	BIT6;	
	public static final int CFG_MACHINE_ZONE_1_WA_2_FIX_4		= 	BIT7;
	public static final int CFG_MACHINE_ZONE_2_WA_1_FIX_1		= 	BIT8;	
	public static final int CFG_MACHINE_ZONE_2_WA_1_FIX_2		= 	BIT9;
	public static final int CFG_MACHINE_ZONE_2_WA_1_FIX_3		= 	BIT10;	
	public static final int CFG_MACHINE_ZONE_2_WA_1_FIX_4		= 	BIT11;	
	public static final int CFG_MACHINE_ZONE_2_WA_2_FIX_1		= 	BIT12;	
	public static final int CFG_MACHINE_ZONE_2_WA_2_FIX_2		= 	BIT13;
	public static final int CFG_MACHINE_ZONE_2_WA_2_FIX_3		= 	BIT14;	
	public static final int CFG_MACHINE_ZONE_2_WA_2_FIX_4		= 	BIT15;
	
	public static final int CONFIG_3 = 37;
	public static final int CFG_MACHINE_ZONE_1_WA_1_PUT_USE_CONDITION	= 	BIT0;
	public static final int CFG_MACHINE_ZONE_1_WA_1_PUT_CONDITION_VAL	= 	BIT1;
	public static final int CFG_MACHINE_ZONE_1_WA_1_PICK_USE_CONDITION	= 	BIT2;
	public static final int CFG_MACHINE_ZONE_1_WA_1_PICK_CONDITION_VAL	= 	BIT3;
	public static final int CFG_MACHINE_ZONE_1_WA_2_PUT_USE_CONDITION	= 	BIT4;
	public static final int CFG_MACHINE_ZONE_1_WA_2_PUT_CONDITION_VAL	= 	BIT5;
	public static final int CFG_MACHINE_ZONE_1_WA_2_PICK_USE_CONDITION	= 	BIT6;	
	public static final int CFG_MACHINE_ZONE_1_WA_2_PICK_CONDITION_VAL	= 	BIT7;
	public static final int CFG_MACHINE_ZONE_2_WA_1_PUT_USE_CONDITION	= 	BIT8;	
	public static final int CFG_MACHINE_ZONE_2_WA_1_PUT_CONDITION_VAL	= 	BIT9;
	public static final int CFG_MACHINE_ZONE_2_WA_1_PICK_USE_CONDITION	= 	BIT10;	
	public static final int CFG_MACHINE_ZONE_2_WA_1_PICK_CONDITION_VAL	= 	BIT11;	
	public static final int CFG_MACHINE_ZONE_2_WA_2_PUT_USE_CONDITION	= 	BIT12;	
	public static final int CFG_MACHINE_ZONE_2_WA_2_PUT_CONDITION_VAL	= 	BIT13;
	public static final int CFG_MACHINE_ZONE_2_WA_2_PICK_USE_CONDITION	= 	BIT14;	
	public static final int CFG_MACHINE_ZONE_2_WA_2_PICK_CONDITION_VAL	= 	BIT15;
	
	public static final int CONFIG_4 = 38;
	public static final int CFG_FIX_1_USE_TIMERS				= 	BIT0;
	public static final int CFG_FIX_1_USE_TIMERS_MEMORY			= 	BIT1;
	public static final int CFG_FIX_1_USE_CLAMPED_SENSOR		= 	BIT2;
	public static final int CFG_FIX_1_AIRBLOW_PUT				= 	BIT3;
	public static final int CFG_FIX_1_AIRBLOW_PUT_DOOR_CLOSED	= 	BIT4;
	public static final int CFG_FIX_1_AIRBLOW_PICK				= 	BIT5;
	public static final int CFG_FIX_1_AIRBLOW_PICK_DOOR_CLOSED	= 	BIT6;
	// BITS 7-15 UNUSED
	
	public static final int CONFIG_5 = 39;
	public static final int CFG_FIX_2_USE_TIMERS				= 	BIT0;
	public static final int CFG_FIX_2_USE_TIMERS_MEMORY			= 	BIT1;
	public static final int CFG_FIX_2_USE_CLAMPED_SENSOR		= 	BIT2;
	public static final int CFG_FIX_2_AIRBLOW_PUT				= 	BIT3;
	public static final int CFG_FIX_2_AIRBLOW_PUT_DOOR_CLOSED	= 	BIT4;
	public static final int CFG_FIX_2_AIRBLOW_PICK				= 	BIT5;
	public static final int CFG_FIX_2_AIRBLOW_PICK_DOOR_CLOSED	= 	BIT6;
	// BITS 7-15 UNUSED
	
	public static final int CONFIG_6 = 40;
	public static final int CFG_FIX_3_USE_TIMERS				= 	BIT0;
	public static final int CFG_FIX_3_USE_TIMERS_MEMORY			= 	BIT1;
	public static final int CFG_FIX_3_USE_CLAMPED_SENSOR		= 	BIT2;
	public static final int CFG_FIX_3_AIRBLOW_PUT				= 	BIT3;
	public static final int CFG_FIX_3_AIRBLOW_PUT_DOOR_CLOSED	= 	BIT4;
	public static final int CFG_FIX_3_AIRBLOW_PICK				= 	BIT5;
	public static final int CFG_FIX_3_AIRBLOW_PICK_DOOR_CLOSED	= 	BIT6;
	// BITS 7-15 UNUSED
	
	public static final int CONFIG_7 = 41;
	public static final int CFG_FIX_4_USE_TIMERS				= 	BIT0;
	public static final int CFG_FIX_4_USE_TIMERS_MEMORY			= 	BIT1;
	public static final int CFG_FIX_4_USE_CLAMPED_SENSOR		= 	BIT2;
	public static final int CFG_FIX_4_AIRBLOW_PUT				= 	BIT3;
	public static final int CFG_FIX_4_AIRBLOW_PUT_DOOR_CLOSED	= 	BIT4;
	public static final int CFG_FIX_4_AIRBLOW_PICK				= 	BIT5;
	public static final int CFG_FIX_4_AIRBLOW_PICK_DOOR_CLOSED	= 	BIT6;
	// BITS 7-15 UNUSED
	

	// CONFIGURATION - MACHINE PARAMETERS
	
	public static final int PAR_MACHINE_MAX_PUT_PREPARE_TIME 	= 50;
	public static final int PAR_MACHINE_MAX_PICK_PREPARE_TIME 	= 51;
	public static final int PAR_MACHINE_MAX_START_CNC_TIME   	= 52;
	public static final int PAR_MACHINE_MAX_NC_RESET_TIME		= 53;
	public static final int PAR_MACHINE_MAX_MC_FINISH_TIME		= 54;
	public static final int PAR_MACHINE_START_CNC_DELAY			= 55;
	public static final int PAR_MACHINE_START_CNC_TIME			= 56;
	public static final int PAR_MACHINE_NC_RESET_TIMER			= 57;
	public static final int PAR_MACHINE_BLUE_LAMP 				= 58;
	public static final int PAR_MACHINE_SIMUALTED_CYCLE_TIME	= 59;

	// CONFIGURATION - DOOR PARAMETERS
	
	public static final int PAR_DOORS_1_TIME_DOOR_OPEN 		= 65;
	public static final int PAR_DOORS_1_TIME_DOOR_CLOSE 	= 66;
	public static final int PAR_DOORS_1_TIME_BUFFER			= 67;

	public static final int PAR_DOORS_2_TIME_DOOR_OPEN 		= 70;
	public static final int PAR_DOORS_2_TIME_DOOR_CLOSE 	= 71;
	public static final int PAR_DOORS_2_TIME_BUFFER			= 72;
	
	// CONFIGURATION - FIXTURE PARAMETERS
	
	public static final int PAR_FIX_TIME_CLOSE 			= 75;
	public static final int PAR_FIX_TIME_OPEN			= 76;
	public static final int PAR_FIX_TIME_AIRBLOW		= 77;
	public static final int PAR_FIX_TIMEOUT_CLAMPED		= 78;
}
