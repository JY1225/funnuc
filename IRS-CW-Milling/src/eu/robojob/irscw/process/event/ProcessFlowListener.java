package eu.robojob.irscw.process.event;


public interface ProcessFlowListener {

	void modeChanged(ModeChangedEvent e);
	void statusChanged(StatusChangedEvent e);
	void dataChanged(ProcessFlowEvent e);
	void finishedAmountChanged(FinishedAmountChangedEvent e);
	void exceptionOccured(ExceptionOccuredEvent e);
	
}
