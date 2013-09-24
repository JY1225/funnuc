package eu.robojob.millassist.external.robot;

import eu.robojob.millassist.external.device.WorkArea;
import eu.robojob.millassist.positioning.Coordinates;
import eu.robojob.millassist.process.PutStep;

public abstract class RobotPutSettings extends AbstractRobotActionSettings<PutStep> {
		
	private boolean doMachineAirblow;
	private boolean releaseBeforeMachine;

	public RobotPutSettings(final AbstractRobot robot, final WorkArea workArea, final GripperHead gripperHead, final Coordinates smoothPoint, final Coordinates location, final boolean doMachineAirblow, final boolean releaseBeforeMachine) {
		super(robot, workArea, gripperHead, smoothPoint, location);
		this.doMachineAirblow = doMachineAirblow;
		this.releaseBeforeMachine = releaseBeforeMachine;
	}
	
	public boolean isDoMachineAirblow() {
		return doMachineAirblow;
	}

	public void setDoMachineAirblow(final boolean doMachineAirblow) {
		this.doMachineAirblow = doMachineAirblow;
	}

	public boolean isReleaseBeforeMachine() {
		return releaseBeforeMachine;
	}

	public void setReleaseBeforeMachine(final boolean releaseBeforeMachine) {
		this.releaseBeforeMachine = releaseBeforeMachine;
	}
}