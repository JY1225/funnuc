package eu.robojob.irscw.external.robot.fanuc;

import eu.robojob.irscw.external.device.WorkArea;
import eu.robojob.irscw.external.robot.GripperHead;
import eu.robojob.irscw.external.robot.RobotPutSettings;
import eu.robojob.irscw.positioning.Coordinates;

public class FanucRobotPutSettings extends RobotPutSettings {

	protected boolean doMachineAirblow;
	
	public FanucRobotPutSettings(WorkArea workArea, GripperHead gripperHead, Coordinates smoothPoint, Coordinates location) {
		super(workArea, gripperHead, smoothPoint, location);
		this.doMachineAirblow = false;
	}
	
	public boolean isDoMachineAirblow() {
		return doMachineAirblow;
	}

	public void setDoMachineAirblow(boolean doMachineAirblow) {
		this.doMachineAirblow = doMachineAirblow;
	}

	public FanucRobotPutSettings() {
		super(null, null, null, null);
	}
}