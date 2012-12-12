package eu.robojob.irscw.external.robot.fanuc;

import eu.robojob.irscw.external.device.WorkArea;
import eu.robojob.irscw.external.robot.GripperHead;
import eu.robojob.irscw.external.robot.RobotPickSettings;
import eu.robojob.irscw.positioning.Coordinates;
import eu.robojob.irscw.workpiece.WorkPiece;

public class FanucRobotPickSettings extends RobotPickSettings {

	protected boolean doMachineAirblow;

	public FanucRobotPickSettings(WorkArea workArea, GripperHead gripperHead, Coordinates smoothPoint, Coordinates location, WorkPiece workPiece) {
		super(workArea, gripperHead, smoothPoint, location, workPiece);
		this.doMachineAirblow = false;
	}
	
	public FanucRobotPickSettings() {
		super(null, null, null, null, null);
		this.doMachineAirblow = false;
	}
	
	public boolean isDoMachineAirblow() {
		return doMachineAirblow;
	}

	public void setDoMachineAirblow(boolean doMachineAirblow) {
		this.doMachineAirblow = doMachineAirblow;
	}
	
}