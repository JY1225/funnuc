package eu.robojob.irscw.process;

public class FixedAmountJob extends AbstractJob {
	
	private int workpieceAmount;
	
	public FixedAmountJob(Process process, int workpieceAmount) {
		super(process);
		this.workpieceAmount = workpieceAmount;
	}

	@Override
	public boolean hasNextProcess() {
		if (finishedWorkpiecesAmount < workpieceAmount) {
			return true;
		} else {
			return false;
		}
	}

}
