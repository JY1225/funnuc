package eu.robojob.millassist.external.robot;


public interface RobotListener {

	void robotConnected(RobotEvent event);
	void robotDisconnected(RobotEvent event);
		
	void robotStatusChanged(RobotEvent event);
	void robotZRestChanged(RobotEvent event);
	void robotAlarmsOccured(RobotAlarmsOccuredEvent event);
	void robotSpeedChanged(RobotEvent event);
	
	void unregister();
	
}
