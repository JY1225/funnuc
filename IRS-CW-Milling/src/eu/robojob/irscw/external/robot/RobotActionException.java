package eu.robojob.irscw.external.robot;

public class RobotActionException extends Exception {

	private static final long serialVersionUID = 1L;

	@Override
	public String getMessage() {
		return "Fout tijdens uitvoeren actie van robot.";
	}
}
