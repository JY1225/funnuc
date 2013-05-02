package eu.robojob.irscw.external.robot.fanuc;

import eu.robojob.irscw.external.device.WorkArea;
import eu.robojob.irscw.external.robot.AbstractRobot;
import eu.robojob.irscw.external.robot.GripperHead;
import eu.robojob.irscw.external.robot.RobotPickSettings;
import eu.robojob.irscw.positioning.Coordinates;
import eu.robojob.irscw.workpiece.WorkPiece;

public class FanucRobotPickSettings extends RobotPickSettings {

	public FanucRobotPickSettings(final AbstractRobot robot, final WorkArea workArea, final GripperHead gripperHead, final Coordinates smoothPoint, final Coordinates location, final WorkPiece workPiece,
			final boolean doMachineAirblow) {
		super(robot, workArea, gripperHead, smoothPoint, location, workPiece, doMachineAirblow);
	}
	
	public FanucRobotPickSettings() {
		super(null, null, null, null, null, null, false);
	}
	
}