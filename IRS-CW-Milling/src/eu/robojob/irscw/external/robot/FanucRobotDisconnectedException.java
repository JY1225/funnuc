package eu.robojob.irscw.external.robot;

import eu.robojob.irscw.external.communication.CommunicationException;

public class FanucRobotDisconnectedException extends CommunicationException {

	private static final long serialVersionUID = 1L;
	private FanucRobot robot;
	
	public FanucRobotDisconnectedException(FanucRobot robot) {
		this.robot = robot;
	}

	public FanucRobot getRobot() {
		return robot;
	}
	
	@Override
	public String getMessage() {
		return "Geen verbinding meer met " + robot.getId();
	}

}
