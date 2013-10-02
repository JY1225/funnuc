package eu.robojob.millassist.external.robot.fanuc;

import eu.robojob.millassist.external.device.WorkArea;
import eu.robojob.millassist.external.robot.AbstractRobot;
import eu.robojob.millassist.external.robot.GripperHead;
import eu.robojob.millassist.external.robot.RobotPickSettings;
import eu.robojob.millassist.positioning.Coordinates;
import eu.robojob.millassist.workpiece.WorkPiece;

public class FanucRobotPickSettings extends RobotPickSettings {

	public FanucRobotPickSettings(final AbstractRobot robot, final WorkArea workArea, final GripperHead gripperHead, final Coordinates smoothPoint, final Coordinates location, final WorkPiece workPiece,
			final boolean doMachineAirblow, final boolean gripInner) {
		super(robot, workArea, gripperHead, smoothPoint, location, workPiece, doMachineAirblow, gripInner);
	}
	
	public FanucRobotPickSettings() {
		super(null, null, null, null, null, null, false, false);
	}
	
}