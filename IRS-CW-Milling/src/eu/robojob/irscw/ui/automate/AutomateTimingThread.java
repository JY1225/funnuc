package eu.robojob.irscw.ui.automate;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.robojob.irscw.process.ProcessFlowTimer;

public class AutomateTimingThread extends Thread {

	private ProcessFlowTimer processFlowTimer;
	private boolean running;
	private AutomatePresenter automatePresenter;
	
	private static final int SLEEP_INTERVAL = 500;
	
	private static Logger logger = LogManager.getLogger(AutomateTimingThread.class.getName());
	
	public AutomateTimingThread(final AutomatePresenter automatePresenter, final ProcessFlowTimer processFlowTimer) {
		this.automatePresenter = automatePresenter;
		this.processFlowTimer = processFlowTimer;
		this.running = true;
	}
	
	@Override
	public void run() {
		logger.debug("Started execution.");
		try {
			while (running) {
				long timeInCurrentFlow = processFlowTimer.getProcessTimeMeasurement(automatePresenter.getMainProcessFlowId());
				automatePresenter.setTimers("?", toTimeString(timeInCurrentFlow), "?", "?");
				Thread.sleep(SLEEP_INTERVAL);
			}
		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
		} catch (Throwable t) {
			logger.error(t);
			t.printStackTrace();
		}
		logger.info(toString() + " ended...");
	}
	
	private String toTimeString(final long milliSeconds) {
		if (milliSeconds == -1) {
			return "--:--:--";
		} else {
			int hours = (int) ((milliSeconds / (1000 * 60 * 60)) % 24);
			String hoursStr = "";
			if (hours < 10) {
				hoursStr += "0";
			}
			hoursStr += hours;
			int minutes = (int) ((milliSeconds / (1000 * 60)) % 60);
			String minutesStr = "";
			if (minutes < 10) {
				minutesStr += "0";
			}
			minutesStr += minutes;
			int seconds = (int) (milliSeconds / 1000) % 60;
			String secondsStr = "";
			if (seconds < 10) {
				secondsStr += "0";
			}
			secondsStr += seconds;
			return hoursStr + ":" + minutesStr + ":" + secondsStr;
		}
	}
	
	@Override
	public void interrupt() {
		this.running = false;
	}
	
	@Override
	public String toString() {
		return "AutomateTimingThread";
	}
}
