package eu.robojob.irscw.external.robot;

import eu.robojob.irscw.external.device.WorkArea;
import eu.robojob.irscw.process.ProcessingWhileWaitingStep;

public class RobotProcessingWhileWaitingSettings extends AbstractRobotActionSettings<ProcessingWhileWaitingStep> {

	public RobotProcessingWhileWaitingSettings(final WorkArea workArea, final GripperHead gripperHead) {
		super(workArea, gripperHead, null, null);
	}

}
