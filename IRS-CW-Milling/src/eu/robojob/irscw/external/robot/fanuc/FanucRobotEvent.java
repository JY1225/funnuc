package eu.robojob.irscw.external.robot.fanuc;

public class FanucRobotEvent {

	private FanucRobot source;
	private int id;
	private long when;
	
	public static final int ROBOT_CONNECTED = 1;
	public static final int ROBOT_DISCONNECTED = 2;
	
	public static final int STATUS_CHANGED = 3;
	public static final int ALARMS_OCCURED = 4;
	
	public FanucRobotEvent (FanucRobot source, int id) {
		this.source = source;
		this.id = id;
		this.when = System.currentTimeMillis();
	}

	public FanucRobot getSource() {
		return source;
	}

	public void setSource(FanucRobot source) {
		this.source = source;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public long getWhen() {
		return when;
	}

	public void setWhen(long when) {
		this.when = when;
	}
}
