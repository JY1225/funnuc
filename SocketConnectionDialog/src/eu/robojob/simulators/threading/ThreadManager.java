package eu.robojob.simulators.threading;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

public class ThreadManager {

	private ExecutorService executorService;
	private static final int amountOfThreads = 5;
	private static ThreadManager instance;
	
	private List<ListeningThread> ioThreads;
	
	private static Logger logger = Logger.getLogger(ThreadManager.class);
	
	private ThreadManager () {
		executorService = Executors.newFixedThreadPool(amountOfThreads);
		ioThreads = new ArrayList<ListeningThread>();
	}
	
	public static ThreadManager getInstance() {
		if (instance == null) {
			instance = new ThreadManager();
		}
		return instance;
	}

	public void submit(Runnable runnable) {
		if (runnable instanceof ListeningThread) {
			logger.info("added thread");
			ioThreads.add((ListeningThread) runnable);
		}
		executorService.submit(runnable);
	}
	
	public void shutDown() {
		logger.debug("about to shutdown");
		for(ListeningThread thread : ioThreads) {
			logger.debug("closing thread");
			thread.closeConnection();
		}
		executorService.shutdownNow();
	}

}
