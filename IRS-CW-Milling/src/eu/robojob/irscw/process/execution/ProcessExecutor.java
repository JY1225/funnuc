package eu.robojob.irscw.process.execution;

public interface ProcessExecutor {

	void stepExecutionFinished(int stepProcessId);
	void notifyException(Exception e);
	void notifyThrowable(Throwable t);
}
