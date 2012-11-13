package eu.robojob.irscw.process;

import java.util.HashMap;
import java.util.Map;

import eu.robojob.irscw.process.event.ActiveStepChangedEvent;
import eu.robojob.irscw.process.event.ExceptionOccuredEvent;
import eu.robojob.irscw.process.event.FinishedAmountChangedEvent;
import eu.robojob.irscw.process.event.ModeChangedEvent;
import eu.robojob.irscw.process.event.ProcessFlowEvent;
import eu.robojob.irscw.process.event.ProcessFlowListener;

public class ProcessFlowTimer implements ProcessFlowListener {

	private long startingTimeCurrentStep;
	private long otherTimeCurrentStep;
	private long pauseTime;

	private Map<AbstractProcessStep, Long> stepDurations;
	
	private ProcessFlow processFlow;
	
	private AbstractProcessStep currentStep;
	
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
				break;
			case PAUSED:
				setMode(Mode.PAUSED);
				break;
			case STOPPED:
				reset();
				break;
			case TEACH:
				setMode(Mode.RUNNING);
				break;
			case READY:
				setMode(Mode.PAUSED);
				break;
			default:
				break;
		}
	}
	
	private void reset() {
		startingTimeCurrentStep = -1;
		otherTimeCurrentStep = 0;
		pauseTime = 0;
		currentStep = null;
		setMode(Mode.STOPPED);
	}

	@Override
	public void activeStepChanged(ActiveStepChangedEvent e) {
		switch(e.getStatusId()) {
			case ActiveStepChangedEvent.NONE_ACTIVE:
				currentStepEnded();
				currentStep = null;
				break;
			case ActiveStepChangedEvent.TEACHING_NEEDED:
				setMode(Mode.PAUSED);
				pauseTime = System.currentTimeMillis();
				break;
			case ActiveStepChangedEvent.TEACHING_FINISHED:
				setMode(Mode.RUNNING);
				break;
			default:
				if (!e.getActiveStep().equals(currentStep)) {
					if (mode == Mode.PAUSED) {
						throw new IllegalStateException("Paused and yet the active step changes?");
					}
					currentStepEnded();
					currentStep = e.getActiveStep();
					startingTimeCurrentStep = System.currentTimeMillis();
				}
				break;
		}
	}
	
	private void setMode(Mode mode) {
		if (this.mode != null) {
			if ((this.mode == Mode.RUNNING) && (mode == Mode.PAUSED)) {
				pauseTime = System.currentTimeMillis();
			} else if ((this.mode == Mode.PAUSED) && (mode == Mode.RUNNING)) {
				otherTimeCurrentStep += (System.currentTimeMillis() - pauseTime);
				pauseTime = -1;
			} else if (mode == Mode.STOPPED) {
				otherTimeCurrentStep = 0;
				pauseTime = -1;
			}
		}
		this.mode = mode;
	}
	
	private void currentStepEnded() {
		if (currentStep != null) {
			long currentTime = System.currentTimeMillis();
			long stepTime = currentTime - startingTimeCurrentStep + otherTimeCurrentStep;
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
			timeInCycle += stepDurations.get(processFlow.getProcessSteps().get(i));
		}
		if (mode == Mode.RUNNING) {
			if (startingTimeCurrentStep != -1) {
				timeInCycle += (System.currentTimeMillis() - startingTimeCurrentStep + otherTimeCurrentStep);
			}
		} else if (mode == Mode.PAUSED) {
			timeInCycle += otherTimeCurrentStep;
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
