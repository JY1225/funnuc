package eu.robojob.millassist.process.event;

import eu.robojob.millassist.process.ProcessFlow;

public class FinishedAmountChangedEvent extends ProcessFlowEvent {

	private int finishedAmount;
	private int totalAmount;
	
	public FinishedAmountChangedEvent(final ProcessFlow source, final int finishedAmount, final int totalAmount) {
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
