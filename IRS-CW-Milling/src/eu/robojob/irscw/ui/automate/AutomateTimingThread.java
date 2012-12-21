package eu.robojob.irscw.ui.automate;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.robojob.irscw.process.ProcessFlowTimer;

public class AutomateTimingThread extends Thread {

	private ProcessFlowTimer processFlowTimer;
	private boolean running;
	private AutomatePresenter automatePresenter;
	
	private static Logger logger = LogManager.getLogger(AutomateTimingThread.class.getName());
	
	public AutomateTimingThread(final AutomatePresenter automatePresenter, final ProcessFlowTimer processFlowTimer) {
		this.automatePresenter = automatePresenter;
		this.processFlowTimer = processFlowTimer;
		this.running = true;
	}
	
	@Override
	public void run() {
		//FIXME implement
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
}
