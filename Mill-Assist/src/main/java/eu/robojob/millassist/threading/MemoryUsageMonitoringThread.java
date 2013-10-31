package eu.robojob.millassist.threading;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MemoryUsageMonitoringThread extends Thread implements MonitoringThread {

	private static Logger logger = LogManager.getLogger(MemoryUsageMonitoringThread.class.getName());
	private static final int SLEEP_TIME = 5* 60 * 1000;
	
	private boolean alive = false;
	
	public MemoryUsageMonitoringThread() {
		this.alive = true;
	}
	
	@Override
	public void run() {
		try {
			while (alive) {
				try {
					logger.debug("Memory usage: " + (Runtime.getRuntime().totalMemory() -
							Runtime.getRuntime().freeMemory()));
					Thread.sleep(SLEEP_TIME);
				} catch (Exception e) {
					logger.error(e);
					alive = false;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			alive = false;
		}
		logger.info(MemoryUsageMonitoringThread.class.getSimpleName() + " ended...");
	}
	
	@Override
	public void interrupt() {
		alive = false;
	}

	@Override
	public void stopExecution() {
		alive = false;
	}
}
