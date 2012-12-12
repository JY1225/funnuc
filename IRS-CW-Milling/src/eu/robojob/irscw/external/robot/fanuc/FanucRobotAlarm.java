package eu.robojob.irscw.external.robot.fanuc;

import eu.robojob.irscw.util.Translator;

public class FanucRobotAlarm {

	public static final int INVALID_SERVICE_TYPE							=	1;
	public static final int INVALID_USERFRAME								=	2;
	public static final int INVALID_GRIPTYPE_FOR_SERVICE					=	3;
	public static final int NO_PNEUMATIC_PRESSURE							=	10;
	public static final int REQUESTED_BODY_NOT_IN_TOOLBAY					=	20;
	public static final int REQUESTED_SUBAGRIP_NOT_IN_TOOLBAY				=	21;
	public static final int REQUESTED_SUBBGRIP_NOT_IN_TOOLBAY				=	22;
	public static final int REQUESTED_BODY_NOT_FORESEEN_IN_TOOLBAY			=	23;
	public static final int REQUESTED_SUBAGRIP_NOT_FORESEEN_IN_TOOLBAY		=	24;
	public static final int REQUESTED_SUBBGRIP_NOT_FORESEEN_IN_TOOLBAY		=	25;
	public static final int DOCKING_BODY_GIVES_ERROR						=	26;
	public static final int DOCKING_SUBAGRIP_GIVES_ERROR					=	27;
	public static final int DOCKING_SUBBGRIP_GIVES_ERROR					=	28;
	public static final int UNDOCKING_BODY_GIVES_ERROR						=	29;
	public static final int UNDOCKING_SUBAGRIP_GIVES_ERROR					=	30;
	public static final int UNDOCKING_SUBBGRIP_GIVES_ERROR					=	31;
	public static final int WORKPIECE_NOT_GRIPPED							=	40;
	public static final int ROBOT_NOT_IN_START_POSITION						=	45;
	
	public static final int FAULT_LED 										= 	60;
	public static final int CMOS_BATTERY_LOW 								= 	61;
	
	private int id;
	private Translator translator;
	
	public FanucRobotAlarm(int id) {
		this.id = id;
		this.translator = Translator.getInstance();
	}
	
	public String getMessage() {
		return translator.getTranslation("FanucRobotAlarm." + id);
	}
	
	public int getPriority() {
		return 0;
	}
	
	public int getId() {
		return id;
	}
	
	@Override
	public int hashCode() {
		return id;
	}
	
	public boolean equals(Object o) {
		if (o instanceof FanucRobotAlarm) {
			if (((FanucRobotAlarm) o).getId() == id) {
				return true;
			}
		}
		return false;
	}
	
	public String toString() {
		switch (id) {
			case INVALID_SERVICE_TYPE:
				return "Invalid service type";
			case INVALID_USERFRAME:
				return "Invalid userframe";
			case INVALID_GRIPTYPE_FOR_SERVICE:
				return "Invalid griptype for service";
			case NO_PNEUMATIC_PRESSURE:
				return "No pneumatic pressure";
			case WORKPIECE_NOT_GRIPPED:
				return "Workpiece not gripped";
			case ROBOT_NOT_IN_START_POSITION:
				return "Robot not in start position";
			case FAULT_LED:
				return "Fault led";
			case CMOS_BATTERY_LOW:
				return "CMOS battery low";
			default:
				return "" + id;
		}
	}
	
}
