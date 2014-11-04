package eu.robojob.millassist.external.robot;

import eu.robojob.millassist.external.device.WorkArea;
import eu.robojob.millassist.external.device.processing.cnc.AbstractCNCMachine;
import eu.robojob.millassist.positioning.Coordinates;
import eu.robojob.millassist.process.PutStep;

public abstract class RobotPutSettings extends AbstractRobotActionSettings<PutStep> {
		
	private boolean doMachineAirblow;
	private boolean releaseBeforeMachine;
	private ApproachType approachType;
	private boolean turnInMachine = false;
	private boolean isTIMPut = false;

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
	
	/**
	 * Check whether turn in machine is allowed.
	 * 
	 * @return In case the device from which we need to pick is a CNC machine, we check the value
	 * of turnInMachine allowed from the machine together with the option chosen at the CNC config. 
	 * Otherwise the result is always false.
	 */
	public boolean getTurnInMachineBeforePut() {
		return (getTurnInMachine() && this.isTIMPut);
	}
	
	public boolean getTurnInMachine() {
		if (getStep().getDevice() instanceof AbstractCNCMachine) {
			return (((AbstractCNCMachine) getStep().getDevice()).getTIMAllowed() && this.turnInMachine);
		}
		return false;
	}
	
	public void setTurnInMachine(final boolean turnInMachine) {
		this.turnInMachine = turnInMachine;
	}
	
	/**
	 * Can change at-runtime depending on the current step of the process.
	 * @param timPickAction
	 */
	public void setIsTIMPut(final boolean timPutAction) {
		this.isTIMPut = timPutAction;
	}
}