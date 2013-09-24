package eu.robojob.millassist.external.robot.fanuc;

import eu.robojob.millassist.external.device.WorkArea;
import eu.robojob.millassist.external.robot.AbstractRobot;
import eu.robojob.millassist.external.robot.GripperHead;
import eu.robojob.millassist.external.robot.RobotPutSettings;
import eu.robojob.millassist.positioning.Coordinates;

public class FanucRobotPutSettings extends RobotPutSettings {
	
	public FanucRobotPutSettings(final AbstractRobot robot, final WorkArea workArea, final GripperHead gripperHead, final Coordinates smoothPoint, final Coordinates location,
			final boolean doMachineAirblow, final boolean releaseBeforeMachine) {
		super(robot, workArea, gripperHead, smoothPoint, location, doMachineAirblow, releaseBeforeMachine);
	}
	
	public FanucRobotPutSettings() {
		super(null, null, null, null, null, false, false);
	}
}