package eu.robojob.irscw.threading;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class ThreadManager {

	private ExecutorService executorService;
	private static final int amountOfThreads = 5;
	private static ThreadManager instance;
	
	private ThreadManager () {
		executorService = Executors.newFixedThreadPool(5);
	}
	
	public static ThreadManager getInstance() {
		if (instance == null) {
			instance = new ThreadManager();
		}
		return instance;
	}

	public void submit(Runnable runnable) {
		executorService.submit(runnable);
	}
	
	public void shutDown() {
		executorService.shutdownNow();
	}

}
