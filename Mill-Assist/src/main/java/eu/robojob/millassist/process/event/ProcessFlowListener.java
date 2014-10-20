package eu.robojob.millassist.process.event;


public interface ProcessFlowListener {

	void modeChanged(ModeChangedEvent e);
	void statusChanged(StatusChangedEvent e);
	void dataChanged(DataChangedEvent e);
	void finishedAmountChanged(FinishedAmountChangedEvent e);
	void exceptionOccured(ExceptionOccuredEvent e);
	
	void unregister();
}
