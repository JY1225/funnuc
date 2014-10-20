package eu.robojob.millassist.process.event;

import eu.robojob.millassist.process.ProcessFlow;

public class ExceptionOccuredEvent extends ProcessFlowEvent {

	private Exception e;
	
	public ExceptionOccuredEvent(final ProcessFlow source, final Exception e) {
		super(source, ProcessFlowEvent.EXCEPTION_OCCURED);
		this.e = e;
	}
	
	public Exception getException() {
		return e;
	}

}
