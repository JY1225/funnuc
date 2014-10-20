package eu.robojob.millassist.external.robot;

import eu.robojob.millassist.external.device.WorkArea;
import eu.robojob.millassist.positioning.Coordinates;
import eu.robojob.millassist.process.PutStep;

public abstract class RobotPutSettings extends AbstractRobotActionSettings<PutStep> {
		
	private boolean doMachineAirblow;
	private boolean releaseBeforeMachine;
	private ApproachType approachType;

	public RobotPutSettings(final AbstractRobot robot, final WorkArea workArea, final GripperHead gripperHead, final Coordinates smoothPoint, final Coordinates location, final boolean doMachineAirblow, final boolean releaseBeforeMachine, final boolean gripInner) {
		super(robot, workArea, gripperHead, smoothPoint, location, gripInner);
		this.doMachineAirblow = doMachineAirblow;
		this.releaseBeforeMachine = releaseBeforeMachine;
		this.approachType = ApproachType.TOP;
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
	
	public ApproachType getApproachType() {
		return this.approachType;
	}
	
	public void setApproachType(ApproachType type) {
		this.approachType = type;
	}
}