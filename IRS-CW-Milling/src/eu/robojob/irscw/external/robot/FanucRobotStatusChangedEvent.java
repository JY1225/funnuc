package eu.robojob.irscw.external.robot;

public class FanucRobotStatusChangedEvent extends FanucRobotEvent {

	private FanucRobotStatus status;
	
	public FanucRobotStatusChangedEvent(FanucRobot source, FanucRobotStatus status) {
		super(source, FanucRobotEvent.STATUS_CHANGED);
		this.status = status;
	}

	public FanucRobotStatus getStatus() {
		return status;
	}
	
	public String toString() {
		return "FanucRobotStatusChangedEvent: " + status.toString();
	}
}
