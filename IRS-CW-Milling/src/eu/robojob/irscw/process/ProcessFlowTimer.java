package eu.robojob.irscw.process;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.robojob.irscw.process.event.FinishedAmountChangedEvent;
import eu.robojob.irscw.process.event.ModeChangedEvent;
import eu.robojob.irscw.process.event.ProcessFlowEvent;
import eu.robojob.irscw.process.event.ProcessFlowListener;
import eu.robojob.irscw.process.event.StatusChangedEvent;

public class ProcessFlowTimer implements ProcessFlowListener {

	private Map<Integer, Long> startingTimeCurrentSteps;	// currently active steps (different workpiece ids) starting time
	private Map<Integer, Long> otherTimeCurrentSteps;		// other time used by currently active steps (different workpiece ids)
	private Map<AbstractProcessStep, Long> stepDurations;
	
	private ProcessFlow processFlow;
		
	private static Logger logger = LogManager.getLogger(ProcessFlowTimer.class.getName());
		
	public ProcessFlowTimer(final ProcessFlow processFlow) {
		this.processFlow = processFlow;
		processFlow.addListener(this);
		stepDurations = new HashMap<AbstractProcessStep, Long>();
		reset();
	}
	
	private void reset() {
		logger.debug("Timer of [" + processFlow + "] reset.");
		startingTimeCurrentSteps = new HashMap<Integer, Long>();
		otherTimeCurrentSteps = new HashMap<Integer, Long>();
	}
	
	@Override
	public void modeChanged(final ModeChangedEvent e) {
		switch (e.getMode()) {
			case STOPPED:		// Only this mode is of importance, because it indicates the execution of one or more steps could be interrupted.
				reset();
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
				startingTimeCurrentSteps.put(workPieceId, System.currentTimeMillis());
				break;
			case StatusChangedEvent.TEACHING_NEEDED :
				long otherTime = 0;
				if (otherTimeCurrentSteps.containsKey(workPieceId)) {
					otherTime = otherTimeCurrentSteps.get(workPieceId);
				}
				otherTime += (System.currentTimeMillis() - startingTimeCurrentSteps.get(workPieceId));
				startingTimeCurrentSteps.remove(workPieceId);
				otherTimeCurrentSteps.put(workPieceId, otherTime);
				break;
			case StatusChangedEvent.TEACHING_FINISHED :
				startingTimeCurrentSteps.put(workPieceId, System.currentTimeMillis());
				break;
			case StatusChangedEvent.ENDED :
				long totalTime = 0;
				if (otherTimeCurrentSteps.containsKey(workPieceId)) {
					totalTime = otherTimeCurrentSteps.get(workPieceId);
				}
				totalTime += (System.currentTimeMillis() - startingTimeCurrentSteps.get(workPieceId));
				startingTimeCurrentSteps.remove(workPieceId);
				otherTimeCurrentSteps.remove(workPieceId);
				stepDurations.put(e.getActiveStep(), totalTime);
				break;
			default:
				break;
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
	
	@Override
	public void dataChanged(final ProcessFlowEvent e) {
		//TODO reset needed here?
	}
	
	@Override
	public void finishedAmountChanged(final FinishedAmountChangedEvent e) {
	}

}
