package eu.robojob.irscw.process;

import eu.robojob.irscw.external.robot.AbstractRobot;
import eu.robojob.irscw.external.robot.AbstractRobotActionSettings;

public interface RobotStep {

	AbstractRobotActionSettings<?> getRobotSettings();
	AbstractRobot getRobot();
	
}
