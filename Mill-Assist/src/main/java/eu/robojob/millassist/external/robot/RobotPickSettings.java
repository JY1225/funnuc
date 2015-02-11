package eu.robojob.millassist.external.robot;

import eu.robojob.millassist.external.device.SimpleWorkArea;
import eu.robojob.millassist.external.device.processing.cnc.AbstractCNCMachine;
import eu.robojob.millassist.positioning.Coordinates;
import eu.robojob.millassist.process.PickStep;
import eu.robojob.millassist.workpiece.WorkPiece;

public abstract class RobotPickSettings extends AbstractRobotActionSettings<PickStep> {
	
	private WorkPiece workPiece;
	private boolean doRobotAirblow;
	private ApproachType approachType;
	private boolean turnInMachine = false;
	private boolean isTIMPick = false;

	public RobotPickSettings(final AbstractRobot robot, final SimpleWorkArea workArea, final GripperHead gripperHead, final Coordinates smoothPoint, final Coordinates location, 
			final WorkPiece workPiece, final boolean doRobotAirblow, final boolean gripInner) {
		super(robot, workArea, gripperHead, smoothPoint, location, gripInner);
		this.workPiece = workPiece;
		this.doRobotAirblow = doRobotAirblow;
		this.approachType = ApproachType.TOP;
	}

	public WorkPiece getWorkPiece() {
		return workPiece;
	}

	public void setWorkPiece(final WorkPiece workPiece) {
		this.workPiece = workPiece;
	}

	public boolean isRobotAirblow() {
		return doRobotAirblow;
	}

	public void setRobotAirblow(final boolean robotAirblow) {
		this.doRobotAirblow = robotAirblow;
	}
	
	public void updateWorkPieceType() {
		if (workPiece.getType().equals(WorkPiece.Type.HALF_FINISHED)) {
			workPiece.setType(WorkPiece.Type.FINISHED);
		}
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
	public boolean getTurnInMachineBeforePick() {
		return (getTurnInMachine() && this.isTIMPick);
	}
	
	/**
	 * Value that is set at-configure time in the CNC machine settings.
	 * @param turnInMachine
	 */
	public void setTurnInMachine(final boolean turnInMachine) {
		this.turnInMachine = turnInMachine;
	}
	
	public boolean getTurnInMachine() {
		if (getStep().getDevice() instanceof AbstractCNCMachine) {
			return (((AbstractCNCMachine) getStep().getDevice()).getTIMAllowed() && this.turnInMachine);
		}
		return false;
	}
	
	/**
	 * Can change at-runtime depending on the current step of the process.
	 * @param timPickAction
	 */
	public void setIsTIMPick(final boolean timPickAction) {
		this.isTIMPick = timPickAction;
	}
}