package eu.robojob.irscw.external.robot.fanuc;

import eu.robojob.irscw.external.device.WorkArea;
import eu.robojob.irscw.external.robot.AbstractRobot;
import eu.robojob.irscw.external.robot.GripperHead;
import eu.robojob.irscw.external.robot.RobotPutSettings;
import eu.robojob.irscw.positioning.Coordinates;

public class FanucRobotPutSettings extends RobotPutSettings {

	private boolean doMachineAirblow;
	
	public FanucRobotPutSettings(final AbstractRobot robot, final WorkArea workArea, final GripperHead gripperHead, final Coordinates smoothPoint, final Coordinates location,
			final boolean doMachineAirblow) {
		super(robot, workArea, gripperHead, smoothPoint, location);
		this.doMachineAirblow = doMachineAirblow;
	}
	
	public boolean isDoMachineAirblow() {
		return doMachineAirblow;
	}

	public void setDoMachineAirblow(final boolean doMachineAirblow) {
		this.doMachineAirblow = doMachineAirblow;
	}

	public FanucRobotPutSettings() {
		super(null, null, null, null, null);
	}
}