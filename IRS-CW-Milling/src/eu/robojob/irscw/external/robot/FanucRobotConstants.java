package eu.robojob.irscw.external.robot;

public class FanucRobotConstants {

	public static final int BIT0								=	0b0000000000000001;
	public static final int BIT1								=	0b0000000000000010;
	public static final int BIT2								=	0b0000000000000100;
	public static final int BIT3								=	0b0000000000001000;
	public static final int BIT4								=	0b0000000000010000;
	public static final int BIT5								=	0b0000000000100000;
	public static final int BIT6								=	0b0000000001000000;
	public static final int BIT7								=	0b0000000010000000;
	public static final int BIT8								=	0b0000000100000000;
	public static final int BIT9								=	0b0000001000000000;
	public static final int BIT10								=	0b0000010000000000;
	public static final int BIT11								=	0b0000100000000000;
	public static final int BIT12								=	0b0001000000000000;
	public static final int BIT13								=	0b0010000000000000;
	public static final int BIT14								=	0b0100000000000000;
	public static final int BIT15								=	0b1000000000000000;
	
	// COMMAND REGISTERS
	public static final int PERMISSIONS							=	54;
	public static final int PERMISSIONS_PICK_GIVEN				=	BIT0;
	public static final int PERMISSIONS_PUT_GIVEN				=	BIT1;
	public static final int PERMISSIONS_PICK_RELEASE_ACK		=	BIT2;
	public static final int PERMISSIONS_PUT_RELEASE_ACK			=	BIT3;
	public static final int PERMISSIONS_JAWS_CHANGE_ACK			=	BIT4;
	public static final int PERMISSIONS_CHANGE_OK				=	BIT5;
	public static final int PERMISSIONS_BAR_MOVED_ACK			=	BIT6;
	
	// ROBOT CONTROLLER VALUE
	public static final int CONTROLLER_VALUE_FAULT_LED			=	BIT0;
	public static final int CONTROLLER_VALUE_CMOS_BATTERY_LOW	=	BIT1;
	
	// ROBOT STATUS REGISTERS
	public static final int STATUS								=	104;
	public static final int STATUS_PICK_RELEASE_REQUEST			=	BIT0;
	public static final int STATUS_PUT_CLAMP_REQUEST			=	BIT1;
	public static final int STATUS_PICK_FINISHED				=	BIT2;
	public static final int STATUS_PUT_FINISHED					=	BIT3;
	public static final int STATUS_PICK_OUT_OF_MACHINE			=	BIT4;
	public static final int STATUS_PUT_OUT_OF_MACHINE			=	BIT5;
	// BIT6 is not used
	public static final int STATUS_GRIPS_CHANGED_FINISHED		=	BIT7;
	public static final int STATUS_ROBOT_IN_JAW_CHANGE_POINT	=	BIT8;
	public static final int STATUS_ROBOT_MOVED_BAR				=	BIT9;
	// TEACHING
	public static final int STATUS_PICK_POSITION_TEACHED		=	BIT10;
	public static final int STATUS_PUT_POSITION_TEACHED			=	BIT11;
	
	// COMMAND IDS
	public static final int COMMAND_ASK_STATUS					=	20;
	public static final int COMMAND_WRITE_SERVICE_GRIPPER		=	40;
	public static final int COMMAND_WRITE_SERVICE_HANDLING		=	41;
	public static final int COMMAND_WRITE_SERVICE_POINT			=	42;
	public static final int COMMAND_WRITE_REGISTER				=	43;
	public static final int COMMAND_WRITE_POSITION_REGISTER		=	44;
	public static final int COMMAND_WRITE_USERFRAME				=	45;
	public static final int COMMAND_SET_PERMISSIONS				=	50;
	public static final int COMMAND_START_SERVICE				=	51;
	public static final int COMMAND_RESTART_PROGRAM				=	60;
	public static final int COMMAND_RESET						=	61;
	public static final int COMMAND_ABORT						=	62;
	public static final int COMMAND_TO_HOME						=	65;
	public static final int COMMAND_TO_JAW_CHANGE				=	66;
	public static final int COMMAND_SET_SPEED					=	67;
	public static final int COMMAND_TO_TRANSPORT_POINT			=	68;
	public static final int COMMAND_ASK_POSITION				=	70;
	
