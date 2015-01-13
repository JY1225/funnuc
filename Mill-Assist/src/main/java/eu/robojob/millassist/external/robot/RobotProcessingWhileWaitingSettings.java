package eu.robojob.millassist.external.robot;

import eu.robojob.millassist.external.device.SimpleWorkArea;
import eu.robojob.millassist.process.ProcessingWhileWaitingStep;

public class RobotProcessingWhileWaitingSettings extends AbstractRobotActionSettings<ProcessingWhileWaitingStep> {

	public RobotProcessingWhileWaitingSettings(final AbstractRobot robot, final SimpleWorkArea workArea, final GripperHead gripperHead) {
		super(robot, workArea, gripperHead, null, null, false);
	}

}
