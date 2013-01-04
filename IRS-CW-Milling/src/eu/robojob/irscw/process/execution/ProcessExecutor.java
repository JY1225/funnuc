package eu.robojob.irscw.process.execution;

public interface ProcessExecutor {

	void notifyException(Exception e);
	void notifyThrowable(Throwable t);
}
