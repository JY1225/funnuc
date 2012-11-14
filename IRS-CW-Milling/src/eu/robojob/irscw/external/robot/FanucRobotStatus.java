package eu.robojob.irscw.external.robot;

import java.util.HashSet;
import java.util.Set;

public class FanucRobotStatus {

	private int controllerString;
	private int errorId;
	private int controllerValue;
	private double zRest;
	private Set<FanucRobotAlarm> alarms;
	private int speed;
	
	public FanucRobotStatus(int errorId, int controllerValue, int controllerString, double zRest, int speed) {
		this.controllerString = controllerString;
		this.errorId = errorId;
		this.controllerValue = controllerValue;
		updateAlarmSet();
		this.zRest = zRest;
		this.speed = speed;
	}
	
	public int getErrorId() {
		return errorId;
	}
	
	public int getControllerValue() {
		return controllerValue;
	}
	
	public Set<FanucRobotAlarm> getAlarms() {
		return alarms;
	}
	
	public int getSpeed() {
		return speed;
	}
	
	public void setSpeed(int speed) {
		this.speed = speed;
	}
	
	private void updateAlarmSet() {
		alarms = new HashSet<FanucRobotAlarm>();
		if (errorId != FanucRobotConstants.E_NO_ERROR) {
			switch (errorId) {
				case FanucRobotConstants.E_INVALID_SERVICE_TYPE:
					alarms.add(new FanucRobotAlarm(FanucRobotAlarm.INVALID_SERVICE_TYPE));
					break;
				case FanucRobotConstants.E_INVALID_USERFRAME:
					alarms.add(new FanucRobotAlarm(FanucRobotAlarm.INVALID_USERFRAME));
					break;
				case FanucRobotConstants.E_INVALID_GRIPTYPE_FOR_SERVICE:
					alarms.add(new FanucRobotAlarm(FanucRobotAlarm.INVALID_GRIPTYPE_FOR_SERVICE));
					break;
				case FanucRobotConstants.E_NO_PNEUMATIC_PRESSURE:
					alarms.add(new FanucRobotAlarm(FanucRobotAlarm.NO_PNEUMATIC_PRESSURE));
					break;
				case FanucRobotConstants.E_REQUESTED_BODY_NOT_IN_TOOLBAY:
					alarms.add(new FanucRobotAlarm(FanucRobotAlarm.REQUESTED_BODY_NOT_IN_TOOLBAY));
					break;
				case FanucRobotConstants.E_REQUESTED_SUBAGRIP_NOT_IN_TOOLBAY:
					alarms.add(new FanucRobotAlarm(FanucRobotAlarm.REQUESTED_SUBAGRIP_NOT_IN_TOOLBAY));
					break;
				case FanucRobotConstants.E_REQUESTED_SUBBGRIP_NOT_IN_TOOLBAY:
					alarms.add(new FanucRobotAlarm(FanucRobotAlarm.REQUESTED_SUBBGRIP_NOT_IN_TOOLBAY));
					break;
				case FanucRobotConstants.E_REQUESTED_BODY_NOT_FORESEEN_IN_TOOLBAY:
					alarms.add(new FanucRobotAlarm(FanucRobotAlarm.REQUESTED_BODY_NOT_FORESEEN_IN_TOOLBAY));
					break;
				case FanucRobotConstants.E_REQUESTED_SUBAGRIP_NOT_FORESEEN_IN_TOOLBAY:
					alarms.add(new FanucRobotAlarm(FanucRobotAlarm.REQUESTED_SUBAGRIP_NOT_FORESEEN_IN_TOOLBAY));
					break;
				case FanucRobotConstants.E_REQUESTED_SUBBGRIP_NOT_FORESEEN_IN_TOOLBAY:
					alarms.add(new FanucRobotAlarm(FanucRobotAlarm.REQUESTED_SUBBGRIP_NOT_FORESEEN_IN_TOOLBAY));
					break;
				case FanucRobotConstants.E_DOCKING_BODY_GIVES_ERROR:
					alarms.add(new FanucRobotAlarm(FanucRobotAlarm.DOCKING_BODY_GIVES_ERROR));
					break;
				case FanucRobotConstants.E_DOCKING_SUBAGRIP_GIVES_ERROR:
					alarms.add(new FanucRobotAlarm(FanucRobotAlarm.DOCKING_SUBAGRIP_GIVES_ERROR));
					break;
				case FanucRobotConstants.E_DOCKING_SUBBGRIP_GIVES_ERROR:
					alarms.add(new FanucRobotAlarm(FanucRobotAlarm.DOCKING_SUBBGRIP_GIVES_ERROR));
					break;
				case FanucRobotConstants.E_UNDOCKING_BODY_GIVES_ERROR:
					alarms.add(new FanucRobotAlarm(FanucRobotAlarm.UNDOCKING_BODY_GIVES_ERROR));
					break;
				case FanucRobotConstants.E_UNDOCKING_SUBAGRIP_GIVES_ERROR:
					alarms.add(new FanucRobotAlarm(FanucRobotAlarm.UNDOCKING_SUBAGRIP_GIVES_ERROR));
					break;
				case FanucRobotConstants.E_UNDOCKING_SUBBGRIP_GIVES_ERROR:
					alarms.add(new FanucRobotAlarm(FanucRobotAlarm.UNDOCKING_SUBBGRIP_GIVES_ERROR));
					break;
				case FanucRobotConstants.E_WORKPIECE_NOT_GRIPPED:
					alarms.add(new FanucRobotAlarm(FanucRobotAlarm.WORKPIECE_NOT_GRIPPED));
					break;
				case FanucRobotConstants.E_ROBOT_NOT_IN_START_POSITION:
					alarms.add(new FanucRobotAlarm(FanucRobotAlarm.ROBOT_NOT_IN_START_POSITION));
					break;
			}
			
		}
		if ((controllerValue & FanucRobotConstants.CV_FAULT_LED) != 0) {
			alarms.add(new FanucRobotAlarm(FanucRobotAlarm.FAULT_LED));
		}
		if ((controllerValue & FanucRobotConstants.CV_CMOS_BATTERY_LOW) != 0) {
			alarms.add(new FanucRobotAlarm(FanucRobotAlarm.CMOS_BATTERY_LOW));
		}
	}
	
