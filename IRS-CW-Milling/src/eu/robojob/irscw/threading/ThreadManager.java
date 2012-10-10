package eu.robojob.irscw.threading;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;


public class ThreadManager {

	private ExecutorService executorService;
	private static final int amountOfThreads = 5;
	private static ThreadManager instance;
	
	private static final Logger logger = Logger.getLogger(ThreadManager.class);
	
	private ThreadManager () {
		executorService = Executors.newFixedThreadPool(amountOfThreads);
	}
	
	public static ThreadManager getInstance() {
		if (instance == null) {
			instance = new ThreadManager();
		}
		return instance;
	}

	public void submit(Runnable runnable) {
		logger.debug("New thread submitted");
		executorService.submit(runnable);
	}
	
	public void shutDown() {
		executorService.shutdownNow();
	}

}
