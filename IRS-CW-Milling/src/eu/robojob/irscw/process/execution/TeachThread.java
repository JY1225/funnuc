package eu.robojob.irscw.process.execution;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.robojob.irscw.external.communication.AbstractCommunicationException;
import eu.robojob.irscw.external.device.AbstractDevice;
import eu.robojob.irscw.external.device.DeviceActionException;
import eu.robojob.irscw.external.robot.AbstractRobot;
import eu.robojob.irscw.external.robot.RobotActionException;
import eu.robojob.irscw.process.AbstractProcessStep;
import eu.robojob.irscw.process.AbstractTransportStep;
import eu.robojob.irscw.process.InterventionStep;
import eu.robojob.irscw.process.ProcessFlow;
import eu.robojob.irscw.process.ProcessFlow.Mode;
import eu.robojob.irscw.process.event.ExceptionOccuredEvent;
import eu.robojob.irscw.process.event.StatusChangedEvent;
import eu.robojob.irscw.threading.ThreadManager;
import eu.robojob.irscw.util.Translator;

public class TeachThread extends Thread {

	private ProcessFlow processFlow;
	private boolean running;
	
	private static Logger logger = LogManager.getLogger(TeachThread.class.getName());
	private static final int WORKPIECE_ID = 0;
	
	protected static final String OTHER_EXCEPTION = "Exception.otherException";
	
	public TeachThread(final ProcessFlow processFlow) {
		this.processFlow = processFlow;
		this.running = false;
	}
	
	@Override
	public void run() {
		// A basic teach thread which will iterate all process steps and request teaching whenever needed.
		logger.debug("Started execution, processflow [" + getProcessFlow() + "].");
		setRunning(true);
		try {
			getProcessFlow().initialize();
			getProcessFlow().setMode(Mode.TEACH);
			resetOffsets();
			try {
				// process-initialization
				getProcessFlow().processProcessFlowEvent(new StatusChangedEvent(getProcessFlow(), null, StatusChangedEvent.PREPARE, WORKPIECE_ID));
				for (AbstractRobot robot : getProcessFlow().getRobots()) {
					robot.recalculateTCPs();
					robot.moveToHome();
				}
				for (AbstractDevice device: getProcessFlow().getDevices()) {
					device.prepareForProcess(getProcessFlow());
				}
				getProcessFlow().setCurrentIndex(WORKPIECE_ID, 0);
				while ((getProcessFlow().getCurrentIndex(WORKPIECE_ID) < getProcessFlow().getProcessSteps().size()) && isRunning()) {
					AbstractProcessStep step = getProcessFlow().getProcessSteps().get(getProcessFlow().getCurrentIndex(WORKPIECE_ID));
					AbstractProcessStep nextStep = null;				
					if (getProcessFlow().getCurrentIndex(WORKPIECE_ID) < (processFlow.getProcessSteps().size() - 1)) {
						nextStep = processFlow.getProcessSteps().get(1 + getProcessFlow().getCurrentIndex(WORKPIECE_ID));
					}
					// check if next step would be an intervention step
					if ((nextStep != null) && (nextStep instanceof InterventionStep) && (getProcessFlow().getCurrentIndex(WORKPIECE_ID) < (processFlow.getProcessSteps().size() - 2))) {
						nextStep = processFlow.getProcessSteps().get(2 + getProcessFlow().getCurrentIndex(WORKPIECE_ID));
					}
					if (step instanceof AbstractTransportStep) {
						((AbstractTransportStep) step).getRobotSettings().setFreeAfter(true);
						if ((nextStep != null) && (nextStep instanceof AbstractTransportStep) && (step instanceof AbstractTransportStep)) {
							AbstractTransportStep trStep = (AbstractTransportStep) step;
							AbstractTransportStep trNextStep = (AbstractTransportStep) nextStep;
							if (trStep.getRobotSettings().getWorkArea().getUserFrame().equals(trNextStep.getRobotSettings().getWorkArea().getUserFrame())) {
								trStep.getRobotSettings().setFreeAfter(false);
							} else {
								trStep.getRobotSettings().setFreeAfter(true);
							}
						}
					}
					if (!(step instanceof InterventionStep)) {
						if (step instanceof AbstractTransportStep) {
							((AbstractTransportStep) step).executeStepTeached(WORKPIECE_ID);
							((AbstractTransportStep) step).finalizeStep();
						} else {
							step.executeStep(WORKPIECE_ID);
						}
					}
					getProcessFlow().setCurrentIndex(WORKPIECE_ID, getProcessFlow().getCurrentIndex(WORKPIECE_ID) + 1);
				}
				if (running) {
					// everything went as it should
					processFlow.setMode(Mode.READY);
					getProcessFlow().setCurrentIndex(WORKPIECE_ID, 0);
					processFlow.incrementFinishedAmount();
					this.running = false;
				} else {
					// flow got interrupted
					processFlow.setMode(Mode.STOPPED);
				}
			} catch (AbstractCommunicationException | RobotActionException | DeviceActionException e) {
				handleException(e);
			} catch (InterruptedException e) {
				if ((!isRunning()) || ThreadManager.isShuttingDown()) {
					logger.info("Execution of one or more steps got interrupted, so let't just stop");
					indicateStopped();
				} else {
					handleException(new Exception(Translator.getTranslation(OTHER_EXCEPTION)));
				}
			} catch (Exception e) {
				handleException(new Exception(Translator.getTranslation(OTHER_EXCEPTION)));
				e.printStackTrace();
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
	
	protected void handleException(final Exception e) {
		getProcessFlow().processProcessFlowEvent(new ExceptionOccuredEvent(getProcessFlow(), e));
		e.printStackTrace();
		logger.error(e);
		indicateStopped();
	}
	
	protected void indicateStopped() {
		getProcessFlow().initialize();
		getProcessFlow().processProcessFlowEvent(new StatusChangedEvent(getProcessFlow(), null, StatusChangedEvent.INACTIVE, WORKPIECE_ID));
		getProcessFlow().setMode(Mode.STOPPED);
	}
	
	@Override
	public void interrupt() {
		if (running) {
			running = false;
			for (AbstractRobot robot :processFlow.getRobots()) {
				robot.interruptCurrentAction();
			}
			for (AbstractDevice device :processFlow.getDevices()) {
				device.interruptCurrentAction();
			}
			processFlow.initialize();
		}
	}

	protected void resetOffsets() {
		for (AbstractProcessStep step: getProcessFlow().getProcessSteps()) {
			if (step instanceof AbstractTransportStep) {
				((AbstractTransportStep) step).setRelativeTeachedOffset(null);
			}
		}
	}
	
	public boolean isRunning() {
		return running;
	}
	
	protected void setRunning(final boolean running) {
		this.running = running;
	}
	
	public ProcessFlow getProcessFlow() {
		return processFlow;
	}
}
