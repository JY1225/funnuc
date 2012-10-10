package eu.robojob.irscw.threading;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import eu.robojob.irscw.external.communication.ExternalCommunicationThread;


public class ThreadManager {

	private ExecutorService executorService;
	private static final int amountOfThreads = 5;
	private static ThreadManager instance;
	
	private static final Logger logger = Logger.getLogger(ThreadManager.class);
	
	private Set<ExternalCommunicationThread> communicationThreads;
	
	private ThreadManager () {
		executorService = Executors.newFixedThreadPool(amountOfThreads);
		communicationThreads = new HashSet<ExternalCommunicationThread>();
	}
	
	public static ThreadManager getInstance() {
		if (instance == null) {
			instance = new ThreadManager();
		}
		return instance;
	}

	public void submit(Runnable runnable) {
		logger.debug("New thread submitted");
		if (runnable instanceof ExternalCommunicationThread) {
			communicationThreads.add((ExternalCommunicationThread) runnable);
		}
		executorService.submit(runnable);
	}
	
	public void shutDown() {
		for (ExternalCommunicationThread thread : communicationThreads) {
			thread.disconnectAndStop();
		}
		executorService.shutdownNow();
	}

}
