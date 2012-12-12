package eu.robojob.irscw.external.robot;

import eu.robojob.irscw.util.Translator;

public class RobotActionException extends Exception {

	private static final long serialVersionUID = 1L;

	private static final String EXCEPTION_DURING_ROBOT_ACTION = "RobotActionException.exceptionDuringRobotAction";

	private AbstractRobot robot;
	private String errorId;
	private Translator translator;
	
	public RobotActionException(final AbstractRobot robot, final String errorId) {
		this.errorId = errorId;
		this.robot = robot;
		this.translator = Translator.getInstance();
	}

	@Override
	public String getMessage() {
		return "Error during the executing of an action of: " + robot.getId() + ", " + super.getMessage();
	}
	
	@Override
	public String getLocalizedMessage() {
		return translator.getTranslation(EXCEPTION_DURING_ROBOT_ACTION) + ": " + robot.getId() + ", " + translator.getTranslation(errorId);
	}
}
