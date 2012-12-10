package eu.robojob.irscw.external.robot;

import eu.robojob.irscw.external.communication.AbstractCommunicationException;

public class FanucRobotDisconnectedException extends AbstractCommunicationException {

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

	@Override
	public String getLocalizedMessage() {
		// TODO Auto-generated method stub
		return null;
	}

}
