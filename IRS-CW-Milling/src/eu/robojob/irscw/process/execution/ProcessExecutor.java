package eu.robojob.irscw.process.execution;


public interface ProcessExecutor {

	void stepExecutionFinished(int stepProcessId);
	void notifyException(Exception e);
	void notifyInterruptedException(InterruptedException e);
	boolean isRunning();
}
