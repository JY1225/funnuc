package eu.robojob.millassist.process.execution;


public interface ProcessExecutor {

	boolean isRunning();
	void interrupt();
}
