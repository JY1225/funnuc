package eu.robojob.irscw.process;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import eu.robojob.irscw.process.event.ActiveStepChangedEvent;
import eu.robojob.irscw.process.event.ExceptionOccuredEvent;
import eu.robojob.irscw.process.event.FinishedAmountChangedEvent;
import eu.robojob.irscw.process.event.ModeChangedEvent;
import eu.robojob.irscw.process.event.ProcessFlowEvent;
import eu.robojob.irscw.process.event.ProcessFlowListener;

public class ProcessFlowTimer implements ProcessFlowListener {

	private long startingTimeCurrentStep;
	private long otherTimeCurrentStep;

	private Map<AbstractProcessStep, Long> stepDurations;
	
	private ProcessFlow processFlow;
	
	private AbstractProcessStep currentStep;
	
	private static final Logger logger = Logger.getLogger(ProcessFlowTimer.class);
	
	enum Mode {
		RUNNING, PAUSED, STOPPED
	}
	
	private Mode mode;
	
	public ProcessFlowTimer(ProcessFlow processFlow) {
		this.processFlow = processFlow;
		processFlow.addListener(this);
		stepDurations = new HashMap<AbstractProcessStep, Long>();
		reset();
	}
	
	@Override
	public void modeChanged(ModeChangedEvent e) {
		switch (e.getMode()) {
			case AUTO:
				setMode(Mode.RUNNING);
				break;
			case FINISHED:
				setMode(Mode.STOPPED);
				break;
			case PAUSED:
				setMode(Mode.PAUSED);
				break;
			case STOPPED:
				reset();
				setMode(Mode.STOPPED);
				break;
			case TEACH:
				setMode(Mode.RUNNING);
				break;
			case READY:
				setMode(Mode.STOPPED);
				break;
			default:
				setMode(Mode.STOPPED);
				break;
		}
	}
	
	private void reset() {
		logger.info("reset!");
		startingTimeCurrentStep = -1;
		otherTimeCurrentStep = 0;
		currentStep = null;
	}

	@Override
	public void activeStepChanged(ActiveStepChangedEvent e) {
		logger.info("active step changed: " + e.getId() + " - " + e.getActiveStep());
		switch(e.getStatusId()) {
			case ActiveStepChangedEvent.NONE_ACTIVE:
				currentStepEnded();
				currentStep = null;
				break;
			case ActiveStepChangedEvent.TEACHING_NEEDED:
				setMode(Mode.PAUSED);
				break;
			case ActiveStepChangedEvent.TEACHING_FINISHED:
				setMode(Mode.RUNNING);
				break;
			default:
				if (!e.getActiveStep().equals(currentStep)) {
					if (mode == Mode.PAUSED) {
						logger.error("FOUT 1!");
						throw new IllegalStateException("Paused and yet the active step changes?");
					}
					currentStep = e.getActiveStep();
					currentStepEnded();
					startingTimeCurrentStep = System.currentTimeMillis();
				} else {
					logger.error("FOUT 2!");
				}
				break;
		}
	}
	
	private void setMode(Mode mode) {
		logger.info("set mode to: " + mode +  ", previous mode: " + this.mode);
		if ((this.mode != null) && this.mode != mode) {
			if ((this.mode == Mode.RUNNING) && (mode == Mode.PAUSED)) {
				if (startingTimeCurrentStep != -1) {
					otherTimeCurrentStep += System.currentTimeMillis() - startingTimeCurrentStep;
					startingTimeCurrentStep = -1;
				}
			} else if (((this.mode == Mode.PAUSED) && (mode == Mode.RUNNING)) || ((this.mode == Mode.STOPPED) && (mode == Mode.RUNNING))){
				logger.info("set starting time current step!");
				startingTimeCurrentStep = System.currentTimeMillis();
			} else if (mode == Mode.STOPPED) {
				otherTimeCurrentStep = 0;
				startingTimeCurrentStep = -1;
			}
		} else {
			if ((this.mode == null) && (mode != null)) {
				startingTimeCurrentStep = System.currentTimeMillis();
			}
		}
		this.mode = mode;
	}
	
	private void currentStepEnded() {
		if (currentStep != null) {
			long currentTime = System.currentTimeMillis();
			long stepTime = otherTimeCurrentStep;
			if (startingTimeCurrentStep != -1) {
				stepTime += currentTime - startingTimeCurrentStep;
			} else {
			}
			stepDurations.put(currentStep, stepTime);
		}
		startingTimeCurrentStep = -1;
		otherTimeCurrentStep = 0;
	}

	
	public long getCycleTime() {
		if (stepDurations.keySet().containsAll(processFlow.getProcessSteps())) {
			long cycleTime = 0;
			for (long stepTime: stepDurations.values()) {
				cycleTime += stepTime;
			}
			return cycleTime;
		} else {
			return -1;
		}
	}
	
	public long getTimeInCurrentCycle() {
		long timeInCycle = 0;
		for (int i = 0; i < processFlow.getCurrentStepIndex(); i++) {
			if (stepDurations.containsKey(processFlow.getProcessSteps().get(i))) {
				timeInCycle += stepDurations.get(processFlow.getProcessSteps().get(i));
			}
		}
		timeInCycle += otherTimeCurrentStep;
		if (mode == Mode.RUNNING) {
			if (startingTimeCurrentStep != -1) {
				timeInCycle += (System.currentTimeMillis() - startingTimeCurrentStep);
			}
		} else if (mode == Mode.PAUSED) {
		} else {
			timeInCycle = -1;
		}
		return timeInCycle;
	}
	
	public ProcessFlow getProcessFlow() {
		return processFlow;
	}
	
	@Override
	public void dataChanged(ProcessFlowEvent e) {
		reset();
	}
	
	@Override
	public void exceptionOccured(ExceptionOccuredEvent e) {}
	@Override
	public void finishedAmountChanged(FinishedAmountChangedEvent e) {}

}
