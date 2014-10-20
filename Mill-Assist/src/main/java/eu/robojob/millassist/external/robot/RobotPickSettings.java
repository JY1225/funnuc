package eu.robojob.millassist.external.robot;

import eu.robojob.millassist.external.device.WorkArea;
import eu.robojob.millassist.positioning.Coordinates;
import eu.robojob.millassist.process.PickStep;
import eu.robojob.millassist.workpiece.WorkPiece;

public abstract class RobotPickSettings extends AbstractRobotActionSettings<PickStep> {
	
	private WorkPiece workPiece;
	private boolean doMachineAirblow;
	private ApproachType approachType;

	public RobotPickSettings(final AbstractRobot robot, final WorkArea workArea, final GripperHead gripperHead, final Coordinates smoothPoint, final Coordinates location, final WorkPiece workPiece, final boolean doMachineAirblow, final boolean gripInner) {
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
}