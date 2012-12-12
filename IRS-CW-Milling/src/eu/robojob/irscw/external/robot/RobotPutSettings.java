package eu.robojob.irscw.external.robot;

import eu.robojob.irscw.external.device.WorkArea;
import eu.robojob.irscw.positioning.Coordinates;
import eu.robojob.irscw.process.PutStep;

public abstract class RobotPutSettings extends AbstractRobotActionSettings<PutStep> {
		
	public RobotPutSettings(final WorkArea workArea, final GripperHead gripperHead, final Coordinates smoothPoint, final Coordinates location) {
		super(workArea, gripperHead, smoothPoint, location);
	}
}