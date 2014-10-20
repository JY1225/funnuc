package eu.robojob.millassist.external.robot;

import eu.robojob.millassist.util.Translator;

public class RobotActionException extends Exception {

	private static final long serialVersionUID = 1L;

	private static final String EXCEPTION_DURING_ROBOT_ACTION = "RobotActionException.exceptionDuringRobotAction";

	private AbstractRobot robot;
	private String errorId;
	
	public RobotActionException(final AbstractRobot robot, final String errorId) {
		this.errorId = errorId;
		this.robot = robot;
	}

	@Override
	public String getMessage() {
		return "Error during the executing of an action of: " + robot.getName() + ", " + super.getMessage();
	}
	
	@Override
	public String getLocalizedMessage() {
		return Translator.getTranslation(EXCEPTION_DURING_ROBOT_ACTION) + ": " + robot.getName() + ", " + Translator.getTranslation(errorId);
	}
}
