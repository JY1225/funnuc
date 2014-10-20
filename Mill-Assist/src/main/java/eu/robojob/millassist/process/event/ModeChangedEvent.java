package eu.robojob.millassist.process.event;

import eu.robojob.millassist.process.ProcessFlow;
import eu.robojob.millassist.process.ProcessFlow.Mode;

public class ModeChangedEvent extends ProcessFlowEvent {

	private Mode mode;
	
	public ModeChangedEvent(final ProcessFlow source, final Mode mode) {
		super(source, ProcessFlowEvent.MODE_CHANGED);
		this.mode = mode;
	}

	public Mode getMode() {
		return mode;
	}
	
	@Override
	public String toString() {
		return super.toString() + " - " + mode;
	}
}
