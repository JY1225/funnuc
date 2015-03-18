package eu.robojob.millassist.process.event;

import eu.robojob.millassist.process.AbstractProcessStep;
import eu.robojob.millassist.process.ProcessFlow;

public class DimensionsChangedEvent extends DataChangedEvent {

	public DimensionsChangedEvent(ProcessFlow source, AbstractProcessStep step, boolean reTeachingNeeded) {
		super(source, step, reTeachingNeeded);
	}

}
