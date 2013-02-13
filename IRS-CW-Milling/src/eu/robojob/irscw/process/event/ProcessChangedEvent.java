package eu.robojob.irscw.process.event;

import eu.robojob.irscw.process.ProcessFlow;

public class ProcessChangedEvent extends DataChangedEvent {

	public ProcessChangedEvent(final ProcessFlow source) {
		super(source, null, true);
	}

}
