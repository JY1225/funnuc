package eu.robojob.millassist.external.robot;

import eu.robojob.millassist.external.device.WorkArea;
import eu.robojob.millassist.external.device.processing.cnc.AbstractCNCMachine;
import eu.robojob.millassist.positioning.Coordinates;
import eu.robojob.millassist.process.PickStep;
import eu.robojob.millassist.workpiece.WorkPiece;

public abstract class RobotPickSettings extends AbstractRobotActionSettings<PickStep> {
	
	private WorkPiece workPiece;
	private boolean doMachineAirblow;
	private ApproachType approachType;
	private boolean turnInMachine = false;

	public RobotPickSettings(final AbstractRobot robot, final WorkArea workArea, final GripperHead gripperHead, final Coordinates smoothPoint, final Coordinates location, 
			final WorkPiece workPiece, final boolean doMachineAirblow, final boolean gripInner) {
		super(robot, workArea, gripperHead, smoothPoint, location, gripInner);
		this.workPiece = workPiece;
		this.doMachineAirblow = doMachineAirblow;
		this.approachType = ApproachType.TOP;
	}

	public WorkPiece getWorkPiece() {
		return workPiece;
	}

	public void setWorkPiece(final WorkPiece workPiece) {
		this.workPiece = workPiece;
	}

	public boolean isDoMachineAirblow() {
		return doMachineAirblow;
	}

	public void setDoMachineAirblow(final boolean doMachineAirblow) {
		this.doMachineAirblow = doMachineAirblow;
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
	public boolean getTurnInMachine() {
		if (getStep().getDevice() instanceof AbstractCNCMachine) {
			return (((AbstractCNCMachine) getStep().getDevice()).getTIMAllowed() && this.turnInMachine);
		}
		return false;
	}
	
	public void setTurnInMachine(final boolean turnInMachine) {
		this.turnInMachine = turnInMachine;
	}
}