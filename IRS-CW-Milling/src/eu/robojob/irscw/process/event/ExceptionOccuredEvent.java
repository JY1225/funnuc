package eu.robojob.irscw.process.event;

import eu.robojob.irscw.process.ProcessFlow;

public class ExceptionOccuredEvent extends ProcessFlowEvent {

	private Exception e;
	
	public ExceptionOccuredEvent(ProcessFlow source, Exception e) {
		super(source, ProcessFlowEvent.EXCEPTION_OCCURED);
		this.e = e;
	}

	public Exception getE() {
		return e;
	}
}
