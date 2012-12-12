package eu.robojob.irscw.external.robot;

import eu.robojob.irscw.external.device.WorkArea;
import eu.robojob.irscw.positioning.Coordinates;
import eu.robojob.irscw.process.PickStep;
import eu.robojob.irscw.workpiece.WorkPiece;

public abstract class RobotPickSettings extends AbstractRobotActionSettings<PickStep> {
	
	protected WorkPiece workPiece;

	public RobotPickSettings(WorkArea workArea, GripperHead gripperHead, Coordinates smoothPoint, Coordinates location, WorkPiece workPiece) {
		super(workArea, gripperHead, smoothPoint, location);
		this.workPiece = workPiece;
	}

	public WorkPiece getWorkPiece() {
		return workPiece;
	}

	public void setWorkPiece(WorkPiece workPiece) {
		this.workPiece = workPiece;
	}
	
}