	public boolean isPickReleaseRequested() {
		return ((controllerString & FanucRobotConstants.STATUS_PICK_RELEASE_REQUEST) > 0);
	}
	
	public boolean isPutClampRequested() {
		return ((controllerString & FanucRobotConstants.STATUS_PUT_CLAMP_REQUEST) > 0);
	}
	
	public boolean isPickFinished() {
		return ((controllerString & FanucRobotConstants.STATUS_PICK_FINISHED) > 0);
	}
	
	public boolean isPutFinished() {
		return ((controllerString & FanucRobotConstants.STATUS_PUT_FINISHED) > 0);
	}
	
	public boolean isPickOutOfMachine() {
		return ((controllerString & FanucRobotConstants.STATUS_PICK_OUT_OF_MACHINE) > 0);
	}
	
	public boolean isPutOutOfMachine() {
		return ((controllerString & FanucRobotConstants.STATUS_PUT_OUT_OF_MACHINE) > 0);
	}
	
	public boolean isGripChangedFinished() {
		return ((controllerString & FanucRobotConstants.STATUS_GRIPS_CHANGED_FINISHED) > 0);
	}
	
	public boolean isRobotInJawChangePoint() {
		return ((controllerString & FanucRobotConstants.STATUS_ROBOT_IN_JAW_CHANGE_POINT) > 0);
	}
	
	public boolean isRobotMovedBar() {
		return ((controllerString & FanucRobotConstants.STATUS_ROBOT_MOVED_BAR) > 0);
	}
	
	public int getControllerString() {
		return controllerString;
	}
	
	public double getZRest() {
		return zRest;
	}
	
	public boolean equals(Object o) {
		if (o instanceof FanucRobotStatus) {
			if ((((FanucRobotStatus) o).getZRest() == zRest) && (((FanucRobotStatus) o).getControllerString() == controllerString)) {
				return true;
			}
		}
		return false;
	}
	
	public int hashCode() {
		return controllerString + ((new Double(zRest)).hashCode());
	}
	
	public String toString() {
		return errorId + "-" + controllerValue + "-" + controllerString + "-" + zRest;
	}
}
