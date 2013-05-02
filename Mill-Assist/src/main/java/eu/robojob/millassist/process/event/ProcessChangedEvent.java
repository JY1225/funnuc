package eu.robojob.millassist.process.event;

import eu.robojob.millassist.process.ProcessFlow;

public class ProcessChangedEvent extends DataChangedEvent {

	public ProcessChangedEvent(final ProcessFlow source) {
		super(source, null, true);
	}

}
