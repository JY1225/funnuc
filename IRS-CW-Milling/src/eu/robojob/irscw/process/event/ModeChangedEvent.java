package eu.robojob.irscw.process.event;

import eu.robojob.irscw.process.ProcessFlow;
import eu.robojob.irscw.process.ProcessFlow.Mode;

public class ModeChangedEvent extends ProcessFlowEvent {

	private Mode mode;
	
	public ModeChangedEvent(ProcessFlow source, Mode mode) {
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
