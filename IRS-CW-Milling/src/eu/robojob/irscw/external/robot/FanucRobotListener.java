package eu.robojob.irscw.external.robot;

public interface FanucRobotListener {

	public void robotConnected(FanucRobotEvent event);
	public void robotDisconnected(FanucRobotEvent event);
		
	public void robotStatusChanged(FanucRobotStatusChangedEvent event);
	public void robotAlarmsOccured(FanucRobotAlarmsOccuredEvent event);
	
}
