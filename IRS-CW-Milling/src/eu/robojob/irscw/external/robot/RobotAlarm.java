package eu.robojob.irscw.external.robot;

import java.util.HashSet;
import java.util.Set;

import eu.robojob.irscw.util.Translator;

public class RobotAlarm {

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
	
	private static final int DEFAULT_PRIORITY = 5;
	
	private int id;
	
	public RobotAlarm(final int id) {
		this.id = id;
	}
	
	public String getMessage() {
		return "Robot alarm id: " + id;
	}

	public String getLocalizedMessage() {
		return Translator.getTranslation("RobotAlarm." + id);
	}
	
	public int getPriority() {
		return DEFAULT_PRIORITY;
	}
	
	public int getId() {
		return id;
	}
	
	@Override
	public int hashCode() {
		return id;
	}
	
	public boolean equals(final Object o) {
		if (o instanceof RobotAlarm) {
			if (((RobotAlarm) o).getId() == id) {
				return true;
			}
		}
		return false;
	}
	
	public String toString() {
		return "RobotAlarm: " + id;
	}
	
	//TODO perhaps this methods should be placed in the FanucRobot-class or an helper class
	public static Set<RobotAlarm> parseFanucRobotAlarms(final int errorId, final int controllerValue) {
		Set<RobotAlarm> alarms = new HashSet<RobotAlarm>();
		if (errorId != RobotConstants.E_NO_ERROR) {
			switch (errorId) {
				case RobotConstants.E_INVALID_SERVICE_TYPE:
					alarms.add(new RobotAlarm(RobotAlarm.INVALID_SERVICE_TYPE));
					break;
				case RobotConstants.E_INVALID_USERFRAME:
					alarms.add(new RobotAlarm(RobotAlarm.INVALID_USERFRAME));
					break;
				case RobotConstants.E_INVALID_GRIPTYPE_FOR_SERVICE:
					alarms.add(new RobotAlarm(RobotAlarm.INVALID_GRIPTYPE_FOR_SERVICE));
					break;
				case RobotConstants.E_NO_PNEUMATIC_PRESSURE:
					alarms.add(new RobotAlarm(RobotAlarm.NO_PNEUMATIC_PRESSURE));
					break;
				case RobotConstants.E_REQUESTED_BODY_NOT_IN_TOOLBAY:
					alarms.add(new RobotAlarm(RobotAlarm.REQUESTED_BODY_NOT_IN_TOOLBAY));
					break;
				case RobotConstants.E_REQUESTED_SUBAGRIP_NOT_IN_TOOLBAY:
					alarms.add(new RobotAlarm(RobotAlarm.REQUESTED_SUBAGRIP_NOT_IN_TOOLBAY));
					break;
				case RobotConstants.E_REQUESTED_SUBBGRIP_NOT_IN_TOOLBAY:
					alarms.add(new RobotAlarm(RobotAlarm.REQUESTED_SUBBGRIP_NOT_IN_TOOLBAY));
					break;
				case RobotConstants.E_REQUESTED_BODY_NOT_FORESEEN_IN_TOOLBAY:
					alarms.add(new RobotAlarm(RobotAlarm.REQUESTED_BODY_NOT_FORESEEN_IN_TOOLBAY));
					break;
				case RobotConstants.E_REQUESTED_SUBAGRIP_NOT_FORESEEN_IN_TOOLBAY:
					alarms.add(new RobotAlarm(RobotAlarm.REQUESTED_SUBAGRIP_NOT_FORESEEN_IN_TOOLBAY));
					break;
				case RobotConstants.E_REQUESTED_SUBBGRIP_NOT_FORESEEN_IN_TOOLBAY:
					alarms.add(new RobotAlarm(RobotAlarm.REQUESTED_SUBBGRIP_NOT_FORESEEN_IN_TOOLBAY));
					break;
				case RobotConstants.E_DOCKING_BODY_GIVES_ERROR:
					alarms.add(new RobotAlarm(RobotAlarm.DOCKING_BODY_GIVES_ERROR));
					break;
				case RobotConstants.E_DOCKING_SUBAGRIP_GIVES_ERROR:
					alarms.add(new RobotAlarm(RobotAlarm.DOCKING_SUBAGRIP_GIVES_ERROR));
					break;
				case RobotConstants.E_DOCKING_SUBBGRIP_GIVES_ERROR:
					alarms.add(new RobotAlarm(RobotAlarm.DOCKING_SUBBGRIP_GIVES_ERROR));
					break;
				case RobotConstants.E_UNDOCKING_BODY_GIVES_ERROR:
					alarms.add(new RobotAlarm(RobotAlarm.UNDOCKING_BODY_GIVES_ERROR));
					break;
				case RobotConstants.E_UNDOCKING_SUBAGRIP_GIVES_ERROR:
					alarms.add(new RobotAlarm(RobotAlarm.UNDOCKING_SUBAGRIP_GIVES_ERROR));
					break;
				case RobotConstants.E_UNDOCKING_SUBBGRIP_GIVES_ERROR:
					alarms.add(new RobotAlarm(RobotAlarm.UNDOCKING_SUBBGRIP_GIVES_ERROR));
					break;
				case RobotConstants.E_WORKPIECE_NOT_GRIPPED:
					alarms.add(new RobotAlarm(RobotAlarm.WORKPIECE_NOT_GRIPPED));
					break;
				case RobotConstants.E_ROBOT_NOT_IN_START_POSITION:
					alarms.add(new RobotAlarm(RobotAlarm.ROBOT_NOT_IN_START_POSITION));
					break;
				default:
					throw new IllegalArgumentException("Unknown alarm type");
			}
		}
		if ((controllerValue & RobotConstants.CV_FAULT_LED) != 0) {
			alarms.add(new RobotAlarm(RobotAlarm.FAULT_LED));
		}
		if ((controllerValue & RobotConstants.CV_CMOS_BATTERY_LOW) != 0) {
			alarms.add(new RobotAlarm(RobotAlarm.CMOS_BATTERY_LOW));
		}
		return alarms;
	}
	
}
