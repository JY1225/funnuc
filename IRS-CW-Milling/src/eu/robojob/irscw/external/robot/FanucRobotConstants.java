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
	
}
