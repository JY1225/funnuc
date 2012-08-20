package eu.robojob.irscw.process;

public class ProcessStepExecution implements Runnable {

	private AbstractProcessStep step;
	private boolean finished;
	
	public ProcessStepExecution(AbstractProcessStep step) {
		this.step = step;
		finished = false;
	}
	
	@Override
	public void run() {
		step.executeStep();
		finished = true;
	}
	
	public boolean hasFinished() {
		return finished;
	}

}
