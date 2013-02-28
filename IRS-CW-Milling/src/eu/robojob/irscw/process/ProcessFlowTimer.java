package eu.robojob.irscw.process;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.robojob.irscw.process.event.DataChangedEvent;
import eu.robojob.irscw.process.event.ExceptionOccuredEvent;
import eu.robojob.irscw.process.event.FinishedAmountChangedEvent;
import eu.robojob.irscw.process.event.ModeChangedEvent;
import eu.robojob.irscw.process.event.ProcessFlowListener;
import eu.robojob.irscw.process.event.StatusChangedEvent;

public class ProcessFlowTimer implements ProcessFlowListener {

	private Map<Integer, Long> startingTimeCurrentSteps;	// currently active steps (different workpiece ids) starting time
	private Map<Integer, Long> otherTimeCurrentSteps;		// other time used by currently active steps (different workpiece ids)
	private Map<Integer, Long> startingTimePauseAfterSteps;
	private Map<Integer, Long> startingTimeCurrentProcessFlow;
	private Map<Integer, Long> otherTimeCurrentProcessFlow;
	private Map<AbstractProcessStep, Long> stepDurations;
	private Map<AbstractProcessStep, Long> waitingTimeAfterStepDurations;
	private Map<Integer, AbstractProcessStep> lastActiveSteps;
	private ProcessFlow processFlow;
	private boolean isPaused;
	private long timeWon;
	
	private static Logger logger = LogManager.getLogger(ProcessFlowTimer.class.getName());
		
	public ProcessFlowTimer(final ProcessFlow processFlow) {
		this.processFlow = processFlow;
		processFlow.addListener(this);
		stepDurations = new HashMap<AbstractProcessStep, Long>();
		waitingTimeAfterStepDurations = new HashMap<AbstractProcessStep, Long>();
		timeWon = 0;
		reset();
	}
	
	private void reset() {
		logger.debug("Timer of [" + processFlow + "] reset.");
		startingTimeCurrentSteps = new HashMap<Integer, Long>();
		startingTimePauseAfterSteps = new HashMap<Integer, Long>();
		otherTimeCurrentSteps = new HashMap<Integer, Long>();
		lastActiveSteps = new HashMap<Integer, AbstractProcessStep>();
		startingTimeCurrentProcessFlow = new HashMap<Integer, Long>();
		otherTimeCurrentProcessFlow = new HashMap<Integer, Long>();
	}
	
	public long getProcessFlowDuration() {
		if (stepDurations.keySet().containsAll(processFlow.getProcessSteps())) {
			long duration = 0;
			for (long time : stepDurations.values()) {
				duration = duration + time;
			}
			for (long time : waitingTimeAfterStepDurations.values()) {
				duration = duration + time;
			}
			return duration;
		} else {
			return -1;
		}
	}
	
	public long getRemainingTime(final int currentMainWorkPieceId) {
		long processFlowDuration = getProcessFlowDuration();
		if (processFlowDuration != -1) {
			long remainingTime = 0;
			int remainingAmount = processFlow.getTotalAmount() - processFlow.getFinishedAmount();
			if (remainingAmount > 1) {
				remainingTime = (remainingAmount - 1) * (processFlowDuration - getTimeWon());
			}
			if (remainingAmount > 0) {
				remainingTime += processFlowDuration - getProcessTimeMeasurement(currentMainWorkPieceId);
			}
			return remainingTime;
		}
		return -1;
	}
	
	@Override
	public void modeChanged(final ModeChangedEvent e) {
		switch (e.getMode()) {
			case STOPPED:
				reset();
				break;
			case READY:
			case PAUSED:
				pauseTimeMeasurements();
				isPaused = true;
				break;
			case AUTO:
				if (isPaused) {
					continueTimeMeasurements();
				}
				break;
			default:
				break;
		}
	}

	@Override
	public void statusChanged(final StatusChangedEvent e) {
		int workPieceId = e.getWorkPieceId();
		switch (e.getStatusId()) {
			case StatusChangedEvent.STARTED :
				// if not the first, stop pause time measurement after previous step
				if (!e.getActiveStep().equals(processFlow.getProcessSteps().get(0))) {
					stopPauseTimeMeasurement(workPieceId);
				} else {
					startProcessTimeMeasurement(workPieceId);
				}
				startStepTimeMeasurement(workPieceId, e.getActiveStep());
				break;
			case StatusChangedEvent.TEACHING_NEEDED :
				pauseTimeMeasurements();
				break;
			case StatusChangedEvent.TEACHING_FINISHED :
				continueTimeMeasurements();
				break;
			case StatusChangedEvent.ENDED :
				stopStepTimeMeasurement(workPieceId, e.getActiveStep());
				if (!e.getActiveStep().equals(processFlow.getProcessSteps().get(processFlow.getProcessSteps().size() - 1))) {
					// pause only possible if finished step is not the last
					startPauseTimeMeasurement(workPieceId, e.getActiveStep());
				} else {
					stopProcessTimeMeasurement(workPieceId);
					// use earliest starting time as time won for next process
					long minStartTime = -1;
					int nextWorkpieceIndex = -1;
					for (Entry<Integer, Long> entry : startingTimeCurrentProcessFlow.entrySet()) {
						if ((minStartTime == -1) || (entry.getValue() < minStartTime)) {
							minStartTime = entry.getValue();
							nextWorkpieceIndex = entry.getKey();
						}
					}
					if (nextWorkpieceIndex != -1) {
						timeWon = getProcessTimeMeasurement(nextWorkpieceIndex);
						logger.debug("-- TIME WON: " + timeWon);
					}
				}
				break;
			default:
				break;
		}
	}
	
