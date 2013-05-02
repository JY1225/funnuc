package eu.robojob.millassist.process.event;

import eu.robojob.millassist.process.AbstractProcessStep;
import eu.robojob.millassist.process.ProcessFlow;

public class DataChangedEvent extends ProcessFlowEvent {

	private AbstractProcessStep step;
	private boolean reTeachingNeeded;
	
	public DataChangedEvent(final ProcessFlow source, final AbstractProcessStep step, final boolean reTeachingNeeded) {
		super(source, ProcessFlowEvent.DATA_CHANGED);
		this.step = step;
		this.reTeachingNeeded = reTeachingNeeded;
	}

	public AbstractProcessStep getStep() {
		return step;
	}

	public void setStep(final AbstractProcessStep step) {
		this.step = step;
	}

	public boolean isReTeachingNeeded() {
		return reTeachingNeeded;
	}

	public void setReTeachingNeeded(final boolean reTeachingNeeded) {
		this.reTeachingNeeded = reTeachingNeeded;
	}

}
