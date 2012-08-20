package eu.robojob.irscw.process;

public abstract class AbstractProcessStep implements Runnable {
	
	protected Process parentProcess;
	private boolean inProcess;
	
	public AbstractProcessStep(Process parentProcess) {
		this.parentProcess = parentProcess;
		inProcess = false;
	}
	
	public abstract void executeStep();
	
	public abstract AbstractProcessStep clone(Process parentProcess);
	
	public abstract String toString();
	
	public void run() {
		inProcess = true;
		executeStep();
		inProcess = false;
	}

	public boolean isInProcess() {
		return inProcess;
	}

	public void setInProcess(boolean inProcess) {
		this.inProcess = inProcess;
	}
	
	public Process getParentProcess() {
		return parentProcess;
	}

	public void setParentProcess(Process parentProcess) {
		this.parentProcess = parentProcess;
	}
	
}
