package eu.robojob.millassist.external.robot.fanuc;

import eu.robojob.millassist.external.device.SimpleWorkArea;
import eu.robojob.millassist.external.robot.AbstractRobot;
import eu.robojob.millassist.external.robot.GripperHead;
import eu.robojob.millassist.external.robot.RobotPutSettings;
import eu.robojob.millassist.positioning.Coordinates;

public class FanucRobotPutSettings extends RobotPutSettings {
	
	public FanucRobotPutSettings(final AbstractRobot robot, final SimpleWorkArea workArea, final GripperHead gripperHead, final Coordinates smoothPoint, final Coordinates location,
			final boolean doRobotAirblow, final boolean releaseBeforeMachine, final boolean gripInner) {
		super(robot, workArea, gripperHead, smoothPoint, location, doRobotAirblow, releaseBeforeMachine, gripInner);
	}
	
	public FanucRobotPutSettings() {
		super(null, null, null, null, null, false, false, false);
	}
}