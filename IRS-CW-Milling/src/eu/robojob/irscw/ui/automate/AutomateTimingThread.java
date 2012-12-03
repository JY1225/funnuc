package eu.robojob.irscw.ui.automate;

import org.apache.log4j.Logger;

import eu.robojob.irscw.process.ProcessFlowTimer;

public class AutomateTimingThread extends Thread {

	private ProcessFlowTimer processFlowTimer;
	private boolean running;
	private AutomatePresenter automatePresenter;
	
	private static final Logger logger = Logger.getLogger(AutomateTimingThread.class);
	
	public AutomateTimingThread(AutomatePresenter automatePresenter, ProcessFlowTimer processFlowTimer) {
		this.automatePresenter = automatePresenter;
		this.processFlowTimer = processFlowTimer;
		this.running = true;
	}
	
	@Override
	public void run() {
		try {
			while (running) {
				long cycleTime = processFlowTimer.getCycleTime();
				long cycleTimePassed = processFlowTimer.getTimeInCurrentCycle();
				long timeTillFinished = -1;
				long timeTillIntervention = -1;
				if ((cycleTime != -1) && (cycleTimePassed != -1)) {
					int amountFinished = processFlowTimer.getProcessFlow().getFinishedAmount();
					int totalAmount = processFlowTimer.getProcessFlow().getTotalAmount();
					timeTillFinished = (totalAmount-amountFinished) * cycleTime - cycleTimePassed;
					timeTillIntervention = processFlowTimer.getTimeTillNextIntervention();
				}
				automatePresenter.setTimers(toTimeString(cycleTime), toTimeString(cycleTimePassed), toTimeString(timeTillIntervention), toTimeString(timeTillFinished));
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					running = false;
				}
			}
		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
		}
		logger.info("Thread ended: " + toString());
	}
	
	private String toTimeString(long milliSeconds) {
		if (milliSeconds == -1) {
			return "--:--:--";
		} else {
			int hours = (int) ((milliSeconds / (1000*60*60)) % 24);
			String hoursStr = "";
			if (hours < 10) {
				hoursStr += "0";
			}
			hoursStr += hours;
			int minutes = (int) ((milliSeconds / (1000*60)) % 60);
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
			return hoursStr + ":" + minutesStr + ":" + secondsStr ;
		}
	}
	
	@Override
	public void interrupt() {
		this.running = false;
	}
}