	private void startProcessTimeMeasurement(final int workPieceId) {
		otherTimeCurrentProcessFlow.remove(workPieceId);
		startingTimeCurrentProcessFlow.put(workPieceId, System.currentTimeMillis());
	}
	
	private void stopProcessTimeMeasurement(final int workPieceId) {
		startingTimeCurrentProcessFlow.remove(workPieceId);
		otherTimeCurrentProcessFlow.remove(workPieceId);
	}
	
	public long getTimeWon() {
		return timeWon;
	}
	
	public long getProcessTimeMeasurement(final int workPieceId) {
		long totalTime = 0;
		if (otherTimeCurrentProcessFlow.containsKey(workPieceId)) {
			totalTime = otherTimeCurrentProcessFlow.get(workPieceId);
		}
		if (startingTimeCurrentProcessFlow.containsKey(workPieceId)) {
			totalTime += (System.currentTimeMillis() - startingTimeCurrentProcessFlow.get(workPieceId));
		}
		return totalTime;
	}
	
	private void startStepTimeMeasurement(final int workPieceId, final AbstractProcessStep step) {
		startingTimeCurrentSteps.put(workPieceId, System.currentTimeMillis());
	}
	
	private void stopStepTimeMeasurement(final int workPieceId, final AbstractProcessStep step) {
		long totalTime = 0;
		if (otherTimeCurrentSteps.containsKey(workPieceId)) {
			totalTime = otherTimeCurrentSteps.get(workPieceId);
			otherTimeCurrentSteps.remove(workPieceId);
		}
		if ((startingTimeCurrentSteps != null) && (startingTimeCurrentSteps.get(workPieceId) != null)) {
			totalTime += (System.currentTimeMillis() - startingTimeCurrentSteps.get(workPieceId));
			logger.debug("TIME STEP [" + step + "]: " + totalTime);
			startingTimeCurrentSteps.remove(workPieceId);
		}
		stepDurations.put(step, totalTime);
	}
	
	private void startPauseTimeMeasurement(final int workPieceId, final AbstractProcessStep step) {
		lastActiveSteps.put(workPieceId, step);
		startingTimePauseAfterSteps.put(workPieceId, System.currentTimeMillis());
	}
	
	private void stopPauseTimeMeasurement(final int workPieceId) {
		if (startingTimePauseAfterSteps.containsKey(workPieceId) && lastActiveSteps.containsKey(workPieceId)) {
			waitingTimeAfterStepDurations.put(lastActiveSteps.get(workPieceId), System.currentTimeMillis() - startingTimePauseAfterSteps.get(workPieceId));
			startingTimePauseAfterSteps.remove(workPieceId);
		}
	}
	
	private void pauseTimeMeasurements() {
		// handle pause for step timing
		for (int workPieceId : startingTimeCurrentSteps.keySet()) {
			long otherTime = 0;
			if (otherTimeCurrentSteps.containsKey(workPieceId)) {
				otherTime = otherTimeCurrentSteps.get(workPieceId);
			}
			otherTime += (System.currentTimeMillis() - startingTimeCurrentSteps.get(workPieceId));
			startingTimeCurrentSteps.remove(workPieceId);
			otherTimeCurrentSteps.put(workPieceId, otherTime);
		}
		// handle pause for processFlow timing
		for (int workPieceId : startingTimeCurrentProcessFlow.keySet()) {
			long otherTime = 0;
			if (otherTimeCurrentProcessFlow.containsKey(workPieceId)) {
				otherTime = otherTimeCurrentProcessFlow.get(workPieceId);
			}
			otherTime += (System.currentTimeMillis() - startingTimeCurrentProcessFlow.get(workPieceId));
			startingTimeCurrentProcessFlow.remove(workPieceId);
			otherTimeCurrentProcessFlow.put(workPieceId, otherTime);
		}
	}
	
	private void continueTimeMeasurements() {
		for (int workPieceId : otherTimeCurrentSteps.keySet()) {
			startingTimeCurrentSteps.put(workPieceId, System.currentTimeMillis());
		}
		for (int workPieceId : otherTimeCurrentProcessFlow.keySet()) {
			startingTimeCurrentProcessFlow.put(workPieceId, System.currentTimeMillis());
		}
	}
	
	public long getStepTime(final AbstractProcessStep step) {
		if (stepDurations.containsKey(step)) {
			return stepDurations.get(step);
		}
		return -1;
	}
	
	public long getTimeInCurrentStep(final int workPieceId) {
		if (!(otherTimeCurrentSteps.containsKey(workPieceId) || startingTimeCurrentSteps.containsKey(workPieceId))) {
			return -1;
		}
		long time = 0;
		if (otherTimeCurrentSteps.containsKey(workPieceId)) {
			time = otherTimeCurrentSteps.get(workPieceId);
		}
		if (startingTimeCurrentSteps.containsKey(workPieceId)) {
			time += System.currentTimeMillis() - startingTimeCurrentSteps.get(workPieceId);
		}
		return time;
	}
	
	public ProcessFlow getProcessFlow() {
		return processFlow;
	}
	
	@Override public void dataChanged(final DataChangedEvent e) { }
	@Override public void finishedAmountChanged(final FinishedAmountChangedEvent e) { }
	@Override public void exceptionOccured(final ExceptionOccuredEvent e) {	 }

}
