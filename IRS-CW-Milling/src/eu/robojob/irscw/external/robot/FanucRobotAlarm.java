package eu.robojob.irscw.external.robot;

import eu.robojob.irscw.util.Translator;

public class FanucRobotAlarm {

	public int NO_ERROR										=	0;
	public int INVALID_SERVICE_TYPE							=	1;
	public int INVALID_USERFRAME							=	2;
	public int INVALID_GRIPTYPE_FOR_SERVICE					=	3;
	public int NO_PNEUMATIC_PRESSURE						=	10;
	public int REQUESTED_BODY_NOT_IN_TOOLBAY				=	20;
	public int REQUESTED_SUBAGRIP_NOT_IN_TOOLBAY			=	21;
	public int REQUESTED_SUBBGRIP_NOT_IN_TOOLBAY			=	22;
	public int REQUESTED_BODY_NOT_FORESEEN_IN_TOOLBAY		=	23;
	public int REQUESTED_SUBAGRIP_NOT_FORESEEN_IN_TOOLBAY	=	24;
	public int REQUESTED_SUBBGRIP_NOT_FORESEEN_IN_TOOLBAY	=	25;
	public int DOCKING_BODY_GIVES_ERROR						=	26;
	public int DOCKING_SUBAGRIP_GIVES_ERROR					=	27;
	public int DOCKING_SUBBGRIP_GIVES_ERROR					=	28;
	public int UNDOCKING_BODY_GIVES_ERROR					=	29;
	public int UNDOCKING_SUBAGRIP_GIVES_ERROR				=	30;
	public int UNDOCKING_SUBBGRIP_GIVES_ERROR				=	31;
	public int WORKPIECE_NOT_GRIPPED						=	40;
	public int ROBOT_NOT_IN_START_POSITION					=	45;
	
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
		return id + "";
	}
}
