package eu.robojob.irscw.threading;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.robojob.irscw.external.communication.ExternalCommunicationThread;

public final class ThreadManager {

	private static final int MAX_THREAD_AMOUNT = 8;
	
	private static Logger logger = LogManager.getLogger(ThreadManager.class.getName());
	
	private static ExecutorService executorService = Executors.newFixedThreadPool(MAX_THREAD_AMOUNT);
	private static Set<ExternalCommunicationThread> communicationThreads = new HashSet<ExternalCommunicationThread>();
	private static Set<MonitoringThread> monitoringThreads = new HashSet<MonitoringThread>();
	
	private ThreadManager() {
	}
	
	public static Future<?> submit(final Thread thread) {
		logger.debug("New thread submitted: [" + thread + "].");
		if (thread instanceof ExternalCommunicationThread) {
			communicationThreads.add((ExternalCommunicationThread) thread);
		} else if (thread instanceof MonitoringThread) {
			monitoringThreads.add((MonitoringThread) thread);
		}
		return executorService.submit(thread);
	}
	
	public static void shutDown() {
		for (ExternalCommunicationThread thread : communicationThreads) {
			thread.disconnectAndStop();
		}
		for (MonitoringThread thread : monitoringThreads) {
			thread.stopExecution();
		}
		executorService.shutdownNow();
	}
	
	public static void stopRunning(final Thread thread) {
		logger.info("About to stop: [" + thread + "].");
		if (communicationThreads.contains(thread)) {
			ExternalCommunicationThread exThread = (ExternalCommunicationThread) thread;
			exThread.disconnectAndStop();
			communicationThreads.remove(thread);
		} else if (monitoringThreads.contains(thread)) {
			MonitoringThread mThread = (MonitoringThread) thread;
			mThread.stopExecution();
			monitoringThreads.remove(mThread);
		} else {
			thread.interrupt();
		}
	}

}
