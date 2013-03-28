package eu.robojob.irscw.process.execution;


public interface ProcessExecutor {

	boolean isRunning();
	void interrupt();
}
