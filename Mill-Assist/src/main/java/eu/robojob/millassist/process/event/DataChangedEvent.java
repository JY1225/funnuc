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
		//indicates that the processFlow has changes since it was last saved. A reset of this flag will be done when saving the flow.
		source.setChangesSinceLastSave(true);
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
