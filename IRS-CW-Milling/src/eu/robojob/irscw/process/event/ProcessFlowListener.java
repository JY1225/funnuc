package eu.robojob.irscw.process.event;


public interface ProcessFlowListener {

	public void modeChanged(ModeChangedEvent e);
	public void activeStepChanged(ActiveStepChangedEvent e);
	public void exceptionOccured(ExceptionOccuredEvent e);
	public void dataChanged(ProcessFlowEvent e);
	public void finishedAmountChanged(FinishedAmountChangedEvent e);
	
}
