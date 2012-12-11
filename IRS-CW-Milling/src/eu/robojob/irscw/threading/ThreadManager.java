package eu.robojob.irscw.threading;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import eu.robojob.irscw.external.communication.ExternalCommunicationThread;


public class ThreadManager {

	private ExecutorService executorService;
	private static final int amountOfThreads = 8;
	private static ThreadManager instance;
	
	private static final Logger logger = Logger.getLogger(ThreadManager.class);
	
	private Set<ExternalCommunicationThread> communicationThreads;
	private Set<MonitoringThread> monitoringThreads;
	
	private ThreadManager () {
		executorService = Executors.newFixedThreadPool(amountOfThreads);
		communicationThreads = new HashSet<ExternalCommunicationThread>();
		monitoringThreads = new HashSet<MonitoringThread>();
	}
	
	public static ThreadManager getInstance() {
		if (instance == null) {
			instance = new ThreadManager();
		}
		return instance;
	}

	public void submit(Thread thread) {
		logger.debug("New thread submitted: " + thread);
		if (thread instanceof ExternalCommunicationThread) {
			communicationThreads.add((ExternalCommunicationThread) thread);
		} else if (thread instanceof MonitoringThread) {
			monitoringThreads.add((MonitoringThread) thread);
		}
		executorService.submit(thread);
	}
	
	public void shutDown() {
		for (ExternalCommunicationThread thread : communicationThreads) {
			thread.disconnectAndStop();
		}
		for (MonitoringThread thread : monitoringThreads) {
			thread.stopExecution();
		}
		executorService.shutdownNow();
	}
	
	public void stopRunning(Thread thread) {
		//if (thread.isAlive()) {
			if (communicationThreads.contains(thread)) {
				ExternalCommunicationThread exThread = (ExternalCommunicationThread) thread;
				exThread.disconnectAndStop();
				communicationThreads.remove(thread);
			} else if (monitoringThreads.contains(thread)) {
				MonitoringThread mThread = (MonitoringThread) thread;
				mThread.stopExecution();
				monitoringThreads.remove(mThread);
			} else {
				logger.info("about to interrupt: " + thread);
				thread.interrupt();
			}
		//}
	}

}
