package eu.robojob.irscw.process.teach;

import eu.robojob.irscw.process.AbstractProcessStep;
import eu.robojob.irscw.process.AbstractTransportStep;
import eu.robojob.irscw.process.ProcessingStep;

public abstract class TeachObserver {

	protected ProcessFlowTeacher flowTeacher;
	
	public void setFlowTeacher(ProcessFlowTeacher flowTeacher) {
		this.flowTeacher = flowTeacher;
		flowTeacher.addObserver(this);
	}
	
	public abstract void finishedTransportStep(AbstractTransportStep transportStep);
	public abstract void finishedProcessingStep(ProcessingStep processingStep);
	
	public abstract void preparedForTeaching(AbstractTransportStep transportStep);
	
	public abstract void stepInProgress(AbstractProcessStep step);
}
