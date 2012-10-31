package eu.robojob.irscw.external.robot;

public interface FanucRobotListener {

	public void robotConnected(FanucRobotEvent event);
	public void robotDisconnected(FanucRobotEvent event);
		
	public void robotStatusChanged(FanucRobotEvent event, FanucRobotStatus status);
	public void robotAlarmsOccured(FanucRobotEvent event);
	
}
