package eu.robojob.millassist.process;

import eu.robojob.millassist.external.robot.AbstractRobot;
import eu.robojob.millassist.external.robot.AbstractRobotActionSettings;

public interface RobotStep {

	AbstractRobotActionSettings<?> getRobotSettings();
	AbstractRobot getRobot();
	
}