	// RESPONSE IDS
	public static final int RESPONSE_ASK_STATUS					=	120;
	public static final int RESPONSE_WRITE_SERVICE_GRIPPER		=	140;
	public static final int RESPONSE_WRITE_SERVICE_HANDLING		=	141;
	public static final int RESPONSE_WRITE_SERVICE_POINT		=	142;
	public static final int RESPONSE_WRITE_REGISTER				=	143;
	public static final int RESPONSE_WIRTE_POSITION_REGISER		=	144;
	public static final int RESPONSE_WRITE_USERFRAME			=	145;
	public static final int RESPONSE_SET_PERMISSIONS			=	150;
	public static final int RESPONSE_START_SERVICE				=	151;
	public static final int RESPONSE_RESTART_PROGRAM			=	160;
	public static final int RESPONSE_RESET						=	161;
	public static final int RESPONSE_ABORT						=	162;
	public static final int RESPONSE_TO_HOME					=	165;
	public static final int RESPONSE_TO_JAW_CHANGE				=	166;
	public static final int RESPONSE_SET_SPEED					=	167;
	public static final int RESPONSE_TO_TRANSPORT_POINT			=	168;
	public static final int RESPONSE_ASK_POSITION				=	170;
	
	// SERVICE GRIPPER
	public static final int SERVICE_GRIPPER_SERVICE_TYPE_TOOL_CHANGE = 1;
	public static final int SERVICE_GRIPPER_SERVICE_TYPE_PICK = 2;
	public static final int SERVICE_GRIPPER_SERVICE_TYPE_PUT = 3;
	public static final int SERVICE_GRIPPER_SERVICE_TYPE_HOME = 4;
	public static final int SERVICE_GRIPPER_SERVICE_TYPE_JAW_CHANGE = 5;
	
	// SERVICE HANDLING
	public static final int SERVICE_HANDLING_PP_MODE_BAR_MOVE 		=	BIT0;
	public static final int SERVICE_HANDLING_PP_MODE_BAR_BREAK 		=	BIT1;
	public static final int SERIVCE_HANDLING_PP_MODE_NOT_TAKE_WP	=	BIT2;
	public static final int SERVICE_HANDLING_PP_MODE_TC_TEST		=	BIT3;
	public static final int SERVICE_HANDLING_PP_MODE_ORDER_12		=	BIT4;
	public static final int SERVICE_HANDLING_PP_MODE_ORDER_21		=	0;
	public static final int SERVICE_HANDLING_PP_MODE_TEACH 			=	BIT5;
	
	// SERVICE POINT
	public static final int SERVICE_POINT_XYZ_ALLOWED_XYZ			=	1;
	public static final int SERVICE_POINT_XYZ_ALLOWED_XY			=	2;
	public static final int SERVICE_POINT_XYZ_ALLOWED_ANGLE			=	3;
	
	// COMMAND_PERMISSIONS
	public static final int PERMISSIONS_COMMAND_PICK						=	BIT0;
	public static final int PERMISSIONS_COMMAND_PUT							= 	BIT1;
	public static final int PERMISSIONS_COMMAND_PICK_RELEASE_ACK			=	BIT2;
	public static final int PERMISSIONS_COMMAND_PUT_CLAMP_ACK				=	BIT3;
	public static final int PERMISSIONS_COMMAND_JAWS_CHANGED_ACK			=	BIT4;
	public static final int PERMISSIONS_COMMAND_RUN_AFTER_JAWS_CHANGED_ACK 	=	BIT5;
	public static final int PERMISSIOSN_COMMAND_BAR_MOVE_ACK				=	BIT6;
}
