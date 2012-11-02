package eu.robojob.irscw.process.event;

import eu.robojob.irscw.process.ProcessFlow;

public class FinishedAmountChangedEvent extends ProcessFlowEvent {

	private int finishedAmount;
	private int totalAmount;
	
	public FinishedAmountChangedEvent(ProcessFlow source, int finishedAmount, int totalAmount) {
		super(source, ProcessFlowEvent.FINISHED_AMOUNT_CHANGED);
		this.finishedAmount = finishedAmount;
		this.totalAmount = totalAmount;
	}

	public int getFinishedAmount() {
		return finishedAmount;
	}

	public int getTotalAmount() {
		return totalAmount;
	}
}
