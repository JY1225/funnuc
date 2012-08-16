package eu.robojob.irscw.process;

public abstract class AbstractProcessStep {
	
	protected Process parentProcess;
	
	public AbstractProcessStep(Process parentProcess) {
		this.parentProcess = parentProcess;
	}

	public abstract void executeStep();
	
	public abstract String toString();
}
