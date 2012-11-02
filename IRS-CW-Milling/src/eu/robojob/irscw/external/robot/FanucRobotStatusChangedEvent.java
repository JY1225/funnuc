package eu.robojob.irscw.external.robot;

public class FanucRobotStatusChangedEvent extends FanucRobotEvent {

	private FanucRobotStatus status;
	
	public FanucRobotStatusChangedEvent(AbstractRobot source, FanucRobotStatus status) {
		super(source, FanucRobotEvent.STATUS_CHANGED);
		this.status = status;
	}

	public FanucRobotStatus getStatus() {
		return status;
	}
}
