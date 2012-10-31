package eu.robojob.irscw.external.robot;

public interface RobotListener {

	public void robotConnected(RobotEvent event);
	public void robotDisconnected(RobotEvent event);
	
	public void robotStatusChanged(RobotEvent event);
	public void robotAlarmsOccured(RobotEvent event);
	
}
