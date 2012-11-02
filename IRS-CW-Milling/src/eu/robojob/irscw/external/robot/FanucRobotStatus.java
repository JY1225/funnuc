package eu.robojob.irscw.external.robot;

public class FanucRobotStatus {

	private int controllerString;
	private double zRest;
	
	public FanucRobotStatus(int controllerString, double zRest) {
		this.controllerString = controllerString;
		this.zRest = zRest;
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
}
