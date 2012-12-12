package eu.robojob.irscw.external.robot;

import eu.robojob.irscw.external.device.WorkArea;
import eu.robojob.irscw.positioning.Coordinates;
import eu.robojob.irscw.process.PutStep;

public abstract class RobotPutSettings extends AbstractRobotActionSettings<PutStep> {
	
	protected PutStep putStep;
	
	public RobotPutSettings(WorkArea workArea, GripperHead gripperHead, Coordinates smoothPoint, Coordinates location) {
		super(workArea, gripperHead, smoothPoint, location);
	}

	public PutStep getPutStep() {
		return putStep;
	}

	public void setPutStep(PutStep putStep) {
		this.putStep = putStep;
	}
	
}