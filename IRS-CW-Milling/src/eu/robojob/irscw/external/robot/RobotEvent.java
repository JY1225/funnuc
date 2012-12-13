package eu.robojob.irscw.external.robot;


public class RobotEvent {

	private AbstractRobot source;
	private int id;
	private long when;
	
	public static final int ROBOT_CONNECTED = 1;
	public static final int ROBOT_DISCONNECTED = 2;
	public static final int STATUS_CHANGED = 3;
	public static final int ALARMS_OCCURED = 4;
	public static final int ZREST_CHANGED = 5;
	public static final int SPEED_CHANGED = 6;
	
	public RobotEvent(final AbstractRobot source, final int id) {
		this.source = source;
		this.id = id;
		this.when = System.currentTimeMillis();
	}

	public AbstractRobot getSource() {
		return source;
	}

	public void setSource(final AbstractRobot source) {
		this.source = source;
	}

	public int getId() {
		return id;
	}

	public void setId(final int id) {
		this.id = id;
	}

	public long getWhen() {
		return when;
	}

	public void setWhen(final long when) {
		this.when = when;
	}
}
